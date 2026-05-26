import java.util.Map;

public final class PointCard extends Card {
    
    int value;

    public PointCard(Map<Gem, Integer> cost, int value){
        super(cost);
        this.value = value;
    }

    public int points() {
        return this.value;
    }
    
    @Override
    public String toString(){
        
        if (cost == null){
            return "Nothing";
        } else {
            return "Points: " + format(cost) + " -> " + points();
        }
    }
}