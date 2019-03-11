package com.pushtorefresh.storio3.sqlite.integration;


import com.pushtorefresh.storio3.sqlite.operations.get.PreparedGetListOfObjects;
import java.util.Collections;
import org.junit.Test;


public class GetListOfObjectsObserveChangesTest extends BaseOperationObserveChangesTest {
    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        User user = putUserBlocking();
        PreparedGetListOfObjects<User> operation = storIOSQLite.get().listOfObjects(User.class).withQuery(query).prepare();
        verifyChangesReceived(operation, tableChanges, Collections.singletonList(user));
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        User user = putUserBlocking();
        PreparedGetListOfObjects<User> operation = storIOSQLite.get().listOfObjects(User.class).withQuery(rawQuery).prepare();
        verifyChangesReceived(operation, tableChanges, Collections.singletonList(user));
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        User user = putUserBlocking();
        PreparedGetListOfObjects<User> operation = storIOSQLite.get().listOfObjects(User.class).withQuery(query).prepare();
        verifyChangesReceived(operation, tagChanges, Collections.singletonList(user));
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        User user = putUserBlocking();
        PreparedGetListOfObjects<User> operation = storIOSQLite.get().listOfObjects(User.class).withQuery(rawQuery).prepare();
        verifyChangesReceived(operation, tagChanges, Collections.singletonList(user));
    }
}

