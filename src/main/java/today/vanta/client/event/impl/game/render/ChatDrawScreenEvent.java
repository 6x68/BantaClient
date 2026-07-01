package today.vanta.client.event.impl.game.render;

import today.vanta.client.event.Event;

import java.util.ArrayList;
import java.util.List;

public class ChatDrawScreenEvent extends Event {
    public final String text;
    public String inlineSuggestion;
    public final List<String> suggestions;

    public ChatDrawScreenEvent(String text) {
        this.text = text;
        this.inlineSuggestion = null;
        this.suggestions = new ArrayList<>();
    }
}