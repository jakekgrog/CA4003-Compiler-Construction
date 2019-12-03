import java.util.LinkedList;
import java.util.Stack;
import java.util.Hashtable;

public class SymbolTable
{


    private final SymbolTableEntry marker = new SymbolTableEntry("MRKR", "MRKR", "MRKR");

    Hashtable<Integer, LinkedList<SymbolTableEntry>> hashTable;
    Stack<SymbolTableEntry> undoStack;

    public SymbolTable() {
        this.hashTable = new Hashtable<>();
        this.undoStack = new Stack<>();

        this.undoStack.push(marker);
    }

    public void getSymbol(String id) {
        Integer key = id.hashCode();
        LinkedList bucket = hashTable.get(key);
    }

    public void putSymbol() {

    }

    public void openScope() {
        //
    }

    public void closeScope() {
        //
    }
}