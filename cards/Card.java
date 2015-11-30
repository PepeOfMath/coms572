package cards;

public abstract class Card {
    private String cName;

    public Card(String name) {
        cName = name;
    }
    
    public String getName() {
        return cName;
    }
}
