package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionSRA extends OneImmediateInstruction {
	
	public InstructionSRA(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SRA, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SRA, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(0);
		
		// Shift top item on the stack right by imm16.
		// Preserve sign (arithmetic shift)
		// NOTE: Undefined behavior if imm16 > 32
		int val = stack0.get(0);
		stack0.remove(0);
		
		int result = val >> getImmediate(); // >> is ARITHMETIC shift in java!
		
		stack0.add(0, result);
		
		context.cycle();
	}
}
