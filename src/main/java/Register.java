public abstract class Register {

    private int value;
    private static int min;
    private static int max;

    public static void setMin(int min) {
        Register.min = min;
    }

    public static void setMax(int max) {
        Register.max = max;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if(value>this.max || value<this.min)
            System.out.println("Value out of range");
        else
            this.value=value;
    }

    public static int getMin() {
        return min;
    }

    public static int getMax() {
        return max;
    }




}
