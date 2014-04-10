package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;
import stack.util.BinMath;

public class InstructionCALL_PC extends OneImmediateInstruction {
	
	public InstructionCALL_PC(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.CALL_PC, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.CALL_PC, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(1);
		
		// Return address
		int return_address = context.getPC() + 1;
		stack0.add(0, return_address);
		
		// conditional branch
		int address = context.getPC() + BinMath.signExtend(16, getImmediate()); // relative to the current PC
		context.setNPC(address);
		
		context.cycle();
	}
}
