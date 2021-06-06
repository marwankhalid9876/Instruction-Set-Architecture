import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

public class BasicComputer {
    private InstructionMemory instructionMemory;
    private DataMemory dataMemory;
    private PC_Register pc;
    private StatusRegister statusRegister;
    private GeneralPurposeRegister[] generalPurposeRegisters;
    private int cycle;
    private InstructionWord oldFetched;
    private InstructionWord newFetched;
    private byte[] oldDecoded;
    private byte[] newDecoded;



    public BasicComputer() {
        instructionMemory = InstructionMemory.getInstance();
        dataMemory = DataMemory.getInstance();
        pc = new PC_Register();
        statusRegister = new StatusRegister();
        generalPurposeRegisters = new GeneralPurposeRegister[64];
        for (int i = 0; i < 64; i++)
            generalPurposeRegisters[i] = new GeneralPurposeRegister();
        cycle=1;
    }


    public void pipeline(int numberOfCycles){
        while (cycle<=numberOfCycles)
        {
            System.out.println("Clock cycle number: " + cycle);
            if(cycle<numberOfCycles-1)
            {

                System.out.println("Instruction to be fetched: Instruction " + cycle);
                newFetched = instructionFetch();
            }
            else
                newFetched=null;

           if(oldDecoded!=null)
           {
               System.out.println("Instruction to be executed: Instruction " + (cycle-2));
               execute(oldDecoded[0],oldDecoded[1],oldDecoded[2],oldDecoded[3]);
           }

            if(oldFetched!=null)
            {
                System.out.println("Instruction to be decoded: Instruction " + (cycle-1));
                newDecoded = instructionDecode(oldFetched);
            }
            else
                newDecoded=null;
           oldFetched=newFetched;
           oldDecoded=newDecoded;
           cycle++;
            System.out.println("--------------");

        }
        System.out.println("PROGRAM EXECUTION DONE!");
        System.out.println("======================================");
        System.out.println("Content of PC register: " + pc.toString());
        System.out.println("Content of Status registers: " + statusRegister.toString());
        System.out.println("Content of general purpose registers: ");
        for(int i=0; i<generalPurposeRegisters.length; i++)
            System.out.println("Register " + (i) + ": " + generalPurposeRegisters[i]);

        System.out.println("Content of data memory: ");
        System.out.println(dataMemory);
        System.out.println("Content of instruction memory: ");
        System.out.println(instructionMemory);
    }

    public void add(byte r1 ,byte valueOfR1, byte valueOfR2) {

        int result = valueOfR1 + valueOfR2;
        int oneByteResult = setStatusRegisters(result);

        if (valueOfR1 >> 7 == valueOfR2 >> 7)
            if (valueOfR1 >> 7 != oneByteResult >> 7)
                statusRegister.setOverflowFlag(true);
            else
                statusRegister.setOverflowFlag(false);
        else
            statusRegister.setOverflowFlag(false);

        statusRegister.setSignFlag(statusRegister.getNegativeFlag() ^ statusRegister.getOverflowFlag());
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }

    public void subtract(byte r1 ,byte valueOfR1, byte valueOfR2) {

        int result = valueOfR1 - valueOfR2;
        int oneByteResult = setStatusRegisters(result);

        if (valueOfR1 >> 7 != valueOfR2 >> 7)
            if (valueOfR2 >> 7 == oneByteResult >> 7)
                statusRegister.setOverflowFlag(true);
            else
                statusRegister.setOverflowFlag(false);
        else
            statusRegister.setOverflowFlag(false);

        statusRegister.setSignFlag(statusRegister.getNegativeFlag() ^ statusRegister.getOverflowFlag());
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }

    public void multiply(byte r1 ,byte valueOfR1, byte valueOfR2) {
        int result = valueOfR1 * valueOfR2;
        int oneByteResult = setStatusRegisters(result);
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }

    public int setStatusRegisters(int result) {
        if (result > Byte.MAX_VALUE) {
            statusRegister.setCarryFlag(true);
            result = result & 255;
        } else
            statusRegister.setCarryFlag(false);

        if (result < 0)
            statusRegister.setNegativeFlag(true);
        else
            statusRegister.setNegativeFlag(false);

        if (result == 0)
            statusRegister.setZeroFlag(true);
        else
            statusRegister.setZeroFlag(false);

        return result;
    }

