
#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#int main0() {
#
#	int *arr2 = (int *) BASE_ADDR_2; // data mapped on core 2
#
#	*(arr2) = 0; // migrates to core 2 (guest context)
#
#	for (int i = 0; i <= 20; i++) {
#		*(arr2) += i;
#	}
#
#	return 0;
#}


#(RA)
#@ENTRY:
	PUSH 0;
	PUSH 0;
	SETHI 0x0400;
	ST_EM 2 0;
	
	PUSH 0;
	
#(i, RA)
#@CONDITION:
	PULL_CP 0;
	PUSH 20;
	COMP_ULT;
	
	B_Z 2;	#branch to @BODY
	
#(i, RA)
#@RETURN:
	DROP 0;
	HALT;
	
#(i, RA)
#@BODY:
	PULL_CP 0;
	PUSH 0;
	SETHI 0x0400;
	TUCK_CP 1;
	LD_EM 1 0;
	ADD;
	PULL 1;
	ST_EM 3 0;
	
#(i, RA)
#@FOR.END:
	PUSH 1;
	ADD;
	
	J_REL -17;	#jump to @CONDITION