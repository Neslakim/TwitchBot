public class BanPhrase {
    private String banphrase;
    private int immunity;

    public BanPhrase(String banphrase, int immunity) {
        this.banphrase = banphrase;
        this.immunity = immunity;
    }

    public BanPhrase(String str) {
        String[] split = str.split(";", 2);
        immunity = Integer.parseInt(split[0]);
        banphrase = split[1];
    }

    @Override
    public String toString() {
        return String.join(";", new String[]{String.valueOf(immunity), banphrase});
    }

    public String getBanphrase() {
        return banphrase;
    }

    public int getImmunity() {
        return immunity;
    }
}
