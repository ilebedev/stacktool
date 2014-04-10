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

public class InstructionFNC_ST extends OneImmediateInstruction implements StoreInstruction{
	
	public InstructionFNC_ST(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.FNC_ST, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.FNC_ST, getImmediate());
	}

	@Override
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		int address = stack0.get(0) + getImmediate();
		
		PredictorModel predictor = core.getPredictor();
		
		// Predict, and decide between FNC_ST_EM and FNC_ST_RA
		if (predictor.isMigration(address)){
			int stack0size = predictor.getStack0Size(address);
			int stack1size = predictor.getStack1Size(address);
			
			InstructionFNC_ST_EM em = new InstructionFNC_ST_EM(stack0size, stack1size, getImmediate(), "");
			em.execute(context, core);
		} else {
			InstructionFNC_ST_RA ra = new InstructionFNC_ST_RA(getImmediate(), "");
			ra.execute(context, core);
		}
	}
}
