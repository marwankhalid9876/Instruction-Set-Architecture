import jdk.swing.interop.SwingInterOpUtils;

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

        if (valueOfR1 << 7 == valueOfR2 << 7)
            if (valueOfR1 << 7 != oneByteResult << 7)
                statusRegister.setOverflowFlag(false);
            else
                statusRegister.setOverflowFlag(true);
        else
            statusRegister.setOverflowFlag(true);

        statusRegister.setSignFlag(statusRegister.getNegativeFlag() ^ statusRegister.getOverflowFlag());
        generalPurposeRegisters[r1].setValue(oneByteResult);
    }

    public void subtract(byte r1, byte r2) {
        int valueOfR1 = generalPurposeRegisters[r1].getValue();
        int valueOfR2 = generalPurposeRegisters[r2].getValue();
        int result = valueOfR1 - valueOfR2;
        int oneByteResult = setStatusRegisters(result);

        if (valueOfR1 << 7 == valueOfR2 << 7)
            if (valueOfR1 << 7 != oneByteResult << 7)
                statusRegister.setOverflowFlag(false);
            else
                statusRegister.setOverflowFlag(true);
        else
            statusRegister.setOverflowFlag(true);

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

    public void execute(byte opcode, byte R1, byte R2OrImmediate) {
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
                loadByte(R1, R2OrImmediate);
                break;
            case 12:
                storeByte(R1, R2OrImmediate);
                break;
            default:
                System.out.println("Invalid Opcode!");

        }
    }


    public void loadByte (byte R1, byte immediate){
        MemoryWord[] memoryArray = dataMemory.getDataMemoryArray();
        byte targetValue = memoryArray[immediate].getValue();
        generalPurposeRegisters[R1].setValue(targetValue);
    }
    public void storeByte ( byte R1, byte immediate){
        MemoryWord[] memoryArray = dataMemory.getDataMemoryArray();
        int targetValue = generalPurposeRegisters[R1].getValue();
        generalPurposeRegisters[R1].setValue(targetValue);
    }

        public static void main (String[]args){
            BasicComputer basicComputer = new BasicComputer();
            basicComputer.generalPurposeRegisters[0].setValue(10);
            basicComputer.generalPurposeRegisters[1].setValue(118);
//        basicComputer.add(0,1);
            System.out.println(basicComputer.generalPurposeRegisters[0].getValue());
            System.out.println(basicComputer.statusRegister.toString());
        }


    }
