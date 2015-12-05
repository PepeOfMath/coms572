package field;

import cards.*;
import util.*;
import java.util.ArrayList;
import java.util.Random;

public class Position {
    
    //The pokemon in this position
    private ArrayList<Pokemon> pkmn;
    //The energies in this position
    private ArrayList<Energy> en;
    public Status stat;
    public int turnPlayed;
    int damage;
    Random r;


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
        r = new Random();
    }
    
    //Get the active (highest evolution) Pokemon in this position
    public Pokemon getPokemon() {
        return pkmn.get(pkmn.size()-1);
    }
    
    public int getEnergyCount() {
        return en.size();
    }

    //Attempt to evolve the Pokemon in this position
    public boolean evolveWith(Pokemon p, int newTurnNum) {
        if (turnPlayed == newTurnNum) return false; //Can't evolve the same turn a card is played
        if (this.getPokemon().name.equals(p.evolvesFrom)) {
            pkmn.add(p);
            turnPlayed = newTurnNum;
            return true;
        }
        return false;
    }
    
    public boolean addEnergy(Energy e) {
        en.add(e);
        return true;
    }
    
    public Energy removeRandomEnergy() {
        return en.remove(r.nextInt(en.size()));
    }
    
    //Removes the top level Pokemon Card and returns it
    public Pokemon removeTopPokemon() {
        if (pkmn.size() == 0) return null;
        return pkmn.remove(pkmn.size()-1);
    }
    
    public Energy removeLastEnergy() {
        if (en.size() == 0) return null;
        return en.remove(en.size()-1);
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
    
    //Apply damage. Return the actual amount done
    public int applyDamage(int d, Type t) {
        int damageToDo = d;
        Pokemon p = getPokemon();
        if (p.weakness == t) damageToDo *= 2;
        if (p.resistance == t) damageToDo -= 20;
        if (damageToDo <= 0) return 0;
        if (damageToDo > (p.totalHP - damage)) damageToDo = p.totalHP - damage;
        damage += damageToDo;
        return damageToDo;
    }
    
    //Heal damage.  Return the actual amount healed
    public int healDamage(int d) {
        if (damage <= d) {
            d = damage;
            damage = 0;
        } else {
            damage -= d;
        }
        return d;
    }
    
    //Return true if the Pokemon in this Position has fainted (has at least as much damage as HP)
    public boolean isFaintedPokemon() {
        return (damage >= getPokemon().totalHP);
    }
    
    public String energyString() {
        int[] list = determineEnergy();
        String text = "";
        if (list[0] != 0) text += list[0] + "D";
        if (list[1] != 0) text += list[1] + "P";
        if (list[2] != 0) text += list[2] + "W";
        if (text.equals("")) return "-";
        return text;
    }
    
    // Return a string describing the pokemon in this slot
    public String toString() {
        String text = "";
        Pokemon p = getPokemon();
        text += p.name;
        text = String.format("%1$-" + 12 + "s", text);
        text += energyString();
        text = String.format("%1$-" + 20 + "s", text);
        text += (p.totalHP - damage) + "/" + p.totalHP;
        text = String.format("%1$-" + 30 + "s", text);
        text += stat;
        return text;
    }
}
