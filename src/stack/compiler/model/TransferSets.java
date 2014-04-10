package stack.compiler.model;

import java.util.HashMap;
import java.util.HashSet;

import stack.sir.model.BasicBlock;

public class TransferSets extends HashMap<BasicBlock, HashSet<Integer>>{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("BasicBlock\tTransferSet");
		for (BasicBlock bb: keySet()){
			sb.append("\n");
			sb.append(bb.getLabel());
			sb.append("\t");
			HashSet<Integer> transferSet = get(bb);
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
		
		return sb.toString();
	}
}
