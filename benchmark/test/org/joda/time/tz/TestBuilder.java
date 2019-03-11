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
package org.joda.time.tz;


import java.io.IOException;
import junit.framework.TestCase;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


/**
 * Test cases for DateTimeZoneBuilder.
 *
 * @author Brian S O'Neill
 */
public class TestBuilder extends TestCase {
    static final DateTimeFormatter OFFSET_FORMATTER = new DateTimeFormatterBuilder().appendTimeZoneOffset(null, true, 2, 4).toFormatter();

    // Each row is {transition, nameKey, standardOffset, offset}
    static final String[][] AMERICA_LOS_ANGELES_DATA = new String[][]{ new String[]{ null, "LMT", "-07:52:58", "-07:52:58" }, new String[]{ "1883-11-18T19:52:58.000Z", "PST", "-08:00", "-08:00" }, new String[]{ "1918-03-31T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1918-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1919-03-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1919-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1942-02-09T02:00:00.000-08:00", "PWT", "-08:00", "-07:00" }, new String[]{ "1945-08-14T23:00:00.000Z", "PPT", "-08:00", "-07:00" }, new String[]{ "1945-09-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1948-03-14T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1949-01-01T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1950-04-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1950-09-24T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1951-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1951-09-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1952-04-27T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1952-09-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1953-04-26T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1953-09-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1954-04-25T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1954-09-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1955-04-24T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1955-09-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1956-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1956-09-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1957-04-28T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1957-09-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1958-04-27T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1958-09-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1959-04-26T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1959-09-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1960-04-24T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1960-09-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1961-04-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1961-09-24T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1962-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1962-10-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1963-04-28T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1963-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1964-04-26T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1964-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1965-04-25T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1965-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1966-04-24T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1966-10-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1967-04-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1967-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1968-04-28T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1968-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1969-04-27T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1969-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1970-04-26T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1970-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1971-04-25T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1971-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1972-04-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1972-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1973-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1973-10-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1974-01-06T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1974-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1975-02-23T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1975-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1976-04-25T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1976-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1977-04-24T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1977-10-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1978-04-30T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1978-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1979-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1979-10-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1980-04-27T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1980-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1981-04-26T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1981-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1982-04-25T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1982-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1983-04-24T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1983-10-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1984-04-29T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1984-10-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1985-04-28T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1985-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1986-04-27T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1986-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1987-04-05T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1987-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1988-04-03T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1988-10-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1989-04-02T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1989-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1990-04-01T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1990-10-28T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1991-04-07T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1991-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1992-04-05T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1992-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1993-04-04T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1993-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1994-04-03T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1994-10-30T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1995-04-02T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1995-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1996-04-07T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1996-10-27T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1997-04-06T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1997-10-26T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1998-04-05T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1998-10-25T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "1999-04-04T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "1999-10-31T02:00:00.000-07:00", "PST", "-08:00", "-08:00" }, new String[]{ "2000-04-02T02:00:00.000-08:00", "PDT", "-08:00", "-07:00" }, new String[]{ "2000-10-29T02:00:00.000-07:00", "PST", "-08:00", "-08:00" } };

    private DateTimeZone originalDateTimeZone = null;

    public TestBuilder(String name) {
        super(name);
    }

    public void testID() {
        DateTimeZone tz = TestBuilder.buildAmericaLosAngeles();
        TestCase.assertEquals("America/Los_Angeles", tz.getID());
        TestCase.assertEquals(false, tz.isFixed());
    }

    public void testForwardTransitions() {
        DateTimeZone tz = TestBuilder.buildAmericaLosAngeles();
        TestBuilder.testForwardTransitions(tz, TestBuilder.AMERICA_LOS_ANGELES_DATA);
    }

    public void testReverseTransitions() {
        DateTimeZone tz = TestBuilder.buildAmericaLosAngeles();
        TestBuilder.testReverseTransitions(tz, TestBuilder.AMERICA_LOS_ANGELES_DATA);
    }

    public void testSerialization() throws IOException {
        DateTimeZone tz = TestBuilder.testSerialization(TestBuilder.buildAmericaLosAngelesBuilder(), "America/Los_Angeles");
        TestCase.assertEquals(false, tz.isFixed());
        TestBuilder.testForwardTransitions(tz, TestBuilder.AMERICA_LOS_ANGELES_DATA);
        TestBuilder.testReverseTransitions(tz, TestBuilder.AMERICA_LOS_ANGELES_DATA);
    }

    public void testFixed() throws IOException {
        DateTimeZoneBuilder builder = new DateTimeZoneBuilder().setStandardOffset(3600000).setFixedSavings("LMT", 0);
        DateTimeZone tz = builder.toDateTimeZone("Test", true);
        for (int i = 0; i < 2; i++) {
            TestCase.assertEquals("Test", tz.getID());
            TestCase.assertEquals(true, tz.isFixed());
            TestCase.assertEquals(3600000, tz.getOffset(0));
            TestCase.assertEquals(3600000, tz.getStandardOffset(0));
            TestCase.assertEquals(0, tz.nextTransition(0));
            TestCase.assertEquals(0, tz.previousTransition(0));
            tz = TestBuilder.testSerialization(builder, "Test");
        }
    }
}

