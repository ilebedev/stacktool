
#include "../lib/em2.h"
#include "../lib/lock.h"

#define TRIALS (10)
#define LOCK_ADDRESS ((unsigned int*)0x0000A000)

int main(int argc, char** argv){
	// multiple threads will be doing this
	
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

