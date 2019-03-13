package com.vaadin.v7.tests.server.component.calendar;


import BackwardEvent.EVENT_ID;
import TimeFormat.Format12H;
import TimeFormat.Format24H;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.v7.ui.components.calendar.event.CalendarEventProvider;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SUNDAY;


/**
 * Basic API tests for the calendar
 */
public class CalendarBasicsTest {
    @Test
    public void testEmptyConstructorInitialization() {
        Calendar calendar = new Calendar();
        // The calendar should have a basic event provider with no events
        CalendarEventProvider provider = calendar.getEventProvider();
        Assert.assertNotNull("Event provider should not be null", provider);
        // Basic event handlers should be registered
        Assert.assertNotNull(calendar.getHandler(EVENT_ID));
        Assert.assertNotNull(calendar.getHandler(ForwardEvent.EVENT_ID));
        Assert.assertNotNull(calendar.getHandler(WeekClick.EVENT_ID));
        Assert.assertNotNull(calendar.getHandler(DateClickEvent.EVENT_ID));
        Assert.assertNotNull(calendar.getHandler(MoveEvent.EVENT_ID));
        Assert.assertNotNull(calendar.getHandler(EventResize.EVENT_ID));
        // Calendar should have undefined size
        Assert.assertTrue(((calendar.getWidth()) < 0));
        Assert.assertTrue(((calendar.getHeight()) < 0));
    }

    @Test
    public void testConstructorWithCaption() {
        final String caption = "My Calendar Caption";
        Calendar calendar = new Calendar(caption);
        Assert.assertEquals(caption, calendar.getCaption());
    }

    @Test
    public void testConstructorWithCustomEventProvider() {
        BasicEventProvider myProvider = new BasicEventProvider();
        Calendar calendar = new Calendar(myProvider);
        Assert.assertEquals(myProvider, calendar.getEventProvider());
    }

    @Test
    public void testConstructorWithCustomEventProviderAndCaption() {
        BasicEventProvider myProvider = new BasicEventProvider();
        final String caption = "My Calendar Caption";
        Calendar calendar = new Calendar(caption, myProvider);
        Assert.assertEquals(caption, calendar.getCaption());
        Assert.assertEquals(myProvider, calendar.getEventProvider());
    }

    @Test
    public void testDefaultStartAndEndDates() {
        Calendar calendar = new Calendar();
        // If no start and end date is set the calendar will display the current
        // week
        java.util.Calendar c = new GregorianCalendar();
        java.util.Calendar c2 = new GregorianCalendar();
        c2.setTime(calendar.getStartDate());
        Assert.assertEquals(c.getFirstDayOfWeek(), c2.get(DAY_OF_WEEK));
        c2.setTime(calendar.getEndDate());
        c.set(DAY_OF_WEEK, ((c.getFirstDayOfWeek()) + 6));
        Assert.assertEquals(c.get(DAY_OF_WEEK), c2.get(DAY_OF_WEEK));
    }

    @Test
    public void testCustomStartAndEndDates() {
        Calendar calendar = new Calendar();
        java.util.Calendar c = new GregorianCalendar();
        Date start = c.getTime();
        c.add(DATE, 3);
        Date end = c.getTime();
        calendar.setStartDate(start);
        calendar.setEndDate(end);
        Assert.assertEquals(start.getTime(), calendar.getStartDate().getTime());
        Assert.assertEquals(end.getTime(), calendar.getEndDate().getTime());
    }

