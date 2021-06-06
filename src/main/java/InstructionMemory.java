import java.util.Arrays;

public class InstructionMemory {
    private static InstructionMemory instructionMemory;
    private InstructionWord[] instructionMemoryArray;
    private InstructionMemory(){
        this.instructionMemoryArray = new InstructionWord[1024];
        for(int i=0; i<1024; i++)
            instructionMemoryArray[i] = new InstructionWord();
    }

    public String toString()
    {
        String res = "";
        for(int i=0; i<instructionMemoryArray.length; i++)
            res += "Block " + i + instructionMemoryArray[i] + "\n";
        return res;
    }

    public InstructionWord[] getInstructionMemoryArray() {
        return instructionMemoryArray;
    }

    public static InstructionMemory getInstance()
    {
        if (instructionMemory == null)
        {
            instructionMemory = new InstructionMemory();
        }

        return instructionMemory;
    }
}
