package stack.isa.sir;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.isa.StoreInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionSIR_STORE extends Instruction implements StoreInstruction{
	
	public InstructionSIR_STORE(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_STORE, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_STORE);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-2);
		
		// store 2nd top value at address = top of the stack
		int address = stack0.get(0);
		int data = stack0.get(1);
		
		// TODO: implement migration/ra using the predictor!
		// Idea: keep 2 instances, one for RA and one for EM. Select which to execute based on the predictor.
		
		stack0.remove(1);
		stack0.remove(0);
		
		core.getMachine().dataMemory.store(address, data);
		
		context.cycle();
	}
}
