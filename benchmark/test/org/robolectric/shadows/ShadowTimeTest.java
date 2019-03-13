package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.TimeFormatException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Arrays;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
public class ShadowTimeTest {
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();

    @Test
    public void shouldSetToNow() throws Exception {
        Time t = new Time();
        SystemClock.setCurrentTimeMillis(1000);
        t.setToNow();
        assertThat(t.toMillis(false)).isEqualTo(1000);
    }

    @Test
    public void shouldHaveNoArgsConstructor() throws Exception {
        Time t = new Time();
        Assert.assertNotNull(t.timezone);
    }

    @Test
    public void shouldHaveCopyConstructor() throws Exception {
        Time t = new Time();
        t.setToNow();
        Time t2 = new Time(t);
        Assert.assertEquals(t.timezone, t2.timezone);
        Assert.assertEquals(t.year, t2.year);
        Assert.assertEquals(t.month, t2.month);
        Assert.assertEquals(t.monthDay, t2.monthDay);
        Assert.assertEquals(t.hour, t2.hour);
        Assert.assertEquals(t.minute, t2.minute);
        Assert.assertEquals(t.second, t2.second);
    }

    @Test
    public void shouldHaveSetTime() throws Exception {
        Time t = new Time();
        t.setToNow();
        Time t2 = new Time();
        t2.set(t);
        Assert.assertEquals(t.timezone, t2.timezone);
        Assert.assertEquals(t.year, t2.year);
        Assert.assertEquals(t.month, t2.month);
        Assert.assertEquals(t.monthDay, t2.monthDay);
        Assert.assertEquals(t.hour, t2.hour);
        Assert.assertEquals(t.minute, t2.minute);
        Assert.assertEquals(t.second, t2.second);
    }

    @Test
    public void shouldHaveSet3Args() throws Exception {
        Time t = new Time();
        t.set(1, 1, 2000);
        Assert.assertEquals(t.year, 2000);
        Assert.assertEquals(t.month, 1);
        Assert.assertEquals(t.monthDay, 1);
    }

    @Test
    public void shouldHaveSet6Args() throws Exception {
        Time t = new Time();
        t.set(1, 1, 1, 1, 1, 2000);
        Assert.assertEquals(t.year, 2000);
        Assert.assertEquals(t.month, 1);
        Assert.assertEquals(t.monthDay, 1);
        Assert.assertEquals(t.second, 1);
        Assert.assertEquals(t.minute, 1);
        Assert.assertEquals(t.hour, 1);
    }

    @Test
    public void shouldHaveTimeZoneConstructor() throws Exception {
        Time t = new Time("UTC");
        Assert.assertEquals(t.timezone, "UTC");
    }

    @Test
    public void shouldClear() throws Exception {
        Time t = new Time();
        t.setToNow();
        t.clear("UTC");
        Assert.assertEquals("UTC", t.timezone);
        Assert.assertEquals(0, t.year);
        Assert.assertEquals(0, t.month);
        Assert.assertEquals(0, t.monthDay);
        Assert.assertEquals(0, t.hour);
        Assert.assertEquals(0, t.minute);
        Assert.assertEquals(0, t.second);
        Assert.assertEquals(0, t.weekDay);
        Assert.assertEquals(0, t.yearDay);
        Assert.assertEquals(0, t.gmtoff);
        Assert.assertEquals((-1), t.isDst);
    }

    @Test
    public void shouldHaveToMillis() throws Exception {
        Time t = new Time();
        t.set((86400 * 1000));
        Assert.assertEquals((86400 * 1000), t.toMillis(false));
    }

    @Test
    public void shouldHaveCurrentTimeZone() throws Exception {
        Assert.assertNotNull(Time.getCurrentTimezone());
    }

