// public class PrintVisitor implements CCALParserVisitor 
// {

//     @Override
//     public Object visit(SimpleNode node, Object data) {
//         throw new RuntimeException("Visit SimpleNode");
//     }

//     @Override
//     public Object visit(Program node, Object data) {
//         node.jjtGetChild(0).jjtAccept(this, data);
//         return (data);
//     }

//     @Override
//     public Object visit(DeclList node, Object data) {
//         node.jjtGetChild(0).jjtAccept(this, data);
//         node.jjtGetChild(1).jjtAccept(this, data);
//         return (data);
//     }

//     @Override
//     public Object visit(VarDecl node, Object data) {
//         System.out.print(node.value + " ");
//         node.jjtGetChild
//     }

//     @Override
//     public Object visit(ConstDecl node, Object data) {

//     }

//     @Override
//     public Object visit(FunctionList node, Object data) {

//     }

//     @Override
//     public Object visit(Function node, Object data) {

//     }

//     @Override
//     public Object visit(Type node, Object data) {

//     }

//     @Override
//     public Object visit(ParamList node, Object data) {

//     }

//     @Override
//     public Object visit(NempParamList node, Object data) {

//     }

//     @Override
//     public Object visit(Main node, Object data) {

//     }

//     @Override
//     public Object visit(StatementBlock node, Object data) {

//     }

//     @Override
//     public Object visit(Statement node, Object data) {

//     }

//     @Override
//     public Object visit(ArgList node, Object data) {

//     }

//     @Override
//     public Object visit(Ident node, Object data) {

//     }

//     @Override
//     public Object visit(Number node, Object data) {

//     }

// }