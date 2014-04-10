package stack.util;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class BidirectionalMap<KeyType, ValueType>{
    private Map<KeyType, ValueType> keyToValueMap = new Hashtable<KeyType, ValueType>();
    private Map<ValueType, KeyType> valueToKeyMap = new Hashtable<ValueType, KeyType>();

    synchronized public void put(KeyType key, ValueType value){
        keyToValueMap.put(key, value);
        valueToKeyMap.put(value, key);
    }

    synchronized public ValueType removeByKey(KeyType key){
        ValueType removedValue = keyToValueMap.remove(key);
        valueToKeyMap.remove(removedValue);
        return removedValue;
    }

    synchronized public KeyType removeByValue(ValueType value){
        KeyType removedKey = valueToKeyMap.remove(value);
        keyToValueMap.remove(removedKey);
        return removedKey;
    }

    public boolean containsKey(KeyType key){
        return keyToValueMap.containsKey(key);
    }

    public boolean containsValue(ValueType value){
        return keyToValueMap.containsValue(value);
    }

    public KeyType getKey(ValueType value){
        return valueToKeyMap.get(value);
    }

    public ValueType get(KeyType key){
        return keyToValueMap.get(key);
    }
    
    public Collection<KeyType> keySet(){
    	return keyToValueMap.keySet();
    }
    
    public Collection<ValueType> valueSet(){
    	return valueToKeyMap.keySet();
    }
}
