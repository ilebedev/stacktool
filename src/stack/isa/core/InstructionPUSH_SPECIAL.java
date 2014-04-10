package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionPUSH_SPECIAL extends OneImmediateInstruction {
	
	public InstructionPUSH_SPECIAL(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.PUSH_SPECIAL, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.PUSH_SPECIAL);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(1);
		
		// Push sign-extended imm16 to the top of the stack
		int value = 0;
		
		switch(getImmediate()){
		case 0:
			value = context.getPC();
			break;
		case 1:
			value = core.getCoreID();
			break;
		case 2:
			value = context.getContextID();
			break;
		default:
			throw new SimulatorException("bad PUSH_SPECIAL code!");
		}
		
		stack0.add(0, value);
		
		context.cycle();
	}
}