    @Test
    public void testCustomLocale() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.CANADA_FRENCH);
        // Setting the locale should set the internal calendars locale
        Assert.assertEquals(Locale.CANADA_FRENCH, calendar.getLocale());
        java.util.Calendar c = new GregorianCalendar(Locale.CANADA_FRENCH);
        Assert.assertEquals(c.getTimeZone().getRawOffset(), calendar.getInternalCalendar().getTimeZone().getRawOffset());
    }

    @Test
    public void testTimeFormat() {
        Calendar calendar = new Calendar();
        // The default timeformat depends on the current locale
        calendar.setLocale(Locale.ENGLISH);
        Assert.assertEquals(Format12H, calendar.getTimeFormat());
        calendar.setLocale(Locale.ITALIAN);
        Assert.assertEquals(Format24H, calendar.getTimeFormat());
        // Setting a specific time format overrides the locale
        calendar.setTimeFormat(Format12H);
        Assert.assertEquals(Format12H, calendar.getTimeFormat());
    }

    @Test
    public void testTimeZone() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.CANADA_FRENCH);
        // By default the calendars timezone is returned
        Assert.assertEquals(calendar.getInternalCalendar().getTimeZone(), calendar.getTimeZone());
        // One can override the default behavior by specifying a timezone
        TimeZone customTimeZone = TimeZone.getTimeZone("Europe/Helsinki");
        calendar.setTimeZone(customTimeZone);
        Assert.assertEquals(customTimeZone, calendar.getTimeZone());
    }

    @Test
    public void testVisibleDaysOfWeek() {
        Calendar calendar = new Calendar();
        // The defaults are the whole week
        Assert.assertEquals(1, calendar.getFirstVisibleDayOfWeek());
        Assert.assertEquals(7, calendar.getLastVisibleDayOfWeek());
        calendar.setFirstVisibleDayOfWeek(0);// Invalid input

        Assert.assertEquals(1, calendar.getFirstVisibleDayOfWeek());
        calendar.setLastVisibleDayOfWeek(0);// Invalid input

        Assert.assertEquals(7, calendar.getLastVisibleDayOfWeek());
        calendar.setFirstVisibleDayOfWeek(8);// Invalid input

        Assert.assertEquals(1, calendar.getFirstVisibleDayOfWeek());
        calendar.setLastVisibleDayOfWeek(8);// Invalid input

        Assert.assertEquals(7, calendar.getLastVisibleDayOfWeek());
        calendar.setFirstVisibleDayOfWeek(4);
        Assert.assertEquals(4, calendar.getFirstVisibleDayOfWeek());
        calendar.setLastVisibleDayOfWeek(6);
        Assert.assertEquals(6, calendar.getLastVisibleDayOfWeek());
        calendar.setFirstVisibleDayOfWeek(7);// Invalid since last day is 6

        Assert.assertEquals(4, calendar.getFirstVisibleDayOfWeek());
        calendar.setLastVisibleDayOfWeek(2);// Invalid since first day is 4

        Assert.assertEquals(6, calendar.getLastVisibleDayOfWeek());
    }

    @Test
    public void testVisibleHoursInDay() {
        Calendar calendar = new Calendar();
        // Defaults are the whole day
        Assert.assertEquals(0, calendar.getFirstVisibleHourOfDay());
        Assert.assertEquals(23, calendar.getLastVisibleHourOfDay());
    }

    @Test
    public void isClientChangeAllowed_connectorEnabled() {
        CalendarBasicsTest.TestCalendar calendar = new CalendarBasicsTest.TestCalendar(true);
        Assert.assertTrue("Calendar with enabled connector doesn't allow client change", calendar.isClientChangeAllowed());
    }

    // regression test to ensure old functionality is not broken
    @Test
    public void defaultFirstDayOfWeek() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.GERMAN);
        // simulating consequences of markAsDirty
        calendar.beforeClientResponse(true);
        Assert.assertEquals(MONDAY, calendar.getInternalCalendar().getFirstDayOfWeek());
    }

    @Test
    public void customFirstDayOfWeek() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.GERMAN);
        calendar.setFirstDayOfWeek(SUNDAY);
        // simulating consequences of markAsDirty
        calendar.beforeClientResponse(true);
        Assert.assertEquals(SUNDAY, calendar.getInternalCalendar().getFirstDayOfWeek());
    }

    @Test
    public void customFirstDayOfWeekCanSetEvenBeforeLocale() {
        Calendar calendar = new Calendar();
        calendar.setFirstDayOfWeek(SUNDAY);
        calendar.setLocale(Locale.GERMAN);
        // simulating consequences of markAsDirty
        calendar.beforeClientResponse(true);
        Assert.assertEquals(SUNDAY, calendar.getInternalCalendar().getFirstDayOfWeek());
    }

    @Test
    public void customFirstDayOfWeekSetNullRestoresDefault() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.GERMAN);
        calendar.setFirstDayOfWeek(SUNDAY);
        calendar.setFirstDayOfWeek(null);
        // simulating consequences of markAsDirty
        calendar.beforeClientResponse(true);
        Assert.assertEquals(MONDAY, calendar.getInternalCalendar().getFirstDayOfWeek());
    }

    @Test(expected = IllegalArgumentException.class)
    public void customFirstDayOfWeekValidation() {
        Calendar calendar = new Calendar();
        int someWrongDayOfWeek = 10;
        calendar.setFirstDayOfWeek(someWrongDayOfWeek);
    }

    private static class TestCalendar extends Calendar {
        TestCalendar(boolean connectorEnabled) {
            isConnectorEnabled = connectorEnabled;
        }

        @Override
        public boolean isConnectorEnabled() {
            return isConnectorEnabled;
        }

        @Override
        public boolean isClientChangeAllowed() {
            return super.isClientChangeAllowed();
        }

        private final boolean isConnectorEnabled;
    }
}

