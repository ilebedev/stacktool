package stack.simulator.des;

import stack.excetpion.SimulatorException;

public abstract class Event {
	public int timestamp;
	public abstract void callback(DiscreteEventSimulator DES) throws SimulatorException;
}
