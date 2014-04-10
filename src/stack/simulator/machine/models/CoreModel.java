package stack.simulator.machine.models;

import java.util.Hashtable;

import stack.excetpion.SimulatorException;
import stack.simulator.Context;
import stack.simulator.machine.Machine;
import stack.simulator.machine.events.MigrationEvent;
import stack.simulator.machine.events.RAEvent;

public class CoreModel {
	protected Machine machine;
	private Context nativeContext;
	private Context guestContext;
	private int coreID;
	
	private Hashtable<Integer, Context> memoryLinks;
	
	private PredictorModel predictor;
	
	boolean tallyRunlength = false;
	int runLength = 0;
	
	// A core model maintains 2 execution engines, and up to 2 contexts
	
	public CoreModel(Machine machine, int coreID){
		this.machine = machine;
		
		nativeContext = null;
		guestContext = null;
		
		memoryLinks = new Hashtable<Integer, Context>();
		
		predictor = new PredictorModel();
		
		this.coreID = coreID;
	}
	
	public StackModel getStack(int stackID, Context context) throws SimulatorException {
		return machine.getStack(context, stackID);
	}
	
	public Context switchNativeContext(Context newContext) throws SimulatorException{
		Context oldContext = nativeContext;
		nativeContext = newContext;
		if (null != newContext){
			machine.currentContexts.put(newContext, this);
			
			getStack(0, nativeContext).setUnlimitedDepth();
			getStack(1, nativeContext).setUnlimitedDepth();
		}
		if (null != oldContext){
			machine.currentContexts.removeByKey(oldContext);
			oldContext.stall();
		}
		
		return oldContext;
	}
	
	public Context switchGuestContext(Context newContext, int maxDepth0, int curDepth0, int maxDepth1, int curDepth1) throws SimulatorException{
		Context oldContext = guestContext;
		guestContext = newContext;
		if (null != newContext){
			machine.currentContexts.put(newContext, this);
			
			getStack(0, guestContext).setAvailableDepth(curDepth0, maxDepth0);
			getStack(1, guestContext).setAvailableDepth(curDepth1, maxDepth1);
		}
		if (null != oldContext){
			machine.currentContexts.removeByKey(oldContext);
			oldContext.stall();
		}
		
		return oldContext;
	}
	
	public Context getNative(){
		return nativeContext;
	}
	
	public Context getGuest(){
		return guestContext;
	}
	
	public int getCoreID() {
		return coreID;
	}
	
	public int load(int address) throws SimulatorException{
		return machine.load(address);
	}
	
	public void store(int address, int data){
		machine.store(address, data);
	}
	
	public void link(int address, Context context) {
		memoryLinks.put(address, context);
	}
	
	public boolean isLinked(int addres, Context context) {
		return memoryLinks.get(addres) == context;
	}
	
	public Machine getMachine() {
		return machine;
	}
	
	public PredictorModel getPredictor(){
		return predictor;
	}

	public void sendRA(CoreModel core, CoreModel targetCore, RAEvent raEvent) throws SimulatorException {
		machine.sendOnNetwork(core, targetCore, raEvent);
	}

	public void migrate(CoreModel core, CoreModel targetCore, MigrationEvent migrationEvent) throws SimulatorException {
		/*
		Context context = migrationEvent.getContext();
		switch (migrationEvent.getType()){
		case CoreMiss:
			System.out.println("M (CoreMiss) (" + context.getContextID() + ") ->" + targetCore.getCoreID());
			break;
		case Eviction:
			System.out.println("M (Eviction) (" + context.getContextID() + ") ->" + targetCore.getCoreID());
			break;
		case Overflow:
			System.out.println("M (Overflow) (" + context.getContextID() + ") ->" + targetCore.getCoreID());
			break;
		case Underflow:
			System.out.println("M (Underflow) (" + context.getContextID() + ") ->" + targetCore.getCoreID());
			break;
		default:
			throw new SimulatorException("weird message type during migration event");
		}
		*/
		machine.sendOnNetwork(core, targetCore, migrationEvent);
	}
}
