push 0
push 0
st_ra 0x0
push 1
push 0
sethi 0x0200
st_ra 0x0
push 2
push 0
sethi 0x0400
st_ra 0x0
push 3
push 0
sethi 0x0600
st_ra 0x0
push 0
push 0
sethi 0x0200
ld_ra 0x0
pull_cp 1
push 1
add
st_ra 0x0
push 0
sethi 0x0400
ld_ra 0x0
pull_cp 1
push 2
add
st_ra 0x0
push 0
sethi 0x0600
ld_ra 0x0
pull_cp 1
push 3
add
st_ra 0x0
push 0
ld_ra 0x0
push 4
add
pull 1
push 4
add
st_ra 0x0
halt
