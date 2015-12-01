package cards;

//Represents an Energy card
//  Should be essentially done
public class Energy extends Card {

    public enum Type {
        WATER, GRASS, FIRE, FIGHTING, PSYCHIC, DARK, STEEL, FAIRY, DRAGON, ELECTRIC, NORMAL
    }
    
    
    
    public final Type eType;
    
    public Card(Type t) {
        eType = t;
    }
    
    public Card(String s) {
        eType = Type.valueOf(s.toUpperCase());
    }
    
    public String toString() {
        return eType.toString() + " Energy";
    }
}
