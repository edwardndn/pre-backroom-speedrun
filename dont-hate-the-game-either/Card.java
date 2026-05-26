import java.util.Map;

public abstract sealed class Card permits PointCard, TradeCard, UpgradeCard {

    private Map<Gem, Integer> cost;

    public Card(Map<Gem, Integer> cost) {
        this.cost = cost;
    }

    public Map<Gem, Integer> cost() {
        return this.cost;
    }

}
