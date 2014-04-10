
#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#int main() {
#
#	int *localArr = (int *) BASE_ADDR_0;
#	int *remoteArr1 = (int *) BASE_ADDR_1;
#
#	*(localArr) = 0;
#	*(remoteArr1) = 0; // remote access to core 1 
#
#	for (int i = 0; i < 10; i++) {
#		*(localArr)++;
#		*(remoteArr1)++; 
#	}
#}

#(RA)
#@ENTRY:
	PUSH 0;
	PUSH 0;
	ST;
	
	PUSH 0;
	PUSH 0;
	SETHI 0x0200;
	ST;
	
	PUSH 0;
	
#(i, RA)
#@CONDITION:
	PULL_CP 0;
	PUSH 10;
	COMP_ULE;
	
	B_Z 2;	#branch to @BODY
	
#(i, RA)
#@RETURN:
	DROP 0;
	HALT;

#(i, RA)
#@BODY:
	#	*(localArr)++;
	PUSH 0;
	LD;
	PUSH 1;
	ADD;
	PUSH 0;
	ST;
	
	#	*(remoteArr1)++; 
	PUSH 0;
	SETHI 0x0200;
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	PULL 1;
	ST;
	
#(i, RA)
#@FOR.END:
	PUSH 1;
	ADD;
	
	J_REL -23;	#jump to @CONDITION