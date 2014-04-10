package stack.asm;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import stack.isa.Instruction;

public class Program {
	public Hashtable<String, Segment> program;
	
	public Program(){
		program = new Hashtable<String, Segment>();
	}
	
	public int getNumSegments(){
		return program.size();
	}
	
	/**
	Get size (in words) of the program.
	**/
	public int size(){
		int s = 0;
		for(Segment segment : program.values()){
			s += segment.size();
		}
		return s;
	}
	
	public List<String> getLabels(){
		LinkedList<String> labels = new LinkedList<String>();
		for (String label : program.keySet()){
			labels.add(label);
		}
		return labels;
	}
	
	public Segment getSegment(String label){
		return program.get(label);
	}
	
	public void addSegment(String label, Segment segment){
		program.put(label, segment);
	}
	
	/** In cases where multiple labels refer to the same instruction
	(for example in a function, where the first instruction is labeled by both the function and the basic block)
	But since the program representation does not assume sequential placement of labeled blocks,
	we must ensure exactly one label is consistently used to refer to a segment. **/
	public void deAlias(AliasMap aliasMap){
		// Aliases (synonyms) are stored in the aliasMap.
		// for each segment in program
		for (Segment segment : program.values()){
			// Replace all labels with their de-aliased (primary equivalent) label.
			segment.deAlias(aliasMap);
		}
	}
	
	@Override
	public String toString(){
		// Make a data structure to hold the summary table
		StringBuilder info = new StringBuilder();
		
		// do some basic analysis
		List<String> labels = getLabels();
		boolean hasMain = labels.contains("main");
		labels.remove("main");
		
		// Print the high-level summary:
		info.append(size());
		info.append(" instructions in ");
		info.append(getNumSegments());
		info.append(" segments;");
		
		if (hasMain){
			info.append(" has MAIN;");
			info.append('\n');
			
			Segment seg = getSegment("main");
			info.append(String.format("%20s\t:\t%d instructions\n", "(main)", seg.size()));
			for (Instruction instruction : seg){
				info.append(instruction.toString());
				info.append('\n');
			}
		}
		
		// Print per-label summary
		for (String label : labels){
			info.append('\n');
			
			String prettyLabel = label;
			if (prettyLabel == ""){
				prettyLabel = "(no label)";
			}
			
			Segment seg = getSegment(label);
			info.append(String.format("%s:\t%d instructions\n", prettyLabel, seg.size()));
			for (Instruction instruction : seg){
				info.append(instruction.toString());
				info.append('\n');
			}
		}
		
		return info.toString();
	}
}
