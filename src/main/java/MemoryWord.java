public class MemoryWord {
    private static final int min = -32768;
    private static final int max = 32767;
    private int value;

    public static int getMin() {
        return min;
    }

    public static int getMax() {
        return max;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}