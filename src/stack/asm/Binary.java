package stack.asm;

import java.util.ArrayList;

public class Binary extends ArrayList<Integer>{
	private static final long serialVersionUID = 1L;

	public Binary(){
		super();
	}
	
	public Binary(int[] bits){
		super();
		
		for (Integer word : bits){
			add(word);
		}
	}
	
	public int[] toBits(){
		int[] bits = new int[size()];
		for (int i=0; i<size(); i++){
			bits[i] = get(i);
		}
		
		return bits;
	}
	
	public void addWord(int word){
		add(word);
	}
	
	@Override
	public String toString(){
		StringBuilder text = new StringBuilder();
		
		for (int i=0; i<size(); i++){
			text.append("0x");
			text.append(String.format("%08x", get(i)).toUpperCase());
			text.append('\n');
		}
		
		return text.toString();
	}
}
