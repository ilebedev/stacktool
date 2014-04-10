package stack.simulator.listeners;

import java.util.Hashtable;
import java.util.Set;

import stack.isa.Instruction;
import stack.isa.LoadInstruction;
import stack.isa.StoreInstruction;
import stack.simulator.Context;
import stack.simulator.Listener;
import stack.simulator.Simulator;
import stack.simulator.des.Event;
import stack.simulator.machine.events.ContextEvent;

public class MemTrace implements Listener{
	Context context;
	int prevPC = 0;
	boolean startup = true;
	Hashtable<Integer, MemTraceEntry> trace;
	Simulator sim;
	
	public MemTrace(int contextID, Simulator sim){
		this.sim = sim;
		this.context = sim.getContext(contextID);
		this.trace = new Hashtable<Integer, MemTraceEntry>();
	}
	
	public void reset() {
		this.trace = new Hashtable<Integer, MemTraceEntry>();
		prevPC = 0;
		startup = true;
	}
	
	@Override
	public boolean condition(Event E) {
		if (E instanceof ContextEvent){
			ContextEvent event = (ContextEvent)E;
			
			if (event.getContext() == context){
				if (event.getContext().isEnabled()){
					if(!event.getContext().isStalled()){
						// Can't list duplicate entries for instructions re-executed due to migration!
						// Hack: Assume single-instruction loops do not exist, track previous PC
						if (startup || (event.getContext().getPC() != prevPC)){
							startup = false;
							prevPC = event.getContext().getPC();
							
							Instruction instruction;
							try {
								instruction = sim.getMachine().fetch(context.getPC());
								
								if (instruction instanceof LoadInstruction){
									return true;
								} else if (instruction instanceof StoreInstruction){
									return true;
								} 
							} catch (Exception e) {
								// nothing
							}
						}
					}
				}
			}
		} 
		
		return false;
	}

	@Override
	public void callback(Event E){
		assert E instanceof ContextEvent;
		ContextEvent event = (ContextEvent)E;
		
		Instruction instruction = null;
		try {
			instruction = sim.getMachine().fetch(context.getPC());
		} catch (Exception e) {
			// nothing
		}
		
		int address = 0;
		int data = 0;
		
		try{
		address = sim.getMachine().getStack(context, 0).get(0);
		data = sim.getMachine().getStack(context, 0).get(1);
		} catch (IndexOutOfBoundsException e){
			// nothing
		}
		
		int fromCore = -1;
		try{
			fromCore = sim.getMachine().getCore(context).getCoreID();
		} catch (Exception e){
			assert false;
			// nothing
		}
		
		trace.put(event.timestamp, new MemTraceEntry(instruction, address, data, fromCore));
	}

	public Set<Integer> getTimestamps() {
		return trace.keySet();
	}
	
	public Instruction getInstruction(int timestamp) {
		return trace.get(timestamp).inst;
	}
	
	public int getAddress(int timestamp) {
		return trace.get(timestamp).address;
	}
	
	public int getData(int timestamp) {
		return trace.get(timestamp).data;
	}
	
	public int getSourceCore(int timestamp) {
		return trace.get(timestamp).fromCore;
	}
	
	private class MemTraceEntry{
		public Instruction inst;
		public int address;
		public int data;
		public int fromCore;
		
		public MemTraceEntry(Instruction inst, int address, int data, int fromCore){
			this.inst = inst;
			this.address = address;
			this.data = data;
			this.fromCore = fromCore;
		}
	}
}
