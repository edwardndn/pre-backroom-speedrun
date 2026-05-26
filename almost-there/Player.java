import java.util.Map;
import java.util.EnumMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Player {

    private final Map<Gem, Integer> caravan;
    private final List<Card> hand;
    private final List<Card> used;
    private final List<PointCard> pointCards;

    public Player() {
        this.caravan = new EnumMap<Gem, Integer>(Gem.class);
        for (Gem gem : Gem.values()) {
            this.caravan.put(gem, 0);
        }
        this.caravan.put(Gem.YELLOW, 3);
        this.hand = new LinkedList<Card>();
        this.hand.add(new TradeCard(Map.<Gem, Integer>of(), Map.<Gem, Integer>of(Gem.YELLOW, 2)));
        this.hand.add(new UpgradeCard(2));
        this.used = new LinkedList<Card>();
        this.pointCards = new ArrayList<PointCard>();
    }

    public boolean canBuy(int position) {
        return position <= this.count(caravan);
    }

    public void buy(int position, Card card) {
        int toPay = position;

        for (Gem gem : Gem.values()) {
            int min = toPay > this.caravan.get(gem) ? this.caravan.get(gem) : toPay;
            toPay -= min;
            this.caravan.put(gem, this.caravan.get(gem) - min);
        }

        this.hand.add(card);
    }

    public boolean canClaim(PointCard card) {
        return this.has(card.cost());
    }

    public void claim(PointCard card) {
        for (Gem gem : card.cost().keySet()) {
            this.caravan.put(gem, this.caravan.get(gem) - card.cost().get(gem));
        }
        this.pointCards.add(card);
    }

    private boolean has(Map<Gem, Integer> gems) {
        for (Gem gem : Gem.values()) {
            if (gems.containsKey(gem) && this.caravan.get(gem) < gems.get(gem)) return false;
        }
        return true;
    }

    private <T> int count(Map<T, Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean canPlay(int position) {
        if (0 <= position && position < this.hand.size()) {
            return this.has(this.hand.get(position).cost());
        }
        return false;
    }

    public void play(int position) {
        switch (this.hand.get(position)) {
            case TradeCard t:
                for (Gem gem : t.cost().keySet()) this.caravan.put(gem, this.caravan.get(gem) - t.cost().get(gem));
                for (Gem gem : t.value().keySet()) this.caravan.put(gem, this.caravan.get(gem) + t.value().get(gem));
                break;
            case UpgradeCard u:
                int upgradeCount = u.upgrades();
                for (Gem gem : Gem.values()) {
                    int min = this.caravan.get(gem) < upgradeCount ? this.caravan.get(gem) : upgradeCount;
                    this.caravan.put(gem, this.caravan.get(gem) - min);
                    this.caravan.put(gem.upgrade(), this.caravan.get(gem.upgrade()) + min);
                    upgradeCount -= min;
                }
                break;
            default:
                break;
        }
        this.used.add(this.hand.remove(position));
    }

    public void rest() {
        this.hand.addAll(this.used);
        this.used.clear();
    }

    public int score() {
        return this.pointCards.stream().mapToInt((c) -> c.points()).sum() + this.count(this.caravan) - this.caravan.get(Gem.YELLOW);
    }

    public boolean hasFivePointCards() {
        return this.pointCards.size() >= 5;
    }

    public void printHand() {
        for (int i = 0; i < this.hand.size(); i++) {
            System.out.println(i + ". " + this.hand.get(i));
        }
    }

    public void printCaravan() {
        System.out.println(Gem.gemMapToString(this.caravan));
    }

    public int handSize() {
        return this.hand.size();
    }

}
