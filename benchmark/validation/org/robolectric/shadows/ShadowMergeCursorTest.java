package org.robolectric.shadows;


import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowMergeCursorTest {
    private SQLiteDatabase database;

    private MergeCursor cursor;

    private SQLiteCursor dbCursor1;

    private SQLiteCursor dbCursor2;

    private static String[] TABLE_1_INSERTS = new String[]{ "INSERT INTO table_1 (id, name_1, value_1, float_value_1, double_value_1) VALUES(1234, 'Chuck', 3463, 1.5, 3.14159);", "INSERT INTO table_1 (id, name_1) VALUES(1235, 'Julie');", "INSERT INTO table_1 (id, name_1) VALUES(1236, 'Chris');" };

    private static String[] TABLE_2_INSERTS = new String[]{ "INSERT INTO table_2 (id, name_2, value_2, float_value_2, double_value_2) VALUES(4321, 'Mary', 3245, 5.4, 2.7818);", "INSERT INTO table_2 (id, name_2) VALUES(4322, 'Elizabeth');", "INSERT INTO table_2 (id, name_2) VALUES(4323, 'Chester');" };

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfConstructorArgumentIsNull() {
        new MergeCursor(null);
    }

    @Test
    public void testEmptyCursors() throws Exception {
        // cursor list with null contents
        cursor = new MergeCursor(new Cursor[1]);
        assertThat(cursor.getCount()).isEqualTo(0);
        assertThat(cursor.moveToFirst()).isFalse();
        assertThat(cursor.getColumnNames()).isNotNull();
        // cursor list with partially null contents
        Cursor[] cursors = new Cursor[2];
        cursors[0] = null;
        cursors[1] = dbCursor1;
        cursor = new MergeCursor(cursors);
        assertThat(cursor.getCount()).isEqualTo(ShadowMergeCursorTest.TABLE_1_INSERTS.length);
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getColumnNames()).isNotNull();
    }

    @Test
    public void testMoveToPositionEmptyCursor() throws Exception {
        Cursor[] cursors = new Cursor[2];
        cursors[0] = null;
        cursors[1] = null;
        cursor = new MergeCursor(cursors);
        assertThat(cursor.getCount()).isEqualTo(0);
        assertThat(cursor.getColumnNames()).isNotNull();
        cursor.moveToPosition(0);
        assertThat(cursor.getColumnNames()).isNotNull();
    }

    @Test
    public void testBoundsSingleCursor() throws Exception {
        Cursor[] cursors = new Cursor[1];
        cursors[0] = dbCursor1;
        assertBounds(cursors, ShadowMergeCursorTest.TABLE_1_INSERTS.length);
    }

    @Test
    public void testBoundsMultipleCursor() throws Exception {
        Cursor[] cursors = new Cursor[2];
        cursors[0] = dbCursor1;
        cursors[1] = dbCursor2;
        assertBounds(cursors, ((ShadowMergeCursorTest.TABLE_1_INSERTS.length) + (ShadowMergeCursorTest.TABLE_2_INSERTS.length)));
    }

    @Test
    public void testGetDataSingleCursor() throws Exception {
        Cursor[] cursors = new Cursor[1];
        cursors[0] = dbCursor1;
        cursor = new MergeCursor(cursors);
        cursor.moveToFirst();
        assertDataCursor1();
    }

    @Test
    public void testGetDataMultipleCursor() throws Exception {
        Cursor[] cursors = new Cursor[2];
        cursors[0] = dbCursor1;
        cursors[1] = dbCursor2;
        cursor = new MergeCursor(cursors);
        cursor.moveToFirst();
        assertDataCursor1();
        cursor.moveToNext();
        assertDataCursor2();
    }

    @Test
    public void testColumnNamesSingleCursor() throws Exception {
        Cursor[] cursors = new Cursor[1];
        cursors[0] = dbCursor1;
        cursor = new MergeCursor(cursors);
        for (int i = 0; i < (ShadowMergeCursorTest.TABLE_1_INSERTS.length); i++) {
            cursor.moveToPosition(i);
            String[] columnNames = cursor.getColumnNames();
            assertColumnNamesCursor1(columnNames);
        }
    }

    @Test
    public void testColumnNamesMultipleCursors() throws Exception {
        Cursor[] cursors = new Cursor[2];
        cursors[0] = dbCursor1;
        cursors[1] = dbCursor2;
        cursor = new MergeCursor(cursors);
        for (int i = 0; i < (ShadowMergeCursorTest.TABLE_1_INSERTS.length); i++) {
            cursor.moveToPosition(i);
            String[] columnNames = cursor.getColumnNames();
            assertColumnNamesCursor1(columnNames);
        }
        for (int i = 0; i < (ShadowMergeCursorTest.TABLE_2_INSERTS.length); i++) {
            cursor.moveToPosition((i + (ShadowMergeCursorTest.TABLE_1_INSERTS.length)));
            String[] columnNames = cursor.getColumnNames();
            assertColumnNamesCursor2(columnNames);
        }
    }

    @Test
    public void testCloseCursors() throws Exception {
        Cursor[] cursors = new Cursor[2];
        cursors[0] = dbCursor1;
        cursors[1] = dbCursor2;
        cursor = new MergeCursor(cursors);
        assertThat(cursor.isClosed()).isFalse();
        assertThat(dbCursor1.isClosed()).isFalse();
        assertThat(dbCursor2.isClosed()).isFalse();
        cursor.close();
        assertThat(cursor.isClosed()).isTrue();
        assertThat(dbCursor1.isClosed()).isTrue();
        assertThat(dbCursor2.isClosed()).isTrue();
    }
}

