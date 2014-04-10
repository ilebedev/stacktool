package stack.python;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

import stack.asm.Binary;
import stack.asm.Program;
import stack.excetpion.LoaderException;
import stack.excetpion.ParserException;
import stack.excetpion.SimulatorException;
import stack.simulator.Context;
import stack.simulator.Loader;
import stack.simulator.Simulator;
import stack.simulator.listeners.Breakpoint;
import stack.simulator.listeners.MemTrace;
import stack.simulator.listeners.MigrationTrace;
import stack.simulator.listeners.Trace;
import stack.simulator.machine.models.CoreModel;

public class sim {
	
	static Simulator S = new Simulator();
	
	public static void init() throws SimulatorException{
		S.getDES().clearQueue();
		S.initialize();
	}
	
	public static void configure(int numCores) throws SimulatorException{
		S = new Simulator(numCores);
		S.disableAllContexts();
		S.getDES().clearQueue();
		S.getDES().clearListeners();
		S.initialize();
	}
	
	public static void enableContext(int context) throws SimulatorException{
		S.enableContext(context);
	}
	
	public static void load(Program prog) throws LoaderException{
		Loader.load(S.getMachine(), prog);
	}
	
	public static void load(Binary bin) throws LoaderException{
		try {
			Loader.load(S.getMachine(), bin);
		} catch (ParserException e) {
			throw new LoaderException(e.getMessage());
		}
	}
	
	//public static void reset(int PC) throws SimulatorException{
	//	S.reset(PC);
	//}
	
	public static void reset(int contextID, int PC) throws SimulatorException{
		S.reset(contextID, PC);
	}
	
	public static void reset(int contextID, String label) throws SimulatorException{
		S.reset(contextID, label);
	}
	
	public static void step() throws SimulatorException{
		S.step(1);
	}
	
	public static void step(int steps) throws SimulatorException{
		S.step(steps);
	}
	
	public static void state(){
		System.out.println(S);
	}
	
	public static void state(int contextID){
		// ID @CoreID : PC (instruction) : [stack0, ...]
		Context context = S.getContext(contextID);
		CoreModel core = S.getMachine().getCore(context);
		String coreID = "-";
		if (null != core){
			coreID = String.valueOf(core.getCoreID());
		}
		int PC = context.getPC();
		
		String instruction;
		try{
			instruction = S.getMachine().fetch(PC).toString();
		} catch (ParserException pe){
			instruction = "--unrecognized instruction--";
		} catch (SimulatorException se){
			instruction = "--uninitialized memory--";
		}
		
		if (context.isStalled()){
			instruction = "--stall--";
		}
		
		if (!context.isEnabled()){
			instruction = "--disabled--";
		}
		
		String stack = "-";
		if (null != core){
			try {
				stack = "Stack 0: " + S.getMachine().getCore(context).getStack(0, context); // + "\tStack 1: " + S.getMachine().getCore(context).getStack(1, context);
			} catch (SimulatorException e) {
				stack = "--bad stack--";
			}
		}
		
		System.out.println(contextID + " @" + coreID + " : PC=" + PC + " (" + instruction + ") : " + stack);
	}
	
	public static void mem(int address){
		String data = "--";
		String data10 = "--";
		try{
			int value = S.getMachine().load(address);
			data = "0x"+String.format("%08x", value).toUpperCase();
			data10 = String.valueOf(value);
		} catch (Exception e){
			// nothing
		}
		
		System.out.println("M[0x"+String.format("%08x", address).toUpperCase()+"] = " + data + " = " + data10);
	}
	
	public static void mem(int address, int length){
		for (int i=0; i<length; i++){
			mem(address+i);
		}
	}
	
	public static void allMem(){
		LinkedList<Integer> addresses = new LinkedList<Integer>(S.getMachine().getAllMemAddresses());
		
		if (addresses.isEmpty()){
			System.out.println("-- (Memory is empty)");
		} else {
		Collections.sort(addresses);
			for (int a : addresses){
				mem(a);
			}
		}
	}
	
	public static void stack(int contextID) throws SimulatorException{
		Context context = S.getContext(contextID);
		System.out.println(S.getMachine().getCore(context).getStack(0, context));
	}
	
	public static void breakpoint(int PC){
		S.getDES().addListener(new Breakpoint(PC, S));
	}
	
