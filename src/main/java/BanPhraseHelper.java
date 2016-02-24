import util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BanPhraseHelper {
    int duration;
    int access;
    Log log = new Log(getClass().getSimpleName());
    String channelName;
    ArrayList<BanPhrase> banPhrases;

    public BanPhraseHelper(String channelName, int duration, int access) {
        this.channelName = channelName;
        banPhrases = new ArrayList<>();
        this.duration = duration;
        this.access = access;
    }

    public void addBanphrase(BanPhrase banPhrase) {
        if (!banPhrases.contains(banPhrase)) {
            this.banPhrases.add(banPhrase);
            saveBanphrases();
        }
    }

    public boolean removeBanphrase(String banPhrase) {
        boolean ret = this.banPhrases.remove(getBanphrase(banPhrase));
        saveBanphrases();
        return ret;
    }

    public BanPhrase getBanphrase(String s) {
        for (BanPhrase bp : banPhrases) {
            if (bp.getBanphrase().equalsIgnoreCase(s)) {
                return bp;
            }
        }
        return null;
    }

    public void loadBanphrases() {
        try (FileReader fileReader = new FileReader(String.format("config/%s/banphrases.txt", channelName));
             BufferedReader reader = new BufferedReader(fileReader)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                banPhrases.add(new BanPhrase(line));
            }
            log.i("Banphrases loaded");
        } catch (IOException e) {
            log.e("Couldn't load the banphrases for " + channelName);
        }
    }

    public void saveBanphrases() {
        try {
            List<String> temp = new ArrayList<>(banPhrases.size());
            temp.addAll(banPhrases.stream().map(BanPhrase::toString).collect(Collectors.toList()));
            Files.write(Paths.get(String.format("config/%s/banphrases.txt", channelName)), temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BanPhrase> getBanPhrases() {
        return banPhrases;
    }

    public boolean hasBanphrase(String p) {
        log.v("Saving banphrases: " + channelName);
        for (BanPhrase bp : banPhrases) {
            if (bp.getBanphrase().equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String source, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    public boolean check(String message) {
        for (BanPhrase bp : banPhrases) {
            if (contains(bp.getBanphrase(), message)) {
                return true;
            }
        }
        return false;
    }

    public String add(int a, String rest, String username) {
        String res = null;
        if (a >= access) {
            String[] s = rest.split(" ", 2);
            if (s.length >= 2 && s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0) {
                if (hasBanphrase(s[0])) {
                    res = String.format("The banphrase '%s' already exists", s[0]);
                } else {
                    int i = 4; // Default
                    try {
                        i = Integer.parseInt(s[1]);
                    } catch (NumberFormatException nfe) {
                        log.e(nfe.toString());
                    }
                    addBanphrase(new BanPhrase(s[0], i));
                    res = String.format("%s -> the phrase '%s' was banned", username, s[0]);
                }
            } else {
                res = String.format("%s -> the phrase '%s' was not banned", username, s[0]);
            }
        }
        return res;
    }

    public String remove(int a, String rest, String username) {
        String res = null;
        if (a >= access) {
            if (rest != null && rest.length() > 0) {
                if (hasBanphrase(rest)) {
                    if (removeBanphrase(rest)) {
                        res = String.format("%s -> the phrase '%s' was unbanned", username, rest);
                    }
                } else {
                    res = String.format("%s -> The phrase '%s' does not exist", username, rest);
                }
            } else {
                res = String.format("%s -> invalid parameters", username);
            }
        }
        return res;
    }
}