    public void loadByte(byte R1, byte immediate) {
        //0,1,2,3,4,5,...31  ---> 0,1,2,3,4,5...31
        //-32,-31,-30,-29 --->32,33,34,35,....63
        int immediateUnsigned = 0;
        if (immediate < 0)
            immediateUnsigned = ((immediate & 0xff)) - 192;
        else
            immediateUnsigned = immediate;
        MemoryWord[] memoryArray = dataMemory.getDataMemoryArray();
        byte targetValue = memoryArray[immediateUnsigned].getValue();
        generalPurposeRegisters[R1].setValue(targetValue);
    }

    public void storeByte(byte R1Value, byte immediate) {
        //0,1,2,3,4,5,...31  ---> 0,1,2,3,4,5...31
        //-32,-31,-30,-29 --->32,33,34,35,....63
        int immediateUnsigned = 0;
        if (immediate < 0)
            immediateUnsigned = ((immediate & 0xff)) - 192;
        else
            immediateUnsigned = immediate;

        MemoryWord[] memoryArray = dataMemory.getDataMemoryArray();
        System.out.println("Memory block in address: " + immediateUnsigned+
                " was updated from " + memoryArray[immediateUnsigned] + " to " + R1Value);
        memoryArray[immediateUnsigned].setValue(R1Value);
    }

    public void loadImmediate(byte r1Address, byte immediate) {
        generalPurposeRegisters[r1Address].setValue(immediate);
    }

    public void branchIfEqualZero(byte r1Value, byte immediate){
        if(r1Value == 0){
            pc.setValue(pc.getValue() + immediate);
        }
    }
    public void shiftLeftCircular(byte r1, byte rVal ,byte immediate){
        int res=rVal<<immediate;
        //helper is the same rVal but in the first 8 bits of 32 bits of int to be able to circular shift them
        int helper=(rVal<<24)&0XFF000000;
        helper=Integer.rotateLeft(helper,immediate);
        //here we should make ( Number being shifted OR bits that is circular shifted)
        res=res|helper;

        res=res&0x000000FF;

        if (res < 0)
            statusRegister.setNegativeFlag(true);
        else
            statusRegister.setNegativeFlag(false);
        if (res == 0)
            statusRegister.setZeroFlag(true);
        else
            statusRegister.setZeroFlag(false);
        generalPurposeRegisters[r1].setValue(res);


    }
    public void shiftRightCircular(byte r1, byte r1Value ,byte immediate){
        int res=r1Value&0x000000FF;
        //helper will hold the values of right circular shifted bits at the left most bits of the 32 bits of int
        int helper=Integer.rotateRight(res,immediate);
        res=res<<(24-immediate);
        res=res|helper;
        res=res>>24;
        res=res&0x000000FF;
        if (res < 0)
            statusRegister.setNegativeFlag(true);
        else
            statusRegister.setNegativeFlag(false);
        if (res == 0)
            statusRegister.setZeroFlag(true);
        else
            statusRegister.setZeroFlag(false);
        generalPurposeRegisters[r1].setValue(res);

    }


    public void and(byte r1 ,byte valueOfR1, byte valueOfR2){
        int result=valueOfR1&valueOfR2;
        int oneByteResult=setStatusRegisters(result);
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }
    public void or(byte r1 ,byte valueOfR1, byte valueOfR2){
        int result=valueOfR1|valueOfR2;
        int oneByteResult=setStatusRegisters(result);
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }
    public void jumpRegister(byte valueOfR1, byte valueOfR2){
        String binaryR1=Integer.toBinaryString(valueOfR1);
        String binaryR2=Integer.toBinaryString(valueOfR2);
        String stringRes=binaryR1+binaryR2;
        int result=Integer.parseInt(stringRes, 2);
        pc.setValue(result);
    }

    public InstructionWord instructionFetch(){
        int nextAddress = pc.getValue();
        InstructionWord[] instructionMemoryArray = instructionMemory.getInstructionMemoryArray();
        return instructionMemoryArray[nextAddress];//instruction to be fetched
    }

