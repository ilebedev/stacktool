package stack.simulator.listeners;

import java.util.Hashtable;
import java.util.Set;

import stack.excetpion.ParserException;
import stack.excetpion.SimulatorException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.Listener;
import stack.simulator.Simulator;
import stack.simulator.des.Event;
import stack.simulator.machine.events.ContextEvent;

public class Trace implements Listener{
	Context context;
	int prevPC = 0;
	boolean startup = true;
	Hashtable<Integer, TraceEntry> trace;
	Simulator sim;
	
	public Trace(int contextID, Simulator sim){
		this.sim = sim;
		this.context = sim.getContext(contextID);
		this.trace = new Hashtable<Integer, TraceEntry>();
	}
	
	public void reset() {
		this.trace = new Hashtable<Integer, TraceEntry>();
		prevPC = 0;
		startup = true;
	}
	
	@Override
	public boolean condition(Event E) {
		if (E instanceof ContextEvent){
			ContextEvent event = (ContextEvent)E;
			
			if (event.getContext() == context){
				if (startup || (prevPC != context.getPC())){
					if (context.isEnabled()){
						if (!context.isStalled()){
							prevPC = context.getPC();
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public void callback(Event E) {
		try {
			TraceEntry entry = new TraceEntry(sim.getMachine().fetch(context.getPC()), context.getPC(), sim.getMachine().getCore(context).getCoreID());
			trace.put(E.timestamp, entry);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (SimulatorException e) {
			e.printStackTrace();
		}
	}

	public Set<Integer> getTimestamps() {
		return trace.keySet();
	}
	
	public Instruction getInstruction(int timestamp) {
		return trace.get(timestamp).inst;
	}
	
	public int getPC(int timestamp) {
		return trace.get(timestamp).pc;
	}
	
	public int getCore(int timestamp) {
		return trace.get(timestamp).core;
	}
	
	private class TraceEntry{
		public Instruction inst;
		public int pc;
		public int core;
		
		public TraceEntry(Instruction inst, int pc, int core){
			this.inst = inst;
			this.pc = pc;
			this.core = core;
		}
	}
}
