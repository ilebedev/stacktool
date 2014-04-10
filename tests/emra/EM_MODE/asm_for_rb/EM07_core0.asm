push 0
push 0
st_em 0x0,2,0
push 0
push 0
sethi 0x0200
st_em 0x0,2,0
push 0
pull_cp 0
push 20
cmp_ule
bz 2
drop 0
halt
push 0
ld_em 0x0,2,0
push 1
add
push 0
st_em 0x0,2,0
push 0
sethi 0x0200
pull_cp 0
ld_em 0x0,2,0
push 1
add
pull 1
st_em 0x0,2,0
push 1
add
j -23