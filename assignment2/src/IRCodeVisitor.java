import com.sun.management.OperatingSystemMXBean;

public class IRCodeVisitor implements CCALParserVisitor
{
    
    private int lblVal = 1;
    private int tmpVal = 1;
    private int paramNum = 0;


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
        node.jjtGetChild(2).jjtAccept(this, data);
        node.jjtGetChild(5).jjtAccept(this, data);
        node.jjtGetChild(6).jjtAccept(this, data);
        return data;
    }

    @Override
    public Object visit(Return node, Object data) {
        if (node.jjtGetNumChildren() == 0) {
            System.out.println("\t" + "return");
            return data;
        }

        SimpleNode rChild = (SimpleNode) node.jjtGetChild(0);
        String rChildType = rChild.toString();

        if (!rChildType.equals("ArithOp")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            System.out.println("\t" + "return " + var);
        } else {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String lOp = (String) rChild.jjtGetChild(0).jjtAccept(this, data);
            String operator = (String) rChild.jjtGetValue();

            System.out.println("\t" + "return t" + (tmpVal-1));
        }

        return data;
    }

    @Override
    public Object visit(Type node, Object data) {
        return data;
    }

    @Override
    public Object visit(ParamList node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(NempParamList node, Object data) {
        String paramId = (String) node.jjtGetChild(0).jjtAccept(this, data);
        System.out.println("\t" + paramId + " = getparam " + paramNum);
        paramNum++;
        if (node.jjtGetNumChildren() == 3) {
            node.jjtGetChild(2).jjtAccept(this, data);
        }
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
        if (((String) node.jjtGetValue()).equals("if")) {
            node.jjtGetChild(0).jjtAccept(this, data);
            System.out.println("\t" + "ifz t" + (tmpVal-1) + " goto L" + lblVal);
            node.jjtGetChild(1).jjtAccept(this, data);
            node.jjtGetChild(2).jjtAccept(this, data);
        } else if (((String) node.jjtGetValue()).equals("else")) {
            System.out.println("L"+(lblVal-1)+":");
            lblVal++;
            node.jjtGetChild(0).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Number node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(CompOp node, Object data) {
        node.childrenAccept(this, data);
        System.out.println("\t" + "t" + tmpVal + " = t" + (tmpVal-2) + " " + ((String)node.jjtGetValue()) + " t" + (tmpVal-1));
        tmpVal++;
        return data;
    }

    @Override
    public Object visit(ArithOp node, Object data) {
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
        int count = printArgs((ArgList)node.jjtGetChild(1), data);
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        
        String instruction = "call " + id + " " + count;

        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        String parentType = parent.toString();
        if (parentType.equals("StatementBlock")) {
            System.out.println("\t" + instruction);
        }
        return instruction;
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
        } else if (rChild.jjtGetChild(1).toString().equals("FuncCall")) {
            String var = (String) node.jjtGetChild(0).jjtAccept(this, data);

            String instruction = (String) rChild.jjtGetChild(1).jjtAccept(this, data);
            System.out.println("\t" + var + " " + instruction);
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

    private int printArgs(ArgList node, Object data) {
        int count = 0;
        while (node.jjtGetNumChildren() != 0) {
            count++;
            System.out.println("\t" + "param " + node.jjtGetChild(0).jjtAccept(this, data));
            node = (ArgList) node.jjtGetChild(1);
        }
        return count;
    }
}