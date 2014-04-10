package stack.sir.parser;

import stack.excetpion.ParserException;
import stack.isa.Instruction;
import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;
import stack.sir.model.Function;
import stack.sir.model.Module;
import stack.util.ParserState;

import java.util.Vector;

public class SIRProductions {
	public static Module parseModule(ParserState state) throws ParserException{
		// MODULE <identifier> {
		//     <constants table>
		//     <function>
		//     <function>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "MODULE");
		String name = SIRTerminals.parseIdentifier(state);
		SIRTerminals.parseLiteral(state, "{");
		
		Module M = new Module(name);
		
		if (SIRTerminals.checkLiteral(state, "CONSTANTS_TABLE")){
			parseConstantsTable(state);
		} else{
			// doesn't look like there are any constants, moving on
		}
		
		if (SIRTerminals.checkLiteral(state, "GLOBALS")){
			parseGlobalsTable(state);
		} else{
			// doesn't look like there are any constants, moving on
		}
		
		while (SIRTerminals.checkLiteral(state, "FUNCTION")){
			Function F = parseFunction(state);
			M.addFunction(F);
		}
		SIRTerminals.parseLiteral(state, "}");
		
		return M;
	}
	private static void parseConstantsTable(ParserState state) throws ParserException{
		// CONSTANTS_TABLE{
		//     <expression>
		//     <expression>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "CONSTANTS_TABLE");
		SIRTerminals.parseLiteral(state, "{");
		
		while (SIRTerminals.checkLiteral(state, "EXPRESSION")){
			Expression E = parseExpression(state);
			
			if ((E.getInputStack().size() != 0) || (E.getOutputStack().size() != 1)){
				throw new ParserException("Constant expression has illegal input and output stack sizes.");
			}
			
			int variable = E.getOutputStack().get(0);
			
			Expression.addConstant(variable, E);
		}
		
		SIRTerminals.parseLiteral(state, "}");
	}
	
	private static void parseGlobalsTable(ParserState state) throws ParserException{
		// GLOBALS{
		//     <expression>
		//     <expression>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "GLOBALS");
		SIRTerminals.parseLiteral(state, "{");
		
		while (SIRTerminals.checkLiteral(state, "EXPRESSION")){
			Expression E = parseExpression(state);
			
			if ((E.getInputStack().size() != 1) || (E.getOutputStack().size() != 0)){
				throw new ParserException("Global expression has illegal input and output stack sizes.");
			}
			
			int variable = E.getInputStack().get(0);
			
			Expression.addGlobal(variable, E);
		}
		
		SIRTerminals.parseLiteral(state, "}");
	}
	
	public static Function parseFunction(ParserState state) throws ParserException{
		// FUNCTION <return address> <return stack size> factorial <argument stack>{
		//     <basic block>
		//     <basic block>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "FUNCTION");
		
		int returnAddress = SIRTerminals.parseVariable(state);
		Vector<Integer> returnStack = SIRTerminals.parseVariableList(state);
		String name = SIRTerminals.parseIdentifier(state);
		Vector<Integer> argumentStack = SIRTerminals.parseVariableList(state);
		
		Function F = new Function(name, returnAddress, returnStack.size(), argumentStack);
		
		SIRTerminals.parseLiteral(state, "{");
		
		while (SIRTerminals.checkLiteral(state, "BASIC_BLOCK")){
			BasicBlock BB = parseBasicBlock(state);
			F.appendBasicBlock(BB);
		}
		SIRTerminals.parseLiteral(state, "}");
		
		return F;
	}
	
	public static BasicBlock parseBasicBlock(ParserState state) throws ParserException{
		// BASIC_BLOCK @label <phiTable> {
		//     <expression>
		//     <expression>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "BASIC_BLOCK");
		String label = SIRTerminals.parseLabel(state);
		
		BasicBlock BB = new BasicBlock(label);
		
		parsePhiTable(state, BB);
		
		SIRTerminals.parseLiteral(state, "{");
		
		while (SIRTerminals.checkLiteral(state, "EXPRESSION")){
			Expression E = parseExpression(state);
			BB.appendExpression(E);
		}
		SIRTerminals.parseLiteral(state, "}");
		
		return BB;
	}
	
	private static void parsePhiTable(ParserState state, BasicBlock BB) throws ParserException{
		// [
		//     <label> : <mappings>,
		//     <label> : <mappings>,
		//     ...
		// ]
		SIRTerminals.parseLiteral(state, "[");
		
		while (!SIRTerminals.checkLiteral(state, "]")){
			// Parse each phi table entry
			//    <label> : (<<var>-><var>>,<<var>-><var>>),
			String label = SIRTerminals.parseLabel(state);
			SIRTerminals.parseLiteral(state, ":");
			SIRTerminals.parseLiteral(state, "(");
			
			while (SIRTerminals.checkLiteral(state, "<")){
				// Parse mapping pairs
				SIRTerminals.parseLiteral(state, "<");
				int sourceVar = SIRTerminals.parseVariable(state);
				SIRTerminals.parseLiteral(state, "->");
				int localVar = SIRTerminals.parseVariable(state);
				SIRTerminals.parseLiteral(state, ">");
				
				BB.addPhiMapping(localVar, label, sourceVar);
				
				if (SIRTerminals.checkLiteral(state, ",")){
					SIRTerminals.parseLiteral(state, ",");
				}
			}
			
			SIRTerminals.parseLiteral(state, ")");
			
			// Consume comma, if one exists
			if (SIRTerminals.checkLiteral(state, ",")){
				SIRTerminals.parseLiteral(state, ",");
			}
		}
		
		SIRTerminals.parseLiteral(state, "]");
	}
	
	public static Expression parseExpression(ParserState state) throws ParserException{
		// EXPRESSION <VariableList> -> <VariableList> {
		//     <constants table>
		//     <function>
		//     <function>
		//     ...
		// }
		
		SIRTerminals.parseLiteral(state, "EXPRESSION");
		Vector<Integer> inputStack = SIRTerminals.parseVariableList(state);
		SIRTerminals.parseLiteral(state, "->");
		Vector<Integer> outputStack = SIRTerminals.parseVariableList(state);
		SIRTerminals.parseLiteral(state, "{");
		
		Expression E = new Expression();
		E.setInputStack(inputStack);
		E.setOutputStack(outputStack);
		
		while (!SIRTerminals.checkLiteral(state, "}")){
			// Parse instruction
			state.in =  state.in.trim();
			String line = state.in.substring(0, state.in.indexOf("\n")).trim();
			state.in = state.in.substring(state.in.indexOf("\n"));
			
			Instruction instruction = Instruction.disassemble(line);
			E.appendInstruction(instruction);
		}
		
		SIRTerminals.parseLiteral(state, "}");
		
		return E;
	}
}