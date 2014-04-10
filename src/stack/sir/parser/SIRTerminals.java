  package stack.sir.parser;

import stack.excetpion.ParserException;
import stack.util.ParserState;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SIRTerminals {
	public static String parseIdentifier(ParserState state) throws ParserException{
		Pattern pattern_identifier = Pattern.compile("\\s*([A-Z][_\\.A-Z]*)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern_identifier.matcher(state.in);
		
		String identifier = "";
		
		if (matcher.find(0)){
			// Found something that looks like a label!
			identifier = matcher.group(1);
			state.in = state.in.substring(matcher.end());
			
		} else {
			throw new ParserException(String.format("Failed to match identifier: \"%30s...\"", state.in));
		}
		
		return identifier;
	}
	
	public static Vector<Integer> parseVariableList(ParserState state) throws ParserException{
		parseLiteral(state, "(");
		
		Vector<Integer> list = new Vector<Integer>();
		
		while (!checkLiteral(state, ")")){
			int variable = parseVariable(state);
			list.add(variable);
			
			if (checkLiteral(state, ",")){
				parseLiteral(state, ",");
			}
		}
		
		parseLiteral(state, ")");
		
		return list;
	}
	
	public static int parseVariable(ParserState state) throws ParserException{
		Pattern pattern_variable = Pattern.compile("^\\s*([1-9][0-9]*)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern_variable.matcher(state.in);
		
		int variable = 0;
		
		if (matcher.find(0)){
			// Found something that looks like a label!
			variable = Integer.valueOf(matcher.group(1));
			state.in = state.in.substring(matcher.end());
			
		} else {
			throw new ParserException(String.format("Failed to match variable: \"%30s...\"", state.in));
		}
		
		return variable;
	}

	public static String parseLabel(ParserState state) throws ParserException{
		Pattern pattern_label = Pattern.compile("^\\s*(@[A-Z][_\\.A-Z0-9]*)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern_label.matcher(state.in);
		
		String label = "";
		
		if (matcher.find(0)){
			// Found something that looks like a label!
			label = matcher.group(1);
			state.in = state.in.substring(matcher.end());
			
		} else {
			throw new ParserException(String.format("Failed to match label: \"%30s...\"", state.in));
		}
		
		return label;
	}
	
	/**
	Parses and consumes a literal token (case independent) or throws an exception.
	**/
	public static void parseLiteral(ParserState state, String literal) throws ParserException{
		Pattern pattern_label = Pattern.compile("^\\s*" + Pattern.quote(literal), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern_label.matcher(state.in);
		
		if (matcher.find(0)){
			// Found the literal!
			state.in = state.in.substring(matcher.end());
		} else {
			throw new ParserException(String.format("Failed to match literal: \"%s\" in \"%30s...\"", literal, state.in));
		}
	}
	
	/**
	Parses, but does not consume a literal token (case independent) or throws an exception.
	Returns false if fails to parse the token.
	**/
	public static boolean checkLiteral(ParserState state, String literal){
		Pattern pattern_label = Pattern.compile("^\\s*" + Pattern.quote(literal), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern_label.matcher(state.in);
		
		//TODO: somehow matches ")" on "6)"
		return matcher.find(0);
	}
}