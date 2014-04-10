##define BASE_ADDR_0 0x00000000 // (core 0)
##define BASE_ADDR_1 0x02000000 // (core 1)
##define BASE_ADDR_2 0x04000000 // (core 2)
##define BASE_ADDR_3 0x06000000 // (core 3)
#
#// thread 0 on core 0
#int main() {
#
#	int *remoteArr1 = (int *) BASE_ADDR_1;
#
#	*(remoteArr1)   = 0;
#
#	for (int i = 0; i < 10; i++) {
#		*(remoteArr1)++; // migrate to core 1
#		*(remoteArr1)++;
#		
#		if (i < 5)    // for the first 5 loops, long run length
#			// TODO: for the first 5 loops, POP 1 ENTRY FROM ST2 -- ST2 UNDERFLOW, PREDICTOR WILL SEND 2 ENTRIES, THEN NO MORE STACK MIGRATIONS UPTO LOOP 5 
#		else
#			// TODO: for the next 5 loops, PUSH 3 ENTRIES TO ST2 (no pop at all) -- ST2 OVERFLOW, PREDICTOR WILL SEND 0 ENTRIES
#	}
#
#	/*  EXPECTED RESULT (MEMORY) AFTER EXECUTION  
#		----------------------------------------
#		...
#		0x02000000 : 20
#		...
#		---------------------------------------
#	*/
#
#	return 0;
#}

#Pass around [i]

#@ENTRY
#[]
	# populate AUX with 5 pieces of data
	PUSH 1;
	PUSH 2;
	PUSH 3;
	PUSH 4;
	PUSH 5;
	MAIN2AUX;
	MAIN2AUX;
	MAIN2AUX;
	MAIN2AUX;
	MAIN2AUX;

	#	*(remoteArr1)   = 0;
	PUSH 0;			#0
	PUSH 0;			#(low)BASE_ADDR_1=0x02000000
	SETHI 0x0200;	#(hi)BASE_ADDR_1=0x02000000
	ST;
	
	# i=0
	PUSH 0;
#[i]
	
#@FOR_COND
#[i]
	#	i < 10
	PULL_CP 0;
	PUSH 10;
	COMP_UGT;
	
	#	continue else branch to @RETURN
	B_Z 32;	#Resolved label @RETURN
	
#[i]

#@FOR_BODY_1
#[i]
	#	*(localArr)++;      // migrate home
	PUSH 0;			#(low)BASE_ADDR_1=0x02000000
	SETHI 0x0200;	#(hi)BASE_ADDR_1=0x02000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
	
	#	*(localArr)++;      // migrate home
	PUSH 0;			#(low)BASE_ADDR_1=0x02000000
	SETHI 0x0200;	#(hi)BASE_ADDR_1=0x02000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
#[i]

#@IF_COND
#[i]
	#	i < 5
	PULL_CP 0;
	PUSH 5;
	COMP_UGT;
	
	#	continue else branch to @ELSE_BODY
	B_Z 3;	#Resolved label @ELSE_BODY
	
#[i]
	
#@IF_BODY
#[i]
	#// TODO: for the first 5 loops, POP 1 ENTRY FROM ST2
	#-- ST2 UNDERFLOW,PREDICTOR WILL SEND 2 ENTRIES, THEN NO MORE STACK MIGRATIONS UPTO LOOP 5 
	AUX2MAIN;
	DROP 0;
	
	#	jump to @FOR_INC
	J_REL 6;		#Resolved label @FOR_INC
#[i]
	
#@ELSE_BODY
#[i]
	#// TODO: for the next 5 loops, PUSH 3 ENTRIES TO ST2 (no pop at all)
	#-- ST2 OVERFLOW, PREDICTOR WILL SEND 0 ENTRIES
	
	PUSH 1;
	MAIN2AUX;
	PUSH 2;
	MAIN2AUX;
	PUSH 3;
	MAIN2AUX;
#[i]

#@FOR_INC
#[i]
	#	i++
	PUSH 1;
	ADD;
	
	#	jump to @FOR_COND
	J_REL -36;	#Resolved label @FOR_COND
#[i]

#@RETURN
#[i]
	DROP 0;
	
	# Get rid of the 15 pieces of data on AUX
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	AUX2MAIN;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	DROP 0;
	HALT;
	