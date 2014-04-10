package stack.asm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import stack.excetpion.AssemblerException;
import stack.isa.Instruction;
import stack.isa.LabelInstruction;
import stack.isa.core.InstructionBNZ;
import stack.isa.core.InstructionCALL_PC;
import stack.isa.core.InstructionJ_PC;
import stack.isa.sir.InstructionSIR_BRANCH;
import stack.isa.sir.InstructionSIR_CALL;
import stack.isa.sir.InstructionSIR_JUMP;

public class Assembler {
	
	/**
	Determines an absolute order of segments in a program (entry point goes first).
	A program cannot multiple segments, but no entry point ("main").
	**/
	public static List<String> orderSegments(Program prog) throws AssemblerException {
		// assign an absolute ordering
		ArrayList<String> ordering = new ArrayList<String>();
		LinkedList<String> labels = new LinkedList<String>(prog.getLabels());
		
		if (labels.size() == 1){
			ordering.add(labels.remove());
			return ordering;
		}
		
		if (labels.contains("__boot")) {
			// otherwise, we need an entry point
			// use the main function ("@main") - put it first
			ordering.add("__boot");
			labels.remove("__boot");
		} else {
			// otherwise we do not know how to determine a legal order of segments
			System.out.println("Multiple segments, but no __boot segment!");
		}
		
		// arrange the rest in some arbitrary order
		for (String label : labels){
			ordering.add(label);
		}
		
		// Clear scheduled labels
		labels.clear();
		
		return ordering;
	}
	
	/**
	Reduces a program to a single segment beginning with the entry point.
	A program cannot multiple segments, but no entry point ("main").
	@throws AssemblerException 
	**/
	public static Program collapse (Program prog) throws AssemblerException{
		// assign an absolute ordering
		List<String> ordering = orderSegments(prog);
		
		// pre-compute offsets for each label
		Hashtable<String, Integer> offsets = new Hashtable<String, Integer>();
		int offset = 0;
		for (int i=0; i<ordering.size(); i++){
			offsets.put(ordering.get(i), offset);
			offset += prog.getSegment(ordering.get(i)).size();
		}
		
		// create a new segment to hold the resulting code
		Segment collapsedSegment = new Segment();
		
		// go through the segments in the order selected earlier
		for (int i=0; i<ordering.size(); i++){
			Segment seg = prog.getSegment(ordering.get(i));
			
			// - for each instruction in the segment
			for (int j=0; j<seg.size(); j++){
				Instruction unresolved_instruction = seg.get(j);
				
				// - - resolve label, if any
				LinkedList<Instruction> resolved_instructions = new LinkedList<Instruction>();
				if (unresolved_instruction instanceof LabelInstruction){
					// Calculate offset of the current instruction relative to the label.
					int label_offset = offsets.get(((LabelInstruction)unresolved_instruction).getLabel());
					int instruction_offset = j + offsets.get(ordering.get(i));
					int relative_offset = label_offset - instruction_offset;
					
					// replace absolute addresses with PIC code, where possible
					if (unresolved_instruction instanceof InstructionSIR_BRANCH){
						resolved_instructions.add(new InstructionBNZ(relative_offset, ""));
						
					} else if (unresolved_instruction instanceof InstructionSIR_CALL){
						resolved_instructions.add(new InstructionCALL_PC(relative_offset, ""));
						
					} else if (unresolved_instruction instanceof InstructionSIR_JUMP){
						resolved_instructions.add(new InstructionJ_PC(relative_offset, ""));
						
					} else {
						// otherwise we do not know how to resolve the label on this unsupported instruction
						throw new AssemblerException("Unsupported labeled (virtual) instruction in  segments in program, but no \"main\" (entry) segment!");
					}
					
				} else {
					resolved_instructions.add(unresolved_instruction);
				}
				
				// copy the instruction into the new segment
				for (Instruction resolvedInstruction : resolved_instructions){
					collapsedSegment.add(resolvedInstruction);
				}
			}
		}
		
		// create a new program
		Program collapsedProgram = new Program();

		// insert the segment into the new program. Call it "main" if main exists, otherwise ""
		collapsedProgram.addSegment("", collapsedSegment);
		
		return collapsedProgram;
	}
	
	public static Binary assemble (Program prog) throws AssemblerException{
		// Collapse the program into a single segment (remove all labels)
		prog = collapse(prog);
		
		// Peephole optimize control flow (remove useless jumps created by subsequent labels)
		// Segment seg = prog.getSegment("");
		// seg = PeepholeOptimization.apply(seg /*, new PassNOP(),  */);
		
		// TODO: this isn't so simple - removing instructions can break relative & absolute addresses. Need to
		// resolve all affected addresses when time an instruction is removed. Leave this alone for now
		
		// Create a new binary to hold the resulting 
		Binary binary = new Binary();
		
		// go instruction by instruction
		Segment seg = prog.getSegment("");
		for (Instruction instruction : seg){
			// and translate each instruction into bits.
			int binaryInstruction = instruction.toBinary();
					
			// Copy the resulting word into the binary
			binary.addWord(binaryInstruction);
		}
		
		// Return complete binary
		return binary;
	}
}
