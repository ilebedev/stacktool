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

public class InstructionST extends OneImmediateInstruction implements StoreInstruction{
	
	public InstructionST(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.ST, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.ST, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		int address = stack0.get(0) + getImmediate();
		
		PredictorModel predictor = core.getPredictor();
		
		// Predict, and decide between ST_EM and ST_RA
		if (predictor.isMigration(address)){
			int stack0size = predictor.getStack0Size(address);
			int stack1size = predictor.getStack1Size(address);
			
			InstructionST_EM em = new InstructionST_EM(stack0size, stack1size, getImmediate(), "");
			em.execute(context, core);
		} else {
			InstructionST_RA ra = new InstructionST_RA(getImmediate(), "");
			ra.execute(context, core);
		}
	}
}
