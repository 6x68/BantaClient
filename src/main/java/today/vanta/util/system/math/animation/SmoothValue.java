package today.vanta.util.system.math.animation;

public class SmoothValue {
    private float value;
    private float target;
    private final float speed;
    private long lastNano;

    public SmoothValue(float initial, float speed) {
        this.value = initial;
        this.target = initial;
        this.speed = speed;
        this.lastNano = System.nanoTime();
    }

    public SmoothValue target(float target) {
        this.target = target;
        return this;
    }

    public float get() {
        long now = System.nanoTime();
        float dt = Math.min((now - lastNano) / 1_000_000_000f, 0.05f);
        lastNano = now;

        float factor = 1f - (float) Math.exp(-speed * dt);
        value += (target - value) * factor;

        if (Math.abs(target - value) < 0.001f) {
            value = target;
        }

        return value;
    }
}
