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

public class InstructionLD extends OneImmediateInstruction implements LoadInstruction{
	public InstructionLD(int offset, String comment){
		super(offset, comment);
	}
	
	@Override
	public String toString() {
		return get_string(StackOP.LD, getImmediate(), getComment());
	}

	@Override
	public int toBinary() {
		return assemble(StackOP.LD, getImmediate());
	}
	
	public void execute(Context context, CoreModel core) throws SimulatorException, UnderflowException, OverflowException {
		StackModel stack0 = core.getStack(0, context);
		int address = stack0.get(0)+getImmediate();
		
		PredictorModel predictor = core.getPredictor();
		
		// Predict, and decide between LD_EM and LD_RA
		if (predictor.isMigration(address)){
			int stack0size = predictor.getStack0Size(address);
			int stack1size = predictor.getStack1Size(address);
			
			InstructionLD_EM em = new InstructionLD_EM(stack0size, stack1size, getImmediate(), "");
			em.execute(context, core);
		} else {
			InstructionLD_RA ra = new InstructionLD_RA(getImmediate(), "");
			ra.execute(context, core);
		}
	}
}
