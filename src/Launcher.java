import java.io.FileInputStream;
import java.io.FileNotFoundException;

import stack.StackTool;


public class Launcher {
	
	public static void main(String[] args) {
		
		// Simply start the LUA shell for now
		if (args.length == 0){
			new StackTool();
		} else {
			String filename = args[0];
			try {
				new StackTool(new FileInputStream(filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// That's it
	}

}
