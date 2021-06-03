public class I_Instruction extends InstructionWord{
    private int immediate;

    public int getImmediate() {
        return immediate;
    }

    public void setImmediate(int immediate) {
        if(immediate>31 || immediate<-32)
            System.out.println("Invalid immediate value!");
        else
            this.immediate = immediate;
    }
}
