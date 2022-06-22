package repository.inmemory;

import changelog.ChangeLog;
import changelog.ChangeListener;
import model.Cab;
import model.CabState;
import repository.CabStateChangeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CabStateChangeInMemoryRepository implements CabStateChangeRepository, ChangeListener<Cab> {
    private final Map<String, List<ChangeLog<CabState>>> stateChangeLogsById = new ConcurrentHashMap<>();

    // optimized using Binary search
    @Override
    public List<ChangeLog<CabState>> logs(String cabId, long startTs, long endTs) {
        return stateChangeLogsById.getOrDefault(cabId, new ArrayList<>()).stream().filter((cl) -> cl.timestamp() >= startTs && cl.timestamp() <= endTs).collect(Collectors.toList());
    }

    // optimized using Binary search
    @Override
    public long totalIdleTimeInGivenDuration(String cabId, long startTs, long endTs) {
        long totalIdleTime = 0;
        List<ChangeLog<CabState>> log = stateChangeLogsById.get(cabId);
        for (int i = 0; i < log.size() && log.get(i).timestamp() <= endTs; i++) {
            if (i + 1 < log.size() && log.get(i + 1).timestamp() < startTs) continue;
            if (log.get(i).value() == CabState.IDLE) {
                long endWithinRange = i + 1 < log.size() && log.get(i + 1).timestamp() < endTs ? log.get(i + 1).timestamp() : endTs;
                long startWithinRange = startTs > log.get(i).timestamp() ? startTs : log.get(i).timestamp();
                totalIdleTime += endWithinRange - startWithinRange;
            }
        }
        return totalIdleTime;
    }

    @Override
    public void onUpdate(Cab o, Cab n) {
        if (o != null && o.state() == n.state()) return;
        ChangeLog<CabState> log = new ChangeLog<>(n.updatedTs(), n.state());
        stateChangeLogsById.putIfAbsent(n.id(), new CopyOnWriteArrayList<>());
        stateChangeLogsById.get(n.id()).add(log);
    }
}
