#define BASE_ADDR_0 0x00000000 // (core 0)
#define BASE_ADDR_1 0x02000000 // (core 1)
#define BASE_ADDR_2 0x04000000 // (core 2)
#define BASE_ADDR_3 0x06000000 // (core 3)

#// thread 0 on core 0
#int main() {
#
#	int *localArr   = (int *) BASE_ADDR_0;
#	int *remoteArr1 = (int *) BASE_ADDR_1;
#	int loop = 0;
#
#	*(localArr)     = 0;
#	*(localArr+1)   = 0;
#	*(remoteArr1)   = 0; // remote access to core 1 
#
#	for (int i = 0; i < 10; i++) {
#		if (i < 5)    // for the first 5 loops, long run length
#			loop = 2;
#		else
#			loop = 1; // for the next 5 loops, short run length
#
#		for (int j = 0; j < loop; j++) {
#			*(remoteArr1)++;
#		}
#		*(localArr)++;      // migrate home 
#		*(localArr+1)++;
#	}
#	
#	return 0;
#}

#Pass around [L,j,i]

#@ENTRY
#[]
	#	int *localArr   = (int *) BASE_ADDR_0;
	#	int *remoteArr1 = (int *) BASE_ADDR_1;
	#	int loop = 0; // this isn't necesary - only need to declare
	#	*(localArr)     = 0;
	PUSH 0;			#0
	PUSH 0;			#BASE_ADDR_0=0x00000000
	ST;
	
	#	*(localArr+1)   = 0;
	PUSH 0;			#0
	PUSH 1;			#1+BASE_ADDR_0=0x00000000
	ST;
	
	#	*(remoteArr1)   = 0; // remote access to core 1
	PUSH 0;			#0
	PUSH 0;			#(low)BASE_ADDR_1=0x02000000
	SETHI 0x0200;	#(hi)BASE_ADDR_1=0x02000000
	ST;
	
	# i=0
	PUSH 0;
	# j=...
	PUSH 0;
	# L=...
	PUSH 0;
#[L,j,i]
	
#@OUTER_FOR_COND
#[L,j,i]
	#	i < 10
	PULL_CP 2;
	PUSH 10;
	COMP_UGT;
	
	#	continue else branch to @RETURN
	B_Z 48;	#Resolved label @RETURN
	
#[L,j,i]
	
#@COND
#[L,j,i]
	#	j = 0
	DROP 1;
	PUSH 0;
	TUCK 1;
	
	#	i < 5
	PULL_CP 2;
	PUSH 5;
	COMP_UGT;
	
	#	continue else branch to @ELSE_BODY
	B_Z 3;	#Resolved label @ELSE_BODY
	
#[L,j,i]
	
#@IF_BODY
#[L,j,i]
	#	loop = 2;
	DROP 0;
	PUSH 2;
	
	#	jump to @INNER_FOR_COND
	J_REL 2;		#Resolved label @INNER_FOR_COND
	
#[L,j,i]
	
#@ELSE_BODY
#[L,j,i]
	#	loop = 1; // for the next 5 loops, short run length
	DROP 0;
	PUSH 1;
	
#[L,j,i]
	
#@INNER_FOR_COND
#[L,j,i]
	#	j < loop
	PULL_CP 1;
	PULL_CP 1;
	COMP_UGT;
	
	#	continue else branch to @OUTER_FOR_BODY_2
	B_Z 13;	#Resolved label @OUTER_FOR_BODY_2
	
#[L,j,i]

#@INNER_FOR_BODY
#[L,j,i]
	#	*(remoteArr1)++;
	PUSH 0;			#(low)BASE_ADDR_1=0x02000000
	SETHI 0x0200;	#(hi)BASE_ADDR_1=0x02000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
	
#[L,j,i]
	
#@INNER_FOR_INC
#[L,j,i]
	#	j++
	PULL 1;
	PUSH 1;
	ADD;
	TUCK 1;
	
	#	jump to @INNER_FOR_COND
	J_REL -17;	#Resolved label @INNER_FOR_COND
	
#[L,j,i]
	
#@OUTER_FOR_BODY_2
#[L,j,i]
	#	*(localArr)++;      // migrate home
	PUSH 0;			#BASE_ADDR_0=0x00000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
	
	#	*(localArr+1)++;
	PUSH 1;			#BASE_ADDR_0=0x00000000
	PULL_CP 0;
	LD;
	PUSH 1;
	ADD;
	TUCK 1;
	ST;
	
#[L,j,i]

#@OUTER_FOR_INC
#[L,j,i]
	#	i++
	PULL 2;
	PUSH 1;
	ADD;
	TUCK 2;
	
	#	jump to @OUTER_FOR_COND
	J_REL -52;	#Resolved label @OUTER_FOR_COND
	
#[L,j,i]

#@RETURN
#[L,j,i]
	DROP 2;
	DROP 1;
	DROP 0;
	HALT;
	