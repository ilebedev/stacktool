/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM01														         */
/*  - under EM-only mode												 */
/*  - 1 thread running on core 0										 */
/*  - Test migrations (native->guest, guest->guest, guest->native)   	 */
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

	/* Please make sure no unexpected migrations happen
	   due to stack underflow/overflow during the trip */ 

	*(localArr)   = 0; // local write
	*(remoteArr1) = 1; // migrate to core 1 (guest) and write
	*(remoteArr2) = 2; // migrate to core 2 (guest) and write
	*(remoteArr3) = 3; // migrate to core 3 (guest) and write
	*(localArr) += 1;  // migrate to core 0 (native) 

	*(remoteArr1+1) = *(localArr);      // migrate to core 1 (guest)
	*(remoteArr2+1) = *(remoteArr1+1);  // first read @ core 1, then migrate to core 2 (guest)

	/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
		----------------------------------------
		0x00000000 : 1
		...
		0x02000000 : 1
		0x02000001 : 1
		...
		0x04000000 : 2
		0x04000001 : 1
		...
		0x06000000 : 3
		...
		---------------------------------------
	*/

	return 0;
}
