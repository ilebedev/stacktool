package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionDROP extends OneImmediateInstruction {
	
	public InstructionDROP(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.DROP, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.DROP, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-1);
		stack0.checkDeepAccess(getImmediate());
		
		// Drop the [imm16] value at the stack0
		stack0.remove(getImmediate());
		
		context.cycle();
	}
}
