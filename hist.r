
# Parse arguments
# args: 
# in1, in2, in3, ...

args = commandArgs(trailingOnly = TRUE)
print(args)

trace = read.table("trace.txt", header=F, sep="~");
names(trace)=c("inst");

# Find and mark instruction types\
trace$p = with(trace, grepl("PULL .*", inst));
trace$pc = with(trace, grepl("PULL_CP .*", inst));
trace$t = with(trace, grepl("TUCK .*", inst));
trace$tc = with(trace, grepl("TUCK_CP .*", inst));
trace$s = with(trace, grepl("SWAP .*", inst));
trace$d = with(trace, grepl("DROP .*", inst));
trace$o = !(trace$p | trace$pc | trace$t | trace$tc | trace$s | trace$d)

# Count each instruction type
ni = sum(!is.na(trace$inst));
np = sum(trace$p);
npc = sum(trace$pc);
nt = sum(trace$t);
ntc = sum(trace$tc);
ns = sum(trace$s);
nd = sum(trace$d);
no = sum(trace$o);

# Calculate ratios of each instruction type
rp = np/ni;
rpc = npc/ni;
rt = nt/ni;
rtc = ntc/ni;
rs = ns/ni;
rd = nd/ni;
ro = no/ni;

# Create a table of histogram values to plot
benchmarkNames = c("factorial")
hdata = data.frame(benchmarkNames)
names(hdata) = c("trace")
hdata$p=c(rp)
hdata$pc=c(rpc)
hdata$t=c(rt)
hdata$tc=c(rtc)
hdata$s=c(rs)
hdata$d=c(rd)
hdata$o=c(ro)
hdata$trace=NULL

# Expand to the right to make room for the legend
par(xpd=T, mar=par()$mar+c(0,0,0,4))

# Graph autos (transposing the matrix) using heat colors,  
# put 10% of the space between each bar, and make labels  
# smaller with horizontal y-axis labels
barplot(	t(hdata),
			main="Trace",
			ylab="% of instructions",
			col=heat.colors(7),
			space=0.1,
			cex.axis=0.8,
			las=1,
			names.arg=benchmarkNames,
			cex=0.8)
   
# Place the legend at (6,30) using heat colors
legend(1.15, 1, c("PULL", "PULL_CP", "TUCK", "TUCK_CP", "SWAP", "DROP", "(non-stack)"), cex=0.8, heat.colors(7))
  
# Restore default clipping rect
par(mar=c(5, 4, 4, 2) + 0.1)

