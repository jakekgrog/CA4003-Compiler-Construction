import java.util.LinkedList;

public class SemanticCheckVisitor implements CCALParserVisitor 
{

    private static String scope;
    SymbolTable st = new SymbolTable();

    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    @Override
    public Object visit(Program node, Object data) {
        scope = "global";
        node.childrenAccept(this, data);
        
        return data;
    }

    @Override
    public Object visit(DeclList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(VarDecl node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data); // Ident
        DataTypes type = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data); // Type
        
        
        SymbolTableEntry e = (SymbolTableEntry)st.getSymbol(id);
        if (id.equalsIgnoreCase(e.id) && scope.equals(e.scope)) {
            // SEM_CHECK: 2
            // Check if identifier already exists in scope;
            System.out.println("Declaration Error: '" + id + "' already declared in this scope");
        } else {
            st.putSymbol(id, type, DataTypes.varDecl, scope);
        }
        return data;
    }

    @Override
    public Object visit(ConstDecl node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this,data); // Identifier
        DataTypes declared_type = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data); // Type
        DataTypes actual_type = (DataTypes) node.jjtGetChild(2).jjtAccept(this, data); // 
        
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);

        if (id.equalsIgnoreCase(e.id) && scope.equals(e.scope)){
            // SEM_CHECK: 2
            // Check if identifier already exists in scope;
            System.out.println("Declaration Error: '" + id + "' already declared in this scope");
        } else if (declared_type != actual_type) {
            // SEM_CHECK: 3
            // Type checks
            System.out.println("Type error: " + actual_type + " cannot be assigned to " + declared_type);
        } else {
            st.putSymbol(id, declared_type, DataTypes.constDecl, scope);
        }
        
        return data;
    }

    @Override
    public Object visit(FunctionList node, Object data) {
        return data;
    }

    @Override
    public Object visit(Function node, Object data) {
        return data;
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
        if (val.equalsIgnoreCase("void")) {
            return DataTypes.voidType;
        }
        return DataTypes.unknown;
    }

    @Override
    public Object visit(ParamList node, Object data) {
        return data;
    }

    @Override
    public Object visit(NempParamList node, Object data) {
        return data;
    }

    @Override
    public Object visit(Main node, Object data) {
        return data;
    }

    @Override
    public Object visit(StatementBlock node, Object data) {
        return data;
    }

    @Override
    public Object visit(Statement node, Object data) {
        return data;
    }

    @Override
    public Object visit(ArgList node, Object data) {
        return data;
    }

    @Override
    public Object visit(Ident node, Object data) {
        return node.value;
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
        return data;
    }

    @Override
    public Object visit(Boolean node, Object data) {
        return DataTypes.bool;
    }

    @Override
    public Object visit(NegateIdent node, Object data) {
        return data;
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
        return data;
    }

    @Override
    public Object visit(Assignment node, Object data) {
        return data;
    }

    @Override
    public Object visit(Return node, Object data) {
        return data;
    }

}