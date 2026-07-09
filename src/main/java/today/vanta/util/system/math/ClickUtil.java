package today.vanta.util.system.math;

import today.vanta.Vanta;
import today.vanta.client.module.impl.client.Clicking;

import java.util.Random;

public class ClickUtil {

    private final Random random;

    private long lastClickNanos;
    private long nextDelayNanos;
    private boolean initialized;

    private int burstTicks;
    private double burstMultiplier = 1.0;
    private int hurtCooldown;

    private double pendingGaussian = Double.NaN;

    public ClickUtil() {
        long seed = System.nanoTime();
        seed ^= Runtime.getRuntime().freeMemory();
        seed ^= Thread.currentThread().getId() * 31L;
        seed ^= System.currentTimeMillis() * 2654435761L;
        this.random = new Random(seed);
    }

    public boolean shouldClick(boolean hurt) {
        Clicking clicking = Vanta.instance.moduleStorage.getT(Clicking.class);
        if (clicking == null) {
            return false;
        }

        long now = System.nanoTime();

        if (!initialized) {
            this.initialized = true;
            this.lastClickNanos = now;
            this.nextDelayNanos = calculateDelayNanos(hurt, clicking);
            return false;
        }

        long elapsed = now - lastClickNanos;
        if (elapsed >= nextDelayNanos) {
            if (elapsed > nextDelayNanos * 4) {
                lastClickNanos = now;
            } else {
                lastClickNanos += nextDelayNanos;
            }
            nextDelayNanos = calculateDelayNanos(hurt, clicking);
            return true;
        }

        return false;
    }

    private long calculateDelayNanos(boolean hurt, Clicking clicking) {
        int minCps = clicking.minCps.getValue().intValue();
        int maxCps = clicking.maxCps.getValue().intValue();

        if (minCps > maxCps) {
            int tmp = minCps;
            minCps = maxCps;
            maxCps = tmp;
        }

        updateState(hurt, clicking);

        double cps;
        if (hurt && clicking.hurtReaction.getValue()) {
            double boost = clicking.hurtBoost.getValue().doubleValue() / 100.0;
            double upper = maxCps + (maxCps - minCps) * boost + 2.5;
            cps = range((minCps + maxCps) / 2.0, upper);
        } else {
            cps = range(minCps, maxCps);
        }

        double delayMs = 1000.0 / cps;

        double jitter = clicking.jitter.getValue().doubleValue() / 100.0;
        delayMs += nextGaussian() * delayMs * jitter;

        delayMs *= burstMultiplier;

        double minDelay = 1000.0 / (maxCps + 6.0);
        double maxDelay = 1000.0 / Math.max(1.0, minCps - 1.0);
        delayMs = Math.max(minDelay, Math.min(delayMs, maxDelay));

        return (long) (delayMs * 1_000_000.0);
    }

    private void updateState(boolean hurt, Clicking clicking) {
        if (hurtCooldown > 0) {
            hurtCooldown--;
        }

        if (hurt && clicking.hurtReaction.getValue() && hurtCooldown == 0) {
            hurtCooldown = 10;
            double boost = clicking.hurtBoost.getValue().doubleValue() / 100.0;
            burstTicks = random.nextInt(3) + 2;
            burstMultiplier = 1.0 - boost * (0.5 + random.nextDouble() * 0.5);
            return;
        }

        if (!clicking.flicks.getValue()) {
            burstMultiplier = 1.0;
            burstTicks = 0;
            return;
        }

        if (burstTicks > 0) {
            burstTicks--;
            if (burstTicks == 0) {
                burstMultiplier = 1.0;
            }
            return;
        }

        double flickChance = clicking.flickChance.getValue().doubleValue() / 100.0;
        double fatigueChance = clicking.fatigueChance.getValue().doubleValue() / 100.0;
        double r = random.nextDouble();

        if (r < flickChance) {
            double boost = clicking.flickBoost.getValue().doubleValue() / 100.0;
            burstTicks = random.nextInt(4) + 2;
            burstMultiplier = 1.0 - boost * (0.5 + random.nextDouble() * 0.5);
        } else if (r < flickChance + fatigueChance) {
            double slowdown = clicking.fatigueSlowdown.getValue().doubleValue() / 100.0;
            burstTicks = random.nextInt(3) + 1;
            burstMultiplier = 1.0 + slowdown * (0.5 + random.nextDouble() * 0.5);
        } else {
            burstMultiplier = 1.0;
        }
    }

    private double nextGaussian() {
        if (!Double.isNaN(pendingGaussian)) {
            double value = pendingGaussian;
            pendingGaussian = Double.NaN;
            return value;
        }

        double u1, u2, s;
        do {
            u1 = 2.0 * random.nextDouble() - 1.0;
            u2 = 2.0 * random.nextDouble() - 1.0;
            s = u1 * u1 + u2 * u2;
        } while (s >= 1.0 || s == 0.0);

        double multiplier = Math.sqrt(-2.0 * Math.log(s) / s);
        pendingGaussian = u2 * multiplier;
        return u1 * multiplier;
    }

    private double range(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
