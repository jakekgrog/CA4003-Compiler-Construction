class STTest {
    public static void main(String[] args) {
        SymbolTable st = new SymbolTable();

        System.out.println("ADDING NEW SYMBOL");
        st.putSymbol("a", "unknown", DataTypes.function, "main");
        
        st.printStack();
        st.printHashTable();
        System.out.println("\n\n");
        
        System.out.println("OPENING A NEW SCOPE....");
        st.openScope();
       
        st.printStack();
        st.printHashTable();
        System.out.println("\n\n");
        
        System.out.println("ADDING TWO NEW SYMBOLS....");
        st.putSymbol("a", "bool", DataTypes.function, "func");
        st.putSymbol("b", "number", DataTypes.function, "func");
        
        st.printStack();
        st.printHashTable();
        System.out.println("\n\n");
        

        System.out.println("CLOSING SCOPE....");
        st.closeScope();
        
        st.printStack();
        st.printHashTable();
        System.out.println("\n\n");
    }
}