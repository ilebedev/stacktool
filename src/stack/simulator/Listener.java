package stack.simulator;

import stack.simulator.des.Event;

public interface Listener {
	public boolean condition(Event event);
	public void callback(Event E);
}
