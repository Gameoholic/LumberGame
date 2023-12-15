package xyz.gameoholic.lumbergame.util;

import java.util.*;

public class RandomUtil {
    private static Random rnd = new Random();

    /**
     * @param items The list of items to generate a random element from.
     * @param chances The chance of each element to be returned. Must be same index as element, and between 0.0 and 1.0.
     * @return A random element from the items list.
     */
    public static <T> T getRandom(List<T> items, List<Double> chances) {
        Double r = rnd.nextDouble();

        List<Double> chancesRange = new ArrayList<>(chances);
        chancesRange.add(0, 0.0); // Left bound

        for (int i = 0; i < chancesRange.size() - 1; i += 2) {
            if (r >= chancesRange.get(i) && r <= chancesRange.get(i + 1))
                return items.get(i);
        }

        throw new RuntimeException("No random value could be determined.");
    }
}
