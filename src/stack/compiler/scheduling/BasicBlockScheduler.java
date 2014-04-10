package stack.compiler.scheduling;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import stack.compiler.analysis.PhiRenaming;
import stack.isa.Instruction;
import stack.isa.core.InstructionBZ;
import stack.isa.core.InstructionDROP;
import stack.isa.core.InstructionPULL;
import stack.isa.core.InstructionPUSH;
import stack.isa.core.InstructionSETHI;
import stack.isa.core.InstructionTUCK;
import stack.isa.core.InstructionTUCK_CP;
import stack.isa.core.InstructionJ;
import stack.isa.sir.InstructionSIR_JUMP;
import stack.isa.sir.InstructionSIR_BRANCH;
import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;
import stack.util.BidirectionalMap;

public class BasicBlockScheduler {
	
	public static Expression schedule(BasicBlock BB,
			HashSet<Integer> inputSet,
			Vector<Integer> inputStack,
			HashMap<BasicBlock, HashSet<Integer>> unrenamedOutputSet,
			HashMap<BasicBlock, Vector<Integer>> unrenamedOutputStack) {
		
		System.out.println("Scheduling " + BB.getLabel());
		
		System.out.println("Inputs: ["+inputStack+"],{"+inputSet+"}");
		System.out.println("Outputs:");
		HashSet<BasicBlock> bbTargetSet = new HashSet<BasicBlock>();
		bbTargetSet.addAll(unrenamedOutputStack.keySet());
		bbTargetSet.addAll(unrenamedOutputSet.keySet());
		if (bbTargetSet.isEmpty()){
			System.out.println("\t(no targets, no outputs)");
		} else {
			for (BasicBlock target : bbTargetSet){
				System.out.println("\t" + target.getLabel() + ": ["+unrenamedOutputStack.get(target)+"],{"+unrenamedOutputSet.get(target)+"}");
			}
		}
		
		// Analyze implicit constraints (order of memory accesses, calls, etc).
		analyzeImplicitConstraints(BB);
		
		// Add transfer stack requirements to the terminal expressions
		handleTransferStack(BB, unrenamedOutputStack, unrenamedOutputSet);
		
		// in case the BB loops back on itself, we must update the inputSet and inputStack with fresh values
		// This is **only** relevant where a BB feeds back on itself.
		//if (unrenamedOutputStack.containsKey(BB)){
		//	inputStack.clear();
		//	for (int i=0; i<unrenamedOutputStack.get(BB).size(); i++){
		//		inputStack.add(unrenamedOutputStack.get(BB).get(i);
		//	}
		//}
		
		// Verify this!
		if (unrenamedOutputStack.containsKey(BB) || unrenamedOutputSet.containsKey(BB)){
			assert inputSet.size() == unrenamedOutputSet.get(BB).size();
			for (int var : unrenamedOutputSet.get(BB)){
				assert inputSet.contains(var) || Expression.isConstant(var);
			}
			
			assert inputStack.size() == unrenamedOutputStack.get(BB).size();
			for (int i=0; i<unrenamedOutputStack.get(BB).size(); i++){
				
				assert inputStack.get(i)==unrenamedOutputStack.get(BB).get(i);
			}
		}
		
		// Elaborate globals
		replicateGlobals(BB);
		
		// Take care of constants
		replicateConstants(BB);
		//elaborateConstants(BB);
		
		System.out.println("Scheduled Outputs:");
		if (bbTargetSet.isEmpty()){
			System.out.println("\t(no targets, no outputs)");
		} else {
			for (BasicBlock target : bbTargetSet){
				System.out.println("\t" + target.getLabel() + ": ["+unrenamedOutputStack.get(target)+"],{"+unrenamedOutputSet.get(target)+"}");
			}
		}
		
		// Come up with the best order of expressions (this uses a cost function)
		LinkedList<Expression> bbOrdering = orderExpressions(BB, inputSet);
		
		// Schedule the stack (this use Koopman, improved)
		scheduleStack(bbOrdering, inputSet, inputStack);
		
		System.out.println("Scheduled Inputs: ["+inputStack+"],{"+inputSet+"}");
		
		// Collapse the now-scheduled basic block into a single expression
		Expression flattenedBB = new Expression();
		for (Expression e : bbOrdering){
			for (Instruction instruction : e.getInstructions()){
				flattenedBB.appendInstruction(instruction);
			}
		}
		
		// Done!
		return flattenedBB;
	}
	
