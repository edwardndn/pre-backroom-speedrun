// this class utilises a stupid implementations of player's hand

import java.util.*;

public final class oldPlayer {

    private Map<Gem, Integer> caravan = new HashMap<>();

    private enum Type {
        TradeCard, UpgradeCard, PointCard
    };

    private List<Card> UpgradeCards = new ArrayList<>();
    private List<Card> TradeCards = new ArrayList<>();
    private List<Card> PointCards = new ArrayList<>(); // this is a stupid implementation, cuz just a counter would have
                                                       // sufficed, but apparently being able to print the point cards
                                                       // might be useful ?
    // we also need a merchant, that's a different class tho
    private List<Card> Merchant = new ArrayList<>(6);
    private List<List<Card>> Hand = new ArrayList<>();
    private List<Card> Used = new ArrayList<>();

    public oldPlayer() {
        caravan.put(Gem.YELLOW, 3);
        caravan.put(Gem.GREEN, 0);
        caravan.put(Gem.BLUE, 0);
        caravan.put(Gem.PINK, 0);
        TradeCards.add(new TradeCard(Map.of(), Map.of(Gem.YELLOW, 2))); // cost 0 gives 2 yellows
        UpgradeCards.add(new UpgradeCard(2)); // capacity of 2
        for (Type type : Type.values()) { // this to ensure the order of the hand is trade > upgrade > point
            if (type == Type.TradeCard) {
                Hand.add(TradeCards);
            } else if (type == Type.UpgradeCard) {
                Hand.add(UpgradeCards);
            } else if (type == Type.PointCard) {
                Hand.add(PointCards);
            }
        }
        newMerchant();
    }

    public void newMerchant() {
        Merchant.clear();
        for (int i = 0; i < 6; i++) {
            Merchant.add(MerchGen());
        }
    }

    public Card genPtCard() {
        Random rand = new Random();
        int value = rand.nextInt(1, 5);
        Map<Gem, Integer> cost = new HashMap<>();
        for (Gem g : Gem.values()) {
            int gemCost = rand.nextInt(0, 4);
            if (gemCost > 0) {
                cost.put(g, gemCost);
            }
        }
        return new PointCard(cost, value);
    }

    public Card genTrCard() {
        Random rand = new Random();
        Map<Gem, Integer> cost = new HashMap<>();
        Map<Gem, Integer> value = new HashMap<>();
        for (Gem g : Gem.values()) {
            int gemCost = rand.nextInt(0, 4);
            int gemValue = rand.nextInt(0, 4);
            if (gemCost > 0) {
                cost.put(g, gemCost);
            }
            if (gemValue > 0) {
                value.put(g, gemValue);
            }
        }
        return new TradeCard(cost, value);
    }

    public Card MerchGen() {
        Random rand = new Random();
        if (rand.nextBoolean()) {
            return genPtCard();
        } else {
            return genTrCard();
        }
    }

    public boolean canBuy(int index) {
        if (index < 0 || index >= Merchant.size()) {
            throw new IllegalArgumentException("which one you tryna buy !?");
        } else {
            boolean can = true;
            Card card = Merchant.get(index);
            for (Gem g : Gem.values()) {
                if (card.cost().get(g) == null || card.cost().get(g) > caravan.get(g)) { // cost() return Map<Gem,
                                                                                         // Integer> , match the Gem in
                                                                                         // Map with g in Gem.values()
                                                                                         // -- the player can buy the
                                                                                         // card if the no. gems in
                                                                                         // caravan is greater than the
                                                                                         // cost of the card
                    can = false;
                }
            }
            return can;
        }
    }

    public void buy(int index, Card card) { // merchant sells trade and upgrade cards
        if (canBuy(index) == false || card != Merchant.get(index)) {
            throw new IllegalArgumentException("either not enough gems or merchant aint have that card");
        } else {
            for (Gem g : Gem.values()) {
                if (card.cost().get(g) != null) {
                    caravan.put(g, caravan.get(g) - card.cost().get(g));
                }
            }
            if (card instanceof TradeCard) {
                TradeCards.add((TradeCard) card);
            } else if (card instanceof UpgradeCard) {
                UpgradeCards.add((UpgradeCard) card);
            }
        }
    }

