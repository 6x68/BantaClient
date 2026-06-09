package today.vanta.client.event.impl.game;

import today.vanta.client.event.Event;

public class ClientBrandEvent extends Event {
    public String brand;

    public ClientBrandEvent(String brand) {
        this.brand = brand;
    }
}