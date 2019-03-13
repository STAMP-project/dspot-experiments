package com.pushtorefresh.storio3.sqlite.integration;


import android.database.Cursor;
import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResults;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class UpdateTest extends BaseTest {
    @Test
    public void updateOne() {
        final User userForInsert = putUserBlocking();
        final User userForUpdate = // using id of inserted user
        // new value
        User.newInstance(userForInsert.id(), "new@email.com");
        updateUserBlocking(userForUpdate);
        checkOnlyOneItemInStorage(userForUpdate);// update should not add new rows!

    }

    @Test
    public void updateNullFieldToNotNull() {
        final User userForInsert = User.newInstance(null, "user@email.com", null);// phone is null

        putUserBlocking(userForInsert);
        final User userForUpdate = // phone not null
        User.newInstance(userForInsert.id(), userForInsert.email(), "1-999-547867");
        updateUserBlocking(userForUpdate);
        checkOnlyOneItemInStorage(userForUpdate);
    }

    @Test
    public void updateNotNullFieldToNull() {
        final User userForInsert = User.newInstance(null, "user@email.com", "1-999-547867");// phone not null

        putUserBlocking(userForInsert);
        final User userForUpdate = // phone is null
        User.newInstance(userForInsert.id(), userForInsert.email(), null);
        updateUserBlocking(userForUpdate);
        checkOnlyOneItemInStorage(userForUpdate);
    }

    @Test
    public void updateCollection() {
        final List<User> usersForInsert = TestFactory.newUsers(3);
        final PutResults<User> insertResults = storIOSQLite.put().objects(usersForInsert).prepare().executeAsBlocking();
        assertThat(insertResults.numberOfInserts()).isEqualTo(usersForInsert.size());
        final List<User> usersForUpdate = new ArrayList<User>(usersForInsert.size());
        for (int i = 0; i < (usersForInsert.size()); i++) {
            usersForUpdate.add(User.newInstance(usersForInsert.get(i).id(), ((("new" + i) + "@email.com") + i)));
        }
        final PutResults<User> updateResults = storIOSQLite.put().objects(usersForUpdate).prepare().executeAsBlocking();
        assertThat(updateResults.numberOfUpdates()).isEqualTo(usersForUpdate.size());
        final Cursor cursor = db.query(UserTableMeta.TABLE, null, null, null, null, null, null);
        assertThat(cursor.getCount()).isEqualTo(usersForUpdate.size());// update should not add new rows!

        for (int i = 0; i < (usersForUpdate.size()); i++) {
            assertThat(cursor.moveToNext()).isTrue();
            assertThat(UserTableMeta.GET_RESOLVER.mapFromCursor(storIOSQLite, cursor)).isEqualTo(usersForUpdate.get(i));
        }
        cursor.close();
    }
}

