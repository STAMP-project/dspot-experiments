package org.robolectric.shadows;


import android.database.AbstractCursor;
import android.net.Uri;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowAbstractCursorTest {
    private ShadowAbstractCursorTest.TestCursor cursor;

    @Test
    public void testMoveToFirst() {
        cursor.theTable.add("Foobar");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(1);
    }

    @Test
    public void testMoveToFirstEmptyList() {
        assertThat(moveToFirst()).isFalse();
        assertThat(cursor.getCount()).isEqualTo(0);
    }

    @Test
    public void testMoveToLast() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToLast()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(2);
    }

    @Test
    public void testMoveToLastEmptyList() {
        assertThat(moveToLast()).isFalse();
        assertThat(cursor.getCount()).isEqualTo(0);
    }

    @Test
    public void testGetPosition() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(2);
        assertThat(getPosition()).isEqualTo(0);
    }

    @Test
    public void testGetPositionSingleEntry() {
        cursor.theTable.add("Foobar");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(getPosition()).isEqualTo(0);
    }

    @Test
    public void testGetPositionEmptyList() {
        assertThat(moveToFirst()).isFalse();
        assertThat(cursor.getCount()).isEqualTo(0);
        assertThat(getPosition()).isEqualTo(0);
    }

    @Test
    public void testMoveToNext() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(2);
        assertThat(moveToNext()).isTrue();
        assertThat(getPosition()).isEqualTo(1);
    }

    @Test
    public void testAttemptToMovePastEnd() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(2);
        assertThat(moveToNext()).isTrue();
        assertThat(getPosition()).isEqualTo(1);
        assertThat(isLast()).isTrue();
        assertThat(moveToNext()).isFalse();
        assertThat(isAfterLast()).isTrue();
        assertThat(getPosition()).isEqualTo(2);
    }

    @Test
    public void testAttemptToMovePastSingleEntry() {
        cursor.theTable.add("Foobar");
        assertThat(moveToFirst()).isTrue();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(moveToNext()).isFalse();
        assertThat(getPosition()).isEqualTo(1);
    }

    @Test
    public void testAttemptToMovePastEmptyList() {
        assertThat(moveToFirst()).isFalse();
        assertThat(cursor.getCount()).isEqualTo(0);
        assertThat(moveToNext()).isFalse();
        assertThat(getPosition()).isEqualTo(0);
    }

    @Test
    public void testMoveToPrevious() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(moveToNext()).isTrue();
        assertThat(getPosition()).isEqualTo(1);
        assertThat(moveToPrevious()).isTrue();
        assertThat(getPosition()).isEqualTo(0);
    }

    @Test
    public void testAttemptToMovePastStart() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(moveToPrevious()).isFalse();
        assertThat(getPosition()).isEqualTo((-1));
    }

    @Test
    public void testIsFirst() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        assertThat(isFirst()).isTrue();
        moveToNext();
        assertThat(isFirst()).isFalse();
        moveToFirst();
        moveToPrevious();
        assertThat(isFirst()).isFalse();
    }

    @Test
    public void testIsLast() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        moveToNext();
        assertThat(isLast()).isTrue();
        moveToPrevious();
        assertThat(isLast()).isFalse();
        moveToFirst();
        moveToNext();
        assertThat(isLast()).isTrue();
    }

    @Test
    public void testIsBeforeFirst() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        moveToNext();
        assertThat(isLast()).isTrue();
        moveToPrevious();
        assertThat(isLast()).isFalse();
        moveToPrevious();
        assertThat(isFirst()).isFalse();
        moveToPrevious();
        assertThat(isBeforeFirst()).isTrue();
    }

    @Test
    public void testIsAfterLast() {
        cursor.theTable.add("Foobar");
        cursor.theTable.add("Bletch");
        assertThat(moveToFirst()).isTrue();
        moveToNext();
        assertThat(isLast()).isTrue();
        moveToNext();
        assertThat(isAfterLast()).isTrue();
        moveToPrevious();
        assertThat(isLast()).isTrue();
        moveToPrevious();
        assertThat(isLast()).isFalse();
        moveToFirst();
        moveToNext();
        assertThat(isAfterLast()).isFalse();
        moveToNext();
        assertThat(isAfterLast()).isTrue();
    }

    @Test
    public void testGetNotificationUri() {
        Uri uri = Uri.parse("content://foo.com");
        ShadowAbstractCursor shadow = Shadows.shadowOf(cursor);
        assertThat(shadow.getNotificationUri_Compatibility()).isNull();
        cursor.setNotificationUri(ApplicationProvider.getApplicationContext().getContentResolver(), uri);
        assertThat(shadow.getNotificationUri_Compatibility()).isEqualTo(uri);
    }

    @Test
    public void testIsClosedWhenAfterCallingClose() {
        assertThat(isClosed()).isFalse();
        close();
        assertThat(isClosed()).isTrue();
    }

    private static class TestCursor extends AbstractCursor {
        public List<Object> theTable = new ArrayList<>();

        @Override
        public int getCount() {
            return theTable.size();
        }

        @Override
        public String[] getColumnNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getString(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNull(int columnIndex) {
            throw new UnsupportedOperationException();
        }
    }
}

