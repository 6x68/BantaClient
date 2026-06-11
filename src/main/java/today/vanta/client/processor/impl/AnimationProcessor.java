package today.vanta.client.processor.impl;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.DrawScreenEvent;
import today.vanta.client.processor.Processor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.system.math.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnimationProcessor extends Processor {
    private final List<Animation> activeAnimations = new CopyOnWriteArrayList<>();

    public void register(Animation animation) {
        this.activeAnimations.add(animation);
    }

    @EventListen
    private void onScreen(DrawScreenEvent event) {
        activeAnimations.removeIf(animation -> {
            animation.update();
            return animation.finished;
        });
    }

    public static AnimationProcessor getInstance() {
        return Vanta.instance.processorStorage.getT(AnimationProcessor.class);
    }
}
