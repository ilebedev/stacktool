package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionSWAP extends OneImmediateInstruction {
	
	public InstructionSWAP(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SWAP, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SWAP, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDeepAccess(getImmediate());
		
		// Swap the value at the top of the stack and the value at stack0[imm16]
		int val1 = stack0.get(0);
		int val2 = stack0.get(getImmediate());
		
		stack0.set(0, val2);
		stack0.set(getImmediate(), val1);
		
		context.cycle();
	}
}
