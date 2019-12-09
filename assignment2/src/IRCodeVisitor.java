import com.sun.management.OperatingSystemMXBean;

public class IRCodeVisitor implements CCALParserVisitor
{
    
    private int lblVal = 1;
    private int tmpVal = 1;


    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    @Override
    public Object visit(Program node, Object data) {
        System.out.println("global:");
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
        return data;
    }

    @Override
    public Object visit(ConstDecl node, Object data) {

        SimpleNode rChild = (SimpleNode) node.jjtGetChild(2);
        String rChildType = rChild.toString();

        if (!rChildType.equals("ArithOp")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String exp = (String) node.jjtGetChild(2).jjtAccept(this, data);
            System.out.println("\t" + var + " = " + exp);

        } else if (!rChild.jjtGetChild(1).toString().equals("ArithOp")){
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);

            String lOp = (String) rChild.jjtGetChild(0).jjtAccept(this, data);
            String rOp = (String) rChild.jjtGetChild(1).jjtAccept(this, data);
            String operator = (String) rChild.jjtGetValue();

            System.out.println("\t" + var + " = " + lOp + " " + operator + " " + rOp);
        } else if (rChild.jjtGetChild(1).toString().equals("ArithOp")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String lOp = (String) rChild.jjtGetChild(0).jjtAccept(this, data);
            String operator = (String) rChild.jjtGetValue();
            
            rChild.jjtGetChild(1).jjtAccept(this, data);

            System.out.println("\t" + var + " = " + lOp + " " + operator + " t" + (tmpVal-1));
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
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(FunctionList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Function node, Object data) {
        String functionId = (String) node.jjtGetChild(1).jjtAccept(this, data);
        System.out.println(functionId + ":");
        node.jjtGetChild(5).jjtAccept(this, data);
        node.jjtGetChild(6).jjtAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Return node, Object data) {
        System.out.println("\t" + "return");
        return data;
    }

    @Override
    public Object visit(Type node, Object data) {
        return data;
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
        System.out.println("main:");
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
    public Object visit(Number node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(CompOp node, Object data) {
        return data;
    }

    @Override
    public Object visit(ArithOp node, Object data) {

        // If rightOp = ArithOp, left + temp
        // If rightOp != ArithOp, left + right

        String operator = (String) node.jjtGetValue();
        SimpleNode lOp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rOp = (SimpleNode) node.jjtGetChild(1);

        String lOpType = lOp.toString();
        String rOpType = rOp.toString();

        if (!rOpType.equals("ArithOp")) {
            String rVal = (String) rOp.jjtAccept(this, data);
            String lVal = (String) lOp.jjtAccept(this, data);
            System.out.println("\t" + "t" + tmpVal + " = " + lVal + " " + operator + " " + rVal);
        } else {
            rOp.jjtAccept(this, data);
            System.out.println("\t" + "t" + tmpVal + " = " + (String) lOp.jjtAccept(this, data) + " " + operator + " t" + (tmpVal-1));
        }

        tmpVal++;
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
    public Object visit(ArgList node, Object data) {
        return data;
    }

    @Override
    public Object visit(FuncCall node, Object data) {
        return data;
    }

    @Override
    public Object visit(Assignment node, Object data) {
        SimpleNode rChild = (SimpleNode) node.jjtGetChild(1);
        String rChildType = rChild.toString();

        if (!rChildType.equals("ArithOp")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String exp = (String) node.jjtGetChild(1).jjtAccept(this, data);
            System.out.println("\t" + var + " = " + exp);

        } else if (!rChild.jjtGetChild(1).toString().equals("ArithOp")){
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);

            String lOp = (String) rChild.jjtGetChild(0).jjtAccept(this, data);
            String rOp = (String) rChild.jjtGetChild(1).jjtAccept(this, data);
            String operator = (String) rChild.jjtGetValue();

            System.out.println("\t" + var + " = " + lOp + " " + operator + " " + rOp);
        } else if (rChild.jjtGetChild(1).toString().equals("ArithOp")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String lOp = (String) rChild.jjtGetChild(0).jjtAccept(this, data);
            String operator = (String) rChild.jjtGetValue();
            
            rChild.jjtGetChild(1).jjtAccept(this, data);

            System.out.println("\t" + var + " = " + lOp + " " + operator + " t" + (tmpVal-1));
        }
        return data;
    }

    @Override
    public Object visit(OpenBlock node, Object data) {
        return data;
    }

    @Override
    public Object visit(CloseBlock node, Object data) {
        return data;
    }

    private Object arithAddrCodeBuilder(SimpleNode node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);
        String parent = node.jjtGetParent().toString();
        if ("Assignment".equals(parent)) {
            return (child1 + " " + node.value + " " + child2);
        }
        String t = "t" + tmpVal;
        tmpVal++;
        System.out.println("\t" + t + " = " + child1 + " " + node.value + " " + child2);
        return t;
    }
}