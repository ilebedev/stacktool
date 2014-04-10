
#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#int main0() {
#	int *arr0 = (int *) BASE_ADDR_0; // data mapped on core 0
#	int *arr1 = (int *) BASE_ADDR_1; // data mapped on core 1
#
#	*(arr0+0) = 0; // local access
#	*(arr1+0) = 0;
#
#	for (int i = 0; i < 20; i++) {
#		*(arr0+0) += 1; 
#		*(arr1+0) += 1; 
#	}
#
#	return 0;
#}

#(RA)
#@ENTRY:
	PUSH 0;
	PUSH 0;
	ST_EM 2 0;
	
	PUSH 0;
	PUSH 0;
	SETHI 0x0200;
	ST_EM 2 0;
	
	PUSH 0;
	
#(i, RA)
#@CONDITION:
	PULL_CP 0;
	PUSH 20;
	COMP_ULE;
	
	B_Z 2; #branch to @BODY
	
#(i, RA)
#@RETURN:
	DROP 0;
	HALT;

#(i, RA)
#@BODY:
	PUSH 0;
	LD_EM 1 0;
	PUSH 1;
	ADD;
	PUSH 0;
	ST_EM 2 0;
	
	PUSH 0;
	SETHI 0x0200;
	PULL_CP 0;
	LD_EM 1 0;
	PUSH 1;
	ADD;
	PULL 1;
	ST_EM 2 0;
	
#(i, RA)
#@FOR.END:
	PUSH 1;
	ADD;
	
	J_REL -23; #jump to @CONDITION