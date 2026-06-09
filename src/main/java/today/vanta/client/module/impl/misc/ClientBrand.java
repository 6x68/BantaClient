package today.vanta.client.module.impl.misc;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.ClientBrandEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientBrand extends Module {
    private boolean message;

    private final StringSetting brand = StringSetting.builder()
            .name("Brand")
            .value("vanilla")
            .values("vanilla")
            .build();

    public ClientBrand() {
        super("ClientBrand", "Changes the clients brand.", Category.MISC);

        brand.addValue(getLatestLunarVersion());

        brand.addListener(((setting, oldValue, newValue) -> {
            if (!message && mc.thePlayer != null) {
                ChatUtil.warn("You must rejoin for the brand to show up!");
                message = true;
            }
        }));
    }

    @EventListen
    private void onUpdate(ClientBrandEvent event) {
        event.brand = brand.getValue();
    }

    @Override
    public void onEnable() {
        if (!message && mc.thePlayer != null) {
            ChatUtil.warn("You must rejoin for the brand to show up!");
            message = true;
        }
    }

    private static String getLatestLunarVersion() {
        String fallback = "lunarclient:v2.22.8-2623";

        try {
            URL url = new URL("https://www.lunarclient.com/changelog");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
            );

            StringBuilder html = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    html.append(line);
                }
            }

            Pattern pattern = Pattern.compile("v\\d+\\.\\d+\\.\\d+-\\d+");
            Matcher matcher = pattern.matcher(html.toString());

            if (matcher.find()) {
                String found = "lunarclient:" + matcher.group();
                Vanta.instance.logger.info("Latest Lunar Client version is: {}", found);
                return found;
            }

            return fallback;
        } catch (Exception e) {
            Vanta.instance.logger.error("Failed to fetch latest Lunar Client version", e);
            return fallback;
        }
    }
}