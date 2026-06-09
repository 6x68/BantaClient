package net.minecraft.client;

import today.vanta.client.event.impl.game.ClientBrandEvent;

public class ClientBrandRetriever {
    public static String getClientModName() {
        ClientBrandEvent event = new ClientBrandEvent("vanilla");
        event.call();
        return event.brand;
    }
}