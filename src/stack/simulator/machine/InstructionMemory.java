package stack.simulator.machine;

import java.util.Hashtable;

import stack.excetpion.SimulatorException;
import stack.isa.Instruction;

public class InstructionMemory{
	protected Hashtable<Integer, Instruction> memory;
	public Hashtable<String, Integer> labels;
	
	public InstructionMemory(){
		memory = new Hashtable<Integer, Instruction>();
		labels = new Hashtable<String, Integer>();
	}
	
	public void addLabel(String label, int address){
		labels.put(label, address);
	}
	
	public int getLabel(String label) throws SimulatorException{
		if (labels.containsKey(label)){
			return labels.get(label);
		} else {
			throw new SimulatorException("Attempted to look up an undefined label");
		}
	}
	
	public Instruction load (int address) throws SimulatorException{
		if (!memory.containsKey(address)){
			throw new SimulatorException("Attempted to load an instruction memory location that has not been written");
		} else {
			return memory.get(address);
		}
	}
	
	public void store (int address, Instruction data){
		memory.put(address, data);
	}
	
	public int size(){
		return memory.size();
	}
}
