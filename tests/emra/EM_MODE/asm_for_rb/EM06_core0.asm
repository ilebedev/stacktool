push 0
push 0
st_em 0x0,2,0
push 0
pull_cp 0
push 20
cmp_ugt
bnz 2
drop 0
halt
push 0
ld_em 0x0,2,0
push 1
add
push 0
st_em 0x0,2,0
push 1
add
j -15
