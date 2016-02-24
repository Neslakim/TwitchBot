import org.pircbotx.Channel;
import util.Log;
import util.Utils;

import java.io.*;
import java.util.*;


public class TwitchChannel {
    Properties properties = new Properties();
    Log log = new Log(getClass().getSimpleName());
    private String name;
    private Commands commands;
    private BanPhraseHelper banPhrases;
    private List<String> tempUrlPermit;
    private Ascii ascii;
    private Emote emote;
    private Caps caps;
    private Url url;
    private FollowAge followAge;

    public TwitchChannel(String n) {
        this.name = Utils.channelNameWithoutOctothorp(n);
        Utils.createFile(new File(String.format("config/%s/commands.txt", n)));
        Utils.createFile(new File(String.format("config/%s/banphrases.txt", n)));
        tempUrlPermit = new ArrayList<>();
        init();
    }

    public void init() {

        try (InputStream i = new FileInputStream(String.format("config/%s/config.cfg", name))) {
            properties.load(i);

            if (Boolean.valueOf(properties.getProperty("emote"))) {
                int duration = Integer.parseInt(properties.getProperty("emote-duration"));
                int limit = Integer.parseInt(properties.getProperty("emote-limit"));
                int bypass = Integer.parseInt(properties.getProperty("emote-bypass"));
                this.emote = new Emote(duration, limit, bypass);
                log.v("Emote protection enabled");
            }

            if (Boolean.valueOf(properties.getProperty("url"))) {
                int duration = Integer.parseInt(properties.getProperty("url-duration"));
                int bypass = Integer.parseInt(properties.getProperty("url-bypass"));
                this.url = new Url(duration, bypass);
                log.v("URL protection enabled");
            }

            if (Boolean.valueOf(properties.getProperty("ascii"))) {
                int duration = Integer.parseInt(properties.getProperty("ascii-duration"));
                int limit = Integer.parseInt(properties.getProperty("ascii-limit"));
                int bypass = Integer.parseInt(properties.getProperty("ascii-bypass"));
                this.ascii = new Ascii(duration, limit, bypass);
                log.v("Ascii protection enabled");
            }

            if (Boolean.valueOf(properties.getProperty("caps"))) {
                int duration = Integer.parseInt(properties.getProperty("caps-duration"));
                int limit = Integer.parseInt(properties.getProperty("caps-limit"));
                int bypass = Integer.parseInt(properties.getProperty("caps-bypass"));
                this.caps = new Caps(duration, limit, bypass);
                log.v("All-caps protection enabled");
            }
            if (Boolean.valueOf(properties.getProperty("banphrases"))) {
                int duration = Integer.parseInt(properties.getProperty("banphrases-duration"));
                int access = Integer.parseInt(properties.getProperty("banphrases-access"));
                banPhrases = new BanPhraseHelper(this.name, duration, access);
                banPhrases.loadBanphrases();
            }
            if (Boolean.valueOf(properties.getProperty("commands"))) {
                int access = Integer.parseInt(properties.getProperty("commands-access"));
                commands = new Commands(this.name, access);
                commands.loadCommands();
            }

            if (Boolean.valueOf(properties.getProperty("followage"))) {
                int access = Integer.parseInt(properties.getProperty("followage-access"));
                followAge = new FollowAge(access);
                log.v("Follow age enabled");
            }

            i.close();
        } catch (IOException e) {
            log.e("Couldn't load the main configuration file, closing program...");
        } catch (NumberFormatException nfe) {
            log.e(nfe.toString());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commands getCommands() {
        return commands;
    }

    public BanPhraseHelper getBanphrases() {
        return banPhrases;
    }

    public void addTempUrlPermit(String nick) {
        tempUrlPermit.add(nick);
        log.v("Adding temp url permit:");
    }

    public void removeTempUrlPermit(String nick) {
        log.v("Removing temp url permit:");
        tempUrlPermit.remove(nick);
    }

    public boolean hasTempUrlPermit(String nick) {
        return tempUrlPermit.contains(nick);
    }

    public void sendMessage(Channel channel, String message) {
        log.i(String.format("Sending message: %s", message));
        channel.send().message(message);
    }

    public void timeOut(Channel channel, String username, int duration) {
        sendMessage(channel, String.format(".timeout %s %d", username, duration));
    }

    private void ban(Channel channel, String username) {
        sendMessage(channel, String.format(".ban %s", username));
    }

    private void unBan(Channel channel, String username) {
        sendMessage(channel, String.format(".unban %s", username));
    }

    public void checkStuff(String username, TwitchChannel channel, Channel chan, String message, Map<String, String> tags, int access) {
        String emotes = tags.get("emotes");
        if (emote != null && emotes != null && emotes.length() > 0 && emote.check(emotes.substring(7), access)) {
            channel.timeOut(chan, username, emote.duration);
//            channel.sendMessage(chan, String.format("%s, enough with the emotes.", username));
        }
        if (url != null && url.check(message, access, hasTempUrlPermit(username))) {
            channel.timeOut(chan, username, url.duration);
//            channel.sendMessage(chan, String.format("No links allowed (%s).", username));
        }
        if (caps != null && caps.check(message, access)) {
            channel.timeOut(chan, username, caps.duration);
//            channel.sendMessage(chan, String.format("%s, please do not post in all caps.", username));
        }
        if (banPhrases != null && banPhrases.check(message)) {
            channel.timeOut(chan, username, banPhrases.duration);
        }
        if (ascii != null && ascii.check(message, access)) {
            channel.timeOut(chan, username, ascii.duration);
//            channel.sendMessage(chan, String.format("%s, please do not post in all caps.", username));
        }
    }

    public void changeProperty(String key, String value) {
        properties.setProperty(key, value);
        try {
            saveProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProperties(Properties p) throws IOException {
        Properties tmp = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        tmp.putAll(properties);
        tmp.store(new FileWriter(String.format("config/%s/config.cfg", name)), null);
    }

    public FollowAge getFollowAge() {
        return followAge;
    }

    public void whisper(Channel chan, String receiver, String message) {

    }
}
