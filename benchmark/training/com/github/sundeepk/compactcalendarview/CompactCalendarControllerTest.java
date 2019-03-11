package com.github.sundeepk.compactcalendarview;


import MotionEvent.ACTION_UP;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.OverScroller;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CompactCalendarControllerTest {
    @Mock
    private Paint paint;

    @Mock
    private OverScroller overScroller;

    @Mock
    private Canvas canvas;

    @Mock
    private Rect rect;

    @Mock
    private Calendar calendar;

    @Mock
    private MotionEvent motionEvent;

    @Mock
    private VelocityTracker velocityTracker;

    @Mock
    private EventsContainer eventsContainer;

    private static final String[] dayColumnNames = new String[]{ "M", "T", "W", "T", "F", "S", "S" };

    CompactCalendarController underTest;

    @Test(expected = IllegalArgumentException.class)
    public void testItThrowsWhenDayColumnsIsNotLengthSeven() {
        String[] dayNames = new String[]{ "Mon", "Tue", "Wed", "Thur", "Fri" };
        underTest.setDayColumnNames(dayNames);
    }

    @Test
    public void testManualScrollAndGestureScrollPlayNicelyTogether() {
        // Set width of view so that scrolling will return a correct value
        underTest.onMeasure(720, 1080, 0, 0);
        Calendar cal = Calendar.getInstance();
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(setTimeToMidnightAndGet(cal, 1423353600000L)));
        underTest.scrollRight();
        // Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(new Date(setTimeToMidnightAndGet(cal, 1425168000000L)), underTest.getFirstDayOfCurrentMonth());
        Mockito.when(motionEvent.getAction()).thenReturn(ACTION_UP);
        // Scroll enough to push calender to next month
        underTest.onScroll(motionEvent, motionEvent, 600, 0);
        underTest.onDraw(canvas);
        underTest.onTouch(motionEvent);
        // Wed, 01 Apr 2015 00:00:00 GMT
        Assert.assertEquals(new Date(setTimeToMidnightAndGet(cal, 1427846400000L)), underTest.getFirstDayOfCurrentMonth());
    }

    @Test
    public void testItScrollsToNextMonth() {
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        underTest.scrollRight();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        // Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(new Date(1425168000000L), actualDate);
    }

    @Test
    public void testItScrollsToPreviousMonth() {
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        underTest.scrollLeft();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        // Thu, 01 Jan 2015 00:00:00 GMT - expected
        Assert.assertEquals(new Date(1420070400000L), actualDate);
    }

    @Test
    public void testItScrollsToNextMonthWhenRtl() {
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        underTest.setIsRtl(true);
        underTest.scrollRight();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        // Thu, 01 Jan 2015 00:00:00 GMT - expected
        Assert.assertEquals(new Date(1420070400000L), actualDate);
    }

    @Test
    public void testItScrollsToPreviousMonthWhenRtl() {
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        underTest.setIsRtl(true);
        underTest.scrollLeft();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        // Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(new Date(1425168000000L), actualDate);
    }

    @Test
    public void testItSetsDayColumns() {
        // simulate Feb month
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(1);
        Mockito.when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);
        String[] dayNames = new String[]{ "Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun" };
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.setDayColumnNames(dayNames);
        underTest.drawMonth(canvas, calendar, 0);
        InOrder inOrder = Mockito.inOrder(canvas);
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Mon"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Tue"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Wed"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Thur"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Fri"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Sat"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Sun"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testListenerIsCalledOnMonthScroll() {
        // Sun, 01 Mar 2015 00:00:00 GMT
        Date expectedDateOnScroll = new Date(1425168000000L);
        Mockito.when(motionEvent.getAction()).thenReturn(ACTION_UP);
        // Set width of view so that scrolling will return a correct value
        underTest.onMeasure(720, 1080, 0, 0);
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        // Scroll enough to push calender to next month
        underTest.onScroll(motionEvent, motionEvent, 600, 0);
        underTest.onDraw(canvas);
        underTest.onTouch(motionEvent);
        Assert.assertEquals(expectedDateOnScroll, underTest.getFirstDayOfCurrentMonth());
    }

    @Test
    public void testItAbbreviatesDayNames() {
        // simulate Feb month
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(1);
        Mockito.when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.setLocale(TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE);
        Mockito.reset(canvas);// reset because invalidate is called

        underTest.setUseWeekDayAbbreviation(true);
        Mockito.reset(canvas);// reset because invalidate is called

        underTest.drawMonth(canvas, calendar, 0);
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.FRANCE);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        InOrder inOrder = Mockito.inOrder(canvas);
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[2]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[3]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[4]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[5]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[6]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[7]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq(dayNames[1]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItReturnsFirstDayOfMonthAfterDateHasBeenSet() {
        // Sun, 01 Feb 2015 00:00:00 GMT
        Date expectedDate = new Date(1422748800000L);
        // Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testItReturnsFirstDayOfMonth() {
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        currentCalender.set(Calendar.MILLISECOND, 0);
        Date expectFirstDayOfMonth = currentCalender.getTime();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        Assert.assertEquals(expectFirstDayOfMonth, actualDate);
    }

    @Test
    public void testItDrawsSundayAsFirstDay() {
        // simulate Feb month
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(1);
        Mockito.when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.setUseWeekDayAbbreviation(true);
        underTest.setFirstDayOfWeek(Calendar.SUNDAY);
        underTest.drawMonth(canvas, calendar, 0);
        InOrder inOrder = Mockito.inOrder(canvas);
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Sun"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Mon"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Tue"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Wed"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Thu"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Fri"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("Sat"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsFirstLetterOfEachDay() {
        // simulate Feb month
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(1);
        Mockito.when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawMonth(canvas, calendar, 0);
        InOrder inOrder = Mockito.inOrder(canvas);
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("M"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("T"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("W"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("T"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("F"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("S"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        inOrder.verify(canvas).drawText(ArgumentMatchers.eq("S"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsDaysOnCalender() {
        // simulate Feb month
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(1);
        Mockito.when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);
        underTest.drawMonth(canvas, calendar, 0);
        for (int dayColumn = 0, dayRow = 0; dayColumn <= 6; dayRow++) {
            if (dayRow == 7) {
                dayRow = 0;
                if (dayColumn <= 6) {
                    dayColumn++;
                }
            }
            if (dayColumn == (CompactCalendarControllerTest.dayColumnNames.length)) {
                break;
            }
            if (dayColumn == 0) {
                Mockito.verify(canvas).drawText(ArgumentMatchers.eq(CompactCalendarControllerTest.dayColumnNames[dayColumn]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
            } else {
                int day = ((((dayRow - 1) * 7) + dayColumn) + 1) - 6;
                if ((day > 0) && (day <= 28)) {
                    Mockito.verify(canvas).drawText(ArgumentMatchers.eq(String.valueOf(day)), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
                }
            }
        }
    }

    @Test
    public void testItDrawsEventDaysOnCalendar() {
        // Sun, 07 Jun 2015 18:20:51 GMT
        // get 30 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = CompactCalendarHelper.getEvents(0, numberOfDaysWithEvents, 1433701251000L);
        Mockito.when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(5);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(2015);
        underTest.shouldDrawIndicatorsBelowSelectedDays(true);// always draw events, even on current day

        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawEvents(canvas, calendar, 0);
        // draw events for every day with an event
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents)).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendar() {
        // Sun, 07 Jun 2015 18:20:51 GMT
        // get 60 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = CompactCalendarHelper.getDayEventWith2EventsPerDay(0, numberOfDaysWithEvents, 1433701251000L);
        Mockito.when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(5);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(2015);
        underTest.shouldDrawIndicatorsBelowSelectedDays(true);// always draw events, even on current day

        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawEvents(canvas, calendar, 0);
        // draw 2 events per day
        Mockito.verify(canvas, Mockito.times((numberOfDaysWithEvents * 2))).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendarWithPlusIndicator() {
        // Sun, 07 Jun 2015 18:20:51 GMT
        // get 120 events in total but only draw 3 event indicators per a day
        int numberOfDaysWithEvents = 30;
        List<Events> events = CompactCalendarHelper.getDayEventWithMultipleEventsPerDay(0, numberOfDaysWithEvents, 1433701251000L);
        Mockito.when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(5);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(2015);
        underTest.shouldDrawIndicatorsBelowSelectedDays(true);// always draw events, even on current day

        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawEvents(canvas, calendar, 0);
        // draw 2 events per day because we don't draw more than 3 indicators
        Mockito.verify(canvas, Mockito.times((numberOfDaysWithEvents * 2))).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
        // draw event indicator with lines
        // 2 calls for each plus event indicator since it takes 2 draw calls to make a plus sign
        Mockito.verify(canvas, Mockito.times((numberOfDaysWithEvents * 2))).drawLine(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarForCurrentMonth() {
        Calendar todayCalendar = Calendar.getInstance();
        int numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayYear = todayCalendar.get(Calendar.YEAR);
        // get events for every day in the month
        List<Events> events = CompactCalendarHelper.getEvents(0, numberOfDaysInMonth, todayCalendar.getTimeInMillis());
        Mockito.when(eventsContainer.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(todayMonth);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(todayYear);
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawEvents(canvas, calendar, 0);
        // draw events for every day except the current day -- selected day is also the current day
        Mockito.verify(canvas, Mockito.times((numberOfDaysInMonth - 1))).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarWithSelectedDay() {
        // Sun, 07 Jun 2015 18:20:51 GMT
        long selectedDayTimestamp = 1433701251000L;
        // get 30 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = CompactCalendarHelper.getEvents(0, numberOfDaysWithEvents, selectedDayTimestamp);
        Mockito.when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(5);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(2015);
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        // Selects first day of the month
        underTest.setCurrentDate(new Date(selectedDayTimestamp));
        underTest.drawEvents(canvas, calendar, 0);
        // draw events for every day except the selected day
        Mockito.verify(canvas, Mockito.times((numberOfDaysWithEvents - 1))).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarForCurrentMonthWithSelectedDay() {
        Calendar todayCalendar = Calendar.getInstance();
        int numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayYear = todayCalendar.get(Calendar.YEAR);
        // get events for every day in the month
        List<Events> events = CompactCalendarHelper.getEvents(0, numberOfDaysInMonth, todayCalendar.getTimeInMillis());
        Mockito.when(eventsContainer.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(todayMonth);
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(todayYear);
        // sets either 1st day or 2nd day so that there are always 2 days selected
        int dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth == 1) {
            todayCalendar.set(Calendar.DAY_OF_MONTH, 2);
        } else {
            todayCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        underTest.setCurrentDate(todayCalendar.getTime());
        underTest.setGrowProgress(1000);// set grow progress so that it simulates the calendar being open

        underTest.drawEvents(canvas, calendar, 0);
        // draw events for every day except the current day and the selected day
        Mockito.verify(canvas, Mockito.times((numberOfDaysInMonth - 2))).drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testItAddsEvent() {
        Event event = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L).get(0);
        underTest.addEvent(event);
        Mockito.verify(eventsContainer).addEvent(event);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItAddsEvents() {
        List<Event> events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L);
        underTest.addEvents(events);
        Mockito.verify(eventsContainer).addEvents(events);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesEvent() {
        Event event = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L).get(0);
        underTest.removeEvent(event);
        Mockito.verify(eventsContainer).removeEvent(event);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesEvents() {
        List<Event> events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L);
        underTest.removeEvents(events);
        Mockito.verify(eventsContainer).removeEvents(events);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItGetCalendarEventsForADate() {
        underTest.getCalendarEventsFor(1433701251000L);
        Mockito.verify(eventsContainer).getEventsFor(1433701251000L);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesCalendarEventsForADate() {
        underTest.removeEventsFor(1433701251000L);
        Mockito.verify(eventsContainer).removeEventByEpochMillis(1433701251000L);
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesAllEvents() {
        underTest.removeAllEvents();
        Mockito.verify(eventsContainer).removeAllEvents();
        Mockito.verifyNoMoreInteractions(eventsContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testItThrowsWhenZeroIsUsedAsFirstDayOfWeek() {
        underTest.setFirstDayOfWeek(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testItThrowsWhenValuesGreaterThanSevenIsUsedAsFirstDayOfWeek() {
        underTest.setFirstDayOfWeek(8);
    }

    @Test
    public void testItGetsDayOfWeekWhenSundayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Sunday as first day means Saturday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 0, 1, 2, 3, 4, 5, 6 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.SUNDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenMondayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Monday as first day means Sunday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 6, 0, 1, 2, 3, 4, 5 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.MONDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenTuesdayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Tuesday as first day means Monday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 5, 6, 0, 1, 2, 3, 4 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.TUESDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenWednesdayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Wednesday as first day means Tuesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 4, 5, 6, 0, 1, 2, 3 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.WEDNESDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenThursdayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Thursday as first day means Wednesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 3, 4, 5, 6, 0, 1, 2 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.THURSDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenFridayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Friday as first day means Wednesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 2, 3, 4, 5, 6, 0, 1 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.FRIDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenSaturdayIsFirstDayOfWeek() {
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Saturday as first day means Friday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = new int[]{ 1, 2, 3, 4, 5, 6, 0 };
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.SATURDAY);
        for (int day = 1; day <= 7; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[(day - 1)] = underTest.getDayOfWeek(calendar);
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }
}

