public class StatusRegister {

    private Boolean[] flags;

    public StatusRegister()
    {
       flags = new Boolean[8];
    }
    public Boolean getZeroFlag() {
        return flags[0];
    }
    public Boolean getSignFlag() {
        return flags[1];
    }
    public Boolean getNegativeFlag() {
        return flags[2];
    }
    public Boolean getOverflowFlag() {
        return flags[3];
    }
    public Boolean getCarryFlag() {
        return flags[4];
    }

    public void setZeroFlag(boolean value){
        flags[0]=value;
    }
    public void setSignFlag(boolean value){
        flags[1]=value;
    }
    public void setNegativeFlag(boolean value){
        flags[2]=value;
    }
    public void setOverflowFlag(boolean value){
        flags[3]=value;
    }
    public void setCarryFlag(boolean value){
        flags[4]=value;
    }

}
