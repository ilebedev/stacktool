package stack.isa.em2.store;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.StoreInstruction;
import stack.isa.em2.RAInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionFNC_ST_RA extends RAInstruction implements StoreInstruction{
	
	public InstructionFNC_ST_RA(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.FNC_ST_RA, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.FNC_ST_RA, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-2);
		
		// Fence
		if (!context.noOutstandingAcks()){
			// Do not advance by a cycle!
			// Simply waste a cycle waiting for Acks to arrive
			return;
		}
		
		// store address is at top of stack
		// Cannot modify state (pop stack) until the instruction can successfully execute.
		int address = stack0.get(0) + getImmediate();
		int data = stack0.get(1);
		
		// CHECK IF CORE MISS
		if (isCoreHit(address, core)){
			// Perform the store locally
			stack0.remove(1);
			stack0.remove(0);
			
			core.store(address, data);
			
			context.cycle();
		} else {
			// Pop!
			stack0.remove(1);
			stack0.remove(0);
			
			// Issue RA request
			sendRAStore(context, core, address, data);
			
			// Promise an ack
			context.promiseAck();
			
			// non-blocking, so we can continue (the ack will eventually come back)
			context.cycle();
		}
	}
}
