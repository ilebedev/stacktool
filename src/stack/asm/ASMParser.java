package stack.asm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stack.excetpion.ParserException;
import stack.isa.Instruction;

public class ASMParser {
	private ASMParser(){
		// nothing
	}
	
	public static Program parseASM(String text, String defaultLabel) throws ParserException{
		// break into lines
		String[] lines = text.split("\n");
		
		// Create an alias map to keep track of redundant labels
		AliasMap aliasMap = new AliasMap();
		
		// Create a new program
		Program prog = new Program();
		
		// Create new segment, ""
		String label = defaultLabel;
		Segment segment = new Segment();
		
		// Prepare pattern for recognizing comments
		Pattern pattern_comment = Pattern.compile("\\s*\\#.*", Pattern.CASE_INSENSITIVE);
		
		
		// Prepare pattern for recognizing labels
		Pattern pattern_label = Pattern.compile("\\s*(@[\\._\\~A-Z]+)", Pattern.CASE_INSENSITIVE);
		
		// for each line:
		for (int lineNumber=0; lineNumber<lines.length; lineNumber++){
			// remove leading and trailing whitespace
			String line = lines[lineNumber].trim();
			
			// Ignore empty lines
			if (line.length() == 0){
				// done handling this line
				continue;
			}
			
			// Ignore comments
			Matcher REComment = pattern_comment.matcher(line);
			if (REComment.matches()){
				// done handling this line
				continue;
			}
			
			// Handle labels
			Matcher RELabel = pattern_label.matcher(line);
			if (RELabel.matches()){
				// handle label
				String newLabel = RELabel.group(1);
				
				// if current segment is empty:
				if (segment.isEmpty()){
					// Unless the label currently has no name,
					if (label == ""){
						// (in which case we populate the name with the new label)
						label = newLabel;
					} else {
						// the new label is an alias
						aliasMap.add(newLabel, label);
					}
				} else {
					// otherwise it begins a new segment,
					// so we record the old segment
					prog.addSegment(label, segment);
					
					// and initialize a new one
					label = newLabel;
					segment = new Segment();
				}
				
				// done handling this line
				continue;
			}

			// If it isn't any of the above, it must be an instruction (or an illegal statement)
			try {
				Instruction instruction = Instruction.disassemble(line);
				
				// If no exception has occurred, this is indeed an instruction.
				// add it to the current segment
				segment.add(instruction);
				
			} catch (ParserException e) {
				throw e;
			}
		}
		
		// close current segment
		if (!segment.isEmpty()){
			prog.addSegment(label, segment);
		}
		
		// resolve aliases!
		prog.deAlias(aliasMap);
		
		// done!
		return prog;
	}
}
