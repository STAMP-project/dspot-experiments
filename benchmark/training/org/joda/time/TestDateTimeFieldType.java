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


import java.lang.reflect.Constructor;
import junit.framework.TestCase;
import org.joda.time.chrono.CopticChronology;


/**
 * This class is a Junit unit test for Chronology.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeFieldType extends TestCase {
    public TestDateTimeFieldType(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void test_era() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.era(), DateTimeFieldType.era());
        TestCase.assertEquals("era", DateTimeFieldType.era().getName());
        TestCase.assertEquals(DurationFieldType.eras(), DateTimeFieldType.era().getDurationType());
        TestCase.assertEquals(null, DateTimeFieldType.era().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().era(), DateTimeFieldType.era().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().era().isSupported(), DateTimeFieldType.era().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.era());
    }

    public void test_centuryOfEra() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.centuryOfEra(), DateTimeFieldType.centuryOfEra());
        TestCase.assertEquals("centuryOfEra", DateTimeFieldType.centuryOfEra().getName());
        TestCase.assertEquals(DurationFieldType.centuries(), DateTimeFieldType.centuryOfEra().getDurationType());
        TestCase.assertEquals(DurationFieldType.eras(), DateTimeFieldType.centuryOfEra().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().centuryOfEra(), DateTimeFieldType.centuryOfEra().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().centuryOfEra().isSupported(), DateTimeFieldType.centuryOfEra().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.centuryOfEra());
    }

    public void test_yearOfCentury() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.yearOfCentury(), DateTimeFieldType.yearOfCentury());
        TestCase.assertEquals("yearOfCentury", DateTimeFieldType.yearOfCentury().getName());
        TestCase.assertEquals(DurationFieldType.years(), DateTimeFieldType.yearOfCentury().getDurationType());
        TestCase.assertEquals(DurationFieldType.centuries(), DateTimeFieldType.yearOfCentury().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().yearOfCentury(), DateTimeFieldType.yearOfCentury().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().yearOfCentury().isSupported(), DateTimeFieldType.yearOfCentury().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.yearOfCentury());
    }

    public void test_yearOfEra() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.yearOfEra(), DateTimeFieldType.yearOfEra());
        TestCase.assertEquals("yearOfEra", DateTimeFieldType.yearOfEra().getName());
        TestCase.assertEquals(DurationFieldType.years(), DateTimeFieldType.yearOfEra().getDurationType());
        TestCase.assertEquals(DurationFieldType.eras(), DateTimeFieldType.yearOfEra().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().yearOfEra(), DateTimeFieldType.yearOfEra().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().yearOfEra().isSupported(), DateTimeFieldType.yearOfEra().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.yearOfEra());
    }

    public void test_year() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.year(), DateTimeFieldType.year());
        TestCase.assertEquals("year", DateTimeFieldType.year().getName());
        TestCase.assertEquals(DurationFieldType.years(), DateTimeFieldType.year().getDurationType());
        TestCase.assertEquals(null, DateTimeFieldType.year().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().year(), DateTimeFieldType.year().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().year().isSupported(), DateTimeFieldType.year().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.year());
    }

    public void test_monthOfYear() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.monthOfYear(), DateTimeFieldType.monthOfYear());
        TestCase.assertEquals("monthOfYear", DateTimeFieldType.monthOfYear().getName());
        TestCase.assertEquals(DurationFieldType.months(), DateTimeFieldType.monthOfYear().getDurationType());
        TestCase.assertEquals(DurationFieldType.years(), DateTimeFieldType.monthOfYear().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().monthOfYear(), DateTimeFieldType.monthOfYear().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().monthOfYear().isSupported(), DateTimeFieldType.monthOfYear().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.monthOfYear());
    }

    public void test_weekyearOfCentury() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.weekyearOfCentury(), DateTimeFieldType.weekyearOfCentury());
        TestCase.assertEquals("weekyearOfCentury", DateTimeFieldType.weekyearOfCentury().getName());
        TestCase.assertEquals(DurationFieldType.weekyears(), DateTimeFieldType.weekyearOfCentury().getDurationType());
        TestCase.assertEquals(DurationFieldType.centuries(), DateTimeFieldType.weekyearOfCentury().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyearOfCentury(), DateTimeFieldType.weekyearOfCentury().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyearOfCentury().isSupported(), DateTimeFieldType.weekyearOfCentury().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.weekyearOfCentury());
    }

    public void test_weekyear() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.weekyear(), DateTimeFieldType.weekyear());
        TestCase.assertEquals("weekyear", DateTimeFieldType.weekyear().getName());
        TestCase.assertEquals(DurationFieldType.weekyears(), DateTimeFieldType.weekyear().getDurationType());
        TestCase.assertEquals(null, DateTimeFieldType.weekyear().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyear(), DateTimeFieldType.weekyear().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyear().isSupported(), DateTimeFieldType.weekyear().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.weekyear());
    }

    public void test_weekOfWeekyear() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.weekOfWeekyear(), DateTimeFieldType.weekOfWeekyear());
        TestCase.assertEquals("weekOfWeekyear", DateTimeFieldType.weekOfWeekyear().getName());
        TestCase.assertEquals(DurationFieldType.weeks(), DateTimeFieldType.weekOfWeekyear().getDurationType());
        TestCase.assertEquals(DurationFieldType.weekyears(), DateTimeFieldType.weekOfWeekyear().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekOfWeekyear(), DateTimeFieldType.weekOfWeekyear().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekOfWeekyear().isSupported(), DateTimeFieldType.weekOfWeekyear().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.weekOfWeekyear());
    }

    public void test_dayOfYear() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.dayOfYear(), DateTimeFieldType.dayOfYear());
        TestCase.assertEquals("dayOfYear", DateTimeFieldType.dayOfYear().getName());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.dayOfYear().getDurationType());
        TestCase.assertEquals(DurationFieldType.years(), DateTimeFieldType.dayOfYear().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfYear(), DateTimeFieldType.dayOfYear().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfYear().isSupported(), DateTimeFieldType.dayOfYear().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.dayOfYear());
    }

    public void test_dayOfMonth() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.dayOfMonth(), DateTimeFieldType.dayOfMonth());
        TestCase.assertEquals("dayOfMonth", DateTimeFieldType.dayOfMonth().getName());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.dayOfMonth().getDurationType());
        TestCase.assertEquals(DurationFieldType.months(), DateTimeFieldType.dayOfMonth().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfMonth(), DateTimeFieldType.dayOfMonth().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfMonth().isSupported(), DateTimeFieldType.dayOfMonth().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.dayOfMonth());
    }

    public void test_dayOfWeek() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.dayOfWeek(), DateTimeFieldType.dayOfWeek());
        TestCase.assertEquals("dayOfWeek", DateTimeFieldType.dayOfWeek().getName());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.dayOfWeek().getDurationType());
        TestCase.assertEquals(DurationFieldType.weeks(), DateTimeFieldType.dayOfWeek().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfWeek(), DateTimeFieldType.dayOfWeek().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().dayOfWeek().isSupported(), DateTimeFieldType.dayOfWeek().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.dayOfWeek());
    }

    public void test_halfdayOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.halfdayOfDay(), DateTimeFieldType.halfdayOfDay());
        TestCase.assertEquals("halfdayOfDay", DateTimeFieldType.halfdayOfDay().getName());
        TestCase.assertEquals(DurationFieldType.halfdays(), DateTimeFieldType.halfdayOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.halfdayOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().halfdayOfDay(), DateTimeFieldType.halfdayOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().halfdayOfDay().isSupported(), DateTimeFieldType.halfdayOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.halfdayOfDay());
    }

    public void test_clockhourOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.clockhourOfDay(), DateTimeFieldType.clockhourOfDay());
        TestCase.assertEquals("clockhourOfDay", DateTimeFieldType.clockhourOfDay().getName());
        TestCase.assertEquals(DurationFieldType.hours(), DateTimeFieldType.clockhourOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.clockhourOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().clockhourOfDay(), DateTimeFieldType.clockhourOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().clockhourOfDay().isSupported(), DateTimeFieldType.clockhourOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.clockhourOfDay());
    }

    public void test_clockhourOfHalfday() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.clockhourOfHalfday(), DateTimeFieldType.clockhourOfHalfday());
        TestCase.assertEquals("clockhourOfHalfday", DateTimeFieldType.clockhourOfHalfday().getName());
        TestCase.assertEquals(DurationFieldType.hours(), DateTimeFieldType.clockhourOfHalfday().getDurationType());
        TestCase.assertEquals(DurationFieldType.halfdays(), DateTimeFieldType.clockhourOfHalfday().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().clockhourOfHalfday(), DateTimeFieldType.clockhourOfHalfday().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().clockhourOfHalfday().isSupported(), DateTimeFieldType.clockhourOfHalfday().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.clockhourOfHalfday());
    }

    public void test_hourOfHalfday() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.hourOfHalfday(), DateTimeFieldType.hourOfHalfday());
        TestCase.assertEquals("hourOfHalfday", DateTimeFieldType.hourOfHalfday().getName());
        TestCase.assertEquals(DurationFieldType.hours(), DateTimeFieldType.hourOfHalfday().getDurationType());
        TestCase.assertEquals(DurationFieldType.halfdays(), DateTimeFieldType.hourOfHalfday().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hourOfHalfday(), DateTimeFieldType.hourOfHalfday().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hourOfHalfday().isSupported(), DateTimeFieldType.hourOfHalfday().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.hourOfHalfday());
    }

    public void test_hourOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.hourOfDay(), DateTimeFieldType.hourOfDay());
        TestCase.assertEquals("hourOfDay", DateTimeFieldType.hourOfDay().getName());
        TestCase.assertEquals(DurationFieldType.hours(), DateTimeFieldType.hourOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.hourOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hourOfDay(), DateTimeFieldType.hourOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hourOfDay().isSupported(), DateTimeFieldType.hourOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.hourOfDay());
    }

    public void test_minuteOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.minuteOfDay(), DateTimeFieldType.minuteOfDay());
        TestCase.assertEquals("minuteOfDay", DateTimeFieldType.minuteOfDay().getName());
        TestCase.assertEquals(DurationFieldType.minutes(), DateTimeFieldType.minuteOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.minuteOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minuteOfDay(), DateTimeFieldType.minuteOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minuteOfDay().isSupported(), DateTimeFieldType.minuteOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.minuteOfDay());
    }

    public void test_minuteOfHour() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.minuteOfHour(), DateTimeFieldType.minuteOfHour());
        TestCase.assertEquals("minuteOfHour", DateTimeFieldType.minuteOfHour().getName());
        TestCase.assertEquals(DurationFieldType.minutes(), DateTimeFieldType.minuteOfHour().getDurationType());
        TestCase.assertEquals(DurationFieldType.hours(), DateTimeFieldType.minuteOfHour().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minuteOfHour(), DateTimeFieldType.minuteOfHour().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minuteOfHour().isSupported(), DateTimeFieldType.minuteOfHour().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.minuteOfHour());
    }

    public void test_secondOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.secondOfDay(), DateTimeFieldType.secondOfDay());
        TestCase.assertEquals("secondOfDay", DateTimeFieldType.secondOfDay().getName());
        TestCase.assertEquals(DurationFieldType.seconds(), DateTimeFieldType.secondOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.secondOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().secondOfDay(), DateTimeFieldType.secondOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().secondOfDay().isSupported(), DateTimeFieldType.secondOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.secondOfDay());
    }

    public void test_secondOfMinute() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.secondOfMinute(), DateTimeFieldType.secondOfMinute());
        TestCase.assertEquals("secondOfMinute", DateTimeFieldType.secondOfMinute().getName());
        TestCase.assertEquals(DurationFieldType.seconds(), DateTimeFieldType.secondOfMinute().getDurationType());
        TestCase.assertEquals(DurationFieldType.minutes(), DateTimeFieldType.secondOfMinute().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().secondOfMinute(), DateTimeFieldType.secondOfMinute().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().secondOfMinute().isSupported(), DateTimeFieldType.secondOfMinute().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.secondOfMinute());
    }

    public void test_millisOfDay() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.millisOfDay(), DateTimeFieldType.millisOfDay());
        TestCase.assertEquals("millisOfDay", DateTimeFieldType.millisOfDay().getName());
        TestCase.assertEquals(DurationFieldType.millis(), DateTimeFieldType.millisOfDay().getDurationType());
        TestCase.assertEquals(DurationFieldType.days(), DateTimeFieldType.millisOfDay().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millisOfDay(), DateTimeFieldType.millisOfDay().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millisOfDay().isSupported(), DateTimeFieldType.millisOfDay().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.millisOfDay());
    }

    public void test_millisOfSecond() throws Exception {
        TestCase.assertEquals(DateTimeFieldType.millisOfSecond(), DateTimeFieldType.millisOfSecond());
        TestCase.assertEquals("millisOfSecond", DateTimeFieldType.millisOfSecond().getName());
        TestCase.assertEquals(DurationFieldType.millis(), DateTimeFieldType.millisOfSecond().getDurationType());
        TestCase.assertEquals(DurationFieldType.seconds(), DateTimeFieldType.millisOfSecond().getRangeDurationType());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millisOfSecond(), DateTimeFieldType.millisOfSecond().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millisOfSecond().isSupported(), DateTimeFieldType.millisOfSecond().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DateTimeFieldType.millisOfSecond());
    }

    public void test_other() throws Exception {
        TestCase.assertEquals(1, DateTimeFieldType.class.getDeclaredClasses().length);
        Class cls = DateTimeFieldType.class.getDeclaredClasses()[0];
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        Constructor con = cls.getDeclaredConstructors()[0];
        Object[] params = new Object[]{ "other", new Byte(((byte) (128))), DurationFieldType.hours(), DurationFieldType.months() };
        con.setAccessible(true);// for Apache Harmony JVM

        DateTimeFieldType type = ((DateTimeFieldType) (con.newInstance(params)));
        TestCase.assertEquals("other", type.getName());
        TestCase.assertSame(DurationFieldType.hours(), type.getDurationType());
        TestCase.assertSame(DurationFieldType.months(), type.getRangeDurationType());
        try {
            type.getField(CopticChronology.getInstanceUTC());
            TestCase.fail();
        } catch (InternalError ex) {
        }
        DateTimeFieldType result = doSerialization(type);
        TestCase.assertEquals(type.getName(), result.getName());
        TestCase.assertNotSame(type, result);
    }
}

