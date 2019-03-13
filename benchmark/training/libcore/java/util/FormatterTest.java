/**
 * Copyright (C) 2009 The Android Open Source Project
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


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;


public class FormatterTest extends TestCase {
    public void test_numberLocalization() throws Exception {
        Locale arabic = new Locale("ar");
        // Check the fast path for %d:
        TestCase.assertEquals("12 \u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\u0660 34", String.format(arabic, "12 %d 34", 1234567890));
        // And the slow path too:
        TestCase.assertEquals("12 \u0661\u066c\u0662\u0663\u0664\u066c\u0665\u0666\u0667\u066c\u0668\u0669\u0660 34", String.format(arabic, "12 %,d 34", 1234567890));
        // And three localized floating point formats:
        TestCase.assertEquals("12 \u0661\u066b\u0662\u0663\u0660\u0627\u0633+\u0660\u0660 34", String.format(arabic, "12 %.3e 34", 1.23));
        TestCase.assertEquals("12 \u0661\u066b\u0662\u0663\u0660 34", String.format(arabic, "12 %.3f 34", 1.23));
        TestCase.assertEquals("12 \u0661\u066b\u0662\u0663 34", String.format(arabic, "12 %.3g 34", 1.23));
        // And date/time formatting (we assume that all time/date number formatting is done by the
        // same code, so this is representative):
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT-08:00"));
        c.setTimeInMillis(0);
        TestCase.assertEquals("12 \u0661\u0666:\u0660\u0660:\u0660\u0660 34", String.format(arabic, "12 %tT 34", c));
        // These shouldn't get localized:
        TestCase.assertEquals("1234", String.format(arabic, "1234"));
        TestCase.assertEquals("1234", String.format(arabic, "%s", "1234"));
        TestCase.assertEquals("1234", String.format(arabic, "%s", 1234));
        TestCase.assertEquals("2322", String.format(arabic, "%o", 1234));
        TestCase.assertEquals("4d2", String.format(arabic, "%x", 1234));
        TestCase.assertEquals("0x1.0p0", String.format(arabic, "%a", 1.0));
    }

    // http://b/2301938
    public void test_uppercaseConversions() throws Exception {
        // In most locales, the upper-case equivalent of "i" is "I".
        TestCase.assertEquals("JAKOB ARJOUNI", String.format(Locale.US, "%S", "jakob arjouni"));
        // In Turkish-language locales, there's a dotted capital "i".
        TestCase.assertEquals("JAKOB ARJOUN\u0130", String.format(new Locale("tr", "TR"), "%S", "jakob arjouni"));
    }

    // Creating a NumberFormat is expensive, so we like to reuse them, but we need to be careful
    // because they're mutable.
    public void test_NumberFormat_reuse() throws Exception {
        TestCase.assertEquals("7.000000 7", String.format("%.6f %d", 7.0, 7));
    }

    public void test_grouping() throws Exception {
        // The interesting case is -123, where you might naively output "-,123" if you're just
        // inserting a separator every three characters. The cases where there are three digits
        // before the first separator may also be interesting.
        TestCase.assertEquals("-1", String.format("%,d", (-1)));
        TestCase.assertEquals("-12", String.format("%,d", (-12)));
        TestCase.assertEquals("-123", String.format("%,d", (-123)));
        TestCase.assertEquals("-1,234", String.format("%,d", (-1234)));
        TestCase.assertEquals("-12,345", String.format("%,d", (-12345)));
        TestCase.assertEquals("-123,456", String.format("%,d", (-123456)));
        TestCase.assertEquals("-1,234,567", String.format("%,d", (-1234567)));
        TestCase.assertEquals("-12,345,678", String.format("%,d", (-12345678)));
        TestCase.assertEquals("-123,456,789", String.format("%,d", (-123456789)));
        TestCase.assertEquals("1", String.format("%,d", 1));
        TestCase.assertEquals("12", String.format("%,d", 12));
        TestCase.assertEquals("123", String.format("%,d", 123));
        TestCase.assertEquals("1,234", String.format("%,d", 1234));
        TestCase.assertEquals("12,345", String.format("%,d", 12345));
        TestCase.assertEquals("123,456", String.format("%,d", 123456));
        TestCase.assertEquals("1,234,567", String.format("%,d", 1234567));
        TestCase.assertEquals("12,345,678", String.format("%,d", 12345678));
        TestCase.assertEquals("123,456,789", String.format("%,d", 123456789));
    }

    public void test_formatNull() throws Exception {
        // We fast-path %s and %d (with no configuration) but need to make sure we handle the
        // special case of the null argument...
        TestCase.assertEquals("null", String.format(Locale.US, "%s", ((String) (null))));
        TestCase.assertEquals("null", String.format(Locale.US, "%d", ((Integer) (null))));
        // ...without screwing up conversions that don't take an argument.
        TestCase.assertEquals("%", String.format(Locale.US, "%%"));
    }

    // Alleged regression tests for historical bugs. (It's unclear whether the bugs were in
    // BigDecimal or Formatter.)
    public void test_BigDecimalFormatting() throws Exception {
        BigDecimal[] input = new BigDecimal[]{ new BigDecimal("20.00000"), new BigDecimal("20.000000"), new BigDecimal(".2"), new BigDecimal("2"), new BigDecimal("-2"), new BigDecimal("200000000000000000000000"), new BigDecimal("20000000000000000000000000000000000000000000000000") };
        String[] output = new String[]{ "20.00", "20.00", "0.20", "2.00", "-2.00", "200000000000000000000000.00", "20000000000000000000000000000000000000000000000000.00" };
        for (int i = 0; i < (input.length); ++i) {
            String result = String.format("%.2f", input[i]);
            TestCase.assertEquals((((((("input=\"" + (input[i])) + "\", ") + ",expected=") + (output[i])) + ",actual=") + result), output[i], result);
        }
    }

    // https://code.google.com/p/android/issues/detail?id=42936
    public void test42936() throws Exception {
        TestCase.assertEquals("0.00000000000000", String.format("%.15g", 0.0));
    }

    // https://code.google.com/p/android/issues/detail?id=53983
    public void test53983() throws Exception {
        FormatterTest.checkFormat("00", "H", 0);
        FormatterTest.checkFormat("0", "k", 0);
        FormatterTest.checkFormat("12", "I", 0);
        FormatterTest.checkFormat("12", "l", 0);
        FormatterTest.checkFormat("01", "H", 1);
        FormatterTest.checkFormat("1", "k", 1);
        FormatterTest.checkFormat("01", "I", 1);
        FormatterTest.checkFormat("1", "l", 1);
        FormatterTest.checkFormat("12", "H", 12);
        FormatterTest.checkFormat("12", "k", 12);
        FormatterTest.checkFormat("12", "I", 12);
        FormatterTest.checkFormat("12", "l", 12);
        FormatterTest.checkFormat("13", "H", 13);
        FormatterTest.checkFormat("13", "k", 13);
        FormatterTest.checkFormat("01", "I", 13);
        FormatterTest.checkFormat("1", "l", 13);
        FormatterTest.checkFormat("00", "H", 24);
        FormatterTest.checkFormat("0", "k", 24);
        FormatterTest.checkFormat("12", "I", 24);
        FormatterTest.checkFormat("12", "l", 24);
    }
}

