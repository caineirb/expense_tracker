package Utils;

import java.util.HashMap;
import static java.lang.Math.min;
import static java.lang.Math.max;

public class FuzzySearch {
    public static boolean contains(String text, String pattern) {
        return fuzzy(text, pattern, 3, false);
    }

    public static boolean contains(String text, String pattern, int max) {
        return fuzzy(text, pattern, max, false);
    }

    public static boolean containsNoCase(String text, String pattern) {
        return fuzzy(text, pattern, 3, true);
    }

    public static boolean containsNoCase(String text, String pattern, int max) {
        return fuzzy(text, pattern, max, true);
    }

    public static boolean isExact(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty()) return false;
        return text.contains(pattern);
    }

    public static boolean isExactNoCase(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty()) return false;
        return text.toLowerCase().contains(pattern.toLowerCase());
    }
    
    private static boolean fuzzy(String text, String pattern, int max, boolean ignoreCase) {
        if (text == null || pattern == null) return false;
        if (pattern.length() > 64) pattern = pattern.substring(0, 64);
        int m = pattern.length();
        if (m == 0) return true;

        if (!ignoreCase && text.contains(pattern)) return true;
        int k = min(max, m / 3);
        if (k <= 0 && !ignoreCase) return false;
        if (text.length() + k < m) return false;

        return myers(text, pattern, max(k, 0), ignoreCase);
    }

    private static boolean myers(String text, String pattern, int k, boolean ic) {
        int m = pattern.length();

        // Per-character bitmasks of where each char occurs in the patterns.
        long[] low = new long[128];
        HashMap<Character, Long> high = null;
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            long bit = 1L << i;
            if (c < 128) low[c] |= bit;
            else {
                if (high == null) high = new HashMap<>();
                high.merge(c, bit, (a, b) -> a | b);
            }
        }

        long pv = -1L, mv = 0L;
        long lastBit = 1L << (m - 1);
        int score = m;
        
        for (int j = 0; j < text.length(); j++) {
            char c = norm(text.charAt(j), ic);
            long eq = c < 128 ? low[c] : (high == null ? 0L : high.getOrDefault(c, 0L));

            long xv = eq | mv;
            long xh = (((eq & pv) + pv) ^ pv) | eq;
            long ph = mv | ~(xh | pv);
            long mh = pv & xh;

            if ((ph & lastBit) != 0) score++;
            else if ((mh & lastBit) != 0) score--;

            ph <<= 1;
            mh <<= 1;
            pv = mh | ~(xv | ph);
            mv = ph & xv;

            if (score <= k) return true;
        }

        return false;
    }

    private static char norm(char c, boolean ic) {
        return ic ? Character.toLowerCase(c) : c;
    }
}
