public class InstructionWord {

    private byte opcode;
    private byte R1;

    @Override
    public String toString() {
        return "InstructionWord{" +
                "opcode=" + opcode +
                ", R1=" + R1;
    }

    public void setOpcode(byte opcode) {
        if(opcode>11 || opcode<0)
            System.out.println("Invalid Opcode");
        else
            this.opcode = opcode;
    }

    public void setR1(byte r1) {
        if(R1>63 || R1<0)
            System.out.println("Invalid Register R1 number");
        else
            R1 = r1;
    }

    public int getOpcode() {
        return opcode;
//        return value >> 12;
    }


    public int getR1() {
//        (value & 4032)>>6
        return R1;
    }
//    public int getImmediateOrR2() {
//        return (value & 63);
//    }


}
