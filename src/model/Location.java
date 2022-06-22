package model;

import java.time.Instant;
import java.util.Objects;

public record Location(Integer id, String stateName, String cityName) {
    public static Location UNKNOWN = new Location(0, "unknown", "unknown");

    public Location(String stateName, String cityName) {
        this(-1, stateName, cityName);
    }

    public Location withId(int id) {
        return new Location(id, stateName, cityName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(stateName, location.stateName) && Objects.equals(cityName, location.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateName, cityName);
    }
}
