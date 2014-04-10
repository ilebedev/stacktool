package stack.isa.core;

import stack.excetpion.SimulatorException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public class InstructionHALT extends Instruction {
	public InstructionHALT(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.HALT, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.HALT);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException {
		context.disable();
	}
	
	@Override
	public boolean isTerminal() {
		return true;
	}
}