    @Test
    public void shouldSwitchTimeZones() throws Exception {
        Time t = new Time("UTC");
        t.set(1414213562373L);
        assertThat(t.timezone).isEqualTo("UTC");
        assertThat(t.gmtoff).isEqualTo(0);
        assertThat(t.format3339(false)).isEqualTo("2014-10-25T05:06:02.000Z");
        t.switchTimezone("America/New_York");
        assertThat(t.format3339(false)).isEqualTo("2014-10-25T01:06:02.000-04:00");
        assertThat(t.timezone).isEqualTo("America/New_York");
        assertThat(t.gmtoff).isEqualTo((-14400L));
        assertThat(t.toMillis(true)).isEqualTo(1414213562000L);
    }

    @Test
    public void shouldHaveCompareAndBeforeAfter() throws Exception {
        Time a = new Time();
        Time b = new Time();
        assertThat(Time.compare(a, b)).isEqualTo(0);
        assertThat(a.before(b)).isFalse();
        assertThat(a.after(b)).isFalse();
        a.year = 2000;
        assertThat(Time.compare(a, b)).isAtLeast(0);
        assertThat(a.after(b)).isTrue();
        assertThat(b.before(a)).isTrue();
        b.year = 2001;
        assertThat(Time.compare(a, b)).isAtMost(0);
        assertThat(b.after(a)).isTrue();
        assertThat(a.before(b)).isTrue();
    }

    @Test
    public void shouldHaveParse() throws Exception {
        Time t = new Time("Europe/Berlin");
        Assert.assertFalse(t.parse("20081013T160000"));
        Assert.assertEquals(2008, t.year);
        Assert.assertEquals(9, t.month);
        Assert.assertEquals(13, t.monthDay);
        Assert.assertEquals(16, t.hour);
        Assert.assertEquals(0, t.minute);
        Assert.assertEquals(0, t.second);
        Assert.assertTrue(t.parse("20081013T160000Z"));
        Assert.assertEquals(2008, t.year);
        Assert.assertEquals(9, t.month);
        Assert.assertEquals(13, t.monthDay);
        Assert.assertEquals(16, t.hour);
        Assert.assertEquals(0, t.minute);
        Assert.assertEquals(0, t.second);
    }

    @Test
    public void shouldParseRfc3339() {
        for (String tz : Arrays.asList("Europe/Berlin", "America/Los Angeles", "Australia/Adelaide")) {
            String desc = "Eval when local timezone is " + tz;
            TimeZone.setDefault(TimeZone.getTimeZone(tz));
            Time t = new Time("Europe/Berlin");
            Assert.assertTrue(desc, t.parse3339("2008-10-13T16:30:50Z"));
            Assert.assertEquals(desc, 2008, t.year);
            Assert.assertEquals(desc, 9, t.month);
            Assert.assertEquals(desc, 13, t.monthDay);
            Assert.assertEquals(desc, 16, t.hour);
            Assert.assertEquals(desc, 30, t.minute);
            Assert.assertEquals(desc, 50, t.second);
            Assert.assertEquals(desc, "UTC", t.timezone);
            Assert.assertFalse(desc, t.allDay);
            t = new Time("Europe/Berlin");
            Assert.assertTrue(desc, t.parse3339("2008-10-13T16:30:50.000+07:00"));
            Assert.assertEquals(desc, 2008, t.year);
            Assert.assertEquals(desc, 9, t.month);
            Assert.assertEquals(desc, 13, t.monthDay);
            Assert.assertEquals(desc, 9, t.hour);
            Assert.assertEquals(desc, 30, t.minute);
            Assert.assertEquals(desc, 50, t.second);
            Assert.assertEquals(desc, "UTC", t.timezone);
            Assert.assertFalse(desc, t.allDay);
            t = new Time("Europe/Berlin");
            Assert.assertFalse(desc, t.parse3339("2008-10-13"));
            Assert.assertEquals(desc, 2008, t.year);
            Assert.assertEquals(desc, 9, t.month);
            Assert.assertEquals(desc, 13, t.monthDay);
            Assert.assertEquals(desc, 0, t.hour);
            Assert.assertEquals(desc, 0, t.minute);
            Assert.assertEquals(desc, 0, t.second);
            Assert.assertEquals(desc, "Europe/Berlin", t.timezone);
            Assert.assertTrue(desc, t.allDay);
        }
    }

