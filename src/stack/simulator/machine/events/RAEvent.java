package stack.simulator.machine.events;

import stack.excetpion.SimulatorException;
import stack.simulator.Context;
import stack.simulator.des.DiscreteEventSimulator;
import stack.simulator.des.Event;
import stack.simulator.machine.models.CoreModel;

public class RAEvent extends Event {
	Context context;
	CoreModel src, target;
	MessageType type;
	int address, data;
	
	public RAEvent(Context context, CoreModel src, CoreModel target, MessageType type, int address, int data) {
		this.context = context;
		this.src = src;
		this.target = target;
		this.type = type;
		this.address = address;
		this.data = data;
	}

	@Override
	public void callback(DiscreteEventSimulator DES) throws SimulatorException {
		RAEvent response;
		
		switch (type){
		case RABlockingStore:
			target.store(address, data);
			// send ack
			response = new  RAEvent(context, target, src, MessageType.RABlockingStoreAck, 0, 0);
			target.sendRA(target, src, response);
			break;
		case RAStore:
			target.store(address, data);
			// send ack
			response = new  RAEvent(context, target, src, MessageType.RAAck, 0, 0);
			target.sendRA(target, src, response);
			break;
		case RAStoreNoAck:
			target.store(address, data);
			break;
		case RABlockingStoreAck:
			context.endStall();
			context.registerAck();
			break;
		case RAAck:
			context.registerAck();
			break;
		case RALoad:
			data = target.load(address);
			response = new  RAEvent(context, target, src, MessageType.RALoadResponse, address, data);
			target.sendRA(target, src, response);
			break;
		case RALoadResponse:
			src.getStack(0, context).add(0, data);
			context.endStall();
			context.cycle();
			break;
		default:
			throw new SimulatorException("unknown RA message!");
		}
	}
	
	public MessageType getType(){
		return type;
	}
}
