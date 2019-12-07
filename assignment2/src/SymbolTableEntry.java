public class SymbolTableEntry
{
    String id;
    String type;
    DataTypes declType;
    String scope;

    public SymbolTableEntry() {
        this.id = "";
        this.type = "";
        DataTypes declType = DataTypes.unknown;
        this.scope = "";
    }

    public SymbolTableEntry(String id, String type, DataTypes declType, String scope) {
        this.id = id;
        this.type = type;
        this.declType = declType;
        this.scope = scope;
    }
}