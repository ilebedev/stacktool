package stack.simulator.des;

import java.util.LinkedList;

public class EventBucket extends LinkedList<Event>{
	private static final long serialVersionUID = 1L;
	public int timestep;
	
	public EventBucket(int timestep){
		super();
		this.timestep = timestep;
	}
}
