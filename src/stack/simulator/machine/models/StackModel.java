package stack.simulator.machine.models;

import java.util.ArrayList;

import stack.excetpion.OverflowException;
import stack.excetpion.UnderflowException;

public class StackModel{
	/**
	 * 
	 */
	private ArrayList<Integer> stack;
	
	private boolean checkDepth = false;
	private int curDepth = 0;
	private int maxDepth = 16;
	
	public StackModel(){
		stack = new ArrayList<Integer>();
		//stack.add(0);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i=0; i<stack.size(); i++){
			if (i>0){
				sb.append(", ");
			}
			int entry = stack.get(i);
			
			if ((entry > 255) | (entry < -1)){
				// print hex
				sb.append("0x");
				sb.append(String.format("%08x",entry).toUpperCase());
			} else {
				// print decimal (for small numbers)
				sb.append(entry);
			}
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	public void setAvailableDepth(int depth, int maxDepth){
		checkDepth = true;
		this.curDepth = depth;
		this.maxDepth = maxDepth;
	}
	
	public void checkDepth(int depthChange) throws UnderflowException, OverflowException{
		if (checkDepth){
			if (curDepth+depthChange < 0){
				throw new UnderflowException();
			} else if (curDepth+depthChange > maxDepth){
				throw new OverflowException();
			}
		}
	}
	
	public void checkDeepAccess(int depth) throws UnderflowException, OverflowException{
		if (checkDepth){
			if (curDepth <= 0){
				throw new UnderflowException();
			} else if (depth > curDepth){
				throw new OverflowException();
			}
		}
	}
	
	public int getCurDepth(){
		return curDepth;
	}
	
	public void setUnlimitedDepth(){
		checkDepth = false;
	}
	
	public int remove(int index){
		curDepth--;
		return stack.remove(index);
	}
	
	public int get(int index){
		return stack.get(index);
	}
	
	public void add(int index, int element){
		curDepth++;
		stack.add(index, element);
	}
	
	public void set(int index, int element){
		stack.set(index, element);
	}
}
