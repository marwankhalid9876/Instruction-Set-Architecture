public class R_Instruction extends InstructionWord{
    private int R2;

    public String toString(){
        return super.toString()  +
                ", R2=" + R2 +  ", isValid: " + super.isValid()+
                '}';
    }
    public int getR2() {
        return R2;
    }

    public void setR2(int r2) throws Exception {
        if(R2>63 || R2<0)
            throw new Exception("Invalid Register number! Your register number must be between 0 and 63 inclusive");
        else
            R2 = r2;
    }

    public static void main(String[] args) {
    }
}
