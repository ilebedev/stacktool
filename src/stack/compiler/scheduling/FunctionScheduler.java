package stack.compiler.scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import stack.isa.Instruction;
import stack.isa.core.InstructionBNZ;
import stack.isa.core.InstructionCALL_PC;
import stack.isa.core.InstructionJ_PC;
import stack.isa.sir.InstructionSIR_BRANCH;
import stack.isa.sir.InstructionSIR_CALL;
import stack.isa.sir.InstructionSIR_JUMP;
import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;
import stack.sir.model.Function;

public class FunctionScheduler {
	
	public static Hashtable<BasicBlock, HashSet<BasicBlock>> getBBTargets(Function F){
		Hashtable<BasicBlock, HashSet<BasicBlock>> targets = new Hashtable<BasicBlock, HashSet<BasicBlock>>();
		
		for (BasicBlock bb : F.getBasicBlocks()){
			targets.put(bb, new HashSet<BasicBlock>());
			
			for (String label : bb.getTargetLabels()){
				targets.get(bb).add(F.getBasicBlock(label));
			}
			
		}
		
		return targets;
	}
	
	public static Vector<BasicBlock> schedule(Function F) {
		Vector<BasicBlock> schedule = new Vector<BasicBlock>();
		
		Hashtable<BasicBlock, HashSet<BasicBlock>> targets = getBBTargets(F);
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		
		// Start with main, go **depth-first** (stack)
		LinkedList<BasicBlock> queue = new LinkedList<BasicBlock>();
		queue.addFirst(F.getEntryBasicBlock());
		
		while (!queue.isEmpty()){
			BasicBlock bb = queue.removeFirst();
			
			// only do anything if not already visited
			if (visited.contains(bb)){
				// nothing
			} else {
				visited.add(bb);
				
				// add all targets **first** (depth-first)
				for (BasicBlock target : targets.get(bb)){
					queue.addFirst(target);
				}
				
				// add the bb to the flattened segment
				schedule.add(bb);
			}
		}
		
		return schedule;
	}

	public static Expression flatten(Function f, Vector<BasicBlock> bbOrdering, HashMap<BasicBlock, Expression> scheduledBBs) {
		// this can ignore the function i guess
		
		Expression E = new Expression();
		// For now, walk down the ordering
		// fp
		// Maintain the current offset
		// Maintain a mapping of labels to offsets
		int offset = 0;
		Hashtable<String, Integer> labels = new Hashtable<String, Integer>();
		
		labels.put(f.getName(), offset);
		for (BasicBlock bb : bbOrdering){
			labels.put(bb.getLabel(), offset);
			
			assert scheduledBBs.containsKey(bb);
			Expression compiledBB = scheduledBBs.get(bb);
			
			for (Instruction instruction : compiledBB.getInstructions()){
				E.appendInstruction(instruction);
				offset++;
			}
		}
		
		// Resolve all labels within the new expression
		E = resolveAvailableLabels(E, labels);
		
		return E;
	}
	
	public static Expression resolveAvailableLabels(Expression E, Hashtable<String, Integer> offsetTable){
		int cursor = 0;
		Expression newE = new Expression();
		
		for (Instruction instruction : E.getInstructions()){
			Instruction resolvedInstruction = instruction;
			
			if (instruction instanceof InstructionSIR_BRANCH){
				InstructionSIR_BRANCH si = ((InstructionSIR_BRANCH)instruction);
				String label = si.getLabel();
				
				if (offsetTable.containsKey(label)){
					int rel_address = offsetTable.get(label)-cursor;
					resolvedInstruction = new InstructionBNZ(rel_address, si.getComment());
				}
					
			} else if (instruction instanceof InstructionSIR_CALL){
				InstructionSIR_CALL si = ((InstructionSIR_CALL)instruction);
				String label = si.getLabel();
				
				if (offsetTable.containsKey(label)){
					int rel_address = offsetTable.get(label)-cursor;
					resolvedInstruction = new InstructionCALL_PC(rel_address, si.getComment());
				}
			} else if (instruction instanceof InstructionSIR_JUMP){
				InstructionSIR_JUMP si = ((InstructionSIR_JUMP)instruction);
				String label = si.getLabel();
				
				if (offsetTable.containsKey(label)){
					int rel_address = offsetTable.get(label)-cursor;
					resolvedInstruction = new InstructionJ_PC(rel_address, si.getComment());
				}
			}
		
			newE.appendInstruction(resolvedInstruction);
			cursor ++;
		}
		
		// Ignore labels that are not in the map for now
		
		return newE;
	}
}
