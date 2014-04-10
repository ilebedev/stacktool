package stack.compiler.analysis;

import java.util.HashSet;
import java.util.Hashtable;

import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;

public class BasicBlockAnalysis {
	public static Hashtable<Expression, HashSet<Integer>> computeDefines(BasicBlock BB) {
		Hashtable<Expression, HashSet<Integer>> defines = new Hashtable<Expression, HashSet<Integer>>();
		
		for (Expression E : BB.getExpressions()){
			for (Integer var : E.getOutputStack()){
				if (!defines.containsKey(E)){
					defines.put(E, new HashSet<Integer>());
				}
				defines.get(E).add(var);
			}
		}
		return defines;
	}
	
	public static Hashtable<Expression, HashSet<Integer>> computeUses(BasicBlock BB) {
		Hashtable<Expression, HashSet<Integer>> uses = new Hashtable<Expression, HashSet<Integer>>();
		
		for (Expression E : BB.getExpressions()){
			for (Integer var : E.getInputStack()){
				if (!uses.containsKey(E)){
					uses.put(E, new HashSet<Integer>());
				}
				uses.get(E).add(var);
			}
		}
		return uses;
	}
}
