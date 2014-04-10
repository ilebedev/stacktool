package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionPULL extends OneImmediateInstruction {
	public InstructionPULL(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.PULL, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.PULL, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDeepAccess(getImmediate());
		
		int var = stack0.get(getImmediate());
		stack0.remove(imm16);
		
		stack0.add(0, var);
		
		context.cycle();
	}
}
