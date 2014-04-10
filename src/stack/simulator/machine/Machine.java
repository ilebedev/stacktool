package stack.simulator.machine;

import java.util.Hashtable;
import java.util.Set;

import stack.excetpion.ParserException;
import stack.excetpion.SimulatorException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.Simulator;
import stack.simulator.des.Event;
import stack.simulator.machine.models.CATModel;
import stack.simulator.machine.models.CoreModel;
import stack.simulator.machine.models.StackModel;
import stack.util.BidirectionalMap;

public class Machine {
	// A machine is something that has memory and one or more cores
	// A machine knows how to do the following:
	// enable/disable a core given ID
	// (host) read/write memory
	// (host) read/write instruction memory
	// service all of its internal components
	
	// a machine also knows where all the contexts reside
	
	public Hashtable<Integer, CoreModel> cores;
	public BidirectionalMap<Context, CoreModel> nativeContexts;
	public BidirectionalMap<Context, CoreModel> currentContexts;
	
	private Hashtable<Context, Hashtable<Integer, StackModel>> stacks;
	
	public InstructionMemory instructionMemory;
	public Memory dataMemory;
	
	private CATModel cat;
	
	private Simulator sim;
	
	private void constructorHelper(){
		cores = new Hashtable<Integer, CoreModel>();
		nativeContexts = new BidirectionalMap<Context, CoreModel>();
		currentContexts = new BidirectionalMap<Context, CoreModel>();
		instructionMemory = new InstructionMemory();
		stacks = new Hashtable<Context, Hashtable<Integer, StackModel>>();
		dataMemory = new Memory();
	}
	
	public Machine (Simulator sim){
		this.sim = sim;
		constructorHelper();
	}
	
	public CoreModel addCore(int id, Context context) throws SimulatorException{
		if (cores.containsKey(id)){
			throw new SimulatorException("Attempted to add a core with duplicate ID");
		}
		
		if (id != context.getContextID()){
			throw new SimulatorException("Attempted to add a core with a context of differing ID");
		}
		
		CoreModel core = new CoreModel(this, id);
		cores.put(id, core);
		
		nativeContexts.put(context, core);
		currentContexts.put(context, core);
		
		core.switchNativeContext(context);
		
		cat = new CATModel(cores.size());
		
		return core;
	}
	
	public void store(int address, int data) {
		dataMemory.store(address, data);
		
	}

	public int load(int address) throws SimulatorException {
		return dataMemory.load(address);
	}
	
	public Set<Integer> getAllMemAddresses() {
		return dataMemory.memory.keySet();
	}
	
	public Instruction fetch(int address) throws ParserException, SimulatorException{
		return instructionMemory.load(address);
	}
	
	public boolean isGuest(Context context){
		return nativeContexts.get(context) == currentContexts.get(context);
	}
	
	public CoreModel getNativeCore(Context context){
		return nativeContexts.get(context);
	}
	
	public CoreModel getCore(Context context){
		return currentContexts.get(context);
	}
	
	public StackModel getStack(Context context, int stackID){
		if (!stacks.containsKey(context)){
			stacks.put(context, new Hashtable<Integer, StackModel>());
		}
		
		Hashtable<Integer, StackModel> contextStacks = stacks.get(context);
		
		if (!contextStacks.containsKey(stackID)){
			contextStacks.put(stackID, new StackModel());
		}
		
		return contextStacks.get(stackID);
	}
	
	public CATModel getCAT(){
		return cat;
	}
	
	@Override
	public String toString(){
		return "Machine. TODO: print something useful!";
	}
	
	public Simulator getSim(){
		return sim;
	}

	public void sendOnNetwork(CoreModel from, CoreModel to, Event event) throws SimulatorException {
		// in absence of a real network model, simply schedule the event to occur on the next cycle
		sim.getDES().schedule(event, 1);
	}
}
