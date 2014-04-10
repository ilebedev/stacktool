package stack.simulator;

import java.util.List;

import stack.asm.Assembler;
import stack.asm.Binary;
import stack.asm.Program;
import stack.asm.Segment;
import stack.excetpion.AssemblerException;
import stack.excetpion.LoaderException;
import stack.excetpion.ParserException;
import stack.isa.Instruction;
import stack.simulator.machine.Machine;


public final class Loader {
	private static int baseAddress = 0;
	
	public static void load(Machine machine, Binary binary) throws LoaderException, ParserException{
		// linearly place the binary in memory beginning at address 0
		for (int address=0; address<binary.size(); address++){
			int word = binary.get(address);
			machine.instructionMemory.store(address, Instruction.disassemble(word));
		}
	}
	
	public static void reset(){
		baseAddress = 0;
	}
	
	public static void load(Machine machine, Program program) throws LoaderException{
		// linearly place the program in memory beginning at address 0
		// put "main" first (if any), arrange the rest in some order.
		// populate the label table
		
		// Compute an absolute ordering of program segments (put entry point first)
		List<String> ordering;
		
		try {
			ordering = Assembler.orderSegments(program);
		} catch (AssemblerException e) {
			throw new LoaderException(e.getMessage());
		}
		
		// For each label, record its label and contents.
		
		for (String label : ordering){
			machine.instructionMemory.addLabel(label, baseAddress);
			Segment seg = program.getSegment(label);
			
			for (Instruction inst : seg){
				machine.instructionMemory.store(baseAddress, inst);
				baseAddress += 1;
			}
		}
	}
}
