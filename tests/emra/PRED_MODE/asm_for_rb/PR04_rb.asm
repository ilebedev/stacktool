push 0;
push 0;
st;
push 0;
push 1;
st;
push 0;
push 0;
sethi 0x0200;
st;
push 0;
push 0;
push 0;
pull_cp 2;
push 10;
cmp_ugt;
bz 49;
drop 1;
push 0;
tuck 1;
pull_cp 2;
push 5;
cmp_ugt;
bz 4;
drop 0;
push 2;
j_pc 3;
drop 0;
push 1;
pull_cp 1;
pull_cp 1;
cmp_ugt;
bz 14;
push 0;
sethi 0x0200;
pull_cp 0;
ld;
push 1;
add;
tuck 1;
st;
pull 1;
push 1;
add;
tuck 1;
j_pc -16;
push 0;
pull_cp 0;
ld;
push 1;
add;
tuck 1;
st;
push 1;
pull_cp 0;
ld;
push 1;
add;
tuck 1;
st;
pull 2;
push 1;
add;
tuck 2;
j_pc -51;
drop 2;
drop 1;
drop 0;
halt;