/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLongArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;


/**
 * Unit tests {@link org.apache.commons.lang3.time.FastDateFormat}.
 *
 * @since 2.0
 */
public class FastDateFormatTest {
    /* Only the cache methods need to be tested here.
    The print methods are tested by {@link FastDateFormat_PrinterTest}
    and the parse methods are tested by {@link FastDateFormat_ParserTest}
     */
    @Test
    public void test_getInstance() {
        final FastDateFormat format1 = FastDateFormat.getInstance();
        final FastDateFormat format2 = FastDateFormat.getInstance();
        Assertions.assertSame(format1, format2);
    }

    @Test
    public void test_getInstance_String() {
        final FastDateFormat format1 = FastDateFormat.getInstance("MM/DD/yyyy");
        final FastDateFormat format2 = FastDateFormat.getInstance("MM-DD-yyyy");
        final FastDateFormat format3 = FastDateFormat.getInstance("MM-DD-yyyy");
        Assertions.assertNotSame(format1, format2);
        Assertions.assertSame(format2, format3);
        Assertions.assertEquals("MM/DD/yyyy", format1.getPattern());
        Assertions.assertEquals(TimeZone.getDefault(), format1.getTimeZone());
        Assertions.assertEquals(TimeZone.getDefault(), format2.getTimeZone());
    }

    @DefaultLocale(language = "en", country = "US")
    @DefaultTimeZone("America/New_York")
    @Test
    public void test_getInstance_String_TimeZone() {
        final FastDateFormat format1 = FastDateFormat.getInstance("MM/DD/yyyy", TimeZone.getTimeZone("Atlantic/Reykjavik"));
        final FastDateFormat format2 = FastDateFormat.getInstance("MM/DD/yyyy");
        final FastDateFormat format3 = FastDateFormat.getInstance("MM/DD/yyyy", TimeZone.getDefault());
        final FastDateFormat format4 = FastDateFormat.getInstance("MM/DD/yyyy", TimeZone.getDefault());
        final FastDateFormat format5 = FastDateFormat.getInstance("MM-DD-yyyy", TimeZone.getDefault());
        final FastDateFormat format6 = FastDateFormat.getInstance("MM-DD-yyyy");
        Assertions.assertNotSame(format1, format2);
        Assertions.assertEquals(TimeZone.getTimeZone("Atlantic/Reykjavik"), format1.getTimeZone());
        Assertions.assertEquals(TimeZone.getDefault(), format2.getTimeZone());
        Assertions.assertSame(format3, format4);
        Assertions.assertNotSame(format3, format5);
        Assertions.assertNotSame(format4, format6);
    }

    @DefaultLocale(language = "en", country = "US")
    @Test
    public void test_getInstance_String_Locale() {
        final FastDateFormat format1 = FastDateFormat.getInstance("MM/DD/yyyy", Locale.GERMANY);
        final FastDateFormat format2 = FastDateFormat.getInstance("MM/DD/yyyy");
        final FastDateFormat format3 = FastDateFormat.getInstance("MM/DD/yyyy", Locale.GERMANY);
        Assertions.assertNotSame(format1, format2);
        Assertions.assertSame(format1, format3);
        Assertions.assertEquals(Locale.GERMANY, format1.getLocale());
    }

    @DefaultLocale(language = "en", country = "US")
    @Test
    public void test_changeDefault_Locale_DateInstance() {
        final FastDateFormat format1 = FastDateFormat.getDateInstance(FastDateFormat.FULL, Locale.GERMANY);
        final FastDateFormat format2 = FastDateFormat.getDateInstance(FastDateFormat.FULL);
        Locale.setDefault(Locale.GERMANY);
        final FastDateFormat format3 = FastDateFormat.getDateInstance(FastDateFormat.FULL);
        Assertions.assertSame(Locale.GERMANY, format1.getLocale());
        Assertions.assertEquals(Locale.US, format2.getLocale());
        Assertions.assertSame(Locale.GERMANY, format3.getLocale());
        Assertions.assertNotSame(format1, format2);
        Assertions.assertNotSame(format2, format3);
    }

