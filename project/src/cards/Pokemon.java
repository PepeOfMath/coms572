package cards;

//Represents the characteristics of a single 'Pokemon' card
public class Pokemon extends Card {
    
    //public final String name;
    public final String evolvesFrom;
    public final int totalHP;
    public final Type type;
    public final Type weakness;
    public final Type resistance;
    public final int retreatCost;
    public final int[] atkCostI; //Should represent the cost of an attack (# of each energy?)
    public final int[] atkCostII;
    public final String atkNameI;
    public final String atkNameII;
    public final String atkI;
    public final String atkII;
    
    public Pokemon(String name, String evolvesFrom, int totalHP, Type type, Type weakness, Type resistance, int retreatCost, int[] atkCostI, String atkNameI, String atkI, int[] atkCostII, String atkNameII, String atkII) {
        super(name);
        //this.name = name;
        this.evolvesFrom = evolvesFrom;
        this.totalHP = totalHP;
        this.type = type;
        this.weakness = weakness;
        this.resistance = resistance;
        this.retreatCost = retreatCost;
        
        //Attack I
        this.atkCostI = atkCostI;
        this.atkNameI = atkNameI;
        this.atkI = atkI;
        
        //Attack II
        this.atkCostII = atkCostII;
        this.atkNameII = atkNameII;
        this.atkII = atkII;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean isBasic() {
    	return evolvesFrom.equals("Null");
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof Pokemon)) return false;
        return this.name.equals( ((Pokemon)o).name );
    }
    
    public int[] getAttackCost(int choice) {
        return (choice == 1) ? atkCostI : atkCostII;
    }
    
    public String getAttackName(int choice) {
        return (choice == 1) ? atkNameI : atkNameII;
    }
    
    public String getAttackEffect(int choice) {
        return (choice == 1) ? atkI : atkII;
    }
}
