package stack.simulator.des;

import java.util.TreeMap;

import stack.excetpion.SimulatorException;

public class EventQueue {
	TreeMap<Integer, EventBucket> eventBuckets;
	
	public EventQueue(){
		eventBuckets = new TreeMap<Integer, EventBucket>();
	}
	
	public void addEvent(Event event){
		if (!eventBuckets.containsKey(event.timestamp)){
			eventBuckets.put(event.timestamp, new EventBucket(event.timestamp));
		}
		
		eventBuckets.get(event.timestamp).addLast(event);
	}
	
	public boolean isEmpty(){
		return eventBuckets.isEmpty();
	}
	
	public Event nextEvent() throws SimulatorException{
		if (isEmpty()){
			throw new SimulatorException("No events in event queue");
		}
		
		EventBucket bucket = eventBuckets.get(eventBuckets.firstKey());
		Event event = bucket.removeFirst();
		
		if (bucket.isEmpty()){
			eventBuckets.remove(bucket.timestep);
		}
		
		return event;
	}
	
	public int size(){
		int s = 0;
		
		for (EventBucket b : eventBuckets.values()){
			s += b.size();
		}
		
		return s;
	}

	public void clear() {
		eventBuckets.clear();
	}
}
