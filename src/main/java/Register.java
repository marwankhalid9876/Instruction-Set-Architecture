public abstract class Register {

    private byte value;
    protected static int min;
    protected static int max;

    public int getValue() {
        if(this.max == 255)
            return value & 0xFF;
        else
            return value;
    }

    public void setValue(int value) {
        this.value= (byte) value;
    }

    public static int getMin() {
        return min;
    }

    public static int getMax() {
        return max;
    }




}
