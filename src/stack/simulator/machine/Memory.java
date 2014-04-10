package stack.simulator.machine;

import java.util.Hashtable;

import stack.excetpion.SimulatorException;

public class Memory {
	protected Hashtable<Integer, Integer> memory;
	
	public Memory(){
		memory = new Hashtable<Integer, Integer>();
	}
	
	public int load (int address) throws SimulatorException{
		if (!memory.containsKey(address)){
			throw new SimulatorException("Attempted to load a memory location that has not been written");
		} else {
			return memory.get(address);
		}
	}
	
	public void store (int address, int data){
		memory.put(address, data);
	}
	
	public int size(){
		return memory.size();
	}
}
