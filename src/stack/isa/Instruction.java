package stack.isa;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



import stack.excetpion.OverflowException;
import stack.excetpion.ParserException;
import stack.excetpion.SimulatorException;
import stack.excetpion.UnderflowException;
import stack.isa.core.*;
import stack.isa.em2.load.*;
import stack.isa.em2.store.*;
import stack.isa.sir.*;
import stack.simulator.Context;
import stack.simulator.machine.models.CoreModel;

public abstract class Instruction {
	String comment;

	public String getComment(){
		if (comment == null){
			comment = "";
		}
		return comment;
	}
	
	public void appendComment(String comment){
		this.comment = this.comment + "; " + comment;
	}
	
	public Instruction(String comment){
		this.comment = comment;
		if (comment == null){
			comment = "";
		}
	}

	@Override
	public abstract String toString();
	public abstract int toBinary();
	public abstract void execute(Context thread, CoreModel core) throws SimulatorException, UnderflowException, OverflowException;

	public static String get_string(StackOP op, String comment){
		return op.getName() + ";\t" + ((comment.length() > 0)? ("#" + comment) : "");
	}
	
	public static String get_string(StackOP op, int imm16, String comment){
		String imm = String.valueOf(imm16);
		if (imm16>>15==1){
			imm = "-"+String.valueOf(((~imm16)+1)&0xFFFF);
		}
		return op.getName() + " " + imm + ";\t" + ((comment.length() > 0)? ("#" + comment) : "");
	}
	
	public static String get_string(StackOP op, int imm8_hi, int imm8_lo, String comment){
		return op.getName() + " " + imm8_hi + " " + imm8_lo+ ";\t" + ((comment.length() > 0)? ("#" + comment) : "");
	}
	
	public static String get_string(StackOP op, int imm_hi, int imm_lo, int offset, String comment){
		return op.getName() + " " + imm_hi + " " + imm_lo + " " + offset+ ";\t" + ((comment.length() > 0)? ("#" + comment) : "");
	}
	
	public static String get_string(StackOP op, String label, String comment){
		return op.getName() + " " + label + ";\t" + ((comment.length() > 0)? ("#" + comment) : "");
	}
	
	public static int assemble(StackOP op){
		return ((op.getEncoding()>>16 & 0x7FFF) << 16);
	}
	
	public static int assemble(StackOP op, int imm16){
		return ((op.getEncoding()>>16 & 0x7FFF) << 16) | imm16 & 0xFFFF;
	}
	
	public static int assemble(StackOP op, int imm8_hi, int imm8_lo){
		return ((op.getEncoding()>>16 & 0x7FFF) << 16) | ((imm8_hi & 0xFF) << 8) | (imm8_lo & 0xFF);
	}

	public static int assemble(StackOP op, int imm_hi, int imm_lo, int offset){
		assert false;
		// this is wrong
		return 0;
	}
	
	public static Instruction disassemble(int instruction) throws ParserException{
		int bin_op = instruction & 0x7FFF0000;
		int imm16 = instruction & 0xFFFF;
		int imm8_hi = (instruction >> 8) & 0xFF;
		int imm8_lo = instruction & 0xFF;
		assert false;
		// this is wrong
		int offset = 0;
		StackOP op = StackOP.translate(bin_op);

		return disassemble(op, imm16, imm8_hi, imm8_lo, offset, "", "");
	}

