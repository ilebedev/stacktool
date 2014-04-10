package stack.isa;

public abstract class LabelInstruction extends Instruction{
	protected String label;
	
	public LabelInstruction(String label, String comment){
		super(comment);
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