	public static void breakpoint(String Label){
		S.getDES().addListener(new Breakpoint(Label, S));
	}
	
	
	static Hashtable<Integer, Trace> activeTraces = new Hashtable<Integer, Trace>();
	public static void startTrace(int contextID){
		if (!activeTraces.containsKey(contextID)){
			Trace trace = new Trace(contextID, S);
			activeTraces.put(contextID, trace);
			S.getDES().addListener(trace);
		} else {
			activeTraces.get(contextID).reset();
		}
	}
	public static String getTrace(int contextID){
		StringBuilder sb = new StringBuilder();
		
		if (!activeTraces.containsKey(contextID)){
			System.out.println("No active trace under contextID = " + contextID);
		} else {
			Trace trace = activeTraces.get(contextID);
			LinkedList<Integer> timestamps = new LinkedList<Integer>(trace.getTimestamps());
			Collections.sort(timestamps);
			for (int timestamp : timestamps){
				sb.append("@");
				sb.append(trace.getCore(timestamp));
				sb.append(" : PC=");
				sb.append(trace.getPC(timestamp));
				sb.append(" \t");
				sb.append(trace.getInstruction(timestamp).toString());
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	static Hashtable<Integer, MemTrace> activeMemTraces = new Hashtable<Integer, MemTrace>();
	public static void startMemTrace(int contextID){
		if (!activeMemTraces.containsKey(contextID)){
			MemTrace memTrace = new MemTrace(contextID, S);
			activeMemTraces.put(contextID, memTrace);
			S.getDES().addListener(memTrace);
		} else {
			activeMemTraces.get(contextID).reset();
		}
	}
	public static String getMemTrace(int contextID){
		StringBuilder sb = new StringBuilder();
		
		if (!activeMemTraces.containsKey(contextID)){
			System.out.println("No active memTrace under contextID = " + contextID);
		} else {
			MemTrace memTrace = activeMemTraces.get(contextID);
			LinkedList<Integer> timestamps = new LinkedList<Integer>(memTrace.getTimestamps());
			Collections.sort(timestamps);
			for (int timestamp : timestamps){
				sb.append("@");
				sb.append(memTrace.getSourceCore(timestamp));
				sb.append(" : ");
				sb.append(memTrace.getInstruction(timestamp).toString());
				sb.append(" to 0x");
				sb.append(String.format("%08x", memTrace.getAddress(timestamp)));
				sb.append(" with D = ");
				sb.append(memTrace.getData(timestamp));
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	static Hashtable<Integer, MigrationTrace> activeMigrationTraces = new Hashtable<Integer, MigrationTrace>();
	public static void startMigrationTrace(int contextID){
		if (!activeMigrationTraces.containsKey(contextID)){
			MigrationTrace migrationTrace = new MigrationTrace(contextID, S);
			activeMigrationTraces.put(contextID, migrationTrace);
			S.getDES().addListener(migrationTrace);
		} else {
			activeMigrationTraces.get(contextID).reset();
		}
	}
	public static String getMigrationTrace(int contextID){
		StringBuilder sb = new StringBuilder();
		
		if (!activeMigrationTraces.containsKey(contextID)){
			System.out.println("No active migration trace under contextID = " + contextID);
		} else {
			MigrationTrace migrationTrace = activeMigrationTraces.get(contextID);
			LinkedList<Integer> timestamps = new LinkedList<Integer>(migrationTrace.getTimestamps());
			Collections.sort(timestamps);
			
			for (int timestamp : timestamps){
				sb.append("@");
				sb.append(migrationTrace.getPC(timestamp));
				sb.append(" : ");
				
				switch (migrationTrace.getReason(timestamp)){
				case CoreMiss:
					sb.append("CMiss ");
					break;
				case Eviction:
					sb.append("Evict ");
					break;
				case Overflow:
					sb.append("Over  ");
					break;
				case Underflow:
					sb.append("Under ");
					break;
				default:
					sb.append("??? ");
				}
				
				sb.append(migrationTrace.getFromCore(timestamp));
				sb.append("->");
				sb.append(migrationTrace.getToCore(timestamp));
				sb.append(" with [");
				sb.append(migrationTrace.getMainStackSize(timestamp));
				sb.append(", ");
				sb.append(migrationTrace.getAuxStackSize(timestamp));
				sb.append("]\n");
			}
		}
		
		return sb.toString();
	}
}
