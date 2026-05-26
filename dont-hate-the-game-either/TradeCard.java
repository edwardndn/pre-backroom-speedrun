import java.util.Map;

public final class TradeCard extends Card {

    private final Map<Gem, Integer> value;

    public TradeCard(Map<Gem, Integer> cost, Map<Gem, Integer> value) { //Well hello :eyes:
        super(cost);
        this.value = value;
    }

    public Map<Gem, Integer> value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Trade: " + Gem.gemMapToString(super.cost()) + " -> " + Gem.gemMapToString(this.value);
    }
    
}
