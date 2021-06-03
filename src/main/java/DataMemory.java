public class DataMemory {
    private static DataMemory dataMemory;
    private static MemoryWord[] DataMemoryArray;

    private DataMemory(){
        this.DataMemoryArray = new MemoryWord[2048];
        for(int i=0; i<2048; i++)
            DataMemoryArray[i]=new MemoryWord();
    }

    public static MemoryWord[] getDataMemoryArray() {
        return DataMemoryArray;
    }

    public static DataMemory getInstance()
    {
        if (dataMemory == null)
        {
            dataMemory = new DataMemory();
        }

        return dataMemory;
    }


}
