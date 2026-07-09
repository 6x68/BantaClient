package today.vanta.util.system.math;

public class Counter {
    private long lastTimestamp = System.nanoTime();

    public long getElapsedTime() {
        return (System.nanoTime() - lastTimestamp) / 1_000_000L;
    }

    public void reset() {
        lastTimestamp = System.nanoTime();
    }

    public boolean hasElapsed(long duration, boolean reset) {
        long elapsedNanos = System.nanoTime() - lastTimestamp;
        long durationNanos = duration * 1_000_000L;
        if (elapsedNanos >= durationNanos) {
            if (reset) {
                this.lastTimestamp = System.nanoTime();
            }
            return true;
        }
        return false;
    }

    public boolean hasElapsed(long duration) {
        return hasElapsed(duration, false);
    }
}
