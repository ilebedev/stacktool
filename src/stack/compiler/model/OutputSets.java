package stack.compiler.model;

import java.util.HashSet;
import java.util.Hashtable;

import stack.sir.model.BasicBlock;

public class OutputSets extends Hashtable<BasicBlock, Hashtable<BasicBlock, HashSet<Integer>>>{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("BasicBlock\tTransferSet");
		for (BasicBlock bb: keySet()){
			sb.append("\n");
			sb.append(bb.getLabel());
			sb.append(":");
			
			Hashtable<BasicBlock, HashSet<Integer>> targets = get(bb);
			for (BasicBlock target : targets.keySet()){
				sb.append("\n\t");
				sb.append(target.getLabel());
				sb.append(" ");
				HashSet<Integer> transferSet = targets.get(target);
				if (!transferSet.isEmpty()){
					sb.append("( ");
					for (Integer var : transferSet){
						sb.append(var);
						sb.append(" ");
					}
					sb.append(")");
				} else {
					sb.append("(empty)");
				}
			}
		}
		
		return sb.toString();
	}
}
