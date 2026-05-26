// this is augmented code given for the purpse of the assignment, ngobito did not write this code

import java.util.Map;

public enum Gem {
    YELLOW(0), GREEN(1), BLUE(2), PINK(3);

    private final int index;

    private Gem(int index) {
        this.index = index;
    }

    public Gem upgrade() {
        return switch (this) {
            case YELLOW -> GREEN;
            case GREEN -> BLUE;
            case BLUE -> PINK;
            case PINK -> PINK;
        };
    }

    public static Gem getGemByIndex(int index) {
        return switch (index) {
            case 0 -> YELLOW;
            case 1 -> GREEN;
            case 2 -> BLUE;
            case 3 -> PINK;
            default -> null;
        };
    }

    public int index() {
        return index;
    }

    public static String gemMapToString(Map<Gem, Integer> map) {
        String mapString = "";
        int commaCount = map.size() - 1;
        for (int i = 0; i < Gem.values().length; i++) {
            if (map.containsKey(Gem.getGemByIndex(i)))
                mapString += map.get(Gem.getGemByIndex(i)) + " " + Gem.getGemByIndex(i) + (commaCount-- > 0? ", " : "");
        }
        return mapString.isEmpty() ? "Nothing" :  mapString;
    }
}