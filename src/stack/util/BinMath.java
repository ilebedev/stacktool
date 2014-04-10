package stack.util;

public class BinMath {
	public static final int TRUE = 0x1;
	public static final int FALSE = 0;
	
	public static boolean isTrue(int value){
		return (value != 0);
	}
	
	public static int zeroExtend(int bits, int value){
		int mask = makeMask(bits);
		
		return value & mask;
	}
	
	public static int signExtend(int bits, int value){
		int mask = makeMask(bits);
		boolean isNegative = (0 != ((value >>> (bits-1)) & 0x1));
		
		if (isNegative){
			int negValue = (value^mask)+1;
			return -negValue;
			
		} else {
			return value & mask;
		}
	}
	
	private static int makeMask(int bits){
		return iexp(2, (bits))-1;
	}
	
	public static int iexp(int base, int n) {
	   int p, y;

	   y = 1;					// Initialize result
	   p = base;				// and p.
	   while(true) {
	      if (0 != (n & 1)){
	    	  y = p*y;			// If n is odd, mult by p.
	      }
	      n = n >> 1;			// Position next bit of n.
	      if (n == 0){
	    	  return y;			// If no more bits in n.
	      }
	      p = p*p;				// Power for next bit of n.
	   }
	}
}
