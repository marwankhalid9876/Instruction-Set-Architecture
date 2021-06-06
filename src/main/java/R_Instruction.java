public class R_Instruction extends InstructionWord{
    private int R2;

    public String toString(){
        return super.toString()  +
                "R2=" + R2 +
                '}';
    }
    public int getR2() {
        return R2;
    }

    public void setR2(int r2) {
        if(R2>63 || R2<0)
            System.out.println("Invalid Register R2 number");
        else
            R2 = r2;
    }
}
