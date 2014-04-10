package stack.compiler.analysis;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import stack.compiler.model.OutputSets;
import stack.compiler.model.TransferSets;
import stack.sir.model.BasicBlock;
import stack.sir.model.Expression;
import stack.sir.model.Function;

public class FunctionAnalysis {
	/**
	For each BB, enumerates the variables it defines (some will be local!).
	@param F - function to run the pass on
	**/
	public static Hashtable<BasicBlock, HashSet<Integer>> computeDefines(Function F) {
		Hashtable<BasicBlock, HashSet<Integer>> defines = new Hashtable<BasicBlock, HashSet<Integer>>();
		
		for (BasicBlock BB : F.getBasicBlocks()){
			HashSet<Integer> def = new HashSet<Integer>();
			
			for (Expression E : BB.getExpressions()){
				for (int variable : E.getOutputStack()){
					def.add(variable);
				}
			}
			
			defines.put(BB, def);
		}
		
		return defines;
	}
	
	/**
	For each BB, enumerates the variables it uses (some will be defined locally too!).
	@param F - function to run the pass on
	**/
	public static Hashtable<BasicBlock, HashSet<Integer>> computeUses(Function F) {
		Hashtable<BasicBlock, HashSet<Integer>> uses = new Hashtable<BasicBlock, HashSet<Integer>>();
		
		for (BasicBlock BB : F.getBasicBlocks()){
			HashSet<Integer> use = new HashSet<Integer>();
			
			for (Expression E : BB.getExpressions()){
				for (int variable : E.getInputStack()){
					// Except constnats! Constants are later filled on-demand
					if (!Expression.isConstant(variable)){
						// TODO: what about renamed variables?
						use.add(variable);
					}
				}
			}
			
			uses.put(BB, use);
		}
		
		return uses;
	}
	
	public static Hashtable<BasicBlock, HashSet<BasicBlock>> enumerateSources(Function F) {
		// Each BB has zero, one, or two targets. If SWITCH is ever implemented, this number will increase.
		// A source of BB A is simply any basic block that has BB A in its target list
		
		Hashtable<BasicBlock, HashSet<BasicBlock>> sources = new Hashtable<BasicBlock, HashSet<BasicBlock>>();
		
		// Entry basic block might not have any sources (unless there's a loop)
		// Make sure there's an entry for it!
		sources.put(F.getEntryBasicBlock(), new HashSet<BasicBlock>());
		
		for (BasicBlock srcBB : F.getBasicBlocks()){
			Expression terminal = srcBB.getTerminal();
			
			for(String label : terminal.getTargetLabels()){
				BasicBlock targetBB = BasicBlock.lookup(label);
				
				if (null == sources.get(targetBB)){
					sources.put(targetBB, new HashSet<BasicBlock>());
				}
				
				sources.get(targetBB).add(srcBB);
			}
		}
		
		return sources;
	}
	
	public static Hashtable<BasicBlock, HashSet<BasicBlock>> enumerateTargets(Function F) {
		Hashtable<BasicBlock, HashSet<BasicBlock>> targets = new Hashtable<BasicBlock, HashSet<BasicBlock>>();
		
		for (BasicBlock srcBB : F.getBasicBlocks()){
			Expression terminal = srcBB.getTerminal();
			
			targets.put(srcBB, new HashSet<BasicBlock>());
			
			if (null != terminal.getTargetLabels()){
				for(String label : terminal.getTargetLabels()){
					BasicBlock targetBB = BasicBlock.lookup(label);
					
					targets.get(srcBB).add(targetBB);
				}
			}
		}
		
		return targets;
	}
	
