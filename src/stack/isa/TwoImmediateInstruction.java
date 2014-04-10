package stack.isa;

public abstract class TwoImmediateInstruction extends Instruction{
	protected int imm8_lo, imm8_hi;
	
	public TwoImmediateInstruction(int imm8_hi, int imm8_lo, String comment){
		super(comment);
		
		this.imm8_hi = imm8_hi;
		this.imm8_lo = imm8_lo;
	}
	
	public int getImmediateHigh(){
		return imm8_hi;
	}
	
	public int getImmediateLow(){
		return imm8_lo;
	}
}
