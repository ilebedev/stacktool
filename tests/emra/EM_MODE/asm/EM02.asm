PUSH 1;       #	RA								
PUSH 0;       #	1	RA							
ST_EM 2 0;    #	0	1	RA						
PUSH 0;       #	RA								
LD_EM 1 0;    #	0	RA							
PUSH 0;       #	M0	RA							
SETHI 0x0200; #	0	M0	RA						
ST_EM 2 0;    #	0x2	M0	RA						
PUSH 0;       #	RA								
PUSH 1;       #	0	RA							
PUSH 2;       #	10	RA							
PUSH 3;       #	210	RA							
PUSH 4;       #	3210	RA						
PUSH 5;       #	43210	RA						
PUSH 6;       #	543210	RA						
PUSH 7;       #	6543210	RA						
PUSH 8;       #	76543210	RA					
PUSH 9;       #	876543210	RA					
PUSH 10;      #	9876543210	RA					
PUSH 11;      #	09876543210	RA					
PUSH 12;      #	109876543210	RA				
PUSH 13;      #	2109876543210	RA				
PUSH 14;      #	32109876543210	RA				
PUSH 15;      #	432109876543210	RA				
PUSH 16;      #	5432109876543210	RA			
PUSH 17;      #	65432109876543210	RA			
PUSH 0;       #	765432109876543210	RA			
LD_EM 1 0;    #	0	765432109876543210	RA		
PUSH 1;       #	M0	765432109876543210	RA		
ADD;          #	1	M0	765432109876543210	RA	
PUSH 0;       #	M01	765432109876543210	RA		
ST_EM 2 0;    #	0	M01	765432109876543210	RA	
DROP 0;       #	765432109876543210	RA			
DROP 0;       #	65432109876543210	RA			
DROP 0;       #	5432109876543210	RA			
DROP 0;       #	432109876543210	RA				
DROP 0;       #	32109876543210	RA				
DROP 0;       #	2109876543210	RA				
DROP 0;       #	109876543210	RA				
DROP 0;       #	09876543210	RA					
DROP 0;       #	9876543210	RA					
DROP 0;       #	876543210	RA					
DROP 0;       #	76543210	RA					
DROP 0;       #	6543210	RA						
DROP 0;       #	543210	RA						
DROP 0;       #	43210	RA						
DROP 0;       #	3210	RA						
DROP 0;       #	210	RA							
DROP 0;       #	10	RA							
DROP 0;       #	0	RA							
HALT;         #	RA								