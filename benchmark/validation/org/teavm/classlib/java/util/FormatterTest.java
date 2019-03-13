/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.DuplicateFormatFlagsException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Formattable;
import java.util.Formatter;
import java.util.IllegalFormatCodePointException;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatFlagsException;
import java.util.IllegalFormatPrecisionException;
import java.util.Locale;
import java.util.MissingFormatWidthException;
import java.util.UnknownFormatConversionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class FormatterTest {
    @Test(expected = UnknownFormatConversionException.class)
    public void unexpectedEndOfFormatString() {
        new Formatter().format("%1", "foo");
    }

    @Test(expected = DuplicateFormatFlagsException.class)
    public void duplicateFlag() {
        new Formatter().format("%--s", "q");
    }

    @Test(expected = UnknownFormatConversionException.class)
    public void noPrecisionAfterDot() {
        new Formatter().format("%1.s", "q");
    }

    @Test
    public void bothPreviousModifierAndArgumentIndexPresent() {
        String result = new Formatter().format("%s %2$<s", "q", "w").toString();
        Assert.assertEquals("q q", result);
    }

    @Test
    public void formatsBoolean() {
        Assert.assertEquals("true", new Formatter().format("%b", true).toString());
        Assert.assertEquals("false", new Formatter().format("%b", false).toString());
        Assert.assertEquals("true", new Formatter().format("%b", new Object()).toString());
        Assert.assertEquals("false", new Formatter().format("%b", null).toString());
        Assert.assertEquals("  true", new Formatter().format("%6b", true).toString());
        Assert.assertEquals("true  ", new Formatter().format("%-6b", true).toString());
        Assert.assertEquals("true", new Formatter().format("%2b", true).toString());
        Assert.assertEquals("tr", new Formatter().format("%2.2b", true).toString());
        Assert.assertEquals("  tr", new Formatter().format("%4.2b", true).toString());
        Assert.assertEquals("TRUE", new Formatter().format("%B", true).toString());
        try {
            new Formatter().format("%+b", true);
            Assert.fail("Should have thrown exception");
        } catch (FormatFlagsConversionMismatchException e) {
            Assert.assertEquals("+", e.getFlags());
            Assert.assertEquals('b', e.getConversion());
        }
    }

    @Test
    public void formatsString() {
        Assert.assertEquals("23 foo", new Formatter().format("%s %s", 23, "foo").toString());
        Assert.assertEquals("0:-1:-1", new Formatter().format("%s", new FormatterTest.A()).toString());
        Assert.assertEquals("0:2:-1", new Formatter().format("%2s", new FormatterTest.A()).toString());
        Assert.assertEquals("0:2:3", new Formatter().format("%2.3s", new FormatterTest.A()).toString());
        Assert.assertEquals("1:3:-1", new Formatter().format("%-3s", new FormatterTest.A()).toString());
    }

    static class A implements Formattable {
        @Override
        public void formatTo(Formatter formatter, int flags, int width, int precision) {
            formatter.format("%s", ((((flags + ":") + width) + ":") + precision));
        }
    }

    @Test
    public void formatsHashCode() {
        Assert.assertEquals("18cc6 17C13", new Formatter().format("%h %H", "foo", "bar").toString());
    }

    @Test
    public void respectsFormatArgumentOrder() {
        String result = new Formatter().format("%s %s %<s %1$s %<s %s %1$s %s %<s", "a", "b", "c", "d").toString();
        Assert.assertEquals("a b b a a c a d d", result);
    }

    @Test
    public void formatsChar() {
        Assert.assertEquals("x:  Y:\udbff\udfff ", new Formatter().format("%c:%3C:%-3c", 'x', 'y', 1114111).toString());
        try {
            new Formatter().format("%c", Integer.MAX_VALUE);
            Assert.fail("IllegalFormatCodePointException expected");
        } catch (IllegalFormatCodePointException e) {
            Assert.assertEquals(Integer.MAX_VALUE, e.getCodePoint());
        }
        Assert.assertEquals("null", new Formatter().format("%c", new Object[]{ null }).toString());
        try {
            new Formatter().format("%C", new FormatterTest.A());
            Assert.fail("IllegalFormatConversionException expected");
        } catch (IllegalFormatConversionException e) {
            Assert.assertEquals(FormatterTest.A.class, e.getArgumentClass());
        }
        try {
            new Formatter().format("%3.1c", 'X');
            Assert.fail("IllegalFormatPrecisionException expected");
        } catch (IllegalFormatPrecisionException e) {
            Assert.assertEquals(1, e.getPrecision());
        }
    }

    @Test
    public void formatsDecimalInteger() {
        Assert.assertEquals("1 2 3 4", new Formatter().format("%d %d %d %d", ((byte) (1)), ((short) (2)), 3, 4L).toString());
        Assert.assertEquals("00023", new Formatter().format("%05d", 23).toString());
        Assert.assertEquals("-0023", new Formatter().format("%05d", (-23)).toString());
        Assert.assertEquals("00001,234", new Formatter(Locale.US).format("%0,9d", 1234).toString());
        Assert.assertEquals("(001,234)", new Formatter(Locale.US).format("%0,(9d", (-1234)).toString());
        Assert.assertEquals("1 12 123 1,234 12,345 123,456 1,234,567", new Formatter(Locale.US).format("%,d %,d %,d %,d %,d %,d %,d", 1, 12, 123, 1234, 12345, 123456, 1234567).toString());
        Assert.assertEquals("  -123:-234  ", new Formatter().format("%6d:%-6d", (-123), (-234)).toString());
        Assert.assertEquals("+123 +0123 +0", new Formatter().format("%+d %+05d %+d", 123, 123, 0).toString());
        Assert.assertEquals(": 123:-123:", new Formatter().format(":% d:% d:", 123, (-123)).toString());
        try {
            new Formatter().format("%#d", 23);
            Assert.fail("Should have thrown exception 1");
        } catch (FormatFlagsConversionMismatchException e) {
            Assert.assertEquals("#", e.getFlags());
        }
        try {
            new Formatter().format("% +d", 23);
            Assert.fail("Should have thrown exception 2");
        } catch (IllegalFormatFlagsException e) {
            Assert.assertTrue(e.getFlags().contains("+"));
            Assert.assertTrue(e.getFlags().contains(" "));
        }
        try {
            new Formatter().format("%-01d", 23);
            Assert.fail("Should have thrown exception 3");
        } catch (IllegalFormatFlagsException e) {
            Assert.assertTrue(e.getFlags().contains("-"));
            Assert.assertTrue(e.getFlags().contains("0"));
        }
        try {
            new Formatter().format("%-d", 23);
            Assert.fail("Should have thrown exception 4");
        } catch (MissingFormatWidthException e) {
            Assert.assertTrue(e.getFormatSpecifier().contains("d"));
        }
        try {
            new Formatter().format("%1.2d", 23);
            Assert.fail("Should have thrown exception 5");
        } catch (IllegalFormatPrecisionException e) {
            Assert.assertEquals(2, e.getPrecision());
        }
    }

    @Test
    public void formatsOctalInteger() {
        Assert.assertEquals("1 2 3 4", new Formatter().format("%o %o %o %o", ((byte) (1)), ((short) (2)), 3, 4L).toString());
        Assert.assertEquals("00027", new Formatter().format("%05o", 23).toString());
        Assert.assertEquals("0173", new Formatter().format("%#o", 123).toString());
        try {
            new Formatter().format("%-01o", 23);
            Assert.fail("Should have thrown exception 1");
        } catch (IllegalFormatFlagsException e) {
            Assert.assertTrue(e.getFlags().contains("-"));
            Assert.assertTrue(e.getFlags().contains("0"));
        }
        try {
            new Formatter().format("%-o", 23);
            Assert.fail("Should have thrown exception 2");
        } catch (MissingFormatWidthException e) {
            Assert.assertTrue(e.getFormatSpecifier().contains("o"));
        }
        try {
            new Formatter().format("%1.2o", 23);
            Assert.fail("Should have thrown exception 3");
        } catch (IllegalFormatPrecisionException e) {
            Assert.assertEquals(2, e.getPrecision());
        }
    }

    @Test
    public void formatsHexInteger() {
        Assert.assertEquals("1 2 3 4", new Formatter().format("%x %x %x %x", ((byte) (1)), ((short) (2)), 3, 4L).toString());
        Assert.assertEquals("00017", new Formatter().format("%05x", 23).toString());
        Assert.assertEquals("0x7b", new Formatter().format("%#x", 123).toString());
        try {
            new Formatter().format("%-01x", 23);
            Assert.fail("Should have thrown exception 1");
        } catch (IllegalFormatFlagsException e) {
            Assert.assertTrue(e.getFlags().contains("-"));
            Assert.assertTrue(e.getFlags().contains("0"));
        }
        try {
            new Formatter().format("%-x", 23);
            Assert.fail("Should have thrown exception 2");
        } catch (MissingFormatWidthException e) {
            Assert.assertTrue(e.getFormatSpecifier().contains("x"));
        }
        try {
            new Formatter().format("%1.2x", 23);
            Assert.fail("Should have thrown exception 3");
        } catch (IllegalFormatPrecisionException e) {
            Assert.assertEquals(2, e.getPrecision());
        }
    }
}

