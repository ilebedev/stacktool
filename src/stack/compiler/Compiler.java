package stack.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import stack.asm.Program;
import stack.asm.Segment;
import stack.compiler.analysis.FunctionAnalysis;
import stack.compiler.model.OutputSets;
import stack.compiler.model.TransferSets;
import stack.compiler.model.TransferStacks;
import stack.compiler.scheduling.BasicBlockScheduler;
import stack.compiler.scheduling.FunctionScheduler;
import stack.isa.Instruction;
import stack.isa.core.InstructionHALT;
import stack.isa.core.InstructionPUSH;
import stack.isa.sir.InstructionSIR_CALL;
import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;
import stack.sir.model.Function;
import stack.sir.model.Module;

public class Compiler {
	
	public static Program Compile(Module M){
		
		Program p = new Program();
		
		// Now legalize each function in the module!
		for (Function F : M.getFunctions()){
			/*
			Here's how this works:
			
			Maintain a set of expressions (compiled basic blocks)
			
			Let B be the set of basic blocks in a function F
			Let St be the set of variables that are passed between basic blocks
			
			For all bb in B, determine the output set given a basic block destination.
			NOTE: A basic block with no destinations (like return) has a *NULL* output set.
			
			Maintain a partial list of ordered transfer sets
			
			In leaf-first order of basic blocks bb in B:
				(blocks with no destination are visited first)
				
				* Using the partial transfer ordered sets computed in previous iterations,
				* and the output sets of bb
				* Compute output **ordered sets** for bb
				
				* schedule bb given the input set and the output **ordered sets**
					-- this can be complicated
					-- exhaustive search or valid orderings?
					-- cost function
					-- - run length
					-- - stack optimality
					-- - useful migration
				* (yields an expression and an input ordered set)
				
				* Store input ordered set into a partial list of ordered transfer sets
			
			* Now, for each bb in B, we have an expression that implements the bb.
			* Moreover, all basic blocks assume a known, global ordering of transfer sets.
			
			* Now get an ordering for basic blocks that maximizes straight-line code
				* Compute a set of targets based on the last instruction of each expression scheduled above.
				* Compute an ordering such that the number of simple concatenations is maximized.
				* TODO: Constraint: entry must be first.
				* TODO: come up with an algorithm for this
			
			* Now, write an expression for the entire function.
			* Rewrite labels with offsets.
			Use a peephole pass to eliminate useless code.
			
			Now the function is a single basic block with exactly one expression
			* Create a segment
			
			TODO: how can we optimize run length *across* basic blocks?
			-- hot paths
			-- optimize all paths naively
			-- jit
			*/
			
			// Analyze control flow
			//Hashtable<BasicBlock, HashSet<BasicBlock>> targets = FunctionAnalysis.enumerateTargets(F);
			
			// Maintain compiled basic blocks
			HashMap<BasicBlock, Expression> scheduledBBs = new HashMap<BasicBlock, Expression>();
			
			// Determine transfer stack set
			TransferSets unrenamedTransferSets = FunctionAnalysis.computeTransferSets(F);
			
			System.out.println("Transfer sets:\n===============");
			for (BasicBlock sbb: unrenamedTransferSets.keySet()){
				System.out.println("\t"+sbb.getLabel()+" : "+ unrenamedTransferSets.get(sbb));
			}
			System.out.println("Transfer sets:\n===============");
			
			// For all bb in B, determine the output set given a basic block destination.
			OutputSets unrenamedOutputSets = FunctionAnalysis.computeOutputSets(F, unrenamedTransferSets);
			
			// Maintain a partial list of ordered transfer sets
			TransferStacks unrenamedTransferStacks = new TransferStacks();
			
			// Add entry block stack (r, args)
			unrenamedTransferStacks.put(F.getEntryBasicBlock(), new Vector<Integer>());
			unrenamedTransferStacks.get(F.getEntryBasicBlock()).add(F.getReturnAddressVariable());
			for (int arg : F.getArgumentStack()){
				unrenamedTransferStacks.get(F.getEntryBasicBlock()).add(arg);
			}
			unrenamedTransferSets.get(F.getEntryBasicBlock()).clear();
			
			// In leaf-first order of basic blocks bb in B:
			LinkedList<BasicBlock> queue = FunctionAnalysis.enumerateLeafFirst(F);
			while (!queue.isEmpty()){
				BasicBlock sourceBB = queue.removeFirst();
				
				System.out.println();
				System.out.println();
				
				// * Compute output **ordered sets** for bb
				// Using previously computed ordered transfer sets, enumerate output stacks (ordered output sets)
				HashMap<BasicBlock, Vector<Integer>> unrenamedOutputStack = new HashMap<BasicBlock, Vector<Integer>>();
				HashMap<BasicBlock, HashSet<Integer>> unrenamedOutputSet = new HashMap<BasicBlock, HashSet<Integer>>();
				
				// This can cause a lot of trouble!
				// In case of loops, there is no leaf-first ordering!
				// Therefore, something must be scheduled without knowing the otput stack (but knowing the output set)
				// In this case, schedule 
				
				// Handle nulls
				if (!unrenamedTransferStacks.containsKey(sourceBB)) unrenamedTransferStacks.put(sourceBB, new Vector<Integer>());
				if (!unrenamedTransferSets.containsKey(sourceBB)) unrenamedTransferSets.put(sourceBB, new HashSet<Integer>());
				
				// Populate output sets
				// Move data structure references so everything is updated correctly!
				if(unrenamedOutputSets.containsKey(sourceBB)){
					for (BasicBlock targetBB : unrenamedOutputSets.get(sourceBB).keySet()){
						// Handle nulls
						if (!unrenamedTransferStacks.containsKey(targetBB)) unrenamedTransferStacks.put(targetBB, new Vector<Integer>());
						if (!unrenamedTransferSets.containsKey(targetBB)) unrenamedTransferSets.put(targetBB, new HashSet<Integer>());
						unrenamedOutputStack.put(targetBB, unrenamedTransferStacks.get(targetBB));
						unrenamedOutputSet.put(targetBB, unrenamedTransferSets.get(targetBB));
					}
				}
				
				// schedule bb given the input set and the output **ordered sets**
				// 	-- this can be complicated
				// 	-- exhaustive search or valid orderings?
				// 	-- cost function
				// 	-- - run length
				// 	-- - stack optimality
				// 	-- - useful migration
				// (yields an expression and an input ordered set)

				// Be very, very careful here..
				Expression bbSchedule = BasicBlockScheduler.schedule(sourceBB, unrenamedTransferSets.get(sourceBB), unrenamedTransferStacks.get(sourceBB), unrenamedOutputSet, unrenamedOutputStack);
				scheduledBBs.put(sourceBB, bbSchedule);
				
				// translate output stack into inputStacks entries (inputStack = inputStacks.get(sourceBB))
				// need to undo phi renaming in order to do this right
				//for (BasicBlock targetBB : unrenamedOutputStack.keySet()){
				//	if (!unrenamedTransferStacks.containsKey(targetBB)){
				//		unrenamedTransferStacks.put(targetBB, unrenamedOutputStack.get(targetBB));
				//		unrenamedTransferSets.get(targetBB).clear();
				//	}
				//}
				 
				// * Store input ordered set into a partial list of ordered transfer sets
				//inputStacks.put(sourceBB, inputStack);
			}
			
			// * Now, for each bb in B, we have an expression that implements the bb.
			// * Moreover, all basic blocks assume a known, global ordering of transfer sets.
			
			// * Now get an ordering for basic blocks that maximizes straight-line code
			// 	* Compute a set of targets based on the last instruction of each expression scheduled above.
			// 	* Compute an ordering such that the number of simple concatenations is maximized.
			// 	* TODO: Constraint: entry must be first.
			// 	* TODO: come up with an algorithm for this
			
			// For now, just go depth-first. It ought to be good enough.
			Vector<BasicBlock> bbOrdering = FunctionScheduler.schedule(F);
			
			// Use a peephole pass to eliminate useless code.
			// TODO: this doesn't happen yet
			// Peephole pass needs to have access to labels, so this is where it ought to happen
			
			// * Now, write an expression for the entire function.
			// * Rewrite labels with offsets.
			Expression fSchedule = FunctionScheduler.flatten(F, bbOrdering, scheduledBBs);
			
			
			// Now the function is a single basic block with exactly one expression
			// * Create a segment
			Segment s = new Segment();
			for (Instruction instruction : fSchedule.getInstructions()){
				s.add(instruction);
			}
			
			// Add the function to the compiled segment
			p.addSegment(F.getName(), s);
			
			// TODO: how can we optimize run length *across* basic blocks?
			// -- hot paths
			// -- optimize all paths naively
			// -- jit
		}
		
		// Inject globals init code into main
		if (null == p.getSegment("main")){
			System.out.println("ERROR: main segment not found. Globals initialization code may be omitted.");
		} else {
			Segment boot = new Segment();
			
			for (Expression gexp : Expression.getGlobalInits()){
				for (Instruction inst : gexp.getInstructions()){
					boot.add(inst);
				}
			}
			
			boot.add(new InstructionPUSH(0, "arg count"));
			boot.add(new InstructionPUSH(-1, "arg ptr"));
			boot.add(new InstructionSIR_CALL("main", "transfer control to main"));
			boot.add(new InstructionHALT("end of platform main"));
			
			p.addSegment("__boot", boot);
		}
		
		return p;
	}
}
