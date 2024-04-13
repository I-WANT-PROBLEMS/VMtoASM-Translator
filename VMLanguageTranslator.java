package ecsProject7;
import java.io.*;
	import java.util.Scanner;
	public class VMLanguageTranslator {
	
		public static void main(String args[]) throws IOException{
			//IP is the InputFile
			//OP is the OutputFile
			File IP = new File("C:\\Users\\Jaswanth Kunisetty\\Downloads\\nand2tetris\\nand2tetris\\projects\\07\\MemoryAccess\\BasicTest\\BasicTest.vm");
			File ASMFile = new File("C:\\Users\\Jaswanth Kunisetty\\Downloads\\nand2tetris\\nand2tetris\\projects\\07\\MemoryAccess\\BasicTest\\BasicTest.asm");
			
			FileWriter OP = new FileWriter(ASMFile);
			Scanner sc = new Scanner(IP);
			
			OP.append("//Set Up Stack\n");
			OP.append("@256\n" + "D=A\n" + "@SP\n" + "M=D\n" + "\n");
			OP.append("//Set LCL baseAddress \n");
			OP.append("@300\n" + "D=A\n" + "@LCL\n" + "M=D\n" + "\n");
			OP.append("//Setting Argument baseAddress \n");
			OP.append("@400\n" + "D=A\n" + "@ARG\n" + "M=D\n" + "\n");
			OP.append("//Setting Pointer 0 \n");
			OP.append("@3000\n" + "D=A\n" + "@THIS\n" + "M=D\n" + "\n");
			OP.append("//Setting Pointer 1 \n");
			OP.append("@3010\n" + "D=A\n" + "@THAT\n" + "M=D\n" + "\n");
			
			while(sc.hasNextLine()) {
				String line = sc.nextLine().replaceAll("//.*", "").trim();
				VMtoASMCodeWriter obj = new VMtoASMCodeWriter(line, IP.getName().replace(".vm", " "));
				OP.append(obj.output + "");
			}
			OP.append("(END)\n");
			OP.append("@END\n");
			OP.append("0;JMP");
			
			sc.close();
			OP.close();
			System.out.println("Translation successfull");
			
		}
	}