    // this fails on LOLLIPOP+; is the shadow impl of parse3339 correct for pre-LOLLIPOP?
    @Test
    @Config(maxSdk = VERSION_CODES.KITKAT_WATCH)
    public void shouldParseRfc3339_withQuestionableFormat() {
        for (String tz : Arrays.asList("Europe/Berlin", "America/Los Angeles", "Australia/Adelaide")) {
            String desc = "Eval when local timezone is " + tz;
            TimeZone.setDefault(TimeZone.getTimeZone(tz));
            Time t = new Time("Europe/Berlin");
            Assert.assertTrue(desc, t.parse3339("2008-10-13T16:30:50.999-03"));
            Assert.assertEquals(desc, 2008, t.year);
            Assert.assertEquals(desc, 9, t.month);
            Assert.assertEquals(desc, 13, t.monthDay);
            Assert.assertEquals(desc, 19, t.hour);
            Assert.assertEquals(desc, 30, t.minute);
            Assert.assertEquals(desc, 50, t.second);
            Assert.assertEquals(desc, "UTC", t.timezone);
            Assert.assertFalse(desc, t.allDay);
        }
    }

    @Test(expected = TimeFormatException.class)
    public void shouldThrowTimeFormatException() throws Exception {
        Time t = new Time();
        t.parse("BLARGH");
    }

    @Test
    public void shouldHaveParseShort() throws Exception {
        Time t = new Time();
        t.parse("20081013");
        Assert.assertEquals(2008, t.year);
        Assert.assertEquals(9, t.month);
        Assert.assertEquals(13, t.monthDay);
        Assert.assertEquals(0, t.hour);
        Assert.assertEquals(0, t.minute);
        Assert.assertEquals(0, t.second);
    }

    @Test
    public void shouldFormat() throws Exception {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(3600000L);
        Assert.assertEquals("Hello epoch 01 1970 01", t.format("Hello epoch %d %Y %d"));
        Assert.assertEquals("Hello epoch  1:00 AM", t.format("Hello epoch %l:%M %p"));
    }

    @Test
    public void shouldFormatAndroidStrings() throws Exception {
        Time t = new Time("UTC");
        // NOTE: month is zero-based.
        t.set(12, 13, 14, 8, 8, 1987);
        Assert.assertEquals(1987, t.year);
        Assert.assertEquals(8, t.month);
        Assert.assertEquals(8, t.monthDay);
        Assert.assertEquals(14, t.hour);
        Assert.assertEquals(13, t.minute);
        Assert.assertEquals(12, t.second);
        // ICS
        // date_and_time
        Assert.assertEquals("Sep 8, 1987, 2:13:12 PM", t.format("%b %-e, %Y, %-l:%M:%S %p"));
        // hour_minute_cap_ampm
        Assert.assertEquals("2:13PM", t.format("%-l:%M%^p"));
    }

    @Test
    public void shouldFormatAllFormats() throws Exception {
        Time t = new Time("Asia/Tokyo");
        t.set(1407496560000L);
        // Don't check for %c (the docs state not to use it, and it doesn't work correctly).
        Assert.assertEquals("Fri", t.format("%a"));
        Assert.assertEquals("Friday", t.format("%A"));
        Assert.assertEquals("Aug", t.format("%b"));
        Assert.assertEquals("August", t.format("%B"));
        Assert.assertEquals("20", t.format("%C"));
        Assert.assertEquals("08", t.format("%d"));
        Assert.assertEquals("08/08/14", t.format("%D"));
        Assert.assertEquals(" 8", t.format("%e"));
        Assert.assertEquals("2014-08-08", t.format("%F"));
        Assert.assertEquals("14", t.format("%g"));
        Assert.assertEquals("2014", t.format("%G"));
        Assert.assertEquals("Aug", t.format("%h"));
        Assert.assertEquals("20", t.format("%H"));
        Assert.assertEquals("08", t.format("%I"));
        Assert.assertEquals("220", t.format("%j"));
        Assert.assertEquals("20", t.format("%k"));
        Assert.assertEquals(" 8", t.format("%l"));
        Assert.assertEquals("08", t.format("%m"));
        Assert.assertEquals("16", t.format("%M"));
        Assert.assertEquals("\n", t.format("%n"));
        Assert.assertEquals("PM", t.format("%p"));
        Assert.assertEquals("pm", t.format("%P"));
        Assert.assertEquals("08:16:00 PM", t.format("%r"));
        Assert.assertEquals("20:16", t.format("%R"));
        Assert.assertEquals("1407496560", t.format("%s"));
        Assert.assertEquals("00", t.format("%S"));
        Assert.assertEquals("\t", t.format("%t"));
        Assert.assertEquals("20:16:00", t.format("%T"));
        Assert.assertEquals("5", t.format("%u"));
        Assert.assertEquals("32", t.format("%V"));
        Assert.assertEquals("5", t.format("%w"));
        Assert.assertEquals("14", t.format("%y"));
        Assert.assertEquals("2014", t.format("%Y"));
        Assert.assertEquals("+0900", t.format("%z"));
        Assert.assertEquals("JST", t.format("%Z"));
        // Padding.
        Assert.assertEquals("8", t.format("%-l"));
        Assert.assertEquals(" 8", t.format("%_l"));
        Assert.assertEquals("08", t.format("%0l"));
        // Escape.
        Assert.assertEquals("%", t.format("%%"));
    }

