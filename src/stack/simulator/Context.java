package stack.simulator;

public class Context {
	public Simulator sim;
	private int PC, nPC;
	private int id;
	
	private int ackCounter = 0;
	private int runLength = 0;
	private boolean countingRunLength = false;
	
	private boolean stall = false;
	private boolean enabled = true;
	
	
	// a context is a context id and a pc+nPC pair.
	
	public Context(Simulator sim, int contextID){
		id = contextID;
		
		this.sim = sim;
		
		PC = 0;
		nPC = PC+1;
		
		ackCounter = 0;
	}
	
	public int getContextID() {
		return id;
	}
	
	public int getPC() {
		return PC;
	}
	
	public void reset(int resetPC){
		PC = resetPC;
		nPC = PC+1;
	}

	public void cycle(){
		PC = nPC;
		nPC = PC+1;
	}

	public void setNPC(int nextPC) {
		nPC = nextPC;
	}
	
	// Enable/Disable context
	public boolean isEnabled(){
		return enabled;
	}
	
	public void disable(){
		enabled = false;
	}
	
	public void enable(){
		enabled = true;
	}
	
	// Control stalls
	public boolean isStalled(){
		return stall;
	}
	
	public void stall(){
		stall = true;
	}
	
	public void endStall(){
		stall = false;
	}
	
	// Acknowledgement counter
	public void promiseAck(){
		ackCounter++;
	}
	
	public void registerAck(){
		ackCounter--;
		
		assert ackCounter >= 0 : "WHAT THE??";
	}
	
	public boolean noOutstandingAcks(){
		return ackCounter == 0;
	}
	
	// Control run length
	public void incrementRunLength() {
		if (countingRunLength){
			runLength++;
		}
	}
	
	public void resetRunLength(){
		countingRunLength = true;
		runLength = 0;
	}
	
	public void freezeRunLength(){
		countingRunLength = false;
	}
	
	public int getRunLength(){
		return runLength;
	}
}
