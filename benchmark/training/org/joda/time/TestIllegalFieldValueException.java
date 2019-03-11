/**
 * Copyright 2001-2013 Stephen Colebourne
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


import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.field.FieldUtils;


/**
 * Tests IllegalFieldValueException by triggering it from other methods.
 *
 * @author Brian S O'Neill
 */
public class TestIllegalFieldValueException extends TestCase {
    public TestIllegalFieldValueException(String name) {
        super(name);
    }

    public void testVerifyValueBounds() {
        try {
            FieldUtils.verifyValueBounds(ISOChronology.getInstance().monthOfYear(), (-5), 1, 31);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.monthOfYear(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("monthOfYear", e.getFieldName());
            TestCase.assertEquals(new Integer((-5)), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("-5", e.getIllegalValueAsString());
            TestCase.assertEquals(new Integer(1), e.getLowerBound());
            TestCase.assertEquals(new Integer(31), e.getUpperBound());
        }
        try {
            FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), 27, 0, 23);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.hourOfDay(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("hourOfDay", e.getFieldName());
            TestCase.assertEquals(new Integer(27), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("27", e.getIllegalValueAsString());
            TestCase.assertEquals(new Integer(0), e.getLowerBound());
            TestCase.assertEquals(new Integer(23), e.getUpperBound());
        }
        try {
            FieldUtils.verifyValueBounds("foo", 1, 2, 3);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(null, e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("foo", e.getFieldName());
            TestCase.assertEquals(new Integer(1), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("1", e.getIllegalValueAsString());
            TestCase.assertEquals(new Integer(2), e.getLowerBound());
            TestCase.assertEquals(new Integer(3), e.getUpperBound());
        }
    }

    public void testSkipDateTimeField() {
        DateTimeField field = new org.joda.time.field.SkipDateTimeField(ISOChronology.getInstanceUTC(), ISOChronology.getInstanceUTC().year(), 1970);
        try {
            field.set(0, 1970);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.year(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("year", e.getFieldName());
            TestCase.assertEquals(new Integer(1970), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("1970", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
    }

    public void testSetText() {
        try {
            ISOChronology.getInstanceUTC().year().set(0, null, Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.year(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("year", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("null", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        try {
            ISOChronology.getInstanceUTC().year().set(0, "nineteen seventy", Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.year(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("year", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals("nineteen seventy", e.getIllegalStringValue());
            TestCase.assertEquals("nineteen seventy", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        try {
            ISOChronology.getInstanceUTC().era().set(0, "long ago", Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.era(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("era", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals("long ago", e.getIllegalStringValue());
            TestCase.assertEquals("long ago", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        try {
            ISOChronology.getInstanceUTC().monthOfYear().set(0, "spring", Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.monthOfYear(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("monthOfYear", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals("spring", e.getIllegalStringValue());
            TestCase.assertEquals("spring", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        try {
            ISOChronology.getInstanceUTC().dayOfWeek().set(0, "yesterday", Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.dayOfWeek(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("dayOfWeek", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals("yesterday", e.getIllegalStringValue());
            TestCase.assertEquals("yesterday", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        try {
            ISOChronology.getInstanceUTC().halfdayOfDay().set(0, "morning", Locale.US);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.halfdayOfDay(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("halfdayOfDay", e.getFieldName());
            TestCase.assertEquals(null, e.getIllegalNumberValue());
            TestCase.assertEquals("morning", e.getIllegalStringValue());
            TestCase.assertEquals("morning", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
    }

    public void testZoneTransition() {
        DateTime dt = new DateTime(2005, 4, 3, 1, 0, 0, 0, DateTimeZone.forID("America/Los_Angeles"));
        try {
            dt.hourOfDay().setCopy(2);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.hourOfDay(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("hourOfDay", e.getFieldName());
            TestCase.assertEquals(new Integer(2), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("2", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
    }

    public void testJulianYearZero() {
        DateTime dt = new DateTime(JulianChronology.getInstanceUTC());
        try {
            dt.year().setCopy(0);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.year(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("year", e.getFieldName());
            TestCase.assertEquals(new Integer(0), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("0", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
    }

    public void testGJCutover() {
        DateTime dt = new DateTime("1582-10-04", GJChronology.getInstanceUTC());
        try {
            dt.dayOfMonth().setCopy(5);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.dayOfMonth(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("dayOfMonth", e.getFieldName());
            TestCase.assertEquals(new Integer(5), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("5", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
        dt = new DateTime("1582-10-15", GJChronology.getInstanceUTC());
        try {
            dt.dayOfMonth().setCopy(14);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
            TestCase.assertEquals(DateTimeFieldType.dayOfMonth(), e.getDateTimeFieldType());
            TestCase.assertEquals(null, e.getDurationFieldType());
            TestCase.assertEquals("dayOfMonth", e.getFieldName());
            TestCase.assertEquals(new Integer(14), e.getIllegalNumberValue());
            TestCase.assertEquals(null, e.getIllegalStringValue());
            TestCase.assertEquals("14", e.getIllegalValueAsString());
            TestCase.assertEquals(null, e.getLowerBound());
            TestCase.assertEquals(null, e.getUpperBound());
        }
    }

    // Test extra constructors not currently called by anything
    public void testOtherConstructors() {
        IllegalFieldValueException e = new IllegalFieldValueException(DurationFieldType.days(), new Integer(1), new Integer(2), new Integer(3));
        TestCase.assertEquals(null, e.getDateTimeFieldType());
        TestCase.assertEquals(DurationFieldType.days(), e.getDurationFieldType());
        TestCase.assertEquals("days", e.getFieldName());
        TestCase.assertEquals(new Integer(1), e.getIllegalNumberValue());
        TestCase.assertEquals(null, e.getIllegalStringValue());
        TestCase.assertEquals("1", e.getIllegalValueAsString());
        TestCase.assertEquals(new Integer(2), e.getLowerBound());
        TestCase.assertEquals(new Integer(3), e.getUpperBound());
        e = new IllegalFieldValueException(DurationFieldType.months(), "five");
        TestCase.assertEquals(null, e.getDateTimeFieldType());
        TestCase.assertEquals(DurationFieldType.months(), e.getDurationFieldType());
        TestCase.assertEquals("months", e.getFieldName());
        TestCase.assertEquals(null, e.getIllegalNumberValue());
        TestCase.assertEquals("five", e.getIllegalStringValue());
        TestCase.assertEquals("five", e.getIllegalValueAsString());
        TestCase.assertEquals(null, e.getLowerBound());
        TestCase.assertEquals(null, e.getUpperBound());
        e = new IllegalFieldValueException("months", "five");
        TestCase.assertEquals(null, e.getDateTimeFieldType());
        TestCase.assertEquals(null, e.getDurationFieldType());
        TestCase.assertEquals("months", e.getFieldName());
        TestCase.assertEquals(null, e.getIllegalNumberValue());
        TestCase.assertEquals("five", e.getIllegalStringValue());
        TestCase.assertEquals("five", e.getIllegalValueAsString());
        TestCase.assertEquals(null, e.getLowerBound());
        TestCase.assertEquals(null, e.getUpperBound());
    }
}