	private static void handleTransferStack(BasicBlock BB, 
			HashMap<BasicBlock, Vector<Integer>> unrenamedOutputStack,
			HashMap<BasicBlock, HashSet<Integer>> unrenamedOutputSet){
		
		// Need a mapping between labels and basic blocks
		BidirectionalMap<String, BasicBlock> labelMap = new BidirectionalMap<String, BasicBlock>();
		for (BasicBlock bb : unrenamedOutputStack.keySet()){
			labelMap.put(bb.getLabel(), bb);
		}
		for (BasicBlock bb : unrenamedOutputSet.keySet()){
			labelMap.put(bb.getLabel(), bb);
		}
		
		// Approach: modify the terminal expression (if the BB has a transfer stack output) to:
		// 1). correctly populate the output stack (phi renaming)
		// 2). input stack corresponds to actual behavior
		// 3). outputs the transfer stack (if any - otherwise it already outputs the return stack!).
		// Also need to modify the terminal itself to handle phi renaming!!!
		// NOTE: this assumes that the terminal of a BB that has a transfer stack output ouputs **nothing else**!
		Expression terminal = BB.getTerminal();
		Vector<Instruction> instructions = terminal.getInstructions();
		
		// The following terminals do not need to be rewritten:
		// - a function return [@, ret vals] -> (J_ABS) -> [ret vals]
		//   (assert there is no transfer stack output!)
		if (instructions.size()==1 && (instructions.get(0) instanceof InstructionJ)){
			assert unrenamedOutputStack.isEmpty();
			assert unrenamedOutputSet.isEmpty();
			return; // nothing needs to be done
		}
		
		// The following terminals must be rewritten
		// - an unconditional branch [] -> (SIR_JUMP) -> []
		else if (instructions.size()==1 && (instructions.get(0) instanceof InstructionSIR_JUMP)){
			assert terminal.getInputStack().isEmpty();
			assert terminal.getOutputStack().isEmpty();
			assert unrenamedOutputSet.size() == 1;
			assert unrenamedOutputStack.size()==1;
			
			String label = ((InstructionSIR_JUMP)instructions.get(0)).getLabel();
			assert labelMap.containsKey(label);
			BasicBlock target = labelMap.get(label);
			
			String comment = instructions.get(0).getComment();
			
			if (!unrenamedOutputStack.containsKey(target)) unrenamedOutputStack.put(target, new Vector<Integer>());
			if (!unrenamedOutputSet.containsKey(target)) unrenamedOutputSet.put(target, new HashSet<Integer>());
			Vector<Integer> unrenamedTransferStack = unrenamedOutputStack.get(target);
			HashSet<Integer> unrenamedTransferSet = unrenamedOutputSet.get(target);
			
			
			// Schedule set outputs in no particular order
			// TODO: improve on this order somehow
			for (int var : unrenamedTransferSet){
				unrenamedTransferStack.add(var);
			}
			unrenamedTransferSet.clear();
						
			// Now figure out the new input stack
			terminal.getInputStack().clear();
			for (int var : unrenamedTransferStack){
				int translatedVar = PhiRenaming.renameVariable(BB, target, var);
				terminal.getInputStack().add(translatedVar);
			}
						
			// Don't bother populating the output stack - it's a poor fit into the model due to phi renaming in case of multiple targets
			// ---
			
			// The new, phi-renamed expression is written here. Keep track of the required input stack.
			terminal.getInstructions().clear();
			terminal.appendInstruction(new InstructionSIR_JUMP(label, "to : " + label + " # " + comment));
			
			assert unrenamedTransferSet.isEmpty();
			return; // all done
		}
		
		// - a conditional branch [cond] -> (SIR_BRANCH, SIR_JUMP) -> []
		else if (instructions.size()==2 && (instructions.get(0) instanceof InstructionSIR_BRANCH) && (instructions.get(1) instanceof InstructionSIR_JUMP)){
			assert terminal.getInputStack().size()==1;
			assert terminal.getOutputStack().isEmpty();
			assert unrenamedOutputStack.size() == 2;
			assert unrenamedOutputSet.size()==2;
			
			int conditionVar = terminal.getInputStack().get(0);
			String labelT  = ((InstructionSIR_BRANCH)instructions.get(0)).getLabel();
			String labelF  = ((InstructionSIR_JUMP)instructions.get(1)).getLabel();
			assert labelMap.containsKey(labelT);
			assert labelMap.containsKey(labelF);
			BasicBlock targetT = labelMap.get(labelT);
			BasicBlock targetF = labelMap.get(labelF);
			String commentT = instructions.get(0).getComment();
			String commentF = instructions.get(1).getComment();
			
			if (!unrenamedOutputStack.containsKey(targetT)) unrenamedOutputStack.put(targetT, new Vector<Integer>());
			if (!unrenamedOutputStack.containsKey(targetF)) unrenamedOutputStack.put(targetF, new Vector<Integer>());
			if (!unrenamedOutputSet.containsKey(targetT)) unrenamedOutputSet.put(targetT, new HashSet<Integer>());
			if (!unrenamedOutputSet.containsKey(targetF)) unrenamedOutputSet.put(targetF, new HashSet<Integer>());
			Vector<Integer> unrenamedTransferStackT = unrenamedOutputStack.get(targetT);
			Vector<Integer> unrenamedTransferStackF = unrenamedOutputStack.get(targetF);
			HashSet<Integer> unrenamedTransferSetT = unrenamedOutputSet.get(targetT);
			HashSet<Integer> unrenamedTransferSetF = unrenamedOutputSet.get(targetF);
			
			// Now figure out the new input stack
			terminal.getInputStack().clear();
			terminal.getInputStack().add(conditionVar); // condition goes first!
			
			// Rename transfer stacks
			Vector<Integer> renamedTransferStackT = PhiRenaming.renameTransferStack(BB, targetT, unrenamedTransferStackT);
			Vector<Integer> renamedTransferStackF = PhiRenaming.renameTransferStack(BB, targetF, unrenamedTransferStackF);
			
			// create an ordered union or transfer stack elements
			Vector<Integer> renamedUnion = new Vector<Integer>();
			for (int i=0; ((i<renamedTransferStackT.size())||(i<renamedTransferStackF.size())); i++){
				Integer t = null;
				Integer f = null;
				if (i<renamedTransferStackT.size()) t = renamedTransferStackT.get(i);
				if (i<renamedTransferStackF.size()) f = renamedTransferStackF.get(i);
				
				if ((null != t) && (!renamedUnion.contains(t))) renamedUnion.add(t);
				if ((f != t) && (f != null) && (!renamedUnion.contains(f))) renamedUnion.add(f);
			}
			
			// for now, stick the input set into the input stack in no particular order
			// TODO: find a way to order the input set well
			for (int unrenamedVar : unrenamedTransferSetT){
				int renamedVar = PhiRenaming.renameVariable(BB, targetT, unrenamedVar);
				if (!renamedUnion.contains(renamedVar)){
					renamedUnion.add(renamedVar);
				}
				unrenamedTransferStackT.add(unrenamedVar);
				renamedTransferStackT.add(renamedVar);
			}
			for (int unrenamedVar : unrenamedTransferSetF){
				int renamedVar = PhiRenaming.renameVariable(BB, targetF, unrenamedVar);
				if (!renamedUnion.contains(renamedVar)){
					renamedUnion.add(renamedVar);
				}
				unrenamedTransferStackF.add(unrenamedVar);
				renamedTransferStackF.add(renamedVar);
			}
			
			unrenamedTransferSetT.clear();
			unrenamedTransferSetF.clear();
			
			// put the union of transfer stacks into the input stack
			for (int var : renamedUnion){
				terminal.getInputStack().add(var);
			}
						
			// Don't bother populating the output stack - it's a poor fit into the model due to phi renaming in case of multiple targets
			// ---
			
			// The new, phi-renamed expression is written here. Keep track of the required input stack.
			// The expression will look like this:
			// 			[cond, U(transferStackT, transferStackF)]
			// 		B_Z @f
			// 			..drop everything not transferStackT
			//		SIR_JUMP @labelT
			//	@f		..drop everything not transferStackF
			//		SIR_JUMP @labelF
			
			terminal.getInstructions().clear();
			
			// Add stack fiddling to correctly populate True transfer stack
			terminal.appendInstructions(stackFiddlingInefficient(new LinkedList<Integer>(renamedUnion), new LinkedList<Integer>(renamedTransferStackT)));
			terminal.appendInstruction(new InstructionSIR_JUMP(labelT, "to " + labelT + " # " + commentT));
			
			// Prepend the condition variable
			int branchDistance = terminal.getInstructions().size()+1;
			terminal.prependInstruction(new InstructionBZ(branchDistance, "branch to false path, leading to " + labelF));
			
			// Add stack fiddling to correctly populate the False transfer stack
			terminal.appendInstructions(stackFiddlingInefficient(new LinkedList<Integer>(renamedUnion), new LinkedList<Integer>(renamedTransferStackF)));
			terminal.appendInstruction(new InstructionSIR_JUMP(labelF, "to " + labelF + " # " + commentF));
			
			assert unrenamedTransferSetT.isEmpty();
			assert unrenamedTransferSetF.isEmpty();
			return; // nothing needs to be done
		}
		
		else {
			// All other terminals are unsupported, and will generate bad code or exceptions!
			assert false;
		}
	}
	
