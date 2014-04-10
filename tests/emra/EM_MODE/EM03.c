/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM03														         */
/*  - under EM-only mode												 */
/*  - 1 thread running on core 0										 */
/*  - Test Stack 1 underflow migration								   	 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

int main() {

	int *localArr   = (int *) BASE_ADDR_0;
	int *remoteArr1 = (int *) BASE_ADDR_1;

	*(localArr)   = 1; // local write
	*(remoteArr1) = *(localArr); // migrate to core 1 (guest) and write

	/* TODO: POP FROM ST1 TILL IT UNDERFLOWS */ 

	*(localArr) += 1; // this becomes a local access since the thread already migrated to core 1 due to stack underflow

	/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
		----------------------------------------
		0x00000000 : 2
		...
		0x02000000 : 1
		...
		---------------------------------------
	*/

	return 0;
}
