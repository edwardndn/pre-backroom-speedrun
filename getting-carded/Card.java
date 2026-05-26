import java.util.*;

public abstract sealed class Card permits PointCard, TradeCard, UpgradeCard {

    public Map<Gem, Integer> cost;

    public Card(Map<Gem, Integer> cost) {
        this.cost = cost;
    }

    public Map<Gem, Integer> cost() {
        return this.cost;
    }

    // public String format(Map<Gem, Integer> cost) {
    //     String output="";
    //     List<Gem> Gems = new ArrayList<>();
    //     for (Map.Entry<Gem, Integer> card : cost.entrySet()) {
    //         for (int i = 0; i<=cost.entrySet().size();i++){
    //             Gems.add(card.getKey());
    //         }
    //     }
    //     int n = 0;
    //     for (Gem g : Gems) { 
    //         output += cost.get(g) + " " + g.toString();
    //         n++;
    //         if (n<3) {
    //             output += ", ";
    //         }
    //     }
    //     if (output.isEmpty()) return "Nothing" ; else return output;
    // }

    public String format(Map<Gem, Integer> cost) throws NullPointerException {
        String output="";
        try {                
            for (Gem g : Gem.values()) {
                if (cost.get(g) == null) continue;
                else {
                    int value = cost.get(g);

                    if (value > 0) { 
                        if (!output.isEmpty()) {
                            output+= ", ";
                        }
                        output+= value + " " + g;
                    }
                }
            }
            if (output.isEmpty()) return "Nothing" ; else return output;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Error encountered";
        }
    }

    public String transFormat(Map<Gem, Integer> cost) {
        String output="";
        if (cost.size()==0) return "Nothing";
        else {
            for(Gem g : Gem.values()) { 
                // String gemStr = g.toString();
                if (cost.get(g) == null) continue;
                if (!output.isEmpty()) {
                    output += ", ";
                }
                output += cost.get(g) + " " + g;
            }
            return output;
        }
    }

    // public abstract int points();
}