    @DefaultLocale(language = "en", country = "US")
    @Test
    public void test_changeDefault_Locale_DateTimeInstance() {
        final FastDateFormat format1 = FastDateFormat.getDateTimeInstance(FastDateFormat.FULL, FastDateFormat.FULL, Locale.GERMANY);
        final FastDateFormat format2 = FastDateFormat.getDateTimeInstance(FastDateFormat.FULL, FastDateFormat.FULL);
        Locale.setDefault(Locale.GERMANY);
        final FastDateFormat format3 = FastDateFormat.getDateTimeInstance(FastDateFormat.FULL, FastDateFormat.FULL);
        Assertions.assertSame(Locale.GERMANY, format1.getLocale());
        Assertions.assertEquals(Locale.US, format2.getLocale());
        Assertions.assertSame(Locale.GERMANY, format3.getLocale());
        Assertions.assertNotSame(format1, format2);
        Assertions.assertNotSame(format2, format3);
    }

    @DefaultLocale(language = "en", country = "US")
    @DefaultTimeZone("America/New_York")
    @Test
    public void test_getInstance_String_TimeZone_Locale() {
        final FastDateFormat format1 = FastDateFormat.getInstance("MM/DD/yyyy", TimeZone.getTimeZone("Atlantic/Reykjavik"), Locale.GERMANY);
        final FastDateFormat format2 = FastDateFormat.getInstance("MM/DD/yyyy", Locale.GERMANY);
        final FastDateFormat format3 = FastDateFormat.getInstance("MM/DD/yyyy", TimeZone.getDefault(), Locale.GERMANY);
        Assertions.assertNotSame(format1, format2);
        Assertions.assertEquals(TimeZone.getTimeZone("Atlantic/Reykjavik"), format1.getTimeZone());
        Assertions.assertEquals(TimeZone.getDefault(), format2.getTimeZone());
        Assertions.assertEquals(TimeZone.getDefault(), format3.getTimeZone());
        Assertions.assertEquals(Locale.GERMANY, format1.getLocale());
        Assertions.assertEquals(Locale.GERMANY, format2.getLocale());
        Assertions.assertEquals(Locale.GERMANY, format3.getLocale());
    }

