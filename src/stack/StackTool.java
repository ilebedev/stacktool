package stack;

import java.io.InputStream;
import java.util.Scanner;

import org.python.util.InteractiveConsole;

/*
import stack.lua.functions.asm.BindingAssemble;
import stack.lua.functions.asm.BindingBinInfo;
import stack.lua.functions.asm.BindingParseASM;
import stack.lua.functions.asm.BindingProgInfo;
import stack.lua.functions.io.BindingRead;
import stack.lua.functions.io.BindingWrite;
import stack.lua.functions.sim.BindingSimBreak;
import stack.lua.functions.sim.BindingSimClearTrace;
import stack.lua.functions.sim.BindingSimIMemProbe;
import stack.lua.functions.sim.BindingSimInit;
import stack.lua.functions.sim.BindingSimLoad;
import stack.lua.functions.sim.BindingSimMemProbe;
import stack.lua.functions.sim.BindingSimRun;
import stack.lua.functions.sim.BindingSimReset;
import stack.lua.functions.sim.BindingSimStackProbe;
import stack.lua.functions.sim.BindingSimState;
import stack.lua.functions.sim.BindingSimStep;
import stack.lua.functions.sim.BindingSimTrace;
import stack.lua.functions.sir.BindingParseSIR;
import stack.lua.functions.sir.BindingSIRSelect;
import stack.lua.functions.util.BindingQuit;
import stack.lua.functions.util.BindingTest;
*/


public class StackTool {
	InteractiveConsole console;
	boolean interperterLoop;
	
	static final String lsep = System.getProperty("line.separator");
	
	public StackTool() {
		initConsole();
		
		runConsole();
	}

	public StackTool(InputStream reader) {
		InputStream in = reader;
		
		initConsole();
		
		runConsole(in);
	}

	private void initConsole() {
		System.out.println("--- Python Script Engine (Jython) ---");
		
		console = new InteractiveConsole();
		
		// Initialize bindings
		setBindings();
	}
	
	private void runConsole() {
		interperterLoop = true;
		Scanner in = new Scanner(System.in);  
		
		while (interperterLoop){
			System.out.print("> ");
			console.push(in.nextLine());
		}
	}
	
	private void runConsole(InputStream stream) {
		console.execfile(stream);
	}
	
	private void setBindings(){
		// Generally useful imports
		console.push("import sys");
		console.push("from java.lang import Math");
		
		// Asm
		System.out.print("...");
		console.push("from stack.python import asm");
		System.out.println("asm");
		
		// SIR
		System.out.print("...");
		console.push("from stack.python import sir");
		System.out.println("sir");
		
		// Compiler
		System.out.print("...");
		console.push("from stack.python import compiler");
		System.out.println("compiler");
		
		// Simulator
		System.out.print("...");
		console.push("from stack.python import sim");
		System.out.println("sim");
		
		// System interface
		System.out.print("...");
		console.push("from stack.python import sys");
		System.out.println("sys");
		
		System.out.println("bindings initialized");
		/*
		// Put stack compiler-related functions
		
		// Put stack simulator functions
		bindings.put("simInit", new BindingSimInit(this));
		bindings.put("simState", new BindingSimState(this));
		bindings.put("simReset", new BindingSimReset(this));
		bindings.put("simStep", new BindingSimStep(this));
		bindings.put("simRun", new BindingSimRun(this));
		bindings.put("simBreak", new BindingSimBreak(this));
		bindings.put("simLoad", new BindingSimLoad(this));
		bindings.put("simIMemProbe", new BindingSimIMemProbe(this));
		bindings.put("simMemProbe", new BindingSimMemProbe(this));
		bindings.put("simStackProbe", new BindingSimStackProbe(this));
		bindings.put("simTrace", new BindingSimTrace(this));
		bindings.put("simClearTrace", new BindingSimClearTrace(this));
		
		return bindings;
		*/
	}
}
