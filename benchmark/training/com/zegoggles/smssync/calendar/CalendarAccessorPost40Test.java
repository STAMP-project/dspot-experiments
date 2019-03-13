package com.zegoggles.smssync.calendar;


import CalendarContract.Calendars;
import CalendarContract.Calendars.CONTENT_URI;
import CalendarContract.Events.ACCESS_DEFAULT;
import CalendarContract.Events.STATUS_CONFIRMED;
import Events.ACCESS_LEVEL;
import Events.CALENDAR_ID;
import Events.DESCRIPTION;
import Events.DTEND;
import Events.DTSTART;
import Events.EVENT_TIMEZONE;
import Events.STATUS;
import Events.TITLE;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import java.util.Date;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
public class CalendarAccessorPost40Test {
    CalendarAccessor accessor;

    @Mock
    ContentResolver resolver;

    @Test
    public void shouldEnableSync() throws Exception {
        Mockito.when(resolver.update(ArgumentMatchers.eq(Uri.parse("content://com.android.calendar/calendars/123")), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class))).thenReturn(1);
        assertThat(accessor.enableSync(123)).isTrue();
    }

    @Test
    @Config
    public void shouldAddEntry() throws Exception {
        ArgumentCaptor<Uri> uri = ArgumentCaptor.forClass(Uri.class);
        ArgumentCaptor<ContentValues> values = ArgumentCaptor.forClass(ContentValues.class);
        Date when = new Date();
        accessor.addEntry(12, when, 100, "Title", "Desc");
        Mockito.verify(resolver).insert(uri.capture(), values.capture());
        assertThat(uri.getValue().toString()).isEqualTo("content://com.android.calendar/events");
        ContentValues cv = values.getValue();
        assertThat(cv.getAsString(TITLE)).isEqualTo("Title");
        assertThat(cv.getAsString(DESCRIPTION)).isEqualTo("Desc");
        assertThat(cv.getAsLong(DTSTART)).isEqualTo(when.getTime());
        assertThat(cv.getAsLong(DTEND)).isGreaterThan(when.getTime());
        assertThat(cv.getAsInteger(ACCESS_LEVEL)).isEqualTo(ACCESS_DEFAULT);
        assertThat(cv.getAsInteger(STATUS)).isEqualTo(STATUS_CONFIRMED);
        assertThat(cv.getAsLong(CALENDAR_ID)).isEqualTo(12L);
        assertThat(cv.getAsString(EVENT_TIMEZONE)).isEqualTo(Time.getCurrentTimezone());
    }

    @Test
    public void shouldGetCalendars() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ "_id", "name", "sync_events" });
        cursor.addRow(new Object[]{ "12", "Testing", 1 });
        Mockito.when(resolver.query(ArgumentMatchers.eq(CONTENT_URI), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.eq(((Calendars.NAME) + " ASC")))).thenReturn(cursor);
        Map<String, String> calendars = accessor.getCalendars();
        assertThat(calendars).hasSize(1);
        assertThat(calendars).containsEntry("12", "Testing");
    }

    @Test
    public void shouldIgnoreSQLiteException() {
        Mockito.when(resolver.insert(ArgumentMatchers.eq(Events.CONTENT_URI), ArgumentMatchers.any(ContentValues.class))).thenThrow(SQLiteException.class);
        boolean result = accessor.addEntry(12, new Date(), 100, "Title", "Desc");
        assertThat(result).isFalse();
    }
}

