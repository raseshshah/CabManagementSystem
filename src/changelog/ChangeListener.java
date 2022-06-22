package changelog;

public interface ChangeListener<T> {
    void onUpdate(T oldValue, T newValue);
}
