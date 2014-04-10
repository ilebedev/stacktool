/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  RA02														         */
/*  - under RA-only mode												 */
/*  - 3 threads, each running on core 0, 1 and 2 						 */
/*    (main0, main1 and main2, respectively)	    			 		 */
/*  - Test remote access contention										 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

// program for thread 0 (on core 0)
int main0() {

	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1

	*(arr1+0) = 0; // remote access

	for (int i = 0; i < 10; i++) {
		*(arr1+0) += 1; // remote accesses
	}

	return 0;
}

// program for thread 1 (on core 1)
int main1() {

	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1

	*(arr1+1) = 0; // local access

	for (int i = 0; i < 10; i++) {
		*(arr1+1) += 1; // local accesses
	}

	return 0;
}

// program for thread 2 (on core 2)
int main2() {

	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1

	*(arr1+2) = 0; // remote access

	for (int i = 0; i < 10; i++) {
		*(arr1+2) += 1; // remote accesses
	}

	return 0;
}

/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
	----------------------------------------
	...
	0x02000000 : 10  (updated by thread 0)
	0x02000004 : 10  (updated by thread 1)
	0x02000008 : 10  (updated by thread 2)
	...
	---------------------------------------
*/
