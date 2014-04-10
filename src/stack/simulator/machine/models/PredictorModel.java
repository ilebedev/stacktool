package stack.simulator.machine.models;

public class PredictorModel {
	// Table
	
	public boolean isMigration(int address){
		return true;
	}
	
	public int getStack0Size(int address){
		return 4;
	}
	
	public int getStack1Size(int address){
		return 2;
	}

	public void feedback(int runLength) {
		// TODO Auto-generated method stub
	}

	public void overflow() {
		// TODO Auto-generated method stub
		
	}

	public void underflow() {
		// TODO Auto-generated method stub
		
	}
	
	// updateUnderflow(int address)
	
	// updateOverflow(int address)
	
	// update(int address, int runlength)
	
	
}
