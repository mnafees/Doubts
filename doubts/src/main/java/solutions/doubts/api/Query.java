/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api;

import rx.Observable;

public interface Query<T> {

    Observable<T> get(int id, String slug);

    void save(T instance);

}
