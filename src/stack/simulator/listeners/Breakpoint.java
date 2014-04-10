package stack.simulator.listeners;

import stack.excetpion.SimulatorException;
import stack.simulator.Listener;
import stack.simulator.Simulator;
import stack.simulator.des.Event;
import stack.simulator.machine.events.ContextEvent;

public class Breakpoint implements Listener{
	int breakPC;
	String label;
	Simulator sim;
	
	public Breakpoint(int PC, Simulator sim){
		this.sim = sim;
		this.breakPC = PC;
	}
	
	public Breakpoint(String label, Simulator sim){
		this.sim = sim;
		try {
			this.breakPC = sim.getMachine().instructionMemory.getLabel(label);
		} catch (SimulatorException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean condition(Event E) {
		if (E instanceof ContextEvent){
			ContextEvent event = (ContextEvent)E;
			if (event.getContext().getPC() == this.breakPC){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void callback(Event E) {
		assert E instanceof ContextEvent;
		ContextEvent event = (ContextEvent)E;
		
		System.out.println("Breakpoint! Context # " + event.getContext().getContextID() + " hit breakpoint at PC = " + event.getContext().getPC());
		sim.halt();
	}

}
