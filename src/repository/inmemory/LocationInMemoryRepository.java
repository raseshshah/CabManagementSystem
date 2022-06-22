package repository.inmemory;

import model.Location;
import repository.LocationRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LocationInMemoryRepository implements LocationRepository {
    private final AtomicInteger id = new AtomicInteger(-1);
    private final Map<Integer, Location> locations = new ConcurrentHashMap<>();

    @Override
    public Location create(Location location) {
        if (exists(location)) {
            throw new IllegalArgumentException("location exits");
        } else {
            Location updatedLocation = location.withId(id.incrementAndGet());
            locations.put(updatedLocation.id(), updatedLocation);
            return updatedLocation;
        }
    }

    @Override
    public boolean exists(Location location) {
        return new HashSet<>(locations.values()).contains(location);
    }

    public Location get(int id) {
        return locations.get(id);
    }
}
