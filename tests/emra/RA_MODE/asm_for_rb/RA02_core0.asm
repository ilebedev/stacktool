push 0
push 0
sethi 0x0200
st_ra 0x0
push 0
pull_cp 0
push 10
cmp_ule
bz 2
drop 0
halt
push 0
sethi 0x0200
ld_ra 0x0
push 1
add
push 0
sethi 0x0200
st_ra 0x0
push 1
add
j -17
