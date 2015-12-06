package cards;

//Represents an Energy card
public class Energy extends Card {
    
    public final Type eType;
    
    public Energy(Type t) {
        super(t.toString() + " Energy");
        eType = t;

    }
    
    public Energy(String s) {
        super(s.toUpperCase() + " Energy");
        eType = Type.valueOf(s.toUpperCase());
    }
    
    public String toString() {
        return eType.toString() + " Energy";
    }
}
