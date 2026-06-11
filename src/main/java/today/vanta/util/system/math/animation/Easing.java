package today.vanta.util.system.math.animation;

public interface Easing {
    float ease(float t);

    Easing LINEAR = t -> t;
    Easing EASE_IN = t -> t * t;
    Easing EASE_OUT = t -> t * (2 - t);
    Easing EASE_IN_OUT = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
}