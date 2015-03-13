/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api;

import retrofit.client.Response;
import rx.Observable;

public interface Query<T> {

    Observable<T> get(int id, String slug);

    Observable<Response> save(T instance);

}
