package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionCALL extends Instruction {
	
	public InstructionCALL(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.CALL, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.CALL);
	}
	
	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(0);
		
		// call at address = top of stack. push return address = nPC
		int address = stack0.get(0);
		stack0.remove(0);
		
		int return_address = context.getPC() + 1; // The return address is the NEXT PC!
		
		stack0.add(0, return_address);
		
		context.setNPC(address);
		
		context.cycle();
	}
}
