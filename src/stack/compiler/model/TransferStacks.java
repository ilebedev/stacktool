package stack.compiler.model;

import java.util.Hashtable;
import java.util.Vector;

import stack.sir.model.BasicBlock;

public class TransferStacks extends Hashtable<BasicBlock, Vector<Integer>>{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("BasicBlock\tTransferSet");
		for (BasicBlock bb: keySet()){
			sb.append("\n");
			sb.append(bb.getLabel());
			sb.append("\t");
			Vector<Integer> transferSet = get(bb);
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
