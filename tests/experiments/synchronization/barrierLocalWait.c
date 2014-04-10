
#include "../lib/em2.h"
#include "../lib/lock.h"

// This barrier implementation is *DYNAMIC* - ish - it requires a #define with num cores

// Layout in memory:
// M[A] = barrier lock
// M[A+1] = counter
// M[A+2] = numParticipatingThreads
// M[C0+AO] = thread0 lock
// M[C1+AO] = thread1 lock
// ...
// M[CN+AO] = threadN lock

void stackEmBarrier_wait(unsigned int* barrierAddress, unsigned int* lockOffset){
	int last = 0;
	
	while(!stackEmLock_acquire(barrierAddress)){/* wait for the barrier lock */}
	
	// register completion at barrier
	int value = *(barrierAddress+1)-1;
	*(barrierAddress+1) = value;
	last = (value<1);
	
	// release the barrier lock
	stackEmLock_release(barrierAddress);
	
	if (last){
		// if last, reset sleep counter
		*(barrierAddress+1) = *(barrierAddress+2);
		
		// if last, wake all other threads up
		for (int i=0; i<NUM_CORES; i++){
			unsigned int* addr = (i<<25)+lockOffset;
			stackEmLock_release(addr);
		}
	} else {
		// else, spin on own lock
		unsigned int* addr = (stackEM_getThreadID()<<25)+lockOffset;
		while(!stackEmLock_acquire(addr)){ // will not be available until the last thread releases it
			// Spin
		}; 
	}
}

void stackEmBarrier_register(unsigned int* barrierAddress){
	while(!stackEmLock_acquire(barrierAddress)){/* wait for the barrier lock */}
	
	// increment num threads yet to reach barrier
	*(barrierAddress+1) = *(barrierAddress+1)+1;
	
	// increment num threads participating
	*(barrierAddress+2) = *(barrierAddress+2)+1;
	
	// NOTE: the lock is already allocated for ALL threads
	
	// release barrier lock
	stackEmLock_release(barrierAddress);
}

#define TRIALS (1)
#define SPIN_ITERATIONS (0)
#define BARRIER_ADDRESS ((unsigned int*)0x0000A000)
#define LOCK_OFFSET (0xA000)

int main(int argc, char** argv){
	// multiple threads will be doing this
	unsigned int* address = BARRIER_ADDRESS;
	
	*BARRIER_ADDRESS = 0;
	*(BARRIER_ADDRESS+1) = 0;
	*(BARRIER_ADDRESS+2) = 0;
	*((unsigned int*)((stackEM_getThreadID()<<25)+LOCK_OFFSET)) = 0;
	
	for (int j=0; j<3; j++){
		stackEM_nop();
	}
	
	stackEmBarrier_register(address); // add this thread to barrier
	
	for (int i=0; i<TRIALS; i++){
		for (int j=0; j<SPIN_ITERATIONS; j++){
			stackEM_nop();
		}
		
		stackEmBarrier_wait(address, ((unsigned int*)LOCK_OFFSET));
	}
	
	return 0;
}

