package stack.excetpion;

public class OverflowException extends Exception{
	private static final long serialVersionUID = 1L;

	public OverflowException(String message) {
		super(message);
	}
	
	public OverflowException() {
		super();
	}
	
	// Nothing needed here! This is simply a special type of exception.
}
