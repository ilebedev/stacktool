package stack.excetpion;

public class UnderflowException extends Exception{
	private static final long serialVersionUID = 1L;

	public UnderflowException(String message) {
		super(message);
	}
	
	public UnderflowException() {
		super();
	}
	
	// Nothing needed here! This is simply a special type of exception.
}
