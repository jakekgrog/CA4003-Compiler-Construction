import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class SemanticCheckVisitor implements CCALParserVisitor 
{

    private static String scope;
    SymbolTable st = new SymbolTable();
    private ArrayList<String> functions = new ArrayList<>();

    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    @Override
    public Object visit(Program node, Object data) {
        scope = "global";
        node.childrenAccept(this, data);
        
        // Check all functions have been used
        
        // Check all variables have been read and written to. 

        return data;
    }

    @Override
    public Object visit(DeclList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(VarDecl node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data); // LIdent
        DataTypes type = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data); // Type

        SymbolTableEntry e = (SymbolTableEntry)st.getSymbol(id);
        if (id.equalsIgnoreCase(e.id) && scope.equals(e.scope)) {
            System.out.println("Declaration Error: '" + id + "' already declared in " + scope + " scope");
        } else {
            st.putSymbol(id, type.toString(), DataTypes.varDecl, scope);
        }
        return data;
    }

    @Override
    public Object visit(ConstDecl node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes declared_type = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data); // LHS Type
        
        DataTypes actual_type = (DataTypes) node.jjtGetChild(2).jjtAccept(this, data); // RHS Type
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);

        if (id.equalsIgnoreCase(e.id) && scope.equals(e.scope)) {
            System.out.println("Already declared in this scope");
        } else if (actual_type.equals(DataTypes.unknown)) {
            System.out.println("Variable used before declaration");
        } else if ( declared_type != actual_type) {
            System.out.println("Assignment of different types");
        } else {
            st.putSymbol(id, declared_type.toString(), DataTypes.constDecl, scope);
        }
        
        return data;
    }

    @Override
    public Object visit(Ident node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(LIdent node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(RIdent node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);

        if (e.id.equals("")) {
            System.out.println("Identifier used before being declared");
            return DataTypes.unknown;
        } else {
            return toDataType(e.type);
        }
    }

    @Override
    public Object visit(FunctionList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Function node, Object data) {
        DataTypes type = (DataTypes) node.jjtGetChild(0).jjtAccept(this, data); // Type
        String id = (String) node.jjtGetChild(1).jjtAccept(this, data); // LIdent

        // Check function with ID doesn't already exist
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
        if (id.equals(e.id) && e.declType == DataTypes.function) {
            System.out.println("Declaration Error: Function cannot be declared more than once");
        }

        String signature = type.toString() + "(";
        scope = id;
        String params = (String) node.jjtGetChild(2).jjtAccept(this, data);  // ParamList

        signature = signature + params + ")";

        st.putSymbol(id, signature, DataTypes.function, scope);
        
        functions.add(id); // Add to check later if it has been used

        
        node.jjtGetChild(3).jjtAccept(this, data);  // Open Block
        node.jjtGetChild(4).jjtAccept(this, data);  // DeclList
        
        node.jjtGetChild(5).jjtAccept(this, data);  // Statement Block

        DataTypes returnType = (DataTypes) node.jjtGetChild(6).jjtAccept(this, data);   // Return type
        if (returnType != type) {
            System.out.println("Type Error: Function (" + id + ") must return " + type);
        }

        node.jjtGetChild(7).jjtAccept(this, data);  // Close Block
        scope = "global";
        return data;
    }

    @Override
    public Object visit(Return node, Object data) {
        if (node.jjtGetNumChildren() == 0) {
            return DataTypes.unknown;
        }
        else if (node.jjtGetNumChildren() == 1) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        }
        return DataTypes.unknown;
    }

    @Override
    public Object visit(Type node, Object data) {
        String val = (String)node.jjtGetValue();

        if (val.equalsIgnoreCase("boolean")) {
            return DataTypes.bool;
        }
        if (val.equalsIgnoreCase("integer")) {
            return DataTypes.number;
        }
        return DataTypes.unknown;
    }

    @Override
    public Object visit(ParamList node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(NempParamList node, Object data) {
        String params = "";

        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes type = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);

        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
        if (id.equalsIgnoreCase(e.id) && scope.equals(e.scope)) {
            System.out.println("Declaration Error: Variable " + id + " already declared in " + scope + " scope");
        } else {
            st.putSymbol(id, type.toString(), type, scope);
        }

        params = params + type.toString();

        if (node.jjtGetNumChildren() == 3) {
            params = params + ", " + (String) node.jjtGetChild(2).jjtAccept(this, data);
        }
        return params;
    }

    @Override
    public Object visit(Main node, Object data) {
        scope = "main";
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(StatementBlock node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Statement node, Object data) {
        return data;
    }

    @Override
    public Object visit(ArgList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Number node, Object data) {
        return DataTypes.number;
    }

    @Override
    public Object visit(CompOp node, Object data) {
        return data;
    }

    @Override
    public Object visit(ArithOp node, Object data) {
        DataTypes leftOpType = (DataTypes) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes rightOpType = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);
        if (leftOpType != rightOpType) {
            System.out.println("Value Error: Incompatible types " + leftOpType + " and " + rightOpType);
        } else {
            return leftOpType;
        }
        return DataTypes.unknown;
    }

    @Override
    public Object visit(Boolean node, Object data) {
        return DataTypes.bool;
    }

    @Override
    public Object visit(NegateIdent node, Object data) {
        node.childrenAccept(this, data);
        return DataTypes.number;
    }

    @Override
    public Object visit(BoolOp node, Object data) {
        return data;
    }

    @Override
    public Object visit(Skip node, Object data) {
        return data;
    }

    @Override
    public Object visit(FuncCall node, Object data) {
        // Check function has been declared
        SimpleNode rIdNode = (SimpleNode) node.jjtGetChild(0);
        SimpleNode idNode = (SimpleNode) rIdNode.jjtGetChild(0);
        String id = (String) idNode.jjtGetValue();
        
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
        if (!id.equals(e.id)) {
            System.out.println("Call Error: Function (" + id + ") has not been defined");
        }
        // Check it has correct number of arguments
        // Check arguments are of correct type
        // Remove from functions list
        // return functions return type
        return DataTypes.number;
    }

    @Override
    public Object visit(Assignment node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        
        // Check id has been declared
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
        if (!id.equalsIgnoreCase(e.id)) {
            System.out.println("Assignment Error: Variable (" + id + ") must be declared before use");
        }
        if (e.declType == DataTypes.constDecl) {
            System.out.println("Assignment Error: Cannot assign to a constant");
        }

        DataTypes exp = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);
        if (toDataType(e.type) != exp) {
            System.out.println("Value Error: Cannot assign " + e.type + " to " + exp);
        }

        return DataTypes.unknown;
    }

    @Override
    public Object visit(OpenBlock node, Object data) {
        st.openScope();
        return data;
    }

    @Override
    public Object visit(CloseBlock node, Object data) {
        st.closeScope();
        return data;
    }

    private DataTypes toDataType(String s) {
        if (s.equals("number")) {
            return DataTypes.number;
        }
        if (s.equals("bool")) {
            return DataTypes.bool;
        }
        if (s.equals("varDecl")) {
            return DataTypes.varDecl;
        }
        if (s.equals("constDecl")) {
            return DataTypes.constDecl;
        }
        if (s.equals("assign")) {
            return DataTypes.assign;
        }
        if (s.equals("function")) {
            return DataTypes.function;
        }
        if (s.equals("compOp")) {
            return DataTypes.compOp;
        }

        return DataTypes.unknown;
    }

}