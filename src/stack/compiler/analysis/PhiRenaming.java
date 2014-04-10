  package stack.compiler.analysis;

import java.util.HashSet;
import java.util.Vector;

import stack.sir.model.BasicBlock;

public class PhiRenaming {
	public static int renameVariable(BasicBlock srcBB, BasicBlock targetBB, int var) {
		return targetBB.rename(var, srcBB);
	}
	
	/*
	public static HashSet<Integer> renameTransferSet(BasicBlock srcBB, BasicBlock targetBB, HashSet<Integer> transferSet){
		// Rename the variables on stack according to target's phi table
		
		HashSet<Integer> renamedSet = new HashSet<Integer>();
		
		for (int variable : transferSet){
			renamedSet.add(targetBB.rename(variable, srcBB));
		}
		
		return renamedSet;
	}
	*/
	public static Vector<Integer> renameTransferStack(BasicBlock srcBB, BasicBlock targetBB, Vector<Integer> transferStack){
		// Rename the variables on stack according to target's phi table
		
		Vector<Integer> renamedStack = new Vector<Integer>();
		
		// TODO: trips up here sometimes
		
		for (int variable : transferStack){
			renamedStack.add(targetBB.rename(variable, srcBB));
		}
		
		return renamedStack;
	}

	public static HashSet<Integer> getPhiDefines(BasicBlock BB) {
		return BB.getPhiDefines();
	}
	
}