	private static void analyzeImplicitConstraints(BasicBlock BB){
		// Create ordering constraints for call instructions
		// Create ordering constraints for memory instructions
		Expression lastStore = null;
		Expression lastLoad = null;
		Expression lastCall = null;
		for (Expression exp : BB.getExpressions()){
			// Traverse BB expressions in given order
			// (this order describes a known valid ordering of call, memory accesses, etc.)
			
			if (exp.hasStore()){
				// All loads must finish
				// All stores must finish
				// All calls must finish
				if (lastLoad != null) lastLoad.addDependent(exp);
				if (lastStore != null) lastStore.addDependent(exp);
				if (lastCall != null) lastCall.addDependent(exp);
				
				lastStore = exp;
			}
			
			if (exp.hasLoad()){
				// All stores must finish
				// All calls must finish
				if (lastStore != null) lastStore.addDependent(exp);
				if (lastCall != null) lastCall.addDependent(exp);
				
				lastLoad = exp;
			}
			
			if (exp.hasCall()){
				// All loads must finish
				// All stores must finish
				// All calls must finish
				if (lastLoad != null) lastLoad.addDependent(exp);
				if (lastStore != null) lastStore.addDependent(exp);
				if (lastCall != null) lastCall.addDependent(exp);
				
				lastCall = exp;
			}
		}
	}
	
