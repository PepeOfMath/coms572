import cards.*;
import java.util.ArrayList;

public class Position {
    
    //The pokemon in this position
    private ArrayList<Pokemon> pkmn;
    //The energies in this position
    private ArrayList<Energy> en;
    public Status stat;
    public int turnPlayed;
    int damage;


    public Position(Pokemon p, int turnNumber) {
        if (p == null || !p.evolvesFrom.equals("Null")) {
            //Not a basic pokemon
            throw new IllegalArgumentException("Not a basic pokemon");
        }
        pkmn = new ArrayList<Pokemon>(3);
        en = new ArrayList<Energy>(5);
        pkmn.add(p);
        stat = Status.NORMAL;
        turnPlayed = turnNumber;
        damage = 0;
    }
    
    //Get the active (highest evolution) Pokemon in this position
    public Pokemon getPokemon() {
        return pkmn.get(pkmn.size()-1);
    }

    //Attempt to evolve the Pokemon in this position
    public boolean EvolveWith(Pokemon p) {
        if (this.getPokemon().name.equals(p.evolvesFrom)) {
            pkmn.add(p);
            return true;
        }
        return false;
    }
    
    public boolean addEnergy(Energy e) {
        en.add(e);
        return true;
    }
    
    //Return an array of energy counts in the same format as Util determines costs
    public int[] determineEnergy() {
        int[] energy = new int[Util.NUM_ENERGY_TYPES];
        for (int i = 0; i < en.size(); i++) {
            switch(en.get(i).eType) {
                case DARK:
                    energy[0]++;
                    break;
                case PSYCHIC:
                    energy[1]++;
                    break;
                case WATER:
                    energy[2]++;
                    break;
            }
        }
        
        return energy;
    }
}
