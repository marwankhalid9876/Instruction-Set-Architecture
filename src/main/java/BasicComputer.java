import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import javax.xml.crypto.Data;

public class BasicComputer {
    private InstructionMemory instructionMemory;
    private DataMemory dataMemory;
    private PC_Register pc;
    private StatusRegister statusRegister;
    private GeneralPurposeRegister[] generalPurposeRegisters;

    public BasicComputer() {
        instructionMemory = InstructionMemory.getInstance();
        dataMemory = DataMemory.getInstance();
        pc = new PC_Register();
        statusRegister = new StatusRegister();
        generalPurposeRegisters = new GeneralPurposeRegister[64];
        for (int i = 0; i < 64; i++)
            generalPurposeRegisters[i] = new GeneralPurposeRegister();
    }


    public void add(byte r1, byte r2) {
        int valueOfR1 = generalPurposeRegisters[r1].getValue();
        int valueOfR2 = generalPurposeRegisters[r2].getValue();
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

    public void subtract(byte r1, byte r2) {
        int valueOfR1 = generalPurposeRegisters[r1].getValue();
        int valueOfR2 = generalPurposeRegisters[r2].getValue();
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

    public void multiply(byte r1, byte r2) {
        int valueOfR1 = generalPurposeRegisters[r1].getValue();
        int valueOfR2 = generalPurposeRegisters[r2].getValue();
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
        memoryArray[immediateUnsigned].setValue(R1Value);
    }

    public void loadImmediate(byte R1, byte immediate) {
        generalPurposeRegisters[R1].setValue(immediate);
    }

    public void branchIfEqualZero(byte R1, byte immediate){
        int r1Value = generalPurposeRegisters[R1].getValue();
        if(r1Value == 0){
            pc.setValue(pc.getValue() + immediate);
        }
    }

    public InstructionWord instructionFetch(){
        int nextAddress = pc.getValue();
        InstructionWord[] instructionMemoryArray = instructionMemory.getInstructionMemoryArray();
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
        switch (opcode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                loadByte(R1, R2OrImmediateValue);
                break;
            case 12:
                storeByte(R1Value, R2OrImmediateValue);
                break;
            default:
                System.out.println("Invalid Opcode!");

        }
    }


    public static void main(String[] args) {
        BasicComputer basicComputer = new BasicComputer();
//        basicComputer.generalPurposeRegisters[0].setValue(10);
//        basicComputer.generalPurposeRegisters[1].setValue(118);
//        basicComputer.add((byte) 0, (byte) 1);
//        System.out.println(basicComputer.generalPurposeRegisters[0].getValue());
//        System.out.println(basicComputer.statusRegister.toString());
        //   byte aByte = -31;

        // int immediateUnsigned = 0;
        // if(aByte<0)
        //     immediateUnsigned = ((aByte & 0xff)) -192;
        // else
        //     immediateUnsigned = aByte;

        // System.out.println(immediateUnsigned);
        // }


    }
}
