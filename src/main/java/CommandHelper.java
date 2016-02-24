public class CommandHelper {
    private String command;
    private String response;
    private int access;

    public CommandHelper(String command, String response, int a) {
        this.command = command;
        this.response = response;
        this.access = a;
    }

    public CommandHelper(String str) {
        String[] split = str.split(";", 3);
        this.command = split[0];
        this.access = Integer.parseInt(split[1]);
        this.response = split[2];
    }

    public String getCommand() {
        return command;
    }

    public String getResponse() {
        return response;
    }
    public int getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return String.join(";", new String[]{command, String.valueOf(access), response});
    }
}
