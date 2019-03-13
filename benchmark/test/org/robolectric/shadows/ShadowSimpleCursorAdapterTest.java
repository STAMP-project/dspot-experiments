package org.robolectric.shadows;


import android.app.Application;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowSimpleCursorAdapterTest {
    private Application context;

    @Test
    public void testChangeCursor() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, 1, null, new String[]{ "name" }, new int[]{ 2 }, 0);
        Cursor cursor = setUpDatabase();
        adapter.changeCursor(cursor);
        assertThat(adapter.getCursor()).isSameAs(cursor);
    }

    @Test
    public void testSwapCursor() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, 1, null, new String[]{ "name" }, new int[]{ 2 }, 0);
        Cursor cursor = setUpDatabase();
        adapter.swapCursor(cursor);
        assertThat(adapter.getCursor()).isSameAs(cursor);
    }

    @Test
    public void testSwapCursorToNull() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, 1, null, new String[]{ "name" }, new int[]{ 2 }, 0);
        Cursor cursor = setUpDatabase();
        adapter.swapCursor(cursor);
        adapter.swapCursor(null);
        assertThat(adapter.getCursor()).isNull();
    }
}

