package stack.python;

import stack.asm.ASMParser;
import stack.asm.Assembler;
import stack.asm.Binary;
import stack.asm.Program;
import stack.excetpion.AssemblerException;
import stack.excetpion.ParserException;
import stack.isa.Instruction;

public class asm {
	public static Program parse(String source, String defaultLabel) throws ParserException{
		return ASMParser.parseASM(source, defaultLabel);
	}
	
	public static Program collapse (Program prog) throws AssemblerException{
		return Assembler.collapse(prog);
	}
	
	public static Binary assemble (Program prog) throws AssemblerException{
		return Assembler.assemble(prog);
	}
	
	public static String print(Program prog) throws AssemblerException, ParserException{
		Binary bin = Assembler.assemble(prog);
		StringBuilder sb = new StringBuilder();
		
		for (int instruction : bin){
			sb.append(Instruction.disassemble(instruction).toString().toLowerCase());
		}
		
		return sb.toString();
	}
}
