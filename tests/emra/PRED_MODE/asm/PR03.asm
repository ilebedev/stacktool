
#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#int main() {
#
#	int *localArr = (int *) BASE_ADDR_0;
#	int *remoteArr1 = (int *) BASE_ADDR_1;
#
#	*(localArr)     = 0;
#	*(localArr+1)   = 0;
#	*(localArr+2)   = 0;
#	*(remoteArr1)   = 0; // remote access to core 1 
#	*(remoteArr1+1) = 0; // remote access to core 1 
#	*(remoteArr1+2) = 0; // remote access to core 1 
#
#	for (int i = 0; i < 10; i++) {
#		*(remoteArr1)++;   // this will become local accesses (by migrating to core 1)
#		*(remoteArr1+1)++; 
#		*(remoteArr1+2)++; 
#		*(localArr)++;     
#		*(localArr+1)++;     
#		*(localArr+2)++;     
#	}
#}

#(RA)
#@ENTRY:
	PUSH 0;
	PUSH 0;
	ST;
	
	PUSH 0;
	PUSH 1;
	ST;
	
	PUSH 0;
	PUSH 2;
	ST;
	
	PUSH 0;
	PUSH 0;
	SETHI 0x0200;
	ST;
	
	PUSH 0;
	PUSH 1;
	SETHI 0x0200;
	ST;
	
	PUSH 0;
	PUSH 2;
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
	#	*(remoteArr1)++;   // this will become local accesses (by migrating to core 1)
	PUSH 0;
	SETHI 0x0200;
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	PULL 1;
	ST;
	
	#	*(remoteArr1+1)++; 
	PUSH 1;
	SETHI 0x0200;
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	PULL 1;
	ST;
	
	#	*(remoteArr1+2)++; 
	PUSH 2;
	SETHI 0x0200;
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	PULL 1;
	ST;
	
	#	*(localArr)++;     // this will become remote accesses from core 1
	PUSH 0;
	LD;
	PUSH 1;
	ADD;
	PUSH 0;
	ST;
	
	#	*(localArr+1)++;
	PUSH 1;
	LD;
	PUSH 1;
	ADD;
	PUSH 1;
	ST;
	
	#	*(localArr+2)++;
	PUSH 2;
	LD;
	PUSH 1;
	ADD;
	PUSH 2;
	ST;
	
#(i, RA)
#@FOR.END:
	PUSH 1;
	ADD;
	
	J_REL -51;	#jump to @CONDITION
	