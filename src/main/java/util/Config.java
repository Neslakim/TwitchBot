package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public String botName;
    public String oauth;
    static Log log = new Log("config");
    private static String filename = "config/bot.cfg";

    public Config() {
        File f = new File(filename);
        if (f.exists() && !f.isDirectory()) {
            load();
        }
    }

    public void load() {
        Properties p = new Properties();
        try (InputStream i = new FileInputStream(filename)) {
            p.load(i);
            botName = String.valueOf(p.getProperty("botname"));
            oauth = String.valueOf(p.getProperty("oauth"));
            i.close();
        } catch (IOException e) {
            log.e("Couldn't load the main configuration file, closing program...");
            System.exit(1);
        }
    }
}
