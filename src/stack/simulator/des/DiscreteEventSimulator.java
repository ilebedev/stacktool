package stack.simulator.des;

import java.util.HashSet;

import stack.excetpion.SimulatorException;
import stack.simulator.Listener;

public class DiscreteEventSimulator {
	private EventQueue queue;
	private int currentTimestep;
	
	boolean isActive = false;
	
	private HashSet<Listener> listeners;
	
	public DiscreteEventSimulator(){
		queue = new EventQueue();
		listeners = new HashSet<Listener>();
	}
	
	public void schedule(Event event, int timeBeforeEvent) throws SimulatorException {
		event.timestamp = currentTimestep + timeBeforeEvent;
		addEvent(event);
	}
	
	private void addEvent(Event event) throws SimulatorException{
		if (event.timestamp < currentTimestep){
			throw new SimulatorException("Attempted to schedule an event in the past");
		}
		
		queue.addEvent(event);
	}
	
	public int getTimestamp(){
		return currentTimestep;
	}
	
	public boolean simulate(int events) throws SimulatorException{
		boolean progress = false;
		isActive = true;
		
		while (!queue.isEmpty() && events > 0 && isActive){
			Event event = queue.nextEvent();
			currentTimestep = event.timestamp;
			
			for (Listener L : listeners){
				if (L.condition(event)){
					L.callback(event);
				}
			}
			
			event.callback(this);
			events -= 1;
			
			progress = true;
		}
		
		return progress;
	}
	
	public void halt() {
		isActive = false;
	}
	
	public int getNumQueuedEvents(){
		return queue.size();
	}

	public void clearQueue() {
		currentTimestep = 0;
		queue.clear();
	}
	
	public void addListener(Listener L){
		listeners.add(L);
	}
	
	public void clearListeners(){
		listeners.clear();
	}
}
