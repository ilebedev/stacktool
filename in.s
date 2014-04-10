CALL_REL 1;
HALT;
TUCK 1;	#autogen for var : 1
TUCK 0;	#autogen for var : 2
PUSH 0;	
TUCK 1;	#autogen for var : 17
PUSH 0;	
TUCK 2;	#autogen for var : 18
SIR_ALLOCA;	
TUCK 0;	#autogen for var : 4
SIR_ALLOCA;	
TUCK 1;	#autogen for var : 5
PULL 2;	#autogen for var : 2 ; stack before is : [4, 5, 2, 17, 18, 1]
PULL 1;	#autogen for var : 4 ; stack before is : [2, 4, 5, 17, 18, 1]
TUCK_CP 2;	#autogen for var : 4
SIR_STORE;	
PULL 2;	#autogen for var : 17 ; stack before is : [5, 4, 17, 18, 1]
PULL 1;	#autogen for var : 5 ; stack before is : [17, 5, 4, 18, 1]
TUCK_CP 1;	#autogen for var : 5
SIR_STORE;	
SIR_ALLOCA;	
TUCK 0;	#autogen for var : 6
PULL 3;	#autogen for var : 18 ; stack before is : [6, 5, 4, 18, 1]
PULL 1;	#autogen for var : 6 ; stack before is : [18, 6, 5, 4, 1]
TUCK_CP 1;	#autogen for var : 6
SIR_STORE;	
PULL 3;	#autogen for var : 1 ; stack before is : [6, 5, 4, 1]
PULL 2;	#autogen for var : 5 ; stack before is : [1, 6, 5, 4]
PULL 2;	#autogen for var : 6 ; stack before is : [5, 1, 6, 4]
PULL 3;	#autogen for var : 4 ; stack before is : [6, 5, 1, 4]
J_REL 0;	#rewritten, 
TUCK 0;	#autogen for var : 4
TUCK 1;	#autogen for var : 6
TUCK 2;	#autogen for var : 5
TUCK 3;	#autogen for var : 1
PULL 0;	#autogen for var : 4 ; stack before is : [4, 6, 5, 1]
TUCK_CP 4;	#autogen for var : 4
SIR_LOAD;	
TUCK 1;	#autogen for var : 9
PULL 0;	#autogen for var : 6 ; stack before is : [6, 9, 5, 1, 4]
TUCK_CP 2;	#autogen for var : 6
SIR_LOAD;	
TUCK 0;	#autogen for var : 8
PULL 1;	#autogen for var : 9 ; stack before is : [8, 9, 6, 5, 1, 4]
PULL 1;	#autogen for var : 8 ; stack before is : [9, 8, 6, 5, 1, 4]
COMP_SLT;	
TUCK 0;	#autogen for var : 10
PULL 1;	#autogen for var : 6 ; stack before is : [10, 6, 5, 1, 4]
PULL 4;	#autogen for var : 4 ; stack before is : [6, 10, 5, 1, 4]
PULL 4;	#autogen for var : 1 ; stack before is : [4, 6, 10, 5, 1]
PULL 4;	#autogen for var : 5 ; stack before is : [1, 4, 6, 10, 5]
PULL 4;	#autogen for var : 10 ; stack before is : [5, 1, 4, 6, 10]
B_Z 1;	#branch to false path, leading to @BB_bla_for.end
J_REL 11;	#rewritten, 
DROP 2;	#removing variable for FALSE path
DROP 2;	#removing variable for FALSE path
J_REL 0;	#rewritten, 
TUCK 0;	#autogen for var : 5
TUCK 1;	#autogen for var : 1
PULL 0;	#autogen for var : 5 ; stack before is : [5, 1]
SIR_LOAD;	
TUCK 1;	#autogen for var : 16
PULL 1;	#autogen for var : 16 ; stack before is : [1, 16]
PULL 1;	#autogen for var : 1 ; stack before is : [16, 1]
J_ABS;	
TUCK 3;	#autogen for var : 1
TUCK 2;	#autogen for var : 4
TUCK 0;	#autogen for var : 5
TUCK 1;	#autogen for var : 6
PUSH 1;	
TUCK 1;	#autogen for var : 19
PULL 0;	#autogen for var : 5 ; stack before is : [5, 19, 6, 4, 1]
TUCK_CP 2;	#autogen for var : 5
SIR_LOAD;	
TUCK 0;	#autogen for var : 11
PULL 1;	#autogen for var : 19 ; stack before is : [11, 19, 5, 6, 4, 1]
PULL 1;	#autogen for var : 11 ; stack before is : [19, 11, 5, 6, 4, 1]
ADD;	
TUCK 0;	#autogen for var : 12
PULL 0;	#autogen for var : 12 ; stack before is : [12, 5, 6, 4, 1]
PULL 1;	#autogen for var : 5 ; stack before is : [12, 5, 6, 4, 1]
TUCK_CP 1;	#autogen for var : 5
SIR_STORE;	
PULL 3;	#autogen for var : 1 ; stack before is : [5, 6, 4, 1]
PULL 1;	#autogen for var : 5 ; stack before is : [1, 5, 6, 4]
PULL 3;	#autogen for var : 4 ; stack before is : [5, 1, 6, 4]
PULL 3;	#autogen for var : 6 ; stack before is : [4, 5, 1, 6]
J_REL 0;	#rewritten, 
TUCK 0;	#autogen for var : 6
TUCK 1;	#autogen for var : 4
TUCK 2;	#autogen for var : 5
TUCK 3;	#autogen for var : 1
PULL 0;	#autogen for var : 6 ; stack before is : [6, 4, 5, 1]
TUCK_CP 1;	#autogen for var : 6
SIR_LOAD;	
TUCK 0;	#autogen for var : 14
PUSH 1;	
TUCK 0;	#autogen for var : 3
PULL 0;	#autogen for var : 3 ; stack before is : [3, 14, 6, 4, 5, 1]
PULL 1;	#autogen for var : 14 ; stack before is : [3, 14, 6, 4, 5, 1]
ADD;	
TUCK 0;	#autogen for var : 15
PULL 0;	#autogen for var : 15 ; stack before is : [15, 6, 4, 5, 1]
PULL 1;	#autogen for var : 6 ; stack before is : [15, 6, 4, 5, 1]
TUCK_CP 1;	#autogen for var : 6
SIR_STORE;	
PULL 3;	#autogen for var : 1 ; stack before is : [6, 4, 5, 1]
PULL 3;	#autogen for var : 5 ; stack before is : [1, 6, 4, 5]
PULL 2;	#autogen for var : 6 ; stack before is : [5, 1, 6, 4]
PULL 3;	#autogen for var : 4 ; stack before is : [6, 5, 1, 4]
J_REL -80;	#rewritten, 
