
#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#int main1() {
#
#	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2
#	int temp = 0;
#
#	*(arr2+1) = 1; // migrates to core 2 (guest context) 
#
#	for (int i = 0; i <= 100; i++) {
#		temp += i;
#	}
#
#	*(arr2+3) = temp;
#
#	return 0;
#}

#(RA)
#@ENTRY:
	PUSH 1;
	PUSH 1;
	SETHI 0x0400;
	ST_EM 2 0;
	
	PUSH 0;
	
	PUSH 0;
	
#(i, temp, RA)
#@CONDITION:
	PULL_CP 0;
	PUSH 100;
	COMP_ULT;
	
	B_Z 5;	#branch to @BODY
	
#(i, temp, RA)
#@RETURN:
	DROP 0;
	
	PUSH 3;
	SETHI 0x0400;
	ST_EM 2 0;
	
	HALT;

#(i, temp, RA)
#@BODY:
	# temp += i
	TUCK_CP 1;
	#(i, temp, i, RA)
	ADD;
	#(temp, i, RA)
	PULL 1;
	#(i, temp, RA)
	
#(i, temp, RA)
#@FOR.END:
	PUSH 1;
	ADD;
	
	J_REL -15;	#@CONDITION