import java.util.Map;

public final class UpgradeCard extends Card {

    private final int upgrades;

    public UpgradeCard(int upgrades) {
        super(Map.<Gem, Integer>of());
        this.upgrades = upgrades;
    }

    public int upgrades() {
        return this.upgrades;
    }

    @Override
    public String toString() {
        return "Upgrade: " + this.upgrades;
    }

}
