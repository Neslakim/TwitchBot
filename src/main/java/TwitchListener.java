import org.pircbotx.*;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import util.Config;
import util.Log;
import util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TwitchListener extends ListenerAdapter {
    Log log = new Log(getClass().getSimpleName());
    private static String URL = "irc.twitch.tv";
    private static String[] GROUP_SERVERS = {"192.16.64.212", "192.16.64.180", "199.9.253.119", "199.9.253.120"};
    private static int PORT = 6667;
    static Config config;
    static MultiBotManager manager;
    static PircBotX main;
    static PircBotX whisper;
    static Map<String, TwitchChannel> channels;

    public static void main(String[] args) {
//        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        config = new Config();
        List<String> c = new ArrayList<>();
        ArrayList<String> cn = readTextFile("config/channels.txt");
        c.stream().filter(s -> !s.startsWith("//")).forEach(s -> {
            c.add(Utils.channelNameWithOctothorp(s));
            TwitchChannel channel = new TwitchChannel(Utils.channelNameWithoutOctothorp(s));
            channels.put(s, channel);
        });
        Configuration.Builder configuration = new Configuration.Builder()
                .setServerPassword(config.oauth)
                .setOnJoinWhoEnabled(false)
                .setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
                .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
                .setName(config.botName)
                .setAutoNickChange(false)
                .setMessageDelay(0)
                .addListener(new TwitchListener())
                .addAutoJoinChannels(c)
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(99999)
                .setAutoReconnectDelay(1000);
        manager = new MultiBotManager();
        main = new PircBotX(configuration.buildForServer(URL, PORT));
        whisper = new PircBotX(configuration.buildForServer(GROUP_SERVERS[2], PORT));
        manager.addBot(main);
        manager.addBot(whisper);
        manager.start();
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
//        Log.v("GenMsg: " + event.toString());
    }

    @Override
    public void onAction(ActionEvent event) {
        // Tags are not yet implemented for actions
        /*TwitchBot bot = event.getBot();
        User user = event.getUser();
        String username = null;
        if (user != null) {
            username = user.getNick();
        }
        Channel chan = event.getChannel();
        TwitchChannel channel = bot.channels.get(event.getChannelSource());
        String message = event.getMessage();
        log.d(event.toString());
        Map<String, String> tags = event.getV3Tags();
        int sub = Integer.parseInt(tags.get("subscriber"));
        boolean mod = tags.get("user-type").equalsIgnoreCase("mod");
        int access = getAccess(username, channel.getName(), sub, mod);
        channel.checkStuff(username, channel, chan, message, tags, access);*/
    }

    @Override
    public void onMessage(MessageEvent event) {
        User user = event.getUser();
        String username = null;
        if (user != null) {
            username = user.getNick();
        }
        Channel chan = event.getChannel();
        TwitchChannel channel = channels.get(event.getChannelSource());

        String message = event.getMessage();
        Map<String, String> tags = event.getV3Tags();
        int sub = Integer.parseInt(tags.get("subscriber"));
        boolean mod = tags.get("user-type").equalsIgnoreCase("mod");
        int access = getAccess(username, channel.getName(), sub, mod);

        if (message.startsWith("!")) {
            String[] msgArray = message.toLowerCase().split(" ", 2);
            String command = "";
            if (msgArray.length > 0) {
                command = msgArray[0];
            }
            String rest = "";
            if (msgArray.length > 1) {
                rest = msgArray[1];
            }
            log.v(String.format("%s: %s %s", username, command, rest));
            switch (command) {
                case "!test":
                    channel.sendMessage(chan, rest);
                    break;
                case "!addcmd":
                    if (channel.getCommands() != null) {
                        String res = channel.getCommands().add(access, rest, username);
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!editcmd":
                    if (channel.getCommands() != null) {
                        String res = channel.getCommands().edit(access, rest, username);
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!rmcmd":
                    if (channel.getCommands() != null) {
                        String res = channel.getCommands().remove(access, rest, username);
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!quit":
                    if (access >= 4) {
                        manager.stop();
                    }
                    break;
                case "!addbp":
                    if (channel.getBanphrases() != null) {
                        String res = channel.getBanphrases().add(access, rest, username);
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!rmbp":
                    if (channel.getBanphrases() != null) {
                        String res = channel.getBanphrases().remove(access, rest, username);
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!permit":
                    String[] s = rest.split(" ", 2);
                    String nick = null;
                    if (s.length > 0) {
                        nick = s[0];
                    }
                    int time = 0;
                    if (s.length > 1) {
                        try {
                            time = Integer.parseInt(s[1]);
                        } catch (NumberFormatException nfe) {
                            log.e(nfe.toString());
                        }
                    }
                    final String n = nick;
                    channel.addTempUrlPermit(nick);
                    new Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    channel.removeTempUrlPermit(n);
                                }
                            },
                            time * 1000
                    );
                    channel.sendMessage(chan, String.format("%s is permitted to post links for %d seconds", nick, time));
                    break;
                case "!followage":
                    if (channel.getFollowAge() != null) {
                        String res = channel.getFollowAge().get(access, username, channel.getName());
                        if (res != null) {
                            channel.sendMessage(chan, res);
                        }
                    }
                    break;
                case "!color":
                    if (access == 4) {
                        channel.sendMessage(chan, String.format(".color %s", rest));
                    }
                    break;
                default:
                    command = Utils.commandWithoutExMark(command);
                    CommandHelper com = channel.getCommands().getCommand(command);
                    if (com != null && access >= com.getAccess() && com.getResponse() != null) {
                        channel.sendMessage(chan, String.format(com.getResponse(), username)); // TODO add more options
                    } else {
                        channel.sendMessage(chan, String.format("%s -> Invalid or unused command.", username));
                    }
                    break;
            }
        } else {
            channel.checkStuff(username, channel, chan, message, tags, access);
        }
    }

    private int getAccess(String username, String channelName, int sub, boolean mod) {
        int access = 1;
        if (username != null
                && username.equalsIgnoreCase(Utils.channelNameWithoutOctothorp(channelName))) {
            access = 4;
        } else if (mod) {
            access = 3;
        } else if (sub == 1) {
            access = 2;
        }
        return access;
    }

    private void whisper(String username, String message) {
        log.i(String.format("Whispering %s: %s", username, message));
        whisper.send().message(username, "/w " + username + " " + message);
    }

    public static ArrayList<String> readTextFile(String fileName) {
        ArrayList<String> result = new ArrayList<>();
        File f = new File(fileName);
        if (!f.exists()) {
            return result;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();

            while (line != null) {
                result.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
