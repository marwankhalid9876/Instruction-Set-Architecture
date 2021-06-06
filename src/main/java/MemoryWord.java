public class MemoryWord {

    private byte value;

    @Override
    public String toString() {
        return "MemoryWord{" +
                "value=" + value +
                '}';
    }

    public byte getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = (byte) value;
    }


}