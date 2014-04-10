package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionBOR extends Instruction {
	
	public InstructionBOR(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.BOR, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.BOR);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-1);
		
		// Push OR of 1st and 2nd item on stack
		int lhs = stack0.get(0);
		int rhs = stack0.get(1);
		
		stack0.remove(1);
		stack0.remove(0);
		
		int result = lhs | rhs;
		stack0.add(0, result);
		
		context.cycle();
	}
}
