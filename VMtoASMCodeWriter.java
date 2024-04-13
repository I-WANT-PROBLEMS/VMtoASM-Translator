package ecsProject7And8;
import java.io.*;
public class VMtoASMCodeWriter {

	StringBuilder output;
	private static int lableGT=0;
	private static int lableLT=0;
	private static int lableEQ=0;
	private String[] Data;
	private String Name;
	private static int returnLabelCnt=0;
	
	public VMtoASMCodeWriter(String line, String fileName) {
		Name = fileName;
		this.Data = line.split(" ");
		this.output = new StringBuilder();
		if(line.startsWith("push")) {
				writePushCode(Data[1],Integer.parseInt(Data[2]));
		}
		else if(line.startsWith("pop")) {
				writePopCode(Data[1],Integer.parseInt(Data[2]));
		}
	    else if(Data.length == 1) {
	    	if(line.startsWith("return")) {
		    	HandleReturn();
		    }
	    	else {
			writeArithmetic(Data[0]);
	    	}
	    	}
	    else if(line.startsWith("label")) {
	    	ConstructLabels(Data[1]);
	    }
	    else if(line.startsWith("if-goto")) {
	    	ConditionalFlow(Data[1]);
	    }
	    else if(line.startsWith("goto")) {
	    	UnConditionalFlow(Data[1]);
	    }
	    else if(line.startsWith("function")) {
	    	HandleFunction(Data[1],Integer.parseInt(Data[2]));
	    }
	    else if(line.startsWith("call")) {
	    	HandleCall(Data[1],Integer.parseInt(Data[2]));
	    }
	}
		

