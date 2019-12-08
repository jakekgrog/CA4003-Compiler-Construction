import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;

public class SemanticCheckVisitor implements CCALParserVisitor 
{

    private static String scope;
    SymbolTable st = new SymbolTable();
    private ArrayList<String> functions = new ArrayList<>();
    private Hashtable<String, Hashtable<String, Integer>> scopeWriteRead = new Hashtable<>();

    
    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    @Override
    public Object visit(Program node, Object data) {
        scope = "global";
        node.childrenAccept(this, data);
        
        // Check all functions have been used
        if (functions.size() > 0) {
            for (String func : functions) {
                System.out.println("Program Error: Function (" + func +") was declared but never used");
            }
        }
        
        // Check all variables have been read and written to.
        checkScopeWriteRead(scope);

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
            scopeDeclared(id, scope);
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
            scopeDeclared(id, scope);
            scopeWrite(id, scope);
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
            scopeRead(id, scope);
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

        scope = id; // The scope of a function is the function ID

        functions.add(id); // To check later if it has been used      

        node.jjtGetChild(3).jjtAccept(this, data);  // Open Block

        String signature = type.toString() + "(";
        String params = (String) node.jjtGetChild(2).jjtAccept(this, data);  // ParamList
        signature = signature + params + ")";

        node.jjtGetChild(4).jjtAccept(this, data);  // DeclList
        
        node.jjtGetChild(5).jjtAccept(this, data);  // Statement Block

        DataTypes returnType = (DataTypes) node.jjtGetChild(6).jjtAccept(this, data);   // Return type
        if (returnType != type) {
            System.out.println("Type Error: Function (" + id + ") must return " + type);
        }
        
        checkScopeWriteRead(scope);

        node.jjtGetChild(7).jjtAccept(this, data);  // Close Block
        st.putSymbol(id, signature, DataTypes.function, scope);
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
            params = params + "," + (String) node.jjtGetChild(2).jjtAccept(this, data);
        }
        return params;
    }

    @Override
    public Object visit(Main node, Object data) {
        scope = "main";
        node.childrenAccept(this, data);
        scope = "global";
        return data;
    }

    @Override
    public Object visit(StatementBlock node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Statement node, Object data) {
        if (node.jjtGetNumChildren() == 3) {           // IF Statement
            node.jjtGetChild(0).jjtAccept(this, data);
            node.jjtGetChild(1).jjtAccept(this, data);
            node.jjtGetChild(2).jjtAccept(this, data);
        } else if (node.jjtGetNumChildren() == 2) {    // WHILE Statement
            node.jjtGetChild(0).jjtAccept(this, data);
            node.jjtGetChild(1).jjtAccept(this, data);
        } else {                                       // ELSE Statement
            node.jjtGetChild(0).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Number node, Object data) {
        return DataTypes.number;
    }

    @Override
    public Object visit(CompOp node, Object data) {
        DataTypes leftOpType = (DataTypes) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes rightOpType = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);
        String operator = (String) node.jjtGetValue();
        
        if (!isValidComp(leftOpType, rightOpType, operator)){
            return DataTypes.unknown;
        }
        return DataTypes.bool;
    }

    @Override
    public Object visit(ArithOp node, Object data) {
        DataTypes leftOpType = (DataTypes) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes rightOpType = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);

        if (leftOpType != DataTypes.number | rightOpType != DataTypes.number) {
            System.out.println("Value Error: Incompatible types " + leftOpType + " and " + rightOpType);
        } else {
            SimpleNode rIdNode = (SimpleNode) node.jjtGetChild(0);
            SimpleNode idNode = (SimpleNode) rIdNode.jjtGetChild(0);
            String id = (String) idNode.jjtGetValue();
            scopeRead(id, scope);
            return DataTypes.number;
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
        DataTypes left = (DataTypes) node.jjtGetChild(0).jjtAccept(this, data);
        DataTypes right = (DataTypes) node.jjtGetChild(1).jjtAccept(this, data);
        String operator = (String) node.jjtGetValue();
        if (left != DataTypes.bool | right != DataTypes.bool) {
            System.out.println("Value Error: Cannot perform " + operator + " on types " + left + " and " + right);
        }
        return data;
    }

    @Override
    public Object visit(Skip node, Object data) {
        return data;
    }

    @Override
    public Object visit(ArgList node, Object data) {
        String params = "";
        if (node.jjtGetNumChildren() != 0) {
            SimpleNode rIdNode = (SimpleNode) node.jjtGetChild(0);
            SimpleNode idNode = (SimpleNode) rIdNode.jjtGetChild(0);
            String id = (String) idNode.jjtAccept(this, data);
            DataTypes type = (DataTypes) rIdNode.jjtAccept(this, data);

            // check the argument is valid
            SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
            if (!id.equals(e.id)) {
                System.out.println("Declaration Error: Variable " + id + " must be declared before use");
            }

            params = params + type.toString() + "," + node.jjtGetChild(1).jjtAccept(this, data);

        }
        return params;
    }

    @Override
    public Object visit(FuncCall node, Object data) {
        // Check function has been declared
        SimpleNode rIdNode = (SimpleNode) node.jjtGetChild(0);
        SimpleNode idNode = (SimpleNode) rIdNode.jjtGetChild(0);
        String id = (String) idNode.jjtGetValue();
        
        SymbolTableEntry e = (SymbolTableEntry) st.getSymbol(id);
        if (!id.equals(e.id)) {
            System.out.println("Invocation Error: Function (" + id + ") has not been defined");
        }
        
        // Check it has correct number of arguments
        Integer expectedNumArgs = getNumArgsFromSignature(e.type);
        DataTypes type = getReturnTypeFromSignature(e.type);

        String args = (String) node.jjtGetChild(1).jjtAccept(this, data);
        
        if (args.length() > 1){
            args = args.substring(0, args.length() - 1);
        }
        String signature = type.toString() + "(" + args.substring(0, args.length()) + ")";
        
        Integer actualNumArgs = getNumArgsFromSignature(signature);

        if (expectedNumArgs != actualNumArgs) {
            System.out.println("Invocation Error: Function (" + id + ") expects " + expectedNumArgs + " arguments but got " + actualNumArgs);
        }
        
        // Check arguments are of correct type
        ArrayList<DataTypes> expectedTypes = getArgumentTypesFromSignature(e.type);
        ArrayList<DataTypes> actualTypes = getArgumentTypesFromSignature(signature);

        for (int i = 0; i < expectedTypes.size() ; i++) {
            DataTypes exp = expectedTypes.get(i);
            DataTypes act = actualTypes.get(i);
            if (!exp.equals(act)) {
                System.out.println("Invocation Error: Expected type " + exp + " for argument " + i + " but got " + act);
            }
        }

        // Remove from functions list
        functions.remove(id);
        // return functions return type
        return getReturnTypeFromSignature(e.type);
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
            System.out.println("Value Error: Cannot assign " + exp + " to " + e.type);
        }
        scopeWrite(id, scope);
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

    private Integer getNumArgsFromSignature(String sig) {
        String[] parts = sig.split("\\(");
        String secondHalf = parts[1];
        String args = secondHalf.substring(0, secondHalf.length() - 1);
        String[] argv = args.split(",");
        if (argv[0].equals("")) {
            return 0;
        }
        return argv.length;
    }

    private DataTypes getReturnTypeFromSignature(String sig) {
        String[] parts = sig.split("\\(");
        return toDataType(parts[0]);
    }

    private ArrayList<DataTypes> getArgumentTypesFromSignature(String sig) {
        ArrayList<DataTypes> dataTypeArray = new ArrayList<>(); 
        
        String[] parts = sig.split("\\(");
        String secondHalf = parts[1];
        String args = secondHalf.substring(0, secondHalf.length() - 1);
        String[] argv = args.split(",");
        if (argv[0].equals("")) {
            return dataTypeArray;
        } else {
            for (String type : argv) {
                dataTypeArray.add(toDataType(type));
            }
            return dataTypeArray;
        }
    }

    private boolean isValidComp(DataTypes left, DataTypes right, String op) {

        boolean isValid = true;
        if (op.equals("==") || op.equals("!=")) {
            if ((left != DataTypes.bool | right != DataTypes.bool) & 
                (left != DataTypes.number | right != DataTypes.number)) {
                    isValid = false;
                    System.out.println("Type Error: Cannot compare types " + 
                                        left + " and " + right + 
                                        " with operator " + op);
            }
        } else {
            if (left != DataTypes.number | right != DataTypes.number) {
                isValid = false;
                System.out.println("Type Error: Cannot compare types " + 
                                    left + " and " + right + 
                                    " with operator " + op);
            }
        }
        return isValid;
    }

    private void scopeRead(String id, String scope) {
        Hashtable idInfo = scopeWriteRead.get(scope);
        idInfo.put(id, 3);
    }

    private void scopeWrite(String id, String scope) {
        Hashtable idInfo = scopeWriteRead.get(scope);
        idInfo.put(id, 2);
    }

    private void scopeDeclared(String id, String scope) {
        if (scopeWriteRead.get(scope) != null){
            Hashtable idInfo = scopeWriteRead.get(scope);
            idInfo.put(id, 1);
        } else {
            scopeWriteRead.put(scope, new Hashtable<String, Integer>());
            Hashtable idInfo = new Hashtable<String, Integer>();

            idInfo.put(id, 1);
            scopeWriteRead.put(scope, idInfo);
        }
    }

    private void checkScopeWriteRead(String scope) {
        Hashtable idInfo = scopeWriteRead.get(scope);
        idInfo.forEach((k, v) -> {
            Integer variableState = (Integer) v;
            if (variableState == 1 && scope != "global") {
                System.out.println("Variable Error: " + k + " has been declared but not written to or read from");
            } else if (variableState == 2 && scope != "main" && scope != "global") {
                System.out.println("Variable Error: " + k + " has been declared and written to but not read from");
            }
        });
    }
}