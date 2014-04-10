229 instructions in 1 segments;
(no label):	229 instructions
push 0x0;	#arg count; start of __$boot
push -0x1;	#arg ptr
call_pc 0xdc;	#call to __$global ; initialize globals
call_pc 0xa8;	#call to main ; transfer control to main
halt;	#end of platform main
j_pc 0x1;	#to : @bb_stackembarrier_wait_while.cond # ; start of stackembarrier_wait
pull 0x1;	#var: 2
pull 0x2;	#var: 3
tuck 0x1;	#var: 3
push 0x0;	
pull 0x1;	#var: 2
tuck_cp 0x0;	#var: 2
pull 0x2;	#var: 83
tuck 0x1;	#var: 83
call_pc 0x87;	#call to stackemlock_acquire ; 
cmp_eq;	
bz 0x3;	#branch to false path, leading to @bb_stackembarrier_wait_while.end
pull 0x2;	#var: 1
j_pc -0xc;	#to @bb_stackembarrier_wait_while.cond # 
j_pc 0x1;	#to @bb_stackembarrier_wait_while.end # 
push 0x1;	
pull 0x1;	#var: 2
tuck_cp 0x1;	#var: 2
tuck 0x1;	#var: 2
add;	
tuck_cp 0x0;	#var: 9
ld 0x0;	
push -0x1;	
pull 0x1;	#var: 10
add;	
tuck_cp 0x0;	#var: 11
pull 0x2;	#var: 9
tuck_cp 0x2;	#var: 9
st 0x0;	
pull 0x2;	#var: 2
tuck_cp 0x1;	#var: 2
call_pc 0xb5;	#call to stackemlock_release ; 
push 0x1;	
pull 0x1;	#var: 11
cmp_slt;	
pull 0x3;	#var: 3
tuck 0x2;	#var: 3
bz 0x6;	#branch to false path, leading to @bb_stackembarrier_wait_if.else
pull 0x2;	#var: 9
tuck 0x1;	#var: 9
pull 0x3;	#var: 1
tuck 0x2;	#var: 1
j_pc 0x4;	#to @bb_stackembarrier_wait_if.then # 
drop 0x0;	#var: 2
drop 0x1;	#var: 9
j_pc 0x27;	#to @bb_stackembarrier_wait_if.else # 
push 0x2;	
add;	
ld 0x0;	
pull 0x1;	#var: 9
st 0x0;	
push 0x0;	
pull 0x1;	#var: 1
j_pc 0x1;	#to : @bb_stackembarrier_wait_for.body # 
pull 0x1;	#var: 18
pull 0x2;	#var: 3
tuck 0x1;	#var: 3
tuck_cp 0x0;	#var: 18
pull 0x2;	#var: 3
tuck 0x1;	#var: 3
sll 0x19;	
pull 0x1;	#var: 3
tuck_cp 0x1;	#var: 3
tuck 0x1;	#var: 3
pull 0x3;	#var: 18
tuck 0x2;	#var: 18
add;	
call_pc 0x91;	#call to stackemlock_release ; 
push 0x6e;	
push 0x1;	
pull 0x2;	#var: 18
add;	
tuck_cp 0x0;	#var: 17
pull 0x2;	#var: 76
tuck 0x1;	#var: 76
cmp_eq;	
pull 0x3;	#var: 1
tuck 0x1;	#var: 1
bz 0x4;	#branch to false path, leading to @bb_stackembarrier_wait_for.body
drop 0x1;	#var: 17
drop 0x1;	#var: 3
j_pc 0x2;	#to @bb_stackembarrier_wait_if.end # 
j_pc -0x1c;	#to @bb_stackembarrier_wait_for.body # 
j;	
call_pc 0x13;	#call to stackem_getthreadid ; 
sll 0x19;	
add;	
pull 0x1;	#var: 1
j_pc 0x1;	#to : @bb_stackembarrier_wait_while.cond12 # 
pull 0x1;	#var: 25
push 0x0;	
pull 0x1;	#var: 25
tuck_cp 0x0;	#var: 25
pull 0x2;	#var: 77
tuck 0x1;	#var: 77
call_pc 0x31;	#call to stackemlock_acquire ; 
cmp_eq;	
pull 0x2;	#var: 1
tuck 0x1;	#var: 1
bz 0x2;	#branch to false path, leading to @bb_stackembarrier_wait_if.end
j_pc -0xb;	#to @bb_stackembarrier_wait_while.cond12 # 
drop 0x1;	#var: 25
j_pc -0x13;	#to @bb_stackembarrier_wait_if.end # 
push_thread;	#; start of stackem_getthreadid
pull 0x1;	
j;	
pull 0x1;	#var: 29; start of stackembarrier_register
j_pc 0x1;	#to : @bb_stackembarrier_register_while.cond # 
push 0x0;	
pull 0x1;	#var: 29
tuck_cp 0x0;	#var: 29
pull 0x2;	#var: 74
tuck 0x1;	#var: 74
call_pc 0x1f;	#call to stackemlock_acquire ; 
cmp_eq;	
bz 0x2;	#branch to false path, leading to @bb_stackembarrier_register_while.end
j_pc -0x8;	#to @bb_stackembarrier_register_while.cond # 
j_pc 0x1;	#to @bb_stackembarrier_register_while.end # 
push 0x1;	
pull 0x1;	#var: 29
tuck_cp 0x1;	#var: 29
tuck 0x1;	#var: 29
add;	
tuck_cp 0x0;	#var: 33
ld 0x0;	
push 0x1;	
pull 0x1;	#var: 34
add;	
pull 0x1;	#var: 33
st 0x0;	
push 0x2;	
pull 0x1;	#var: 29
tuck_cp 0x1;	#var: 29
tuck 0x1;	#var: 29
add;	
tuck_cp 0x0;	#var: 36
ld 0x0;	
push 0x1;	
pull 0x1;	#var: 37
add;	
pull 0x1;	#var: 36
st 0x0;	
call_pc 0x46;	#call to stackemlock_release ; 
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
fnc_st 0x0;	
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
drop 0x1;	#var: 40; start of main
drop 0x1;	#var: 41
push 0x0;	
push -0x6000;	
st 0x0;	
push 0x0;	
push -0x5fff;	
st 0x0;	
push 0x0;	
push -0x5ffe;	
st 0x0;	
push -0x6000;	
call_pc -0x4b;	#call to stackem_getthreadid ; 
sll 0x19;	
bor;	
push 0x0;	
pull 0x1;	#var: 50
st 0x0;	
push 0x0;	
pull 0x1;	#var: 39
j_pc 0x1;	#to : @bb_main_for.body # 
pull 0x1;	#var: 52
call_pc 0x23;	#call to stackem_nop ; 
push 0x3;	
pull 0x1;	#var: 52
push 0x1;	
pull 0x1;	#var: 52
add;	
tuck_cp 0x0;	#var: 51
pull 0x2;	#var: 60
tuck 0x1;	#var: 60
cmp_eq;	
pull 0x2;	#var: 39
tuck 0x1;	#var: 39
bz 0x3;	#branch to false path, leading to @bb_main_for.body
drop 0x1;	#var: 51
j_pc 0x2;	#to @bb_main_for.end13 # 
j_pc -0x10;	#to @bb_main_for.body # 
push -0x6000;	
call_pc -0x63;	#call to stackembarrier_register ; 
push -0x6000;	
push -0x6000;	
call_pc -0xd0;	#call to stackembarrier_wait ; 
push 0x0;	
pull 0x1;	#var: 39
j;	
pull 0x1;	#var: 111; start of stackemlock_release
push 0x0;	
pull 0x1;	#var: 111
fnc_st_ra_noack 0x0;	
j;	
j;	#return to boot routine; start of __$global
drop 0x0;	#var: 299; start of stackem_halt
halt;	
pull 0x1;	#var: 11; start of stackemlock_allocate
call_pc -0x9;	#call to stackemlock_release ; 
j;	
j;	#; start of stackem_nop
