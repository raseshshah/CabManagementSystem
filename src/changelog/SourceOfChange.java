package changelog;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface SourceOfChange<T> {
    Set<ChangeListener<Object>> changeListeners = ConcurrentHashMap.newKeySet();
    default void registerChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }
    default void unregisterChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }
    default void notifyChange(T oldValue, T newValue) {
        changeListeners.forEach(l -> l.onUpdate(oldValue, newValue));
    }
}
