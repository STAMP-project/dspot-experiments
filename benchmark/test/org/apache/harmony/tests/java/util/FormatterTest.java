/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.DuplicateFormatFlagsException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.GregorianCalendar;
import java.util.IllegalFormatCodePointException;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatException;
import java.util.IllegalFormatFlagsException;
import java.util.IllegalFormatPrecisionException;
import java.util.IllegalFormatWidthException;
import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.MissingFormatWidthException;
import java.util.TimeZone;
import java.util.UnknownFormatConversionException;
import junit.framework.TestCase;

import static java.util.Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT;
import static java.util.Formatter.BigDecimalLayoutForm.SCIENTIFIC;
import static java.util.Formatter.BigDecimalLayoutForm.valueOf;
import static java.util.Formatter.BigDecimalLayoutForm.values;


public class FormatterTest extends TestCase {
    private boolean root;

    class MockAppendable implements Appendable {
        public Appendable append(CharSequence arg0) throws IOException {
            return null;
        }

        public Appendable append(char arg0) throws IOException {
            return null;
        }

        public Appendable append(CharSequence arg0, int arg1, int arg2) throws IOException {
            return null;
        }
    }

    class MockFormattable implements Formattable {
        public void formatTo(Formatter formatter, int flags, int width, int precision) throws IllegalFormatException {
            if ((flags & (FormattableFlags.UPPERCASE)) != 0) {
                formatter.format((((("CUSTOMIZED FORMAT FUNCTION" + " WIDTH: ") + width) + " PRECISION: ") + precision));
            } else {
                formatter.format((((("customized format function" + " width: ") + width) + " precision: ") + precision));
            }
        }

        public String toString() {
            return "formattable object";
        }

        public int hashCode() {
            return 15;
        }
    }

    class MockDestination implements Flushable , Appendable {
        private StringBuilder data = new StringBuilder();

        private boolean enabled = false;

        public Appendable append(char c) throws IOException {
            if (enabled) {
                data.append(c);
                enabled = true;// enable it after the first append

            } else {
                throw new IOException();
            }
            return this;
        }

        public Appendable append(CharSequence csq) throws IOException {
            if (enabled) {
                data.append(csq);
                enabled = true;// enable it after the first append

            } else {
                throw new IOException();
            }
            return this;
        }

        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            if (enabled) {
                data.append(csq, start, end);
                enabled = true;// enable it after the first append

            } else {
                throw new IOException();
            }
            return this;
        }

        public void flush() throws IOException {
            throw new IOException("Always throw IOException");
        }

