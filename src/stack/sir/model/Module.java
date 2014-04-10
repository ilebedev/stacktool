package stack.sir.model;

import java.util.ArrayList;
import java.util.Hashtable;

public class Module {
	Hashtable<String, Function> functions;
	
	String name;
	
	public Module(String name){
		this.name = name;
		functions = new Hashtable<String, Function>();
	}
	
	public void addFunction(Function F){
		functions.put(F.getName(), F);
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<String> getFunctionNames(){
		return new ArrayList<String>(functions.keySet());
	}
	
	public ArrayList<Function> getFunctions(){
		return new ArrayList<Function>(functions.values());
	}
	
	public Function getFunction(String selection) {
		return functions.get(selection);
	}
	
	public String toString() {
		StringBuilder info = new StringBuilder();
		info.append("MODULE ");
		info.append(getName());
		info.append("{\n");
		
		// Constnats table
		StringBuilder constantsTable = new StringBuilder();
		constantsTable.append("\tCONSTANTS_TABLE{\n");
		for (Expression E : Expression.getConstantExpressions()){
			constantsTable.append("\t\t");
			constantsTable.append(E.toString().replaceAll("\n", "\n\t\t"));
			constantsTable.append("\n");
		}
		
		constantsTable.append("\t}\n\n");
		
		info.append(constantsTable.toString());
		// Functions
		for (Function F : functions.values()){
			info.append("\t");
			info.append(F.toString().replaceAll("\n", "\n\t"));
			info.append("\n");
		}
		
		info.append("}\n");
		
		return "stack-module-"+this.hashCode()+":\n"+info.toString();
	}
}