    public boolean canClaim(PointCard card) { // point cards are claimed from the pile
        if (card.cost() == null) {
            return true;
        }
        boolean can = true;
        for (Gem g : Gem.values()) {
            if (card.cost().get(g) > caravan.get(g)) {
                can = false;
            }
        }
        return can;
    }

    public void claim(PointCard card) {
        if (canClaim(card) == false) {
            throw new IllegalArgumentException("you aint claiming nothin with that broke ahh boi");
        } else {
            for (Gem g : Gem.values()) {
                if (card.cost().get(g) != null) {
                    caravan.put(g, caravan.get(g) - card.cost().get(g));
                }
            }
            PointCards.add(card);
        }
    }

    public int findTypeIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("internal: cannot call findGroup() on given index");
        } else {
            int typeIndex = 0;
            int s = -1;
            for (int i = 0; i < Hand.size(); i++) {
                if (index < s) {
                    return typeIndex;
                } else {
                    typeIndex++;
                    i++;
                    s += Hand.get(i).size();
                }
            }
            return typeIndex;
        }
    }

    public int findLocalIndex(int index) {
        int s = 0;
        if (index < Hand.get(0).size() - 1) {
            return index;
        } else {
            for (int i = 0; i < findTypeIndex(index); i++) {
                s += Hand.get(i).size();
            }
        }
        return index - s;
    }

    public boolean canPlay(int index) {
        if (index < 0 || index >= Hand.size()) {
            throw new IllegalArgumentException("aint no imaginary card for you to play boi !!");
        } else {
            Card card = Hand.get(findTypeIndex(index)).get(findLocalIndex(index));
            boolean can = false;
            if (card instanceof TradeCard || card instanceof UpgradeCard) {
                for (Gem g : Gem.values()) {
                    if (card.cost().get(g) != null && card.cost().get(g) <= caravan.get(g)) {
                        can = true;
                    }
                }
            }
            return can;
        }
    }

    public void play(int index) {
        if (canPlay(index) == false) {
            throw new IllegalArgumentException("you broke ahh aint be playin that card");
        } else {
            Card card = Hand.get(findTypeIndex(index)).get(findLocalIndex(index));
            Hand.get(findTypeIndex(index)).remove(findLocalIndex(index));
            Used.add(card);

            if (card instanceof TradeCard) { // follow through with the trade
                for (Gem g : Gem.values()) {
                    if (card.cost().get(g) != null) { // if the it costs some gems of type g
                        caravan.put(g, caravan.get(g) - card.cost().get(g)); // change the caravan to less that cost =
                                                                             // effectively paying the cost to play the
                                                                             // card
                    }
                    TradeCard trCard = (TradeCard) card;
                    int gain = trCard.value().get(g); // see if the trade gains the player any gem of type g
                    caravan.put(g, caravan.get(g) + gain); // even if gain = 0 it would still be the correct post-trade
                                                           // gem count
                }
            } else if (card instanceof UpgradeCard) {
                int cap = ((UpgradeCard) card).upgrades(); // copy the upgrade capacity, so that we dont change the card
                                                           // property
                for (int i = 0; i < Gem.values().length; i++) { // switch to each type of gem
                    if (cap > caravan.get(Gem.values()[i])) {
                        caravan.put(Gem.values()[i + 1], caravan.get(Gem.values()[i]));
                        cap -= caravan.get(Gem.values()[i]);
                    } else {
                        caravan.put(Gem.values()[i], caravan.get(Gem.values()[i]) + cap);
                        break;
                    }
                }
            }
        }
    }

    public void rest() {
        for (Card card : Used) {
            if (card instanceof TradeCard) { // assuming that we did the intial player generation right
                Hand.get(0).add(card);
            } else if (card instanceof UpgradeCard) {
                Hand.get(1).add(card);
            }
        }
        Used.clear();
    }

    public int score() {
        int score = 0;
        for (Card card : PointCards) {
            if (card instanceof PointCard) {
                PointCard ptCard = (PointCard) card;
                score += ptCard.points();
            }
        }
        for (Gem g : Gem.values()) {
            if (g != Gem.YELLOW && caravan.get(g) != null) {
                score += caravan.get(g);
            }
        }
        return score;
    }

    public boolean hasFivePointCards() {
        return PointCards.size() >= 5;
    }
}