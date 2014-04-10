package stack.python;

import stack.excetpion.ParserException;
import stack.sir.model.Module;
import stack.sir.parser.SIRParser;

public class sir {
	public static Module parse(String sir) throws ParserException{
		return SIRParser.parseSIR(sir);
	}
}
