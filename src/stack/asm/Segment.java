package stack.asm;

import java.util.LinkedList;

import stack.isa.Instruction;
import stack.isa.LabelInstruction;

public class Segment extends LinkedList<Instruction>{
	private static final long serialVersionUID = 1L;

	public Segment(){
		super();
	}
	
	public Segment(Segment seg){
		super(seg);
	}
	
	/**
	Replace all labels referenced in the segment with
	corresponding de-aliased labels according to the provided alias dictionary.
	 **/
	public void deAlias(AliasMap map){
		// for each instruction
		for (Instruction instruction : this){
			// if this instruction has a label to resolve
			if (instruction instanceof LabelInstruction){
				String aliasedLabel = ((LabelInstruction)instruction).getLabel();
				
				// Lookup de-aliased label (lookup correctly evaluates non-aliased labels to the same label).
				String deAliasedLabel = map.lookup(aliasedLabel);
				
				// replace the label with its de-aliased equivalent:
				((LabelInstruction)instruction).setLabel(deAliasedLabel);
			}
		}
	}
}