    // these fail on LOLLIPOP+; is the shadow impl of format correct for pre-LOLLIPOP?
    @Test
    @Config(maxSdk = VERSION_CODES.KITKAT_WATCH)
    public void shouldFormatAllFormats_withQuestionableResults() throws Exception {
        Time t = new Time("Asia/Tokyo");
        t.set(1407496560000L);
        Assert.assertEquals("08/08/2014", t.format("%x"));
        Assert.assertEquals("08:16:00 PM", t.format("%X"));
        // Case.
        Assert.assertEquals("PM", t.format("%^P"));
        Assert.assertEquals("PM", t.format("%#P"));
    }

    @Test
    public void shouldFormat2445() throws Exception {
        Time t = new Time();
        t.timezone = "PST";
        Assert.assertEquals("19700101T000000", t.format2445());
        t.timezone = Time.TIMEZONE_UTC;
        // 2445 formatted date should hava a Z postfix
        Assert.assertEquals("19700101T000000Z", t.format2445());
    }

    @Test
    public void shouldFormat3339() throws Exception {
        Time t = new Time("Europe/Berlin");
        Assert.assertEquals("1970-01-01T00:00:00.000+00:00", t.format3339(false));
        Assert.assertEquals("1970-01-01", t.format3339(true));
    }

    @Test
    public void testIsEpoch() throws Exception {
        Time t = new Time();
        boolean isEpoch = Time.isEpoch(t);
        Assert.assertEquals(true, isEpoch);
    }

    @Test
    public void testGetJulianDay() throws Exception {
        Time time = new Time();
        time.set(0, 0, 0, 12, 5, 2008);
        time.timezone = "Australia/Sydney";
        long millis = time.normalize(true);
        // This is the Julian day for 12am for this day of the year
        int julianDay = Time.getJulianDay(millis, time.gmtoff);
        // Change the time during the day and check that we get the same
        // Julian day.
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                time.set(0, minute, hour, 12, 5, 2008);
                millis = time.normalize(true);
                int day = Time.getJulianDay(millis, time.gmtoff);
                Assert.assertEquals(day, julianDay);
            }
        }
    }

    @Test
    public void testSetJulianDay() throws Exception {
        Time time = new Time();
        time.set(0, 0, 0, 12, 5, 2008);
        time.timezone = "Australia/Sydney";
        long millis = time.normalize(true);
        int julianDay = Time.getJulianDay(millis, time.gmtoff);
        time.setJulianDay(julianDay);
        Assert.assertTrue((((time.hour) == 0) || ((time.hour) == 1)));
        Assert.assertEquals(0, time.minute);
        Assert.assertEquals(0, time.second);
        millis = time.toMillis(false);
        int day = Time.getJulianDay(millis, time.gmtoff);
        Assert.assertEquals(day, julianDay);
    }
}