    public byte[] instructionDecode(InstructionWord instruction){

        System.out.println("PC register was updated from " + pc.getValue() + " to " + (pc.getValue()+1));
        pc.setValue(pc.getValue()+1);//word-addressable
        byte[] instructionFields = new byte[4];
        //instructionFields = [opcode, R1, R1Value, R2Value(or immediate)]
        //to be used in execution
        instructionFields[0]=(byte)instruction.getOpcode();
        byte R1 =(byte)instruction.getR1();
        instructionFields[1]= R1;
        instructionFields[2]=(byte)generalPurposeRegisters[R1].getValue();
        byte R2OrImmediate;

        if(instruction instanceof I_Instruction)
            R2OrImmediate =(byte)((I_Instruction) instruction).getImmediate();
        else
        {
            byte R2 =(byte)((R_Instruction) instruction).getR2();
            R2OrImmediate=(byte)generalPurposeRegisters[R2].getValue();
        }
        instructionFields[3]=R2OrImmediate;
        return instructionFields;
    }


    public void execute(byte opcode, byte R1, byte R1Value, byte R2OrImmediateValue) {
        Boolean[] statusRegisterBefore = new Boolean[8];
        System.arraycopy(statusRegister.getFlags(),0,statusRegisterBefore, 0,8);
        switch (opcode) {
            case 0:
                add(R1, R1Value, R2OrImmediateValue);
                break;
            case 1:
                subtract(R1, R1Value, R2OrImmediateValue);
                break;
            case 2:
                multiply(R1, R1Value, R2OrImmediateValue);
                break;
            case 3:
                loadImmediate(R1, R2OrImmediateValue);
                break;
            case 4:
                branchIfEqualZero(R1Value,R2OrImmediateValue);
                break;
            case 5:
                and(R1,R1Value,R2OrImmediateValue);
                break;
            case 6:
                or(R1,R1Value,R2OrImmediateValue);
                break;
            case 7:
                System.out.println("PC register was updated from " + pc.getValue());
                jumpRegister(R1Value,R2OrImmediateValue);
                System.out.print(" to " + pc.getValue());
                break;
            case 8:
                shiftLeftCircular(R1,R1Value,R2OrImmediateValue);
                break;
            case 9:
                shiftRightCircular(R1,R1Value,R2OrImmediateValue);
                break;
            case 10:
                loadByte(R1, R2OrImmediateValue);
                break;
            case 11:
                storeByte(R1Value, R2OrImmediateValue);
                break;
            default:
                System.out.println("Invalid Opcode!");
        }
        if(R1Value!=generalPurposeRegisters[R1].getValue())
            System.out.println("Register " + R1 + " was updated from " + R1Value + " to " + generalPurposeRegisters[R1].getValue());

        if(Arrays.equals(statusRegister.getFlags(), statusRegisterBefore))
            System.out.println("Status Register was updated from " + Arrays.toString(statusRegisterBefore) +
                    " to " + Arrays.toString(statusRegister.getFlags()));
    }


    public int parse(String path)
    {
        int numberOfInstructions=0;
        try {
            File file = new File(path);
            FileReader fr;

            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                Vector<String> lineTokenizer = new Vector<>();
                StringTokenizer st = new StringTokenizer(line," ");
                // now I have each line in a vector
                InstructionWord instruction = null;
                String R1;
                String R2OrImmediate;
                while(st.hasMoreTokens())
                    lineTokenizer.add(st.nextToken());
                switch(lineTokenizer.get(0)) {
                    case "ADD":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)0);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "SUB":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)1);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "MUL":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)2);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "LDI":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)3);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                    case "BEQZ":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)4);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                    case "AND":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)5);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "OR":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)6);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "JR":
                        instruction = new R_Instruction();
                        instruction.setOpcode((byte)7);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2).substring(1);
                        instruction.setR1(Byte.parseByte(R1));
                        ((R_Instruction)instruction).setR2(Byte.parseByte(R2OrImmediate));
                        break;
                    case "SLC":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)8);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                    case "SRC":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)9);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                    case "LB":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)10);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                    case "SB":
                        instruction = new I_Instruction();
                        instruction.setOpcode((byte)11);
                        R1 = lineTokenizer.get(1).substring(1);
                        R2OrImmediate = lineTokenizer.get(2);
                        instruction.setR1(Byte.parseByte(R1));
                        ((I_Instruction)instruction).setImmediate(Byte.parseByte(R2OrImmediate));
                        break;
                }

                instructionMemory.getInstructionMemoryArray()[numberOfInstructions]=instruction;//add it to instruction memory
                numberOfInstructions++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(numberOfInstructions==0)
            return -1;
        return 3+((numberOfInstructions-1)*1);
    }

    public static void main(String[] args) {

        BasicComputer basicComputer = new BasicComputer();
        int numberOfCycles = basicComputer.parse("Program 1.txt");
        basicComputer.pipeline(numberOfCycles);
    }
}
