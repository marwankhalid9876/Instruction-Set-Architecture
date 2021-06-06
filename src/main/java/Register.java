public abstract class Register {

    private byte value;
    protected int min;
    protected int max;

    public int getValue() {
        if (this.max == 255)
            return value & 0xFF;
        else
            return value;
    }


    public void setValue(int value) {
        this.value = (byte) value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String toString()
    {
        return getValue() + "";
    }

    public static void main(String[] args) {
        System.out.println((byte)(255));

    }

}
