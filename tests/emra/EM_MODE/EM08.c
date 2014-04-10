/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM08														         */
/*  - under EM-only mode												 */
/*  - 2 threads, each running on core 0 and 1    						 */
/*    (main0 and main1, respectively)       	    			 		 */
/*  - Test eviction						     							 */
/*  - Please make sure that NO stack-related migration happens			 */
/*                                                                       */
/*************************************************************************/

#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

// program for thread 0 (on core 0) 
int main0() {

	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2
	int temp = 0;

	*(arr2) = 1; // migrates to core 2 (guest context)

	for (int i = 0; i <= 100; i++) {
		temp += i;
	}
	
	*(arr2+2) = temp;

	return 0;
}

// program for thread 1 (on core 1)
int main1() {

	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2
	int temp = 0;

	*(arr2+1) = 1; // migrates to core 2 (guest context) 

	for (int i = 0; i <= 100; i++) {
		temp += i;
	}

	*(arr2+3) = temp;

	return 0;
}

/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
	----------------------------------------
	...
	0x04000000 :    1  (updated by thread 0)
	0x04000004 :    1  (updated by thread 1)
	0x04000008 : 5050  (updated by thread 0)
	0x0400000c : 5050  (updated by thread 1)
	...
	---------------------------------------
*/
