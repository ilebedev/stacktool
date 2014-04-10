PUSH 0;       #	RA				
PUSH 0;       #	0	RA			
ST_EM 2 0;    #	0	0	RA		
PUSH 1;       #	RA				
PUSH 0;       #	1	RA			
SETHI 0x0200; #	0	1	RA		
ST_EM 2 0;    #	0x2	1|	RA		# migrate
PUSH 2;       #	|RA				
PUSH 0;       #	2|	RA			
SETHI 0x0400; #	0	2|	RA		
ST_EM 2 0;    #	0x4	2|	RA		# migrate
PUSH 3;       #	|RA				
PUSH 0;       #	3|	RA			
SETHI 0x0600; #	0	3|	RA		
ST_EM 2 0;    #	0x6	3|	RA		
PUSH 0;       #	|RA				
LD_EM 1 0;    #	0|	RA			
PUSH 1;       #	M0|	RA			
ADD;          #	1	M0|	RA		
PUSH 0;       #	M01|	RA			
ST_EM 2;      #	0	M01|RA		# migrate
PUSH 0;       #	|RA				
LD_EM 1 0;    #	0|	RA			
PUSH 1;       #	M0|	RA			
SETHI 0x0200; #	1	M0|	RA		
TUCK_CP 1;    #	0x2	M0|	RA		
ST_EM 3 0;    #	0x2	M0	0x2|RA	# migrate
LD_EM 1 0;    #	0x2|	RA			
PUSH 1;       #	M2|	RA			
SETHI 0x0400; #	1	M2|	RA		
ST_EM 2 0;    #	0x4	M2|	RA		# migrate
HALT;         #	RA