	private static void elaborateConstants(BasicBlock BB) {
		HashSet<Expression> elaboratedConstants = new HashSet<Expression>();
		HashSet<Integer> visited = new HashSet<Integer>();
		for (Expression exp : BB.getExpressions()){
			for (int var : exp.getInputStack()){
				if (Expression.isConstant(var) && !visited.contains(var)){
					// Replicate the constant
					Expression constant = new Expression(Expression.getConstant(var));
					
					visited.add(var);
					elaboratedConstants.add(constant);
				}
			}
		}
		
		for (Expression constant : elaboratedConstants){
			BB.appendExpression(constant);
		}
	}
	
	private static void replicateConstants(BasicBlock BB) {
		HashSet<Expression> replicatedConstants = new HashSet<Expression>();
		
		for (Expression exp : BB.getExpressions()){
			for (int var : exp.getInputStack()){
				if (Expression.isConstant(var)){
					// Replicate the constant
					Expression constant = new Expression(Expression.getConstant(var));
					
					// Rename the constant to avoid conflicts if it is replicated
					int newVarID = Expression.getNewVariableID();
					constant.getOutputStack().set(constant.getOutputStack().indexOf(var), newVarID);
					exp.getInputStack().set(exp.getInputStack().indexOf(var), newVarID);
					
					replicatedConstants.add(constant);
				}
			}
		}
		
		for (Expression constant : replicatedConstants){
			BB.appendExpression(constant);
		}
	}
	
	private static void replicateGlobals(BasicBlock BB) {
		HashSet<Expression> replicatedGlobals = new HashSet<Expression>();
		
		for (Expression exp : BB.getExpressions()){
			for (int var : exp.getInputStack()){
				if (Expression.isGlobal(var)){
					// Create an expression to populate the global value
					Expression global = new Expression();
					
					// Input Stack
					global.getInputStack().clear();
					
					// Populate expression
					int address = Expression.getGlobalAddress(var);
					int low16 = address & 0xFFFF;
					int hi16 = (address >> 16) & 0xFFFF;
					global.appendInstruction(new InstructionPUSH(low16, "Global variable " + var));
					global.appendInstruction(new InstructionSETHI(hi16, ""));
					
					// Output stack
					global.getOutputStack().clear();
					global.getOutputStack().add(var);
					
					// Rename the constant to avoid conflicts if it is replicated
					int newVarID = Expression.getNewVariableID();
					global.getOutputStack().set(global.getOutputStack().indexOf(var), newVarID);
					exp.getInputStack().set(exp.getInputStack().indexOf(var), newVarID);
					
					replicatedGlobals.add(global);
				}
			}
		}
		
		for (Expression constant : replicatedGlobals){
			BB.appendExpression(constant);
		}
	}
	
