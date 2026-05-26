import java.util.*;

public class Player {
    private Map<Gem, Integer> caravan = new HashMap<>(4);
    private List<Card> hand = new ArrayList<>();
    private List<Card> used = new ArrayList<>();
    private List<PointCard> PointCards = new ArrayList<>();

    public Player() {
        for (Gem g : Gem.values()) {
            switch (g) {
                case YELLOW -> caravan.put(g, 3);
                default -> caravan.put(g, 0);
            }
        }
        hand.add(new TradeCard(Map.of(), Map.of(Gem.YELLOW, 2)));
        hand.add(new UpgradeCard(2));
    }

    // spending mechanisms
    public boolean canAffordMerchant(int cost) {
        if (cost == 0)
            return true;
        int inventory = 0;
        for (Gem g : Gem.values()) {
            inventory += caravan.getOrDefault(g, 0);
        }
        return inventory >= cost;
    }

    public boolean canAffordCardCost(Map<Gem, Integer> cost) {
        if (cost == null)
            return true;
        for (Gem g : Gem.values()) {
            int inventory = caravan.getOrDefault(g, 0);
            int needs = cost.getOrDefault(g, 0);
            if (inventory < needs)
                return false;
        }
        return true;
    }

    public void payPerGem(Map<Gem, Integer> cost) {
        if (canAffordCardCost(cost) == true) {
            for (Gem g : Gem.values()) {
                if (cost.get(g) != null) {
                    int price = cost.get(g);
                    int inventory = caravan.getOrDefault(g, 0);
                    caravan.put(g, inventory - price);
                }
            }
        }  
    }

    public void payMerchant(int cost) {
        for (Gem g : Gem.values()) {
            int inventory = caravan.getOrDefault(g, 0);
            int pay = Math.min(inventory, cost);
            caravan.put(g, inventory - pay);
            cost -= pay;
            if (cost == 0)
                break;
        }
    }

    public boolean canBuy(int index) {
        int inventory = 0;
        for (Gem g : Gem.values()) {
            inventory += caravan.getOrDefault(g, 0);
        }
        return inventory >= index;
    }

    public void buy(int cost, Card card) {
        if (canAffordMerchant(cost) == true) {
            payMerchant(cost);
        }
        hand.add(card);
    }

    public boolean canClaim(PointCard card) {
        return canAffordCardCost(card.cost());
    }

    public void claim(PointCard card) {
        payPerGem(card.cost());
        PointCards.add(card);
    }

    public boolean canPlay(int index) {
        if (index < 0 || index >= hand.size())
            return false;
        Card card = hand.get(index);
        if (card instanceof UpgradeCard)
            return true;
        if (card instanceof TradeCard)
            return canAffordCardCost(card.cost());
        return false;
    }

    public void play(int index) {
        if (canPlay(index) == true) {
            Card card = hand.get(index);
            hand.remove(index);
            used.add(card);

            if (card instanceof TradeCard) {
                TradeCard trCard = (TradeCard) card;
                payPerGem(card.cost());
                for (Gem g : Gem.values()) {
                    int gain = trCard.value().getOrDefault(g, 0);
                    caravan.put(g, caravan.getOrDefault(g, 0) + gain);
                }
            }

            if (card instanceof UpgradeCard) {
                UpgradeCard upCard = (UpgradeCard) card;
                int cap = upCard.upgrades();

                Gem[] gems = Gem.values();
                for (int i = 0; i < gems.length - 1 && cap > 0; i++) {
                    Gem from = gems[i];
                    Gem to = gems[i + 1];
                    int have = caravan.getOrDefault(from, 0);
                    caravan.put(from, have - 1);
                    caravan.put(to, caravan.getOrDefault(to, 0) + 1);
                    cap -= 1;
                }
            }
        }
    }

    public void rest() {
        for (Card card : used) {
            hand.add(card);
        }
        used.clear();
    }

    public int score() {
        int score = 0;
        for (PointCard card : PointCards) {
                score += card.points();
        }
        for (Gem g : Gem.values()) {
            if (g != Gem.YELLOW) {
                score += caravan.getOrDefault(g,0);
            }
        }
        return score;
    }

    public boolean hasFivePointCards() {
        int count = 0;
        for (@SuppressWarnings("unused") PointCard card : PointCards) {
                count += 1;
        }
        return count >= 5;
    }
}