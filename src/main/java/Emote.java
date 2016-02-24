public class Emote {
    public int limit;
    public int bypass;
    public int duration;

    public Emote(int duration, int limit, int bypass) {
        this.duration = duration;
        this.limit = limit;
        this.bypass = bypass;
    }

    private int emoteCount(String s) {
        int c = 0;
        String[] emotes = s.split("/");
        for (String emote : emotes) {
            String[] e = emote.split(",");
            c += e.length;
        }
        return c;
    }

    public boolean check(String message, int access) {
        return access < bypass && emoteCount(message) > limit;
    }
}
