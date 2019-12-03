import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Arrays;

public class SymbolTable
{


    final String marker = "";

    Hashtable<Integer, LinkedList<SymbolTableEntry>> hashTable;
    Stack<String> undoStack;

    public SymbolTable() {
        this.hashTable = new Hashtable<>();
        this.undoStack = new Stack<>();

        this.undoStack.push(marker);
    }

    public Object getSymbol(String id) {
        Integer key = id.hashCode();
        LinkedList bucket = hashTable.get(key);
        
        Object obj;

        // Search the bucket for the symbol entry
        ListIterator listIter = bucket.listIterator();
        while (listIter.hasNext()) {
            SymbolTableEntry smbl = (SymbolTableEntry)listIter.next();
            if (smbl.id.equals(id)) {
                obj = smbl;
                return obj;
            }
        }
        obj = "NOTFOUND";
        return obj;      
    }

    public void putSymbol() {
        //
    }

    public void openScope() {
        undoStack.add(marker);
    }

    public void closeScope() {
        ;
    }

    public void printStack() {
        System.out.println("---------PRINTING UNDO STACK---------");
        System.out.println(Arrays.toString(this.undoStack.toArray()));
        System.out.println("-------------------------------------");
    }
}