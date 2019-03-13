package org.robolectric.shadows;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SQLiteQueryBuilderTest {
    private static final String TABLE_NAME = "sqlBuilderTest";

    private static final String COL_VALUE = "valueCol";

    private static final String COL_GROUP = "groupCol";

    private SQLiteDatabase database;

    private SQLiteQueryBuilder builder;

    private long firstRecordId;

    @Test
    public void shouldBeAbleToMakeQueries() {
        Cursor cursor = builder.query(database, new String[]{ "rowid" }, null, null, null, null, null);
        assertThat(cursor.getCount()).isEqualTo(2);
    }

    @Test
    public void shouldBeAbleToMakeQueriesWithSelection() {
        Cursor cursor = builder.query(database, new String[]{ "rowid" }, ((SQLiteQueryBuilderTest.COL_VALUE) + "=?"), new String[]{ "record1" }, null, null, null);
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getLong(0)).isEqualTo(firstRecordId);
    }

    @Test
    public void shouldBeAbleToMakeQueriesWithGrouping() {
        Cursor cursor = builder.query(database, new String[]{ "rowid" }, null, null, SQLiteQueryBuilderTest.COL_GROUP, null, null);
        assertThat(cursor.getCount()).isEqualTo(1);
    }
}

