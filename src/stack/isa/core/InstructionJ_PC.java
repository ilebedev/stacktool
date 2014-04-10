package stack.isa.core;

import stack.excetpion.SimulatorException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.util.BinMath;

public class InstructionJ_PC extends OneImmediateInstruction {
	
	public InstructionJ_PC(int imm16, String comment){
		super(imm16, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.J_PC, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.J_PC, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException {
		// conditional branch
		int address = context.getPC() + BinMath.signExtend(16, getImmediate()); // relative to the current PC
		context.setNPC(address);
		
		context.cycle();
	}
	
	@Override
	public boolean isTerminal(){
		return true;
	}
}
