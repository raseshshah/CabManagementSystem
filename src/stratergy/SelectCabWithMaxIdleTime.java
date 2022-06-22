package stratergy;

import model.Cab;

import java.util.Comparator;
import java.util.Set;

public class SelectCabWithMaxIdleTime implements CabSelectionStrategy {
    @Override
    public Cab select(Set<Cab> availableCabs) {
        if (availableCabs.isEmpty()) throw new IllegalArgumentException("cannot select cab from empty cabs");
        return availableCabs.stream().min(Comparator.comparingLong(Cab::updatedTs)).get();
    }
}
