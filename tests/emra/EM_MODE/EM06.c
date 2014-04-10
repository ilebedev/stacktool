/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM06														         */
/*  - under EM-only mode												 */
/*  - 2 threads, each running on core 0 and 1    						 */
/*    (main0 and main1, respectively)       	    			 		 */
/*  - Test SMT execution (1 native + 1 guest)							 */
/*  - Please make sure that NO stack-related migration happens			 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

// program for thread 0 (on core 0) -- keeps running on its native core 0 (all local accesses)
int main0() {

	int *localArr = (int *) BASE_ADDR_0; // data mapped on core 0

	*(localArr+0) = 0; // local access

	for (int i = 0; i < 20; i++) {
		*(localArr+0) += 1; // local accesses
	}

	return 0;
}

// program for thread 1 (on core 1) -- migrates to core 0 and keeps running there (guest context)
int main1() {

	int *remoteArr = (int *) BASE_ADDR_0; // data mapped on core 0

	*(remoteArr+1) = 0; // migrates to core 0

	for (int i = 0; i < 20; i++) {
		*(remoteArr+1) += 1; // becomes local accesses at core 0 (guest context)
	}

	return 0;
}

/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
	----------------------------------------
	...
	0x00000000 : 20  (updated by thread 0)
	0x00000001 : 20  (updated by thread 1)
	...
	---------------------------------------
*/
