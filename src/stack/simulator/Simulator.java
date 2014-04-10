package stack.simulator;

import java.util.Collection;
import java.util.Hashtable;

import stack.excetpion.SimulatorException;
import stack.simulator.des.DiscreteEventSimulator;
import stack.simulator.machine.Machine;
import stack.simulator.machine.events.ContextEvent;

public class Simulator{
	// A simulator is a DES engine
	// and a set of contexts
	// and a machine that runs those contexts
	
	protected DiscreteEventSimulator DES;
	protected Machine machine;
	protected Hashtable<Integer, Context> contexts;
	
	private void constructor_helper(){
		DES = new DiscreteEventSimulator();
		machine = new Machine(this);
		contexts = new Hashtable<Integer, Context>();
		Loader.reset();
	}
	
	public Simulator(){
		constructor_helper();
		
		// Add a core0, and its native context.
		try {
			machine.addCore(0, addContext(0));
		} catch (SimulatorException e) {
			e.printStackTrace();
		}
	}
	
	public Simulator(int numCores){
		constructor_helper();
		
		// Add a core0, and its native context.
		try {
			for (int i=0; i<numCores; i++){
				machine.addCore(i, addContext(i));
			}
		} catch (SimulatorException e) {
			e.printStackTrace();
		}
	}
	
	public Context addContext(int contextID) throws SimulatorException{
		if (contexts.containsKey(contextID)){
			throw new SimulatorException("Attempted to add a new context with duplicate ID");
		}
		Context newContext = new Context(this, contextID);
		contexts.put(contextID, newContext);
		
		return newContext;
	}
	
	public void reset(int PC) {
		for (Context c : contexts.values()){
			c.reset(PC);
		}
	}
	
	public void reset(int contextID, int PC) {
		getContext(contextID).reset(PC);
	}
	
	public void reset(int contextID, String label) throws SimulatorException {
		int PC = machine.instructionMemory.getLabel(label);
		getContext(contextID).reset(PC);
	}
	
	public void initialize() throws SimulatorException{
		for (Context c : contexts.values()){
			if (c.isEnabled()){
				DES.schedule(new ContextEvent(c), 0);
			}
		}
	}
	
	public void step() throws SimulatorException{
		if (!DES.simulate(1)){
			throw new SimulatorException("No forward progress made by DES");
		}
	}
	
	public void step(int numEvents) throws SimulatorException{
		if (!DES.simulate(numEvents)){
			throw new SimulatorException("No forward progress made by DES");
		}
	}
	
	public Machine getMachine(){
		return machine;
	}
	
	public Context getContext(int ID){
		return contexts.get(ID);
	}
	
	public Collection<Context> contextSet(){
		return contexts.values();
	}
	
	public DiscreteEventSimulator getDES(){
		return DES;
	}
	
	public void halt() {
		DES.halt();
	}
	
	@Override
	public String toString(){
		String str = "Simulator with " + contexts.size() + " contexts. Time=" + DES.getTimestamp() + " (" + DES.getNumQueuedEvents() + " events queued).\n";
		
		for (Context c : contexts.values()){
			str += "Context " + c.getContextID() + " : PC=" + c.getPC() + " : at core" + machine.getCore(c).getCoreID() + "\n";
		}
		
		return str;
	}

	public void disableAllContexts() {
		for (Context context : contexts.values()){
			context.disable();
		}
	}

	public void enableContext(int contextID) throws SimulatorException {
		Context context = contexts.get(contextID);
		if (null == context){
			throw new SimulatorException("Nonexistant context id");
		}
		
		context.enable();
	}
}