    @Test
    public void testCheckDefaults() {
        final FastDateFormat format = FastDateFormat.getInstance();
        final FastDateFormat medium = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT);
        Assertions.assertEquals(medium, format);
        final SimpleDateFormat sdf = new SimpleDateFormat();
        Assertions.assertEquals(sdf.toPattern(), format.getPattern());
        Assertions.assertEquals(Locale.getDefault(), format.getLocale());
        Assertions.assertEquals(TimeZone.getDefault(), format.getTimeZone());
    }

    @Test
    public void testCheckDifferingStyles() {
        final FastDateFormat shortShort = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT, Locale.US);
        final FastDateFormat shortLong = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.LONG, Locale.US);
        final FastDateFormat longShort = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.SHORT, Locale.US);
        final FastDateFormat longLong = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.LONG, Locale.US);
        Assertions.assertNotEquals(shortShort, shortLong);
        Assertions.assertNotEquals(shortShort, longShort);
        Assertions.assertNotEquals(shortShort, longLong);
        Assertions.assertNotEquals(shortLong, longShort);
        Assertions.assertNotEquals(shortLong, longLong);
        Assertions.assertNotEquals(longShort, longLong);
    }

    @Test
    public void testDateDefaults() {
        Assertions.assertEquals(FastDateFormat.getDateInstance(FastDateFormat.LONG, Locale.CANADA), FastDateFormat.getDateInstance(FastDateFormat.LONG, TimeZone.getDefault(), Locale.CANADA));
        Assertions.assertEquals(FastDateFormat.getDateInstance(FastDateFormat.LONG, TimeZone.getTimeZone("America/New_York")), FastDateFormat.getDateInstance(FastDateFormat.LONG, TimeZone.getTimeZone("America/New_York"), Locale.getDefault()));
        Assertions.assertEquals(FastDateFormat.getDateInstance(FastDateFormat.LONG), FastDateFormat.getDateInstance(FastDateFormat.LONG, TimeZone.getDefault(), Locale.getDefault()));
    }

    @Test
    public void testTimeDefaults() {
        Assertions.assertEquals(FastDateFormat.getTimeInstance(FastDateFormat.LONG, Locale.CANADA), FastDateFormat.getTimeInstance(FastDateFormat.LONG, TimeZone.getDefault(), Locale.CANADA));
        Assertions.assertEquals(FastDateFormat.getTimeInstance(FastDateFormat.LONG, TimeZone.getTimeZone("America/New_York")), FastDateFormat.getTimeInstance(FastDateFormat.LONG, TimeZone.getTimeZone("America/New_York"), Locale.getDefault()));
        Assertions.assertEquals(FastDateFormat.getTimeInstance(FastDateFormat.LONG), FastDateFormat.getTimeInstance(FastDateFormat.LONG, TimeZone.getDefault(), Locale.getDefault()));
    }

    @Test
    public void testTimeDateDefaults() {
        Assertions.assertEquals(FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM, Locale.CANADA), FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM, TimeZone.getDefault(), Locale.CANADA));
        Assertions.assertEquals(FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM, TimeZone.getTimeZone("America/New_York")), FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM, TimeZone.getTimeZone("America/New_York"), Locale.getDefault()));
        Assertions.assertEquals(FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM), FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.MEDIUM, TimeZone.getDefault(), Locale.getDefault()));
    }

    @Test
    public void testParseSync() throws InterruptedException {
        final String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        final SimpleDateFormat inner = new SimpleDateFormat(pattern);
        final Format sdf = new Format() {
            private static final long serialVersionUID = 1L;

            @Override
            public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
                synchronized(this) {
                    return inner.format(obj, toAppendTo, fieldPosition);
                }
            }

            @Override
            public Object parseObject(final String source, final ParsePosition pos) {
                synchronized(this) {
                    return inner.parseObject(source, pos);
                }
            }
        };
        final AtomicLongArray sdfTime = measureTime(sdf, sdf);
        final Format fdf = FastDateFormat.getInstance(pattern);
        final AtomicLongArray fdfTime = measureTime(fdf, fdf);
        System.out.println((((">>FastDateFormatTest: FastDatePrinter:" + (fdfTime.get(0))) + "  SimpleDateFormat:") + (sdfTime.get(0))));
        System.out.println((((">>FastDateFormatTest: FastDateParser:" + (fdfTime.get(1))) + "  SimpleDateFormat:") + (sdfTime.get(1))));
    }

    private static final int NTHREADS = 10;

    private static final int NROUNDS = 10000;

    /**
     * According to LANG-954 (https://issues.apache.org/jira/browse/LANG-954) this is broken in Android 2.1.
     */
    @Test
    public void testLANG_954() {
        final String pattern = "yyyy-MM-dd'T'";
        FastDateFormat.getInstance(pattern);
    }

    @Test
    public void testLANG_1152() {
        final TimeZone utc = FastTimeZone.getGmtTimeZone();
        final Date date = new Date(Long.MAX_VALUE);
        String dateAsString = FastDateFormat.getInstance("yyyy-MM-dd", utc, Locale.US).format(date);
        Assertions.assertEquals("292278994-08-17", dateAsString);
        dateAsString = FastDateFormat.getInstance("dd/MM/yyyy", utc, Locale.US).format(date);
        Assertions.assertEquals("17/08/292278994", dateAsString);
    }

    @Test
    public void testLANG_1267() {
        FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }
}

