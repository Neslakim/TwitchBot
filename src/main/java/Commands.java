import util.Log;
import util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Commands {
    Log log = new Log(getClass().getSimpleName());
    ArrayList<CommandHelper> commands;
    String channelName;
    private int access;

    public Commands(String channelName, int access) {
        log.v("Channelname: " + channelName);
        this.channelName = channelName;
        this.commands = new ArrayList<>();
        this.access = access;
    }

    public CommandHelper getCommand(String s) {
        for (CommandHelper c : commands) {
            if (c.getCommand().equalsIgnoreCase(s)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<CommandHelper> getCommands() {
        return commands;
    }

    public boolean addCommand(CommandHelper com) {
        if (commands.contains(com)) {
            log.v(String.format("Command %s exists", com.getCommand()));
            return false;
        }
        boolean ret = this.commands.add(com);
        saveCommands();
        return ret;
    }

    public boolean setCommand(CommandHelper com) {
        if (hasCommand(com.getCommand())) {
            removeCommand(com.getCommand());
        }
        return addCommand(com);
    }

    public void removeCommand(String com) {
        for (CommandHelper c : commands) {
            if (c.getCommand().equalsIgnoreCase(com)) {
                this.commands.remove(c);
                break;
            }
        }
        saveCommands();
    }

    public void loadCommands() {
        try (FileReader fileReader = new FileReader(String.format("config/%s/commands.txt", channelName));
             BufferedReader reader = new BufferedReader(fileReader)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                commands.add(new CommandHelper(line));
            }
            log.i("Commands loaded");
        } catch (IOException e) {
            log.e("Couldn't load the commands for " + channelName);
        }
    }

    public void saveCommands() {
        try {
            List<String> temp = new ArrayList<>(commands.size());
            temp.addAll(commands.stream().map(CommandHelper::toString).collect(Collectors.toList()));
            Files.write(Paths.get(String.format("config/%s/commands.txt", channelName)), temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCommand(String c) {
        for (CommandHelper com : commands) {
            if (c != null && c.equalsIgnoreCase(com.getCommand())) {
                return true;
            }
        }
        return false;
    }

    public String add(int a, String rest, String username) {
        String res = null;
        if (a >= access) {
            String[] s = rest.split(" ", 3);
            if (s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0 && s[2] != null && s[2].length() > 0) {
                String c1 = Utils.commandWithoutExMark(s[0]);
                if (!hasCommand(c1)) {
                    int c2 = 4; // Default = broadcaster immune
                    try {
                        c2 = Integer.parseInt(s[1]);
                    } catch (NumberFormatException nfe) {
                        log.e(nfe.toString());
                    }
                    String c3 = s[2];
                    addCommand(new CommandHelper(c1, c3, c2));
                    CommandHelper c = getCommand(c1);
                    if (c != null && c.getCommand().equals(c3)) {
                        res = String.format("%s -> Command %s has been added.", username, c1);
                    } else {
                        res = String.format("%s ->Failed to add command %s.", username, c1);
                    }
                } else {
                    res = String.format("%s -> command '%s' already exists", username, c1);
                }
            } else {
                res = String.format("%s -> Invalid parameters.", username);
            }
        }
        return res;
    }

    public String edit(int a, String rest, String username) {
        String res = null;
        if (a >= access) {
            String[] s = rest.split(" ", 3);
            if (s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0 && s[2] != null && s[2].length() > 0) {
                String c1 = Utils.commandWithoutExMark(s[0]);
                if (hasCommand(c1)) {
                    int c2 = 4; // Default = broadcaster immune
                    try {
                        c2 = Integer.parseInt(s[1]);
                    } catch (NumberFormatException nfe) {
                        log.e(nfe.toString());
                    }
                    String c3 = s[2];
                    setCommand(new CommandHelper(c1, c3, c2));
                    if (getCommand(c1).getResponse().equals(c3)) {
                        res = String.format("%s -> Command %s has been updated.", username, c1);
                    } else {
                        res = String.format("%s -> Failed to edit command %s.", username, c1);
                    }
                } else {
                    res =  String.format("%s -> command '%s' does not exist", username, c1);
                }
            } else {
                res =  String.format("%s -> Invalid parameters.", username);
            }
        }
        return res;
    }

    public String remove(int a, String rest, String username) {
        String res = null;
        if (a >= access) {
            if (rest != null && rest.length() > 0) {
                if (getCommand(rest) == null) {
                    res = String.format("%s - >Command %s does not exist.", username, rest);
                } else {
                    removeCommand(rest);
                    if (getCommand(rest) == null) {
                        res = String.format("%s -> Command %s has been removed.", username, rest);
                    } else {
                        res = String.format("%s -> Failed to remove command %s.", username, rest);
                    }
                }
            } else {
                res = String.format("%s -> Invalid parameters.", username);
            }
        }
        return res;
    }
}
