package stack.isa.sir;

import stack.excetpion.SimulatorException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public class InstructionUNREACHABLE extends Instruction {
	public InstructionUNREACHABLE(){
		super("");
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.UNREACHABLE, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.UNREACHABLE);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException {
		throw new SimulatorException("Tried to execute an instruction declared UNREACHABLE!");
	}
	
	@Override
	public boolean isTerminal() {
		return true;
	}
}