	private static LinkedList<Expression> orderExpressions(BasicBlock BB,
			//Vector<Integer> inputStack,
			HashSet<Integer> inputSet) {
		// Analyze:
		// Enumerate external variables
		// Find the mapping of variables to expressions
		
		// maintain a visited set.
		// maintain a partial expression list
		// Put terminal on the partial expression list, visit terminal
		
		// While there exist unvisited expressions
		// - Use a cost function (given current partial expression) to select the best next expression
		// - Put its output variables into memory or on one of the stacks
		
		LinkedList<Expression> bbOrdering = new LinkedList<Expression>();
		
		HashSet<Expression> candidates = new HashSet<Expression>();
		LinkedList<Integer> requiredStack = new LinkedList<Integer>();
		candidates.addAll(BB.getExpressions());
		
		Expression terminal = BB.getTerminal();
		bbOrdering.addFirst(terminal);
		candidates.remove(terminal);
		
		// the input stack of terminal
		for (int var : terminal.getInputStack()){
			requiredStack.addLast(var);
		}
		
		while (!candidates.isEmpty()){
			// select an expression according to some cost function
			
			// TODO: this ought to do something with the input stack, if one is available
			
			Expression exp = select(BB, inputSet, requiredStack, candidates);
			bbOrdering.addFirst(exp);
			candidates.remove(exp);
			
			for (Integer var : exp.getOutputStack()){
				assert requiredStack.contains(var);
				requiredStack.remove((Integer)var);
			}
			
			Vector<Integer> reqVars = exp.getInputStack();
			for (int i=reqVars.size()-1; i>=0; i--){
				// Note: these are added in reverse (the top reuired item will be top on the req stack)
				int var = reqVars.get(i);
				requiredStack.addFirst(var);
			}
		}
		
		return bbOrdering;
	}

