package Utils;

public class StringCase {
    public static String convertToTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c: input.toCharArray()) {
            if (Character.isSpaceChar(c)) nextTitleCase = true;
            else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            else c = Character.toLowerCase(c);
            titleCase.append(c);
        }

        return titleCase.toString();
    }
}
