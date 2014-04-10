package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;
import stack.util.BinMath;

public class InstructionPUSH extends OneImmediateInstruction {
	public InstructionPUSH(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.PUSH, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.PUSH, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(1);
		
		// Push sign-extended imm16 to the top of the stack
		int imm16 = getImmediate();
		int value = BinMath.signExtend(16, imm16);
		
		stack0.add(0, value);
		
		context.cycle();
	}
}