	private static void scheduleStack(
			LinkedList<Expression> bbOrdering,
			HashSet<Integer> inputSet,
			Vector<Integer> outInputStack) {
		
		// Figure out def-use and use-use pairs
		Hashtable<Integer, Expression> defs = new Hashtable<Integer, Expression>();
		Hashtable<Integer, Expression> uses = new Hashtable<Integer, Expression>();
		Vector<StackPair> pairs = new Vector<BasicBlockScheduler.StackPair>();
		
		// TODO: idea: if x-stack inputs need to be accounted for, add an empty expression that outputs ordered x-stack at the beginning of bbOrdering.
		// TODO: this would also make the input stack ordering easier to determine
		// TODO: what to do about return stack and argument stack?
		
		LinkedList<Expression> bbOrderingStatic = new LinkedList<Expression>(bbOrdering);
		Expression inputs = new Expression();
		HashSet<Integer> inputsVisited = new HashSet<Integer>();;
		
		for (Expression exp : bbOrderingStatic){
			for (int use : exp.getInputStack()){
				// Handle inputs
				if ((inputSet.contains(use) || outInputStack.contains(use)) && !inputsVisited.contains(use)){
					// Input
					inputs.getOutputStack().add(use);
					
					inputsVisited.add(use);
					
					defs.put(use, inputs);
				}
				
				// Handle normal cases
				if (uses.containsKey(use)){
					// check previous uses
					// create a use-use pair
					StackPair p = new StackPair();
					p.var = use;
					p.exp1 = uses.get(use);
					p.exp2 = exp;
					p.use_use = true;
					pairs.add(p);
					
					// Record the new use
					uses.put(use, exp);
					
				} else if (defs.containsKey(use)){
					// create a def-use pair
					StackPair p = new StackPair();
					p.var = use;
					p.exp1 = defs.get(use);
					p.exp2 = exp;
					p.use_use = false;
					pairs.add(p);
					
					// Record the new use
					uses.put(use, exp);
				} else {
					// Trips up here sometimes!
					assert !Expression.isConstant(use);
					assert false;
				}
			}
			
			for (int def : exp.getOutputStack()){
				// add a new def
				defs.put(def, exp);
			}
		}
		
		// Calculate distance for each pair
		Hashtable<Integer, Integer> inputDistance = new Hashtable<Integer, Integer>();
		
		for (StackPair pair : pairs){
			// System.out.println("Stack pair : var#" + pair.var);
			if (inputs == pair.exp1){
				// ignore order of inputs - determine it later based on distance (lowest distance input first)
				pair.distance = bbOrdering.indexOf(pair.exp2);
				inputDistance.put(pair.var, pair.distance);
			} else {
				pair.distance = bbOrdering.indexOf(pair.exp2) - bbOrdering.indexOf(pair.exp1);
			}
		}
		
		// Insert inputs according to distance metric
		// Sort inputs by distance (lowest first)
		// of course only do this if the inputs have not already been determined
		Vector<Integer> orderedInputs;
		if (null == outInputStack || outInputStack.isEmpty()){
			orderedInputs = new Vector<Integer>(inputs.getOutputStack());
			Collections.sort(orderedInputs, new InputDistanceComparator(inputDistance));
		} else {
			// TODO: line these up in whatever order the input stack dictates
			assert inputs.getInputStack().isEmpty();
			orderedInputs = new Vector<Integer>();
			LinkedList<Integer> inStackList = new LinkedList<Integer>(outInputStack);
			while(!inStackList.isEmpty()){
				int var = inStackList.removeFirst();
				orderedInputs.add(var);
			}
		}
		
		// Fix order of output stack of input expression
		inputs.getOutputStack().clear();
		inputs.getOutputStack().addAll(orderedInputs);
		bbOrdering.addFirst(inputs);
		
		// Sort by distance in ascending order
		Collections.sort(pairs, new Comparator<StackPair>() {
			public int compare(StackPair a, StackPair b) {
				return (new Integer(((StackPair) a).distance).compareTo(new Integer(((StackPair) b).distance)));
			}});
		
		// For each pair, insert stack fiddling expressions
		// Maintain BB stack depth (L-stack) for each instruction. Initialize to 0
		Hashtable<Expression, LinkedList<Integer>> LStackPre = new Hashtable<Expression, LinkedList<Integer>>();
		Hashtable<Expression, LinkedList<Integer>> LStackPost = new Hashtable<Expression, LinkedList<Integer>>();
		// Note: LStack depth is defined at the point **after** the expression is evaluated
		for (Expression exp : bbOrdering){
			LStackPre.put(exp, new LinkedList<Integer>());
			LStackPost.put(exp, new LinkedList<Integer>());
		}
		
		for (StackPair pair : pairs){
			// TODO: note - relying on a good peephole pass here to do local re-ordering of instructions to get rid of unnecesary stack fiddling.
			// Increment stack depth for the entire interval (stack is defined **after** exp evaluates, so omit last)
			for (int i=bbOrdering.indexOf(pair.exp1); i<=bbOrdering.indexOf(pair.exp2); i++){
				Expression intervalExp = bbOrdering.get(i);
				if (i>bbOrdering.indexOf(pair.exp1)){
					LStackPre.get(intervalExp).add(pair.var);
				}
				if (i<bbOrdering.indexOf(pair.exp2)){
					LStackPost.get(intervalExp).add(pair.var);
				}
			}
		}
		
		// Now modify the expressions to include stack scheduling operations
		/* For each expression
		For each input element (in correct order)
			Add a pull instruction if pull depth > 0
			For each copy (in increasing depth):
				Add a tuck/tuck_cp element if tuck depth > 0
		For each output element (in correct order)
			For each copy (in increasing depth):
				Add a tuck/tuck_cp element if tuck depth > 0
		 */
		for (Expression exp : bbOrdering){
			// Inject instructions before and after exp to:
			// 1). convert LstackPre into LstackPost
			// 2). satisfy the input stack of the expression
			
			// Data is transferred in one of two ways:
			// via the expression (place arguments on input stack, output stack has results)
			// via a forwarding stack (resides under the expression stack
			//
			// Want to minimize effort in constructing input & transfer stacks from LStackPre.
			// Want to minimize effort in converting output & transfer stacks into LStackPost
			
			// enumerate the transfer set
			// Transfer set = {x | x in LStackPost, x not in exp.output}
			LinkedList<Integer> transferStack = new LinkedList<Integer>();
			for (int var : LStackPost.get(exp)){
				if (!exp.getOutputStack().contains(var)){
					assert LStackPre.get(exp).contains(var);
					transferStack.addLast(var);
				}
			}
			
			// Enumerate stack at expression entry
			LinkedList<Integer> inStack = new LinkedList<Integer>(transferStack);
			for (int i=exp.getInputStack().size()-1; i>=0; i--){
				// (in reverse order because adding one by one to the front
				int var = exp.getInputStack().get(i);
				inStack.addFirst(var);
			}
			
			// Emit pre- Pulls.
			exp.prependInstructions(stackFiddlingInefficient(LStackPre.get(exp), inStack));
			
			// Enumerate stack at expression exit
			LinkedList<Integer> outStack = new LinkedList<Integer>(transferStack);
			for (int i=exp.getOutputStack().size()-1; i>=0; i--){
				// (in reverse order because adding one by one to the front
				int var = exp.getOutputStack().get(i);
				outStack.addFirst(var);
			}
			
			// Emit instructions to translate tempStack into LStackPost
			exp.appendInstructions(stackFiddlingInefficient(outStack, LStackPost.get(exp)));
		}
		
		// Write stack content comments on all scheduled expressions
		System.out.println("--- Local stack schedule ---");
		for (Expression exp : bbOrdering){
			System.out.print(LStackPre.get(exp));
			System.out.print(" : ");
			System.out.print(exp.getInputStack().toString());
			System.out.print("->");
			System.out.print(exp.getOutputStack().toString());
			System.out.print(" : ");
			if (!exp.getDependents().isEmpty()){
				System.out.print(" MUST BE FOLLOWED BY");
				for (Expression req : exp.getDependents()){
					System.out.print(" [");
					System.out.print(req.getInputStack().toString());
					System.out.print("->");
					System.out.print(req.getOutputStack().toString());
					System.out.print(" ]");
				}
			}
			System.out.println(LStackPost.get(exp));
		}
		System.out.println("--- (end) Local stack schedule ---");
		
		outInputStack.clear();
		for (int var : orderedInputs){
			outInputStack.add(var);
			inputSet.remove(var);
		}
		
		assert inputSet.isEmpty();
	}
	
