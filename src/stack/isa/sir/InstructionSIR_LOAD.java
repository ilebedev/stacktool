package stack.isa.sir;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.isa.LoadInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionSIR_LOAD extends Instruction implements LoadInstruction{
	
	public InstructionSIR_LOAD(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_LOAD, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_LOAD);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(0);
		
		// store 2nd top value at address = top of the stack
		int address = stack0.get(0);
		
		// TODO: implement migration/ra using the predictor!
		// Idea: keep 2 instances, one for RA and one for EM. Select which to execute based on the predictor.
		
		int result = core.getMachine().dataMemory.load(address);
		
		stack0.remove(0);
		
		stack0.add(0, result);
		
		context.cycle();
	}
}
