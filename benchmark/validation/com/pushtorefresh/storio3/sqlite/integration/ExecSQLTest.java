package com.pushtorefresh.storio3.sqlite.integration;


import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.operations.execute.PreparedExecuteSQL;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ExecSQLTest extends BaseTest {
    @Test
    public void shouldReturnQueryInGetData() {
        final RawQuery query = // we don't want to really delete table
        RawQuery.builder().query("DROP TABLE IF EXISTS no_such_table").build();
        final PreparedExecuteSQL operation = storIOSQLite.executeSQL().withQuery(query).prepare();
        assertThat(operation.getData()).isEqualTo(query);
    }

    @Test
    public void execSQLWithEmptyArgs() {
        // Should not throw exceptions!
        storIOSQLite.executeSQL().withQuery(// we don't want to really delete table
        RawQuery.builder().query("DROP TABLE IF EXISTS no_such_table").build()).prepare().executeAsBlocking();
    }

    @Test
    public void shouldPassArgsAsObjects() {
        final User user = putUserBlocking();
        assertThat(user.id()).isNotNull();
        // noinspection ConstantConditions
        final long uid = user.id();
        final String query = ((((("UPDATE " + (UserTableMeta.TABLE)) + " SET ") + (UserTableMeta.COLUMN_ID)) + " = MIN(") + (UserTableMeta.COLUMN_ID)) + ", ?)";
        storIOSQLite.executeSQL().withQuery(// as integer is less, as string is greater
        RawQuery.builder().query(query).args((uid - 1)).build()).prepare().executeAsBlocking();
        List<User> users = getAllUsersBlocking();
        assertThat(users.size()).isEqualTo(1);
        // Was updated, because (uid - 1) passed as object, not string, and (uid - 1) < uid.
        assertThat(users.get(0).id()).isEqualTo((uid - 1));
    }
}

