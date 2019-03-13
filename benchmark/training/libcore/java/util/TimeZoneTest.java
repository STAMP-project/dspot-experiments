/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import junit.framework.TestCase;


public class TimeZoneTest extends TestCase {
    // http://code.google.com/p/android/issues/detail?id=877
    public void test_useDaylightTime_Taiwan() {
        TimeZone asiaTaipei = TimeZone.getTimeZone("Asia/Taipei");
        TestCase.assertFalse("Taiwan doesn't use DST", asiaTaipei.useDaylightTime());
    }

    // http://code.google.com/p/android/issues/detail?id=8016
    public void test_useDaylightTime_Iceland() {
        TimeZone atlanticReykjavik = TimeZone.getTimeZone("Atlantic/Reykjavik");
        TestCase.assertFalse("Reykjavik doesn't use DST", atlanticReykjavik.useDaylightTime());
    }

    // http://code.google.com/p/android/issues/detail?id=11542
    public void test_clone_SimpleTimeZone() {
        SimpleTimeZone stz = new SimpleTimeZone(21600000, "Central Standard Time");
        stz.setStartYear(1000);
        stz.inDaylightTime(new Date());
        stz.clone();
    }

    // http://b/3049014
    public void testCustomTimeZoneDisplayNames() {
        TimeZone tz0001 = new SimpleTimeZone(60000, "ONE MINUTE");
        TimeZone tz0130 = new SimpleTimeZone(5400000, "ONE HOUR, THIRTY");
        TimeZone tzMinus0130 = new SimpleTimeZone((-5400000), "NEG ONE HOUR, THIRTY");
        TestCase.assertEquals("GMT+00:01", tz0001.getDisplayName(false, TimeZone.SHORT, Locale.US));
        TestCase.assertEquals("GMT+01:30", tz0130.getDisplayName(false, TimeZone.SHORT, Locale.US));
        TestCase.assertEquals("GMT-01:30", tzMinus0130.getDisplayName(false, TimeZone.SHORT, Locale.US));
    }

    // http://code.google.com/p/android/issues/detail?id=14395
    public void testPreHistoricInDaylightTime() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
        TimeZone.setDefault(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = sdf.parse("1902-11-01T00:00:00.000+0800");
        TestCase.assertEquals((-2119680000000L), date.getTime());
        TestCase.assertEquals((-28800000), tz.getOffset(date.getTime()));
        TestCase.assertFalse(tz.inDaylightTime(date));
        TestCase.assertEquals("Fri Oct 31 08:00:00 PST 1902", date.toString());
        TestCase.assertEquals("31 Oct 1902 16:00:00 GMT", date.toGMTString());
        // Any time before we have transition data is considered non-daylight, even in summer.
        date = sdf.parse("1902-06-01T00:00:00.000+0800");
        TestCase.assertEquals((-28800000), tz.getOffset(date.getTime()));
        TestCase.assertFalse(tz.inDaylightTime(date));
    }

    public void testPreHistoricOffsets() throws Exception {
        // The "Asia/Saigon" time zone has just a few transitions, and hasn't changed in a
        // long time, which is convenient for testing:
        // 
        // libcore.util.ZoneInfo[Asia/Saigon,mRawOffset=25200000,mUseDst=false]
        // 0 : time=-2005974400 Fri Jun 08 16:53:20 1906 GMT+00:00 = Fri Jun 08 23:59:40 1906 ICT isDst=0 offset=  380 gmtOffset=25580
        // 1 : time=-1855983920 Fri Mar 10 16:54:40 1911 GMT+00:00 = Fri Mar 10 23:54:40 1911 ICT isDst=0 offset=    0 gmtOffset=25200
        // 2 : time=-1819954800 Tue Apr 30 17:00:00 1912 GMT+00:00 = Wed May 01 01:00:00 1912 ICT isDst=0 offset= 3600 gmtOffset=28800
        // 3 : time=-1220428800 Thu Apr 30 16:00:00 1931 GMT+00:00 = Thu Apr 30 23:00:00 1931 ICT isDst=0 offset=    0 gmtOffset=25200
        TimeZone tz = TimeZone.getTimeZone("Asia/Saigon");
        // Times before our first transition should assume we're still following that transition.
        // Note: the RI reports 25600 here because it has more transitions than we do.
        TimeZoneTest.assertNonDaylightOffset(25580, (-2005975000L), tz);
        TimeZoneTest.assertNonDaylightOffset(25580, (-2005974400L), tz);// 0

        TimeZoneTest.assertNonDaylightOffset(25580, (-2005974000L), tz);
        TimeZoneTest.assertNonDaylightOffset(25200, (-1855983920L), tz);// 1

        TimeZoneTest.assertNonDaylightOffset(25200, (-1855983900L), tz);
        TimeZoneTest.assertNonDaylightOffset(28800, (-1819954800L), tz);// 2

        TimeZoneTest.assertNonDaylightOffset(28800, (-1819954000L), tz);
        TimeZoneTest.assertNonDaylightOffset(25200, (-1220428800L), tz);// 3

        // Times after out last transition should assume we're still following that transition.
        TimeZoneTest.assertNonDaylightOffset(25200, (-1220428000L), tz);
        // There are plenty more examples. "Africa/Bissau" is one:
        // 
        // libcore.util.ZoneInfo[Africa/Bissau,mRawOffset=0,mUseDst=false]
        // 0 : time=-1849388260 Fri May 26 01:02:20 1911 GMT+00:00 = Fri May 26 00:02:20 1911 GMT isDst=0 offset=-3600 gmtOffset=-3600
        // 1 : time=  157770000 Wed Jan 01 01:00:00 1975 GMT+00:00 = Wed Jan 01 01:00:00 1975 GMT isDst=0 offset=    0 gmtOffset=0
        tz = TimeZone.getTimeZone("Africa/Bissau");
        TimeZoneTest.assertNonDaylightOffset((-3600), (-1849388300L), tz);
        TimeZoneTest.assertNonDaylightOffset((-3600), (-1849388260L), tz);// 0

        TimeZoneTest.assertNonDaylightOffset((-3600), (-1849388200L), tz);
        TimeZoneTest.assertNonDaylightOffset(0, 157770000L, tz);// 1

        TimeZoneTest.assertNonDaylightOffset(0, 157780000L, tz);
    }

