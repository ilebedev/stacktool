package stack.python;

import stack.asm.Program;
import stack.compiler.Compiler;
import stack.compiler.analysis.FunctionAnalysis;
import stack.compiler.model.TransferSets;
import stack.sir.model.Function;
import stack.sir.model.Module;

public class compiler {
	public static Program compile(Module M){
		return Compiler.Compile(M);
	}
	
	public static TransferSets getTransferSets(Function F){
		return FunctionAnalysis.computeTransferSets(F);
	}
}
