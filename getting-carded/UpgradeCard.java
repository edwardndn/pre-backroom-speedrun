import java.util.Map;

public final class UpgradeCard extends Card {
    int cap = 0;
    public UpgradeCard(int cap){
        super(Map.of());
        this.cap = cap;
    }
    public int upgrades(){
        return this.cap;
    }

    public String toString() {
        return "Upgrade: " + upgrades();
    }

    // public int points(){
    //     return 0;
    // }

}