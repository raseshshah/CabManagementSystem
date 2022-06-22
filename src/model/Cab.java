package model;

import java.time.Instant;
import java.util.Objects;

public record Cab(String id, CabState state, Location location, long updatedTs) {
    public Cab(String id, CabState state, Location location) {
        this(id, state, location, System.currentTimeMillis());
    }
    public Cab withStateAndCity(CabState newState, Location newLocation) {
        return new Cab(id, newState, newLocation);
    }

    @Override
    public String toString() {
        return "Cab[" +
                "id='" + id + '\'' +
                ", state=" + state +
                ", location=" + location +
                ", updatedTs=" + Instant.ofEpochMilli(updatedTs) +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cab cab = (Cab) o;
        return Objects.equals(id, cab.id) && state == cab.state && Objects.equals(location, cab.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, location);
    }
}