	public static Instruction disassemble(String instruction) throws ParserException{
		// Interpert the instruction string using a regex
		
		//ADD;	#COMMENT
		//PUSH 10;	#COMMENT
		//MOV 0 1;	#COMMENT
		//MAGIC_CALL @f_factorial;	#COMMENT
		Pattern pattern_0arg = Pattern.compile("\\s*([_A-Z2]+)\\s*;\\s*(#.*)?", Pattern.CASE_INSENSITIVE);
		Pattern pattern_1arg = Pattern.compile("\\s*([_A-Z2]+)\\s*(-?[0-9xABCDEF]+)\\s*;\\s*(#.*)?", Pattern.CASE_INSENSITIVE);
		Pattern pattern_2arg = Pattern.compile("\\s*([_A-Z2]+)\\s*(-?[0-9xABCDEF]+)\\s*[,]*\\s*(-?[0-9xABCDEF]+)\\s*;\\s*(#.*)?", Pattern.CASE_INSENSITIVE);
		Pattern pattern_3arg = Pattern.compile("\\s*([_A-Z2]+)\\s*(-?[0-9xABCDEF]+)\\s*[,]*\\s*(-?[0-9xABCDEF]+)\\s*[,]*\\s*(-?[0-9xABCDEF]+)\\s*;\\s*(#.*)?", Pattern.CASE_INSENSITIVE);
		Pattern pattern_lab = Pattern.compile("\\s*([_A-Z2]+)\\s*(@?[^\\s;]+)\\s*;\\s*(#.*)?", Pattern.CASE_INSENSITIVE);
		
		Pattern pattern_hex = Pattern.compile("0x([0-9A-F]+)", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher_0arg = pattern_0arg.matcher(instruction);
		Matcher matcher_1arg = pattern_1arg.matcher(instruction);
		Matcher matcher_2arg = pattern_2arg.matcher(instruction);
		Matcher matcher_3arg = pattern_3arg.matcher(instruction);
		Matcher matcher_lab = pattern_lab.matcher(instruction);
		
		int imm16 = 0;
		int imm8_hi = 0;
		int imm8_lo = 0;
		int offset = 0;
		String label = "";
		String comment = "";
		String s_op;
		
		if (matcher_0arg.matches()){
			// instruction with no arguments
			s_op = matcher_0arg.group(1);
			comment = matcher_0arg.group(2);
		} else if (matcher_1arg.matches()) {
			// instruction with one immediate
			s_op = matcher_1arg.group(1);
			// imm16 can be hex. Handle that case first.
			String immediate = matcher_1arg.group(2);
			Matcher matcher_hex = pattern_hex.matcher(immediate);
			if (matcher_hex.matches()){
				imm16 = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				imm16 = Integer.parseInt(immediate);
			}
			comment = matcher_1arg.group(3);
		} else if (matcher_2arg.matches()) {
			// instruction with two immediates
			s_op = matcher_2arg.group(1);
			// imm8_hi can be hex. Handle that case first.
			String immediate_hi = matcher_2arg.group(2);
			Matcher matcher_hex = pattern_hex.matcher(immediate_hi);
			if (matcher_hex.matches()){
				imm8_hi = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				imm8_hi = Integer.parseInt(immediate_hi);
			}
			
			// imm8_lo can be hex. Handle that case first.
			String immediate_lo = matcher_2arg.group(3);
			matcher_hex = pattern_hex.matcher(immediate_lo);
			if (matcher_hex.matches()){
				imm8_lo = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				imm8_lo = Integer.parseInt(immediate_lo);
			}
			
			comment = matcher_2arg.group(4);
		} else if (matcher_3arg.matches()){
			// instruction with three immediates
			s_op = matcher_3arg.group(1);
			
			// offset can be hex. Handle that case first.
			String str_offset = matcher_3arg.group(2);
			Matcher matcher_hex = pattern_hex.matcher(str_offset);
			if (matcher_hex.matches()){
				offset = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				offset = Integer.parseInt(str_offset);
			}
						
			// imm8_hi can be hex. Handle that case first.
			String immediate_hi = matcher_3arg.group(3);
			matcher_hex = pattern_hex.matcher(immediate_hi);
			if (matcher_hex.matches()){
				imm8_hi = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				imm8_hi = Integer.parseInt(immediate_hi);
			}
			
			// imm8_lo can be hex. Handle that case first.
			String immediate_lo = matcher_3arg.group(4);
			matcher_hex = pattern_hex.matcher(immediate_lo);
			if (matcher_hex.matches()){
				imm8_lo = Integer.parseInt(matcher_hex.group(1), 16);  
			} else {
				imm8_lo = Integer.parseInt(immediate_lo);
			}
			
			comment = matcher_3arg.group(5);
		} else if (matcher_lab.matches()) {
			// instruction with a label operand
			s_op = matcher_lab.group(1);
			label = matcher_lab.group(2);
			comment = matcher_lab.group(3);
		} else {
			// not a recognizable instruction string
			throw new ParserException("Illegal Instruction format : \"" + instruction + "\n");
		}
		
		StackOP op = StackOP.translate(s_op);
		
		imm16 = imm16 & 0xFFFF;
		imm8_hi = imm8_hi & 0xFF;
		imm8_lo = imm8_lo & 0xFF;
		offset = offset & 0xFF;
		
		try{
		return disassemble(op, imm16, imm8_hi, imm8_lo, offset, label, comment);
		} catch (Exception e){
			throw new ParserException("Error trying to parse instruction : \"" + instruction +"\"");
		}
	}

	public static Instruction disassemble(StackOP op, int imm16, int imm8_hi, int imm8_lo, int offset, String label, String comment) throws ParserException{
		Instruction I;
		
		switch(op){
		case SLL:
			I = new InstructionSLL(imm16, comment);
			break;
		case SRL:
			I = new InstructionSRL(imm16, comment);
			break;
		case SRA:
			I = new InstructionSRA(imm16, comment);
			break;
		case LNOT:
			I = new InstructionLNOT(comment);
			break;
		case BNOT:
			I = new InstructionBNOT(comment);
			break;
		case SETHI:
			I = new InstructionSETHI(imm16, comment);
			break;
		case LAND:
			I = new InstructionLAND(comment);
			break;
		case LOR:
			I = new InstructionLOR(comment);
			break;
		case BAND:
			I = new InstructionBAND(comment);
			break;
		case BOR:
			I = new InstructionBOR(comment);
			break;
		case BXOR:
			I = new InstructionBXOR(comment);
			break;
		case ADD:
			I = new InstructionADD(comment);
			break;
		case SUB:
			I = new InstructionSUB(comment);
			break;
		case CMP_EQ:
			I = new InstructionCMP_EQ(comment);
			break;
		case CMP_NE:
			I = new InstructionCMP_NE(comment);
			break;
		case CMP_UGT:
			I = new InstructionCMP_UGT(comment);
			break;
		case CMP_UGE:
			I = new InstructionCMP_UGE(comment);
			break;
		case CMP_ULT:
			I = new InstructionCMP_ULT(comment);
			break;
		case CMP_ULE:
			I = new InstructionCMP_ULE(comment);
			break;
		case CMP_SGT:
			I = new InstructionCMP_SGT(comment);
			break;
		case CMP_SGE:
			I = new InstructionCMP_SGE(comment);
			break;
		case CMP_SLT:
			I = new InstructionCMP_SLT(comment);
			break;
		case CMP_SLE:
			I = new InstructionCMP_SLE(comment);
			break;
		case MUL:
			I = new InstructionMUL(comment);
			break;
		case PULL:
			I = new InstructionPULL(imm16, comment);
			break;
		case PULL_CP:
			I = new InstructionPULL_CP(imm16, comment);
			break;
		case TUCK:
			I = new InstructionTUCK(imm16, comment);
			break;
		case TUCK_CP:
			I = new InstructionTUCK_CP(imm16, comment);
			break;
		case SWAP:
			I = new InstructionSWAP(imm16, comment);
			break;
		case DROP:
			I = new InstructionDROP(imm16, comment);
			break;
		case PUSH:
			I = new InstructionPUSH(imm16, comment);
			break;
		case PUSH_SPECIAL:
			I = new InstructionPUSH_SPECIAL(imm16, comment);
			break;
		case MAIN2AUX_CP:
			I = new InstructionMAIN2AUX_CP(comment);
			break;
		case MAIN2AUX:
			I = new InstructionMAIN2AUX(comment);
			break;
		case AUX2MAIN_CP:
			I = new InstructionAUX2MAIN_CP(comment);
			break;
		case AUX2MAIN:
			I = new InstructionAUX2MAIN(comment);
			break;
		case LD:
			I = new InstructionLD(imm16, comment);
			break;
		case LD_EM:
			I = new InstructionLD_EM(imm8_hi, imm8_lo, offset, comment);
			break;
		case LD_RA:
			I = new InstructionLD_RA(imm16, comment);
			break;
		case LD_RSV:
			I = new InstructionLD_RSV(imm8_hi, imm8_lo, offset, comment);
			break;
		case FNC_LD:
			I = new InstructionFNC_LD(imm16, comment);
			break;
		case FNC_LD_EM:
			I = new InstructionFNC_LD_EM(imm8_hi, imm8_lo, offset, comment);
			break;
		case FNC_LD_RA:
			I = new InstructionFNC_LD_RA(imm16, comment);
			break;
		case FNC_LD_RSV:
			I = new InstructionFNC_LD_RSV(imm8_hi, imm8_lo, offset, comment);
			break;
		case ST_NOACK:
			I = new InstructionST_NOACK(imm16, comment);
			break;
		case ST_EM_NOACK:
			I = new InstructionST_EM_NOACK(imm8_hi, imm8_lo, offset, comment);
			break;
		case ST_RA_NOACK:
			I = new InstructionST_RA_NOACK(imm16, comment);
			break;
		case ST_COND:
			I = new InstructionST_COND(imm8_hi, imm8_lo, offset, comment);
			break;
		case FNC_ST_NOACK:
			I = new InstructionFNC_ST_NOACK(imm16, comment);
			break;
		case FNC_ST_EM_NOACK:
			I = new InstructionFNC_ST_EM_NOACK(imm8_hi, imm8_lo, offset, comment);
			break;
		case FNC_ST_RA_NOACK:
			I = new InstructionFNC_ST_RA_NOACK(imm16, comment);
			break;
		case FNC_ST_COND:
			I = new InstructionFNC_ST_COND(imm8_hi, imm8_lo, offset, comment);
			break;
		case ST:
			I = new InstructionST(imm16, comment);
			break;
		case ST_EM:
			I = new InstructionST_EM(imm8_hi, imm8_lo, offset, comment);
			break;
		case ST_RA:
			I = new InstructionST_RA(imm16, comment);
			break;
		case FNC_ST:
			I = new InstructionFNC_ST(imm16, comment);
			break;
		case FNC_ST_EM:
			I = new InstructionFNC_ST_EM(imm8_hi, imm8_lo, offset, comment);
			break;
		case FNC_ST_RA:
			I = new InstructionFNC_ST_RA(imm16, comment);
			break;
		case J:
			I = new InstructionJ(comment);
			break;
		case J_PC:
			I = new InstructionJ_PC(imm16, comment);
			break;
		case CALL:
			I = new InstructionCALL(comment);
			break;
		case CALL_PC:
			I = new InstructionCALL_PC(imm16, comment);
			break;
		case BZ:
			I = new InstructionBZ(imm16, comment);
			break;
		case BNZ:
			I = new InstructionBNZ(imm16, comment);
			break;
		case HALT:
			I = new InstructionHALT(comment);
			break;
		
		case SIR_ALLOCA:
			I = new InstructionSIR_ALLOCA(comment);
			break;
		//case SIR_PUSH_CONST:
		//	I = new InstructionSIR_PUSH_CONST(imm16, comment);
		//	break;
		case SIR_BRANCH:
			I = new InstructionSIR_BRANCH(label, comment);
			break;
		case SIR_JUMP:
			I = new InstructionSIR_JUMP(label, comment);
			break;
		case SIR_CALL:
			I = new InstructionSIR_CALL(label, comment);
			break;
		case SIR_LOAD:
			I = new InstructionSIR_LOAD(comment);
			break;
		//case SIR_RETURN:
		//	I = new InstructionSIR_RETURN(comment);
		//	break;
		case SIR_STORE:
			I = new InstructionSIR_STORE(comment);
			break;
		case UNREACHABLE:
			I = new InstructionUNREACHABLE();
			break;
			
		default:
			throw new ParserException("Unhandled Opcoede");
		}
		return I;
	}

	protected enum StackOP{
		// Core
		//				CATEGORY | OPCODE
		SLL(			((0<<24)|(0<<16)),	"SLL"),
		SRL(			((0<<24)|(1<<16)),	"SRL"),
		SRA(			((0<<24)|(2<<16)),	"SRA"),
		
		LNOT(			((0<<24)|(3<<16)),	"LNOT"),
		BNOT(			((0<<24)|(4<<16)),	"BNOT"),
		SETHI(			((0<<24)|(5<<16)),	"SETHI"),
		LAND(			((1<<24)|(0<<16)),	"LAND"),
		LOR(			((1<<24)|(1<<16)),	"LOR"),
		BAND(			((1<<24)|(2<<16)),	"BAND"),
		BOR(			((1<<24)|(3<<16)),	"BOR"),
		BXOR(			((1<<24)|(4<<16)),	"BXOR"),
		
		ADD(			((1<<24)|(5<<16)),	"ADD"),
		SUB(			((1<<24)|(6<<16)),	"SUB"),
		
		CMP_EQ(			((1<<24)|(7<<16)),	"CMP_EQ"),
		CMP_NE(			((1<<24)|(8<<16)),	"CMP_NE"),
		CMP_UGT(		((1<<24)|(9<<16)),	"CMP_UGT"),
		CMP_UGE(		((1<<24)|(10<<16)),	"CMP_UGE"),
		CMP_ULT(		((1<<24)|(11<<16)),	"CMP_ULT"),
		CMP_ULE(		((1<<24)|(12<<16)),	"CMP_ULE"),
		CMP_SGT(		((1<<24)|(13<<16)),	"CMP_SGT"),
		CMP_SGE(		((1<<24)|(14<<16)),	"CMP_SGE"),
		CMP_SLT(		((1<<24)|(15<<16)),	"CMP_SLT"),
		CMP_SLE(		((1<<24)|(16<<16)),	"CMP_SLE"),
		
		MUL(			((2<<24)|(0<<16)),	"MUL"),
		
		PULL(			((3<<24)|(0<<16)),	"PULL"),
		PULL_CP(		((3<<24)|(1<<16)),	"PULL_CP"),
		TUCK(			((3<<24)|(2<<16)),	"TUCK"),
		TUCK_CP(		((3<<24)|(3<<16)),	"TUCK_CP"),
		SWAP(			((3<<24)|(4<<16)),	"SWAP"),
		DROP(			((3<<24)|(5<<16)),	"DROP"),
		PUSH(			((3<<24)|(6<<16)),	"PUSH"),
		
		PUSH_SPECIAL(	((3<<24)|(7<<16)),	"PUSH_SPECIAL"),
		
		MAIN2AUX_CP(	((4<<24)|(0<<16)),	"MAIN2AUX_CP"),
		MAIN2AUX(		((4<<24)|(1<<16)),	"MAIN2AUX"),
		AUX2MAIN_CP(	((4<<24)|(2<<16)),	"AUX2MAIN_CP"),
		AUX2MAIN(		((4<<24)|(3<<16)),	"AUX2MAIN"),
				
		LD(				((5<<24)|(0<<16)),	"LD"),
		LD_EM(			((5<<24)|(1<<16)),	"LD_EM"),
		LD_RA(			((5<<24)|(2<<16)),	"LD_RA"),
		LD_RSV(			((5<<24)|(4<<16)),	"LD_RSV"),
		
		FNC_LD(			((5<<24)|(8<<16)),	"FNC_LD"),
		FNC_LD_EM(		((5<<24)|(9<<16)),	"FNC_LD_EM"),
		FNC_LD_RA(		((5<<24)|(10<<16)),	"FNC_LD_RA"),
		FNC_LD_RSV(		((5<<24)|(12<<16)),	"FNC_LD_RSV"),
		
		ST_NOACK(		((6<<24)|(0<<16)),	"ST_NOACK"),
		ST_EM_NOACK(	((6<<24)|(1<<16)),	"ST_EM_NOACK"),
		ST_RA_NOACK(	((6<<24)|(2<<16)),	"ST_RA_NOACK"),
		ST_COND(		((6<<24)|(4<<16)),	"ST_COND"),
		
		FNC_ST_NOACK(	((6<<24)|(8<<16)),	"FNC_ST_NOACK"),
		FNC_ST_EM_NOACK(((6<<24)|(9<<16)),	"FNC_ST_EM_NOACK"),
		FNC_ST_RA_NOACK(((6<<24)|(10<<16)),	"FNC_ST_RA_NOACK"),
		FNC_ST_COND(	((6<<24)|(12<<16)),	"FNC_ST_COND"),
		
		ST(				((6<<24)|(16<<16)),	"ST"),
		ST_EM(			((6<<24)|(17<<16)),	"ST_EM"),
		ST_RA(			((6<<24)|(18<<16)),	"ST_RA"),
		
		FNC_ST(			((6<<24)|(24<<16)),	"FNC_ST"),
		FNC_ST_EM(		((6<<24)|(25<<16)),	"FNC_ST_EM"),
		FNC_ST_RA(		((6<<24)|(26<<16)),	"FNC_ST_RA"),
	
		J(				((7<<24)|(0<<16)),	"J"),
		J_PC(			((7<<24)|(1<<16)),	"J_PC"), // Equivalent to U_BRANCH
		CALL(			((7<<24)|(2<<16)),	"CALL"),
		CALL_PC(		((7<<24)|(3<<16)),	"CALL_PC"),
		
		BZ(				((8<<24)|(0<<16)),	"BZ"),
		BNZ(			((8<<24)|(1<<16)),	"BNZ"),
		
		HALT(			((15<<24)|(0<<16)),	"HALT"),

		// SIR instructions (not implemented in hardware)
		SIR_ALLOCA(		((14<<24)|(0<<16)),	"SIR_ALLOCA"),
		//SIR_PUSH_CONST(	((14<<24)|(1<<16)),	"SIR_PUSH_CONST"),
		SIR_BRANCH(		((14<<24)|(2<<16)),	"SIR_BRANCH"),
		SIR_JUMP(		((14<<24)|(3<<16)),	"SIR_JUMP"),
		SIR_CALL(		((14<<24)|(4<<16)),	"SIR_CALL"),
		//SIR_RETURN(		((14<<24)|(5<<16)),	"SIR_RETURN"),
		SIR_LOAD(		((14<<24)|(6<<16)),	"SIR_LOAD"),
		SIR_STORE(		((14<<24)|(7<<16)),	"SIR_STORE"),
		
		// Simulator-only UNREACHABLE (not implemented in hardware)
		UNREACHABLE(	((7<<27)|(4<<24)|(4<<16)),	"UNREACHABLE");

		private int encoding;
		private String name;
		
		public String getName(){
			return name;
		}
		
		public int getEncoding(){
			return encoding;
		}
		
		private StackOP(int encoding, String name){
			this.encoding = encoding;
			this.name = name;
		}

		public static Dictionary<Integer, StackOP> encoding_map = null;
		public static Dictionary<String, StackOP> name_map = null;

		public static StackOP translate(int encoding){
			if (null == encoding_map){
				init_encoding_map();
			}
			
			return encoding_map.get(encoding);
		}

		public static StackOP translate(String name){
			if (null == name_map){
				init_name_map();
			}
			
			return name_map.get(name.toUpperCase());
		}
		
		private static void init_encoding_map(){
			encoding_map = new Hashtable<Integer, StackOP>();
			
			encoding_map.put(SLL.getEncoding(), 			SLL);
			encoding_map.put(SRL.getEncoding(), 			SRL);
			encoding_map.put(SRA.getEncoding(), 			SRA);
			
			encoding_map.put(LNOT.getEncoding(), 			LNOT);
			encoding_map.put(BNOT.getEncoding(), 			BNOT);
			encoding_map.put(SETHI.getEncoding(), 			SETHI);
			encoding_map.put(LAND.getEncoding(), 			LAND);
			encoding_map.put(LOR.getEncoding(), 			LOR);
			encoding_map.put(BAND.getEncoding(), 			BAND);
			encoding_map.put(BOR.getEncoding(), 			BOR);
			encoding_map.put(BXOR.getEncoding(), 			BXOR);
			
			encoding_map.put(ADD.getEncoding(), 			ADD);
			encoding_map.put(SUB.getEncoding(), 			SUB);
			
			encoding_map.put(CMP_EQ.getEncoding(), 			CMP_EQ);
			encoding_map.put(CMP_NE.getEncoding(), 			CMP_NE);
			encoding_map.put(CMP_UGT.getEncoding(), 		CMP_UGT);
			encoding_map.put(CMP_UGE.getEncoding(), 		CMP_UGE);
			encoding_map.put(CMP_ULT.getEncoding(), 		CMP_ULT);
			encoding_map.put(CMP_ULE.getEncoding(), 		CMP_ULE);
			encoding_map.put(CMP_SGT.getEncoding(), 		CMP_SGT);
			encoding_map.put(CMP_SGE.getEncoding(), 		CMP_SGE);
			encoding_map.put(CMP_SLT.getEncoding(), 		CMP_SLT);
			encoding_map.put(CMP_SLE.getEncoding(), 		CMP_SLE);
			
			encoding_map.put(MUL.getEncoding(), 			MUL);
			
			encoding_map.put(PULL.getEncoding(), 			PULL);
			encoding_map.put(PULL_CP.getEncoding(), 		PULL_CP);
			encoding_map.put(TUCK.getEncoding(), 			TUCK);
			encoding_map.put(TUCK_CP.getEncoding(), 		TUCK_CP);
			encoding_map.put(SWAP.getEncoding(), 			SWAP);
			encoding_map.put(DROP.getEncoding(), 			DROP);
			encoding_map.put(PUSH.getEncoding(), 			PUSH);
			
			encoding_map.put(PUSH_SPECIAL.getEncoding(), 	PUSH_SPECIAL);
			
			encoding_map.put(MAIN2AUX_CP.getEncoding(), 	MAIN2AUX_CP);
			encoding_map.put(MAIN2AUX.getEncoding(), 		MAIN2AUX);
			encoding_map.put(AUX2MAIN_CP.getEncoding(), 	AUX2MAIN_CP);
			encoding_map.put(AUX2MAIN.getEncoding(), 		AUX2MAIN);
			
			encoding_map.put(LD.getEncoding(), 				LD);
			encoding_map.put(LD_EM.getEncoding(), 			LD_EM);
			encoding_map.put(LD_RA.getEncoding(), 			LD_RA);
			encoding_map.put(LD_RSV.getEncoding(), 			LD_RSV);
			
			encoding_map.put(FNC_LD.getEncoding(), 			FNC_LD);
			encoding_map.put(FNC_LD_EM.getEncoding(), 		FNC_LD_EM);
			encoding_map.put(FNC_LD_RA.getEncoding(), 		FNC_LD_RA);
			encoding_map.put(FNC_LD_RSV.getEncoding(), 		FNC_LD_RSV);
			
			encoding_map.put(ST_NOACK.getEncoding(), 		ST_NOACK);
			encoding_map.put(ST_EM_NOACK.getEncoding(), 	ST_EM_NOACK);
			encoding_map.put(ST_RA_NOACK.getEncoding(), 	ST_RA_NOACK);
			encoding_map.put(ST_COND.getEncoding(), 		ST_COND);
			
			encoding_map.put(FNC_ST_NOACK.getEncoding(), 	FNC_ST_NOACK);
			encoding_map.put(FNC_ST_EM_NOACK.getEncoding(), FNC_ST_EM_NOACK);
			encoding_map.put(FNC_ST_RA_NOACK.getEncoding(), FNC_ST_RA_NOACK);
			encoding_map.put(FNC_ST_COND.getEncoding(), 	FNC_ST_COND);
			
			encoding_map.put(ST.getEncoding(), 				ST);
			encoding_map.put(ST_EM.getEncoding(), 			ST_EM);
			encoding_map.put(ST_RA.getEncoding(), 			ST_RA);
			
			encoding_map.put(FNC_ST.getEncoding(), 			FNC_ST);
			encoding_map.put(FNC_ST_EM.getEncoding(), 		FNC_ST_EM);
			encoding_map.put(FNC_ST_RA.getEncoding(), 		FNC_ST_RA);
			
			encoding_map.put(J.getEncoding(), 				J);
			encoding_map.put(J_PC.getEncoding(), 			J_PC);
			
			encoding_map.put(CALL.getEncoding(), 			CALL);
			encoding_map.put(CALL_PC.getEncoding(), 		CALL_PC);
			
			encoding_map.put(BZ.getEncoding(), 				BZ);
			encoding_map.put(BNZ.getEncoding(), 			BNZ);
			
			encoding_map.put(HALT.getEncoding(), 			HALT);
			
			// SIR instructions (not implemented in hardware)
			encoding_map.put(SIR_ALLOCA.getEncoding(), 		SIR_ALLOCA);
			//encoding_map.put(SIR_PUSH_CONST.getEncoding(), SIR_PUSH_CONST);
			encoding_map.put(SIR_BRANCH.getEncoding(), 		SIR_BRANCH);
			encoding_map.put(SIR_JUMP.getEncoding(), 		SIR_JUMP);
			encoding_map.put(SIR_CALL.getEncoding(), 		SIR_CALL);
			//encoding_map.put(SIR_RETURN.getEncoding(), 	SIR_RETURN);
			encoding_map.put(SIR_LOAD.getEncoding(), 		SIR_LOAD);
			encoding_map.put(SIR_STORE.getEncoding(), 		SIR_STORE);
			encoding_map.put(UNREACHABLE.getEncoding(), 	UNREACHABLE);
		}
		
		private static void init_name_map(){
			name_map = new Hashtable<String, StackOP>();
			
			name_map.put(SLL.getName(), 			SLL);
			name_map.put(SRL.getName(), 			SRL);
			name_map.put(SRA.getName(), 			SRA);
			
			name_map.put(LNOT.getName(), 			LNOT);
			name_map.put(BNOT.getName(), 			BNOT);
			name_map.put(SETHI.getName(), 			SETHI);
			name_map.put(LAND.getName(), 			LAND);
			name_map.put(LOR.getName(), 			LOR);
			name_map.put(BAND.getName(), 			BAND);
			name_map.put(BOR.getName(), 			BOR);
			name_map.put(BXOR.getName(), 			BXOR);
			
			name_map.put(ADD.getName(), 			ADD);
			name_map.put(SUB.getName(), 			SUB);
			
			name_map.put(CMP_EQ.getName(), 			CMP_EQ);
			name_map.put(CMP_NE.getName(), 			CMP_NE);
			name_map.put(CMP_UGT.getName(), 		CMP_UGT);
			name_map.put(CMP_UGE.getName(), 		CMP_UGE);
			name_map.put(CMP_ULT.getName(), 		CMP_ULT);
			name_map.put(CMP_ULE.getName(), 		CMP_ULE);
			name_map.put(CMP_SGT.getName(), 		CMP_SGT);
			name_map.put(CMP_SGE.getName(), 		CMP_SGE);
			name_map.put(CMP_SLT.getName(), 		CMP_SLT);
			name_map.put(CMP_SLE.getName(), 		CMP_SLE);
			
			name_map.put(MUL.getName(), 			MUL);
			
			name_map.put(PULL.getName(), 			PULL);
			name_map.put(PULL_CP.getName(), 		PULL_CP);
			name_map.put(TUCK.getName(), 			TUCK);
			name_map.put(TUCK_CP.getName(), 		TUCK_CP);
			name_map.put(SWAP.getName(), 			SWAP);
			name_map.put(DROP.getName(), 			DROP);
			name_map.put(PUSH.getName(), 			PUSH);
			
			name_map.put(PUSH_SPECIAL.getName(), 	PUSH_SPECIAL);
			
			name_map.put(MAIN2AUX_CP.getName(), 	MAIN2AUX_CP);
			name_map.put(MAIN2AUX.getName(), 		MAIN2AUX);
			name_map.put(AUX2MAIN_CP.getName(), 	AUX2MAIN_CP);
			name_map.put(AUX2MAIN.getName(), 		AUX2MAIN);
			
			name_map.put(LD.getName(), 				LD);
			name_map.put(LD_EM.getName(), 			LD_EM);
			name_map.put(LD_RA.getName(), 			LD_RA);
			name_map.put(LD_RSV.getName(), 			LD_RSV);
			
			name_map.put(FNC_LD.getName(), 			FNC_LD);
			name_map.put(FNC_LD_EM.getName(), 		FNC_LD_EM);
			name_map.put(FNC_LD_RA.getName(), 		FNC_LD_RA);
			name_map.put(FNC_LD_RSV.getName(), 		FNC_LD_RSV);
			
			name_map.put(ST_NOACK.getName(), 		ST_NOACK);
			name_map.put(ST_EM_NOACK.getName(), 	ST_EM_NOACK);
			name_map.put(ST_RA_NOACK.getName(), 	ST_RA_NOACK);
			name_map.put(ST_COND.getName(), 		ST_COND);
			
			name_map.put(FNC_ST_NOACK.getName(), 	FNC_ST_NOACK);
			name_map.put(FNC_ST_EM_NOACK.getName(), FNC_ST_EM_NOACK);
			name_map.put(FNC_ST_RA_NOACK.getName(), FNC_ST_RA_NOACK);
			name_map.put(FNC_ST_COND.getName(), 	FNC_ST_COND);
			
			name_map.put(ST.getName(), 				ST);
			name_map.put(ST_EM.getName(), 			ST_EM);
			name_map.put(ST_RA.getName(), 			ST_RA);
			
			name_map.put(FNC_ST.getName(), 			FNC_ST);
			name_map.put(FNC_ST_EM.getName(), 		FNC_ST_EM);
			name_map.put(FNC_ST_RA.getName(), 		FNC_ST_RA);
			
			name_map.put(J.getName(), 				J);
			name_map.put(J_PC.getName(), 			J_PC);
			
			name_map.put(CALL.getName(), 			CALL);
			name_map.put(CALL_PC.getName(), 		CALL_PC);
			
			name_map.put(BZ.getName(),		 		BZ);
			name_map.put(BNZ.getName(), 			BNZ);
			
			name_map.put(HALT.getName(), 			HALT);
			
			// SIR instructions (not implemented in hardware)
			name_map.put(SIR_ALLOCA.getName(), 		SIR_ALLOCA);
			//name_map.put(SIR_PUSH_CONST.getName(), SIR_PUSH_CONST);
			name_map.put(SIR_BRANCH.getName(), 		SIR_BRANCH);
			name_map.put(SIR_JUMP.getName(), 		SIR_JUMP);
			name_map.put(SIR_CALL.getName(), 		SIR_CALL);
			//name_map.put(SIR_RETURN.getName(), 	SIR_RETURN);
			name_map.put(SIR_LOAD.getName(), 		SIR_LOAD);
			name_map.put(SIR_STORE.getName(), 		SIR_STORE);
			name_map.put(UNREACHABLE.getName(), 	UNREACHABLE);
		}
	}

	public boolean isTerminal() {
		return false;
	}
}
