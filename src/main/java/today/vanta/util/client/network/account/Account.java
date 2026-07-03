package today.vanta.util.client.network.account;

public class Account {
    public String username, uuid;
    public String token = "", refreshToken = "";
    public String skin = AccountSavingUtil.getSteveHead();

    public Account(String username, String uuid) {
        this.username = username;
        this.uuid = uuid == null ? "" : uuid;
    }

    public Account(String username, String uuid, String token) {
        this(username, uuid);
        this.token = token;
    }

    public Account(String username, String uuid, String token, String refreshToken) {
        this(username, uuid);
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public boolean isEmail() {
        return username.contains("@");
    }

    public boolean isCracked() {
        return uuid.isEmpty();
    }
}