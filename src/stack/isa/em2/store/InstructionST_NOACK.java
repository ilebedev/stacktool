package stack.isa.em2.store;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.OneImmediateInstruction;
import stack.isa.StoreInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.PredictorModel;
import stack.simulator.machine.models.StackModel;

public class InstructionST_NOACK extends OneImmediateInstruction implements StoreInstruction{
	
	public InstructionST_NOACK(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.ST_NOACK, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.ST_NOACK, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		int address = stack0.get(0) + getImmediate();
		
		PredictorModel predictor = core.getPredictor();
		
		// Predict, and decide between ST_EM_NOACK and ST_RA_NOACK
		if (predictor.isMigration(address)){
			int stack0size = predictor.getStack0Size(address);
			int stack1size = predictor.getStack1Size(address);
			
			InstructionST_EM_NOACK em = new InstructionST_EM_NOACK(stack0size, stack1size, getImmediate(), "");
			em.execute(context, core);
		} else {
			InstructionST_RA_NOACK ra = new InstructionST_RA_NOACK(getImmediate(), "");
			ra.execute(context, core);
		}
	}
}