    public void testZeroTransitionZones() throws Exception {
        // Zones with no transitions historical or future seem ideal for testing.
        String[] ids = new String[]{ "Africa/Bujumbura", "Indian/Cocos", "Pacific/Wake", "UTC" };
        for (String id : ids) {
            TimeZone tz = TimeZone.getTimeZone(id);
            TestCase.assertFalse(tz.useDaylightTime());
            TestCase.assertFalse(tz.inDaylightTime(new Date(Integer.MIN_VALUE)));
            TestCase.assertFalse(tz.inDaylightTime(new Date(0)));
            TestCase.assertFalse(tz.inDaylightTime(new Date(Integer.MAX_VALUE)));
            int currentOffset = tz.getOffset(new Date(0).getTime());
            TestCase.assertEquals(currentOffset, tz.getOffset(new Date(Integer.MIN_VALUE).getTime()));
            TestCase.assertEquals(currentOffset, tz.getOffset(new Date(Integer.MAX_VALUE).getTime()));
        }
    }

    // http://code.google.com/p/android/issues/detail?id=16608
    public void testHelsinkiOverflow() throws Exception {
        long time = 2147483648000L;// Tue, 19 Jan 2038 03:14:08 GMT

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Helsinki");
        long offset = timeZone.getOffset(time);
        // This might cause us trouble if Europe/Helsinki changes its rules. See the bug for
        // details of the intent of this test.
        TestCase.assertEquals(7200000, offset);
    }

    // http://code.google.com/p/android/issues/detail?id=11918
    public void testHasSameRules() throws Exception {
        TimeZone denver = TimeZone.getTimeZone("America/Denver");
        TimeZone phoenix = TimeZone.getTimeZone("America/Phoenix");
        TestCase.assertFalse(denver.hasSameRules(phoenix));
    }

