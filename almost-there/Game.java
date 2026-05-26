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
        
        read(args);

        Player Guy = new Player();

        while (!hasWon(Guy)) {
            startTurn(Guy,s);
        }
        s.close();
    }
    
    private static int validate(int selection, int listSize, Scanner s) {
        while (selection < 0 || selection >= listSize) {
            System.out.println("That's not a valid selection, try again.");
            System.out.print("Enter your selection: ");
            selection = s.nextInt();
        }
        return selection;
    }

    private static void startTurn(Player Dummy,Scanner s) {
        prompt();
        int move = validate(s.nextInt(),4,s);
        switch (move) {
            case 0 -> playProcedure(Dummy,s); // play a card
            case 1 -> restProcedure(Dummy); // rest
            case 2 -> buyProcedure(Dummy,s); // buy from merchant
            case 3 -> claimProcedure(Dummy,s); // claim from pool
        }
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


    private static void buyProcedure(Player Dummy, Scanner s) {
        System.out.println("======================");
        System.out.println("Current Merchant Cards");
        System.out.println("======================");
        
        printCardsIn(merchant);

        System.out.println("======================");
        System.out.print("You have the following gems: ");
        Dummy.printCaravan();
        System.out.println("Which card would you like to buy?");

        System.out.print("Enter your selection: ");
        int selection = validate(s.nextInt(), merchant.size(), s);

        if (Dummy.canBuy(selection)) {
            Dummy.buy(selection, merchant.get(selection));
            System.out.println("You bought the card.");
            merchant.remove(selection);
        } else {
            System.out.println("You cannot buy that card.");
        }
    }

    private static void claimProcedure(Player Dummy, Scanner s) {
        System.out.println("===================");
        System.out.println("Current Point Cards");
        System.out.println("===================");

        printCardsIn(pool);

        System.out.println("===================");
        System.out.print("You have the following gems: ");
        Dummy.printCaravan();
        System.out.println("Which card would you like to claim?");

        System.out.print("Enter your selection: ");
        int selection = validate(s.nextInt(),pool.size(),s); 

        if (Dummy.canClaim(pool.get(selection))) {
            Dummy.claim(pool.get(selection));
            System.out.println("You claimed the card.");
            pool.remove(selection);
        } else {
            System.out.println("You cannot claim that card.");
        }
    }

    private static void playProcedure(Player Dummy, Scanner s) {
        System.out.println("============");
        System.out.println("Current Hand");
        System.out.println("============");
        
        Dummy.printHand();
        
        System.out.println("============");
        System.out.println("You have the following gems:");
        Dummy.printCaravan();
        System.out.println("Which card would you like to play?");
        
        System.out.print("Enter your selection: ");
        int selection = validate(s.nextInt(), Dummy.handSize(), s);
        
        if (Dummy.canPlay(selection)) {
            Dummy.play(selection);
            System.out.println("You played the card.");
            System.out.println("You have the following gems:");
            Dummy.printCaravan();  
        } else {
            System.out.println("You cannot play that card.");
        }
    }

    public static void read(String[] args) {
        try {
            Scanner reader = new Scanner(new File(args[0]));
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                switch (line) {
                    case "Trade": {
                        Map<Gem,Integer> cost = mapGem(reader.nextLine().trim());
                        Map<Gem, Integer> value = mapGem(reader.nextLine().trim());
                        // System.out.println("TRADE");
                        // System.out.println(cost);
                        // System.out.println(value);
                        // System.out.println("--------------------");
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
                        Map<Gem, Integer> cost = mapGem(reader.nextLine().trim());
                        // System.out.println("POINT");
                        // System.out.println(cost);
                        // System.out.println("--------------------");
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
        Map<Gem, Integer> map = new HashMap<>();
        String[] intString = line.trim().split(",\\s*");
        Gem[] gems = Gem.values();

        for (int i = 0; i < intString.length && i < gems.length; i++) {
            int val = Integer.parseInt(intString[i].trim());
            if (val > 0)
                map.put(gems[i], val);
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
        if (Dummy.hasFivePointCards() || pool.size() == 0) {
            System.out.println("The player scored "+Dummy.score()+" points.");
            return true;
        } else {
            return false;
        }
    }
}