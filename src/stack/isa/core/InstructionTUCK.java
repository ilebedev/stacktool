package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionTUCK extends OneImmediateInstruction {
	
	public InstructionTUCK(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.TUCK, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.TUCK, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDeepAccess(getImmediate());
		
		// pop the first element, and store it at stack0[imm16]
		// stack offset refers to indexes BEFORE the pop!
		
		int datum = stack0.remove(0);
		stack0.add(getImmediate(), datum);
		
		context.cycle();
	}
}
