package today.vanta.util.game.render.shape;

import today.vanta.util.game.render.Renderable;

public abstract class Shape<T extends Shape<T>> {
    protected double x, y, width, height;
    protected float rotation;

    @SuppressWarnings("unchecked")
    public T rotate(float rotation) {
        this.rotation = rotation;
        return (T) this;
    }

    public abstract void push(Renderable renderable);
}