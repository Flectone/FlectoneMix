package net.flectone.mix.util;

public class StringUtil {

    public static String convertIntToString(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        }

        double convertedNum;
        String suffix;

        if (num < 1_000_000) {
            convertedNum = num / 1000.0;
            suffix = "K";
        } else {
            convertedNum = num / 1_000_000.0;
            suffix = "M";
        }

        if (convertedNum % 1 == 0) {
            return String.format("%.0f%s", convertedNum, suffix);
        } else {
            return String.format("%.1f%s", convertedNum, suffix);
        }
    }

}
