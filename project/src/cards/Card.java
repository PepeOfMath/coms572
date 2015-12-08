package cards;

//Super class for all the Card classes
public abstract class Card {
    public final String name;
    
    public Card(String name) {
        this.name = name;
    }
    
    public boolean equals(Object o) {
    	if (o == null || !(o instanceof Card)) return false;
    	Card c = (Card)o;
    	return this.name.equals(c.name);
    }
}
