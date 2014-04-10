##define BASE_ADDR_0 0x00000000 // (core 0)
##define BASE_ADDR_1 0x02000000 // (core 1)
##define BASE_ADDR_2 0x04000000 // (core 2)
##define BASE_ADDR_3 0x06000000 // (core 3)
#
#// thread 0 on core 0
#int main() {
#
#	int *localArr   = (int *) BASE_ADDR_0;
#	int *remoteArr1 = (int *) BASE_ADDR_1;
#	int *remoteArr2 = (int *) BASE_ADDR_2;
#
#	*(localArr)     = 0;
#	*(localArr+1)   = 0;
#	*(remoteArr1)   = 0; // remote access to core 1 
#	*(remoteArr2)   = 0; // remote access to core 2 
#
#	for (int i = 0; i < 10; i++) {
#		for (int j = 0; j < 2; j++) {
#			*(remoteArr1)++;
#			if (i >= 5)				// for the first 5 loops, run length >= 3 to core 1. For the next 5 loops, run length < 3 to core 1.
#				*(remoteArr2)++;
#		}
#		*(localArr)++;      // migrate home 
#		*(localArr+1)++;
#	}
#
#	return 0;
#}

#Pass around [j,i]

#@ENTRY
	#	*(localArr)     = 0;
PUSH 0;
PUSH 0;	#	localArr = BASE_ADDR_0 = 0x00000000
ST;

	#	*(localArr+1)   = 0;
PUSH 0;
PUSH 1;	#	1+localArr = BASE_ADDR_0 = 0x00000000
ST;

	#	*(remoteArr1)   = 0; // remote access to core 1 
PUSH 0;
PUSH 0;			#	(lo)remoteArr1 = BASE_ADDR_1 = 0x02000000
SETHI 0x0200;	#	(hi)remoteArr1 = BASE_ADDR_1 = 0x02000000
ST;

	#	*(remoteArr2)   = 0; // remote access to core 2 
PUSH 0;
PUSH 0;			#	(lo)remoteArr2 = BASE_ADDR_2 = 0x04000000
SETHI 0x0400;	#	(hi)remoteArr2 = BASE_ADDR_2 = 0x04000000
ST;

	#	Pass around j, i
PUSH 0;	# i=0
PUSH 0; # j=0
#[j, i]

#@OUTER_FOR_COND
#[j, i]
	#	j = 0
	DROP 0;
	PUSH 0;
	
	#	i < 10
	PULL_CP 1;
	PUSH 10;
	COMP_UGT;
	
	#	continue else branch to @RETURN
	B_Z 46;	#Resolved label @RETURN
#[j, i]

#@INNER_FOR_COND
#[j, i]
	#	j < 2
	PULL_CP 0;
	PUSH 2;
	COMP_UGT;
	
	#	continue else branch to @OUTER_FOR_BODY2
	B_Z 23;	#Resolved label @OUTER_FOR_BODY2
#[j, i]

#@INNER_FOR_BODY1
#[j, i]
	#	*(remoteArr1)++;
	PUSH 0;			#	(lo)remoteArr1 = BASE_ADDR_1 = 0x02000000
	SETHI 0x0200;	#	(hi)remoteArr1 = BASE_ADDR_1 = 0x02000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
#[j, i]

#@IF_COND
#[j, i]
	#	i >= 5
	PULL_CP 1;
	PUSH 5;
	COMP_ULE;
	
	#	continue else branch @INNER_FOR_INC 
	B_Z 8;		#Resolved label @INNER_FOR_INC
#[j, i]

#@IF_BODY
#[j, i]
	#	*(remoteArr2)++;
	PUSH 0;			#	(lo)remoteArr2 = BASE_ADDR_2 = 0x04000000
	SETHI 0x0400;	#	(hi)remoteArr2 = BASE_ADDR_2 = 0x04000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
#[j, i]

#@INNER_FOR_INC
#[j, i]
	#	j++
	PUSH 1;
	ADD;
	
	# jump to @INNER_FOR_COND
	J_REL -27;	#Resolved label @INNER_FOR_COND
#[j, i]

#@OUTER_FOR_BODY2
#[j, i]
	#	*(localArr)++;      // migrate home 
	PUSH 0;	#	localArr = BASE_ADDR_0 = 0x00000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
	
	#	*(localArr+1)++;
	PUSH 1;	#	1+localArr = BASE_ADDR_0 = 0x00000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
#[j, i]

#@OUTER_FOR_INC
#[j, i]
	#	i++
	PULL 1;
	PUSH 1;
	ADD;
	TUCK 1;
	
	# jump to @OUTER_FOR_COND
	J_REL -52;	#Resolved label @OUTER_FOR_COND
#[j, i]

#@RETURN
#[j, i]
	DROP 1;
	DROP 0;
	HALT;
	