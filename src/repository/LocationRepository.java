package repository;

import model.Location;

import java.util.Optional;

public interface LocationRepository {
    Location create(Location location);

    boolean exists(Location location);


    Location get(int id);
}