	public static TransferSets computeTransferSets(Function F) {
		// The transfer stack required by a basic block contains all live variables at the point of entry to the BB
		// These are the variables required by the BB and everything reachable from BB.
		
		// Investigate control flow
		Hashtable<BasicBlock, HashSet<BasicBlock>> targets = FunctionAnalysis.enumerateTargets(F);
		Hashtable<BasicBlock, HashSet<Integer>> uses = computeUses(F);
		Hashtable<BasicBlock, HashSet<Integer>> defines = computeDefines(F);
		
		TransferSets unionInSet = new TransferSets();
		
		// Algorithm:
		// For each BB:
		for (BasicBlock BB : F.getBasicBlocks()){
			//Need to keep track of paths, not just net sets of variables
			
			//1). Enumerate paths using a queue of paths
			HashSet<LinkedList<BasicBlock>> paths = new HashSet<LinkedList<BasicBlock>>();
			LinkedList<LinkedList<BasicBlock>> queue = new LinkedList<LinkedList<BasicBlock>>();
			
			LinkedList<BasicBlock> originPath = new LinkedList<BasicBlock>();
			originPath.add(BB);
			queue.add(originPath);
			
			while (!queue.isEmpty()){
				//start with rbb. remove
				LinkedList<BasicBlock> path = queue.removeFirst();
				
				// Catalog the path
				paths.add(path);
				
				// Add all sub-paths
				//etc. Work with last entry of the Path.
				BasicBlock endpointBB = path.getLast();
				for (BasicBlock nextBB : targets.get(endpointBB)){
					// Check to make sure paths are acyclic (break backedge.)
					if (!path.contains(nextBB)){
						//add [rbb->t1], [rbb, t2]
						LinkedList<BasicBlock> newPath = new LinkedList<BasicBlock>(path);
						newPath.add(nextBB);
						queue.add(newPath);
					}
				}
			}
				
			//2). For each path [bb1, bb2, ... bbn]
			for (LinkedList<BasicBlock> path : paths){
				BasicBlock prevBB = null;
				HashSet<Integer> pathDefines = new HashSet<Integer>();
				HashSet<Integer> pathRequires = new HashSet<Integer>();
				
				for (BasicBlock curBB : path){
					// - Enumerate defines
					pathDefines.addAll(defines.get(curBB));
					
					// - if not the first element on the path,					
					if (prevBB != null){
						// Add phi defines
						pathDefines.addAll(PhiRenaming.getPhiDefines(curBB));
					}
					
					//	- Enumerate path required variables (uses - defines)
					for (int var : uses.get(curBB)){
						// if not the first element on the path,
						// phi-rename all uses
						if (prevBB != null){
							var = PhiRenaming.renameVariable(prevBB, curBB, var);
						}
						
						// - Compute required sets = uses - defines
						if (!pathDefines.contains(var)){
							pathRequires.add(var);
						}
					}
					
					// Also phi-rename phi table requirements
					if (prevBB != null){
						for (int var : PhiRenaming.getPhiDefines(curBB)){
							// rename the variable
							var = PhiRenaming.renameVariable(prevBB, curBB, var);
							
							// - Compute required sets = uses - defines
							if (!pathDefines.contains(var)){
								pathRequires.add(var);
							}
						}
					}
					
					prevBB = curBB;
				}
				
				// Get rid of constants! They don't need to be on the transfer stack
				HashSet<Integer> finalPathRequires = new HashSet<Integer>();
				for (int var : pathRequires){
					if (!Expression.isConstant(var)){
						finalPathRequires.add(var);
					}
				}
				
				// - add required set to union
				if (!unionInSet.containsKey(BB)){
					unionInSet.put(BB, new HashSet<Integer>());
				}
				unionInSet.get(BB).addAll(finalPathRequires);
			}
		}
		
		//3). Required set of bb is the union of required sets of each path, as computed above
		return unionInSet;
	}
	
	public static OutputSets computeOutputSets(Function F, TransferSets transferSets){
		OutputSets outputSets = new OutputSets();
		
		/*
		for each target BB
			For each source BB corresponding to the target
		   
		* 	rename the set against the phi table of the target basic block
		*   (leave constants intact)
		*/
		
		Hashtable<BasicBlock, HashSet<BasicBlock>> sources = enumerateSources(F);
		
		if (!transferSets.isEmpty()){
			for (BasicBlock targetBB : transferSets.keySet()){
				if (!sources.isEmpty()){
					for (BasicBlock sourceBB : sources.get(targetBB)){
						HashSet<Integer> transferSet = transferSets.get(targetBB);
						
						// Rename the transfer set
						// Note: no! we do not rename here - we rename as needed, as late as possible to allow back-annotation.
						//HashSet<Integer> phiTransferSet = PhiRenaming.renameTransferSet(sourceBB, targetBB, transferSet);
						
						// Store the renamed transfer set
						if (!outputSets.containsKey(sourceBB)){
							outputSets.put(sourceBB, new Hashtable<BasicBlock, HashSet<Integer>>());
						}
						
						outputSets.get(sourceBB).put(targetBB, transferSet /*instead of phiTransferSet*/);
					}
				}
			}
		}
		
		// TODO do this right!
		
		return outputSets;
	}

	public static LinkedList<BasicBlock> enumerateLeafFirst(Function F){
		Hashtable<BasicBlock, HashSet<BasicBlock>> targets = enumerateTargets(F);
		Hashtable<BasicBlock, HashSet<BasicBlock>> sources = enumerateSources(F);
		LinkedList<BasicBlock> ordering = new LinkedList<BasicBlock>();
		LinkedList<BasicBlock> queue = new LinkedList<BasicBlock>();
		
		// This is supposed to enumerate basic blocks in F in leaf-first order
		// This means that blocks with no targets are to be placed first, followed by their prececessors, etc.
		// The entry block is to be placed last.
		
		// Approach:
		// Enumerate the targets
		// Add all bbs with no targets to the end of the queue.
		
		// While the queue is not empty:
		//  pop a BB off the queue
		//  add BB to the ordering
		//  put all of BB's sources at the end of the queue
		//  Invariant: all of BB's targets have been visited!
		
		// IMPLEMENTATION:
		// Add all bbs with no targets to the end of the queue.
		for (BasicBlock bb : F.getBasicBlocks()){
			if (targets.get(bb).isEmpty()){
				queue.addLast(bb);
			}
		}
		
		while (!queue.isEmpty()){
			BasicBlock bb = queue.removeFirst();
			
			//  add BB to the ordering
			ordering.addLast(bb);
			
		//  put all of BB's sources at the end of the queue
			for (BasicBlock sbb : sources.get(bb)){
				if ((!ordering.contains(sbb)) && (!queue.contains(sbb))){
					queue.addLast(sbb);
				}
			}
			
			//  Invariant: all of BB's targets have been visited!
			//  The only exception is if the BB targets itself
			for (BasicBlock tbb : targets.get(bb)){
				if (tbb != bb){
					// This is not possible for loops:
					//assert ordering.contains(tbb);
				}
			}
		}
		
		// Make sure everything's been scheduled
		assert ordering.size() == F.getBasicBlocks().size();
		
		return ordering;
	}
}