package stack.sir.model;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

public class BasicBlock {
	// dag
	// terminator?
	String label;
	Vector<Expression> expressions;
	Hashtable<Integer, Hashtable<String, Integer>> phiTable;
	Expression terminal;
	
	private static Hashtable<String, BasicBlock> directory = null;
	
	public BasicBlock(String label){
		if (directory == null){
			directory = new Hashtable<String, BasicBlock>();
		}
		
		this.label = label;
		expressions = new Vector<Expression>();
		phiTable = new Hashtable<Integer, Hashtable<String,Integer>>();
		
		directory.put(label, this);
	}
	
	public void appendExpression(Expression expression){
		if (expression.isTerminal()){
			terminal = expression;
		}
		
		expressions.add(expression);
	}
	
	public Vector<Expression> getExpressions() {
		return expressions;
	}
	
	public void addPhiMapping(int localVar, String label, int sourceVar){
		if (!phiTable.containsKey(localVar)){
			phiTable.put(localVar, new Hashtable<String, Integer>());
		}
		
		//TODO: this is broken
		phiTable.get(localVar).put(label, sourceVar);
	}
	
	public HashSet<Integer> getPhiDefines() {
		// TODO Auto-generated method stub
		return new HashSet<Integer>(phiTable.keySet());
	}
	
	public String getLabel(){
		return label;
	}
	
	public static BasicBlock lookup(String label){
		return directory.get(label);
	}
	
	@Override
	public String toString(){
		String s = super.toString() + ":\n";
		s += "BASIC_BLOCK " + label + "[";
		
		// Phi table
		if (phiTable.size() > 0){
			for (int i : phiTable.keySet()){
				s += "\n\t\t" + i + " <- " + phiTable.get(i);
			}
		}
		
		s += "\n]{\n";
		
		for (Expression E : expressions){
			s+= "\t";
			if (E == terminal){
				s+= "*(terminal) ";
			}
			s+= E.toString().replaceAll("\n", "\n\t") + "\n";
		}
		
		s += "}\n";
		
		return s;
	}
	
	public Expression getTerminal(){
		return terminal;
	}
	

	public int rename(int variable, BasicBlock src) {
		// If the variable is in the rename table, rename it accordingly
		if (phiTable.containsKey(variable)){
			if (null != phiTable.get(variable).get(src.getLabel())){
				return phiTable.get(variable).get(src.getLabel());
			}
		}
		return variable;
	}

	public HashSet<String> getTargetLabels() {
		return terminal.getTargetLabels();
	}
}
