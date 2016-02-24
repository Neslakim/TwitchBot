import util.Log;

public class Ascii {
    Log log = new Log(getClass().getSimpleName());
    int duration;
    int limit;
    int bypass;

    public Ascii(int duration, int length, int bypass) {
        this.duration = duration;
        this.limit = length;
        this.bypass = bypass;
    }

    private double getRatio(String msg) {
        int n = 0;
        for (char c : msg.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                n++;
            }
        }
        return n / msg.length();
    }

    public boolean check(String msg, int access) {
        double ratio = getRatio(msg);
        return access < bypass && (msg.length() > limit && ratio > 0.8) || ratio > 0.93;
    }
}
