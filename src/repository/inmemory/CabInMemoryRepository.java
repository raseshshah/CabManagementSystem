package repository.inmemory;

import model.Cab;
import model.CabState;
import model.Location;
import repository.CabRepository;
import repository.LocationRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CabInMemoryRepository implements CabRepository {
    private final LocationRepository locationRepository;
    private final Map<String, Cab> cabById = new ConcurrentHashMap<>();

    public CabInMemoryRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void create(Cab newCab) {
        validLocation(newCab);
        if (cabById.containsKey(newCab.id())) throw new IllegalArgumentException("cab exists");
        cabById.computeIfAbsent(newCab.id(), (k) -> {
            notifyChange(null, newCab);
            return newCab;
        });
    }

    @Override
    public boolean replace(Cab old, Cab updated) {
        validLocation(updated);
        if (!Objects.equals(old.id(), updated.id()))
            throw new IllegalArgumentException("Trying to replace unrelated cabs: old " + old.id() + " new " + updated.id());
        Cab computed = cabById.computeIfPresent(old.id(), (k, existing) -> {
            if (existing.equals(old)) {
                notifyChange(existing, updated);
                return updated;
            } else return existing;
        });
        return Objects.equals(computed, updated);
    }

    @Override
    public void remove(Cab cab) {
        cabById.remove(cab.id());
    }

    @Override
    public Cab get(String cabId) {
        return cabById.get(cabId);
    }

    @Override
    public Set<Cab> getAll() {
        return new HashSet<>(cabById.values());
    }

    @Override
    public Set<Cab> availableCabs(Location location) {
        return cabById.values().stream().filter(c -> c.state() == CabState.IDLE && location.equals(c.location())).collect(Collectors.toSet());
    }

    private void validLocation(Cab cab) {
        if (!locationRepository.exists(cab.location()))
            throw new IllegalArgumentException("cab location is out side service location " + cab.location());
        if(cab.state() == CabState.IDLE && cab.location().equals(Location.UNKNOWN))
            throw new IllegalArgumentException("cab required proper location in case of idle state");
        if(cab.state() == CabState.ON_TRIP && !cab.location().equals(Location.UNKNOWN))
            throw new IllegalArgumentException("cab should not be located while on trip");
    }
}
