package util;

import java.io.*;
import java.util.regex.Pattern;

public class Utils {
    static Log log = new Log("Utils");

    public static String createAndParseBufferedReader(InputStream input) {
        String toReturn = "";
        try (InputStreamReader inputStreamReader = new InputStreamReader(input);
             BufferedReader br = new BufferedReader(inputStreamReader)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            toReturn = sb.toString();
        } catch (Exception e) {
            log.e(e.toString());
        }
        return toReturn;
    }

    public static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

    public static int countSubstring2(String subStr, String str) {
        return str.split(Pattern.quote(subStr), -1).length - 1;
    }

    public static String channelNameWithoutOctothorp(String s) {
        return s.replaceFirst("#", "");
    }

    public static String channelNameWithOctothorp(String s) {
        if (s.startsWith("#")) {
            return s;
        } else {
            return "#" + s;
        }
    }

    public static String commandWithoutExMark(String command) {
        if (command.startsWith("!")) {
            return command.replaceFirst("!", "");
        } else {
            return command;
        }
    }

    public static void createFile(File f) {
        log.v("Creating file: " + f.getAbsolutePath());
        if (!f.exists()) {
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
