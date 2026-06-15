package today.vanta.client.event.impl.game.render;

import net.minecraft.entity.Entity;
import today.vanta.client.event.Event;

public class RenderNametagsEvent extends Event {
    public final Entity entity;
    public final double x, y, z;

    public RenderNametagsEvent(Entity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}