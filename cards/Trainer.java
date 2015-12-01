package cards;

//Represents a trainer card
public class Trainer extends Card {
    public final String name;
    public final boolean isSupporter;
    public final Effect cardEffect;
    
    public Trainer(String name, boolean isSupporter, Effect cardEffect) {
        this.name = name;
        this.isSupporter = isSupporter;
        this.cardEffect = cardEffect;
    }
    
    public String toString() {
        return name;
    }
}
