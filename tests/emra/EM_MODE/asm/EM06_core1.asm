PUSH 0;			#@entry
PUSH 1;			#0x00000001
ST_EM 2 0;
PUSH 0;
PULL_CP 0;	#@condition
PUSH 20;
COMP_UGT;
B_NZ 2;		#branch to @body
DROP 0;			#@return
HALT;
PUSH 1;			#@body, 0x00000001
LD_EM 1 0;
PUSH 1;
ADD;
PUSH 1;
ST_EM 2 0;		#0x00000001
PUSH 1;			#@for.end
ADD;
J_REL -15;
