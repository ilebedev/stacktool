# Lock address
push 0;
sethi 0x1000;
#[L]

# Initialize lock
push 0;
pull_cp 1;
st_ra 0;

# Initialize counter
push 2;
push 0;
pull_cp 1;
st_ra;

#[C, L]

# iteration counter=10;
push 25;

#[i, C, L]

#@iter
pull_cp 0;
bz 15;	#halt;

#@lock
pull_cp 2;
call_pc 17;	#@aquire;
bz -2;

# process counter (THIS ONE COUNTS DOWN)
#[i, C, L]
pull_cp 1;	#[C, i, C, L]
ld_ra 0;	#[Mc, i, C, L]
push -1;	#[1 Mc, i, C, L]
add;		#[Mc-, i, C, L]
pull_cp 2;	#[C, Mc-, i, C, L]
st_ra 0;	#[i, C, L]

# free
pull_cp 2;
call_pc 25;	#@free;

# decrement
push -1;
add;
j_pc -15;	#@iter;

# @halt
drop 2;
drop 1;
drop 0;
halt;

#@aquire
#bool acquire(imt address){
	main2aux;
	
	pull_cp 0;	#[a,a]
	pull_cp 0;	#[a,a,a]
	fnc_ld_ra 0;	#[v,a,a]
	
	bnz 8;	#@end_outer
	# available remotely	[a,a]
	# Migrate, try to reserve.
	fnc_ld_rsv 0 2 0;	#[v,a]
	bnz 7;	#@end_inner
	# available locally, reserved.
	# lock
	push 1;			#[1, a]
	pull 1;			#[a, 1]
	st_cond 0 2 0;
	# return result (1 if success, 0 otherwise)
	
	aux2main;
	j;
	
#- @end_outer [a,a]
	drop 0;
#- @end_inner [a]
	drop 0;
	push 0;
	aux2main;
	j;
#}

#@free
#void free(int address){
	main2aux;
	push 0;
	pull 1;
	fnc_st_ra 0;
	
	aux2main;
	j;
#}



