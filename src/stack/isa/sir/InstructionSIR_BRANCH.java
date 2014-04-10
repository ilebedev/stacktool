package stack.isa.sir;

import stack.excetpion.SimulatorException;
import stack.isa.LabelInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public class InstructionSIR_BRANCH extends LabelInstruction {
	String label_false;
	
	public InstructionSIR_BRANCH(String label_true, String comment){
		super(label_true, comment);
	}
	
	public String getFalseLabel(){
		return label_false;
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_BRANCH, getLabel() + " : " + label_false, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_BRANCH);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException {
		throw new SimulatorException("SIR_BRANCH is fucked right now.");
		//context.cycle();
	}
}
