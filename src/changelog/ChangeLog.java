package changelog;

import java.time.Instant;

public record ChangeLog<T>(long timestamp, T value) {
    @Override
    public String toString() {
        return "ChangeLog[" +
                "timestamp=" + Instant.ofEpochMilli(timestamp) +
                ", value=" + value +
                ']';
    }
}
