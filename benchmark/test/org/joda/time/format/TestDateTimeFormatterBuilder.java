/**
 * Copyright 2001-2011 Stephen Colebourne
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


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;


/**
 * This class is a Junit unit test for DateTimeFormatterBuilder.
 *
 * @author Stephen Colebourne
 * @author Brian S O'Neill
 */
public class TestDateTimeFormatterBuilder extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEW_YORK = DateTimeZone.forID("America/New_York");

    private static final DateTimeZone LOS_ANGELES = DateTimeZone.forID("America/Los_Angeles");

    private static final DateTimeZone OFFSET_0200 = DateTimeZone.forID("+02:00");

    private static final DateTimeZone OFFSET_023012 = DateTimeZone.forID("+02:30:12");

    public TestDateTimeFormatterBuilder(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void test_toFormatter() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        try {
            bld.toFormatter();
            TestCase.fail();
        } catch (UnsupportedOperationException ex) {
        }
        bld.appendLiteral('X');
        TestCase.assertNotNull(bld.toFormatter());
    }

    public void test_toPrinter() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        try {
            bld.toPrinter();
            TestCase.fail();
        } catch (UnsupportedOperationException ex) {
        }
        bld.appendLiteral('X');
        TestCase.assertNotNull(bld.toPrinter());
    }

    public void test_toParser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        try {
            bld.toParser();
            TestCase.fail();
        } catch (UnsupportedOperationException ex) {
        }
        bld.appendLiteral('X');
        TestCase.assertNotNull(bld.toParser());
    }

    // -----------------------------------------------------------------------
    public void test_canBuildFormatter() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        TestCase.assertEquals(false, bld.canBuildFormatter());
        bld.appendLiteral('X');
        TestCase.assertEquals(true, bld.canBuildFormatter());
    }

    public void test_canBuildPrinter() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        TestCase.assertEquals(false, bld.canBuildPrinter());
        bld.appendLiteral('X');
        TestCase.assertEquals(true, bld.canBuildPrinter());
    }

    public void test_canBuildParser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        TestCase.assertEquals(false, bld.canBuildParser());
        bld.appendLiteral('X');
        TestCase.assertEquals(true, bld.canBuildParser());
    }

    // -----------------------------------------------------------------------
    public void test_append_Formatter() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimeFormatter f = bld.toFormatter();
        DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
        bld2.appendLiteral('X');
        bld2.append(f);
        bld2.appendLiteral('Z');
        TestCase.assertEquals("XYZ", bld2.toFormatter().print(0L));
    }

    // -----------------------------------------------------------------------
    public void test_append_Printer() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimePrinter p = bld.toPrinter();
        DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
        bld2.appendLiteral('X');
        bld2.append(p);
        bld2.appendLiteral('Z');
        DateTimeFormatter f = bld2.toFormatter();
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(false, f.isParser());
        TestCase.assertEquals("XYZ", f.print(0L));
    }

    public void test_append_nullPrinter() {
        try {
            DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
            bld2.append(((DateTimePrinter) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void test_append_Parser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimeParser p = bld.toParser();
        DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
        bld2.appendLiteral('X');
        bld2.append(p);
        bld2.appendLiteral('Z');
        DateTimeFormatter f = bld2.toFormatter();
        TestCase.assertEquals(false, f.isPrinter());
        TestCase.assertEquals(true, f.isParser());
        TestCase.assertEquals(0, f.withZoneUTC().parseMillis("XYZ"));
    }

    public void test_append_nullParser() {
        try {
            DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
            bld2.append(((DateTimeParser) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void test_append_Printer_nullParser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimePrinter p = bld.toPrinter();
        try {
            DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
            bld2.append(p, ((DateTimeParser) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void test_append_nullPrinter_Parser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimeParser p = bld.toParser();
        try {
            DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
            bld2.append(((DateTimePrinter) (null)), p);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void test_appendOptional_Parser() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendLiteral('Y');
        DateTimeParser p = bld.toParser();
        DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
        bld2.appendLiteral('X');
        bld2.appendOptional(p);
        bld2.appendLiteral('Z');
        DateTimeFormatter f = bld2.toFormatter();
        TestCase.assertEquals(false, f.isPrinter());
        TestCase.assertEquals(true, f.isParser());
        TestCase.assertEquals(0, f.withZoneUTC().parseMillis("XYZ"));
    }

    public void test_appendOptional_nullParser() {
        try {
            DateTimeFormatterBuilder bld2 = new DateTimeFormatterBuilder();
            bld2.appendOptional(((DateTimeParser) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void test_appendFixedDecimal() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendFixedDecimal(DateTimeFieldType.year(), 4);
        DateTimeFormatter f = bld.toFormatter();
        TestCase.assertEquals("2007", f.print(new DateTime("2007-01-01")));
        TestCase.assertEquals("0123", f.print(new DateTime("123-01-01")));
        TestCase.assertEquals("0001", f.print(new DateTime("1-2-3")));
        TestCase.assertEquals("99999", f.print(new DateTime("99999-2-3")));
        TestCase.assertEquals("-0099", f.print(new DateTime("-99-2-3")));
        TestCase.assertEquals("0000", f.print(new DateTime("0-2-3")));
        TestCase.assertEquals(2001, f.parseDateTime("2001").getYear());
        try {
            f.parseDateTime("-2001");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            f.parseDateTime("200");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            f.parseDateTime("20016");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
        bld = new DateTimeFormatterBuilder();
        bld.appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2);
        bld.appendLiteral(':');
        bld.appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2);
        bld.appendLiteral(':');
        bld.appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2);
        f = bld.toFormatter();
        TestCase.assertEquals("01:02:34", f.print(new DateTime("T1:2:34")));
        DateTime dt = f.parseDateTime("01:02:34");
        TestCase.assertEquals(1, dt.getHourOfDay());
        TestCase.assertEquals(2, dt.getMinuteOfHour());
        TestCase.assertEquals(34, dt.getSecondOfMinute());
        try {
            f.parseDateTime("0145:02:34");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            f.parseDateTime("01:0:34");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_appendFixedSignedDecimal() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendFixedSignedDecimal(DateTimeFieldType.year(), 4);
        DateTimeFormatter f = bld.toFormatter();
        TestCase.assertEquals("2007", f.print(new DateTime("2007-01-01")));
        TestCase.assertEquals("0123", f.print(new DateTime("123-01-01")));
        TestCase.assertEquals("0001", f.print(new DateTime("1-2-3")));
        TestCase.assertEquals("99999", f.print(new DateTime("99999-2-3")));
        TestCase.assertEquals("-0099", f.print(new DateTime("-99-2-3")));
        TestCase.assertEquals("0000", f.print(new DateTime("0-2-3")));
        TestCase.assertEquals(2001, f.parseDateTime("2001").getYear());
        TestCase.assertEquals((-2001), f.parseDateTime("-2001").getYear());
        TestCase.assertEquals(2001, f.parseDateTime("+2001").getYear());
        try {
            f.parseDateTime("20016");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_appendTimeZoneOffset_parse() {
        for (int i = 1; i <= 4; i++) {
            for (int j = i; j <= 4; j++) {
                DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
                bld.appendTimeZoneOffset("Z", true, i, j);
                DateTimeFormatter f = bld.toFormatter();
                // parse
                TestCase.assertEquals(TestDateTimeFormatterBuilder.OFFSET_0200, f.withOffsetParsed().parseDateTime("+02").getZone());
                TestCase.assertEquals(TestDateTimeFormatterBuilder.OFFSET_0200, f.withOffsetParsed().parseDateTime("+02:00").getZone());
                TestCase.assertEquals(TestDateTimeFormatterBuilder.OFFSET_0200, f.withOffsetParsed().parseDateTime("+02:00:00").getZone());
                TestCase.assertEquals(TestDateTimeFormatterBuilder.OFFSET_0200, f.withOffsetParsed().parseDateTime("+02:00:00.000").getZone());
            }
        }
    }

    public void test_appendTimeZoneOffset_print_min1max1() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 1, 1).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_print_min1max2() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 1, 2).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02:30", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_print_min1max3() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 1, 3).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02:30:12", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_print_min2max2() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 2).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02:00", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02:30", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_print_min2max3() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 3).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02:00", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02:30:12", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_print_min3max3() throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 3, 3).toFormatter();
        TestDateTimeFormatterBuilder.assertPrint("+02:00:00", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_0200));
        TestDateTimeFormatterBuilder.assertPrint("+02:30:12", f, new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.OFFSET_023012));
    }

    public void test_appendTimeZoneOffset_invalidText() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendTimeZoneOffset("Z", true, 1, 1);
        DateTimeFormatter f = bld.toFormatter();
        try {
            f.parseDateTime("Nonsense");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void test_appendTimeZoneOffset_zeroMinInvalid() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        try {
            bld.appendTimeZoneOffset("Z", true, 0, 2);
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_appendTimeZoneId() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        bld.appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        TestCase.assertEquals("Asia/Tokyo", f.print(new DateTime(2007, 3, 4, 0, 0, 0, TestDateTimeFormatterBuilder.TOKYO)));
        TestCase.assertEquals(TestDateTimeFormatterBuilder.TOKYO, f.parseDateTime("Asia/Tokyo").getZone());
        try {
            f.parseDateTime("Nonsense");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void test_printParseZoneTokyo() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 Asia/Tokyo", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Asia/Tokyo"));
    }

    public void test_printParseZoneParis() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.PARIS);
        TestCase.assertEquals("2007-03-04 12:30 Europe/Paris", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Europe/Paris"));
        TestCase.assertEquals(dt, f.withOffsetParsed().parseDateTime("2007-03-04 12:30 Europe/Paris"));
    }

    public void test_printParseZoneDawson() {
        // clashes with shorter Dawson
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Dawson"));
        TestCase.assertEquals("2007-03-04 12:30 America/Dawson", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Dawson"));
    }

    public void test_printParseZoneDawson_suffix() {
        // clashes with shorter Dawson
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId().appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Dawson"));
        TestCase.assertEquals("2007-03-04 12:30 America/Dawson]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Dawson]"));
    }

    public void test_printParseZoneDawsonCreek() {
        // clashes with shorter Dawson
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Dawson_Creek"));
        TestCase.assertEquals("2007-03-04 12:30 America/Dawson_Creek", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek"));
    }

    public void test_printParseZoneDawsonCreek_suffix() {
        // clashes with shorter Dawson
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId().appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Dawson_Creek"));
        TestCase.assertEquals("2007-03-04 12:30 America/Dawson_Creek]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek]"));
    }

    public void test_printParseZoneEtcGMT() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ");
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT"));
    }

    public void test_printParseZoneEtcGMT_suffix() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ").appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT]"));
    }

    public void test_printParseZoneGMT() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ");
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("GMT"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 GMT"));
    }

    public void test_printParseZoneGMT_suffix() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ").appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("GMT"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 GMT]"));
    }

    public void test_printParseZoneEtcGMT1() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ");
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT+1"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT+1", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT+1"));
    }

    public void test_printParseZoneEtcGMT1_suffix() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ").appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT+1"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT+1]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT+1]"));
    }

    public void test_printParseZoneEtcGMT10() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ");
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT+10"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT+10", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT+10"));
    }

    public void test_printParseZoneEtcGMT10_suffix() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ").appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("Etc/GMT+10"));
        TestCase.assertEquals("2007-03-04 12:30 Etc/GMT+10]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 Etc/GMT+10]"));
    }

    public void test_printParseZoneMET() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ");
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("MET"));
        TestCase.assertEquals("2007-03-04 12:30 MET", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 MET"));
    }

    public void test_printParseZoneMET_suffix() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ZZZ").appendLiteral(']');
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("MET"));
        TestCase.assertEquals("2007-03-04 12:30 MET]", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 MET]"));
    }

    public void test_printParseZoneBahiaBanderas() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Bahia_Banderas"));
        TestCase.assertEquals("2007-03-04 12:30 America/Bahia_Banderas", f.print(dt));
        TestCase.assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Bahia_Banderas"));
    }

    public void test_printParseOffset() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2);
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 +09:00", f.print(dt));
        TestCase.assertEquals(dt.withZone(DateTimeZone.getDefault()), f.parseDateTime("2007-03-04 12:30 +09:00"));
        TestCase.assertEquals(dt, f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseDateTime("2007-03-04 12:30 +09:00"));
        TestCase.assertEquals(dt.withZone(DateTimeZone.forOffsetHours(9)), f.withOffsetParsed().parseDateTime("2007-03-04 12:30 +09:00"));
    }

    public void test_printParseOffsetAndZone() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2).appendLiteral(' ').appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 +09:00 Asia/Tokyo", f.print(dt));
        TestCase.assertEquals(dt, f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseDateTime("2007-03-04 12:30 +09:00 Asia/Tokyo"));
        TestCase.assertEquals(dt.withZone(TestDateTimeFormatterBuilder.PARIS), f.withZone(TestDateTimeFormatterBuilder.PARIS).parseDateTime("2007-03-04 12:30 +09:00 Asia/Tokyo"));
        TestCase.assertEquals(dt.withZone(DateTimeZone.forOffsetHours(9)), f.withOffsetParsed().parseDateTime("2007-03-04 12:30 +09:00 Asia/Tokyo"));
    }

    public void test_parseWrongOffset() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2);
        DateTimeFormatter f = bld.toFormatter();
        DateTime expected = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forOffsetHours(7));
        // parses offset time then adjusts to requested zone
        TestCase.assertEquals(expected.withZone(TestDateTimeFormatterBuilder.TOKYO), f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseDateTime("2007-03-04 12:30 +07:00"));
        // parses offset time returning offset zone
        TestCase.assertEquals(expected, f.withOffsetParsed().parseDateTime("2007-03-04 12:30 +07:00"));
        // parses offset time then converts to default zone
        TestCase.assertEquals(expected.withZone(DateTimeZone.getDefault()), f.parseDateTime("2007-03-04 12:30 +07:00"));
    }

    public void test_parseWrongOffsetAndZone() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2).appendLiteral(' ').appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime expected = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forOffsetHours(7));
        // parses offset time then adjusts to parsed zone
        TestCase.assertEquals(expected.withZone(TestDateTimeFormatterBuilder.TOKYO), f.parseDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
        // parses offset time then adjusts to requested zone
        TestCase.assertEquals(expected.withZone(TestDateTimeFormatterBuilder.TOKYO), f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
        // parses offset time returning offset zone (ignores zone)
        TestCase.assertEquals(expected, f.withOffsetParsed().parseDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
    }

    // -----------------------------------------------------------------------
    public void test_localPrintParseZoneTokyo() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 Asia/Tokyo", f.print(dt));
        LocalDateTime expected = new LocalDateTime(2007, 3, 4, 12, 30);
        TestCase.assertEquals(expected, f.parseLocalDateTime("2007-03-04 12:30 Asia/Tokyo"));
    }

    public void test_localPrintParseOffset() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2);
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 +09:00", f.print(dt));
        LocalDateTime expected = new LocalDateTime(2007, 3, 4, 12, 30);
        TestCase.assertEquals(expected, f.parseLocalDateTime("2007-03-04 12:30 +09:00"));
        TestCase.assertEquals(expected, f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseLocalDateTime("2007-03-04 12:30 +09:00"));
        TestCase.assertEquals(expected, f.withOffsetParsed().parseLocalDateTime("2007-03-04 12:30 +09:00"));
    }

    public void test_localPrintParseOffsetAndZone() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2).appendLiteral(' ').appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, TestDateTimeFormatterBuilder.TOKYO);
        TestCase.assertEquals("2007-03-04 12:30 +09:00 Asia/Tokyo", f.print(dt));
        LocalDateTime expected = new LocalDateTime(2007, 3, 4, 12, 30);
        TestCase.assertEquals(expected, f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseLocalDateTime("2007-03-04 12:30 +09:00 Asia/Tokyo"));
        TestCase.assertEquals(expected, f.withZone(TestDateTimeFormatterBuilder.PARIS).parseLocalDateTime("2007-03-04 12:30 +09:00 Asia/Tokyo"));
    }

    public void test_localParseWrongOffsetAndZone() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneOffset("Z", true, 2, 2).appendLiteral(' ').appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        LocalDateTime expected = new LocalDateTime(2007, 3, 4, 12, 30);
        // parses offset time then adjusts to parsed zone
        TestCase.assertEquals(expected, f.parseLocalDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
        // parses offset time then adjusts to requested zone
        TestCase.assertEquals(expected, f.withZone(TestDateTimeFormatterBuilder.TOKYO).parseLocalDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
        // parses offset time returning offset zone (ignores zone)
        TestCase.assertEquals(expected, f.withOffsetParsed().parseLocalDateTime("2007-03-04 12:30 +07:00 Asia/Tokyo"));
    }

    // -----------------------------------------------------------------------
    public void test_printParseShortName() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneShortName();
        DateTimeFormatter f = bld.toFormatter().withLocale(Locale.ENGLISH);
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(false, f.isParser());
        DateTime dt1 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-01-04 12:30 GMT", f.print(dt1));
        DateTime dt2 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-07-04 12:30 BST", f.print(dt2));
        try {
            f.parseDateTime("2007-03-04 12:30 GMT");
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    public void test_printParseShortNameWithLookup() {
        Map<String, DateTimeZone> lookup = new LinkedHashMap<String, DateTimeZone>();
        lookup.put("GMT", TestDateTimeFormatterBuilder.LONDON);
        lookup.put("BST", TestDateTimeFormatterBuilder.LONDON);
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneShortName(lookup);
        DateTimeFormatter f = bld.toFormatter().withLocale(Locale.ENGLISH);
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(true, f.isParser());
        DateTime dt1 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-01-04 12:30 GMT", f.print(dt1));
        DateTime dt2 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-07-04 12:30 BST", f.print(dt2));
        TestCase.assertEquals(dt1, f.parseDateTime("2011-01-04 12:30 GMT"));
        TestCase.assertEquals(dt2, f.parseDateTime("2011-07-04 12:30 BST"));
        try {
            f.parseDateTime("2007-03-04 12:30 EST");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void test_printParseShortNameWithAutoLookup() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneShortName(null);
        DateTimeFormatter f = bld.toFormatter().withLocale(Locale.ENGLISH);
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(true, f.isParser());
        DateTime dt1 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.NEW_YORK);
        TestCase.assertEquals("2011-01-04 12:30 EST", f.print(dt1));
        DateTime dt2 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.NEW_YORK);
        TestCase.assertEquals("2011-07-04 12:30 EDT", f.print(dt2));
        DateTime dt3 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LOS_ANGELES);
        TestCase.assertEquals("2011-01-04 12:30 PST", f.print(dt3));
        DateTime dt4 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LOS_ANGELES);
        TestCase.assertEquals("2011-07-04 12:30 PDT", f.print(dt4));
        DateTime dt5 = new DateTime(2011, 7, 4, 12, 30, 0, DateTimeZone.UTC);
        TestCase.assertEquals("2011-07-04 12:30 UTC", f.print(dt5));
        TestCase.assertEquals((((dt1.getZone()) + " ") + (f.parseDateTime("2011-01-04 12:30 EST").getZone())), dt1, f.parseDateTime("2011-01-04 12:30 EST"));
        TestCase.assertEquals(dt2, f.parseDateTime("2011-07-04 12:30 EDT"));
        TestCase.assertEquals(dt3, f.parseDateTime("2011-01-04 12:30 PST"));
        TestCase.assertEquals(dt4, f.parseDateTime("2011-07-04 12:30 PDT"));
        TestCase.assertEquals(dt5, f.parseDateTime("2011-07-04 12:30 UT"));
        TestCase.assertEquals(dt5, f.parseDateTime("2011-07-04 12:30 UTC"));
        try {
            f.parseDateTime("2007-03-04 12:30 PPP");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_printParseLongName() {
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneName();
        DateTimeFormatter f = bld.toFormatter().withLocale(Locale.ENGLISH);
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(false, f.isParser());
        DateTime dt1 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-01-04 12:30 Greenwich Mean Time", f.print(dt1));
        DateTime dt2 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-07-04 12:30 British Summer Time", f.print(dt2));
        try {
            f.parseDateTime("2007-03-04 12:30 GMT");
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    public void test_printParseLongNameWithLookup() {
        Map<String, DateTimeZone> lookup = new LinkedHashMap<String, DateTimeZone>();
        lookup.put("Greenwich Mean Time", TestDateTimeFormatterBuilder.LONDON);
        lookup.put("British Summer Time", TestDateTimeFormatterBuilder.LONDON);
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneName(lookup);
        DateTimeFormatter f = bld.toFormatter().withLocale(Locale.ENGLISH);
        TestCase.assertEquals(true, f.isPrinter());
        TestCase.assertEquals(true, f.isParser());
        DateTime dt1 = new DateTime(2011, 1, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-01-04 12:30 Greenwich Mean Time", f.print(dt1));
        DateTime dt2 = new DateTime(2011, 7, 4, 12, 30, 0, TestDateTimeFormatterBuilder.LONDON);
        TestCase.assertEquals("2011-07-04 12:30 British Summer Time", f.print(dt2));
        TestCase.assertEquals(dt1, f.parseDateTime("2011-01-04 12:30 Greenwich Mean Time"));
        TestCase.assertEquals(dt2, f.parseDateTime("2011-07-04 12:30 British Summer Time"));
        try {
            f.parseDateTime("2007-03-04 12:30 EST");
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }
}

