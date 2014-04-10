package stack.simulator.listeners;

import java.util.Hashtable;
import java.util.Set;

import stack.simulator.Context;
import stack.simulator.Listener;
import stack.simulator.Simulator;
import stack.simulator.des.Event;
import stack.simulator.machine.events.MessageType;
import stack.simulator.machine.events.MigrationEvent;

public class MigrationTrace implements Listener{
	Context context;
	Hashtable<Integer, MigrationTraceEntry> trace;
	Simulator sim;
	
	public MigrationTrace(int contextID, Simulator sim){
		this.sim = sim;
		this.context = sim.getContext(contextID);
		this.trace = new Hashtable<Integer, MigrationTraceEntry>();
	}
	
	public void reset() {
		this.trace = new Hashtable<Integer, MigrationTraceEntry>();
	}
	
	@Override
	public boolean condition(Event event) {
		// whenever the context's PC advances.
		if (event instanceof MigrationEvent){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void callback(Event E) {
		assert E instanceof MigrationEvent;
		MigrationEvent event = (MigrationEvent)E;
		
		trace.put(event.timestamp, new MigrationTraceEntry(
				event.getContext().getPC(),
				event.getSourceCore().getCoreID(),
				event.getTargetCore().getCoreID(),
				event.getType(),
				event.getMainDepth(),
				event.getAuxDepth()));
	}

	public Set<Integer> getTimestamps() {
		return trace.keySet();
	}
	
	public int getPC(int timestamp) {
		return trace.get(timestamp).PC;
	}
	
	public int getFromCore(int timestamp) {
		return trace.get(timestamp).fromCore;
	}
	
	public int getToCore(int timestamp) {
		return trace.get(timestamp).toCore;
	}
	
	public MessageType getReason(int timestamp) {
		return trace.get(timestamp).reason;
	}
	
	public int getMainStackSize(int timestamp) {
		return trace.get(timestamp).stack0depth;
	}
	
	public int getAuxStackSize(int timestamp) {
		return trace.get(timestamp).stack1depth;
	}
	
	private class MigrationTraceEntry{
		public int PC;
		public int fromCore;
		public int toCore;
		public MessageType reason;
		public int stack0depth;
		public int stack1depth;
		
		public MigrationTraceEntry(int PC, int fromCore, int toCore, MessageType reason, int stack0depth, int stack1depth){
			this.PC = PC;
			this.fromCore = fromCore;
			this.toCore = toCore;
			this.reason = reason;
			this.stack0depth = stack0depth;
			this.stack1depth = stack1depth;
		}
	}
}
