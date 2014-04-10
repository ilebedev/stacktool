package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionPULL_CP extends OneImmediateInstruction {
	
	public InstructionPULL_CP(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.PULL_CP, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.PULL_CP, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(1);
		stack0.checkDeepAccess(getImmediate()+1);
		
		int var = stack0.get(getImmediate());
		stack0.add(0, var);
		
		context.cycle();
	}
}
