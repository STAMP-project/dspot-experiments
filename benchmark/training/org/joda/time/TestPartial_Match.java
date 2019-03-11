/**
 * Copyright 2001-2005 Stephen Colebourne
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
package org.joda.time;


import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for Partial.
 *
 * @author Stephen Colebourne
 */
public class TestPartial_Match extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final int OFFSET = 1;

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestPartial_Match.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestPartial_Match.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestPartial_Match.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestPartial_Match.PARIS);

    private static final Chronology ISO_LONDON = ISOChronology.getInstance(TestPartial_Match.LONDON);

    private static final Chronology ISO_TOKYO = ISOChronology.getInstance(TestPartial_Match.TOKYO);

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(TestPartial_Match.PARIS);

    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(TestPartial_Match.LONDON);

    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestPartial_Match.TOKYO);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME1 = (((1L * (MILLIS_PER_HOUR)) + (2L * (MILLIS_PER_MINUTE))) + (3L * (MILLIS_PER_SECOND))) + 4L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestPartial_Match(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testIsMatch_Instant() {
        // Year=2005, Month=7 (July), DayOfWeek=2 (Tuesday)
        Partial test = createYMDwPartial(TestPartial_Match.ISO_UTC, 2005, 7, 2);
        DateTime instant = new DateTime(2005, 7, 5, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.isMatch(instant));
        instant = new DateTime(2005, 7, 4, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.isMatch(instant));
        instant = new DateTime(2005, 7, 6, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.isMatch(instant));
        instant = new DateTime(2005, 7, 12, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.isMatch(instant));
        instant = new DateTime(2005, 7, 19, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.isMatch(instant));
        instant = new DateTime(2005, 7, 26, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.isMatch(instant));
        instant = new DateTime(2005, 8, 2, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.isMatch(instant));
        instant = new DateTime(2006, 7, 5, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.isMatch(instant));
        instant = new DateTime(2005, 6, 5, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.isMatch(instant));
    }

    // -----------------------------------------------------------------------
    public void testIsMatch_Partial() {
        // Year=2005, Month=7 (July), DayOfWeek=2 (Tuesday)
        Partial test = createYMDwPartial(TestPartial_Match.ISO_UTC, 2005, 7, 2);
        LocalDate partial = new LocalDate(2005, 7, 5);
        TestCase.assertEquals(true, test.isMatch(partial));
        partial = new LocalDate(2005, 7, 4);
        TestCase.assertEquals(false, test.isMatch(partial));
        partial = new LocalDate(2005, 7, 6);
        TestCase.assertEquals(false, test.isMatch(partial));
        partial = new LocalDate(2005, 7, 12);
        TestCase.assertEquals(true, test.isMatch(partial));
        partial = new LocalDate(2005, 7, 19);
        TestCase.assertEquals(true, test.isMatch(partial));
        partial = new LocalDate(2005, 7, 26);
        TestCase.assertEquals(true, test.isMatch(partial));
        partial = new LocalDate(2005, 8, 2);
        TestCase.assertEquals(false, test.isMatch(partial));
        partial = new LocalDate(2006, 7, 5);
        TestCase.assertEquals(false, test.isMatch(partial));
        partial = new LocalDate(2005, 6, 5);
        TestCase.assertEquals(false, test.isMatch(partial));
        try {
            test.isMatch(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

