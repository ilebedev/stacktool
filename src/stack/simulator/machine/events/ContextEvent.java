package stack.simulator.machine.events;

import stack.excetpion.OverflowException;
import stack.excetpion.ParserException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.Instruction;
import stack.simulator.Context;
import stack.simulator.des.DiscreteEventSimulator;
import stack.simulator.des.Event;
import stack.simulator.machine.Machine;
import stack.simulator.machine.models.CoreModel;

public class ContextEvent extends Event {
	Context context;
	
	public ContextEvent(Context context){
		this.context = context;
	}
	
	@Override
	public void callback(DiscreteEventSimulator DES) throws SimulatorException {
		// Currently, the presence of a guest thread *always* preempts the native thread
		
		Machine machine = context.sim.getMachine();
		CoreModel coreModel = machine.getCore(context);
		
		// Quit trying to simulate if disabled.
		if (!context.isEnabled()){
			return;	// without scheduling the next contextEvent
		}
		
		// Wait if stalled
		if (context.isStalled()){
			//System.out.println("s (" + context.getContextID() + ") @" + coreModel.getCoreID() + ", PC=" + context.getPC());
			DES.schedule(new ContextEvent(context), 1);
			return;
		}
		
		if (null == coreModel){
			throw new SimulatorException("Context has no core associated with it");
		}
		
		boolean isNative = (context == coreModel.getNative());
		assert (isNative || (coreModel.getGuest()==context));
		
		// Execute the instruction!
		// the context has a core associated with it, is enabled, and is not stalled
		
		Instruction inst;
		try {
			inst = machine.fetch(context.getPC());
		} catch (ParserException e) {
			throw new SimulatorException("Failed to parse an instruction on fetch at PC=" + coreModel.getGuest().getPC());
		}
			
		try{
			System.out.print("i (" + context.getContextID() + ") @" + coreModel.getCoreID() + ", PC=" + context.getPC() + " : ");
			System.out.println(inst.toString());
			System.out.println(coreModel.getStack(0, context));
			System.out.println();
			
			inst.execute(context, coreModel);
		} catch (OverflowException oe){
			if (isNative){
				throw new SimulatorException("Something bad happened - the native core shouldn't overflow");
			} else {
				// Evict, reexecute on native
				Context evicted = coreModel.switchGuestContext(null, 0, 0, 0, 0);
				CoreModel homeCore = coreModel.getMachine().getNativeCore(evicted);

				coreModel.migrate(coreModel, homeCore, new MigrationEvent(evicted, coreModel, homeCore, MessageType.Overflow, -1, -1));
			}
			
		} catch (UnderflowException ue){
			if (isNative){
				throw new SimulatorException("Stack underflow on native core! : context # " + coreModel.getNative().getContextID());
			} else {
				// Evict, reexecute on native
				Context evicted = coreModel.switchGuestContext(null, 0, 0, 0, 0);
				CoreModel homeCore = coreModel.getMachine().getNativeCore(evicted);
	
				coreModel.migrate(coreModel, homeCore, new MigrationEvent(evicted, coreModel, homeCore, MessageType.Underflow, -1, -1));
			}
		}
		
		// schedule the next event.
		DES.schedule(new ContextEvent(context), 1);
	}

	public Context getContext() {
		return context;
	}
}
