92 instructions in 1 segments;
(no label):	92 instructions
push 0x0;	#arg count; start of __$boot
push -0x1;	#arg ptr
call_pc 0x45;	#call to __$global ; initialize globals
call_pc 0x1b;	#call to main ; transfer control to main
halt;	#end of platform main
push_thread;	#; start of stackem_getthreadid
pull 0x1;	
j;	
main2aux;	#; start of stackemlock_acquire
pull_cp 0x0;	
pull_cp 0x0;	
fnc_ld_ra 0x0;	
bnz 0x9;	
fnc_ld_rsv_em 0x0;	
bnz 0x8;	
push 0x1;	
pull 0x1;	
st 0x0;	
push 0x1;	
aux2main;	
j;	
drop 0x0;	
drop 0x0;	
push 0x0;	
aux2main;	
j;	
pull 0x1;	#var: 211; start of stackemlock_test
fnc_ld_ra 0x0;	
pull 0x1;	#var: 299
j;	
drop 0x1;	#var: 2; start of main
drop 0x1;	#var: 3
push 0x0;	
push -0x6000;	
st 0x0;	
push 0x0;	
pull 0x1;	#var: 1
j_pc 0x1;	#to : @bb_main_while.cond.preheader # 
j_pc 0x1;	#to : @bb_main_while.cond # 
pull 0x1;	#var: 8
push 0x0;	
push -0x6000;	
call_pc -0x22;	#call to stackemlock_acquire ; 
cmp_eq;	
bz 0x3;	#branch to false path, leading to @bb_main_while.end
pull 0x1;	#var: 1
j_pc -0x7;	#to @bb_main_while.cond # 
j_pc 0x1;	#to @bb_main_while.end # 
push -0x6000;	
call_pc 0x11;	#call to stackemlock_release ; 
push 0x1;	
pull 0x1;	#var: 8
add;	
push 0xa;	
pull 0x1;	#var: 7
tuck_cp 0x1;	#var: 7
cmp_eq;	
pull 0x2;	#var: 1
tuck 0x1;	#var: 1
bz 0x3;	#branch to false path, leading to @bb_main_while.cond.preheader
drop 0x1;	#var: 7
j_pc 0x2;	#to @bb_main_for.end # 
j_pc -0x18;	#to @bb_main_while.cond.preheader # 
push 0x0;	
pull 0x1;	#var: 1
j;	
pull 0x1;	#var: 111; start of stackemlock_release
push 0x0;	
pull 0x1;	#var: 111
fnc_st_ra_noack 0x0;	
j;	
j;	#return to boot routine; start of __$global
main2aux;	#; start of stackemlock_acquire_naive
pull_cp 0x0;	
fnc_ld_rsv_em 0x0;	
bnz 0x6;	
push 0x1;	
pull 0x1;	
st 0x0;	
push 0x1;	
aux2main;	
j;	
drop 0x0;	
push 0x0;	
aux2main;	
j;	
drop 0x0;	#var: 299; start of stackem_halt
halt;	
j;	#; start of stackem_nop
pull 0x1;	#var: 11; start of stackemlock_allocate
call_pc -0x18;	#call to stackemlock_release ; 
j;	
