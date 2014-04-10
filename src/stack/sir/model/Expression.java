package stack.sir.model;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import stack.isa.Instruction;
import stack.isa.LoadInstruction;
import stack.isa.StoreInstruction;
import stack.isa.core.InstructionCALL;
import stack.isa.core.InstructionCALL_PC;
import stack.isa.core.InstructionPUSH;
import stack.isa.core.InstructionSETHI;
import stack.isa.em2.store.InstructionST;
import stack.isa.sir.InstructionSIR_BRANCH;
import stack.isa.sir.InstructionSIR_CALL;
import stack.isa.sir.InstructionSIR_JUMP;

public class Expression {
	//list of instructions
	private Vector<Integer> inputStack;
	private Vector<Instruction> instructions;
	private Vector<Integer> outputStack;
	
	private HashSet<Expression> dependents;
	
	static int idCounter = 1;
	static HashSet<Integer> usedIDs = new HashSet<Integer>();
	
	private static Hashtable<Integer, Expression> constantTable = new Hashtable<Integer, Expression>();
	private static Hashtable<Integer, Integer> globalsTable = new Hashtable<Integer, Integer>();
	private static HashSet<Expression> globalsInit = new HashSet<Expression>();
	
	public Expression(){
		inputStack = new Vector<Integer>();
		instructions = new Vector<Instruction>();
		outputStack = new Vector<Integer>();
		
		dependents = new HashSet<Expression>();
	}
	
	public Expression(Expression exp) {
		inputStack = new Vector<Integer>(exp.inputStack);
		instructions = new Vector<Instruction>(exp.instructions);
		outputStack = new Vector<Integer>(exp.outputStack);
		
		dependents = new HashSet<Expression>();
	}

	public void setInputStack(Vector<Integer> stack){
		for (int var : stack){
			usedIDs.add(var);
		}
		
		inputStack = stack;
	}
	
	public void appendInstruction(Instruction instruction){
		instructions.add(instruction);
	}
	
	public void appendInstructions(LinkedList<Instruction> newInstructions) {
		for (int i=0; i<newInstructions.size(); i++){
			instructions.add(newInstructions.get(i));
		}
	}
	
	public void prependInstruction(Instruction instruction) {
		instructions.add(0, instruction);
	}
	
	public void prependInstructions(LinkedList<Instruction> newInstructions) {
		for (int i=0; i<newInstructions.size(); i++){
			instructions.add(i, newInstructions.get(i));
		}
	}
	
	public void setOutputStack(Vector<Integer> stack){
		for (int var : stack){
			usedIDs.add(var);
		}
		
		outputStack = stack;
	}
	
	public Vector<Integer> getInputStack(){
		return inputStack;
	}
	
	public Vector<Instruction> getInstructions(){
		return instructions;
	}
	
	public Vector<Integer> getOutputStack(){
		return outputStack;
	}
	
	public static void addConstant(int variable, Expression expression){
		usedIDs.add(variable);
		constantTable.put(variable, expression);
	}
	
	public static boolean isConstant(int variable){
		return constantTable.containsKey(variable);
	}
	
	public static Expression getConstant(int variable){
		return constantTable.get(variable);
	}
	
	public static Vector<Expression> getConstantExpressions(){
		return new Vector<Expression>(constantTable.values());
	}
	
	private static int globalAddress = 0;
	public static void addGlobal(int variable, Expression initExp){
		usedIDs.add(variable);
		
		// Assign an address
		globalsTable.put(variable, globalAddress);
		
		// Add store to the init expression
		int addr16lo = globalAddress & 0xFFFF;
		int addr16hi = (globalAddress >> 16) & 0xFFFF;
		initExp.appendInstruction(new InstructionPUSH(addr16lo, "addr lo"));
		if (addr16hi != 0){
			initExp.appendInstruction(new InstructionSETHI(addr16hi, "addr hi"));
		}
		initExp.appendInstruction(new InstructionST(0, "init for variable " + variable + " at address " + globalAddress));
		
		// Remove input
		initExp.getInputStack().clear();
		
		// List the init expression in the init segment
		globalsInit.add(initExp);
		
		globalAddress++;
	}
	
	public static boolean isGlobal(int variable){
		return globalsTable.containsKey(variable);
	}
	
	public static int getGlobalAddress(int var) {
		assert globalsTable.containsKey(var);
		return globalsTable.get(var);
	}
	
	public static HashSet<Expression> getGlobalInits() {
		return globalsInit;
	}
	
	@Override
	public String toString(){
		String s = super.toString();
		//EXPRESSION (4) -> (7){	#Load
		//	SIR_LOAD;
		//}
		
		s += ":\nEXPRESSION (";
		// print in stack
		for (int i=0; i<inputStack.size(); i++){
			s += inputStack.get(i);
			if (i < inputStack.size()-1){
				s += ", ";
			}
		}
		s += ") -> (";
		
		// print out stack
		for (int i=0; i<outputStack.size(); i++){
			s += outputStack.get(i);
			if (i < outputStack.size()-1){
				s += ", ";
			}
		}
		s += "){\n";
		
		//print instructions
		for (Instruction instruction : instructions){
			s += "\t" + instruction.toString() + "\n";
		}
		
		s += "}\n";
		
		return s;
	}

	public boolean isTerminal() {
		if (!instructions.isEmpty()){
			return instructions.get(instructions.size()-1).isTerminal();
		} else {
			return false;
		}
	}
	
	public boolean hasStore() {
		for (Instruction inst : instructions){
			if (inst instanceof StoreInstruction) {
				return true;
			} 
		}
		return false;
	}
	
	public boolean hasLoad() {
		for (Instruction inst : instructions){
			if (inst instanceof LoadInstruction) {
				return true;
			} 
		}
		return false;
	}
	
	public boolean hasCall() {
		for (Instruction inst : instructions){
			if (inst instanceof InstructionCALL) {
				return true;
			} else if (inst instanceof InstructionCALL_PC) {
				return true;
			} else if (inst instanceof InstructionSIR_CALL) {
				return true;
			} 
		}
		return false;
	}

	public HashSet<String> getTargetLabels() {
		HashSet<String> targets = new HashSet<String>();
		
		for (Instruction instruction : getInstructions()){
			if (instruction instanceof InstructionSIR_BRANCH){
				targets.add(((InstructionSIR_BRANCH)instruction).getLabel());
			} else if (instruction instanceof InstructionSIR_JUMP){
				targets.add(((InstructionSIR_JUMP)instruction).getLabel());
			}
		}
		
		return targets;
	}

	public static int getNewVariableID() {
		while (usedIDs.contains(idCounter)){
			idCounter++;
		}
		int id = idCounter;
		usedIDs.add(id);
		return id;
	}
	
	public void addDependent(Expression exp){
		if ((exp != this) && (null != exp)){
			dependents.add(exp);
		}
	}
	
	public HashSet<Expression> getDependents(){
		return dependents;
	}
}
