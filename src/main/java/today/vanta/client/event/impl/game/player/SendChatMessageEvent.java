package today.vanta.client.event.impl.game.player;

import today.vanta.client.event.Event;

public class SendChatMessageEvent extends Event {
    public String message;

    public SendChatMessageEvent(String message) {
        this.message = message;
    }
}