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
 * This class is a Junit unit test for DurationFieldType.
 *
 * @author Stephen Colebourne
 */
public class TestDurationFieldType extends TestCase {
    public TestDurationFieldType(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void test_eras() throws Exception {
        TestCase.assertEquals(DurationFieldType.eras(), DurationFieldType.eras());
        TestCase.assertEquals("eras", DurationFieldType.eras().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().eras(), DurationFieldType.eras().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().eras().isSupported(), DurationFieldType.eras().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.eras());
    }

    public void test_centuries() throws Exception {
        TestCase.assertEquals(DurationFieldType.centuries(), DurationFieldType.centuries());
        TestCase.assertEquals("centuries", DurationFieldType.centuries().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().centuries(), DurationFieldType.centuries().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().centuries().isSupported(), DurationFieldType.centuries().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.centuries());
    }

    public void test_years() throws Exception {
        TestCase.assertEquals(DurationFieldType.years(), DurationFieldType.years());
        TestCase.assertEquals("years", DurationFieldType.years().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().years(), DurationFieldType.years().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().years().isSupported(), DurationFieldType.years().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.years());
    }

    public void test_months() throws Exception {
        TestCase.assertEquals(DurationFieldType.months(), DurationFieldType.months());
        TestCase.assertEquals("months", DurationFieldType.months().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().months(), DurationFieldType.months().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().months().isSupported(), DurationFieldType.months().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.months());
    }

    public void test_weekyears() throws Exception {
        TestCase.assertEquals(DurationFieldType.weekyears(), DurationFieldType.weekyears());
        TestCase.assertEquals("weekyears", DurationFieldType.weekyears().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyears(), DurationFieldType.weekyears().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weekyears().isSupported(), DurationFieldType.weekyears().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.weekyears());
    }

    public void test_weeks() throws Exception {
        TestCase.assertEquals(DurationFieldType.weeks(), DurationFieldType.weeks());
        TestCase.assertEquals("weeks", DurationFieldType.weeks().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weeks(), DurationFieldType.weeks().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().weeks().isSupported(), DurationFieldType.weeks().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.weeks());
    }

    public void test_days() throws Exception {
        TestCase.assertEquals(DurationFieldType.days(), DurationFieldType.days());
        TestCase.assertEquals("days", DurationFieldType.days().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().days(), DurationFieldType.days().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().days().isSupported(), DurationFieldType.days().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.days());
    }

    public void test_halfdays() throws Exception {
        TestCase.assertEquals(DurationFieldType.halfdays(), DurationFieldType.halfdays());
        TestCase.assertEquals("halfdays", DurationFieldType.halfdays().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().halfdays(), DurationFieldType.halfdays().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().halfdays().isSupported(), DurationFieldType.halfdays().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.halfdays());
    }

    public void test_hours() throws Exception {
        TestCase.assertEquals(DurationFieldType.hours(), DurationFieldType.hours());
        TestCase.assertEquals("hours", DurationFieldType.hours().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hours(), DurationFieldType.hours().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().hours().isSupported(), DurationFieldType.hours().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.hours());
    }

    public void test_minutes() throws Exception {
        TestCase.assertEquals(DurationFieldType.minutes(), DurationFieldType.minutes());
        TestCase.assertEquals("minutes", DurationFieldType.minutes().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minutes(), DurationFieldType.minutes().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().minutes().isSupported(), DurationFieldType.minutes().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.minutes());
    }

    public void test_seconds() throws Exception {
        TestCase.assertEquals(DurationFieldType.seconds(), DurationFieldType.seconds());
        TestCase.assertEquals("seconds", DurationFieldType.seconds().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().seconds(), DurationFieldType.seconds().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().seconds().isSupported(), DurationFieldType.seconds().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.seconds());
    }

    public void test_millis() throws Exception {
        TestCase.assertEquals(DurationFieldType.millis(), DurationFieldType.millis());
        TestCase.assertEquals("millis", DurationFieldType.millis().getName());
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millis(), DurationFieldType.millis().getField(CopticChronology.getInstanceUTC()));
        TestCase.assertEquals(CopticChronology.getInstanceUTC().millis().isSupported(), DurationFieldType.millis().isSupported(CopticChronology.getInstanceUTC()));
        assertSerialization(DurationFieldType.millis());
    }

    public void test_other() throws Exception {
        TestCase.assertEquals(1, DurationFieldType.class.getDeclaredClasses().length);
        Class cls = DurationFieldType.class.getDeclaredClasses()[0];
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        Constructor con = cls.getDeclaredConstructors()[0];
        Object[] params = new Object[]{ "other", new Byte(((byte) (128))) };
        DurationFieldType type = ((DurationFieldType) (con.newInstance(params)));
        TestCase.assertEquals("other", type.getName());
        try {
            type.getField(CopticChronology.getInstanceUTC());
            TestCase.fail();
        } catch (InternalError ex) {
        }
        DurationFieldType result = doSerialization(type);
        TestCase.assertEquals(type.getName(), result.getName());
        TestCase.assertNotSame(type, result);
    }
}

