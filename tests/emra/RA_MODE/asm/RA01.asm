#*****************************************
#                                         
#  EM^2 basic testbench 					
#                                         
#  RA01										
#  - under RA-only mode						
#  - 1 thread running on core 0				
#  - Test remote accesses & local accesses
#                                         
#*****************************************

#	EXPECTED RESULT (MEMORY) AFTER EXECUTION  
#	----------------------------------------
#	0x00000000 : 0
#	0x00000001 : 1
#	0x00000002 : 2
#	0x00000003 : 3
#	0x00000010 : 4
#	...
#	0x02000000 : 1
#	...
#	0x04000000 : 2
#	...
#	0x06000000 : 3
#	...
#	---------------------------------------

# Constants table
# BASE_ADDR_0 0x00000000 // (core 0)
# BASE_ADDR_1 0x02000000 // (core 1)
# BASE_ADDR_2 0x04000000 // (core 2)
# BASE_ADDR_3 0x06000000 // (core 3)

# Variable table
# 6	int *localArr   = (int *) BASE_ADDR_0;
# 7	int *remoteArr1 = (int *) BASE_ADDR_1;
# 8	int *remoteArr2 = (int *) BASE_ADDR_2;
# 9	int *remoteArr3 = (int *) BASE_ADDR_3;

# Main entry is here. Return address is assumed on stack
#	*(localArr)   = 0; // local write
PUSH 0;
PUSH 0;
ST_RA;

#	*(remoteArr1) = 1; // remote write to core 1
PUSH 1;
PUSH 0;
SETHI 0x0200;
ST_RA;

#	*(remoteArr2) = 2; // remote write to core 2
PUSH 2;
PUSH 0;
SETHI 0x0400;
ST_RA;

#	*(remoteArr3) = 3; // remote write to core 3
PUSH 3;
PUSH 0;
SETHI 0x0600;
ST_RA;

# localArr
PUSH 0;

#	*(localArr+1) = *(remoteArr1);   // remote read to core 1 & local write
PUSH 0;
SETHI 0x0200;
LD_RA;

PULL_CP 1;
PUSH 1;
ADD;

ST_RA;

#	*(localArr+2) = *(remoteArr2);   // remote read to core 2 & local write
PUSH 0;
SETHI 0x0400;
LD_RA;

PULL_CP 1;
PUSH 2;
ADD;

ST_RA;

#	*(localArr+3) = *(remoteArr3);   // remote read to core 3 & local write
PUSH 0;
SETHI 0x0600;
LD_RA;

PULL_CP 1;
PUSH 3;
ADD;

ST_RA;

#	*(localArr+4) = *(localArr) + 4; // local read & local write
PUSH 0;
LD_RA;

PUSH 4;
ADD;

PULL 1;
PUSH 4;
ADD;

ST_RA;

# Return (lowest item on stack is the return address)
HALT;
