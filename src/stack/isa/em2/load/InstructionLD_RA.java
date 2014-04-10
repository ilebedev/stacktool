package stack.isa.em2.load;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.LoadInstruction;
import stack.isa.em2.RAInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionLD_RA extends RAInstruction implements LoadInstruction{
	
	public InstructionLD_RA(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.LD_RA, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.LD_RA, getImmediate());
	}
	
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(0);
		
		// load address is at top of stack
		// Cannot modify state (pop stack) until the instruction can successfully execute.
		int address = stack0.get(0) + getImmediate();
		
		// CHECK IF CORE MISS
		if (isCoreHit(address, core)){
			// Perform the load locally
			stack0.remove(0);
			
			int data = core.load(address);
			
			stack0.add(0, data);
			context.cycle();
		} else {
			// Pop!
			stack0.remove(0);
			
			// Issue RA request
			sendRALoad(context, core, address);
			
			// Stall until the RA response puts the loaded data onto the stack
			context.stall();
			
			// stack is populated by the response from the network 
			// context.cycle() occurs when the request is serviced!
			// Core is unblocked when the response comes back
		}
	}
}