	void setStack() {
		output.append("//Set Up Stack\n"  + "\n");
		output.append("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
		output.append("@300\n" + "D=A\n" + "@LCL\n" + "M=D\n");
		output.append("@400\n" + "D=A\n" + "@ARG\n" + "M=D\n");
		output.append("@3000\n" + "D=A\n" + "@THIS\n" + "M=D\n");
		output.append("@3010\n" + "D=A\n" + "@THAT\n" + "M=D\n");
	}
	
	void ConstructLabels(String LabelName) {
		output.append("(" + LabelName + ")\n" + "\n");
	}
	
	void ConditionalFlow(String s) {
		output.append("@SP\n");
    	output.append("AM=M-1\n");
    	output.append("D=M\n");
    	output.append("@" + s + "\n");
    	output.append("D;JNE\n" + "\n");
	}
	
	void UnConditionalFlow(String s) {
		output.append("@" + s + "\n"); 
		output.append("0;JMP\n" + "\n");
	}
	
	void HandleReturn() {
		output.append("//Hangling Return statement\n");
		//Keeping the value at the return address
		// Save current LCL address as the end of the caller frame -- we'll need it to compute frame offsets.
        output.append("@LCL\n");   
        output.append("D=M\n");   
        output.append("@R13\n");  
        output.append("M=D\n");

        // Save return address at frame - 5
        output.append("@5\n");
        output.append("A=D-A\n");
        output.append("D=M\n");
        output.append("@R14\n");
        output.append("M=D\n");

        // Pop the stack and setup the return value.
        	output.append("@SP\n");
        output.append("AM=M-1\n");
        output.append("D=M\n");
        output.append("@ARG\n");
        output.append("A=M\n");
        output.append("M=D\n");

        // Restore caller's stack pointer
        output.append ("@ARG\n");
        output.append("D=M+1\n");                
        output.append("@SP\n");
        output.append("M=D\n");

        // Restoring THAT 
        output.append("@R13\n"); 
        output.append("AM=M-1\n");
        output.append("D=M\n");
        output.append("@THAT\n"); 
        output.append ("M=D\n");

        // Restoring THIS 
        output.append("@R13\n"); 
        output.append("AM=M-1\n");
        output.append("D=M\n");
        output.append("@THIS\n"); 
        output.append ("M=D\n");

        // Restore ARG ptr
        output.append("@R13\n"); 
        output.append("AM=M-1\n");
        output.append("D=M\n");
        output.append ("@ARG\n"); 
        output.append ("M=D\n");

        // Restore LCL ptr
        output.append("@R13\n"); 
        output.append("AM=M-1\n");
        output.append("D=M\n");
        output.append("@LCL\n"); 
        output.append("M=D\n");

        // Jump to return address
        output.append ("@R14\n"); 
        output.append ("A=M\n");
        output.append ("0;JMP\n" + "\n");
	}
	
	void HandleFunction(String funcName,int nargs) {
		output.append("//Function handling\n");
		output.append("(" + funcName + ")\n");
		for(int i=nargs;i>0;i--) {
			output.append("@SP\n");
			output.append("A=M\n");
			output.append("M=0\n");
			output.append("@SP");
			output.append("M=M+1");
		}
	}
	
	void push_D_onto_Stack() {
		output.append("@SP\n");
		output.append("A=M\n");
		output.append("M=D\n");
		output.append("@SP\n");
		output.append("M=M+1\n");
	}
	
	void HandleCall(String funcName, int nArgs) {
		output.append("//Call Handling\n");
		String returnLabel = "RETURN_LABEL" + returnLabelCnt;
		returnLabelCnt = returnLabelCnt + 1;
		
		output.append("@" + returnLabel + "\n");
		output.append("D=A\n");
		push_D_onto_Stack();
		
		output.append("@LCL\n");
		output.append("D=M\n");
		push_D_onto_Stack();
		
		output.append("@ARG\n");
		output.append("D=M\n");
		push_D_onto_Stack();
		
		output.append("@THIS\n");
		output.append("D=M\n");
		push_D_onto_Stack();
		
		output.append("@THAT\n");
		output.append("D=M\n");
		push_D_onto_Stack();
		
		//Setting the Stack of the calee
		output.append("@SP\n");
		output.append("D=M\n");
		output.append("@LCL\n");
		output.append("M=D\n");
		
		output.append("@5\n");
		output.append("D=D-A\n");
		output.append("@" + nArgs + "\n");
		output.append("D=D-A\n");
		output.append("@ARG\n");
		output.append("M=D\n");
		
		output.append("@" + funcName + "\n");
		output.append("0;JMP\n");
		output.append("(" + returnLabel + ")\n" + "\n");
	}
	void writePushCode(String Segment, int BlockNum) {
		
		switch(Segment) {
	
		case "argument":
			output.append("//Push Argument " + BlockNum + "\n");
            output.append("@ARG\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("A=A+D\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "local":
        	output.append("//Push local " + BlockNum + "\n");
            output.append("@LCL\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("A=A+D\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "static":
        	output.append("//Push static " + BlockNum + "\n");
        	output.append("@" + Name + "." + BlockNum + "\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "constant":
        	output.append("//Push constant " + BlockNum + "\n");
        	output.append("@" + BlockNum + "\n");
            output.append("D=A\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "that":
        	output.append("//Push that " + BlockNum + "\n");
            output.append("@THAT\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("A=A+D\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "this":
        	output.append("//Push this " + BlockNum + "\n");
            output.append("@THIS\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("A=A+D\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "pointer":
            if (BlockNum == 0) {
            	output.append("//Push Pointer " + BlockNum + "\n");
                output.append("@THIS\n");
                output.append("D=M\n");
                output.append("@SP\n");
                output.append("A=M\n");
                output.append("M=D\n");
                output.append("@SP\n");
                output.append("M=M+1\n" + "\n");
            } else {
            	output.append("//Push Pointer " + BlockNum + "\n");
                output.append("@THAT\n");
                output.append("D=M\n");
                output.append("@SP\n");
                output.append("A=M\n");
                output.append("M=D\n");
                output.append("@SP\n");
                output.append("M=M+1\n" + "\n");
            }
            break;
        case "temp":
        	output.append("//Push temp " + BlockNum + "\n");
            output.append("@" + (BlockNum+5) + "\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=D\n");
            output.append("D=A+1\n");
            output.append("@SP\n");
            output.append("M=D\n" + "\n");
            break;
		}
	}
	
	void writePopCode(String Segment, int BlockNum) {
		
		switch(Segment) {
		
		case "argument":
			output.append("//Pop Argument " + BlockNum + "\n");
            output.append("@ARG\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("D=D+A\n");
            output.append("@R13\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("AM=M-1\n");
            output.append("D=M\n");
            output.append("@R13\n");
            output.append("A=M\n");
            output.append("M=D\n" + "\n");
            break;
        case "local":
        	output.append("//Pop local " + BlockNum + "\n");
        	output.append("@" + BlockNum + "\n");
            output.append("D=A\n");
            output.append("@LCL\n");
            output.append("D=D+M\n");
            output.append("@R13\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@R13\n");
            output.append("A=M\n");
            output.append("M=D\n" + "\n");
            break;
        case "static":
        	output.append("//Pop static " + BlockNum + "\n");
        	output.append("@SP\n");
        	output.append("AM=M-1\n");
        	output.append("D=M\n");
        	output.append("@" + Name + "." + BlockNum + "\n");
        	output.append("M=D\n" + "\n");
            break;
        case "that":
        	output.append("//Pop that " + BlockNum + "\n");
            output.append("@THAT\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("D=D+A\n");
            output.append("@R13\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("AM=M-1\n");
            output.append("D=M\n");
            output.append("@R13\n");
            output.append("A=M\n");
            output.append("M=D\n" + "\n");
            break;
        case "this":
        	output.append("//Pop this " + BlockNum + "\n");
            output.append("@THIS\n");
            output.append("D=M\n");
            output.append("@" + BlockNum + "\n");
            output.append("D=D+A\n");
            output.append("@R13\n");
            output.append("M=D\n");
            output.append("@SP\n");
            output.append("AM=M-1\n");
            output.append("D=M\n");
            output.append("@R13\n");
            output.append("A=M\n");
            output.append("M=D\n" + "\n");
            break;
        case "pointer":
            if (BlockNum == 0) {
            	output.append("//Pop Pointer " + BlockNum + "\n");
                output.append("@THIS\n");
                output.append("D=A\n");
                output.append("@R13\n");
                output.append("M=D\n");
                output.append("@SP\n");
                output.append("AM=M-1\n");
                output.append("D=M\n");
                output.append("@R13\n");
                output.append("A=M\n");
                output.append("M=D\n" + "\n");
            } else {
            	output.append("//Pop Pointer " + BlockNum + "\n");
                output.append("@THAT\n");
                output.append("D=A\n");
                output.append("@R13\n");
                output.append("M=D\n");
                output.append("@SP\n");
                output.append("AM=M-1\n");
                output.append("D=M\n");
                output.append("@R13\n");
                output.append("A=M\n");
                output.append("M=D\n" + "\n");
            }
            break;
        case "temp":
        	output.append("//Pop temp " + BlockNum + "\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@" + (BlockNum+5) + "\n");
            output.append("M=D\n" + "\n" );
            break;
		}
	}
	
	void writeArithmetic(String ComName) {
		switch(ComName) {
		case "add":
            output.append("// add \n");
            output.append("@SP\n");
            output.append("AM=M-1\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("AM=M-1\n");
            output.append("M=M+D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "sub":
            output.append("// sub \n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("M=M-D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "neg":
            output.append("// neg\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("M=-M\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "eq":
            output.append("// eq\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M-D\n");
            output.append("M=-1\n");
            output.append("@EQ_LBL_" + lableEQ + "\n");
            output.append("D;JEQ\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=0\n");
            output.append("(EQ_LBL_" + lableEQ + ")\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            lableEQ++;
            break;
        case "gt":
            output.append("// gt\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M-D\n");
            output.append("M=-1\n");
            output.append("@GT_LBL_" + lableGT + "\n");
            output.append("D;JGT\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=0\n");
            output.append("(GT_LBL_" + lableGT + ")\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            lableGT++;
            break;
        case "lt":
            output.append("// lt").append("\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M-D\n");
            output.append("M=-1\n");
            output.append("@LT_LBL_" + lableLT + "\n");
            output.append("D;JLT\n");
            output.append("@SP\n");
            output.append("A=M\n");
            output.append("M=0\n");
            output.append("(LT_LBL_" + lableLT + ")\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            lableLT++;
            break;
        case "and":
            output.append("// and\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("M=M&D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "or":
            output.append("// or" + "\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("D=M\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("M=M|D\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
        case "not":
            output.append("// not\n");
            output.append("@SP\n");
            output.append("M=M-1\n");
            output.append("A=M\n");
            output.append("M=!M\n");
            output.append("@SP\n");
            output.append("M=M+1\n" + "\n");
            break;
			
		}
	}
	
	}
	

