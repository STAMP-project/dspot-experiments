package com.zegoggles.smssync.service;


import com.fsck.k9.mail.internet.MimeMessage;
import com.zegoggles.smssync.calendar.CalendarAccessor;
import com.zegoggles.smssync.mail.CallFormatter;
import com.zegoggles.smssync.mail.ConversionResult;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.mail.PersonLookup;
import com.zegoggles.smssync.mail.PersonRecord;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class CalendarSyncerTest {
    CalendarSyncer syncer;

    @Mock
    CalendarAccessor accessor;

    @Mock
    PersonLookup personLookup;

    @Mock
    CallFormatter callFormatter;

    static final long CALENDAR_ID = 123;

    @Test
    public void shouldSyncCalendar() throws Exception {
        ConversionResult result = new ConversionResult(DataType.CALLLOG);
        final String NUMBER = "12345";
        final String NAME = "Foo";
        final int DURATION = 10;
        final int TYPE = 1;
        Date callTime = new Date();
        result.add(new MimeMessage(), message(DURATION, TYPE, NUMBER, callTime));
        result.add(new MimeMessage(), message(DURATION, TYPE, NUMBER, callTime));
        Mockito.when(callFormatter.callTypeString(TYPE, NAME)).thenReturn("title1");
        Mockito.when(callFormatter.formatForCalendar(TYPE, NUMBER, DURATION)).thenReturn("title2");
        Mockito.when(personLookup.lookupPerson(NUMBER)).thenReturn(new PersonRecord(1, NAME, "foo@bar", NUMBER));
        syncer.syncCalendar(result);
        Mockito.verify(accessor, Mockito.times(2)).addEntry(ArgumentMatchers.eq(CalendarSyncerTest.CALENDAR_ID), ArgumentMatchers.eq(callTime), ArgumentMatchers.eq(DURATION), ArgumentMatchers.eq("title1"), ArgumentMatchers.eq("title2"));
    }

    @Test
    public void shouldEnableSync() throws Exception {
        shouldSyncCalendar();
        Mockito.verify(accessor).enableSync(CalendarSyncerTest.CALENDAR_ID);
    }

    @Test
    public void shouldOnlyEnableSyncOnce() throws Exception {
        shouldSyncCalendar();
        Mockito.reset(accessor);
        shouldSyncCalendar();
        Mockito.verify(accessor, Mockito.never()).enableSync(CalendarSyncerTest.CALENDAR_ID);
    }
}

