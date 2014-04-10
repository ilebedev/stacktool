/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM07														         */
/*  - under EM-only mode												 */
/*  - 2 threads, each running on core 0 and 1    						 */
/*    (main0 and main1, respectively)       	    			 		 */
/*  - Test swapping between two threads     							 */
/*  - Please make sure that NO stack-related migration happens			 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

// program for thread 0 (on core 0) 
int main0() {

	int *arr0 = (int *) BASE_ADDR_0; // data mapped on core 0
	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1

	*(arr0+0) = 0; // local access
	*(arr1+0) = 0;

	for (int i = 0; i < 20; i++) {
		*(arr0+0) += 1; 
		*(arr1+0) += 1; 
	}

	return 0;
}

// program for thread 1 (on core 1)
int main1() {

	int *arr0 = (int *) BASE_ADDR_0; // data mapped on core 0
	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1

	*(arr0+1) = 0; // local access
	*(arr1+1) = 0;

	for (int i = 0; i < 20; i++) {
		*(arr0+1) += 1; 
		*(arr1+1) += 1; 
	}

	return 0;
}

/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
	----------------------------------------
	...
	0x00000000 : 20  (updated by thread 0)
	0x00000001 : 20  (updated by thread 1)
	...
	0x02000000 : 20  (updated by thread 0)
	0x02000001 : 20  (updated by thread 1)
	...
	---------------------------------------
*/
