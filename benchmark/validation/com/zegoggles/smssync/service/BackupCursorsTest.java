package com.zegoggles.smssync.service;


import BackupCursors.CursorAndType;
import android.database.Cursor;
import com.zegoggles.smssync.mail.DataType;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BackupCursorsTest {
    BackupCursors cursors;

    @Test
    public void testEmptyCursor() {
        BackupCursors empty = new BackupCursors();
        assertThat(empty.count()).isEqualTo(0);
        assertThat(empty.hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyCursorShouldThrowNoSuchElementException() {
        BackupCursors empty = new BackupCursors();
        empty.next();
    }

    @Test
    public void shouldReportTotalCountOfAllCursors() throws Exception {
        assertThat(cursors.count()).isEqualTo(5);
    }

    @Test
    public void shouldReportCountForDataType() throws Exception {
        assertThat(cursors.count(DataType.SMS)).isEqualTo(1);
        assertThat(cursors.count(DataType.MMS)).isEqualTo(4);
        assertThat(cursors.count(DataType.CALLLOG)).isEqualTo(0);
        assertThat(cursors.count(null)).isEqualTo(0);
    }

    @Test
    public void shouldIterateOverAllContainedCursors() throws Exception {
        for (int i = 0; i < (cursors.count()); i++) {
            assertThat(cursors.hasNext()).isTrue();
            BackupCursors.CursorAndType cursorAndType = cursors.next();
            assertThat(cursorAndType).isNotNull();
            assertThat(cursorAndType.cursor).isNotNull();
            assertThat(cursorAndType.type).isNotNull();
        }
        assertThat(cursors.hasNext()).isFalse();
        try {
            cursors.next();
            Assert.fail("expected exception");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void shouldCloseAllCursors() throws Exception {
        BackupCursors cursors = new BackupCursors();
        Cursor mockedCursor1 = Mockito.mock(Cursor.class);
        Cursor mockedCursor2 = Mockito.mock(Cursor.class);
        cursors.add(DataType.SMS, mockedCursor1);
        cursors.add(DataType.MMS, mockedCursor2);
        cursors.close();
        Mockito.verify(mockedCursor1).close();
        Mockito.verify(mockedCursor2).close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportRemove() throws Exception {
        cursors.remove();
    }
}

