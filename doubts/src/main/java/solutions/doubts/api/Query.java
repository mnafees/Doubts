package solutions.doubts.api;

import rx.Observable;

public interface Query<T> {
    Observable<T> get(int id, String slug);
    void save(T instance);
}
