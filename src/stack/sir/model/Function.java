package stack.sir.model;

import java.util.Vector;
import java.util.Hashtable;

public class Function {
	//list of basic blocks
	String name;
	int returnAddressVariable;
	int returnStackSize;
	Vector<Integer> argumentStack;
	Hashtable<String, BasicBlock> basicBlocks;
	BasicBlock entry;
	
	
	public Function(String name, int returnAddressVariable, int returnStackSize, Vector<Integer> argumentStack){
		this.name = name;
		this.returnAddressVariable = returnAddressVariable;
		this.returnStackSize = returnStackSize;
		this.argumentStack = argumentStack;
		basicBlocks = new Hashtable<String, BasicBlock>();
	}
	
	public String getName(){
		return name;
	}
	
	public void appendBasicBlock(BasicBlock bb){
		if (null == entry){
			entry = bb;
		}
		basicBlocks.put(bb.getLabel(), bb);
	}
	
	public Vector<String> getBasicBlockLabels(){
		return new Vector<String>(basicBlocks.keySet());
	}
	
	public Vector<BasicBlock> getBasicBlocks(){
		return new Vector<BasicBlock>(basicBlocks.values());
	}
	
	public BasicBlock getBasicBlock(String selection) {
		return basicBlocks.get(selection);
	}
	
	public BasicBlock getEntryBasicBlock(){
		return entry;
	}
	
	public int getReturnAddressVariable(){
		return returnAddressVariable;
	}
	
	public int getReturnStackSize(){
		return returnStackSize;
	}
	
	public Vector<Integer> getArgumentStack(){
		return argumentStack;
	}
	
	public String toString() {
		StringBuilder info = new StringBuilder();
		info.append("FUNCTION ");
		info.append(getReturnAddressVariable());
		info.append(" ");
		info.append(getReturnStackSize());
		info.append(" ");
		info.append(getName());
		info.append(" (");
		for (int i=0; i<getArgumentStack().size(); i++){
			int var = getArgumentStack().get(i);
			info.append(var);
			if (i < getArgumentStack().size()-1){
				info.append(",");
			}
		}
		info.append("){\n");
		
		// Functions
		for (BasicBlock BB : basicBlocks.values()){
			info.append("\t");
			if (BB == entry){
				info.append("*(entry) ");
			}
			info.append(BB.toString().replaceAll("\n", "\n\t"));
			info.append("\n");
		}
		
		info.append("}\n");
		
		return info.toString();
	}
}