    // http://code.google.com/p/android/issues/detail?id=24036
    public void testNullId() throws Exception {
        try {
            TimeZone.getTimeZone(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    // http://b.corp.google.com/issue?id=6556561
    public void testCustomZoneIds() throws Exception {
        // These are all okay (and equivalent).
        TestCase.assertEquals("GMT+05:00", TimeZone.getTimeZone("GMT+05:00").getID());
        TestCase.assertEquals("GMT+05:00", TimeZone.getTimeZone("GMT+5:00").getID());
        TestCase.assertEquals("GMT+05:00", TimeZone.getTimeZone("GMT+0500").getID());
        TestCase.assertEquals("GMT+05:00", TimeZone.getTimeZone("GMT+500").getID());
        TestCase.assertEquals("GMT+05:00", TimeZone.getTimeZone("GMT+5").getID());
        // These aren't.
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5.5").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:5").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:0").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:005").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:000").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+005:00").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+05:99").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+28:00").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+05:00.00").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+05:00:00").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+junk").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5junk").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:junk").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("GMT+5:00junk").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("junkGMT+5:00").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("junk").getID());
        TestCase.assertEquals("GMT", TimeZone.getTimeZone("gmt+5:00").getID());
    }

    public void test_getDSTSavings() throws Exception {
        TestCase.assertEquals(0, TimeZone.getTimeZone("UTC").getDSTSavings());
        TestCase.assertEquals(3600000, TimeZone.getTimeZone("America/Los_Angeles").getDSTSavings());
        TestCase.assertEquals(1800000, TimeZone.getTimeZone("Australia/Lord_Howe").getDSTSavings());
    }

    public void testSimpleTimeZoneDoesNotCallOverrideableMethodsFromConstructor() {
        new SimpleTimeZone(0, "X", Calendar.MARCH, 1, 1, 1, Calendar.SEPTEMBER, 1, 1, 1) {
            @Override
            public void setStartRule(int m, int d, int dow, int time) {
                TestCase.fail();
            }

            @Override
            public void setStartRule(int m, int d, int dow, int time, boolean after) {
                TestCase.fail();
            }

            @Override
            public void setEndRule(int m, int d, int dow, int time) {
                TestCase.fail();
            }

            @Override
            public void setEndRule(int m, int d, int dow, int time, boolean after) {
                TestCase.fail();
            }
        };
    }

    // http://b/7955614 and http://b/8026776.
    public void testDisplayNames() throws Exception {
        // Check that there are no time zones that use DST but have the same display name for
        // both standard and daylight time.
        StringBuilder failures = new StringBuilder();
        for (String id : TimeZone.getAvailableIDs()) {
            TimeZone tz = TimeZone.getTimeZone(id);
            String longDst = tz.getDisplayName(true, TimeZone.LONG, Locale.US);
            String longStd = tz.getDisplayName(false, TimeZone.LONG, Locale.US);
            String shortDst = tz.getDisplayName(true, TimeZone.SHORT, Locale.US);
            String shortStd = tz.getDisplayName(false, TimeZone.SHORT, Locale.US);
            if (tz.useDaylightTime()) {
                // The long std and dst strings must differ!
                if (longDst.equals(longStd)) {
                    failures.append(String.format("\n%20s: LD=\'%s\' LS=\'%s\'!", id, longDst, longStd));
                }
                // The short std and dst strings must differ!
                if (shortDst.equals(shortStd)) {
                    failures.append(String.format("\n%20s: SD=\'%s\' SS=\'%s\'!", id, shortDst, shortStd));
                }
                // If the short std matches the long dst, or the long std matches the short dst,
                // it probably means we have a time zone that icu4c doesn't believe has ever
                // observed dst.
                if (shortStd.equals(longDst)) {
                    failures.append(String.format("\n%20s: SS=\'%s\' LD=\'%s\'!", id, shortStd, longDst));
                }
                if (longStd.equals(shortDst)) {
                    failures.append(String.format("\n%20s: LS=\'%s\' SD=\'%s\'!", id, longStd, shortDst));
                }
                // The long and short dst strings must differ!
                if ((longDst.equals(shortDst)) && (!(longDst.startsWith("GMT")))) {
                    failures.append(String.format("\n%20s: LD=\'%s\' SD=\'%s\'!", id, longDst, shortDst));
                }
            }
            // Sanity check that whenever a display name is just a GMT string that it's the
            // right GMT string.
            String gmtDst = TimeZoneTest.formatGmtString(tz, true);
            String gmtStd = TimeZoneTest.formatGmtString(tz, false);
            if ((TimeZoneTest.isGmtString(longDst)) && (!(longDst.equals(gmtDst)))) {
                failures.append(String.format("\n%s: LD %s", id, longDst));
            }
            if ((TimeZoneTest.isGmtString(longStd)) && (!(longStd.equals(gmtStd)))) {
                failures.append(String.format("\n%s: LS %s", id, longStd));
            }
            if ((TimeZoneTest.isGmtString(shortDst)) && (!(shortDst.equals(gmtDst)))) {
                failures.append(String.format("\n%s: SD %s", id, shortDst));
            }
            if ((TimeZoneTest.isGmtString(shortStd)) && (!(shortStd.equals(gmtStd)))) {
                failures.append(String.format("\n%s: SS %s", id, shortStd));
            }
        }
        TestCase.assertEquals("", failures.toString());
    }

    public void testSantiago() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("America/Santiago");
        TestCase.assertEquals("Chile Summer Time", tz.getDisplayName(true, TimeZone.LONG, Locale.US));
        TestCase.assertEquals("Chile Standard Time", tz.getDisplayName(false, TimeZone.LONG, Locale.US));
        TestCase.assertEquals("GMT-03:00", tz.getDisplayName(true, TimeZone.SHORT, Locale.US));
        TestCase.assertEquals("GMT-04:00", tz.getDisplayName(false, TimeZone.SHORT, Locale.US));
    }

    // http://b/7955614
    public void testApia() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("Pacific/Apia");
        TestCase.assertEquals("Samoa Daylight Time", tz.getDisplayName(true, TimeZone.LONG, Locale.US));
        TestCase.assertEquals("Samoa Standard Time", tz.getDisplayName(false, TimeZone.LONG, Locale.US));
        TestCase.assertEquals("GMT+14:00", tz.getDisplayName(true, TimeZone.SHORT, Locale.US));
        TestCase.assertEquals("GMT+13:00", tz.getDisplayName(false, TimeZone.SHORT, Locale.US));
    }

    public void testAllDisplayNames() throws Exception {
        for (Locale locale : Locale.getAvailableLocales()) {
            for (String id : TimeZone.getAvailableIDs()) {
                TimeZone tz = TimeZone.getTimeZone(id);
                TestCase.assertNotNull(tz.getDisplayName(false, TimeZone.LONG, locale));
            }
        }
    }
}

