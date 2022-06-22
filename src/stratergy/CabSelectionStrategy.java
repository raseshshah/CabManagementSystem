package stratergy;

import model.Cab;

import java.util.Set;

public interface CabSelectionStrategy {
    final CabSelectionStrategy MAX_IDLE = new SelectCabWithMaxIdleTime();
    Cab select(Set<Cab> availableCabs);
}
