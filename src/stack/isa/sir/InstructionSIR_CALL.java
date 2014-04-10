package stack.isa.sir;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.LabelInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public class InstructionSIR_CALL extends LabelInstruction {
	
	public InstructionSIR_CALL(String label, String comment){
		super(label, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_CALL, label, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_CALL);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		throw new SimulatorException("SIR_CALL is fucked right now.");
		//context.cycle();
	}
}
