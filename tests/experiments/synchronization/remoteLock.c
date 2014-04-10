
#include "../lib/em2.h"
#include "../lib/lock.h"

#define TRIALS (10)
#define LOCK_ADDRESS (0xFF00A000)

int main(int argc, char** argv){
	unsigned int* address = LOCK_ADDRESS;
	
	*address = 0;
	
	for (int i=0; i<TRIALS; i++){
		while (!stackEmLock_acquire(address)){
			// spin
		}
		// DO NOTHING
		
		stackEmLock_release(address);
	}
	
	return 0;
}

