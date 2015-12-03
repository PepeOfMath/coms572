package cards;

//Represents an Energy card
//  Should be essentially done
public class Energy extends Card {
    
    
    public final Type eType;
    
    public Energy(Type t) {
        eType = t;
    }
    
    public Energy(String s) {
        eType = Type.valueOf(s.toUpperCase());
    }
    
    public String toString() {
        return eType.toString() + " Energy";
    }
}
