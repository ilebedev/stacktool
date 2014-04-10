package stack.isa;

public abstract class ThreeImmediateInstruction extends Instruction{
protected int imm8_lo, imm8_hi, offset;
	
	public ThreeImmediateInstruction(int imm8_hi, int imm8_lo, int offset, String comment){
		super(comment);
		
		this.imm8_hi = imm8_hi;
		this.imm8_lo = imm8_lo;
		this.offset = offset;
	}
	
	public int getImmediateHigh(){
		return imm8_hi;
	}
	
	public int getImmediateLow(){
		return imm8_lo;
	}
	
	public int getOffset(){
		return offset;
	}
}
