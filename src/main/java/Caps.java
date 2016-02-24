public class Caps {
    public int limit;
    public int bypass;
    public int duration;

    public Caps(int duration, int limit, int bypass) {
        this.duration = duration;
        this.limit = limit;
        this.bypass = bypass;
    }

    public double capsPercentage(String s) {
//        s = s.replaceAll("\\s+","");
//        s = s.replaceAll("[^a-zA-Z]","");
        int caps = 0;
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                caps++;
            }
        }
        return (caps * 1D) / (s.length() * 1D) * 100D;
    }

    public boolean check(String message, int access) {
        return access < bypass && capsPercentage(message) > limit;
    }
}