	private static LinkedList<Instruction> stackFiddlingInefficient(LinkedList<Integer> in, LinkedList<Integer> out){
		// This is tricky, but here's one approach:
		LinkedList<Instruction> instructions = new LinkedList<Instruction>();
		
		// TODO: this can be better done using a block sorting approach that moves smallest contiguous blocks first.
		
		// assert no duplicates in input
		for (int i=0; i<in.size(); i++){
			int var = in.get(i);
			assert in.indexOf(var)==i;
		}
		
		// Maintain 2 stacks: sorted (initially empty) and todo (initially in).
		LinkedList<Integer> todo = new LinkedList<Integer>(in);
		LinkedList<Integer> sortedIDs = new LinkedList<Integer>();
		LinkedList<Integer> outMutable = new LinkedList<Integer>(out);
		
		// Invariant: *sorted* has all items in correct relative order required by out. Also corrct # of copies.
		
		// For each var in todo (left to right)
		// pull to the front, tuck to the right place in visited. 
		// if pull and tuck are to the same depth, emit nothing.
		// if pull or tuck are to depth 0, disregard.
		
		while (!todo.isEmpty()){
			int var = todo.removeFirst();
			
			int numCopies = Collections.frequency(out, var);
			if (numCopies == 0){
				// this variable is not needed in out, and is therefore dropped.
				instructions.add(new InstructionDROP(sortedIDs.size(), "var: "+ var));
			} else {
				LinkedList<Integer> nearestPriors = new LinkedList<Integer>(); 
				// populate nearest priors.
				int prior = -1;
				for (int i=0; i<out.size(); i++){
					assert prior != (Integer)i;
					
					if (out.get(i)==var){
						nearestPriors.add(prior);
						numCopies--;
					}
					
					if (sortedIDs.contains(i)){
						prior = i;
					}
				}
				
				assert numCopies == 0;
					
				// Sort in decreasing order - this will keep stack access depth to a minimum
				Collections.sort(nearestPriors, new Comparator<Integer>() {
						public int compare(Integer a, Integer b) {
							return (b.compareTo(a));
						}});
				
				// Emit appropriate instructions for the variable
				if (sortedIDs.size() > 0){
					instructions.add(new InstructionPULL(sortedIDs.size(), "var: "+ var));
				}
				for (int i=0; i<nearestPriors.size(); i++){
					prior = nearestPriors.get(i);
					int depth = 0;
					if (prior >= 0){
						depth = sortedIDs.indexOf(prior)+1;
					}
	
					if (i+1 < nearestPriors.size()){
						instructions.add(new InstructionTUCK_CP(depth, "var: "+ var));
					} else {
						if (depth > 0){
							instructions.add(new InstructionTUCK(depth, "var: "+ var));
						}
					}
					
					// remove the case of pull-tuck to equal depths
					if (nearestPriors.size()==1){
						if (depth==sortedIDs.size()){
							// this pull-tuck pair did nothing. Remove it.
							if (depth>0){
								instructions.removeLast();
								instructions.removeLast();
							}
						}
					}
					
					sortedIDs.add(depth, outMutable.lastIndexOf(var));
					outMutable.set(outMutable.lastIndexOf(var), null);	// need to use LastIndexOf due to copy insertion order (farthest first)
				}
			}
		}
		
		// Verify the result.
		assert todo.isEmpty();
		assert out.size() == sortedIDs.size();
		for (int i=0; i<out.size(); i++){
			assert i==sortedIDs.get(i);
		}
		
		return instructions;
	}

