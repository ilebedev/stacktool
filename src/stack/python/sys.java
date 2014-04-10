package stack.python;

import java.io.*; 
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

public class sys {
	public static void run(String command){
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			// block
			p.waitFor();
			
			// Read back result and return 
			System.out.println(streamToString(p.getErrorStream()));
			System.out.println(streamToString(p.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void env(){
		Map<String, String> env = System.getenv();
		LinkedList<String> keySet = new LinkedList<String>(env.keySet());
		Collections.sort(keySet, new Comparator<String>() {
			public int compare(String a, String b) {
				return (a.toLowerCase().compareTo(b.toLowerCase()));
			}});
			
		for (String envName : keySet) {
		     System.out.format("%s=%s%n", envName, env.get(envName));
		}
	}
	
	private static String streamToString(InputStream stream){
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return sb.toString();
	}
}
