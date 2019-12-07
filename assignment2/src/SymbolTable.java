import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Arrays;

public class SymbolTable
{

    final String marker = " ";

    Hashtable<Integer, LinkedList<SymbolTableEntry>> hashTable;
    Stack<String> undoStack;

    public SymbolTable() {
        this.hashTable = new Hashtable<>();
        this.undoStack = new Stack<>();
        this.undoStack.push(marker);
    }

    public Object getSymbol(String id) {
        Integer key = id.hashCode();

        if (hashTable.containsKey(key)) {
            LinkedList bucket = hashTable.get(key);

            // Search the bucket for the symbol entry
            ListIterator listIter = bucket.listIterator();
            while (listIter.hasNext()) {
                SymbolTableEntry smbl = (SymbolTableEntry)listIter.next();
                if (smbl.id.equals(id))
                    return (Object)smbl;
            }
        } 
        return (Object) new SymbolTableEntry();      
    }

    public void putSymbol(String id, DataTypes type, DataTypes declType, String scope) {
        SymbolTableEntry newEntry = new SymbolTableEntry(id, type, declType, scope);
        
        // Push symbol to undo stack
        undoStack.add(id);
        
        Integer key = id.hashCode();
        if (hashTable.containsKey(key)) {
            LinkedList bucket = hashTable.get(key);
            bucket.offerFirst(newEntry);
        } else {
            LinkedList bucket = new LinkedList<>(Arrays.asList(newEntry));
            hashTable.put(key, bucket);
        }
    }

    public void openScope() {
        undoStack.add(marker);
    }

    public void closeScope() {
        String symbol = undoStack.pop();
        
        while (!symbol.equals(marker)) {
            Integer key = symbol.hashCode();
            LinkedList bucket = hashTable.get(key);

            ListIterator listIter = bucket.listIterator();
            while (listIter.hasNext()) {
                SymbolTableEntry smbl = (SymbolTableEntry)listIter.next();
                if (smbl.id.equals(symbol))
                    bucket.remove(smbl);
            }
            symbol = undoStack.pop();
        }
    }

    public void printStack() {
        System.out.println("---------PRINTING UNDO STACK---------");
        System.out.println(Arrays.toString(this.undoStack.toArray()));
        System.out.println("-------------------------------------");
    }

    public void printHashTable() {
        System.out.println("--------PRINTING HASH TABLE----------");
        System.out.println(hashTable.toString());
        System.out.println("-------------------------------------");
    }
}