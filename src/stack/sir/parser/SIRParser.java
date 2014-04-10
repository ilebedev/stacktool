package stack.sir.parser;

import stack.excetpion.ParserException;
import stack.sir.model.Module;
import stack.util.ParserState;

public class SIRParser {

	private SIRParser(){
		// nothing
	}
	
	public static Module parseSIR(String text) throws ParserException{
		// Remove comments
		ParserState state = new ParserState(removeComments(text.trim()));
		
		// SIR may only begin with a module.
		try{
			Module module = SIRProductions.parseModule(state);
			
			if (!state.in.isEmpty()){
				throw new ParserException("Leftover characters after done parsing module :\"" + state.in + "\"");
			}
			
			return module;
		} catch (Exception e){
			throw new ParserException("Error while parsing SIR : " + e.getMessage() + "\n" + state.in);
		}   
	}
	
	private static String removeComments(String input){
		return input.replaceAll("#.*","\n");
	}
}