        public String toString() {
            return data.toString();
        }
    }

    private File notExist;

    private File fileWithContent;

    private File readOnly;

    private File secret;

    private TimeZone defaultTimeZone;

    private Locale defaultLocale;

    /**
     * java.util.Formatter#Formatter()
     */
    public void test_Constructor() {
        Formatter f = new Formatter();
        TestCase.assertNotNull(f);
        TestCase.assertTrue(((f.out()) instanceof StringBuilder));
        TestCase.assertEquals(f.locale(), Locale.getDefault());
        TestCase.assertNotNull(f.toString());
    }

    /**
     * java.util.Formatter#Formatter(Appendable)
     */
    public void test_ConstructorLjava_lang_Appendable() {
        FormatterTest.MockAppendable ma = new FormatterTest.MockAppendable();
        Formatter f1 = new Formatter(ma);
        TestCase.assertEquals(ma, f1.out());
        TestCase.assertEquals(f1.locale(), Locale.getDefault());
        TestCase.assertNotNull(f1.toString());
        Formatter f2 = new Formatter(((Appendable) (null)));
        /* If a(the input param) is null then a StringBuilder will be created
        and the output can be attained by invoking the out() method. But RI
        raises an error of FormatterClosedException when invoking out() or
        toString().
         */
        Appendable sb = f2.out();
        TestCase.assertTrue((sb instanceof StringBuilder));
        TestCase.assertNotNull(f2.toString());
    }

    /**
     * java.util.Formatter#Formatter(Locale)
     */
    public void test_ConstructorLjava_util_Locale() {
        Formatter f1 = new Formatter(Locale.FRANCE);
        TestCase.assertTrue(((f1.out()) instanceof StringBuilder));
        TestCase.assertEquals(f1.locale(), Locale.FRANCE);
        TestCase.assertNotNull(f1.toString());
        Formatter f2 = new Formatter(((Locale) (null)));
        TestCase.assertNull(f2.locale());
        TestCase.assertTrue(((f2.out()) instanceof StringBuilder));
        TestCase.assertNotNull(f2.toString());
    }

    /**
     * java.util.Formatter#Formatter(Appendable, Locale)
     */
    public void test_ConstructorLjava_lang_AppendableLjava_util_Locale() {
        FormatterTest.MockAppendable ma = new FormatterTest.MockAppendable();
        Formatter f1 = new Formatter(ma, Locale.CANADA);
        TestCase.assertEquals(ma, f1.out());
        TestCase.assertEquals(f1.locale(), Locale.CANADA);
        Formatter f2 = new Formatter(ma, null);
        TestCase.assertNull(f2.locale());
        TestCase.assertEquals(ma, f1.out());
        Formatter f3 = new Formatter(null, Locale.GERMAN);
        TestCase.assertEquals(f3.locale(), Locale.GERMAN);
        TestCase.assertTrue(((f3.out()) instanceof StringBuilder));
    }

    /**
     * java.util.Formatter#Formatter(String)
     */
    public void test_ConstructorLjava_lang_String() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((String) (null)));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        f = new Formatter(notExist.getPath());
        TestCase.assertEquals(f.locale(), Locale.getDefault());
        f.close();
        f = new Formatter(fileWithContent.getPath());
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly.getPath());
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(String, String)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_String() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((String) (null)), Charset.defaultCharset().name());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        try {
            f = new Formatter(notExist.getPath(), null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        }
        f = new Formatter(notExist.getPath(), Charset.defaultCharset().name());
        TestCase.assertEquals(f.locale(), Locale.getDefault());
        f.close();
        try {
            f = new Formatter(notExist.getPath(), "ISO 1111-1");
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        }
        f = new Formatter(fileWithContent.getPath(), "UTF-16BE");
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly.getPath(), "UTF-16BE");
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(String, String, Locale)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_StringLjava_util_Locale() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((String) (null)), Charset.defaultCharset().name(), Locale.KOREA);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        try {
            f = new Formatter(notExist.getPath(), null, Locale.KOREA);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        }
        f = new Formatter(notExist.getPath(), Charset.defaultCharset().name(), null);
        TestCase.assertNotNull(f);
        f.close();
        f = new Formatter(notExist.getPath(), Charset.defaultCharset().name(), Locale.KOREA);
        TestCase.assertEquals(f.locale(), Locale.KOREA);
        f.close();
        try {
            f = new Formatter(notExist.getPath(), "ISO 1111-1", Locale.CHINA);
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        }
        f = new Formatter(fileWithContent.getPath(), "UTF-16BE", Locale.CANADA_FRENCH);
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly.getPath(), Charset.defaultCharset().name(), Locale.ITALY);
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(File)
     */
    public void test_ConstructorLjava_io_File() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((File) (null)));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        f = new Formatter(notExist);
        TestCase.assertEquals(f.locale(), Locale.getDefault());
        f.close();
        f = new Formatter(fileWithContent);
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly);
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(File, String)
     */
    public void test_ConstructorLjava_io_FileLjava_lang_String() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((File) (null)), Charset.defaultCharset().name());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        f = new Formatter(notExist, Charset.defaultCharset().name());
        TestCase.assertEquals(f.locale(), Locale.getDefault());
        f.close();
        f = new Formatter(fileWithContent, "UTF-16BE");
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly, Charset.defaultCharset().name());
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
        try {
            f = new Formatter(notExist, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        } finally {
            if (notExist.exists()) {
                // Fail on RI on Windows, because output stream is created and
                // not closed when exception thrown
                TestCase.assertTrue(notExist.delete());
            }
        }
        try {
            f = new Formatter(notExist, "ISO 1111-1");
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        } finally {
            if (notExist.exists()) {
                // Fail on RI on Windows, because output stream is created and
                // not closed when exception thrown
                TestCase.assertTrue(notExist.delete());
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(File, String, Locale)
     */
    public void test_ConstructorLjava_io_FileLjava_lang_StringLjava_util_Locale() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((File) (null)), Charset.defaultCharset().name(), Locale.KOREA);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        try {
            f = new Formatter(notExist, null, Locale.KOREA);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        }
        f = new Formatter(notExist, Charset.defaultCharset().name(), null);
        TestCase.assertNotNull(f);
        f.close();
        f = new Formatter(notExist, Charset.defaultCharset().name(), Locale.KOREA);
        TestCase.assertEquals(f.locale(), Locale.KOREA);
        f.close();
        try {
            f = new Formatter(notExist, "ISO 1111-1", Locale.CHINA);
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        }
        f = new Formatter(fileWithContent.getPath(), "UTF-16BE", Locale.CANADA_FRENCH);
        TestCase.assertEquals(0, fileWithContent.length());
        f.close();
        if (!(root)) {
            try {
                f = new Formatter(readOnly.getPath(), Charset.defaultCharset().name(), Locale.ITALY);
                TestCase.fail("should throw FileNotFoundException");
            } catch (FileNotFoundException e) {
                // expected
            }
        }
    }

    /**
     * java.util.Formatter#Formatter(PrintStream)
     */
    public void test_ConstructorLjava_io_PrintStream() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((PrintStream) (null)));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        PrintStream ps = new PrintStream(notExist, "UTF-16BE");
        f = new Formatter(ps);
        TestCase.assertEquals(Locale.getDefault(), f.locale());
        f.close();
    }

    /**
     * java.util.Formatter#Formatter(OutputStream)
     */
    public void test_ConstructorLjava_io_OutputStream() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((OutputStream) (null)));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        OutputStream os = new FileOutputStream(notExist);
        f = new Formatter(os);
        TestCase.assertEquals(Locale.getDefault(), f.locale());
        f.close();
    }

    /**
     * java.util.Formatter#Formatter(OutputStream, String)
     */
    public void test_ConstructorLjava_io_OutputStreamLjava_lang_String() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((OutputStream) (null)), Charset.defaultCharset().name());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(notExist);
            f = new Formatter(os, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        } finally {
            os.close();
        }
        try {
            os = new PipedOutputStream();
            f = new Formatter(os, "TMP-1111");
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        } finally {
            os.close();
        }
        os = new FileOutputStream(fileWithContent);
        f = new Formatter(os, "UTF-16BE");
        TestCase.assertEquals(Locale.getDefault(), f.locale());
        f.close();
    }

    /**
     * Test method for 'java.util.Formatter.Formatter(OutputStream, String,
     * Locale)
     */
    public void test_ConstructorLjava_io_OutputStreamLjava_lang_StringLjava_util_Locale() throws IOException {
        Formatter f = null;
        try {
            f = new Formatter(((OutputStream) (null)), Charset.defaultCharset().name(), Locale.getDefault());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e1) {
            // expected
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(notExist);
            f = new Formatter(os, null, Locale.getDefault());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e2) {
            // expected
        } finally {
            os.close();
        }
        os = new FileOutputStream(notExist);
        f = new Formatter(os, Charset.defaultCharset().name(), null);
        f.close();
        try {
            os = new PipedOutputStream();
            f = new Formatter(os, "TMP-1111", Locale.getDefault());
            TestCase.fail("should throw UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e1) {
            // expected
        }
        os = new FileOutputStream(fileWithContent);
        f = new Formatter(os, "UTF-16BE", Locale.ENGLISH);
        TestCase.assertEquals(Locale.ENGLISH, f.locale());
        f.close();
    }

    /**
     * java.util.Formatter#locale()
     */
    public void test_locale() {
        Formatter f = null;
        f = new Formatter(((Locale) (null)));
        TestCase.assertNull(f.locale());
        f.close();
        try {
            f.locale();
            TestCase.fail("should throw FormatterClosedException");
        } catch (FormatterClosedException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#out()
     */
    public void test_out() {
        Formatter f = null;
        f = new Formatter();
        TestCase.assertNotNull(f.out());
        TestCase.assertTrue(((f.out()) instanceof StringBuilder));
        f.close();
        try {
            f.out();
            TestCase.fail("should throw FormatterClosedException");
        } catch (FormatterClosedException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#flush()
     */
    public void test_flush() throws IOException {
        Formatter f = null;
        f = new Formatter(notExist);
        TestCase.assertTrue((f instanceof Flushable));
        f.close();
        try {
            f.flush();
            TestCase.fail("should throw FormatterClosedException");
        } catch (FormatterClosedException e) {
            // expected
        }
        f = new Formatter();
        // For destination that does not implement Flushable
        // No exception should be thrown
        f.flush();
    }

    /**
     * java.util.Formatter#close()
     */
    public void test_close() throws IOException {
        Formatter f = new Formatter(notExist);
        TestCase.assertTrue((f instanceof Closeable));
        f.close();
        // close next time will not throw exception
        f.close();
        TestCase.assertNull(f.ioException());
    }

    /**
     * java.util.Formatter#toString()
     */
    public void test_toString() {
        Formatter f = new Formatter();
        TestCase.assertNotNull(f.toString());
        TestCase.assertEquals(f.out().toString(), f.toString());
        f.close();
        try {
            f.toString();
            TestCase.fail("should throw FormatterClosedException");
        } catch (FormatterClosedException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#ioException()
     */
    public void test_ioException() throws IOException {
        Formatter f = null;
        f = new Formatter(new FormatterTest.MockDestination());
        TestCase.assertNull(f.ioException());
        f.flush();
        TestCase.assertNotNull(f.ioException());
        f.close();
        FormatterTest.MockDestination md = new FormatterTest.MockDestination();
        f = new Formatter(md);
        f.format("%s%s", "1", "2");
        // format stop working after IOException
        TestCase.assertNotNull(f.ioException());
        TestCase.assertEquals("", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for null parameter
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_null() {
        Formatter f = new Formatter();
        try {
            f.format(((String) (null)), "parameter");
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        f = new Formatter();
        f.format("hello", ((Object[]) (null)));
        TestCase.assertEquals("hello", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for argument index
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_ArgIndex() {
        Formatter formatter = new Formatter(Locale.US);
        formatter.format("%1$s%2$s%3$s%4$s%5$s%6$s%7$s%8$s%9$s%11$s%10$s", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
        TestCase.assertEquals("1234567891110", formatter.toString());
        formatter = new Formatter(Locale.JAPAN);
        formatter.format("%0$s", "hello");
        TestCase.assertEquals("hello", formatter.toString());
        try {
            formatter = new Formatter(Locale.US);
            formatter.format("%-1$s", "1", "2");
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
        try {
            formatter = new Formatter(Locale.US);
            formatter.format("%$s", "hello", "2");
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
        try {
            Formatter f = new Formatter(Locale.US);
            f.format("%", "string");
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
        formatter = new Formatter(Locale.FRANCE);
        formatter.format("%1$s%2$s%3$s%4$s%5$s%6$s%7$s%8$s%<s%s%s%<s", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
        TestCase.assertEquals("123456788122", formatter.toString());
        formatter = new Formatter(Locale.FRANCE);
        formatter.format("xx%1$s22%2$s%s%<s%5$s%<s&%7$h%2$s%8$s%<s%s%s%<ssuffix", "1", "2", "3", "4", "5", "6", 7, "8", "9", "10", "11");
        TestCase.assertEquals("xx12221155&7288233suffix", formatter.toString());
        try {
            formatter.format("%<s", "hello");
            TestCase.fail("should throw MissingFormatArgumentException");
        } catch (MissingFormatArgumentException e) {
            // expected
        }
        formatter = new Formatter(Locale.US);
        try {
            formatter.format("%123$s", "hello");
            TestCase.fail("should throw MissingFormatArgumentException");
        } catch (MissingFormatArgumentException e) {
            // expected
        }
        formatter = new Formatter(Locale.US);
        try {
            // 2147483648 is the value of Integer.MAX_VALUE + 1
            formatter.format("%2147483648$s", "hello");
            TestCase.fail("should throw MissingFormatArgumentException");
        } catch (MissingFormatArgumentException e) {
            // expected
        }
        try {
            // 2147483647 is the value of Integer.MAX_VALUE
            formatter.format("%2147483647$s", "hello");
            TestCase.fail("should throw MissingFormatArgumentException");
        } catch (MissingFormatArgumentException e) {
            // expected
        }
        formatter = new Formatter(Locale.US);
        try {
            formatter.format("%s%s", "hello");
            TestCase.fail("should throw MissingFormatArgumentException");
        } catch (MissingFormatArgumentException e) {
            // expected
        }
        formatter = new Formatter(Locale.US);
        formatter.format("$100", 100);
        TestCase.assertEquals("$100", formatter.toString());
        formatter = new Formatter(Locale.UK);
        formatter.format("%01$s", "string");
        TestCase.assertEquals("string", formatter.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for width
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_Width() {
        Formatter f = new Formatter(Locale.US);
        f.format("%1$8s", "1");
        TestCase.assertEquals("       1", f.toString());
        f = new Formatter(Locale.US);
        f.format("%1$-1%", "string");
        TestCase.assertEquals("%", f.toString());
        f = new Formatter(Locale.ITALY);
        // 2147483648 is the value of Integer.MAX_VALUE + 1
        f.format("%2147483648s", "string");
        TestCase.assertEquals("string", f.toString());
        // the value of Integer.MAX_VALUE will allocate about 4G bytes of
        // memory.
        // It may cause OutOfMemoryError, so this value is not tested
    }

    /**
     * java.util.Formatter#format(String, Object...) for precision
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_Precision() {
        Formatter f = new Formatter(Locale.US);
        f.format("%.5s", "123456");
        TestCase.assertEquals("12345", f.toString());
        f = new Formatter(Locale.US);
        // 2147483648 is the value of Integer.MAX_VALUE + 1
        f.format("%.2147483648s", "...");
        TestCase.assertEquals("...", f.toString());
        // the value of Integer.MAX_VALUE will allocate about 4G bytes of
        // memory.
        // It may cause OutOfMemoryError, so this value is not tested
        f = new Formatter(Locale.US);
        f.format("%10.0b", Boolean.TRUE);
        TestCase.assertEquals("          ", f.toString());
        f = new Formatter(Locale.US);
        f.format("%10.01s", "hello");
        TestCase.assertEquals("         h", f.toString());
        try {
            f = new Formatter(Locale.US);
            f.format("%.s", "hello", "2");
            TestCase.fail("should throw Exception");
        } catch (UnknownFormatConversionException | IllegalFormatPrecisionException expected) {
            // expected
        }
        try {
            f = new Formatter(Locale.US);
            f.format("%.-5s", "123456");
            TestCase.fail("should throw Exception");
        } catch (UnknownFormatConversionException | IllegalFormatPrecisionException expected) {
            // expected
        }
        try {
            f = new Formatter(Locale.US);
            f.format("%1.s", "hello", "2");
            TestCase.fail("should throw Exception");
        } catch (UnknownFormatConversionException | IllegalFormatPrecisionException expected) {
            // expected
        }
        f = new Formatter(Locale.US);
        f.format("%5.1s", "hello");
        TestCase.assertEquals("    h", f.toString());
        f = new Formatter(Locale.FRANCE);
        f.format("%.0s", "hello", "2");
        TestCase.assertEquals("", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for line sperator
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_LineSeparator() {
        Formatter f = null;
        /* J2ObjC: Setting line.separator has no effect, see System.lineSeparator().
        String oldSeparator = System.getProperty("line.separator");
        try {
        System.setProperty("line.separator", "!\n");

        f = new Formatter(Locale.US);
        f.format("%1$n", 1);
        assertEquals("!\n", f.toString());

        f = new Formatter(Locale.KOREAN);
        f.format("head%1$n%2$n", 1, new Date());
        assertEquals("head!\n!\n", f.toString());

        f = new Formatter(Locale.US);
        f.format("%n%s", "hello");
        assertEquals("!\nhello", f.toString());
        } finally {
        System.setProperty("line.separator", oldSeparator);
        }
         */
        f = new Formatter(Locale.US);
        try {
            f.format("%-n");
            TestCase.fail("should throw IllegalFormatFlagsException: %-n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("%+n");
            TestCase.fail("should throw IllegalFormatFlagsException: %+n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("%#n");
            TestCase.fail("should throw IllegalFormatFlagsException: %#n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("% n");
            TestCase.fail("should throw IllegalFormatFlagsException: % n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("%0n");
            TestCase.fail("should throw IllegalFormatFlagsException: %0n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("%,n");
            TestCase.fail("should throw IllegalFormatFlagsException: %,n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            f.format("%(n");
            TestCase.fail("should throw IllegalFormatFlagsException: %(n");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%4n");
            TestCase.fail("should throw IllegalFormatWidthException");
        } catch (IllegalFormatWidthException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%-4n");
            TestCase.fail("should throw IllegalFormatWidthException");
        } catch (IllegalFormatWidthException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%.9n");
            TestCase.fail("should throw IllegalFormatPrecisionException");
        } catch (IllegalFormatPrecisionException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%5.9n");
            TestCase.fail("should throw IllegalFormatPrecisionException");
        } catch (IllegalFormatPrecisionException e) {
            // expected
        }
        // System.setProperty("line.separator", oldSeparator);
    }

    /**
     * java.util.Formatter#format(String, Object...) for percent
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_Percent() {
        Formatter f = null;
        f = new Formatter(Locale.ENGLISH);
        f.format("%1$%", 100);
        TestCase.assertEquals("%", f.toString());
        f = new Formatter(Locale.CHINA);
        f.format("%1$%%%", "hello", new Object());
        TestCase.assertEquals("%%", f.toString());
        f = new Formatter(Locale.CHINA);
        f.format("%%%s", "hello");
        TestCase.assertEquals("%hello", f.toString());
        f = new Formatter(Locale.US);
        try {
            f.format("%.9%");
            TestCase.fail("should throw IllegalFormatPrecisionException");
        } catch (IllegalFormatPrecisionException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%5.9%");
            TestCase.fail("should throw IllegalFormatPrecisionException");
        } catch (IllegalFormatPrecisionException e) {
            // expected
        }
        /* J2ObjC: Throws IllegalFormatFlagsException.
        f = new Formatter(Locale.US);
        assertFormatFlagsConversionMismatchException(f, "%+%");
        assertFormatFlagsConversionMismatchException(f, "%#%");
        assertFormatFlagsConversionMismatchException(f, "% %");
        assertFormatFlagsConversionMismatchException(f, "%0%");
        assertFormatFlagsConversionMismatchException(f, "%,%");
        assertFormatFlagsConversionMismatchException(f, "%(%");
         */
        // J2ObjC: Seems to be more compatible with the RI than Android, strangely.
        // f = new Formatter(Locale.KOREAN);
        // f.format("%4%", 1);
        // /*
        // * fail on RI the output string should be right justified by appending
        // * spaces till the whole string is 4 chars width.
        // */
        // assertEquals("   %", f.toString());
        // f = new Formatter(Locale.US);
        // f.format("%-4%", 100);
        // /*
        // * fail on RI, throw UnknownFormatConversionException the output string
        // * should be left justified by appending spaces till the whole string is
        // * 4 chars width.
        // */
        // assertEquals("%   ", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for flag
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_Flag() {
        Formatter f = new Formatter(Locale.US);
        try {
            f.format("%1$-#-8s", "something");
            TestCase.fail("should throw DuplicateFormatFlagsException");
        } catch (DuplicateFormatFlagsException e) {
            // expected
        }
        final char[] chars = new char[]{ '-', '#', '+', ' ', '0', ',', '(', '%', '<' };
        Arrays.sort(chars);
        f = new Formatter(Locale.US);
        for (char i = 0; i <= 256; i++) {
            // test 8 bit character
            if ((((Arrays.binarySearch(chars, i)) >= 0) || (Character.isDigit(i))) || (Character.isLetter(i))) {
                // Do not test 0-9, a-z, A-Z and characters in the chars array.
                // They are characters used as flags, width or conversions
                continue;
            }
            try {
                f.format((("%" + i) + "s"), 1);
                TestCase.fail("should throw UnknownFormatConversionException");
            } catch (UnknownFormatConversionException e) {
                // expected
            } catch (IllegalFormatPrecisionException e) {
                // If i is '.', s can also be interpreted as an illegal precision.
                if (i != '.') {
                    throw e;
                }
            }
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for general
     * conversion b/B
     */
    public void test_format_LString$LObject_GeneralConversionB() {
        final Object[][] triple = new Object[][]{ new Object[]{ Boolean.FALSE, "%3.2b", " fa" }, new Object[]{ Boolean.FALSE, "%-4.6b", "false" }, new Object[]{ Boolean.FALSE, "%.2b", "fa" }, new Object[]{ Boolean.TRUE, "%3.2b", " tr" }, new Object[]{ Boolean.TRUE, "%-4.6b", "true" }, new Object[]{ Boolean.TRUE, "%.2b", "tr" }, new Object[]{ new Character('c'), "%3.2b", " tr" }, new Object[]{ new Character('c'), "%-4.6b", "true" }, new Object[]{ new Character('c'), "%.2b", "tr" }, new Object[]{ new Byte(((byte) (1))), "%3.2b", " tr" }, new Object[]{ new Byte(((byte) (1))), "%-4.6b", "true" }, new Object[]{ new Byte(((byte) (1))), "%.2b", "tr" }, new Object[]{ new Short(((short) (1))), "%3.2b", " tr" }, new Object[]{ new Short(((short) (1))), "%-4.6b", "true" }, new Object[]{ new Short(((short) (1))), "%.2b", "tr" }, new Object[]{ new Integer(1), "%3.2b", " tr" }, new Object[]{ new Integer(1), "%-4.6b", "true" }, new Object[]{ new Integer(1), "%.2b", "tr" }, new Object[]{ new Float(1.1F), "%3.2b", " tr" }, new Object[]{ new Float(1.1F), "%-4.6b", "true" }, new Object[]{ new Float(1.1F), "%.2b", "tr" }, new Object[]{ new Double(1.1), "%3.2b", " tr" }, new Object[]{ new Double(1.1), "%-4.6b", "true" }, new Object[]{ new Double(1.1), "%.2b", "tr" }, new Object[]{ "", "%3.2b", " tr" }, new Object[]{ "", "%-4.6b", "true" }, new Object[]{ "", "%.2b", "tr" }, new Object[]{ "string content", "%3.2b", " tr" }, new Object[]{ "string content", "%-4.6b", "true" }, new Object[]{ "string content", "%.2b", "tr" }, new Object[]{ new FormatterTest.MockFormattable(), "%3.2b", " tr" }, new Object[]{ new FormatterTest.MockFormattable(), "%-4.6b", "true" }, new Object[]{ new FormatterTest.MockFormattable(), "%.2b", "tr" }, new Object[]{ ((Object) (null)), "%3.2b", " fa" }, new Object[]{ ((Object) (null)), "%-4.6b", "false" }, new Object[]{ ((Object) (null)), "%.2b", "fa" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f = null;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.FRANCE);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
            f = new Formatter(Locale.GERMAN);
            f.format(((String) (triple[i][pattern])).toUpperCase(Locale.US), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), ((String) (triple[i][output])).toUpperCase(Locale.US), f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for general
     * conversion type 's' and 'S'
     */
    public void test_format_LString$LObject_GeneralConversionS() {
        final Object[][] triple = new Object[][]{ new Object[]{ Boolean.FALSE, "%2.3s", "fal" }, new Object[]{ Boolean.FALSE, "%-6.4s", "fals  " }, new Object[]{ Boolean.FALSE, "%.5s", "false" }, new Object[]{ Boolean.TRUE, "%2.3s", "tru" }, new Object[]{ Boolean.TRUE, "%-6.4s", "true  " }, new Object[]{ Boolean.TRUE, "%.5s", "true" }, new Object[]{ new Character('c'), "%2.3s", " c" }, new Object[]{ new Character('c'), "%-6.4s", "c     " }, new Object[]{ new Character('c'), "%.5s", "c" }, new Object[]{ new Byte(((byte) (1))), "%2.3s", " 1" }, new Object[]{ new Byte(((byte) (1))), "%-6.4s", "1     " }, new Object[]{ new Byte(((byte) (1))), "%.5s", "1" }, new Object[]{ new Short(((short) (1))), "%2.3s", " 1" }, new Object[]{ new Short(((short) (1))), "%-6.4s", "1     " }, new Object[]{ new Short(((short) (1))), "%.5s", "1" }, new Object[]{ new Integer(1), "%2.3s", " 1" }, new Object[]{ new Integer(1), "%-6.4s", "1     " }, new Object[]{ new Integer(1), "%.5s", "1" }, new Object[]{ new Float(1.1F), "%2.3s", "1.1" }, new Object[]{ new Float(1.1F), "%-6.4s", "1.1   " }, new Object[]{ new Float(1.1F), "%.5s", "1.1" }, new Object[]{ new Double(1.1), "%2.3s", "1.1" }, new Object[]{ new Double(1.1), "%-6.4s", "1.1   " }, new Object[]{ new Double(1.1), "%.5s", "1.1" }, new Object[]{ "", "%2.3s", "  " }, new Object[]{ "", "%-6.4s", "      " }, new Object[]{ "", "%.5s", "" }, new Object[]{ "string content", "%2.3s", "str" }, new Object[]{ "string content", "%-6.4s", "stri  " }, new Object[]{ "string content", "%.5s", "strin" }, new Object[]{ new FormatterTest.MockFormattable(), "%2.3s", "customized format function width: 2 precision: 3" }, new Object[]{ new FormatterTest.MockFormattable(), "%-6.4s", "customized format function width: 6 precision: 4" }, new Object[]{ new FormatterTest.MockFormattable(), "%.5s", "customized format function width: -1 precision: 5" }, new Object[]{ ((Object) (null)), "%2.3s", "nul" }, new Object[]{ ((Object) (null)), "%-6.4s", "null  " }, new Object[]{ ((Object) (null)), "%.5s", "null" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f = null;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.FRANCE);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
            f = new Formatter(Locale.GERMAN);
            f.format(((String) (triple[i][pattern])).toUpperCase(Locale.US), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), ((String) (triple[i][output])).toUpperCase(Locale.US), f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for general
     * conversion type 'h' and 'H'
     */
    public void test_format_LString$LObject_GeneralConversionH() {
        final Object[] input = new Object[]{ Boolean.FALSE, Boolean.TRUE, new Character('c'), new Byte(((byte) (1))), new Short(((short) (1))), new Integer(1), new Float(1.1F), new Double(1.1), "", "string content", new FormatterTest.MockFormattable(), ((Object) (null)) };
        Formatter f = null;
        for (int i = 0; i < ((input.length) - 1); i++) {
            f = new Formatter(Locale.FRANCE);
            f.format("%h", input[i]);
            TestCase.assertEquals(((("triple[" + i) + "]:") + (input[i])), Integer.toHexString(input[i].hashCode()), f.toString());
            f = new Formatter(Locale.GERMAN);
            f.format("%H", input[i]);
            TestCase.assertEquals(((("triple[" + i) + "]:") + (input[i])), Integer.toHexString(input[i].hashCode()).toUpperCase(Locale.US), f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for general
     * conversion other cases
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_GeneralConversionOther() {
        /* In Turkish locale, the upper case of '\u0069' is '\u0130'. The
        following test indicate that '\u0069' is coverted to upper case
        without using the turkish locale.
         */
        Formatter f = new Formatter(new Locale("tr"));
        f.format("%S", "i");
        // assertEquals("\u0049", f.toString());  J2ObjC changed.
        TestCase.assertEquals("\u0130", f.toString());
        final Object[] input = new Object[]{ Boolean.FALSE, Boolean.TRUE, new Character('c'), new Byte(((byte) (1))), new Short(((short) (1))), new Integer(1), new Float(1.1F), new Double(1.1), "", "string content", new FormatterTest.MockFormattable(), ((Object) (null)) };
        f = new Formatter(Locale.GERMAN);
        for (int i = 0; i < (input.length); i++) {
            if (!((input[i]) instanceof Formattable)) {
                try {
                    f.format("%#s", input[i]);
                    /* fail on RI, spec says if the '#' flag is present and the
                    argument is not a Formattable , then a
                    FormatFlagsConversionMismatchException will be thrown.
                     */
                    TestCase.fail("should throw FormatFlagsConversionMismatchException");
                } catch (FormatFlagsConversionMismatchException e) {
                    // expected
                }
            } else {
                f.format("%#s%<-#8s", input[i]);
                TestCase.assertEquals("customized format function width: -1 precision: -1customized format function width: 8 precision: -1", f.toString());
            }
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for general
     * conversion exception
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_GeneralConversionException() {
        final String[] flagMismatch = new String[]{ "%#b", "%+b", "% b", "%0b", "%,b", "%(b", "%#B", "%+B", "% B", "%0B", "%,B", "%(B", "%#h", "%+h", "% h", "%0h", "%,h", "%(h", "%#H", "%+H", "% H", "%0H", "%,H", "%(H", "%+s", "% s", "%0s", "%,s", "%(s", "%+S", "% S", "%0S", "%,S", "%(S" };
        Formatter f = new Formatter(Locale.US);
        for (int i = 0; i < (flagMismatch.length); i++) {
            try {
                f.format(flagMismatch[i], "something");
                TestCase.fail("should throw FormatFlagsConversionMismatchException");
            } catch (FormatFlagsConversionMismatchException e) {
                // expected
            }
        }
        final String[] missingWidth = new String[]{ "%-b", "%-B", "%-h", "%-H", "%-s", "%-S" };
        for (int i = 0; i < (missingWidth.length); i++) {
            try {
                f.format(missingWidth[i], "something");
                TestCase.fail("should throw MissingFormatWidthException");
            } catch (MissingFormatWidthException e) {
                // expected
            }
        }
        // Regression test
        f = new Formatter();
        try {
            f.format("%c", ((byte) (-1)));
            TestCase.fail("Should throw IllegalFormatCodePointException");
        } catch (IllegalFormatCodePointException e) {
            // expected
        }
        f = new Formatter();
        try {
            f.format("%c", ((short) (-1)));
            TestCase.fail("Should throw IllegalFormatCodePointException");
        } catch (IllegalFormatCodePointException e) {
            // expected
        }
        f = new Formatter();
        try {
            f.format("%c", (-1));
            TestCase.fail("Should throw IllegalFormatCodePointException");
        } catch (IllegalFormatCodePointException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for Character
     * conversion
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_CharacterConversion() {
        Formatter f = new Formatter(Locale.US);
        final Object[] illArgs = new Object[]{ Boolean.TRUE, new Float(1.1F), new Double(1.1), "string content", new Float(1.1F), new Date() };
        for (int i = 0; i < (illArgs.length); i++) {
            try {
                f.format("%c", illArgs[i]);
                TestCase.fail("should throw IllegalFormatConversionException");
            } catch (IllegalFormatConversionException e) {
                // expected
            }
        }
        try {
            f.format("%c", Integer.MAX_VALUE);
            TestCase.fail("should throw IllegalFormatCodePointException");
        } catch (IllegalFormatCodePointException e) {
            // expected
        }
        try {
            f.format("%#c", 'c');
            TestCase.fail("should throw FormatFlagsConversionMismatchException");
        } catch (FormatFlagsConversionMismatchException e) {
            // expected
        }
        final Object[][] triple = new Object[][]{ new Object[]{ 'c', "%c", "c" }, new Object[]{ 'c', "%-2c", "c " }, new Object[]{ '\u0123', "%c", "\u0123" }, new Object[]{ '\u0123', "%-2c", "\u0123 " }, new Object[]{ ((byte) (17)), "%c", "\u0011" }, new Object[]{ ((byte) (17)), "%-2c", "\u0011 " }, new Object[]{ ((short) (4369)), "%c", "\u1111" }, new Object[]{ ((short) (4369)), "%-2c", "\u1111 " }, new Object[]{ 17, "%c", "\u0011" }, new Object[]{ 17, "%-2c", "\u0011 " } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(triple[i][output], f.toString());
        }
        f = new Formatter(Locale.US);
        f.format("%c", 65536);
        TestCase.assertEquals(65536, f.toString().codePointAt(0));
        try {
            f.format("%2.2c", 'c');
            TestCase.fail("should throw IllegalFormatPrecisionException");
        } catch (IllegalFormatPrecisionException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        f.format("%C", 'w');
        // error on RI, throw UnknownFormatConversionException
        // RI do not support converter 'C'
        TestCase.assertEquals("W", f.toString());
        f = new Formatter(Locale.JAPAN);
        f.format("%Ced", 4369);
        // error on RI, throw UnknownFormatConversionException
        // RI do not support converter 'C'
        TestCase.assertEquals("\u1111ed", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for legal
     * Byte/Short/Integer/Long conversion type 'd'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_ByteShortIntegerLongConversionD() {
        final Object[][] triple = new Object[][]{ new Object[]{ 0, "%d", "0" }, new Object[]{ 0, "%10d", "         0" }, new Object[]{ 0, "%-1d", "0" }, new Object[]{ 0, "%+d", "+0" }, new Object[]{ 0, "% d", " 0" }, new Object[]{ 0, "%,d", "0" }, new Object[]{ 0, "%(d", "0" }, new Object[]{ 0, "%08d", "00000000" }, new Object[]{ 0, "%-+,(11d", "+0         " }, new Object[]{ 0, "%0 ,(11d", " 0000000000" }, new Object[]{ ((byte) (255)), "%d", "-1" }, new Object[]{ ((byte) (255)), "%10d", "        -1" }, new Object[]{ ((byte) (255)), "%-1d", "-1" }, new Object[]{ ((byte) (255)), "%+d", "-1" }, new Object[]{ ((byte) (255)), "% d", "-1" }, new Object[]{ ((byte) (255)), "%,d", "-1" }, new Object[]{ ((byte) (255)), "%(d", "(1)" }, new Object[]{ ((byte) (255)), "%08d", "-0000001" }, new Object[]{ ((byte) (255)), "%-+,(11d", "(1)        " }, new Object[]{ ((byte) (255)), "%0 ,(11d", "(000000001)" }, new Object[]{ ((short) (61731)), "%d", "-3805" }, new Object[]{ ((short) (61731)), "%10d", "     -3805" }, new Object[]{ ((short) (61731)), "%-1d", "-3805" }, new Object[]{ ((short) (61731)), "%+d", "-3805" }, new Object[]{ ((short) (61731)), "% d", "-3805" }, new Object[]{ ((short) (61731)), "%,d", "-3.805" }, new Object[]{ ((short) (61731)), "%(d", "(3805)" }, new Object[]{ ((short) (61731)), "%08d", "-0003805" }, new Object[]{ ((short) (61731)), "%-+,(11d", "(3.805)    " }, new Object[]{ ((short) (61731)), "%0 ,(11d", "(00003.805)" }, new Object[]{ 1193046, "%d", "1193046" }, new Object[]{ 1193046, "%10d", "   1193046" }, new Object[]{ 1193046, "%-1d", "1193046" }, new Object[]{ 1193046, "%+d", "+1193046" }, new Object[]{ 1193046, "% d", " 1193046" }, new Object[]{ 1193046, "%,d", "1.193.046" }, new Object[]{ 1193046, "%(d", "1193046" }, new Object[]{ 1193046, "%08d", "01193046" }, new Object[]{ 1193046, "%-+,(11d", "+1.193.046 " }, new Object[]{ 1193046, "%0 ,(11d", " 01.193.046" }, new Object[]{ -3, "%d", "-3" }, new Object[]{ -3, "%10d", "        -3" }, new Object[]{ -3, "%-1d", "-3" }, new Object[]{ -3, "%+d", "-3" }, new Object[]{ -3, "% d", "-3" }, new Object[]{ -3, "%,d", "-3" }, new Object[]{ -3, "%(d", "(3)" }, new Object[]{ -3, "%08d", "-0000003" }, new Object[]{ -3, "%-+,(11d", "(3)        " }, new Object[]{ -3, "%0 ,(11d", "(000000003)" }, new Object[]{ 124076833L, "%d", "124076833" }, new Object[]{ 124076833L, "%10d", " 124076833" }, new Object[]{ 124076833L, "%-1d", "124076833" }, new Object[]{ 124076833L, "%+d", "+124076833" }, new Object[]{ 124076833L, "% d", " 124076833" }, new Object[]{ 124076833L, "%,d", "124.076.833" }, new Object[]{ 124076833L, "%(d", "124076833" }, new Object[]{ 124076833L, "%08d", "124076833" }, new Object[]{ 124076833L, "%-+,(11d", "+124.076.833" }, new Object[]{ 124076833L, "%0 ,(11d", " 124.076.833" }, new Object[]{ -1L, "%d", "-1" }, new Object[]{ -1L, "%10d", "        -1" }, new Object[]{ -1L, "%-1d", "-1" }, new Object[]{ -1L, "%+d", "-1" }, new Object[]{ -1L, "% d", "-1" }, new Object[]{ -1L, "%,d", "-1" }, new Object[]{ -1L, "%(d", "(1)" }, new Object[]{ -1L, "%08d", "-0000001" }, new Object[]{ -1L, "%-+,(11d", "(1)        " }, new Object[]{ -1L, "%0 ,(11d", "(000000001)" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.GERMAN);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for legal
     * Byte/Short/Integer/Long conversion type 'o'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_ByteShortIntegerLongConversionO() {
        final Object[][] triple = new Object[][]{ new Object[]{ 0, "%o", "0" }, new Object[]{ 0, "%-6o", "0     " }, new Object[]{ 0, "%08o", "00000000" }, new Object[]{ 0, "%#o", "00" }, new Object[]{ 0, "%0#11o", "00000000000" }, new Object[]{ 0, "%-#9o", "00       " }, new Object[]{ ((byte) (255)), "%o", "377" }, new Object[]{ ((byte) (255)), "%-6o", "377   " }, new Object[]{ ((byte) (255)), "%08o", "00000377" }, new Object[]{ ((byte) (255)), "%#o", "0377" }, new Object[]{ ((byte) (255)), "%0#11o", "00000000377" }, new Object[]{ ((byte) (255)), "%-#9o", "0377     " }, new Object[]{ ((short) (61731)), "%o", "170443" }, new Object[]{ ((short) (61731)), "%-6o", "170443" }, new Object[]{ ((short) (61731)), "%08o", "00170443" }, new Object[]{ ((short) (61731)), "%#o", "0170443" }, new Object[]{ ((short) (61731)), "%0#11o", "00000170443" }, new Object[]{ ((short) (61731)), "%-#9o", "0170443  " }, new Object[]{ 1193046, "%o", "4432126" }, new Object[]{ 1193046, "%-6o", "4432126" }, new Object[]{ 1193046, "%08o", "04432126" }, new Object[]{ 1193046, "%#o", "04432126" }, new Object[]{ 1193046, "%0#11o", "00004432126" }, new Object[]{ 1193046, "%-#9o", "04432126 " }, new Object[]{ -3, "%o", "37777777775" }, new Object[]{ -3, "%-6o", "37777777775" }, new Object[]{ -3, "%08o", "37777777775" }, new Object[]{ -3, "%#o", "037777777775" }, new Object[]{ -3, "%0#11o", "037777777775" }, new Object[]{ -3, "%-#9o", "037777777775" }, new Object[]{ 124076833L, "%o", "731241441" }, new Object[]{ 124076833L, "%-6o", "731241441" }, new Object[]{ 124076833L, "%08o", "731241441" }, new Object[]{ 124076833L, "%#o", "0731241441" }, new Object[]{ 124076833L, "%0#11o", "00731241441" }, new Object[]{ 124076833L, "%-#9o", "0731241441" }, new Object[]{ -1L, "%o", "1777777777777777777777" }, new Object[]{ -1L, "%-6o", "1777777777777777777777" }, new Object[]{ -1L, "%08o", "1777777777777777777777" }, new Object[]{ -1L, "%#o", "01777777777777777777777" }, new Object[]{ -1L, "%0#11o", "01777777777777777777777" }, new Object[]{ -1L, "%-#9o", "01777777777777777777777" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.ITALY);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for legal
     * Byte/Short/Integer/Long conversion type 'x' and 'X'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_ByteShortIntegerLongConversionX() {
        final Object[][] triple = new Object[][]{ new Object[]{ 0, "%x", "0" }, new Object[]{ 0, "%-8x", "0       " }, new Object[]{ 0, "%06x", "000000" }, new Object[]{ 0, "%#x", "0x0" }, new Object[]{ 0, "%0#12x", "0x0000000000" }, new Object[]{ 0, "%-#9x", "0x0      " }, new Object[]{ ((byte) (255)), "%x", "ff" }, new Object[]{ ((byte) (255)), "%-8x", "ff      " }, new Object[]{ ((byte) (255)), "%06x", "0000ff" }, new Object[]{ ((byte) (255)), "%#x", "0xff" }, new Object[]{ ((byte) (255)), "%0#12x", "0x00000000ff" }, new Object[]{ ((byte) (255)), "%-#9x", "0xff     " }, new Object[]{ ((short) (61731)), "%x", "f123" }, new Object[]{ ((short) (61731)), "%-8x", "f123    " }, new Object[]{ ((short) (61731)), "%06x", "00f123" }, new Object[]{ ((short) (61731)), "%#x", "0xf123" }, new Object[]{ ((short) (61731)), "%0#12x", "0x000000f123" }, new Object[]{ ((short) (61731)), "%-#9x", "0xf123   " }, new Object[]{ 1193046, "%x", "123456" }, new Object[]{ 1193046, "%-8x", "123456  " }, new Object[]{ 1193046, "%06x", "123456" }, new Object[]{ 1193046, "%#x", "0x123456" }, new Object[]{ 1193046, "%0#12x", "0x0000123456" }, new Object[]{ 1193046, "%-#9x", "0x123456 " }, new Object[]{ -3, "%x", "fffffffd" }, new Object[]{ -3, "%-8x", "fffffffd" }, new Object[]{ -3, "%06x", "fffffffd" }, new Object[]{ -3, "%#x", "0xfffffffd" }, new Object[]{ -3, "%0#12x", "0x00fffffffd" }, new Object[]{ -3, "%-#9x", "0xfffffffd" }, new Object[]{ 124076833L, "%x", "7654321" }, new Object[]{ 124076833L, "%-8x", "7654321 " }, new Object[]{ 124076833L, "%06x", "7654321" }, new Object[]{ 124076833L, "%#x", "0x7654321" }, new Object[]{ 124076833L, "%0#12x", "0x0007654321" }, new Object[]{ 124076833L, "%-#9x", "0x7654321" }, new Object[]{ -1L, "%x", "ffffffffffffffff" }, new Object[]{ -1L, "%-8x", "ffffffffffffffff" }, new Object[]{ -1L, "%06x", "ffffffffffffffff" }, new Object[]{ -1L, "%#x", "0xffffffffffffffff" }, new Object[]{ -1L, "%0#12x", "0xffffffffffffffff" }, new Object[]{ -1L, "%-#9x", "0xffffffffffffffff" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f;
        for (int i = 0; i < (triple.length); i++) {
            f = new Formatter(Locale.FRANCE);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
            f = new Formatter(Locale.FRANCE);
            f.format(((String) (triple[i][pattern])), triple[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (triple[i][input])) + ",pattern[") + i) + "]:") + (triple[i][pattern])), triple[i][output], f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for Date/Time
     * conversion
     * J2ObjC: Noticing some incompatibilities on Mac/iOS.
     * public void test_formatLjava_lang_String$Ljava_lang_Object_DateTimeConversion() {
     * Formatter f = null;
     * Date now = new Date(1147327147578L);
     *
     * Calendar paris = Calendar.getInstance(TimeZone
     * .getTimeZone("Europe/Paris"), Locale.FRANCE);
     * paris.set(2006, 4, 8, 12, 0, 0);
     * paris.set(Calendar.MILLISECOND, 453);
     * Calendar china = Calendar.getInstance(
     * TimeZone.getTimeZone("GMT-08:00"), Locale.CHINA);
     * china.set(2006, 4, 8, 12, 0, 0);
     * china.set(Calendar.MILLISECOND, 609);
     *
     * final Object[][] lowerCaseGermanTriple = {
     * { 0L, 'a', "Do." },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'a', "So." },  //$NON-NLS-2$
     * { -1000L, 'a', "Do." },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'a', "Do." },  //$NON-NLS-2$
     * { paris, 'a', "Mo." },  //$NON-NLS-2$
     * { china, 'a', "Mo." },  //$NON-NLS-2$
     * { 0L, 'b', "Jan" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'b', "Aug" },  //$NON-NLS-2$
     * { -1000L, 'b', "Jan" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'b', "Mai" },  //$NON-NLS-2$
     * { paris, 'b', "Mai" },  //$NON-NLS-2$
     * { china, 'b', "Mai" },  //$NON-NLS-2$
     * { 0L, 'c', "Do. Jan 01 08:00:00 GMT+08:00 1970" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'c', "So. Aug 17 15:18:47 GMT+08:00 292278994" },  //$NON-NLS-2$
     * { -1000L, 'c', "Do. Jan 01 07:59:59 GMT+08:00 1970" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'c', "Do. Mai 11 13:59:07 GMT+08:00 2006" },  //$NON-NLS-2$
     * { paris, 'c', "Mo. Mai 08 12:00:00 MESZ 2006" },  //$NON-NLS-2$
     * { china, 'c', "Mo. Mai 08 12:00:00 GMT-08:00 2006" },  //$NON-NLS-2$
     * { 0L, 'd', "01" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'd', "17" },  //$NON-NLS-2$
     * { -1000L, 'd', "01" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'd', "11" },  //$NON-NLS-2$
     * { paris, 'd', "08" },  //$NON-NLS-2$
     * { china, 'd', "08" },  //$NON-NLS-2$
     * { 0L, 'e', "1" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'e', "17" },  //$NON-NLS-2$
     * { -1000L, 'e', "1" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'e', "11" },  //$NON-NLS-2$
     * { paris, 'e', "8" },  //$NON-NLS-2$
     * { china, 'e', "8" },  //$NON-NLS-2$
     * { 0L, 'h', "Jan" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'h', "Aug" },  //$NON-NLS-2$
     * { -1000L, 'h', "Jan" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'h', "Mai" },  //$NON-NLS-2$
     * { paris, 'h', "Mai" },  //$NON-NLS-2$
     * { china, 'h', "Mai" },  //$NON-NLS-2$
     * { 0L, 'j', "001" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'j', "229" },  //$NON-NLS-2$
     * { -1000L, 'j', "001" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'j', "131" },  //$NON-NLS-2$
     * { paris, 'j', "128" },  //$NON-NLS-2$
     * { china, 'j', "128" },  //$NON-NLS-2$
     * { 0L, 'k', "8" },  //$NON-NLS-2$
     * { Long.MAX_VALUE, 'k', "15" },  //$NON-NLS-2$
     * { -1000L, 'k', "7" },  //$NON-NLS-2$
     * { new Date(1147327147578L), 'k', "13" },  //$NON-NLS-2$
     * { paris, 'k', "12" },  //$NON-NLS-2$
     * { china, 'k', "12" },  //$NON-NLS-2$
     * { 0L, 'l', "8" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'l', "3" }, //$NON-NLS-2$
     * { -1000L, 'l', "7" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'l', "1" }, //$NON-NLS-2$
     * { paris, 'l', "12" }, //$NON-NLS-2$
     * { china, 'l', "12" }, //$NON-NLS-2$
     * { 0L, 'm', "01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'm', "08" }, //$NON-NLS-2$
     * { -1000L, 'm', "01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'm', "05" }, //$NON-NLS-2$
     * { paris, 'm', "05" }, //$NON-NLS-2$
     * { china, 'm', "05" }, //$NON-NLS-2$
     * { 0L, 'p', "vorm." }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'p', "nachm." }, //$NON-NLS-2$
     * { -1000L, 'p', "vorm." }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'p', "nachm." }, //$NON-NLS-2$
     * { paris, 'p', "nachm." }, //$NON-NLS-2$
     * { china, 'p', "nachm." }, //$NON-NLS-2$
     * { 0L, 'r', "08:00:00 vorm." }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'r', "03:18:47 nachm." }, //$NON-NLS-2$
     * { -1000L, 'r', "07:59:59 vorm." }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'r', "01:59:07 nachm." }, //$NON-NLS-2$
     * { paris, 'r', "12:00:00 nachm." }, //$NON-NLS-2$
     * { china, 'r', "12:00:00 nachm." }, //$NON-NLS-2$
     * { 0L, 's', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 's', "9223372036854775" }, //$NON-NLS-2$
     * { -1000L, 's', "-1" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 's', "1147327147" }, //$NON-NLS-2$
     * { paris, 's', "1147082400" }, //$NON-NLS-2$
     * { china, 's', "1147118400" }, //$NON-NLS-2$
     * { 0L, 'y', "70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'y', "94" }, //$NON-NLS-2$
     * { -1000L, 'y', "70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'y', "06" }, //$NON-NLS-2$
     * { paris, 'y', "06" }, //$NON-NLS-2$
     * { china, 'y', "06" }, //$NON-NLS-2$
     * { 0L, 'z', "+0800" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'z', "+0800" }, //$NON-NLS-2$
     * { -1000L, 'z', "+0800" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'z', "+0800" }, //$NON-NLS-2$
     * { paris, 'z', "+0100" }, //$NON-NLS-2$
     * { china, 'z', "-0800" }, //$NON-NLS-2$
     *
     * };
     *
     * final Object[][] lowerCaseFranceTriple = {
     * { 0L, 'a', "jeu." }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'a', "dim." }, //$NON-NLS-2$
     * { -1000L, 'a', "jeu." }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'a', "jeu." }, //$NON-NLS-2$
     * { paris, 'a', "lun." }, //$NON-NLS-2$
     * { china, 'a', "lun." }, //$NON-NLS-2$
     * { 0L, 'b', "janv." }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'b', "ao\u00fbt" }, //$NON-NLS-2$
     * { -1000L, 'b', "janv." }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'b', "mai" }, //$NON-NLS-2$
     * { paris, 'b', "mai" }, //$NON-NLS-2$
     * { china, 'b', "mai" }, //$NON-NLS-2$
     * { 0L, 'c', "jeu. janv. 01 08:00:00 UTC+08:00 1970" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'c', "dim. ao\u00fbt 17 15:18:47 UTC+08:00 292278994" }, //$NON-NLS-2$
     * { -1000L, 'c', "jeu. janv. 01 07:59:59 UTC+08:00 1970" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'c', "jeu. mai 11 13:59:07 UTC+08:00 2006" }, //$NON-NLS-2$
     * { paris, 'c', "lun. mai 08 12:00:00 HAEC 2006" }, //$NON-NLS-2$
     * { china, 'c', "lun. mai 08 12:00:00 UTC-08:00 2006" }, //$NON-NLS-2$
     * { 0L, 'd', "01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'd', "17" }, //$NON-NLS-2$
     * { -1000L, 'd', "01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'd', "11" }, //$NON-NLS-2$
     * { paris, 'd', "08" }, //$NON-NLS-2$
     * { china, 'd', "08" }, //$NON-NLS-2$
     * { 0L, 'e', "1" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'e', "17" }, //$NON-NLS-2$
     * { -1000L, 'e', "1" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'e', "11" }, //$NON-NLS-2$
     * { paris, 'e', "8" }, //$NON-NLS-2$
     * { china, 'e', "8" }, //$NON-NLS-2$
     * { 0L, 'h', "janv." }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'h', "ao\u00fbt" }, //$NON-NLS-2$
     * { -1000L, 'h', "janv." }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'h', "mai" }, //$NON-NLS-2$
     * { paris, 'h', "mai" }, //$NON-NLS-2$
     * { china, 'h', "mai" }, //$NON-NLS-2$
     * { 0L, 'j', "001" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'j', "229" }, //$NON-NLS-2$
     * { -1000L, 'j', "001" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'j', "131" }, //$NON-NLS-2$
     * { paris, 'j', "128" }, //$NON-NLS-2$
     * { china, 'j', "128" }, //$NON-NLS-2$
     * { 0L, 'k', "8" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'k', "15" }, //$NON-NLS-2$
     * { -1000L, 'k', "7" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'k', "13" }, //$NON-NLS-2$
     * { paris, 'k', "12" }, //$NON-NLS-2$
     * { china, 'k', "12" }, //$NON-NLS-2$
     * { 0L, 'l', "8" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'l', "3" }, //$NON-NLS-2$
     * { -1000L, 'l', "7" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'l', "1" }, //$NON-NLS-2$
     * { paris, 'l', "12" }, //$NON-NLS-2$
     * { china, 'l', "12" }, //$NON-NLS-2$
     * { 0L, 'm', "01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'm', "08" }, //$NON-NLS-2$
     * { -1000L, 'm', "01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'm', "05" }, //$NON-NLS-2$
     * { paris, 'm', "05" }, //$NON-NLS-2$
     * { china, 'm', "05" }, //$NON-NLS-2$
     * { 0L, 'p', "am" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'p', "pm" }, //$NON-NLS-2$
     * { -1000L, 'p', "am" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'p', "pm" }, //$NON-NLS-2$
     * { paris, 'p', "pm" }, //$NON-NLS-2$
     * { china, 'p', "pm" }, //$NON-NLS-2$
     * { 0L, 'r', "08:00:00 AM" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'r', "03:18:47 PM" }, //$NON-NLS-2$
     * { -1000L, 'r', "07:59:59 AM" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'r', "01:59:07 PM" }, //$NON-NLS-2$
     * { paris, 'r', "12:00:00 PM" }, //$NON-NLS-2$
     * { china, 'r', "12:00:00 PM" }, //$NON-NLS-2$
     * { 0L, 's', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 's', "9223372036854775" }, //$NON-NLS-2$
     * { -1000L, 's', "-1" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 's', "1147327147" }, //$NON-NLS-2$
     * { paris, 's', "1147082400" }, //$NON-NLS-2$
     * { china, 's', "1147118400" }, //$NON-NLS-2$
     * { 0L, 'y', "70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'y', "94" }, //$NON-NLS-2$
     * { -1000L, 'y', "70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'y', "06" }, //$NON-NLS-2$
     * { paris, 'y', "06" }, //$NON-NLS-2$
     * { china, 'y', "06" }, //$NON-NLS-2$
     * { 0L, 'z', "+0800" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'z', "+0800" }, //$NON-NLS-2$
     * { -1000L, 'z', "+0800" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'z', "+0800" }, //$NON-NLS-2$
     * { paris, 'z', "+0100" }, //$NON-NLS-2$
     * { china, 'z', "-0800" }, //$NON-NLS-2$
     *
     * };
     *
     * final Object[][] lowerCaseJapanTriple = {
     * { 0L, 'a', "\u6728" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'a', "\u65e5" }, //$NON-NLS-2$
     * { -1000L, 'a', "\u6728" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'a', "\u6728" }, //$NON-NLS-2$
     * { paris, 'a', "\u6708" }, //$NON-NLS-2$
     * { china, 'a', "\u6708" }, //$NON-NLS-2$
     * { 0L, 'b', "1\u6708" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'b', "8\u6708" }, //$NON-NLS-2$
     * { -1000L, 'b', "1\u6708" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'b', "5\u6708" }, //$NON-NLS-2$
     * { paris, 'b', "5\u6708" }, //$NON-NLS-2$
     * { china, 'b', "5\u6708" }, //$NON-NLS-2$
     * { 0L, 'c', "\u6728 1\u6708 01 08:00:00 GMT+08:00 1970" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'c', "\u65e5 8\u6708 17 15:18:47 GMT+08:00 292278994" }, //$NON-NLS-2$
     * { -1000L, 'c', "\u6728 1\u6708 01 07:59:59 GMT+08:00 1970" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'c', "\u6728 5\u6708 11 13:59:07 GMT+08:00 2006" }, //$NON-NLS-2$
     * { paris, 'c', "\u6708 5\u6708 08 12:00:00 GMT+02:00 2006" }, //$NON-NLS-2$
     * { china, 'c', "\u6708 5\u6708 08 12:00:00 GMT-08:00 2006" }, //$NON-NLS-2$
     * { 0L, 'd', "01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'd', "17" }, //$NON-NLS-2$
     * { -1000L, 'd', "01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'd', "11" }, //$NON-NLS-2$
     * { paris, 'd', "08" }, //$NON-NLS-2$
     * { china, 'd', "08" }, //$NON-NLS-2$
     * { 0L, 'e', "1" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'e', "17" }, //$NON-NLS-2$
     * { -1000L, 'e', "1" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'e', "11" }, //$NON-NLS-2$
     * { paris, 'e', "8" }, //$NON-NLS-2$
     * { china, 'e', "8" }, //$NON-NLS-2$
     * { 0L, 'h', "1\u6708" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'h', "8\u6708" }, //$NON-NLS-2$
     * { -1000L, 'h', "1\u6708" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'h', "5\u6708" }, //$NON-NLS-2$
     * { paris, 'h', "5\u6708" }, //$NON-NLS-2$
     * { china, 'h', "5\u6708" }, //$NON-NLS-2$
     * { 0L, 'j', "001" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'j', "229" }, //$NON-NLS-2$
     * { -1000L, 'j', "001" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'j', "131" }, //$NON-NLS-2$
     * { paris, 'j', "128" }, //$NON-NLS-2$
     * { china, 'j', "128" }, //$NON-NLS-2$
     * { 0L, 'k', "8" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'k', "15" }, //$NON-NLS-2$
     * { -1000L, 'k', "7" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'k', "13" }, //$NON-NLS-2$
     * { paris, 'k', "12" }, //$NON-NLS-2$
     * { china, 'k', "12" }, //$NON-NLS-2$
     * { 0L, 'l', "8" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'l', "3" }, //$NON-NLS-2$
     * { -1000L, 'l', "7" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'l', "1" }, //$NON-NLS-2$
     * { paris, 'l', "12" }, //$NON-NLS-2$
     * { china, 'l', "12" }, //$NON-NLS-2$
     * { 0L, 'm', "01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'm', "08" }, //$NON-NLS-2$
     * { -1000L, 'm', "01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'm', "05" }, //$NON-NLS-2$
     * { paris, 'm', "05" }, //$NON-NLS-2$
     * { china, 'm', "05" }, //$NON-NLS-2$
     * { 0L, 'p', "\u5348\u524d" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'p', "\u5348\u5f8c" }, //$NON-NLS-2$
     * { -1000L, 'p', "\u5348\u524d" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'p', "\u5348\u5f8c" }, //$NON-NLS-2$
     * { paris, 'p', "\u5348\u5f8c" }, //$NON-NLS-2$
     * { china, 'p', "\u5348\u5f8c" }, //$NON-NLS-2$
     * { 0L, 'r', "08:00:00 \u5348\u524d" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'r', "03:18:47 \u5348\u5f8c" }, //$NON-NLS-2$
     * { -1000L, 'r', "07:59:59 \u5348\u524d" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'r', "01:59:07 \u5348\u5f8c" }, //$NON-NLS-2$
     * { paris, 'r', "12:00:00 \u5348\u5f8c" }, //$NON-NLS-2$
     * { china, 'r', "12:00:00 \u5348\u5f8c" }, //$NON-NLS-2$
     * { 0L, 's', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 's', "9223372036854775" }, //$NON-NLS-2$
     * { -1000L, 's', "-1" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 's', "1147327147" }, //$NON-NLS-2$
     * { paris, 's', "1147082400" }, //$NON-NLS-2$
     * { china, 's', "1147118400" }, //$NON-NLS-2$
     * { 0L, 'y', "70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'y', "94" }, //$NON-NLS-2$
     * { -1000L, 'y', "70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'y', "06" }, //$NON-NLS-2$
     * { paris, 'y', "06" }, //$NON-NLS-2$
     * { china, 'y', "06" }, //$NON-NLS-2$
     * { 0L, 'z', "+0800" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'z', "+0800" }, //$NON-NLS-2$
     * { -1000L, 'z', "+0800" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'z', "+0800" }, //$NON-NLS-2$
     * { paris, 'z', "+0100" }, //$NON-NLS-2$
     * { china, 'z', "-0800" }, //$NON-NLS-2$
     * };
     *
     * final int input = 0;
     * final int pattern = 1;
     * final int output = 2;
     * for (int i = 0; i < 90; i++) {
     * // go through legal conversion
     * String formatSpecifier = "%t" + lowerCaseGermanTriple[i][pattern]; //$NON-NLS-2$
     * String formatSpecifierUpper = "%T" + lowerCaseGermanTriple[i][pattern]; //$NON-NLS-2$
     * // test '%t'
     * f = new Formatter(Locale.GERMAN);
     * f.format(formatSpecifier, lowerCaseGermanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + lowerCaseGermanTriple[i][input], //$NON-NLS-2$
     * lowerCaseGermanTriple[i][output], f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.FRANCE, formatSpecifier, lowerCaseFranceTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + lowerCaseFranceTriple[i][input], //$NON-NLS-2$
     * lowerCaseFranceTriple[i][output], f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.JAPAN, formatSpecifier, lowerCaseJapanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + lowerCaseJapanTriple[i][input], //$NON-NLS-2$
     * lowerCaseJapanTriple[i][output], f.toString());
     *
     * // test '%T'
     * f = new Formatter(Locale.GERMAN);
     * f.format(formatSpecifierUpper, lowerCaseGermanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + lowerCaseGermanTriple[i][input], //$NON-NLS-2$
     * ((String) lowerCaseGermanTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.FRANCE, formatSpecifierUpper, lowerCaseFranceTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + lowerCaseFranceTriple[i][input], //$NON-NLS-2$
     * ((String) lowerCaseFranceTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.JAPAN, formatSpecifierUpper, lowerCaseJapanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + lowerCaseJapanTriple[i][input], //$NON-NLS-2$
     * ((String) lowerCaseJapanTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     * }
     *
     * final Object[][] upperCaseGermanTriple = {
     * { 0L, 'A', "Donnerstag" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'A', "Sonntag" }, //$NON-NLS-2$
     * { -1000L, 'A', "Donnerstag" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'A', "Donnerstag" }, //$NON-NLS-2$
     * { paris, 'A', "Montag" }, //$NON-NLS-2$
     * { china, 'A', "Montag" }, //$NON-NLS-2$
     * { 0L, 'B', "Januar" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'B', "August" }, //$NON-NLS-2$
     * { -1000L, 'B', "Januar" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'B', "Mai" }, //$NON-NLS-2$
     * { paris, 'B', "Mai" }, //$NON-NLS-2$
     * { china, 'B', "Mai" }, //$NON-NLS-2$
     * { 0L, 'C', "19" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'C', "2922789" }, //$NON-NLS-2$
     * { -1000L, 'C', "19" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'C', "20" }, //$NON-NLS-2$
     * { paris, 'C', "20" }, //$NON-NLS-2$
     * { china, 'C', "20" }, //$NON-NLS-2$
     * { 0L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'D', "08/17/94" }, //$NON-NLS-2$
     * { -1000L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'D', "05/11/06" }, //$NON-NLS-2$
     * { paris, 'D', "05/08/06" }, //$NON-NLS-2$
     * { china, 'D', "05/08/06" }, //$NON-NLS-2$
     * { 0L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'F', "292278994-08-17" }, //$NON-NLS-2$
     * { -1000L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'F', "2006-05-11" }, //$NON-NLS-2$
     * { paris, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { china, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { 0L, 'H', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'H', "15" }, //$NON-NLS-2$
     * { -1000L, 'H', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'H', "13" }, //$NON-NLS-2$
     * { paris, 'H', "12" }, //$NON-NLS-2$
     * { china, 'H', "12" }, //$NON-NLS-2$
     * { 0L, 'I', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'I', "03" }, //$NON-NLS-2$
     * { -1000L, 'I', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'I', "01" }, //$NON-NLS-2$
     * { paris, 'I', "12" }, //$NON-NLS-2$
     * { china, 'I', "12" }, //$NON-NLS-2$
     * { 0L, 'L', "000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'L', "807" }, //$NON-NLS-2$
     * { -1000L, 'L', "000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'L', "578" }, //$NON-NLS-2$
     * { paris, 'L', "453" }, //$NON-NLS-2$
     * { china, 'L', "609" }, //$NON-NLS-2$
     * { 0L, 'M', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'M', "18" }, //$NON-NLS-2$
     * { -1000L, 'M', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'M', "59" }, //$NON-NLS-2$
     * { paris, 'M', "00" }, //$NON-NLS-2$
     * { china, 'M', "00" }, //$NON-NLS-2$
     * { 0L, 'N', "000000000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'N', "807000000" }, //$NON-NLS-2$
     * { -1000L, 'N', "000000000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'N', "578000000" }, //$NON-NLS-2$
     * { paris, 'N', "609000000" }, //$NON-NLS-2$
     * { china, 'N', "609000000" }, //$NON-NLS-2$
     * { 0L, 'Q', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Q', "9223372036854775807" }, //$NON-NLS-2$
     * { -1000L, 'Q', "-1000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Q', "1147327147578" }, //$NON-NLS-2$
     * { paris, 'Q', "1147082400453" }, //$NON-NLS-2$
     * { china, 'Q', "1147118400609" }, //$NON-NLS-2$
     * { 0L, 'R', "08:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'R', "15:18" }, //$NON-NLS-2$
     * { -1000L, 'R', "07:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'R', "13:59" }, //$NON-NLS-2$
     * { paris, 'R', "12:00" }, //$NON-NLS-2$
     * { china, 'R', "12:00" }, //$NON-NLS-2$
     * { 0L, 'S', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'S', "47" }, //$NON-NLS-2$
     * { -1000L, 'S', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'S', "07" }, //$NON-NLS-2$
     * { paris, 'S', "00" }, //$NON-NLS-2$
     * { china, 'S', "00" }, //$NON-NLS-2$
     * { 0L, 'T', "08:00:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'T', "15:18:47" }, //$NON-NLS-2$
     * { -1000L, 'T', "07:59:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'T', "13:59:07" }, //$NON-NLS-2$
     * { paris, 'T', "12:00:00" }, //$NON-NLS-2$
     * { china, 'T', "12:00:00" }, //$NON-NLS-2$
     * { 0L, 'Y', "1970" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Y', "292278994" }, //$NON-NLS-2$
     * { -1000L, 'Y', "1970" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Y', "2006" }, //$NON-NLS-2$
     * { paris, 'Y', "2006" }, //$NON-NLS-2$
     * { china, 'Y', "2006" }, //$NON-NLS-2$
     * { 0L, 'Z', "CST" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Z', "CST" }, //$NON-NLS-2$
     * { -1000L, 'Z', "CST" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Z', "CST" }, //$NON-NLS-2$
     * { paris, 'Z', "CEST" }, //$NON-NLS-2$
     * { china, 'Z', "GMT-08:00" }, //$NON-NLS-2$
     *
     * };
     *
     * final Object[][] upperCaseFranceTriple = {
     * { 0L, 'A', "jeudi" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'A', "dimanche" }, //$NON-NLS-2$
     * { -1000L, 'A', "jeudi" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'A', "jeudi" }, //$NON-NLS-2$
     * { paris, 'A', "lundi" }, //$NON-NLS-2$
     * { china, 'A', "lundi" }, //$NON-NLS-2$
     * { 0L, 'B', "janvier" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'B', "ao\u00fbt" }, //$NON-NLS-2$
     * { -1000L, 'B', "janvier" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'B', "mai" }, //$NON-NLS-2$
     * { paris, 'B', "mai" }, //$NON-NLS-2$
     * { china, 'B', "mai" }, //$NON-NLS-2$
     * { 0L, 'C', "19" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'C', "2922789" }, //$NON-NLS-2$
     * { -1000L, 'C', "19" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'C', "20" }, //$NON-NLS-2$
     * { paris, 'C', "20" }, //$NON-NLS-2$
     * { china, 'C', "20" }, //$NON-NLS-2$
     * { 0L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'D', "08/17/94" }, //$NON-NLS-2$
     * { -1000L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'D', "05/11/06" }, //$NON-NLS-2$
     * { paris, 'D', "05/08/06" }, //$NON-NLS-2$
     * { china, 'D', "05/08/06" }, //$NON-NLS-2$
     * { 0L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'F', "292278994-08-17" }, //$NON-NLS-2$
     * { -1000L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'F', "2006-05-11" }, //$NON-NLS-2$
     * { paris, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { china, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { 0L, 'H', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'H', "15" }, //$NON-NLS-2$
     * { -1000L, 'H', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'H', "13" }, //$NON-NLS-2$
     * { paris, 'H', "12" }, //$NON-NLS-2$
     * { china, 'H', "12" }, //$NON-NLS-2$
     * { 0L, 'I', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'I', "03" }, //$NON-NLS-2$
     * { -1000L, 'I', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'I', "01" }, //$NON-NLS-2$
     * { paris, 'I', "12" }, //$NON-NLS-2$
     * { china, 'I', "12" }, //$NON-NLS-2$
     * { 0L, 'L', "000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'L', "807" }, //$NON-NLS-2$
     * { -1000L, 'L', "000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'L', "578" }, //$NON-NLS-2$
     * { paris, 'L', "453" }, //$NON-NLS-2$
     * { china, 'L', "609" }, //$NON-NLS-2$
     * { 0L, 'M', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'M', "18" }, //$NON-NLS-2$
     * { -1000L, 'M', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'M', "59" }, //$NON-NLS-2$
     * { paris, 'M', "00" }, //$NON-NLS-2$
     * { china, 'M', "00" }, //$NON-NLS-2$
     * { 0L, 'N', "000000000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'N', "807000000" }, //$NON-NLS-2$
     * { -1000L, 'N', "000000000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'N', "578000000" }, //$NON-NLS-2$
     * { paris, 'N', "453000000" }, //$NON-NLS-2$
     * { china, 'N', "468000000" }, //$NON-NLS-2$
     * { 0L, 'Q', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Q', "9223372036854775807" }, //$NON-NLS-2$
     * { -1000L, 'Q', "-1000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Q', "1147327147578" }, //$NON-NLS-2$
     * { paris, 'Q', "1147082400453" }, //$NON-NLS-2$
     * { china, 'Q', "1147118400609" }, //$NON-NLS-2$
     * { 0L, 'R', "08:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'R', "15:18" }, //$NON-NLS-2$
     * { -1000L, 'R', "07:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'R', "13:59" }, //$NON-NLS-2$
     * { paris, 'R', "12:00" }, //$NON-NLS-2$
     * { china, 'R', "12:00" }, //$NON-NLS-2$
     * { 0L, 'S', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'S', "47" }, //$NON-NLS-2$
     * { -1000L, 'S', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'S', "07" }, //$NON-NLS-2$
     * { paris, 'S', "00" }, //$NON-NLS-2$
     * { china, 'S', "00" }, //$NON-NLS-2$
     * { 0L, 'T', "08:00:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'T', "15:18:47" }, //$NON-NLS-2$
     * { -1000L, 'T', "07:59:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'T', "13:59:07" }, //$NON-NLS-2$
     * { paris, 'T', "12:00:00" }, //$NON-NLS-2$
     * { china, 'T', "12:00:00" }, //$NON-NLS-2$
     * { 0L, 'Y', "1970" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Y', "292278994" }, //$NON-NLS-2$
     * { -1000L, 'Y', "1970" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Y', "2006" }, //$NON-NLS-2$
     * { paris, 'Y', "2006" }, //$NON-NLS-2$
     * { china, 'Y', "2006" }, //$NON-NLS-2$
     * { 0L, 'Z', "CST" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Z', "CST" }, //$NON-NLS-2$
     * { -1000L, 'Z', "CST" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Z', "CST" }, //$NON-NLS-2$
     * { paris, 'Z', "CEST" }, //$NON-NLS-2$
     * { china, 'Z', "GMT-08:00" }, //$NON-NLS-2$
     *
     * };
     *
     * final Object[][] upperCaseJapanTriple = {
     * { 0L, 'A', "\u6728\u66dc\u65e5" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'A', "\u65e5\u66dc\u65e5" }, //$NON-NLS-2$
     * { -1000L, 'A', "\u6728\u66dc\u65e5" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'A', "\u6728\u66dc\u65e5" }, //$NON-NLS-2$
     * { paris, 'A', "\u6708\u66dc\u65e5" }, //$NON-NLS-2$
     * { china, 'A', "\u6708\u66dc\u65e5" }, //$NON-NLS-2$
     * { 0L, 'B', "1\u6708" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'B', "8\u6708" }, //$NON-NLS-2$
     * { -1000L, 'B', "1\u6708" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'B', "5\u6708" }, //$NON-NLS-2$
     * { paris, 'B', "5\u6708" }, //$NON-NLS-2$
     * { china, 'B', "5\u6708" }, //$NON-NLS-2$
     * { 0L, 'C', "19" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'C', "2922789" }, //$NON-NLS-2$
     * { -1000L, 'C', "19" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'C', "20" }, //$NON-NLS-2$
     * { paris, 'C', "20" }, //$NON-NLS-2$
     * { china, 'C', "20" }, //$NON-NLS-2$
     * { 0L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'D', "08/17/94" }, //$NON-NLS-2$
     * { -1000L, 'D', "01/01/70" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'D', "05/11/06" }, //$NON-NLS-2$
     * { paris, 'D', "05/08/06" }, //$NON-NLS-2$
     * { china, 'D', "05/08/06" }, //$NON-NLS-2$
     * { 0L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'F', "292278994-08-17" }, //$NON-NLS-2$
     * { -1000L, 'F', "1970-01-01" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'F', "2006-05-11" }, //$NON-NLS-2$
     * { paris, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { china, 'F', "2006-05-08" }, //$NON-NLS-2$
     * { 0L, 'H', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'H', "15" }, //$NON-NLS-2$
     * { -1000L, 'H', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'H', "13" }, //$NON-NLS-2$
     * { paris, 'H', "12" }, //$NON-NLS-2$
     * { china, 'H', "12" }, //$NON-NLS-2$
     * { 0L, 'I', "08" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'I', "03" }, //$NON-NLS-2$
     * { -1000L, 'I', "07" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'I', "01" }, //$NON-NLS-2$
     * { paris, 'I', "12" }, //$NON-NLS-2$
     * { china, 'I', "12" }, //$NON-NLS-2$
     * { 0L, 'L', "000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'L', "807" }, //$NON-NLS-2$
     * { -1000L, 'L', "000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'L', "578" }, //$NON-NLS-2$
     * { paris, 'L', "453" }, //$NON-NLS-2$
     * { china, 'L', "609" }, //$NON-NLS-2$
     * { 0L, 'M', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'M', "18" }, //$NON-NLS-2$
     * { -1000L, 'M', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'M', "59" }, //$NON-NLS-2$
     * { paris, 'M', "00" }, //$NON-NLS-2$
     * { china, 'M', "00" }, //$NON-NLS-2$
     * { 0L, 'N', "000000000" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'N', "807000000" }, //$NON-NLS-2$
     * { -1000L, 'N', "000000000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'N', "578000000" }, //$NON-NLS-2$
     * { paris, 'N', "453000000" }, //$NON-NLS-2$
     * { china, 'N', "468000000" }, //$NON-NLS-2$
     * { 0L, 'Q', "0" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Q', "9223372036854775807" }, //$NON-NLS-2$
     * { -1000L, 'Q', "-1000" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Q', "1147327147578" }, //$NON-NLS-2$
     * { paris, 'Q', "1147082400453" }, //$NON-NLS-2$
     * { china, 'Q', "1147118400609" }, //$NON-NLS-2$
     * { 0L, 'R', "08:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'R', "15:18" }, //$NON-NLS-2$
     * { -1000L, 'R', "07:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'R', "13:59" }, //$NON-NLS-2$
     * { paris, 'R', "12:00" }, //$NON-NLS-2$
     * { china, 'R', "12:00" }, //$NON-NLS-2$
     * { 0L, 'S', "00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'S', "47" }, //$NON-NLS-2$
     * { -1000L, 'S', "59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'S', "07" }, //$NON-NLS-2$
     * { paris, 'S', "00" }, //$NON-NLS-2$
     * { china, 'S', "00" }, //$NON-NLS-2$
     * { 0L, 'T', "08:00:00" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'T', "15:18:47" }, //$NON-NLS-2$
     * { -1000L, 'T', "07:59:59" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'T', "13:59:07" }, //$NON-NLS-2$
     * { paris, 'T', "12:00:00" }, //$NON-NLS-2$
     * { china, 'T', "12:00:00" }, //$NON-NLS-2$
     * { 0L, 'Y', "1970" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Y', "292278994" }, //$NON-NLS-2$
     * { -1000L, 'Y', "1970" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Y', "2006" }, //$NON-NLS-2$
     * { paris, 'Y', "2006" }, //$NON-NLS-2$
     * { china, 'Y', "2006" }, //$NON-NLS-2$
     * { 0L, 'Z', "CST" }, //$NON-NLS-2$
     * { Long.MAX_VALUE, 'Z', "CST" }, //$NON-NLS-2$
     * { -1000L, 'Z', "CST" }, //$NON-NLS-2$
     * { new Date(1147327147578L), 'Z', "CST" }, //$NON-NLS-2$
     * { paris, 'Z', "CEST" }, //$NON-NLS-2$
     * { china, 'Z', "GMT-08:00" }, //$NON-NLS-2$
     * };
     *
     *
     * for (int i = 0; i < 90; i++) {
     * String formatSpecifier = "%t" + upperCaseGermanTriple[i][pattern]; //$NON-NLS-2$
     * String formatSpecifierUpper = "%T" + upperCaseGermanTriple[i][pattern]; //$NON-NLS-2$
     * if ((Character) upperCaseGermanTriple[i][pattern] == 'N') {
     * // result can't be predicted on RI, so skip this test
     * continue;
     * }
     * // test '%t'
     * f = new Formatter(Locale.JAPAN);
     * f.format(formatSpecifier, upperCaseJapanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + upperCaseJapanTriple[i][input], //$NON-NLS-2$
     * upperCaseJapanTriple[i][output], f.toString());
     *
     * f = new Formatter(Locale.JAPAN);
     * f.format(Locale.GERMAN, formatSpecifier, upperCaseGermanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + upperCaseGermanTriple[i][input], //$NON-NLS-2$
     * upperCaseGermanTriple[i][output], f.toString());
     *
     * f = new Formatter(Locale.JAPAN);
     * f.format(Locale.FRANCE, formatSpecifier, upperCaseFranceTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifier //$NON-NLS-2$
     * + " Argument: " + upperCaseFranceTriple[i][input], //$NON-NLS-2$
     * upperCaseFranceTriple[i][output], f.toString());
     *
     * // test '%T'
     * f = new Formatter(Locale.GERMAN);
     * f.format(formatSpecifierUpper, upperCaseGermanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + upperCaseGermanTriple[i][input], //$NON-NLS-2$
     * ((String) upperCaseGermanTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.JAPAN, formatSpecifierUpper, upperCaseJapanTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + upperCaseJapanTriple[i][input], //$NON-NLS-2$
     * ((String) upperCaseJapanTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     *
     * f = new Formatter(Locale.GERMAN);
     * f.format(Locale.FRANCE, formatSpecifierUpper, upperCaseFranceTriple[i][input]);
     * assertEquals("Format pattern: " + formatSpecifierUpper //$NON-NLS-2$
     * + " Argument: " + upperCaseFranceTriple[i][input], //$NON-NLS-2$
     * ((String) upperCaseFranceTriple[i][output])
     * .toUpperCase(Locale.US), f.toString());
     * }
     *
     * f = new Formatter(Locale.US);
     * f.format("%-10ta", now); //$NON-NLS-2$
     * assertEquals("Thu       ", f.toString()); //$NON-NLS-2$
     *
     * f = new Formatter(Locale.US);
     * f.format("%10000000000000000000000000000000001ta", now); //$NON-NLS-2$
     * assertEquals("Thu", f.toString().trim()); //$NON-NLS-2$
     * }
     */
    /**
     * java.util.Formatter#format(String, Object...) for null argment for
     * Byte/Short/Integer/Long/BigInteger conversion
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_ByteShortIntegerLongNullConversion() {
        Formatter f = new Formatter(Locale.FRANCE);
        f.format("%d%<o%<x%<5X", ((Integer) (null)));
        TestCase.assertEquals("nullnullnull NULL", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%d%<#03o %<0#4x%<6X", ((Long) (null)));
        TestCase.assertEquals("nullnull null  NULL", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%(+,07d%<o %<x%<6X", ((Byte) (null)));
        TestCase.assertEquals("   nullnull null  NULL", f.toString());
        f = new Formatter(Locale.ITALY);
        f.format("%(+,07d%<o %<x%<0#6X", ((Short) (null)));
        TestCase.assertEquals("   nullnull null  NULL", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%(+,-7d%<( o%<+(x %<( 06X", ((BigInteger) (null)));
        TestCase.assertEquals("null   nullnull   NULL", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for legal
     * BigInteger conversion type 'd'
     */
    public void test_formatLjava_lang_String$LBigInteger() {
        final Object[][] tripleD = new Object[][]{ new Object[]{ new BigInteger("123456789012345678901234567890"), "%d", "123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%10d", "123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-1d", "123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%+d", "+123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "% d", " 123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%,d", "123.456.789.012.345.678.901.234.567.890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%(d", "123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%08d", "123456789012345678901234567890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-+,(11d", "+123.456.789.012.345.678.901.234.567.890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%0 ,(11d", " 123.456.789.012.345.678.901.234.567.890" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%10d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-1d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%+d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "% d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%,d", "-9.876.543.210.987.654.321.098.765.432.100.000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%(d", "(9876543210987654321098765432100000)" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%08d", "-9876543210987654321098765432100000" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-+,(11d", "(9.876.543.210.987.654.321.098.765.432.100.000)" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%0 ,(11d", "(9.876.543.210.987.654.321.098.765.432.100.000)" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
         };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        Formatter f;
        for (int i = 0; i < (tripleD.length); i++) {
            f = new Formatter(Locale.GERMAN);
            f.format(((String) (tripleD[i][pattern])), tripleD[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleD[i][input])) + ",pattern[") + i) + "]:") + (tripleD[i][pattern])), tripleD[i][output], f.toString());
        }
        final Object[][] tripleO = new Object[][]{ new Object[]{ new BigInteger("123456789012345678901234567890"), "%o", "143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-6o", "143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%08o", "143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%#o", "0143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%0#11o", "0143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-#9o", "0143564417755415637016711617605322" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%o", "-36336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-6o", "-36336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%08o", "-36336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%#o", "-036336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%0#11o", "-036336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-#9o", "-036336340043453651353467270113157312240" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
         };
        for (int i = 0; i < (tripleO.length); i++) {
            f = new Formatter(Locale.ITALY);
            f.format(((String) (tripleO[i][pattern])), tripleO[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleO[i][input])) + ",pattern[") + i) + "]:") + (tripleO[i][pattern])), tripleO[i][output], f.toString());
        }
        final Object[][] tripleX = new Object[][]{ new Object[]{ new BigInteger("123456789012345678901234567890"), "%x", "18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-8x", "18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%06x", "18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%#x", "0x18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%0#12x", "0x18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("123456789012345678901234567890"), "%-#9x", "0x18ee90ff6c373e0ee4e3f0ad2" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%x", "-1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-8x", "-1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%06x", "-1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%#x", "-0x1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%0#12x", "-0x1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
        , new Object[]{ new BigInteger("-9876543210987654321098765432100000"), "%-#9x", "-0x1e6f380472bd4bae6eb8259bd94a0" }// $NON-NLS-2$
        // $NON-NLS-2$
        // $NON-NLS-2$
         };
        for (int i = 0; i < (tripleX.length); i++) {
            f = new Formatter(Locale.FRANCE);
            f.format(((String) (tripleX[i][pattern])), tripleX[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleX[i][input])) + ",pattern[") + i) + "]:") + (tripleX[i][pattern])), tripleX[i][output], f.toString());
        }
        f = new Formatter(Locale.GERMAN);
        f.format("%(+,-7d%<( o%<+(x %<( 06X", ((BigInteger) (null)));
        TestCase.assertEquals("null   nullnull   NULL", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for padding of
     * BigInteger conversion
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigIntegerPaddingConversion() {
        Formatter f = null;
        BigInteger bigInt = new BigInteger("123456789012345678901234567890");
        f = new Formatter(Locale.GERMAN);
        f.format("%32d", bigInt);
        TestCase.assertEquals("  123456789012345678901234567890", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%+32x", bigInt);
        TestCase.assertEquals("      +18ee90ff6c373e0ee4e3f0ad2", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("% 32o", bigInt);
        TestCase.assertEquals(" 143564417755415637016711617605322", f.toString());
        BigInteger negBigInt = new BigInteger("-1234567890123456789012345678901234567890");
        f = new Formatter(Locale.GERMAN);
        f.format("%( 040X", negBigInt);
        TestCase.assertEquals("(000003A0C92075C0DBF3B8ACBC5F96CE3F0AD2)", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%+(045d", negBigInt);
        TestCase.assertEquals("(0001234567890123456789012345678901234567890)", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%+,-(60d", negBigInt);
        TestCase.assertEquals("(1.234.567.890.123.456.789.012.345.678.901.234.567.890)     ", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigInteger
     * conversion exception
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigIntegerConversionException() {
        Formatter f = null;
        final String[] flagsConversionMismatches = new String[]{ "%#d", "%,o", "%,x", "%,X" };
        for (int i = 0; i < (flagsConversionMismatches.length); i++) {
            try {
                f = new Formatter(Locale.CHINA);
                f.format(flagsConversionMismatches[i], new BigInteger("1"));
                TestCase.fail("should throw FormatFlagsConversionMismatchException");
            } catch (FormatFlagsConversionMismatchException e) {
                // expected
            }
        }
        final String[] missingFormatWidths = new String[]{ "%-0d", "%0d", "%-d", "%-0o", "%0o", "%-o", "%-0x", "%0x", "%-x", "%-0X", "%0X", "%-X" };
        for (int i = 0; i < (missingFormatWidths.length); i++) {
            try {
                f = new Formatter(Locale.KOREA);
                f.format(missingFormatWidths[i], new BigInteger("1"));
                TestCase.fail("should throw MissingFormatWidthException");
            } catch (MissingFormatWidthException e) {
                // expected
            }
        }
        final String[] illFlags = new String[]{ "%+ d", "%-08d", "%+ o", "%-08o", "%+ x", "%-08x", "%+ X", "%-08X" };
        for (int i = 0; i < (illFlags.length); i++) {
            try {
                f = new Formatter(Locale.CANADA);
                f.format(illFlags[i], new BigInteger("1"));
                TestCase.fail("should throw IllegalFormatFlagsException");
            } catch (IllegalFormatFlagsException e) {
                // expected
            }
        }
        final String[] precisionExceptions = new String[]{ "%.4d", "%2.5o", "%8.6x", "%11.17X" };
        for (int i = 0; i < (precisionExceptions.length); i++) {
            try {
                f = new Formatter(Locale.US);
                f.format(precisionExceptions[i], new BigInteger("1"));
                TestCase.fail("should throw IllegalFormatPrecisionException");
            } catch (IllegalFormatPrecisionException e) {
                // expected
            }
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%D", new BigInteger("1"));
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%O", new BigInteger("1"));
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
        try {
            f = new Formatter();
            f.format("%010000000000000000000000000000000001d", new BigInteger("1"));
            TestCase.fail("should throw MissingFormatWidthException");
        } catch (MissingFormatWidthException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigInteger
     * exception throwing order
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigIntegerExceptionOrder() {
        Formatter f = null;
        BigInteger big = new BigInteger("100");
        /* Order summary: UnknownFormatConversionException >
        MissingFormatWidthException > IllegalFormatFlagsException >
        IllegalFormatPrecisionException > IllegalFormatConversionException >
        FormatFlagsConversionMismatchException
         */
        f = new Formatter(Locale.US);
        try {
            f.format("%(o", false);
            TestCase.fail();
        } catch (FormatFlagsConversionMismatchException expected) {
        } catch (IllegalFormatConversionException expected) {
        }
        try {
            f.format("%.4o", false);
            TestCase.fail();
        } catch (IllegalFormatPrecisionException expected) {
        } catch (IllegalFormatConversionException expected) {
        }
        try {
            f.format("%+ .4o", big);
            TestCase.fail();
        } catch (IllegalFormatPrecisionException expected) {
        } catch (IllegalFormatFlagsException expected) {
        }
        try {
            f.format("%+ -o", big);
            TestCase.fail();
        } catch (MissingFormatWidthException expected) {
        } catch (IllegalFormatFlagsException expected) {
        }
        try {
            f.format("%-O", big);
            TestCase.fail();
        } catch (MissingFormatWidthException expected) {
        } catch (UnknownFormatConversionException expected) {
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for Float/Double
     * conversion type 'e' and 'E'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatConversionE() {
        Formatter f = null;
        final Object[][] tripleE = new Object[][]{ new Object[]{ 0.0F, "%e", "0.000000e+00" }, new Object[]{ 0.0F, "%#.0e", "0.e+00" }, new Object[]{ 0.0F, "%#- (9.8e", " 0.00000000e+00" }, new Object[]{ 0.0F, "%#+0(8.4e", "+0.0000e+00" }, new Object[]{ 0.0F, "%-+(1.6e", "+0.000000e+00" }, new Object[]{ 0.0F, "% 0(12e", " 0.000000e+00" }, new Object[]{ 101.0F, "%e", "1.010000e+02" }, new Object[]{ 101.0F, "%#.0e", "1.e+02" }, new Object[]{ 101.0F, "%#- (9.8e", " 1.01000000e+02" }, new Object[]{ 101.0F, "%#+0(8.4e", "+1.0100e+02" }, new Object[]{ 101.0F, "%-+(1.6e", "+1.010000e+02" }, new Object[]{ 101.0F, "% 0(12e", " 1.010000e+02" }, new Object[]{ 1.0F, "%e", "1.000000e+00" }, new Object[]{ 1.0F, "%#.0e", "1.e+00" }, new Object[]{ 1.0F, "%#- (9.8e", " 1.00000000e+00" }, new Object[]{ 1.0F, "%#+0(8.4e", "+1.0000e+00" }, new Object[]{ 1.0F, "%-+(1.6e", "+1.000000e+00" }, new Object[]{ 1.0F, "% 0(12e", " 1.000000e+00" }, new Object[]{ -98.0F, "%e", "-9.800000e+01" }, new Object[]{ -98.0F, "%#.0e", "-1.e+02" }, new Object[]{ -98.0F, "%#- (9.8e", "(9.80000000e+01)" }, new Object[]{ -98.0F, "%#+0(8.4e", "(9.8000e+01)" }, new Object[]{ -98.0F, "%-+(1.6e", "(9.800000e+01)" }, new Object[]{ -98.0F, "% 0(12e", "(9.800000e+01)" }, new Object[]{ 1.23F, "%e", "1.230000e+00" }, new Object[]{ 1.23F, "%#.0e", "1.e+00" }, new Object[]{ 1.23F, "%#- (9.8e", " 1.23000002e+00" }, new Object[]{ 1.23F, "%#+0(8.4e", "+1.2300e+00" }, new Object[]{ 1.23F, "%-+(1.6e", "+1.230000e+00" }, new Object[]{ 1.23F, "% 0(12e", " 1.230000e+00" }, new Object[]{ 34.123455F, "%e", "3.412346e+01" }, new Object[]{ 34.123455F, "%#.0e", "3.e+01" }, new Object[]{ 34.123455F, "%#- (9.8e", " 3.41234550e+01" }, new Object[]{ 34.123455F, "%#+0(8.4e", "+3.4123e+01" }, new Object[]{ 34.123455F, "%-+(1.6e", "+3.412346e+01" }, new Object[]{ 34.123455F, "% 0(12e", " 3.412346e+01" }, new Object[]{ -0.12345F, "%e", "-1.234500e-01" }, new Object[]{ -0.12345F, "%#.0e", "-1.e-01" }, new Object[]{ -0.12345F, "%#- (9.8e", "(1.23450004e-01)" }, new Object[]{ -0.12345F, "%#+0(8.4e", "(1.2345e-01)" }, new Object[]{ -0.12345F, "%-+(1.6e", "(1.234500e-01)" }, new Object[]{ -0.12345F, "% 0(12e", "(1.234500e-01)" }, new Object[]{ -9876.123F, "%e", "-9.876123e+03" }, new Object[]{ -9876.123F, "%#.0e", "-1.e+04" }, new Object[]{ -9876.123F, "%#- (9.8e", "(9.87612305e+03)" }, new Object[]{ -9876.123F, "%#+0(8.4e", "(9.8761e+03)" }, new Object[]{ -9876.123F, "%-+(1.6e", "(9.876123e+03)" }, new Object[]{ -9876.123F, "% 0(12e", "(9.876123e+03)" }, new Object[]{ Float.MAX_VALUE, "%e", "3.402823e+38" }, new Object[]{ Float.MAX_VALUE, "%#.0e", "3.e+38" }, new Object[]{ Float.MAX_VALUE, "%#- (9.8e", " 3.40282347e+38" }, new Object[]{ Float.MAX_VALUE, "%#+0(8.4e", "+3.4028e+38" }, new Object[]{ Float.MAX_VALUE, "%-+(1.6e", "+3.402823e+38" }, new Object[]{ Float.MAX_VALUE, "% 0(12e", " 3.402823e+38" }, new Object[]{ Float.MIN_VALUE, "%e", "1.401298e-45" }, new Object[]{ Float.MIN_VALUE, "%#.0e", "1.e-45" }, new Object[]{ Float.MIN_VALUE, "%#- (9.8e", " 1.40129846e-45" }, new Object[]{ Float.MIN_VALUE, "%#+0(8.4e", "+1.4013e-45" }, new Object[]{ Float.MIN_VALUE, "%-+(1.6e", "+1.401298e-45" }, new Object[]{ Float.MIN_VALUE, "% 0(12e", " 1.401298e-45" }, new Object[]{ Float.NaN, "%e", "NaN" }, new Object[]{ Float.NaN, "%#.0e", "NaN" }, new Object[]{ Float.NaN, "%#- (9.8e", "NaN      " }, new Object[]{ Float.NaN, "%#+0(8.4e", "     NaN" }, new Object[]{ Float.NaN, "%-+(1.6e", "NaN" }, new Object[]{ Float.NaN, "% 0(12e", "         NaN" }, new Object[]{ Float.NEGATIVE_INFINITY, "%e", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#.0e", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#- (9.8e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#+0(8.4e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%-+(1.6e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "% 0(12e", "  (Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%e", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#.0e", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#- (9.8e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#+0(8.4e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%-+(1.6e", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "% 0(12e", "  (Infinity)" }, new Object[]{ 0.0, "%e", "0.000000e+00" }, new Object[]{ 0.0, "%#.0e", "0.e+00" }, new Object[]{ 0.0, "%#- (9.8e", " 0.00000000e+00" }, new Object[]{ 0.0, "%#+0(8.4e", "+0.0000e+00" }, new Object[]{ 0.0, "%-+(1.6e", "+0.000000e+00" }, new Object[]{ 0.0, "% 0(12e", " 0.000000e+00" }, new Object[]{ 1.0, "%e", "1.000000e+00" }, new Object[]{ 1.0, "%#.0e", "1.e+00" }, new Object[]{ 1.0, "%#- (9.8e", " 1.00000000e+00" }, new Object[]{ 1.0, "%#+0(8.4e", "+1.0000e+00" }, new Object[]{ 1.0, "%-+(1.6e", "+1.000000e+00" }, new Object[]{ 1.0, "% 0(12e", " 1.000000e+00" }, new Object[]{ -1.0, "%e", "-1.000000e+00" }, new Object[]{ -1.0, "%#.0e", "-1.e+00" }, new Object[]{ -1.0, "%#- (9.8e", "(1.00000000e+00)" }, new Object[]{ -1.0, "%#+0(8.4e", "(1.0000e+00)" }, new Object[]{ -1.0, "%-+(1.6e", "(1.000000e+00)" }, new Object[]{ -1.0, "% 0(12e", "(1.000000e+00)" }, new Object[]{ 1.0E-8, "%e", "1.000000e-08" }, new Object[]{ 1.0E-8, "%#.0e", "1.e-08" }, new Object[]{ 1.0E-8, "%#- (9.8e", " 1.00000000e-08" }, new Object[]{ 1.0E-8, "%#+0(8.4e", "+1.0000e-08" }, new Object[]{ 1.0E-8, "%-+(1.6e", "+1.000000e-08" }, new Object[]{ 1.0E-8, "% 0(12e", " 1.000000e-08" }, new Object[]{ 9122.1, "%e", "9.122100e+03" }, new Object[]{ 9122.1, "%#.0e", "9.e+03" }, new Object[]{ 9122.1, "%#- (9.8e", " 9.12210000e+03" }, new Object[]{ 9122.1, "%#+0(8.4e", "+9.1221e+03" }, new Object[]{ 9122.1, "%-+(1.6e", "+9.122100e+03" }, new Object[]{ 9122.1, "% 0(12e", " 9.122100e+03" }, new Object[]{ 0.1, "%e", "1.000000e-01" }, new Object[]{ 0.1, "%#.0e", "1.e-01" }, new Object[]{ 0.1, "%#- (9.8e", " 1.00000000e-01" }, new Object[]{ 0.1, "%#+0(8.4e", "+1.0000e-01" }, new Object[]{ 0.1, "%-+(1.6e", "+1.000000e-01" }, new Object[]{ 0.1, "% 0(12e", " 1.000000e-01" }, new Object[]{ -2.0, "%e", "-2.000000e+00" }, new Object[]{ -2.0, "%#.0e", "-2.e+00" }, new Object[]{ -2.0, "%#- (9.8e", "(2.00000000e+00)" }, new Object[]{ -2.0, "%#+0(8.4e", "(2.0000e+00)" }, new Object[]{ -2.0, "%-+(1.6e", "(2.000000e+00)" }, new Object[]{ -2.0, "% 0(12e", "(2.000000e+00)" }, new Object[]{ -0.39, "%e", "-3.900000e-01" }, new Object[]{ -0.39, "%#.0e", "-4.e-01" }, new Object[]{ -0.39, "%#- (9.8e", "(3.90000000e-01)" }, new Object[]{ -0.39, "%#+0(8.4e", "(3.9000e-01)" }, new Object[]{ -0.39, "%-+(1.6e", "(3.900000e-01)" }, new Object[]{ -0.39, "% 0(12e", "(3.900000e-01)" }, new Object[]{ -1.2345678900123458E9, "%e", "-1.234568e+09" }, new Object[]{ -1.2345678900123458E9, "%#.0e", "-1.e+09" }, new Object[]{ -1.2345678900123458E9, "%#- (9.8e", "(1.23456789e+09)" }, new Object[]{ -1.2345678900123458E9, "%#+0(8.4e", "(1.2346e+09)" }, new Object[]{ -1.2345678900123458E9, "%-+(1.6e", "(1.234568e+09)" }, new Object[]{ -1.2345678900123458E9, "% 0(12e", "(1.234568e+09)" }, new Object[]{ Double.MAX_VALUE, "%e", "1.797693e+308" }, new Object[]{ Double.MAX_VALUE, "%#.0e", "2.e+308" }, new Object[]{ Double.MAX_VALUE, "%#- (9.8e", " 1.79769313e+308" }, new Object[]{ Double.MAX_VALUE, "%#+0(8.4e", "+1.7977e+308" }, new Object[]{ Double.MAX_VALUE, "%-+(1.6e", "+1.797693e+308" }, new Object[]{ Double.MAX_VALUE, "% 0(12e", " 1.797693e+308" }, new Object[]{ Double.MIN_VALUE, "%e", "4.900000e-324" }, new Object[]{ Double.MIN_VALUE, "%#.0e", "5.e-324" }, new Object[]{ Double.MIN_VALUE, "%#- (9.8e", " 4.90000000e-324" }, new Object[]{ Double.MIN_VALUE, "%#+0(8.4e", "+4.9000e-324" }, new Object[]{ Double.MIN_VALUE, "%-+(1.6e", "+4.900000e-324" }, new Object[]{ Double.MIN_VALUE, "% 0(12e", " 4.900000e-324" }, new Object[]{ Double.NaN, "%e", "NaN" }, new Object[]{ Double.NaN, "%#.0e", "NaN" }, new Object[]{ Double.NaN, "%#- (9.8e", "NaN      " }, new Object[]{ Double.NaN, "%#+0(8.4e", "     NaN" }, new Object[]{ Double.NaN, "%-+(1.6e", "NaN" }, new Object[]{ Double.NaN, "% 0(12e", "         NaN" }, new Object[]{ Double.NEGATIVE_INFINITY, "%e", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%#.0e", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%#- (9.8e", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "%#+0(8.4e", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "%-+(1.6e", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "% 0(12e", "  (Infinity)" }, new Object[]{ Double.POSITIVE_INFINITY, "%e", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%#.0e", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%#- (9.8e", " Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%#+0(8.4e", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%-+(1.6e", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "% 0(12e", "    Infinity" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleE.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (tripleE[i][pattern])), tripleE[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleE[i][input])) + ",pattern[") + i) + "]:") + (tripleE[i][pattern])), tripleE[i][output], f.toString());
            // test for conversion type 'E'
            f = new Formatter(Locale.US);
            f.format(((String) (tripleE[i][pattern])).toUpperCase(), tripleE[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleE[i][input])) + ",pattern[") + i) + "]:") + (tripleE[i][pattern])), ((String) (tripleE[i][output])).toUpperCase(Locale.UK), f.toString());
        }
        f = new Formatter(Locale.GERMAN);
        f.format("%e", 1001.0F);
        /* fail on RI, spec says 'e' requires the output to be formatted in
        general scientific notation and the localization algorithm is
        applied. But RI format this case to 1.001000e+03, which does not
        conform to the German Locale
         */
        TestCase.assertEquals("1,001000e+03", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for Float/Double
     * conversion type 'g' and 'G'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatConversionG() {
        Formatter f = null;
        final Object[][] tripleG = new Object[][]{ new Object[]{ 1001.0F, "%g", "1001.00" }, new Object[]{ 1001.0F, "%- (,9.8g", " 1,001.0000" }, new Object[]{ 1001.0F, "%+0(,8.4g", "+001,001" }, new Object[]{ 1001.0F, "%-+(,1.6g", "+1,001.00" }, new Object[]{ 1001.0F, "% 0(,12.0g", " 0000001e+03" }, new Object[]{ 1.0F, "%g", "1.00000" }, new Object[]{ 1.0F, "%- (,9.8g", " 1.0000000" }, new Object[]{ 1.0F, "%+0(,8.4g", "+001.000" }, new Object[]{ 1.0F, "%-+(,1.6g", "+1.00000" }, new Object[]{ 1.0F, "% 0(,12.0g", " 00000000001" }, new Object[]{ -98.0F, "%g", "-98.0000" }, new Object[]{ -98.0F, "%- (,9.8g", "(98.000000)" }, new Object[]{ -98.0F, "%+0(,8.4g", "(098.00)" }, new Object[]{ -98.0F, "%-+(,1.6g", "(98.0000)" }, new Object[]{ -98.0F, "% 0(,12.0g", "(000001e+02)" }, new Object[]{ 1.0E-6F, "%g", "1.00000e-06" }, new Object[]{ 1.0E-6F, "%- (,9.8g", " 1.0000000e-06" }, new Object[]{ 1.0E-6F, "%+0(,8.4g", "+1.000e-06" }, new Object[]{ 1.0E-6F, "%-+(,1.6g", "+1.00000e-06" }, new Object[]{ 1.0E-6F, "% 0(,12.0g", " 0000001e-06" }, new Object[]{ 345.12344F, "%g", "345.123" }, new Object[]{ 345.12344F, "%- (,9.8g", " 345.12344" }, new Object[]{ 345.12344F, "%+0(,8.4g", "+00345.1" }, new Object[]{ 345.12344F, "%-+(,1.6g", "+345.123" }, new Object[]{ 345.12344F, "% 0(,12.0g", " 0000003e+02" }, new Object[]{ -1.2345E-7F, "%g", "-1.23450e-07" }, new Object[]{ -1.2345E-7F, "%- (,9.8g", "(1.2344999e-07)" }, new Object[]{ -1.2345E-7F, "%+0(,8.4g", "(1.234e-07)" }, new Object[]{ -1.2345E-7F, "%-+(,1.6g", "(1.23450e-07)" }, new Object[]{ -1.2345E-7F, "% 0(,12.0g", "(000001e-07)" }, new Object[]{ -987.1235F, "%g", "-987.123" }, new Object[]{ -987.1235F, "%- (,9.8g", "(987.12347)" }, new Object[]{ -987.1235F, "%+0(,8.4g", "(0987.1)" }, new Object[]{ -987.1235F, "%-+(,1.6g", "(987.123)" }, new Object[]{ -987.1235F, "% 0(,12.0g", "(000001e+03)" }, new Object[]{ Float.MAX_VALUE, "%g", "3.40282e+38" }, new Object[]{ Float.MAX_VALUE, "%- (,9.8g", " 3.4028235e+38" }, new Object[]{ Float.MAX_VALUE, "%+0(,8.4g", "+3.403e+38" }, new Object[]{ Float.MAX_VALUE, "%-+(,1.6g", "+3.40282e+38" }, new Object[]{ Float.MAX_VALUE, "% 0(,12.0g", " 0000003e+38" }, new Object[]{ Float.MIN_VALUE, "%g", "1.40130e-45" }, new Object[]{ Float.MIN_VALUE, "%- (,9.8g", " 1.4012985e-45" }, new Object[]{ Float.MIN_VALUE, "%+0(,8.4g", "+1.401e-45" }, new Object[]{ Float.MIN_VALUE, "%-+(,1.6g", "+1.40130e-45" }, new Object[]{ Float.MIN_VALUE, "% 0(,12.0g", " 0000001e-45" }, new Object[]{ Float.NaN, "%g", "NaN" }, new Object[]{ Float.NaN, "%- (,9.8g", "NaN      " }, new Object[]{ Float.NaN, "%+0(,8.4g", "     NaN" }, new Object[]{ Float.NaN, "%-+(,1.6g", "NaN" }, new Object[]{ Float.NaN, "% 0(,12.0g", "         NaN" }, new Object[]{ Float.NEGATIVE_INFINITY, "%g", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%- (,9.8g", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%+0(,8.4g", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%-+(,1.6g", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "% 0(,12.0g", "  (Infinity)" }, new Object[]{ Float.POSITIVE_INFINITY, "%g", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%- (,9.8g", " Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%+0(,8.4g", "+Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%-+(,1.6g", "+Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "% 0(,12.0g", "    Infinity" }, new Object[]{ 1.0, "%g", "1.00000" }, new Object[]{ 1.0, "%- (,9.8g", " 1.0000000" }, new Object[]{ 1.0, "%+0(,8.4g", "+001.000" }, new Object[]{ 1.0, "%-+(,1.6g", "+1.00000" }, new Object[]{ 1.0, "% 0(,12.0g", " 00000000001" }, new Object[]{ -1.0, "%g", "-1.00000" }, new Object[]{ -1.0, "%- (,9.8g", "(1.0000000)" }, new Object[]{ -1.0, "%+0(,8.4g", "(01.000)" }, new Object[]{ -1.0, "%-+(,1.6g", "(1.00000)" }, new Object[]{ -1.0, "% 0(,12.0g", "(0000000001)" }, new Object[]{ 1.0E-8, "%g", "1.00000e-08" }, new Object[]{ 1.0E-8, "%- (,9.8g", " 1.0000000e-08" }, new Object[]{ 1.0E-8, "%+0(,8.4g", "+1.000e-08" }, new Object[]{ 1.0E-8, "%-+(,1.6g", "+1.00000e-08" }, new Object[]{ 1.0E-8, "% 0(,12.0g", " 0000001e-08" }, new Object[]{ 1912.1, "%g", "1912.10" }, new Object[]{ 1912.1, "%- (,9.8g", " 1,912.1000" }, new Object[]{ 1912.1, "%+0(,8.4g", "+001,912" }, new Object[]{ 1912.1, "%-+(,1.6g", "+1,912.10" }, new Object[]{ 1912.1, "% 0(,12.0g", " 0000002e+03" }, new Object[]{ 0.1, "%g", "0.100000" }, new Object[]{ 0.1, "%- (,9.8g", " 0.10000000" }, new Object[]{ 0.1, "%+0(,8.4g", "+00.1000" }, new Object[]{ 0.1, "%-+(,1.6g", "+0.100000" }, new Object[]{ 0.1, "% 0(,12.0g", " 000000000.1" }, new Object[]{ -2.0, "%g", "-2.00000" }, new Object[]{ -2.0, "%- (,9.8g", "(2.0000000)" }, new Object[]{ -2.0, "%+0(,8.4g", "(02.000)" }, new Object[]{ -2.0, "%-+(,1.6g", "(2.00000)" }, new Object[]{ -2.0, "% 0(,12.0g", "(0000000002)" }, new Object[]{ -3.9E-4, "%g", "-0.000390000" }, new Object[]{ -3.9E-4, "%- (,9.8g", "(0.00039000000)" }, new Object[]{ -3.9E-4, "%+0(,8.4g", "(0.0003900)" }, new Object[]{ -3.9E-4, "%-+(,1.6g", "(0.000390000)" }, new Object[]{ -3.9E-4, "% 0(,12.0g", "(00000.0004)" }, new Object[]{ -1.2345678900123458E9, "%g", "-1.23457e+09" }, new Object[]{ -1.2345678900123458E9, "%- (,9.8g", "(1.2345679e+09)" }, new Object[]{ -1.2345678900123458E9, "%+0(,8.4g", "(1.235e+09)" }, new Object[]{ -1.2345678900123458E9, "%-+(,1.6g", "(1.23457e+09)" }, new Object[]{ -1.2345678900123458E9, "% 0(,12.0g", "(000001e+09)" }, new Object[]{ Double.MAX_VALUE, "%g", "1.79769e+308" }, new Object[]{ Double.MAX_VALUE, "%- (,9.8g", " 1.7976931e+308" }, new Object[]{ Double.MAX_VALUE, "%+0(,8.4g", "+1.798e+308" }, new Object[]{ Double.MAX_VALUE, "%-+(,1.6g", "+1.79769e+308" }, new Object[]{ Double.MAX_VALUE, "% 0(,12.0g", " 000002e+308" }, new Object[]{ Double.MIN_VALUE, "%g", "4.90000e-324" }, new Object[]{ Double.MIN_VALUE, "%- (,9.8g", " 4.9000000e-324" }, new Object[]{ Double.MIN_VALUE, "%+0(,8.4g", "+4.900e-324" }, new Object[]{ Double.MIN_VALUE, "%-+(,1.6g", "+4.90000e-324" }, new Object[]{ Double.MIN_VALUE, "% 0(,12.0g", " 000005e-324" }, new Object[]{ Double.NaN, "%g", "NaN" }, new Object[]{ Double.NaN, "%- (,9.8g", "NaN      " }, new Object[]{ Double.NaN, "%+0(,8.4g", "     NaN" }, new Object[]{ Double.NaN, "%-+(,1.6g", "NaN" }, new Object[]{ Double.NaN, "% 0(,12.0g", "         NaN" }, new Object[]{ Double.NEGATIVE_INFINITY, "%g", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%- (,9.8g", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "%+0(,8.4g", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "%-+(,1.6g", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "% 0(,12.0g", "  (Infinity)" }, new Object[]{ Double.POSITIVE_INFINITY, "%g", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%- (,9.8g", " Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%+0(,8.4g", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%-+(,1.6g", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "% 0(,12.0g", "    Infinity" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleG.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (tripleG[i][pattern])), tripleG[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleG[i][input])) + ",pattern[") + i) + "]:") + (tripleG[i][pattern])), tripleG[i][output], f.toString());
            // test for conversion type 'G'
            f = new Formatter(Locale.US);
            f.format(((String) (tripleG[i][pattern])).toUpperCase(), tripleG[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleG[i][input])) + ",pattern[") + i) + "]:") + (tripleG[i][pattern])), ((String) (tripleG[i][output])).toUpperCase(Locale.UK), f.toString());
        }
        f = new Formatter(Locale.US);
        f.format("%.5g", 0.0F);
        TestCase.assertEquals("0.0000", f.toString());
        f = new Formatter(Locale.US);
        f.format("%.0g", 0.0F);
        /* fail on RI, spec says if the precision is 0, then it is taken to be
        1. but RI throws ArrayIndexOutOfBoundsException.
         */
        TestCase.assertEquals("0", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%g", 1001.0F);
        /* fail on RI, spec says 'g' requires the output to be formatted in
        general scientific notation and the localization algorithm is
        applied. But RI format this case to 1001.00, which does not conform
        to the German Locale
         */
        TestCase.assertEquals("1001,00", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for Float/Double
     * conversion type 'g' and 'G' overflow
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatConversionG_Overflow() {
        Formatter f = new Formatter();
        f.format("%g", 999999.5);
        TestCase.assertEquals("1.00000e+06", f.toString());
        f = new Formatter();
        f.format("%g", 99999.5);
        TestCase.assertEquals("99999.5", f.toString());
        f = new Formatter();
        f.format("%.4g", 99.95);
        TestCase.assertEquals("99.95", f.toString());
        f = new Formatter();
        f.format("%g", 99.95);
        TestCase.assertEquals("99.9500", f.toString());
        f = new Formatter();
        f.format("%g", 0.9);
        TestCase.assertEquals("0.900000", f.toString());
        f = new Formatter();
        f.format("%.0g", 9.5E-5);
        TestCase.assertEquals("0.0001", f.toString());
        f = new Formatter();
        f.format("%g", 0.0999999);
        TestCase.assertEquals("0.0999999", f.toString());
        f = new Formatter();
        f.format("%g", 9.0E-5);
        TestCase.assertEquals("9.00000e-05", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for Float/Double
     * conversion type 'f'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatConversionF() {
        Formatter f = null;
        final Object[][] tripleF = new Object[][]{ new Object[]{ 0.0F, "%f", "0,000000" }, new Object[]{ 0.0F, "%#.3f", "0,000" }, new Object[]{ 0.0F, "%,5f", "0,000000" }, new Object[]{ 0.0F, "%- (12.0f", " 0          " }, new Object[]{ 0.0F, "%#+0(1.6f", "+0,000000" }, new Object[]{ 0.0F, "%-+(8.4f", "+0,0000 " }, new Object[]{ 0.0F, "% 0#(9.8f", " 0,00000000" }, new Object[]{ 1234.0F, "%f", "1234,000000" }, new Object[]{ 1234.0F, "%#.3f", "1234,000" }, new Object[]{ 1234.0F, "%,5f", "1.234,000000" }, new Object[]{ 1234.0F, "%- (12.0f", " 1234       " }, new Object[]{ 1234.0F, "%#+0(1.6f", "+1234,000000" }, new Object[]{ 1234.0F, "%-+(8.4f", "+1234,0000" }, new Object[]{ 1234.0F, "% 0#(9.8f", " 1234,00000000" }, new Object[]{ 1.0F, "%f", "1,000000" }, new Object[]{ 1.0F, "%#.3f", "1,000" }, new Object[]{ 1.0F, "%,5f", "1,000000" }, new Object[]{ 1.0F, "%- (12.0f", " 1          " }, new Object[]{ 1.0F, "%#+0(1.6f", "+1,000000" }, new Object[]{ 1.0F, "%-+(8.4f", "+1,0000 " }, new Object[]{ 1.0F, "% 0#(9.8f", " 1,00000000" }, new Object[]{ -98.0F, "%f", "-98,000000" }, new Object[]{ -98.0F, "%#.3f", "-98,000" }, new Object[]{ -98.0F, "%,5f", "-98,000000" }, new Object[]{ -98.0F, "%- (12.0f", "(98)        " }, new Object[]{ -98.0F, "%#+0(1.6f", "(98,000000)" }, new Object[]{ -98.0F, "%-+(8.4f", "(98,0000)" }, new Object[]{ -98.0F, "% 0#(9.8f", "(98,00000000)" }, new Object[]{ 1.0E-6F, "%f", "0,000001" }, new Object[]{ 1.0E-6F, "%#.3f", "0,000" }, new Object[]{ 1.0E-6F, "%,5f", "0,000001" }, new Object[]{ 1.0E-6F, "%- (12.0f", " 0          " }, new Object[]{ 1.0E-6F, "%#+0(1.6f", "+0,000001" }, new Object[]{ 1.0E-6F, "%-+(8.4f", "+0,0000 " }, new Object[]{ 1.0E-6F, "% 0#(9.8f", " 0,00000100" }, new Object[]{ 345.12344F, "%f", "345,123444" }, new Object[]{ 345.12344F, "%#.3f", "345,123" }, new Object[]{ 345.12344F, "%,5f", "345,123444" }, new Object[]{ 345.12344F, "%- (12.0f", " 345        " }, new Object[]{ 345.12344F, "%#+0(1.6f", "+345,123444" }, new Object[]{ 345.12344F, "%-+(8.4f", "+345,1234" }, new Object[]{ 345.12344F, "% 0#(9.8f", " 345,12344360" }, new Object[]{ -1.2345E-7F, "%f", "-0,000000" }, new Object[]{ -1.2345E-7F, "%#.3f", "-0,000" }, new Object[]{ -1.2345E-7F, "%,5f", "-0,000000" }, new Object[]{ -1.2345E-7F, "%- (12.0f", "(0)         " }, new Object[]{ -1.2345E-7F, "%#+0(1.6f", "(0,000000)" }, new Object[]{ -1.2345E-7F, "%-+(8.4f", "(0,0000)" }, new Object[]{ -1.2345E-7F, "% 0#(9.8f", "(0,00000012)" }, new Object[]{ -9.8765434E8F, "%f", "-987654336,000000" }, new Object[]{ -9.8765434E8F, "%#.3f", "-987654336,000" }, new Object[]{ -9.8765434E8F, "%,5f", "-987.654.336,000000" }, new Object[]{ -9.8765434E8F, "%- (12.0f", "(987654336) " }, new Object[]{ -9.8765434E8F, "%#+0(1.6f", "(987654336,000000)" }, new Object[]{ -9.8765434E8F, "%-+(8.4f", "(987654336,0000)" }, new Object[]{ -9.8765434E8F, "% 0#(9.8f", "(987654336,00000000)" }, new Object[]{ Float.MAX_VALUE, "%f", "340282346638528860000000000000000000000,000000" }, new Object[]{ Float.MAX_VALUE, "%#.3f", "340282346638528860000000000000000000000,000" }, new Object[]{ Float.MAX_VALUE, "%,5f", "340.282.346.638.528.860.000.000.000.000.000.000.000,000000" }, new Object[]{ Float.MAX_VALUE, "%- (12.0f", " 340282346638528860000000000000000000000" }, new Object[]{ Float.MAX_VALUE, "%#+0(1.6f", "+340282346638528860000000000000000000000,000000" }, new Object[]{ Float.MAX_VALUE, "%-+(8.4f", "+340282346638528860000000000000000000000,0000" }, new Object[]{ Float.MAX_VALUE, "% 0#(9.8f", " 340282346638528860000000000000000000000,00000000" }, new Object[]{ Float.MIN_VALUE, "%f", "0,000000" }, new Object[]{ Float.MIN_VALUE, "%#.3f", "0,000" }, new Object[]{ Float.MIN_VALUE, "%,5f", "0,000000" }, new Object[]{ Float.MIN_VALUE, "%- (12.0f", " 0          " }, new Object[]{ Float.MIN_VALUE, "%#+0(1.6f", "+0,000000" }, new Object[]{ Float.MIN_VALUE, "%-+(8.4f", "+0,0000 " }, new Object[]{ Float.MIN_VALUE, "% 0#(9.8f", " 0,00000000" }, new Object[]{ Float.NaN, "%f", "NaN" }, new Object[]{ Float.NaN, "%#.3f", "NaN" }, new Object[]{ Float.NaN, "%,5f", "  NaN" }, new Object[]{ Float.NaN, "%- (12.0f", "NaN         " }, new Object[]{ Float.NaN, "%#+0(1.6f", "NaN" }, new Object[]{ Float.NaN, "%-+(8.4f", "NaN     " }, new Object[]{ Float.NaN, "% 0#(9.8f", "      NaN" }, new Object[]{ Float.NEGATIVE_INFINITY, "%f", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#.3f", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%,5f", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%- (12.0f", "(Infinity)  " }, new Object[]{ Float.NEGATIVE_INFINITY, "%#+0(1.6f", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "%-+(8.4f", "(Infinity)" }, new Object[]{ Float.NEGATIVE_INFINITY, "% 0#(9.8f", "(Infinity)" }, new Object[]{ Float.POSITIVE_INFINITY, "%f", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%#.3f", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%,5f", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%- (12.0f", " Infinity   " }, new Object[]{ Float.POSITIVE_INFINITY, "%#+0(1.6f", "+Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%-+(8.4f", "+Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "% 0#(9.8f", " Infinity" }, new Object[]{ 0.0, "%f", "0,000000" }, new Object[]{ 0.0, "%#.3f", "0,000" }, new Object[]{ 0.0, "%,5f", "0,000000" }, new Object[]{ 0.0, "%- (12.0f", " 0          " }, new Object[]{ 0.0, "%#+0(1.6f", "+0,000000" }, new Object[]{ 0.0, "%-+(8.4f", "+0,0000 " }, new Object[]{ 0.0, "% 0#(9.8f", " 0,00000000" }, new Object[]{ 1.0, "%f", "1,000000" }, new Object[]{ 1.0, "%#.3f", "1,000" }, new Object[]{ 1.0, "%,5f", "1,000000" }, new Object[]{ 1.0, "%- (12.0f", " 1          " }, new Object[]{ 1.0, "%#+0(1.6f", "+1,000000" }, new Object[]{ 1.0, "%-+(8.4f", "+1,0000 " }, new Object[]{ 1.0, "% 0#(9.8f", " 1,00000000" }, new Object[]{ -1.0, "%f", "-1,000000" }, new Object[]{ -1.0, "%#.3f", "-1,000" }, new Object[]{ -1.0, "%,5f", "-1,000000" }, new Object[]{ -1.0, "%- (12.0f", "(1)         " }, new Object[]{ -1.0, "%#+0(1.6f", "(1,000000)" }, new Object[]{ -1.0, "%-+(8.4f", "(1,0000)" }, new Object[]{ -1.0, "% 0#(9.8f", "(1,00000000)" }, new Object[]{ 1.0E-8, "%f", "0,000000" }, new Object[]{ 1.0E-8, "%#.3f", "0,000" }, new Object[]{ 1.0E-8, "%,5f", "0,000000" }, new Object[]{ 1.0E-8, "%- (12.0f", " 0          " }, new Object[]{ 1.0E-8, "%#+0(1.6f", "+0,000000" }, new Object[]{ 1.0E-8, "%-+(8.4f", "+0,0000 " }, new Object[]{ 1.0E-8, "% 0#(9.8f", " 0,00000001" }, new Object[]{ 1000.1, "%f", "1000,100000" }, new Object[]{ 1000.1, "%#.3f", "1000,100" }, new Object[]{ 1000.1, "%,5f", "1.000,100000" }, new Object[]{ 1000.1, "%- (12.0f", " 1000       " }, new Object[]{ 1000.1, "%#+0(1.6f", "+1000,100000" }, new Object[]{ 1000.1, "%-+(8.4f", "+1000,1000" }, new Object[]{ 1000.1, "% 0#(9.8f", " 1000,10000000" }, new Object[]{ 0.1, "%f", "0,100000" }, new Object[]{ 0.1, "%#.3f", "0,100" }, new Object[]{ 0.1, "%,5f", "0,100000" }, new Object[]{ 0.1, "%- (12.0f", " 0          " }, new Object[]{ 0.1, "%#+0(1.6f", "+0,100000" }, new Object[]{ 0.1, "%-+(8.4f", "+0,1000 " }, new Object[]{ 0.1, "% 0#(9.8f", " 0,10000000" }, new Object[]{ -2.0, "%f", "-2,000000" }, new Object[]{ -2.0, "%#.3f", "-2,000" }, new Object[]{ -2.0, "%,5f", "-2,000000" }, new Object[]{ -2.0, "%- (12.0f", "(2)         " }, new Object[]{ -2.0, "%#+0(1.6f", "(2,000000)" }, new Object[]{ -2.0, "%-+(8.4f", "(2,0000)" }, new Object[]{ -2.0, "% 0#(9.8f", "(2,00000000)" }, new Object[]{ -9.0E-5, "%f", "-0,000090" }, new Object[]{ -9.0E-5, "%#.3f", "-0,000" }, new Object[]{ -9.0E-5, "%,5f", "-0,000090" }, new Object[]{ -9.0E-5, "%- (12.0f", "(0)         " }, new Object[]{ -9.0E-5, "%#+0(1.6f", "(0,000090)" }, new Object[]{ -9.0E-5, "%-+(8.4f", "(0,0001)" }, new Object[]{ -9.0E-5, "% 0#(9.8f", "(0,00009000)" }, new Object[]{ -1.2345678900123458E9, "%f", "-1234567890,012346" }, new Object[]{ -1.2345678900123458E9, "%#.3f", "-1234567890,012" }, new Object[]{ -1.2345678900123458E9, "%,5f", "-1.234.567.890,012346" }, new Object[]{ -1.2345678900123458E9, "%- (12.0f", "(1234567890)" }, new Object[]{ -1.2345678900123458E9, "%#+0(1.6f", "(1234567890,012346)" }, new Object[]{ -1.2345678900123458E9, "%-+(8.4f", "(1234567890,0123)" }, new Object[]{ -1.2345678900123458E9, "% 0#(9.8f", "(1234567890,01234580)" }, new Object[]{ Double.MAX_VALUE, "%f", "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,000000" }, new Object[]{ Double.MAX_VALUE, "%#.3f", "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,000" }, new Object[]{ Double.MAX_VALUE, "%,5f", "179.769.313.486.231.570.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000.000,000000" }, new Object[]{ Double.MAX_VALUE, "%- (12.0f", " 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" }, new Object[]{ Double.MAX_VALUE, "%#+0(1.6f", "+179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,000000" }, new Object[]{ Double.MAX_VALUE, "%-+(8.4f", "+179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,0000" }, new Object[]{ Double.MAX_VALUE, "% 0#(9.8f", " 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,00000000" }, new Object[]{ Double.MIN_VALUE, "%f", "0,000000" }, new Object[]{ Double.MIN_VALUE, "%#.3f", "0,000" }, new Object[]{ Double.MIN_VALUE, "%,5f", "0,000000" }, new Object[]{ Double.MIN_VALUE, "%- (12.0f", " 0          " }, new Object[]{ Double.MIN_VALUE, "%#+0(1.6f", "+0,000000" }, new Object[]{ Double.MIN_VALUE, "%-+(8.4f", "+0,0000 " }, new Object[]{ Double.MIN_VALUE, "% 0#(9.8f", " 0,00000000" }, new Object[]{ Double.NaN, "%f", "NaN" }, new Object[]{ Double.NaN, "%#.3f", "NaN" }, new Object[]{ Double.NaN, "%,5f", "  NaN" }, new Object[]{ Double.NaN, "%- (12.0f", "NaN         " }, new Object[]{ Double.NaN, "%#+0(1.6f", "NaN" }, new Object[]{ Double.NaN, "%-+(8.4f", "NaN     " }, new Object[]{ Double.NaN, "% 0#(9.8f", "      NaN" }, new Object[]{ Double.POSITIVE_INFINITY, "%f", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%#.3f", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%,5f", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%- (12.0f", " Infinity   " }, new Object[]{ Double.POSITIVE_INFINITY, "%#+0(1.6f", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%-+(8.4f", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "% 0#(9.8f", " Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%f", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%#.3f", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%,5f", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%- (12.0f", "(Infinity)  " }, new Object[]{ Double.NEGATIVE_INFINITY, "%#+0(1.6f", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "%-+(8.4f", "(Infinity)" }, new Object[]{ Double.NEGATIVE_INFINITY, "% 0#(9.8f", "(Infinity)" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleF.length); i++) {
            f = new Formatter(Locale.GERMAN);
            f.format(((String) (tripleF[i][pattern])), tripleF[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleF[i][input])) + ",pattern[") + i) + "]:") + (tripleF[i][pattern])), tripleF[i][output], f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for Float/Double
     * conversion type 'a' and 'A'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatConversionA() {
        Formatter f = null;
        final Object[][] tripleA = new Object[][]{ new Object[]{ -0.0F, "%a", "-0x0.0p0" }, new Object[]{ -0.0F, "%#.3a", "-0x0.000p0" }, new Object[]{ -0.0F, "%5a", "-0x0.0p0" }, new Object[]{ -0.0F, "%- 12.0a", "-0x0.0p0    " }, new Object[]{ -0.0F, "%#+01.6a", "-0x0.000000p0" }, new Object[]{ -0.0F, "%-+8.4a", "-0x0.0000p0" }, new Object[]{ 0.0F, "%a", "0x0.0p0" }, new Object[]{ 0.0F, "%#.3a", "0x0.000p0" }, new Object[]{ 0.0F, "%5a", "0x0.0p0" }, new Object[]{ 0.0F, "%- 12.0a", " 0x0.0p0    " }, new Object[]{ 0.0F, "%#+01.6a", "+0x0.000000p0" }, new Object[]{ 0.0F, "%-+8.4a", "+0x0.0000p0" }, new Object[]{ 1234.0F, "%a", "0x1.348p10" }, new Object[]{ 1234.0F, "%#.3a", "0x1.348p10" }, new Object[]{ 1234.0F, "%5a", "0x1.348p10" }, new Object[]{ 1234.0F, "%- 12.0a", " 0x1.3p10   " }, new Object[]{ 1234.0F, "%#+01.6a", "+0x1.348000p10" }, new Object[]{ 1234.0F, "%-+8.4a", "+0x1.3480p10" }, new Object[]{ 1.0F, "%a", "0x1.0p0" }, new Object[]{ 1.0F, "%#.3a", "0x1.000p0" }, new Object[]{ 1.0F, "%5a", "0x1.0p0" }, new Object[]{ 1.0F, "%- 12.0a", " 0x1.0p0    " }, new Object[]{ 1.0F, "%#+01.6a", "+0x1.000000p0" }, new Object[]{ 1.0F, "%-+8.4a", "+0x1.0000p0" }, new Object[]{ -98.0F, "%a", "-0x1.88p6" }, new Object[]{ -98.0F, "%#.3a", "-0x1.880p6" }, new Object[]{ -98.0F, "%5a", "-0x1.88p6" }, new Object[]{ -98.0F, "%- 12.0a", "-0x1.8p6    " }, new Object[]{ -98.0F, "%#+01.6a", "-0x1.880000p6" }, new Object[]{ -98.0F, "%-+8.4a", "-0x1.8800p6" }, new Object[]{ 345.12344F, "%a", "0x1.591f9ap8" }, new Object[]{ 345.12344F, "%5a", "0x1.591f9ap8" }, new Object[]{ 345.12344F, "%#+01.6a", "+0x1.591f9ap8" }, new Object[]{ -9.8765434E8F, "%a", "-0x1.d6f346p29" }, new Object[]{ -9.8765434E8F, "%#.3a", "-0x1.d6fp29" }, new Object[]{ -9.8765434E8F, "%5a", "-0x1.d6f346p29" }, new Object[]{ -9.8765434E8F, "%- 12.0a", "-0x1.dp29   " }, new Object[]{ -9.8765434E8F, "%#+01.6a", "-0x1.d6f346p29" }, new Object[]{ -9.8765434E8F, "%-+8.4a", "-0x1.d6f3p29" }, new Object[]{ Float.MAX_VALUE, "%a", "0x1.fffffep127" }, new Object[]{ Float.MAX_VALUE, "%5a", "0x1.fffffep127" }, new Object[]{ Float.MAX_VALUE, "%#+01.6a", "+0x1.fffffep127" }, new Object[]{ Float.NaN, "%a", "NaN" }, new Object[]{ Float.NaN, "%#.3a", "NaN" }, new Object[]{ Float.NaN, "%5a", "  NaN" }, new Object[]{ Float.NaN, "%- 12.0a", "NaN         " }, new Object[]{ Float.NaN, "%#+01.6a", "NaN" }, new Object[]{ Float.NaN, "%-+8.4a", "NaN     " }, new Object[]{ Float.NEGATIVE_INFINITY, "%a", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%#.3a", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%5a", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%- 12.0a", "-Infinity   " }, new Object[]{ Float.NEGATIVE_INFINITY, "%#+01.6a", "-Infinity" }, new Object[]{ Float.NEGATIVE_INFINITY, "%-+8.4a", "-Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%a", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%#.3a", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%5a", "Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%- 12.0a", " Infinity   " }, new Object[]{ Float.POSITIVE_INFINITY, "%#+01.6a", "+Infinity" }, new Object[]{ Float.POSITIVE_INFINITY, "%-+8.4a", "+Infinity" }, new Object[]{ -0.0, "%a", "-0x0.0p0" }, new Object[]{ -0.0, "%#.3a", "-0x0.000p0" }, new Object[]{ -0.0, "%5a", "-0x0.0p0" }, new Object[]{ -0.0, "%- 12.0a", "-0x0.0p0    " }, new Object[]{ -0.0, "%#+01.6a", "-0x0.000000p0" }, new Object[]{ -0.0, "%-+8.4a", "-0x0.0000p0" }, new Object[]{ 0.0, "%a", "0x0.0p0" }, new Object[]{ 0.0, "%#.3a", "0x0.000p0" }, new Object[]{ 0.0, "%5a", "0x0.0p0" }, new Object[]{ 0.0, "%- 12.0a", " 0x0.0p0    " }, new Object[]{ 0.0, "%#+01.6a", "+0x0.000000p0" }, new Object[]{ 0.0, "%-+8.4a", "+0x0.0000p0" }, new Object[]{ 1.0, "%a", "0x1.0p0" }, new Object[]{ 1.0, "%#.3a", "0x1.000p0" }, new Object[]{ 1.0, "%5a", "0x1.0p0" }, new Object[]{ 1.0, "%- 12.0a", " 0x1.0p0    " }, new Object[]{ 1.0, "%#+01.6a", "+0x1.000000p0" }, new Object[]{ 1.0, "%-+8.4a", "+0x1.0000p0" }, new Object[]{ -1.0, "%a", "-0x1.0p0" }, new Object[]{ -1.0, "%#.3a", "-0x1.000p0" }, new Object[]{ -1.0, "%5a", "-0x1.0p0" }, new Object[]{ -1.0, "%- 12.0a", "-0x1.0p0    " }, new Object[]{ -1.0, "%#+01.6a", "-0x1.000000p0" }, new Object[]{ -1.0, "%-+8.4a", "-0x1.0000p0" }, new Object[]{ 1.0E-8, "%a", "0x1.5798ee2308c3ap-27" }, new Object[]{ 1.0E-8, "%5a", "0x1.5798ee2308c3ap-27" }, new Object[]{ 1.0E-8, "%- 12.0a", " 0x1.5p-27  " }, new Object[]{ 1.0E-8, "%#+01.6a", "+0x1.5798eep-27" }, new Object[]{ 1000.1, "%a", "0x1.f40cccccccccdp9" }, new Object[]{ 1000.1, "%5a", "0x1.f40cccccccccdp9" }, new Object[]{ 1000.1, "%- 12.0a", " 0x1.fp9    " }, new Object[]{ 0.1, "%a", "0x1.999999999999ap-4" }, new Object[]{ 0.1, "%5a", "0x1.999999999999ap-4" }, new Object[]{ -2.0, "%a", "-0x1.0p1" }, new Object[]{ -2.0, "%#.3a", "-0x1.000p1" }, new Object[]{ -2.0, "%5a", "-0x1.0p1" }, new Object[]{ -2.0, "%- 12.0a", "-0x1.0p1    " }, new Object[]{ -2.0, "%#+01.6a", "-0x1.000000p1" }, new Object[]{ -2.0, "%-+8.4a", "-0x1.0000p1" }, new Object[]{ -9.0E-5, "%a", "-0x1.797cc39ffd60fp-14" }, new Object[]{ -9.0E-5, "%5a", "-0x1.797cc39ffd60fp-14" }, new Object[]{ -1.2345678900123458E9, "%a", "-0x1.26580b480ca46p30" }, new Object[]{ -1.2345678900123458E9, "%5a", "-0x1.26580b480ca46p30" }, new Object[]{ -1.2345678900123458E9, "%- 12.0a", "-0x1.2p30   " }, new Object[]{ -1.2345678900123458E9, "%#+01.6a", "-0x1.26580bp30" }, new Object[]{ -1.2345678900123458E9, "%-+8.4a", "-0x1.2658p30" }, new Object[]{ Double.MAX_VALUE, "%a", "0x1.fffffffffffffp1023" }, new Object[]{ Double.MAX_VALUE, "%5a", "0x1.fffffffffffffp1023" }, new Object[]{ Double.MIN_VALUE, "%a", "0x0.0000000000001p-1022" }, new Object[]{ Double.MIN_VALUE, "%5a", "0x0.0000000000001p-1022" }, new Object[]{ Double.NaN, "%a", "NaN" }, new Object[]{ Double.NaN, "%#.3a", "NaN" }, new Object[]{ Double.NaN, "%5a", "  NaN" }, new Object[]{ Double.NaN, "%- 12.0a", "NaN         " }, new Object[]{ Double.NaN, "%#+01.6a", "NaN" }, new Object[]{ Double.NaN, "%-+8.4a", "NaN     " }, new Object[]{ Double.NEGATIVE_INFINITY, "%a", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%#.3a", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%5a", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%- 12.0a", "-Infinity   " }, new Object[]{ Double.NEGATIVE_INFINITY, "%#+01.6a", "-Infinity" }, new Object[]{ Double.NEGATIVE_INFINITY, "%-+8.4a", "-Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%a", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%#.3a", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%5a", "Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%- 12.0a", " Infinity   " }, new Object[]{ Double.POSITIVE_INFINITY, "%#+01.6a", "+Infinity" }, new Object[]{ Double.POSITIVE_INFINITY, "%-+8.4a", "+Infinity" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleA.length); i++) {
            f = new Formatter(Locale.UK);
            f.format(((String) (tripleA[i][pattern])), tripleA[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleA[i][input])) + ",pattern[") + i) + "]:") + (tripleA[i][pattern])), tripleA[i][output], f.toString());
            // test for conversion type 'A'
            f = new Formatter(Locale.UK);
            f.format(((String) (tripleA[i][pattern])).toUpperCase(), tripleA[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleA[i][input])) + ",pattern[") + i) + "]:") + (tripleA[i][pattern])), ((String) (tripleA[i][output])).toUpperCase(Locale.UK), f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigDecimal
     * conversion type 'e' and 'E'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigDecimalConversionE() {
        Formatter f = null;
        final Object[][] tripleE = new Object[][]{ new Object[]{ BigDecimal.ZERO, "%e", "0.000000e+00" }, new Object[]{ BigDecimal.ZERO, "%#.0e", "0.e+00" }, new Object[]{ BigDecimal.ZERO, "%# 9.8e", " 0.00000000e+00" }, new Object[]{ BigDecimal.ZERO, "%#+0(8.4e", "+0.0000e+00" }, new Object[]{ BigDecimal.ZERO, "%-+17.6e", "+0.000000e+00    " }, new Object[]{ BigDecimal.ZERO, "% 0(20e", " 00000000.000000e+00" }, new Object[]{ BigDecimal.ONE, "%e", "1.000000e+00" }, new Object[]{ BigDecimal.ONE, "%#.0e", "1.e+00" }, new Object[]{ BigDecimal.ONE, "%# 9.8e", " 1.00000000e+00" }, new Object[]{ BigDecimal.ONE, "%#+0(8.4e", "+1.0000e+00" }, new Object[]{ BigDecimal.ONE, "%-+17.6e", "+1.000000e+00    " }, new Object[]{ BigDecimal.ONE, "% 0(20e", " 00000001.000000e+00" }, new Object[]{ BigDecimal.TEN, "%e", "1.000000e+01" }, new Object[]{ BigDecimal.TEN, "%#.0e", "1.e+01" }, new Object[]{ BigDecimal.TEN, "%# 9.8e", " 1.00000000e+01" }, new Object[]{ BigDecimal.TEN, "%#+0(8.4e", "+1.0000e+01" }, new Object[]{ BigDecimal.TEN, "%-+17.6e", "+1.000000e+01    " }, new Object[]{ BigDecimal.TEN, "% 0(20e", " 00000001.000000e+01" }, new Object[]{ new BigDecimal((-1)), "%e", "-1.000000e+00" }, new Object[]{ new BigDecimal((-1)), "%#.0e", "-1.e+00" }, new Object[]{ new BigDecimal((-1)), "%# 9.8e", "-1.00000000e+00" }, new Object[]{ new BigDecimal((-1)), "%#+0(8.4e", "(1.0000e+00)" }, new Object[]{ new BigDecimal((-1)), "%-+17.6e", "-1.000000e+00    " }, new Object[]{ new BigDecimal((-1)), "% 0(20e", "(0000001.000000e+00)" }, new Object[]{ new BigDecimal("5.000E999"), "%e", "5.000000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%#.0e", "5.e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%# 9.8e", " 5.00000000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%#+0(8.4e", "+5.0000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%-+17.6e", "+5.000000e+999   " }, new Object[]{ new BigDecimal("5.000E999"), "% 0(20e", " 0000005.000000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%e", "-5.000000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%#.0e", "-5.e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%# 9.8e", "-5.00000000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%#+0(8.4e", "(5.0000e+999)" }, new Object[]{ new BigDecimal("-5.000E999"), "%-+17.6e", "-5.000000e+999   " }, new Object[]{ new BigDecimal("-5.000E999"), "% 0(20e", "(000005.000000e+999)" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleE.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (tripleE[i][pattern])), tripleE[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleE[i][input])) + ",pattern[") + i) + "]:") + (tripleE[i][pattern])), tripleE[i][output], f.toString());
            // test for conversion type 'E'
            f = new Formatter(Locale.US);
            f.format(((String) (tripleE[i][pattern])).toUpperCase(), tripleE[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleE[i][input])) + ",pattern[") + i) + "]:") + (tripleE[i][pattern])), ((String) (tripleE[i][output])).toUpperCase(Locale.US), f.toString());
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigDecimal
     * conversion type 'g' and 'G'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigDecimalConversionG() {
        Formatter f = null;
        final Object[][] tripleG = new Object[][]{ new Object[]{ BigDecimal.ZERO, "%g", "0.00000" }, new Object[]{ BigDecimal.ZERO, "%.5g", "0.0000" }, new Object[]{ BigDecimal.ZERO, "%- (,9.8g", " 0.0000000" }, new Object[]{ BigDecimal.ZERO, "%+0(,8.4g", "+000.000" }, new Object[]{ BigDecimal.ZERO, "%-+10.6g", "+0.00000  " }, new Object[]{ BigDecimal.ZERO, "% 0(,12.0g", " 00000000000" }, new Object[]{ BigDecimal.ONE, "%g", "1.00000" }, new Object[]{ BigDecimal.ONE, "%.5g", "1.0000" }, new Object[]{ BigDecimal.ONE, "%- (,9.8g", " 1.0000000" }, new Object[]{ BigDecimal.ONE, "%+0(,8.4g", "+001.000" }, new Object[]{ BigDecimal.ONE, "%-+10.6g", "+1.00000  " }, new Object[]{ BigDecimal.ONE, "% 0(,12.0g", " 00000000001" }, new Object[]{ new BigDecimal((-1)), "%g", "-1.00000" }, new Object[]{ new BigDecimal((-1)), "%.5g", "-1.0000" }, new Object[]{ new BigDecimal((-1)), "%- (,9.8g", "(1.0000000)" }, new Object[]{ new BigDecimal((-1)), "%+0(,8.4g", "(01.000)" }, new Object[]{ new BigDecimal((-1)), "%-+10.6g", "-1.00000  " }, new Object[]{ new BigDecimal((-1)), "% 0(,12.0g", "(0000000001)" }, new Object[]{ new BigDecimal((-1.0E-6)), "%g", "-1.00000e-06" }, new Object[]{ new BigDecimal((-1.0E-6)), "%.5g", "-1.0000e-06" }, new Object[]{ new BigDecimal((-1.0E-6)), "%- (,9.8g", "(1.0000000e-06)" }, new Object[]{ new BigDecimal((-1.0E-6)), "%+0(,8.4g", "(1.000e-06)" }, new Object[]{ new BigDecimal((-1.0E-6)), "%-+10.6g", "-1.00000e-06" }, new Object[]{ new BigDecimal((-1.0E-6)), "% 0(,12.0g", "(000001e-06)" }, new Object[]{ new BigDecimal(2.0E-4), "%g", "0.000200000" }, new Object[]{ new BigDecimal(2.0E-4), "%.5g", "0.00020000" }, new Object[]{ new BigDecimal(2.0E-4), "%- (,9.8g", " 0.00020000000" }, new Object[]{ new BigDecimal(2.0E-4), "%+0(,8.4g", "+0.0002000" }, new Object[]{ new BigDecimal(2.0E-4), "%-+10.6g", "+0.000200000" }, new Object[]{ new BigDecimal(2.0E-4), "% 0(,12.0g", " 000000.0002" }, new Object[]{ new BigDecimal((-0.003)), "%g", "-0.00300000" }, new Object[]{ new BigDecimal((-0.003)), "%.5g", "-0.0030000" }, new Object[]{ new BigDecimal((-0.003)), "%- (,9.8g", "(0.0030000000)" }, new Object[]{ new BigDecimal((-0.003)), "%+0(,8.4g", "(0.003000)" }, new Object[]{ new BigDecimal((-0.003)), "%-+10.6g", "-0.00300000" }, new Object[]{ new BigDecimal((-0.003)), "% 0(,12.0g", "(000000.003)" }, new Object[]{ new BigDecimal("5.000E999"), "%g", "5.00000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%.5g", "5.0000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%- (,9.8g", " 5.0000000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%+0(,8.4g", "+5.000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "%-+10.6g", "+5.00000e+999" }, new Object[]{ new BigDecimal("5.000E999"), "% 0(,12.0g", " 000005e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%g", "-5.00000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%.5g", "-5.0000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "%- (,9.8g", "(5.0000000e+999)" }, new Object[]{ new BigDecimal("-5.000E999"), "%+0(,8.4g", "(5.000e+999)" }, new Object[]{ new BigDecimal("-5.000E999"), "%-+10.6g", "-5.00000e+999" }, new Object[]{ new BigDecimal("-5.000E999"), "% 0(,12.0g", "(00005e+999)" } };
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        for (int i = 0; i < (tripleG.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (tripleG[i][pattern])), tripleG[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleG[i][input])) + ",pattern[") + i) + "]:") + (tripleG[i][pattern])), tripleG[i][output], f.toString());
            // test for conversion type 'G'
            f = new Formatter(Locale.US);
            f.format(((String) (tripleG[i][pattern])).toUpperCase(), tripleG[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleG[i][input])) + ",pattern[") + i) + "]:") + (tripleG[i][pattern])), ((String) (tripleG[i][output])).toUpperCase(Locale.US), f.toString());
        }
        f = new Formatter(Locale.GERMAN);
        f.format("%- (,9.6g", new BigDecimal("4E6"));
        /* fail on RI, spec says 'g' requires the output to be formatted in
        general scientific notation and the localization algorithm is
        applied. But RI format this case to 4.00000e+06, which does not
        conform to the German Locale
         */
        TestCase.assertEquals(" 4,00000e+06", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigDecimal
     * conversion type 'f'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigDecimalConversionF() {
        Formatter f = null;
        final int input = 0;
        final int pattern = 1;
        final int output = 2;
        final Object[][] tripleF = new Object[][]{ new Object[]{ BigDecimal.ZERO, "%f", "0.000000" }, new Object[]{ BigDecimal.ZERO, "%#.3f", "0.000" }, new Object[]{ BigDecimal.ZERO, "%#,5f", "0.000000" }, new Object[]{ BigDecimal.ZERO, "%- #(12.0f", " 0.         " }, new Object[]{ BigDecimal.ZERO, "%#+0(1.6f", "+0.000000" }, new Object[]{ BigDecimal.ZERO, "%-+(8.4f", "+0.0000 " }, new Object[]{ BigDecimal.ZERO, "% 0#(9.8f", " 0.00000000" }, new Object[]{ BigDecimal.ONE, "%f", "1.000000" }, new Object[]{ BigDecimal.ONE, "%#.3f", "1.000" }, new Object[]{ BigDecimal.ONE, "%#,5f", "1.000000" }, new Object[]{ BigDecimal.ONE, "%- #(12.0f", " 1.         " }, new Object[]{ BigDecimal.ONE, "%#+0(1.6f", "+1.000000" }, new Object[]{ BigDecimal.ONE, "%-+(8.4f", "+1.0000 " }, new Object[]{ BigDecimal.ONE, "% 0#(9.8f", " 1.00000000" }, new Object[]{ BigDecimal.TEN, "%f", "10.000000" }, new Object[]{ BigDecimal.TEN, "%#.3f", "10.000" }, new Object[]{ BigDecimal.TEN, "%#,5f", "10.000000" }, new Object[]{ BigDecimal.TEN, "%- #(12.0f", " 10.        " }, new Object[]{ BigDecimal.TEN, "%#+0(1.6f", "+10.000000" }, new Object[]{ BigDecimal.TEN, "%-+(8.4f", "+10.0000" }, new Object[]{ BigDecimal.TEN, "% 0#(9.8f", " 10.00000000" }, new Object[]{ new BigDecimal((-1)), "%f", "-1.000000" }, new Object[]{ new BigDecimal((-1)), "%#.3f", "-1.000" }, new Object[]{ new BigDecimal((-1)), "%#,5f", "-1.000000" }, new Object[]{ new BigDecimal((-1)), "%- #(12.0f", "(1.)        " }, new Object[]{ new BigDecimal((-1)), "%#+0(1.6f", "(1.000000)" }, new Object[]{ new BigDecimal((-1)), "%-+(8.4f", "(1.0000)" }, new Object[]{ new BigDecimal((-1)), "% 0#(9.8f", "(1.00000000)" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%f", "9999999999999999999999999999999999999999999.000000" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%#.3f", "9999999999999999999999999999999999999999999.000" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%#,5f", "9,999,999,999,999,999,999,999,999,999,999,999,999,999,999.000000" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%- #(12.0f", " 9999999999999999999999999999999999999999999." }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%#+0(1.6f", "+9999999999999999999999999999999999999999999.000000" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "%-+(8.4f", "+9999999999999999999999999999999999999999999.0000" }, new Object[]{ new BigDecimal("9999999999999999999999999999999999999999999"), "% 0#(9.8f", " 9999999999999999999999999999999999999999999.00000000" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%f", "-9999999999999999999999999999999999999999999.000000" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%#.3f", "-9999999999999999999999999999999999999999999.000" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%#,5f", "-9,999,999,999,999,999,999,999,999,999,999,999,999,999,999.000000" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%- #(12.0f", "(9999999999999999999999999999999999999999999.)" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%#+0(1.6f", "(9999999999999999999999999999999999999999999.000000)" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "%-+(8.4f", "(9999999999999999999999999999999999999999999.0000)" }, new Object[]{ new BigDecimal("-9999999999999999999999999999999999999999999"), "% 0#(9.8f", "(9999999999999999999999999999999999999999999.00000000)" } };
        for (int i = 0; i < (tripleF.length); i++) {
            f = new Formatter(Locale.US);
            f.format(((String) (tripleF[i][pattern])), tripleF[i][input]);
            TestCase.assertEquals(((((((("triple[" + i) + "]:") + (tripleF[i][input])) + ",pattern[") + i) + "]:") + (tripleF[i][pattern])), tripleF[i][output], f.toString());
        }
        f = new Formatter(Locale.US);
        f.format("%f", new BigDecimal("5.0E9"));
        // error on RI
        // RI throw ArrayIndexOutOfBoundsException
        TestCase.assertEquals("5000000000.000000", f.toString());
    }

    /**
     * java.util.Formatter#format(String, Object...) for exceptions in
     * Float/Double/BigDecimal conversion type 'e', 'E', 'g', 'G', 'f', 'a', 'A'
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatDoubleBigDecimalConversionException() {
        Formatter f = null;
        final char[] conversions = new char[]{ 'e', 'E', 'g', 'G', 'f', 'a', 'A' };
        final Object[] illArgs = new Object[]{ false, ((byte) (1)), ((short) (2)), 3, ((long) (4)), new BigInteger("5"), new Character('c'), new Object(), new Date() };
        for (int i = 0; i < (illArgs.length); i++) {
            for (int j = 0; j < (conversions.length); j++) {
                try {
                    f = new Formatter(Locale.UK);
                    f.format(("%" + (conversions[j])), illArgs[i]);
                    TestCase.fail("should throw IllegalFormatConversionException");
                } catch (IllegalFormatConversionException e) {
                    // expected
                }
            }
        }
        try {
            f = new Formatter(Locale.UK);
            f.format("%a", new BigDecimal(1));
            TestCase.fail("should throw IllegalFormatConversionException");
        } catch (IllegalFormatConversionException e) {
            // expected
        }
        try {
            f = new Formatter(Locale.UK);
            f.format("%A", new BigDecimal(1));
            TestCase.fail("should throw IllegalFormatConversionException");
        } catch (IllegalFormatConversionException e) {
            // expected
        }
        final String[] flagsConversionMismatches = new String[]{ "%,e", "%,E", "%#g", "%#G", "%,a", "%,A", "%(a", "%(A" };
        for (int i = 0; i < (flagsConversionMismatches.length); i++) {
            try {
                f = new Formatter(Locale.CHINA);
                f.format(flagsConversionMismatches[i], new BigDecimal(1));
                TestCase.fail("should throw FormatFlagsConversionMismatchException");
            } catch (FormatFlagsConversionMismatchException e) {
                // expected
            }
            try {
                f = new Formatter(Locale.JAPAN);
                f.format(flagsConversionMismatches[i], ((BigDecimal) (null)));
                TestCase.fail("should throw FormatFlagsConversionMismatchException");
            } catch (FormatFlagsConversionMismatchException e) {
                // expected
            }
        }
        final String[] missingFormatWidths = new String[]{ "%-0e", "%0e", "%-e", "%-0E", "%0E", "%-E", "%-0g", "%0g", "%-g", "%-0G", "%0G", "%-G", "%-0f", "%0f", "%-f", "%-0a", "%0a", "%-a", "%-0A", "%0A", "%-A" };
        for (int i = 0; i < (missingFormatWidths.length); i++) {
            try {
                f = new Formatter(Locale.KOREA);
                f.format(missingFormatWidths[i], 1.0F);
                TestCase.fail("should throw MissingFormatWidthException");
            } catch (MissingFormatWidthException e) {
                // expected
            }
            try {
                f = new Formatter(Locale.KOREA);
                f.format(missingFormatWidths[i], ((Float) (null)));
                TestCase.fail("should throw MissingFormatWidthException");
            } catch (MissingFormatWidthException e) {
                // expected
            }
        }
        final String[] illFlags = new String[]{ "%+ e", "%+ E", "%+ g", "%+ G", "%+ f", "%+ a", "%+ A", "%-03e", "%-03E", "%-03g", "%-03G", "%-03f", "%-03a", "%-03A" };
        for (int i = 0; i < (illFlags.length); i++) {
            try {
                f = new Formatter(Locale.CANADA);
                f.format(illFlags[i], 1.23);
                TestCase.fail("should throw IllegalFormatFlagsException");
            } catch (IllegalFormatFlagsException e) {
                // expected
            }
            try {
                f = new Formatter(Locale.CANADA);
                f.format(illFlags[i], ((Double) (null)));
                TestCase.fail("should throw IllegalFormatFlagsException");
            } catch (IllegalFormatFlagsException e) {
                // expected
            }
        }
        f = new Formatter(Locale.US);
        try {
            f.format("%F", 1);
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for
     * Float/Double/BigDecimal exception throwing order
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatDoubleBigDecimalExceptionOrder() {
        Formatter f = null;
        /* Summary: UnknownFormatConversionException >
        MissingFormatWidthException > IllegalFormatFlagsException >
        FormatFlagsConversionMismatchException >
        IllegalFormatConversionException
         */
        try {
            // compare FormatFlagsConversionMismatchException and
            // IllegalFormatConversionException
            f = new Formatter(Locale.US);
            f.format("%,e", ((byte) (1)));
            TestCase.fail("should throw FormatFlagsConversionMismatchException");
        } catch (FormatFlagsConversionMismatchException e) {
            // expected
        }
        try {
            // compare IllegalFormatFlagsException and
            // FormatFlagsConversionMismatchException
            f = new Formatter(Locale.US);
            f.format("%+ ,e", 1.0F);
            TestCase.fail("should throw IllegalFormatFlagsException");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            // compare MissingFormatWidthException and
            // IllegalFormatFlagsException
            f = new Formatter(Locale.US);
            f.format("%+ -e", 1.0F);
            TestCase.fail("should throw MissingFormatWidthException");
        } catch (MissingFormatWidthException e) {
            // expected
        }
        try {
            // compare UnknownFormatConversionException and
            // MissingFormatWidthException
            f = new Formatter(Locale.US);
            f.format("%-F", 1.0F);
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for BigDecimal
     * exception throwing order
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_BigDecimalExceptionOrder() {
        Formatter f = null;
        BigDecimal bd = new BigDecimal("1.0");
        /* Summary: UnknownFormatConversionException >
        MissingFormatWidthException > IllegalFormatFlagsException >
        FormatFlagsConversionMismatchException >
        IllegalFormatConversionException
         */
        try {
            // compare FormatFlagsConversionMismatchException and
            // IllegalFormatConversionException
            f = new Formatter(Locale.US);
            f.format("%,e", ((byte) (1)));
            TestCase.fail("should throw FormatFlagsConversionMismatchException");
        } catch (FormatFlagsConversionMismatchException e) {
            // expected
        }
        try {
            // compare IllegalFormatFlagsException and
            // FormatFlagsConversionMismatchException
            f = new Formatter(Locale.US);
            f.format("%+ ,e", bd);
            TestCase.fail("should throw IllegalFormatFlagsException");
        } catch (IllegalFormatFlagsException e) {
            // expected
        }
        try {
            // compare MissingFormatWidthException and
            // IllegalFormatFlagsException
            f = new Formatter(Locale.US);
            f.format("%+ -e", bd);
            TestCase.fail("should throw MissingFormatWidthException");
        } catch (MissingFormatWidthException e) {
            // expected
        }
        // compare UnknownFormatConversionException and
        // MissingFormatWidthException
        try {
            f = new Formatter(Locale.US);
            f.format("%-F", bd);
            TestCase.fail("should throw UnknownFormatConversionException");
        } catch (UnknownFormatConversionException e) {
            // expected
        }
    }

    /**
     * java.util.Formatter#format(String, Object...) for null argment for
     * Float/Double/BigDecimal conversion
     */
    public void test_formatLjava_lang_String$Ljava_lang_Object_FloatDoubleBigDecimalNullConversion() {
        Formatter f = null;
        // test (Float)null
        f = new Formatter(Locale.FRANCE);
        f.format("%#- (9.0e", ((Float) (null)));
        TestCase.assertEquals("         ", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%-+(1.6E", ((Float) (null)));
        TestCase.assertEquals("NULL", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%+0(,8.4g", ((Float) (null)));
        TestCase.assertEquals("    null", f.toString());
        f = new Formatter(Locale.FRANCE);
        f.format("%- (9.8G", ((Float) (null)));
        TestCase.assertEquals("NULL     ", f.toString());
        f = new Formatter(Locale.FRANCE);
        f.format("%- (12.1f", ((Float) (null)));
        TestCase.assertEquals("n           ", f.toString());
        f = new Formatter(Locale.FRANCE);
        f.format("% .4a", ((Float) (null)));
        TestCase.assertEquals("null", f.toString());
        f = new Formatter(Locale.FRANCE);
        f.format("%06A", ((Float) (null)));
        TestCase.assertEquals("  NULL", f.toString());
        // test (Double)null
        f = new Formatter(Locale.GERMAN);
        f.format("%- (9e", ((Double) (null)));
        TestCase.assertEquals("null     ", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%#-+(1.6E", ((Double) (null)));
        TestCase.assertEquals("NULL", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%+0(6.4g", ((Double) (null)));
        TestCase.assertEquals("  null", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%- (,5.8G", ((Double) (null)));
        TestCase.assertEquals("NULL ", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("% (.4f", ((Double) (null)));
        TestCase.assertEquals("null", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("%#.6a", ((Double) (null)));
        TestCase.assertEquals("null", f.toString());
        f = new Formatter(Locale.GERMAN);
        f.format("% 2.5A", ((Double) (null)));
        TestCase.assertEquals("NULL", f.toString());
        // test (BigDecimal)null
        f = new Formatter(Locale.UK);
        f.format("%#- (6.2e", ((BigDecimal) (null)));
        TestCase.assertEquals("nu    ", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%-+(1.6E", ((BigDecimal) (null)));
        TestCase.assertEquals("NULL", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%+-(,5.3g", ((BigDecimal) (null)));
        TestCase.assertEquals("nul  ", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%0 3G", ((BigDecimal) (null)));
        TestCase.assertEquals("NULL", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%0 (9.0G", ((BigDecimal) (null)));
        TestCase.assertEquals("         ", f.toString());
        f = new Formatter(Locale.UK);
        f.format("% (.5f", ((BigDecimal) (null)));
        TestCase.assertEquals("null", f.toString());
        f = new Formatter(Locale.UK);
        f.format("%06a", ((BigDecimal) (null)));
        TestCase.assertEquals("  null", f.toString());
        f = new Formatter(Locale.UK);
        f.format("% .5A", ((BigDecimal) (null)));
        TestCase.assertEquals("NULL", f.toString());
    }

    /**
     * java.util.Formatter.BigDecimalLayoutForm#values()
     */
    public void test_values() {
        Formatter.BigDecimalLayoutForm[] vals = values();
        TestCase.assertEquals("Invalid length of enum values", 2, vals.length);
        TestCase.assertEquals("Wrong scientific value in enum", SCIENTIFIC, vals[0]);
        TestCase.assertEquals("Wrong dec float value in enum", DECIMAL_FLOAT, vals[1]);
    }

    /**
     * java.util.Formatter.BigDecimalLayoutForm#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        Formatter.BigDecimalLayoutForm sci = valueOf("SCIENTIFIC");
        TestCase.assertEquals("Wrong scientific value in enum", SCIENTIFIC, sci);
        Formatter.BigDecimalLayoutForm decFloat = valueOf("DECIMAL_FLOAT");
        TestCase.assertEquals("Wrong dec float value from valueOf ", DECIMAL_FLOAT, decFloat);
    }

    /* Regression test for Harmony-5845
    test the short name for timezone whether uses DaylightTime or not
     */
    public void test_DaylightTime() {
        Locale.setDefault(Locale.US);
        Calendar c1 = new GregorianCalendar(2007, 0, 1);
        Calendar c2 = new GregorianCalendar(2007, 7, 1);
        for (String tz : TimeZone.getAvailableIDs()) {
            if (tz.equals("America/Los_Angeles")) {
                c1.setTimeZone(TimeZone.getTimeZone(tz));
                c2.setTimeZone(TimeZone.getTimeZone(tz));
                TestCase.assertTrue(String.format("%1$tZ%2$tZ", c1, c2).equals("PSTPDT"));
            }
            if (tz.equals("America/Panama")) {
                c1.setTimeZone(TimeZone.getTimeZone(tz));
                c2.setTimeZone(TimeZone.getTimeZone(tz));
                TestCase.assertTrue(String.format("%1$tZ%2$tZ", c1, c2).equals("ESTEST"));
            }
        }
    }

    /* Regression test for Harmony-5845
    test scientific notation to follow RI's behavior
     */
    public void test_ScientificNotation() {
        Formatter f = new Formatter();
        MathContext mc = new MathContext(30);
        BigDecimal value = new BigDecimal(0.1, mc);
        f.format("%.30G", value);
        String result = f.toString();
        String expected = "0.100000000000000005551115123126";
        TestCase.assertEquals(expected, result);
    }
}

