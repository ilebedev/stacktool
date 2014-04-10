package stack.isa.core;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionJ extends Instruction {
	
	public InstructionJ(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.J, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.J);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(-1);
		
		// jump to address = top of stack.
		int address = stack0.get(0);
		stack0.remove(0);
		
		context.setNPC(address);
		
		context.cycle();
	}
	
	@Override
	public boolean isTerminal(){
		return true;
	}
}
