package stack.isa.em2.load;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.LoadInstruction;
import stack.isa.em2.EMInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionLD_RSV extends EMInstruction implements LoadInstruction{
	
	public InstructionLD_RSV(int stack0, int stack1, int offset, String comment){
		super(stack0, stack1, offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.LD_RSV, imm8_hi, imm8_lo, offset, getComment());
	}
	
	@Override
	public int toBinary() {
		return assemble(StackOP.LD_RSV, imm8_hi, imm8_lo, offset);
	}
	
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);

		stack0.checkDepth(0);
		
		// load address is at top of stack
		// Cannot modify state (pop stack) until the instruction can successfully execute.
		int address = stack0.get(0)+getOffset();
		
		// CHECK IF CORE MISS
		if (isCoreHit(address, core)){
			// Perform the load locally
			stack0.remove(0);
			
			core.link(address, context);
			int data = core.load(address);
			context.incrementRunLength();
			
			stack0.add(0, data);
			context.cycle();
		} else {
			// Core miss
			// migrate and re-execute on a remote core
			migrate(address, context, core, getStack0Depth(), getStack1Depth());
			
			// The instruction does not execute!
			// The context does not advance by a cycle
		}
	}
}
