package repository;

import changelog.ChangeListener;
import model.Cab;
import model.CabState;
import changelog.ChangeLog;

import java.util.List;

public interface CabStateChangeRepository extends ChangeListener<Cab> {
    List<ChangeLog<CabState>> logs(String cabId, long startTs, long endTs);
    long totalIdleTimeInGivenDuration(String cabId, long startTs, long endTs);
}
