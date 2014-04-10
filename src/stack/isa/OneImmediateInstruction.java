package stack.isa;

public abstract class OneImmediateInstruction extends Instruction{
	protected int imm16;
	
	public OneImmediateInstruction(int imm16, String comment){
		super(comment);
		this.imm16 = imm16;
	}
	
	public int getImmediate(){
		return imm16;
	}
}
