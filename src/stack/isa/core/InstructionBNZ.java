package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;
import stack.util.BinMath;

public class InstructionBNZ extends OneImmediateInstruction {
	
	public InstructionBNZ(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.BNZ, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.BNZ, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-1);
		
		// conditional branch
		boolean condition =stack0.get(0) != 0;
		stack0.remove(0);
		
		int address = context.getPC() + BinMath.signExtend(16, getImmediate()); // relative to the current PC
		
		if (condition){
			context.setNPC(address);
		} else {
			// nothing
		}
		
		context.cycle();
	}
	
	@Override
	public boolean isTerminal(){
		return true;
	}
}
