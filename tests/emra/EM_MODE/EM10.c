/*************************************************************************/
/*                                                                       */
/*  EM^2 basic testbench 												 */
/*                                                                       */
/*  EM10														         */
/*  - under EM-only mode												 */
/*  - 4 threads, each running on core 0,1,2 and 3  						 */
/*    (main0,main1,main2 and main3 respectively)     			 		 */
/*  - Test migration contention still making progress    				 */
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

	*(arr2) = 0; // migrates to core 2 (guest context)

	for (int i = 0; i <= 20; i++) {
		*(arr2) += i;
	}

	return 0;
}

// program for thread 1 (on core 1)
int main1() {

	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2

	*(arr2+1) = 0; // migrates to core 2 (guest context) 

	for (int i = 0; i <= 20; i++) {
		*(arr2+1) += i;
	}

	return 0;
}

// program for thread 2 (on core 2)
int main2() {

	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2

	*(arr2+2) = 0; // local accesses

	for (int i = 0; i <= 20; i++) {
		*(arr2+2) += i;
	}

	return 0;
}

// program for thread 3 (on core 3)
int main3() {

	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2

	*(arr2+3) = 0; // migrates to core 2 (guest context) 

	for (int i = 0; i <= 20; i++) {
		*(arr2+3) += i;
	}

	return 0;
}

/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
	----------------------------------------
	...
	0x04000000 : 210  (updated by thread 0)
	0x04000004 : 210  (updated by thread 1)
	0x04000008 : 210  (updated by thread 2)
	0x0400000c : 210  (updated by thread 3)
	...
	---------------------------------------
*/
