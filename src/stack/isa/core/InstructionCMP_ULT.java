package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionCMP_ULT extends Instruction {
	
	public InstructionCMP_ULT(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.CMP_ULT, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.CMP_ULT);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-1);
		
		// Push unsigned < of 1st and 2nd item on stack
		long lhs = stack0.get(0) & 0xffffffffL;
		long rhs = stack0.get(1) & 0xffffffffL;
		
		stack0.remove(1);
		stack0.remove(0);
		
		int result = (lhs < rhs) ? 1: 0;
		
		stack0.add(0, result);
		
		context.cycle();
	}
}
