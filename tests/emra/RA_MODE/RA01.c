/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  RA01														         */
/*  - under RA-only mode												 */
/*  - 1 thread running on core 0										 */
/*  - Test remote accesses & local accesses								 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

int main() {

	int *localArr   = (int *) BASE_ADDR_0;
	int *remoteArr1 = (int *) BASE_ADDR_1;
	int *remoteArr2 = (int *) BASE_ADDR_2;
	int *remoteArr3 = (int *) BASE_ADDR_3;

	*(localArr)   = 0; // local write
	*(remoteArr1) = 1; // remote write to core 1
	*(remoteArr2) = 2; // remote write to core 2
	*(remoteArr3) = 3; // remote write to core 3

	*(localArr+1) = *(remoteArr1);   // remote read to core 1 & local write
	*(localArr+2) = *(remoteArr2);   // remote read to core 2 & local write
	*(localArr+3) = *(remoteArr3);   // remote read to core 3 & local write
	*(localArr+4) = *(localArr) + 4; // local read & local write

	/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
		----------------------------------------
		0x00000000 : 0
		0x00000004 : 1
		0x00000008 : 2
		0x0000000c : 3
		0x00000010 : 4
		...
		0x02000000 : 1
		...
		0x04000000 : 2
		...
		0x06000000 : 3
		...
		---------------------------------------
	*/

	return 0;
}
