push 0
push 0
sethi 0x0400
st_em 0x0,2,0
push 0
pull_cp 0
push 20
cmp_ult
bz 2
drop 0
halt
pull_cp 0
push 0
sethi 0x0400
tuck_cp 1
ld_em 0x0,2,0
add
pull 1
st_em 0x0,4,0
push 1
add
j -17