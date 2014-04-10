package stack.isa.em2.load;

import stack.excetpion.OverflowException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.LoadInstruction;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.PredictorModel;
import stack.simulator.machine.models.StackModel;

public class InstructionFNC_LD extends OneImmediateInstruction implements LoadInstruction{
	
	public InstructionFNC_LD(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.FNC_LD, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.FNC_LD, getImmediate());
	}
	
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		int address = stack0.get(0) + getImmediate();
		
		PredictorModel predictor = core.getPredictor();
		
		// Predict, and decide between FNC_LD_EM and FNC_LD_RA
		if (predictor.isMigration(address)){
			int stack0size = predictor.getStack0Size(address);
			int stack1size = predictor.getStack1Size(address);
			
			InstructionFNC_LD_EM em = new InstructionFNC_LD_EM(stack0size, stack1size, getImmediate(), "");
			em.execute(context, core);
		} else {
			InstructionFNC_LD_RA ra = new InstructionFNC_LD_RA(getImmediate(), "");
			ra.execute(context, core);
		}
	}
}
