package stack.isa.em2;

import stack.excetpion.SimulatorException;
import stack.isa.ThreeImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.events.MessageType;
import stack.simulator.machine.events.MigrationEvent;
import stack.simulator.machine.models.CoreModel;

public abstract class EMInstruction extends ThreeImmediateInstruction{

	public EMInstruction(int stack0, int stack1, int offset, String comment) {
		super(stack0, stack1, offset, comment);
	}
	
	public int getStack0Depth(){
		return getImmediateHigh();
	}
	
	public int getStack1Depth(){
		return getImmediateLow();
	}
	
	protected boolean isCoreHit(int address, CoreModel core){
		CoreModel targetCore = core.getMachine().cores.get(core.getMachine().getCAT().getNativeCoreID(address));
		
		assert targetCore != null;
		
		return core == targetCore;
	}
	
	protected void migrate(int address, Context context, CoreModel curCore, int stack0size, int stack1size) throws SimulatorException{
		CoreModel targetCore = curCore.getMachine().cores.get(curCore.getMachine().getCAT().getNativeCoreID(address));
		
		assert targetCore != null;
		
		// Evict self
		// TODO: this is temporary, and should be done by the migration serialization event.
		if (curCore.getGuest() == context){
			curCore.switchGuestContext(null, 0, 0, 0, 0);
		} else if (curCore.getNative() == context){
			curCore.switchNativeContext(null);
		}
		
		// Schedule a migration
		curCore.migrate(curCore, targetCore, new MigrationEvent(context, curCore, targetCore, MessageType.CoreMiss, stack0size, stack1size));
	}
}
