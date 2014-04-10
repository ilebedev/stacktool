#// program for thread 0 (on core 0) -- keeps running on its native core 0 (all local accesses)
#int main0() {
#	*(0x00000000) = 0; // local access
#
#	for (int i = 0; i < 20; i++) {
#		*(0x00000000) += 1; // local accesses
#	}
#}

PUSH 0;			#@entry
PUSH 0;
ST_EM 2 0;
PUSH 0;
PULL_CP 0;	#@condition
PUSH 20;
COMP_UGT;
B_NZ 2;		#branch to @body
DROP 0;			#@return
HALT;
PUSH 0;			#@body
LD_EM 1 0;
PUSH 1;
ADD;
PUSH 0;
ST_EM 2 0;
PUSH 1;			#@for.end
ADD;
J_REL -15;
