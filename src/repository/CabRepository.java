package repository;

import changelog.SourceOfChange;
import model.Cab;
import model.Location;

import java.util.Set;

public interface CabRepository extends SourceOfChange<Cab> {
    void create(Cab newCab);
    boolean replace(Cab old, Cab updated);
    void remove(Cab cab);
    Cab get(String cabId);

    Set<Cab> getAll();
    Set<Cab> availableCabs(Location location);
}
