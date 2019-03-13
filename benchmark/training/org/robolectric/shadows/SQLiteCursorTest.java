package org.robolectric.shadows;


import Cursor.FIELD_TYPE_BLOB;
import Cursor.FIELD_TYPE_FLOAT;
import Cursor.FIELD_TYPE_INTEGER;
import Cursor.FIELD_TYPE_NULL;
import Cursor.FIELD_TYPE_STRING;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SQLiteCursorTest {
    private SQLiteDatabase database;

    private Cursor cursor;

    @Test
    public void testGetColumnNames() throws Exception {
        String[] columnNames = cursor.getColumnNames();
        assertColumnNames(columnNames);
    }

    @Test
    public void testGetColumnNamesEmpty() throws Exception {
        setupEmptyResult();
        String[] columnNames = cursor.getColumnNames();
        // Column names are present even with an empty result.
        assertThat(columnNames).isNotNull();
        assertColumnNames(columnNames);
    }

    @Test
    public void testGetColumnIndex() throws Exception {
        assertThat(cursor.getColumnIndex("id")).isEqualTo(0);
        assertThat(cursor.getColumnIndex("name")).isEqualTo(1);
    }

    @Test
    public void testGetColumnIndexNotFound() throws Exception {
        assertThat(cursor.getColumnIndex("Fred")).isEqualTo((-1));
    }

    @Test
    public void testGetColumnIndexEmpty() throws Exception {
        setupEmptyResult();
        assertThat(cursor.getColumnIndex("id")).isEqualTo(0);
        assertThat(cursor.getColumnIndex("name")).isEqualTo(1);
    }

    @Test
    public void testGetColumnIndexOrThrow() throws Exception {
        assertThat(cursor.getColumnIndexOrThrow("id")).isEqualTo(0);
        assertThat(cursor.getColumnIndexOrThrow("name")).isEqualTo(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnIndexOrThrowNotFound() throws Exception {
        cursor.getColumnIndexOrThrow("Fred");
    }

    @Test
    public void testGetColumnIndexOrThrowEmpty() throws Exception {
        setupEmptyResult();
        assertThat(cursor.getColumnIndexOrThrow("name")).isEqualTo(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnIndexOrThrowNotFoundEmpty() throws Exception {
        setupEmptyResult();
        cursor.getColumnIndexOrThrow("Fred");
    }

    @Test
    public void testMoveToFirst() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(0)).isEqualTo(1234);
        assertThat(cursor.getString(1)).isEqualTo("Chuck");
    }

    @Test
    public void testMoveToFirstEmpty() throws Exception {
        setupEmptyResult();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void testMoveToNext() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getInt(0)).isEqualTo(1235);
        assertThat(cursor.getString(1)).isEqualTo("Julie");
    }

    @Test
    public void testMoveToNextPastEnd() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.moveToNext()).isFalse();
    }

    @Test
    public void testMoveBackwards() throws Exception {
        assertThat(cursor.getPosition()).isEqualTo((-1));
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(0);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(1);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(2);
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(0);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(1);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(2);
        assertThat(cursor.moveToPosition(1)).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(1);
    }

    @Test
    public void testMoveToNextEmpty() throws Exception {
        setupEmptyResult();
        assertThat(cursor.moveToFirst()).isFalse();
        assertThat(cursor.moveToNext()).isFalse();
    }

    @Test
    public void testMoveToPrevious() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.moveToPrevious()).isTrue();
        assertThat(cursor.getInt(0)).isEqualTo(1234);
        assertThat(cursor.getString(1)).isEqualTo("Chuck");
    }

    @Test
    public void testMoveToPreviousPastStart() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        // Impossible to move cursor before the first item
        assertThat(cursor.moveToPrevious()).isFalse();
    }

    @Test
    public void testMoveToPreviousEmpty() throws Exception {
        setupEmptyResult();
        assertThat(cursor.moveToFirst()).isFalse();
        assertThat(cursor.moveToPrevious()).isFalse();
    }

    @Test
    public void testGetPosition() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(0);
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.getPosition()).isEqualTo(1);
    }

    @Test
    public void testGetBlob() throws Exception {
        String sql = "UPDATE table_name set blob_value=? where id=1234";
        byte[] byteData = sql.getBytes(StandardCharsets.UTF_8);
        database.execSQL(sql, new Object[]{ byteData });
        assertThat(cursor.moveToFirst()).isTrue();
        byte[] retrievedByteData = cursor.getBlob(5);
        assertThat(byteData.length).isEqualTo(retrievedByteData.length);
        for (int i = 0; i < (byteData.length); i++) {
            assertThat(byteData[i]).isEqualTo(retrievedByteData[i]);
        }
    }

    @Test
    public void testGetClob() throws Exception {
        String sql = "UPDATE table_name set clob_value=? where id=1234";
        String s = "Don't CLOBber my data, please. Thank you.";
        database.execSQL(sql, new Object[]{ s });
        assertThat(cursor.moveToFirst()).isTrue();
        String actual = cursor.getString(6);
        assertThat(s).isEqualTo(actual);
    }

    @Test
    public void testGetString() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        String[] data = new String[]{ "Chuck", "Julie", "Chris" };
        for (String aData : data) {
            assertThat(cursor.getString(1)).isEqualTo(aData);
            cursor.moveToNext();
        }
    }

    @Test
    public void testGetStringWhenInteger() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getString(0)).isEqualTo("1234");
    }

    @Test
    public void testGetStringWhenLong() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getString(2)).isEqualTo("3463");
    }

    @Test
    public void testGetStringWhenFloat() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getString(3)).isEqualTo("1.5");
    }

    @Test
    public void testGetStringWhenDouble() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getString(4)).isEqualTo("3.14159");
    }

    @Test(expected = SQLiteException.class)
    public void testGetStringWhenBlob() throws Exception {
        String sql = "UPDATE table_name set blob_value=? where id=1234";
        byte[] byteData = sql.getBytes(StandardCharsets.UTF_8);
        database.execSQL(sql, new Object[]{ byteData });
        assertThat(cursor.moveToFirst()).isTrue();
        cursor.getString(5);
    }

    @Test(expected = SQLiteException.class)
    public void testGetIntWhenBlob() throws Exception {
        String sql = "UPDATE table_name set blob_value=? where id=1234";
        byte[] byteData = sql.getBytes(StandardCharsets.UTF_8);
        database.execSQL(sql, new Object[]{ byteData });
        assertThat(cursor.moveToFirst()).isTrue();
        cursor.getInt(5);
    }

    @Test
    public void testGetStringWhenNull() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getString(5)).isNull();
    }

    @Test
    public void testGetInt() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        int[] data = new int[]{ 1234, 1235, 1236 };
        for (int aData : data) {
            assertThat(cursor.getInt(0)).isEqualTo(aData);
            cursor.moveToNext();
        }
    }

    @Test
    public void testGetNumbersFromStringField() throws Exception {
        database.execSQL("update table_name set name = '1.2'");
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(1)).isEqualTo(1);
        assertThat(cursor.getDouble(1)).isEqualTo(1.2);
        assertThat(cursor.getFloat(1)).isEqualTo(1.2F);
    }

    @Test
    public void testGetNumbersFromBlobField() throws Exception {
        database.execSQL("update table_name set name = '1.2'");
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(1)).isEqualTo(1);
        assertThat(cursor.getDouble(1)).isEqualTo(1.2);
        assertThat(cursor.getFloat(1)).isEqualTo(1.2F);
    }

    @Test
    public void testGetLong() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getLong(2)).isEqualTo(3463L);
    }

    @Test
    public void testGetFloat() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getFloat(3)).isEqualTo(((float) (1.5)));
    }

    @Test
    public void testGetDouble() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getDouble(4)).isEqualTo(3.14159);
    }

    @Test
    public void testClose() throws Exception {
        assertThat(cursor.isClosed()).isFalse();
        cursor.close();
        assertThat(cursor.isClosed()).isTrue();
    }

    @Test
    public void testIsNullWhenNull() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.moveToNext()).isTrue();
        assertThat(cursor.isNull(cursor.getColumnIndex("id"))).isFalse();
        assertThat(cursor.isNull(cursor.getColumnIndex("name"))).isFalse();
        assertThat(cursor.isNull(cursor.getColumnIndex("long_value"))).isTrue();
        assertThat(cursor.isNull(cursor.getColumnIndex("float_value"))).isTrue();
        assertThat(cursor.isNull(cursor.getColumnIndex("double_value"))).isTrue();
    }

    @Test
    public void testIsNullWhenNotNull() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        for (int i = 0; i < 5; i++) {
            assertThat(cursor.isNull(i)).isFalse();
        }
    }

    @Test
    public void testIsNullWhenIndexOutOfBounds() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        // column index 5 is out-of-bounds
        assertThat(cursor.isNull(5)).isTrue();
    }

    @Test
    public void testGetTypeWhenInteger() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(0)).isEqualTo(FIELD_TYPE_INTEGER);
    }

    @Test
    public void testGetTypeWhenString() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(1)).isEqualTo(FIELD_TYPE_STRING);
    }

    @Test
    public void testGetTypeWhenLong() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(2)).isEqualTo(FIELD_TYPE_INTEGER);
    }

    @Test
    public void testGetTypeWhenFloat() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(3)).isEqualTo(FIELD_TYPE_FLOAT);
    }

    @Test
    public void testGetTypeWhenDouble() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(4)).isEqualTo(FIELD_TYPE_FLOAT);
    }

    @Test
    public void testGetTypeWhenBlob() throws Exception {
        String sql = "UPDATE table_name set blob_value=? where id=1234";
        byte[] byteData = sql.getBytes(StandardCharsets.UTF_8);
        database.execSQL(sql, new Object[]{ byteData });
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(5)).isEqualTo(FIELD_TYPE_BLOB);
    }

    @Test
    public void testGetTypeWhenNull() throws Exception {
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(5)).isEqualTo(FIELD_TYPE_NULL);
    }

    @Test
    public void testGetNullNumberValues() throws Exception {
        String sql = "UPDATE table_name set long_value=NULL, float_value=NULL, double_value=NULL";
        database.execSQL(sql);
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getType(2)).isEqualTo(FIELD_TYPE_NULL);
        assertThat(cursor.getLong(2)).isEqualTo(0);
        assertThat(cursor.getType(3)).isEqualTo(FIELD_TYPE_NULL);
        assertThat(cursor.getFloat(3)).isEqualTo(0.0F);
        assertThat(cursor.getType(4)).isEqualTo(FIELD_TYPE_NULL);
        assertThat(cursor.getDouble(4)).isEqualTo(0.0);
    }
}

