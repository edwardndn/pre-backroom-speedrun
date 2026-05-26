import java.util.Map;

public final class TradeCard extends Card { 
    Map<Gem, Integer> value;
    public  TradeCard(Map<Gem, Integer> cost, Map<Gem, Integer> value){
        super(cost);
        this.value = value;
    }
    public Map<Gem, Integer> value(){
        return this.value;
    }

    @Override
    public String toString() {
        return "Trade: " + format(cost) + " -> " + format(value);
    }
    
    // public int points(){
    //     return 0;
    // }
}