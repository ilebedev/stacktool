/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  PR08														         */
/*  - under EM/RA mode using Predictor									 */
/*  - use threshold of 3 												 */
/*  - 1 thread running on core 0										 */
/*  - Test predictor learns ST2 underflow/overflow 						 */ 
/*  - DEFAULT is to send 0 for ST2: test underflow first, then overflow	 */ 
/*  - Please make sure that no other types of migration happen			 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

// thread 0 on core 0
int main() {

	int *remoteArr1 = (int *) BASE_ADDR_1;

	*(remoteArr1)   = 0;

	for (int i = 0; i < 10; i++) {
		*(remoteArr1)++; // migrate to core 1
		*(remoteArr1)++;
		
		if (i < 5)    // for the first 5 loops, long run length
			// TODO: for the first 5 loops, POP 1 ENTRY FROM ST2 -- ST2 UNDERFLOW, PREDICTOR WILL SEND 2 ENTRIES, THEN NO MORE STACK MIGRATIONS UPTO LOOP 5 
		else
			// TODO: for the next 5 loops, PUSH 3 ENTRIES TO ST2 (no pop at all) -- ST2 OVERFLOW, PREDICTOR WILL SEND 0 ENTRIES
	}

	/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
		----------------------------------------
		...
		0x02000000 : 20
		...
		---------------------------------------
	*/

	return 0;
}
