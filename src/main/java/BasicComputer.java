import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.util.Arrays;

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
        cycle=0;
    }


    public void pipeline(int numberOfCycles){
        while (cycle<=numberOfCycles)
        {
           cycle++;
           System.out.println("Clock cycle number: " + cycle);

            if(cycle<numberOfCycles-1)
            {
                newFetched = instructionFetch();
                System.out.println("Instruction to be fetched: " + cycle);
            }
            else
                newFetched=null;

            if(oldFetched!=null)
            {
                newDecoded = instructionDecode(oldFetched);
                System.out.println("Instruction to be decoded: " + (cycle-1));
            }
            else
                newDecoded=null;

           if(oldDecoded!=null)
           {
               execute(oldDecoded[0],oldDecoded[1],oldDecoded[2],oldDecoded[3]);
               System.out.println("Instruction to be executed: " + (cycle-2));
           }
           oldFetched=newFetched;
           oldDecoded=newDecoded;
        }
        System.out.println("Content of PC register: " + pc.toString());
        System.out.println("Content of Status registers: " + statusRegister.toString());
        System.out.println("Content of general purpose registers: ");
        for(int i=0; i<generalPurposeRegisters.length; i++)
            System.out.println("Register " + (i+1) + ": " + generalPurposeRegisters[i]);

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
        System.out.println("Memory block in address: " + immediate+
                " was updated from " + memoryArray[immediate] + " to " + R1Value);
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
        System.out.println("PC register was updated from " + pc.getValue() + "to " + (pc.getValue()+1));
        return instructionMemoryArray[nextAddress];//instruction to be fetched
    }

    public byte[] instructionDecode(InstructionWord instruction){
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
            R2OrImmediate=(byte)((R_Instruction) instruction).getR2();
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
            System.out.println("Register " + R1 + " was updated from " + R1Value + " to" + generalPurposeRegisters[R1].getValue());

        if(Arrays.equals(statusRegister.getFlags(), statusRegisterBefore))
            System.out.println("Status Register was updated from " + Arrays.toString(statusRegisterBefore) +
                    " to" + Arrays.toString(statusRegister.getFlags()));
    }


    public static void main(String[] args) {

    }
}
