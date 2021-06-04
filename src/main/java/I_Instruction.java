public class I_Instruction extends InstructionWord{
    private byte immediate;

    public int getImmediate() {
        return immediate;
    }

    public void setImmediate(byte immediate) {
        if(immediate>31 || immediate<-32)
            System.out.println("Invalid immediate value!");
        else
            this.immediate = immediate;
    }
}
