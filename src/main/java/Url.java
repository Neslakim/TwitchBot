import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url {
    static String urlRegex = "\\(?(?:(http|https):\\/\\/)?(?:((?:[^\\W\\s]|\\.|-|[:]{1})+)@{1})?((?:www.)?(?:[^\\W\\s]|\\.|-)+[\\.][^\\W\\s]{2,4}|localhost(?=\\/)|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::(\\d*))?([\\/]?[^\\s\\?]*[\\/]{1})*(?:\\/?([^\\s\\n\\?\\[\\]\\{\\}\\#]*(?:(?=\\.)){1}|[^\\s\\n\\?\\[\\]\\{\\}\\.\\#]*)?([\\.]{1}[^\\s\\?\\#]*)?)?(?:\\?{1}([^\\s\\n\\#\\[\\]]*))?([\\#][^\\s\\n]*)?\\)?";
    public int bypass;
    public int duration;

    public Url(int duration, int bypass) {
        this.bypass = bypass;
        this.duration = duration;
    }

    public static List<String> findUrls(String s) {
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile(urlRegex).matcher(s);
        while (m.find()) {
            String u = m.group();
            if (!allMatches.contains(u)) {
                allMatches.add(u);
            }
        }
        return allMatches;
    }

    public boolean check(String message, int access, boolean tempPermit) {
        return !(tempPermit || access >= bypass) && findUrls(message).size() > 0;
    }
}
