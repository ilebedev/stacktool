package stack.isa.em2.store;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.StoreInstruction;
import stack.isa.em2.EMInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionST_COND extends EMInstruction implements StoreInstruction{
	
	public InstructionST_COND(int stack0, int stack1, int offset, String comment){
		super(stack0, stack1, offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.ST_COND, imm8_hi, imm8_lo, getOffset(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.ST_COND, imm8_hi, imm8_lo, getOffset());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);

		stack0.checkDepth(-1);
				
		// store address is at top of stack
		// Cannot modify state (pop stack) until the instruction can successfully execute.
		int address = stack0.get(0) + getOffset();
		int data = stack0.get(1);
		
		// CHECK IF CORE MISS
		if (isCoreHit(address, core)){
			// Perform the store locally
			stack0.remove(1);
			stack0.remove(0);
			
			if (core.isLinked(address, context)){
				core.store(address, data);
				stack0.add(0, 1); // successful
			} else {
				stack0.add(0, 0); // unsuccessful
			}
			context.incrementRunLength();
			
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
