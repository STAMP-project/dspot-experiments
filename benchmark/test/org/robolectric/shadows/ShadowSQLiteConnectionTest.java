package org.robolectric.shadows;


import ShadowSQLiteConnection.Connections;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowSQLiteConnectionTest {
    private SQLiteDatabase database;

    private File databasePath;

    private long ptr;

    private SQLiteConnection conn;

    private Connections CONNECTIONS;

    @Test
    public void testSqlConversion() {
        assertThat(ShadowSQLiteConnection.convertSQLWithLocalizedUnicodeCollator("select * from `routine`")).isEqualTo("select * from `routine`");
        assertThat(ShadowSQLiteConnection.convertSQLWithLocalizedUnicodeCollator(("select * from `routine` order by name \n\r \f collate\f\n\tunicode" + "\n, id \n\n\t collate\n\t \n\flocalized"))).isEqualTo(("select * from `routine` order by name COLLATE NOCASE\n" + ", id COLLATE NOCASE"));
        assertThat(ShadowSQLiteConnection.convertSQLWithLocalizedUnicodeCollator("select * from `routine` order by name collate localized")).isEqualTo("select * from `routine` order by name COLLATE NOCASE");
        assertThat(ShadowSQLiteConnection.convertSQLWithLocalizedUnicodeCollator("select * from `routine` order by name collate unicode")).isEqualTo("select * from `routine` order by name COLLATE NOCASE");
    }

    @Test
    public void testSQLWithLocalizedOrUnicodeCollatorShouldBeSortedAsNoCase() throws Exception {
        database.execSQL("insert into routine(name) values ('??????? ???????')");
        database.execSQL("insert into routine(name) values ('Hand press 1')");
        database.execSQL("insert into routine(name) values ('hand press 2')");
        database.execSQL("insert into routine(name) values ('Hand press 3')");
        List<String> expected = Arrays.asList("Hand press 1", "hand press 2", "Hand press 3", "??????? ???????");
        String sqlLocalized = "SELECT `name` FROM `routine` ORDER BY `name` collate localized";
        String sqlUnicode = "SELECT `name` FROM `routine` ORDER BY `name` collate unicode";
        assertThat(simpleQueryForList(database, sqlLocalized)).isEqualTo(expected);
        assertThat(simpleQueryForList(database, sqlUnicode)).isEqualTo(expected);
    }

    @Test
    public void nativeOpen_addsConnectionToPool() {
        assertThat(conn).isNotNull();
        assertThat(conn.isOpen()).named("open").isTrue();
    }

    @Test
    public void nativeClose_closesConnection() {
        ShadowSQLiteConnection.nativeClose(ptr);
        assertThat(conn.isOpen()).named("open").isFalse();
    }

    @Test
    public void reset_closesConnection() {
        ShadowSQLiteConnection.reset();
        assertThat(conn.isOpen()).named("open").isFalse();
    }

    @Test
    public void reset_clearsConnectionCache() {
        final Map<Long, SQLiteConnection> connectionsMap = ReflectionHelpers.getField(CONNECTIONS, "connectionsMap");
        assertThat(connectionsMap).named("connections before").isNotEmpty();
        ShadowSQLiteConnection.reset();
        assertThat(connectionsMap).named("connections after").isEmpty();
    }

    @Test
    public void reset_clearsStatementCache() {
        final Map<Long, SQLiteStatement> statementsMap = ReflectionHelpers.getField(CONNECTIONS, "statementsMap");
        assertThat(statementsMap).named("statements before").isNotEmpty();
        ShadowSQLiteConnection.reset();
        assertThat(statementsMap).named("statements after").isEmpty();
    }

    @Test
    public void error_resultsInSpecificExceptionWithCause() {
        try {
            database.execSQL("insert into routine(name) values ('Hand press 1')");
            ContentValues values = new ContentValues(1);
            values.put("rowid", "foo");
            database.update("routine", values, "name='Hand press 1'", null);
            Assert.fail();
        } catch (SQLiteDatatypeMismatchException expected) {
            assertThat(expected).hasCauseThat().hasCauseThat().isInstanceOf(SQLiteException.class);
        }
    }

    @Test
    public void interruption_doesNotConcurrentlyModifyDatabase() throws Exception {
        Thread.currentThread().interrupt();
        try {
            database.execSQL("insert into routine(name) values ('??????? ???????')");
        } finally {
            Thread.interrupted();
        }
        ShadowSQLiteConnection.reset();
    }

    @Test
    public void test_setUseInMemoryDatabase() throws Exception {
        assertThat(conn.isMemoryDatabase()).isFalse();
        ShadowSQLiteConnection.setUseInMemoryDatabase(true);
        SQLiteDatabase inMemoryDb = createDatabase("in_memory.db");
        SQLiteConnection inMemoryConn = getSQLiteConnection(inMemoryDb);
        assertThat(inMemoryConn.isMemoryDatabase()).isTrue();
        inMemoryDb.close();
    }
}

