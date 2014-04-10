package stack.simulator.machine.models;

public class CATModel {
	int numCores = 0;
	
	public CATModel(int numCores){
		this.numCores = numCores;
	}
	
	public int getNativeCoreID(int address){
		// The address space is segmented into chunks such that each core gets the same amount of native memory.
		int numIDBits = Integer.numberOfLeadingZeros(numCores-1);
		int id;
		if (numIDBits == 32){
			id = 0;
		} else {
			id = address >> numIDBits;
		}
		return id;
	}
	
	
}
