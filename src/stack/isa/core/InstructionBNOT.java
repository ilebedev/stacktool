package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionBNOT extends Instruction {
	
	public InstructionBNOT(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.BNOT, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.BNOT);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(0);
		
		// Push NOT of 1st item on stack
		int val = stack0.get(0);
		stack0.remove(0);
		
		int result = ~val;
		
		stack0.add(0, result);
		
		context.cycle();
	}
}
