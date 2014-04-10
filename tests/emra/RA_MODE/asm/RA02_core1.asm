#*****************************************
#                                         
#  EM^2 basic testbench 					
#
#  THREAD 1 (CORE 1)
#                                         
#  RA02										
#  - under RA-only mode							
#  - 3 threads, each running on core 0, 1 and 2 
#    (main0, main1 and main2, respectively)	    
#  - Test remote access contention				
#                                         
#*****************************************

# EXPECTED RESULT (MEMORY) AFTER EXECUTION  
#----------------------------------------
#...
#0x02000000 : 10  (updated by thread 0)
#0x02000001 : 10  (updated by thread 1)
#0x02000002 : 10  (updated by thread 2)
#...
#---------------------------------------

#(RA)
#@BB_entry
PUSH 0;
PUSH 1;
SETHI 0x0200;
ST_RA;
PUSH 0;
#J_REL @BB_condition # not needed
#(0,RA)
		
#(i,RA)
#@BB_condition
PULL_CP 0;
#(i,i,RA)
PUSH 10;
COMP_ULE;
#(b,i,RA)
B_Z 2;	#@BB_for.body;
#J_REL @BB_return; # not needed
#(i,RA)

#(i,RA)
#@BB_return[]{
DROP 0;
#(RA)
HALT;
#()

#(i,RA)
#@BB_for.body
PUSH 1;
SETHI 0x0200;
LD_RA;
#(M,i,RA)
PUSH 1;
ADD;
#(M++,i,RA)
PUSH 1;
SETHI 0x0200;
ST_RA;
#(i,RA)
#J_REL @BB_for.post;	# not needed
#(i,RA)

#(i,RA)	
#@BB_for.post[]
PUSH 1;
ADD;
#(i++,RA)
J_REL -17;	#@BB_condition;
#(i++,RA)