	private static Expression select(BasicBlock BB,
			HashSet<Integer> inputSet,
			LinkedList<Integer> outStack,
			HashSet<Expression> candidates){
		
		Expression bestExp = null;
		int bestCost = 0;
		
		// Enumerate all variables used as input by the candidate set
		// Selected expression must not define something that is required by the candidate set
		// An expression defines something if it outputs it but does not take it as input
		HashSet<Integer> requiredVars = new HashSet<Integer>();
		for (Expression exp : candidates){
			for (int use : exp.getInputStack()){
				requiredVars.add(use);
			}
		}

		// Select expression with the lowest overall cost. Break ties by search order.
		for (Expression exp : candidates){
			int expCost = cost(BB, inputSet, requiredVars, outStack, candidates, exp);
			if ((expCost < bestCost) || (null == bestExp)){
				bestExp = exp;
				bestCost = expCost;
			}
		}
		
		assert bestCost != Integer.MAX_VALUE;
		return bestExp;
	}
	
	private static int cost (BasicBlock BB,
			HashSet<Integer> inputSet,
			HashSet<Integer> requiredVars,
			LinkedList<Integer> outStack,
			HashSet<Expression> candidates,
			Expression selection){
		
		// TODO: the actual cost function goes here.
		
		// TODO: try to select things in reverse polish order.
		// Absolute distance doesn't matter so much (as long as it is positive)
		// Instead, stack depth distance is the important measure.
		
		// Requirements:
		// All items on the output stack must be on the required stack
		
		int stackCost = 0;
		
		// Make sure implicit requirements are met
		for (Expression dep : selection.getDependents()){
			if (candidates.contains(dep)){
				return Integer.MAX_VALUE; // Illegal configuration! do not select this.
			}
		}
					
		for (int i=0; i<selection.getOutputStack().size(); i++){
			int var = selection.getOutputStack().get(i);
			
			// The expression must not produce values not present on the output stack
			// TODO: is this correct? Sometimes we may have situations that require DROP
			if (!outStack.contains(var)){
				return Integer.MAX_VALUE; // Illegal configuration! do not select this.
			}
			
			// The expression must not define anything that will be needed in the future
			// To define is to output X, but not require X as input
			if (requiredVars.contains(var) && !selection.getInputStack().contains(var)){
				return Integer.MAX_VALUE; // Illegal configuration! do not select this.
			}
			
			// want to pick things that output values close to the top of the stack
			// stack depth is heavy
			// local rearrangements, however, are cheap.
			
			//int accessDepth = outStack.indexOf(var);
			//if (accessDepth < i){
			//	accessDepth = 0;
			//}
			
			//stackCost += accessDepth;
			stackCost += 0;
		}
		
		// TODO:
		// Optimize for:
		// stack edit distance (and depth of edit)
		// underflow/overflow
		return stackCost;
	}
	
	private static class StackPair{
		public int var;
		public boolean use_use;
		public Expression exp1, exp2;
		public int distance;
	}
	
	private static class InputDistanceComparator implements Comparator<Integer> {
		private Hashtable<Integer, Integer> inputDistance;
		public InputDistanceComparator(Hashtable<Integer, Integer> inputDistance){
			this.inputDistance = inputDistance;
		}
		
		public int compare(Integer a, Integer b) {
			return (new Integer(inputDistance.get(a)).compareTo(new Integer(inputDistance.get(b))));
		}
	}
}