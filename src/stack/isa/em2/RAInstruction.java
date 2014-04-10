package stack.isa.em2;

import stack.excetpion.SimulatorException;
import stack.isa.OneImmediateInstruction;
import stack.simulator.Context;
import stack.simulator.machine.events.MessageType;
import stack.simulator.machine.events.RAEvent;
import stack.simulator.machine.models.CoreModel;

public abstract class RAInstruction extends OneImmediateInstruction{

	public RAInstruction(int offset, String comment) {
		super(offset, comment);
	}
	
	protected boolean isCoreHit(int address, CoreModel core){
		CoreModel targetCore = core.getMachine().cores.get(core.getMachine().getCAT().getNativeCoreID(address));
		
		assert targetCore != null;
		
		return core == targetCore;
	}
	
	protected void sendRALoad(Context context, CoreModel core, int address) throws SimulatorException{
		CoreModel targetCore = core.getMachine().cores.get(core.getMachine().getCAT().getNativeCoreID(address));
		assert null != targetCore;
		core.sendRA(core, targetCore, new RAEvent(context, core, targetCore, MessageType.RALoad, address, 0));
	}
	
	protected void sendRAStore(Context context, CoreModel core, int address, int data) throws SimulatorException{
		CoreModel targetCore = core.getMachine().cores.get(core.getMachine().getCAT().getNativeCoreID(address));
		assert null != targetCore;
		core.sendRA(core, targetCore, new RAEvent(context, core, targetCore, MessageType.RABlockingStore, address, data));
	}
	
	protected void sendRAStoreNoAck(Context context, CoreModel core, int address, int data) throws SimulatorException{
		CoreModel targetCore = core.getMachine().cores.get(core.getMachine().getCAT().getNativeCoreID(address));
		assert null != targetCore;
		core.sendRA(core, targetCore, new RAEvent(context, core, targetCore, MessageType.RAStoreNoAck, address, data));
	}
}
