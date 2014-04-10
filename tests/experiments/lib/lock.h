#ifndef __STACK_EM_LOCK__
#define __STACK_EM_LOCK__

// Layout in memory:
// M[A] = lock

void stackEmLock_allocate(unsigned int* lockAddress);

void stackEmLock_release(unsigned int* lockAddress);

int stackEmLock_test(unsigned int* lockAddress);

int stackEmLock_acquire(unsigned int* lockAddress);

int stackEmLock_acquire_naive(unsigned int* lockAddress);

#endif
