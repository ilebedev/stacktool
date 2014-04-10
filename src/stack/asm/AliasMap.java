package stack.asm;

import java.util.Hashtable;

public class AliasMap {
	//       alias, realName
	Hashtable<String, String> map;
	
	public AliasMap(){
		map = new Hashtable<String, String>();
	}
	
	public void add(String alias, String refersTo){
		if (!map.containsKey(alias)){	
			if (map.containsKey(refersTo)){
				add(alias, map.get(refersTo));
			}
		}
	}
	
	public String lookup(String alias){
		String label = map.get(alias);
		if (null == label){
			// If no alias exists in the alias map,
			// then the label is not an alias for another label,
			// and therefore refers to itself
			return alias;
			
		} else {
			return label;
		}
	}
}
