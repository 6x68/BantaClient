package today.vanta.util.client;

public interface IClient {
    String CLIENT_NAME = "Vanta";
    String CLIENT_VERSION = "1.0";
    String CLIENT_FULL_TITLE = CLIENT_NAME + " - " + CLIENT_VERSION;

    String USERNAME = System.getProperty("user.name");
}
