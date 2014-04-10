push 1
push 1
sethi 0x0400
st_em 0x0,2,0
push 0
push 0
pull_cp 0
push 100
cmp_ult
bz 5
drop 0
push 3
sethi 0x0400
st_em 0x0,2,0
halt
tuck_cp 1
add
pull 1
push 1
add
j -15