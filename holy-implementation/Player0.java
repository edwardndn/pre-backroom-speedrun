// this class over complicates the merchant and the pointcard pool
// inherited from Player0.1.java, Player0.2.java over complicates the spending process and wrongly interpreted the point card system

import java.util.*;

public class Player0 {
    private Map<Gem, Integer> caravan = new HashMap<>();
    private List<Card> hand = new ArrayList<>();
    private List<Card> merchant = new ArrayList<>(6);
    private List<PointCard> pool = new ArrayList<>(5);
    private List<Card> used = new ArrayList<>();

    public Player0() {
        for (Gem g : Gem.values()) {
            switch (g) {
                case YELLOW -> caravan.put(g, 3);
                default -> caravan.put(g, 0);
            }
        }
        setUpMerchant();
        setUpPool();
        hand.add(new TradeCard(Map.of(), Map.of(Gem.YELLOW, 2)));
        hand.add(new UpgradeCard(2));
    }

    public void setUpPool() {
    }

    public void setUpMerchant() {
        for (int i = 0; i < 6; i++) {
            merchant.add(merchGen());
        }
    }

    public Card merchGen() {
        Random rand = new Random();
        int cardType = rand.nextInt(0, 2);
        if (cardType == 0) {
            return genTrCard();
        } else {
            return genUpCard();
        }
    }

    public Card genTrCard() {
        Random rand = new Random();
        Map<Gem, Integer> cost = new HashMap<>();
        Map<Gem, Integer> value = new HashMap<>();
        for (Gem g : Gem.values()) {
            int gemCost = rand.nextInt(0, 4);
            int gemValue = rand.nextInt(0, 6);
            cost.put(g, gemCost);
            value.put(g, gemValue);
        }
        return new TradeCard(cost, value);
    }

    public Card genUpCard() {
        Random rand = new Random();
        int capacity = rand.nextInt(1, 11);
        return new UpgradeCard(capacity);
    }

    public boolean canBuy(int index) {
        boolean can = false;
        if (index > 0 || index < merchant.size()) {
            try {
                for (Gem g : Gem.values()) {
                    if (caravan.get(g) >= index)
                        can = true;
                    break;
                }
                return can;
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Exception caught @ canBuy()");
            }
        }
        return can;
    }

    public void buy(int index, Card card) {
        if (canBuy(index) == true || card.equals(merchant.get(index))) {
            int cost = index;
            for (Gem g : Gem.values()) {
                if (caravan.get(g) >= index) {
                    int have = caravan.get(g);
                    caravan.put(g, have - cost);
                    break;
                }
            }
            merchant.remove(index);
            hand.add(card);
        }
    }

    public boolean canClaim(PointCard card) {
        boolean can = true;
        if (pool.contains(card)) {
            Map<Gem, Integer> cost = card.cost();
            if (cost == null) {
                cost = Map.of();
            }
            for (Gem g : Gem.values()) {
                int price = cost.getOrDefault(g, 0);
                int have = caravan.getOrDefault(g, 0);
                if (price > have) {
                    can = false;
                    break;
                }
            }
        }
        return can;
    }

    public void claim(PointCard card) {
        if (canClaim(card) == true) {
            Map<Gem, Integer> cost = card.cost();
            if (cost == null) {
                cost = Map.of();
            }
            for (Gem g : Gem.values()) {
                caravan.put(g, caravan.getOrDefault(g, 0) - cost.getOrDefault(g, 0));
            }
            pool.remove(card);
            hand.add(card);
        }
    }

    public boolean canPlay(int index) {
        try {
            Card card = hand.get(index);
            if (card instanceof UpgradeCard) {
                return true;
            }
            Map<Gem, Integer> cost = card.cost();
            if (cost == null) {
                cost = Map.of();
            }
            for (Gem g : Gem.values()) {
                int price = cost.getOrDefault(g, 0);
                int have = caravan.getOrDefault(g, 0);
                if (price > have) {
                    return false;
                }
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            System.err.print("canPlay(): index out of bounds");
            return false;
        } catch (IllegalArgumentException e) {
            System.err.print("canPlay(): IllegalArgumentException");
            return false;
        }
    }

    public void play(int index) {
        if (canPlay(index) == true) {
            Card card = hand.get(index);
            Map<Gem, Integer> cost = card.cost();
            if (cost == null) {
                cost = Map.of();
            }
            for (Gem g : Gem.values()) {
                int price = cost.getOrDefault(g, 0);
                int have = caravan.getOrDefault(g, 0);
                caravan.put(g, have - price);
            }
            hand.remove(card);
            used.add(card);

            if (card instanceof TradeCard) {
                TradeCard trCard = (TradeCard) card;
                for (Gem g : Gem.values()) {
                    int value = trCard.value().getOrDefault(g, 0);
                    caravan.put(g, caravan.get(g) + value);
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
                    int use = Math.min(have, cap);
                    caravan.put(from, have - use);
                    caravan.put(to, caravan.getOrDefault(to, 0) + use);
                    cap -= use;
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
        for (Card card : hand) {
            if (card instanceof PointCard) {
                PointCard ptCard = (PointCard) card;
                score += ptCard.points();
            }
        }
        for (Gem g : Gem.values()) {
            if (g != Gem.YELLOW) {
                score += caravan.getOrDefault(g, 0);
            }
        }
        return score;
    }

    public boolean hasFivePointCards() {
        int count = 0;
        for (Card card : hand) {
            if (card instanceof PointCard) {
                count += 1;
            }
        }
        return count >= 5;
    }
}