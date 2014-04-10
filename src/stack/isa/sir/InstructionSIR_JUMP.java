package stack.isa.sir;

import stack.excetpion.SimulatorException;
import stack.isa.LabelInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public class InstructionSIR_JUMP extends LabelInstruction {
	public InstructionSIR_JUMP(String label, String comment){
		super(label, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_JUMP, label, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_JUMP);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException {
		throw new SimulatorException("SIR_JUMP is fucked right now.");
		
		//context.cycle();
	}
	
	@Override
	public boolean isTerminal(){
		return true;
	}
}
