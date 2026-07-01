package today.vanta.client.event.impl.game.network;

import today.vanta.client.event.Event;

public class PrintChatMessage extends Event {
    public final String message;

    public PrintChatMessage(String message) {
        this.message = message;
    }
}