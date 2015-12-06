package cards;

//Represents a trainer card
public class Trainer extends Card {
    //public final String name;
    public final boolean isSupporter;
    public final boolean targetsPokemon;
    public final String cardEffect;
    
    public Trainer(String name, boolean isSupporter, boolean targetsPokemon, String cardEffect) {
        super(name);
        //this.name = name;
        this.isSupporter = isSupporter;
        this.targetsPokemon = targetsPokemon;
        this.cardEffect = cardEffect;
        
    }
    
    public String toString() {
        return name;
    }
}
