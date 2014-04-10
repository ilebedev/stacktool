PUSH 0;       #	RA                  ()
MAIN2AUX;     #	0	RA              ()
PUSH 1;       #	RA                  (0)
MAIN2AUX;     #	1	RA              (0)
PUSH 2;       #	RA                  (10)
MAIN2AUX;     #	2	RA              (10)
PUSH 3;       #	RA                  (210)
MAIN2AUX;     #	3	RA              (210)
PUSH 4;       #	RA                  (3210)
MAIN2AUX;     #	4	RA              (3210)
PUSH 1;       #	RA                  (43210)
PUSH 0;       #	1	RA				(43210)
ST_EM 2 0;    #	0	1	RA          (43210)
PUSH 0;       #	RA                  (43210)
LD_EM 1 0;    #	0	RA              (43210)
PUSH 0;       #	M0	RA              (43210)
SETHI 0x0200; #	0	M0	RA          (43210)
ST_EM 2 0;    #	0x2	M0	RA          (43210)
AUX2MAIN;     #	RA                  (43210)
DROP 0;       #	4	RA              (3210)
AUX2MAIN;     #	RA                  (3210)
DROP 0;       #	3	RA              (210)
AUX2MAIN;     #	RA                  (210)
DROP 0;       #	2	RA              (10)
AUX2MAIN;     #	RA                  (10)
DROP 0;       #	1	RA              (0)
AUX2MAIN;     #	RA                  (0)
DROP 0;       #	0	RA              ()
PUSH 0;       #	RA
LD_EM 1 0;    #	0	RA
PUSH 1;       #	M0	RA
ADD;          #	1	M0	RA
PUSH 0;       #	M01	RA
ST_EM 2 0;    #	0	M01	RA
HALT;    	  #	RA
