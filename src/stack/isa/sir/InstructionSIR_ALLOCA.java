package stack.isa.sir;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;

public class InstructionSIR_ALLOCA extends Instruction {
	
	public static int allocaCounter = 0x10000000;
	public InstructionSIR_ALLOCA(String comment){
		super(comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.SIR_ALLOCA, getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.SIR_ALLOCA);
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		
		stack0.checkDepth(1);
		
		// Push sign-extended imm16 to the top of the stack
		int value = allocaCounter;
		allocaCounter++;
		
		stack0.add(0, value);
		
		context.cycle();
	}
}
