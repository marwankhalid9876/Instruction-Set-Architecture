public class I_Instruction extends InstructionWord{
    private byte immediate;

    public int getImmediate() {
        return immediate;
    }

    @Override
    public String toString() {
        return super.toString()  +
                ", immediate=" + immediate + ", isValid: " + super.isValid()+
                '}';
    }

    public void setImmediate(byte immediate) throws Exception {
        if(immediate>31 || immediate<-32)
            throw new Exception("Invalid immediate number! Your immediate number must be between -32 and 31 inclusive");
        else
            this.immediate = immediate;
    }
}
