package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionAUX2MAIN_CP extends Instruction {
	
	public InstructionAUX2MAIN_CP(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.AUX2MAIN_CP, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.AUX2MAIN_CP);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		// Move top value from stack # imm8_hi to stack # imm8_lo
		StackModel stack0 = core.getStack(0, context);
		StackModel stack1 = core.getStack(1, context);
		
		stack0.checkDepth(1);
		stack1.checkDepth(-1);
		
		int value = stack1.get(0);
		stack0.add(0, value);
		
		context.cycle();
	}
}
