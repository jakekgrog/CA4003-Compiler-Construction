public class SymbolTableEntry
{
    String id;
    String type;
    String scope;

    public SymbolTableEntry() {
        this.id = "";
        this.type = "";
        this.scope = "";
    }

    public SymbolTableEntry(String id, String type, String scope) {
        this.id = id;
        this.type = type;
        this.scope = scope;
    }
}