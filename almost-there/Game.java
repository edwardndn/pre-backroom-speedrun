import java.util.Map;
import java.util.HashMap;
// import java.util.EnumMap;
import java.util.List;
// import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Game {
    private static List<Card> merchant = new ArrayList<>();
    private static List<PointCard> pool = new ArrayList<>();
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("What did you have your deck saved as? ");
        String deck = s.nextLine();
        
        read(deck);

        Player Guy = new Player();

        while (!hasWon(Guy)) {
            startTurn(Guy);
        }
        s.close();
    }

    private static void startTurn(Player Dummy) {
        Scanner s = new Scanner(System.in);
        prompt();
        int move = s.nextInt();
        switch (move) {
            case 0 -> playProcedure(Dummy); // play a card
            case 1 -> restProcedure(Dummy); // rest
            case 2 -> buyProcedure(Dummy); // buy from merchant
            case 3 -> claimProcedure(Dummy); // claim from pool
        }
        s.close();
    }

    private static void prompt() {
        System.out.println("Would you like to:");
        System.out.println("0. Play a card");
        System.out.println("1. Rest");
        System.out.println("2. Buy a Merchant Card");
        System.out.println("3. Claim a Point Card");
        System.out.print("Enter your selection: ");
    }

    private static void restProcedure(Player Dummy) {
        Dummy.rest();
        System.out.println("You rested and refilled your hand.");
    }

    private static void buyProcedure(Player Dummy) {
        System.out.println("======================");
        System.out.println("Current Merchant Cards");
        System.out.println("======================");
        
        printCardsIn(merchant);

        System.out.println("======================");
        System.out.print("You have the following gems: ");
        Dummy.printCaravan();
        System.out.println("Which card would you like to buy?");
        System.out.print("Enter your selection: ");
        
        Scanner s = new Scanner(System.in);
        int selection = s.nextInt();
        if (Dummy.canBuy(selection)) {
            Dummy.buy(selection, merchant.get(selection));
            System.out.println("You bought the card.");
        } else {
            System.out.println("You cannot buy that card.");
        }
        s.close();
    }

    private static void claimProcedure(Player Dummy) {
        System.out.println("===================");
        System.out.println("Current Point Cards");
        System.out.println("===================");
        
        printCardsIn(pool);

        System.out.println("===================");
        System.out.print("You have the following gems: ");
        Dummy.printCaravan();
        System.out.println("Which card would you like to claim?");
        System.out.print("Enter your selection: ");
        
        Scanner s = new Scanner(System.in);
        int selection = s.nextInt();
        if (Dummy.canClaim(pool.get(selection))) {
            Dummy.claim(pool.get(selection));
            System.out.println("You claimed the card.");
        } else {
            System.out.println("You cannot claim that card.");
        }
        s.close();
    }

    private static void playProcedure(Player Dummy) {
        System.out.println("============");
        System.out.println("Current Hand");
        System.out.println("============");

        Dummy.printHand();
    
        System.out.println("You have the following gems: ");
        Dummy.printCaravan();
        System.out.println("Which card would you like to play?");
        System.out.print("Enter your selection: ");

        Scanner s = new Scanner(System.in);
        int selection = s.nextInt();
        if (Dummy.canPlay(selection)) {
            Dummy.play(selection);
            System.out.println("You played the card.");
            System.out.println("You have the following gems: ");
            Dummy.printCaravan();  
        } else {
            System.out.println("You cannot play that card.");
        }
        s.close();
    }

    public static void read(String deck) {
        try {
            Scanner reader = new Scanner(new File(deck));
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                switch (line) {
                    case "Trade": {
                        Map<Gem,Integer> cost = mapGem(reader.nextLine().trim());
                        Map<Gem,Integer> value = mapGem(reader.nextLine().trim());
                        TradeCard t = new TradeCard(cost, value);
                        merchant.add(t);
                        continue;
                    }
                    case "Upgrade": {
                        int capacity = Integer.parseInt(reader.nextLine().trim());
                        UpgradeCard u = new UpgradeCard(capacity);
                        merchant.add(u);
                        continue;
                    }
                    case "Point": {
                        Map<Gem,Integer> cost = mapGem(reader.nextLine().trim());
                        int worth = Integer.parseInt(reader.nextLine().trim());
                        PointCard p = new PointCard(cost, worth);
                        pool.add(p);
                        continue;
                    }
                    default:
                        continue;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        System.out.println("Let's Play Decade!");
    }
    
    private static Map<Gem, Integer> mapGem(String line) {
        List<Integer> intList = new ArrayList<>();
        Map<Gem, Integer> map = new HashMap<Gem, Integer>();
        String[] intString = line.trim().split(",\\s*");
        for (String s : intString) {
            intList.add(Integer.parseInt(s));
        }
        for (int i : intList) {
            for (Gem g : Gem.values()) {
                switch (g) {
                    case YELLOW:
                        map.put(Gem.YELLOW, i);
                        break;
                    case GREEN:
                        map.put(Gem.GREEN, i);
                        break;
                    case BLUE:
                        map.put(Gem.BLUE, i);
                        break;
                    case PINK:
                        map.put(Gem.PINK, i);
                        break;
                }
            }
        }
        return map;
    }

    // i wrote this not knowing that apparently these methods are already given in
    // the scaffold - i keep them here because i don't want my efforts wasted

    private static String print(Card card) {
        switch (card) {
            case TradeCard t:
                return t.toString();
            case UpgradeCard u:
                return u.toString();
            case PointCard p:
                return p.toString();
        }
    }
                            
    private static void printCardsIn(List<? extends Card> list) {
    for (int i = 0; i< list.size(); i++) {
            System.out.println( i + ". " + print(list.get(i)));
        }  
    }
                            
    private static boolean hasWon(Player Dummy) {
        if (Dummy.hasFivePointCards() || pool.size() == 0 || Dummy.handSize() == 0) {
            System.out.println(Dummy.score());
            return true;
        } else {
            return false;
        }
    }
}
