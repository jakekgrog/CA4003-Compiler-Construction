public class SymbolTableEntry
{
    String id;
    DataTypes type;
    DataTypes declType;
    String scope;

    public SymbolTableEntry() {
        this.id = "";
        this.type = DataTypes.unknown;
        DataTypes declType = DataTypes.unknown;
        this.scope = "";
    }

    public SymbolTableEntry(String id, DataTypes type, DataTypes declType, String scope) {
        this.id = id;
        this.type = type;
        this.declType = declType;
        this.scope = scope;
    }
}