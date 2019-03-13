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
package libcore.java.text;


import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;


public class OldDateFormatTest extends TestCase {
    private class MockDateFormat extends DateFormat {
        private static final long serialVersionUID = 1L;

        public MockDateFormat() {
            super();
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            // it is a fake
            return null;
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            // it is a fake
            return null;
        }
    }

    /**
     * java.text.DateFormat#DateFormat() Test of method
     *        java.text.DateFormat#DateFormat().
     */
    public void test_Constructor() {
        try {
            new OldDateFormatTest.MockDateFormat();
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#equals(java.lang.Object obj) Test of
     *        java.text.DateFormat#equals(java.lang.Object obj).
     */
    public void test_equalsLjava_lang_Object() {
        try {
            DateFormat format = DateFormat.getInstance();
            DateFormat clone = ((DateFormat) (format.clone()));
            TestCase.assertTrue("Clone and parent are not equaled", format.equals(clone));
            TestCase.assertTrue("Clone is equal to other object", (!(clone.equals(DateFormat.getTimeInstance()))));
            format.setCalendar(Calendar.getInstance());
            TestCase.assertTrue("Clone and parent are not equaled", format.equals(clone));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#format(java.util.Date) Test of method
     *        java.text.DateFormat#format(java.util.Date).
     */
    public void test_formatLjava_util_Date() {
        try {
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
            Date current = new Date();
            String dtf = format.format(current);
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm a", Locale.US);
            TestCase.assertTrue("Incorrect date format", sdf.format(current).equals(dtf));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#format(Object, StringBuffer, FieldPosition)
     *        Test of method java.text.DateFormat#format(Object, StringBuffer,
     *        FieldPosition)
     */
    public void test_formatLjava_lang_ObjectLjava_lang_StringBufferLjava_text_FieldPosition() {
        try {
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
            Date current = new Date();
            StringBuffer toAppend = new StringBuffer();
            FieldPosition fp = new FieldPosition(DateFormat.YEAR_FIELD);
            StringBuffer sb = format.format(current, toAppend, fp);
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm a", Locale.US);
            TestCase.assertTrue("Incorrect date format", sdf.format(current).equals(sb.toString()));
            TestCase.assertTrue("Incorrect beginIndex of filed position", ((fp.getBeginIndex()) == ((sb.lastIndexOf("/")) + 1)));
            TestCase.assertTrue("Incorrect endIndex of filed position", ((fp.getEndIndex()) == ((sb.lastIndexOf("/")) + 3)));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    public void test_getTimeZone() {
        try {
            DateFormat format = DateFormat.getInstance();
            TimeZone tz = format.getTimeZone();
            // if(1 == 1)
            // throw new Exception(tz.getClass().getName());
            // We know we are not sun.util so:
            // Redundant checking
            // assertFalse("Incorrect zone info", tz.getClass().getName().equals(
            // "sun.util.calendar.ZoneInfo"));
            TestCase.assertTrue("Incorrect time zone", tz.equals(format.getCalendar().getTimeZone()));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#hashCode() Test of method
     *        java.text.DateFormat#hashCode().
     */
    public void test_hashCode() {
        try {
            DateFormat df1 = DateFormat.getInstance();
            DateFormat df2 = ((DateFormat) (df1.clone()));
            TestCase.assertTrue("Hash codes of clones are not equal", ((df1.hashCode()) == (df2.hashCode())));
            TestCase.assertTrue("Hash codes of different objects are the same", ((df1.hashCode()) != (DateFormat.getDateInstance().hashCode())));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#isLenient() Test of method
     *        java.text.DateFormat#isLenient().
     */
    public void test_isLenient() {
        DateFormat df = DateFormat.getInstance();
        Calendar c = df.getCalendar();
        if (df.isLenient()) {
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
            c.setLenient(false);
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
                TestCase.fail("Expected IllegalArgumentException was not thrown");
            } catch (IllegalArgumentException e) {
                // expected
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
        } else {
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
                TestCase.fail("Expected IllegalArgumentException was not thrown");
            } catch (IllegalArgumentException e) {
                // expected
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
            c.setLenient(true);
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
        }
    }

    /**
     * java.text.DateFormat#parse(String)
     */
    public void test_parseLString() throws Exception {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        try {
            format.parse("not a Date");
            TestCase.fail("should throw ParseException first");
        } catch (ParseException pe) {
            TestCase.assertNotNull(pe.getMessage());
        }
        Date current = new Date();
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(current.getDate(), date.getDate());
            TestCase.assertEquals(current.getDay(), date.getDay());
            TestCase.assertEquals(current.getMonth(), date.getMonth());
            TestCase.assertEquals(current.getYear(), date.getYear());
            TestCase.assertEquals(current.getHours(), date.getHours());
            TestCase.assertEquals(current.getMinutes(), date.getMinutes());
            TestCase.assertEquals(0, date.getSeconds());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        try {
            format.parse("27/08/1998");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        try {
            format.parse("30/30/908 4:50, PDT");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        try {
            format.parse("837039928046");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.US);
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(current.getDate(), date.getDate());
            TestCase.assertEquals(current.getDay(), date.getDay());
            TestCase.assertEquals(current.getMonth(), date.getMonth());
            TestCase.assertEquals(current.getYear(), date.getYear());
            TestCase.assertEquals(0, date.getHours());
            TestCase.assertEquals(0, date.getMinutes());
            TestCase.assertEquals(0, date.getSeconds());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        try {
            format.parse("Jan 16 1970");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        try {
            format.parse("27/08/1998");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        format = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(current.getDate(), date.getDate());
            TestCase.assertEquals(current.getDay(), date.getDay());
            TestCase.assertEquals(current.getMonth(), date.getMonth());
            TestCase.assertEquals(current.getYear(), date.getYear());
            TestCase.assertEquals(0, date.getHours());
            TestCase.assertEquals(0, date.getMinutes());
            TestCase.assertEquals(0, date.getSeconds());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(current.getDate(), date.getDate());
            TestCase.assertEquals(current.getDay(), date.getDay());
            TestCase.assertEquals(current.getMonth(), date.getMonth());
            TestCase.assertEquals(current.getYear(), date.getYear());
            TestCase.assertEquals(0, date.getHours());
            TestCase.assertEquals(0, date.getMinutes());
            TestCase.assertEquals(0, date.getSeconds());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        format = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.US);
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(1, date.getDate());
            TestCase.assertEquals(0, date.getMonth());
            TestCase.assertEquals(70, date.getYear());
            TestCase.assertEquals(current.getHours(), date.getHours());
            TestCase.assertEquals(current.getMinutes(), date.getMinutes());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        try {
            format.parse("8:58:44");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException pe) {
            // expected
        }
        format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.US);
        try {
            Date date = format.parse(format.format(current).toString());
            TestCase.assertEquals(current.getDate(), date.getDate());
            TestCase.assertEquals(current.getDay(), date.getDay());
            TestCase.assertEquals(current.getMonth(), date.getMonth());
            TestCase.assertEquals(current.getYear(), date.getYear());
            TestCase.assertEquals(current.getHours(), date.getHours());
            TestCase.assertEquals(current.getMinutes(), date.getMinutes());
        } catch (ParseException pe) {
            TestCase.fail("ParseException was thrown for current Date.");
        }
        try {
            format.parse("January 31 1970 7:52:34 AM PST");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException expected) {
        }
        try {
            format.parse("January 31 1970");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException expected) {
        }
        format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
        String formatPattern = ((SimpleDateFormat) (format)).toPattern();
        String formattedCurrent = format.format(current);
        Date date = format.parse(formattedCurrent);
        // Date has millisecond accuracy, but humans don't use time formats that precise.
        if (((date.getTime()) / 1000) != ((current.getTime()) / 1000)) {
            TestCase.fail((((((((date.getTime()) + " != ") + (current.getTime())) + "; ") + formatPattern) + "; ") + formattedCurrent));
        }
        try {
            format.parse("January 16, 1970 8:03:52 PM CET");
            TestCase.fail("ParseException was not thrown.");
        } catch (ParseException expected) {
        }
    }

    /**
     * java.text.DateFormat#parseObject(String, ParsePosition) Test of
     *        method java.text.DateFormat#parseObject(String, ParsePosition).
     *        Case 1: Try to parse correct data string. Case 2: Try to parse
     *        partialy correct data string. Case 3: Try to use argument
     *        ParsePosition as null.
     */
    public void test_parseObjectLjava_lang_StringLjava_text_ParsePosition() {
        DateFormat df = DateFormat.getInstance();
        try {
            // case 1: Try to parse correct data string.
            Date current = new Date();
            ParsePosition pp = new ParsePosition(0);
            int parseIndex = pp.getIndex();
            Date result = ((Date) (df.parseObject(df.format(current), pp)));
            TestCase.assertEquals("Dates are different.", current.getDate(), result.getDate());
            TestCase.assertEquals("Days are different.", current.getDay(), result.getDay());
            TestCase.assertEquals("Months are different.", current.getMonth(), result.getMonth());
            TestCase.assertEquals("Years are different.", current.getYear(), result.getYear());
            TestCase.assertEquals("Hours are different", current.getHours(), result.getHours());
            TestCase.assertEquals("Minutes are diffetrent,", current.getMinutes(), result.getMinutes());
            TestCase.assertTrue("Parse operation return null", (result != null));
            TestCase.assertTrue("ParseIndex is incorrect", ((pp.getIndex()) != parseIndex));
            // case 2: Try to parse partially correct data string.
            pp.setIndex(0);
            char[] cur = df.format(current).toCharArray();
            cur[((cur.length) / 2)] = 'Z';
            String partialCorrect = new String(cur);
            result = ((Date) (df.parseObject(partialCorrect, pp)));
            TestCase.assertTrue("Parse operation return not-null", (result == null));
            TestCase.assertTrue("ParseIndex is incorrect", ((pp.getIndex()) == 0));
            TestCase.assertTrue("ParseErrorIndex is incorrect", ((pp.getErrorIndex()) == ((cur.length) / 2)));
            pp.setIndex(2);
            char[] curDate = df.format(current).toCharArray();
            char[] newArray = new char[(curDate.length) + (pp.getIndex())];
            for (int i = 0; i < (curDate.length); i++) {
                newArray[(i + (pp.getIndex()))] = curDate[i];
            }
            result = ((Date) (df.parseObject(new String(newArray), pp)));
            // assertEquals(current, result);
            TestCase.assertEquals("Dates are different.", current.getDate(), result.getDate());
            TestCase.assertEquals("Days are different.", current.getDay(), result.getDay());
            TestCase.assertEquals("Months are different.", current.getMonth(), result.getMonth());
            TestCase.assertEquals("Years are different.", current.getYear(), result.getYear());
            TestCase.assertEquals("Hours are different", current.getHours(), result.getHours());
            TestCase.assertEquals("Minutes are diffetrent,", current.getMinutes(), result.getMinutes());
            // case 3: Try to use argument ParsePosition as null.
            try {
                df.parseObject(df.format(current), null);
                TestCase.fail("Expected NullPointerException was not thrown");
            } catch (NullPointerException e) {
                // expected
            }
            TestCase.assertNull(df.parseObject("test", pp));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#setLenient(boolean) Test of method
     *        java.text.DateFormat#setLenient(boolean).
     */
    public void test_setLenientZ() {
        DateFormat df = DateFormat.getInstance();
        Calendar c = df.getCalendar();
        try {
            c.setLenient(true);
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
            c.setLenient(false);
            try {
                c.set(Calendar.DAY_OF_MONTH, 32);
                c.get(Calendar.DAY_OF_MONTH);
                TestCase.fail("Expected IllegalArgumentException was not thrown");
            } catch (IllegalArgumentException e) {
                // expected
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception " + (e.toString())));
            }
        } catch (Exception e) {
            TestCase.fail(("Uexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.DateFormat#setTimeZone(TimeZone) Test of method
     *        java.text.DateFormat#setTimeZone(TimeZone).
     */
    public void test_setTimeZoneLjava_util_TimeZone() {
        try {
            DateFormat format = DateFormat.getInstance();
            TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
            format.setTimeZone(tz);
            TestCase.assertTrue("TimeZone is set incorrectly", tz.equals(format.getTimeZone()));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }
}

