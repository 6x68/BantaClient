package today.vanta.util.system.math.animation;

public interface Easing {
    float ease(float t);

    Easing LINEAR = t -> t;
    Easing EASE_IN = t -> t * t;
    Easing EASE_OUT = t -> t * (2 - t);
    Easing EASE_IN_OUT = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;

    Easing EASE_IN_QUAD = t -> t * t;
    Easing EASE_OUT_QUAD = t -> t * (2 - t);
    Easing EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;

    Easing EASE_OUT_BACK = t -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    };

    Easing EASE_OUT_ELASTIC = t -> {
        float c4 = (2 * (float) Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1 : (float) Math.pow(2, -10 * t) * (float) Math.sin((t * 10 - 0.75) * c4) + 1;
    };

    Easing EASE_OUT_EXPO = t -> t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);

    Easing EASE_IN_QUART = t -> t * t * t * t;
    Easing EASE_OUT_QUART = t -> 1 - (float) Math.pow(1 - t, 4);
    Easing EASE_IN_OUT_QUART = t -> t < 0.5f ? 8 * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 4) / 2;
}