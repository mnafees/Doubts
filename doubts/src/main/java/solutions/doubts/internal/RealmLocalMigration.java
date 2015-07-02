/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.internal;

import io.realm.Realm;
import io.realm.RealmMigration;

public class RealmLocalMigration implements RealmMigration {

    @Override
    public long execute(Realm realm, long version) {
        return 0;
    }
}
