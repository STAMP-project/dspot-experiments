/**
 * Copyright 2001-2014 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.format;


import DateTimeZone.UTC;
import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;


/**
 * Test.
 */
public class TestDateTimeParserBucket extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(TestDateTimeParserBucket.PARIS);

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final DateTimeZone ZONE_0400 = DateTimeZone.forOffsetHours(4);

    private static final Chronology ISO_0400 = ISOChronology.getInstance(TestDateTimeParserBucket.ZONE_0400);

    private static final int MILLIS_PER_HOUR = 3600000;

    private static final int MILLIS_PER_MINUTE = 60000;

    private static final int OFFSET_0400 = 4 * (TestDateTimeParserBucket.MILLIS_PER_HOUR);

    private static final Locale LOCALE = Locale.CANADA;

    public TestDateTimeParserBucket(String name) {
        super(name);
    }

    public void testConstructor_5arg() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.BUDDHIST_PARIS, TestDateTimeParserBucket.LOCALE, 2010, 2001);
        TestCase.assertEquals(TestDateTimeParserBucket.BUDDHIST_UTC, test.getChronology());
        TestCase.assertEquals(TestDateTimeParserBucket.LOCALE, test.getLocale());
        TestCase.assertEquals(((Integer) (2010)), test.getPivotYear());
        TestCase.assertEquals(null, test.getOffsetInteger());
        TestCase.assertEquals(TestDateTimeParserBucket.PARIS, test.getZone());
    }

    public void testSetOffset() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.BUDDHIST_PARIS, TestDateTimeParserBucket.LOCALE, 2010, 2001);
        TestCase.assertEquals(null, test.getOffsetInteger());
        test.setOffset(((Integer) (1000)));
        TestCase.assertEquals(((Integer) (1000)), test.getOffsetInteger());
        test.setOffset(null);
        TestCase.assertEquals(null, test.getOffsetInteger());
    }

    public void testSetZone() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.BUDDHIST_PARIS, TestDateTimeParserBucket.LOCALE, 2010, 2001);
        TestCase.assertEquals(TestDateTimeParserBucket.PARIS, test.getZone());
        test.setZone(TestDateTimeParserBucket.LONDON);
        TestCase.assertEquals(TestDateTimeParserBucket.LONDON, test.getZone());
    }

    public void testCompute() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        TestCase.assertEquals((100 - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis());
        TestCase.assertEquals((100 - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        // note that computeMillis(true) differs depending on whether fields are saved or not
        TestCase.assertEquals((100 - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(true));
    }

    public void testSaveCompute() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis());
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.hourOfDay(), 5);
        TestCase.assertEquals((((5 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(((5 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(true));
        TestCase.assertEquals((((5 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), 6);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), 7);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (7 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_avoidSideEffects() {
        // computeMillis() has side effects, so check works without it
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        Object state = test.saveState();
        test.saveField(DateTimeFieldType.minuteOfHour(), 6);
        TestCase.assertEquals(true, test.restoreState(state));
        test.saveField(DateTimeFieldType.minuteOfHour(), 7);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (7 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_offset() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.setOffset(((Integer) (0)));
        TestCase.assertEquals(((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_zone() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.setZone(UTC);
        TestCase.assertEquals(((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_text() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), "2", Locale.ENGLISH);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), "6", Locale.ENGLISH);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_twoStates() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state1 = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), 6);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state2 = test.saveState();
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.secondOfMinute(), 8);
        TestCase.assertEquals((((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 8000) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state2));
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state1));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state2));
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state1));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_sameStates() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        Object state1 = test.saveState();
        Object state2 = test.saveState();
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), 6);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state2));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        test.saveField(DateTimeFieldType.minuteOfHour(), 8);
        TestCase.assertEquals(((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (8 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state1));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state2));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
        TestCase.assertEquals(true, test.restoreState(state1));
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testSaveRestoreState_badType() {
        DateTimeParserBucket bucket1 = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        DateTimeParserBucket bucket2 = new DateTimeParserBucket(100, TestDateTimeParserBucket.BUDDHIST_PARIS, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        TestCase.assertEquals(false, bucket1.restoreState(null));
        TestCase.assertEquals(false, bucket1.restoreState(""));
        TestCase.assertEquals(false, bucket2.restoreState(bucket1.saveState()));
    }

    // -------------------------------------------------------------------------
    public void testReset() {
        DateTimeParserBucket test = new DateTimeParserBucket(100, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        TestCase.assertEquals(TestDateTimeParserBucket.ISO_UTC, test.getChronology());
        TestCase.assertEquals(TestDateTimeParserBucket.LOCALE, test.getLocale());
        TestCase.assertEquals(((Integer) (2000)), test.getPivotYear());
        TestCase.assertEquals(null, test.getOffsetInteger());
        TestCase.assertEquals(TestDateTimeParserBucket.ZONE_0400, test.getZone());
        test.setOffset(((Integer) (200)));
        test.setZone(TestDateTimeParserBucket.LONDON);
        test.saveField(DateTimeFieldType.hourOfDay(), 2);
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + 100) - 200), test.computeMillis(false));
        TestCase.assertEquals(((Integer) (200)), test.getOffsetInteger());
        TestCase.assertEquals(TestDateTimeParserBucket.LONDON, test.getZone());
        test.reset();
        TestCase.assertEquals(TestDateTimeParserBucket.ISO_UTC, test.getChronology());
        TestCase.assertEquals(TestDateTimeParserBucket.LOCALE, test.getLocale());
        TestCase.assertEquals(((Integer) (2000)), test.getPivotYear());
        TestCase.assertEquals(null, test.getOffsetInteger());
        TestCase.assertEquals(TestDateTimeParserBucket.ZONE_0400, test.getZone());
        TestCase.assertEquals((100 - (TestDateTimeParserBucket.OFFSET_0400)), test.computeMillis(false));
    }

    public void testParse() {
        DateTimeParserBucket test = new DateTimeParserBucket(0, TestDateTimeParserBucket.ISO_0400, TestDateTimeParserBucket.LOCALE, 2000, 2000);
        DateTimeParser parser = new DateTimeParser() {
            public int parseInto(DateTimeParserBucket bucket, String text, int position) {
                bucket.saveField(DateTimeFieldType.hourOfDay(), 2);
                bucket.saveField(DateTimeFieldType.minuteOfHour(), 6);
                return position + 1;
            }

            public int estimateParsedLength() {
                return 1;
            }
        };
        long millis = test.parseMillis(parser, "A");
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) - (TestDateTimeParserBucket.OFFSET_0400)), millis);
        millis = test.parseMillis(parser, "B");
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) - (TestDateTimeParserBucket.OFFSET_0400)), millis);
        millis = test.parseMillis(parser, "C");
        TestCase.assertEquals((((2 * (TestDateTimeParserBucket.MILLIS_PER_HOUR)) + (6 * (TestDateTimeParserBucket.MILLIS_PER_MINUTE))) - (TestDateTimeParserBucket.OFFSET_0400)), millis);
    }
}

