import java.util.Map;

public final class PointCard extends Card {

    private final int points;

    public PointCard(Map<Gem, Integer> cost, int points) {
        super(cost);
        this.points = points;
    }

    public int points() {
        return this.points;
    }

    @Override
    public String toString() {
        return "Points: " + Gem.gemMapToString(super.cost()) + " -> " + this.points;
    }

}
