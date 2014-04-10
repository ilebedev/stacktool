package stack.simulator.machine.events;

import stack.excetpion.SimulatorException;
import stack.simulator.Context;
import stack.simulator.des.DiscreteEventSimulator;
import stack.simulator.des.Event;
import stack.simulator.machine.models.CoreModel;

public class MigrationEvent extends Event {
	Context context;
	CoreModel src, target;
	MessageType type;
	int stack0depth, stack1depth;
	
	public MigrationEvent(Context context, CoreModel src, CoreModel target, MessageType type, int stack0depth, int stack1depth) {
		this.context = context;
		this.src = src;
		this.target = target;
		this.type = type;
		this.stack0depth = stack0depth;
		this.stack1depth = stack1depth;
	}

	@Override
	public void callback(DiscreteEventSimulator DES) throws SimulatorException {
		boolean isNative = context.getContextID() == target.getCoreID();
		
		if (isNative){
			Context oldContext = target.switchNativeContext(context);
			context.endStall();
			
			// give predictor feedback
			switch (type){
			case CoreMiss:
				target.getPredictor().feedback(context.getRunLength());
				break;
			case Eviction:
				target.getPredictor().feedback(context.getRunLength());
				break;
			case Overflow:
				target.getPredictor().overflow();
				break;
			case Underflow:
				target.getPredictor().underflow();
				break;
			default:
				throw new SimulatorException("weird message type during migration event");
			}
			
			// reset run length
			context.resetRunLength();
			
			// freeze run length
			context.freezeRunLength();
			
			if (null != oldContext){
				throw new SimulatorException("Migration home somehow evicted a context from the native core!");
			}
		} else {
			int max0 = 16;
			int max1 = 4;
			int cur0 = stack0depth;
			int cur1 = stack1depth;
			
			Context oldContext = target.switchGuestContext(context, max0, cur0, max1, cur1);
			context.endStall();
			
			if (0 != context.getRunLength()){
				context.freezeRunLength();
			} else {
				context.resetRunLength();
			}
			
			if (null != oldContext){
				// Cause a migration of oldContext home.
				CoreModel homeCore = target.getMachine().getNativeCore(oldContext);
				
				// reschedule somehow if the core is stalled
				// TODO: induce memory fence!
				// TODO: this is really wrong!!
				
				target.migrate(target, homeCore, new MigrationEvent(oldContext, target, homeCore, MessageType.Eviction, -1, -1));
			}
		}
	}
	
	public MessageType getType(){
		return type;
	}
	
	public Context getContext(){
		return context;
	}

	public CoreModel getSourceCore() {
		return src;
	}
	
	public CoreModel getTargetCore() {
		return target;
	}

	public int getMainDepth() {
		return stack0depth;
	}

	public int getAuxDepth() {
		return stack1depth;
	}
}
