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
package org.apache.harmony.tests.java.text;


import java.text.ChoiceFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import junit.framework.TestCase;


public class ChoiceFormatTest extends TestCase {
    double[] limits = new double[]{ 0, 1, ChoiceFormat.nextDouble(1), ChoiceFormat.nextDouble(2) };

    String[] formats = new String[]{ "Less than one", "one", "Between one and two", "Greater than two" };

    ChoiceFormat f1 = new ChoiceFormat(limits, formats);

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#ChoiceFormat(double[], java.lang.String[])
     */
    public void test_Constructor$D$Ljava_lang_String() {
        // Test for method java.text.ChoiceFormat(double [], java.lang.String
        // [])
        String formattedString;
        double[] appleLimits = new double[]{ 1, 2, 3, 4, 5 };
        String[] appleFormats = new String[]{ "Tiny Apple", "Small Apple", "Medium Apple", "Large Apple", "Huge Apple" };
        ChoiceFormat cf = new ChoiceFormat(appleLimits, appleFormats);
        formattedString = cf.format(Double.NEGATIVE_INFINITY);
        TestCase.assertTrue(("a) Incorrect format returned: " + formattedString), formattedString.equals("Tiny Apple"));
        formattedString = cf.format(0.5);
        TestCase.assertTrue(("b) Incorrect format returned: " + formattedString), formattedString.equals("Tiny Apple"));
        formattedString = cf.format(1.0);
        TestCase.assertTrue(("c) Incorrect format returned: " + formattedString), formattedString.equals("Tiny Apple"));
        formattedString = cf.format(1.5);
        TestCase.assertTrue(("d) Incorrect format returned: " + formattedString), formattedString.equals("Tiny Apple"));
        formattedString = cf.format(2.0);
        TestCase.assertTrue(("e) Incorrect format returned: " + formattedString), formattedString.equals("Small Apple"));
        formattedString = cf.format(2.5);
        TestCase.assertTrue(("f) Incorrect format returned: " + formattedString), formattedString.equals("Small Apple"));
        formattedString = cf.format(3.0);
        TestCase.assertTrue(("g) Incorrect format returned: " + formattedString), formattedString.equals("Medium Apple"));
        formattedString = cf.format(4.0);
        TestCase.assertTrue(("h) Incorrect format returned: " + formattedString), formattedString.equals("Large Apple"));
        formattedString = cf.format(5.0);
        TestCase.assertTrue(("i) Incorrect format returned: " + formattedString), formattedString.equals("Huge Apple"));
        formattedString = cf.format(5.5);
        TestCase.assertTrue(("j) Incorrect format returned: " + formattedString), formattedString.equals("Huge Apple"));
        formattedString = cf.format(6.0);
        TestCase.assertTrue(("k) Incorrect format returned: " + formattedString), formattedString.equals("Huge Apple"));
        formattedString = cf.format(Double.POSITIVE_INFINITY);
        TestCase.assertTrue(("l) Incorrect format returned: " + formattedString), formattedString.equals("Huge Apple"));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#ChoiceFormat(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.text.ChoiceFormat(java.lang.String)
        String formattedString;
        String patternString = "-2#Inverted Orange| 0#No Orange| 0<Almost No Orange| 1#Normal Orange| 2#Expensive Orange";
        ChoiceFormat cf = new ChoiceFormat(patternString);
        formattedString = cf.format(Double.NEGATIVE_INFINITY);
        TestCase.assertTrue(("a) Incorrect format returned: " + formattedString), formattedString.equals("Inverted Orange"));
        formattedString = cf.format((-3));
        TestCase.assertTrue(("b) Incorrect format returned: " + formattedString), formattedString.equals("Inverted Orange"));
        formattedString = cf.format((-2));
        TestCase.assertTrue(("c) Incorrect format returned: " + formattedString), formattedString.equals("Inverted Orange"));
        formattedString = cf.format((-1));
        TestCase.assertTrue(("d) Incorrect format returned: " + formattedString), formattedString.equals("Inverted Orange"));
        formattedString = cf.format((-0));
        TestCase.assertTrue(("e) Incorrect format returned: " + formattedString), formattedString.equals("No Orange"));
        formattedString = cf.format(0);
        TestCase.assertTrue(("f) Incorrect format returned: " + formattedString), formattedString.equals("No Orange"));
        formattedString = cf.format(0.1);
        TestCase.assertTrue(("g) Incorrect format returned: " + formattedString), formattedString.equals("Almost No Orange"));
        formattedString = cf.format(1);
        TestCase.assertTrue(("h) Incorrect format returned: " + formattedString), formattedString.equals("Normal Orange"));
        formattedString = cf.format(1.5);
        TestCase.assertTrue(("i) Incorrect format returned: " + formattedString), formattedString.equals("Normal Orange"));
        formattedString = cf.format(2);
        TestCase.assertTrue(("j) Incorrect format returned: " + formattedString), formattedString.equals("Expensive Orange"));
        formattedString = cf.format(3);
        TestCase.assertTrue(("k) Incorrect format returned: " + formattedString), formattedString.equals("Expensive Orange"));
        formattedString = cf.format(Double.POSITIVE_INFINITY);
        TestCase.assertTrue(("l) Incorrect format returned: " + formattedString), formattedString.equals("Expensive Orange"));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#applyPattern(java.lang.String)
     */
    public void test_applyPatternLjava_lang_String() {
        // Test for method void
        // java.text.ChoiceFormat.applyPattern(java.lang.String)
        ChoiceFormat f = ((ChoiceFormat) (f1.clone()));
        f.applyPattern("0#0|1#1");
        TestCase.assertTrue("Incorrect limits", Arrays.equals(f.getLimits(), new double[]{ 0, 1 }));
        TestCase.assertTrue("Incorrect formats", Arrays.equals(f.getFormats(), new String[]{ "0", "1" }));
        // Regression for Harmony 540
        double[] choiceLimits = new double[]{ -1, 0, 1, ChoiceFormat.nextDouble(1) };
        String[] choiceFormats = new String[]{ "is negative", "is zero or fraction", "is one", "is more than 1" };
        f = new ChoiceFormat("");
        f.applyPattern("-1#is negative|0#is zero or fraction|1#is one|1<is more than 1");
        TestCase.assertTrue("Incorrect limits", Arrays.equals(f.getLimits(), choiceLimits));
        TestCase.assertTrue("Incorrect formats", Arrays.equals(f.getFormats(), choiceFormats));
        f = new ChoiceFormat("");
        try {
            f.applyPattern("-1#is negative|0#is zero or fraction|-1#is one|1<is more than 1");
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        f = new ChoiceFormat("");
        try {
            f.applyPattern("-1is negative|0#is zero or fraction|1#is one|1<is more than 1");
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        f = new ChoiceFormat("");
        f.applyPattern("-1<is negative|0#is zero or fraction|1#is one|1<is more than 1");
        choiceLimits[0] = ChoiceFormat.nextDouble((-1));
        TestCase.assertTrue("Incorrect limits", Arrays.equals(f.getLimits(), choiceLimits));
        TestCase.assertTrue("Incorrect formats", Arrays.equals(f.getFormats(), choiceFormats));
        f = new ChoiceFormat("");
        f.applyPattern("-1#is negative|0#is zero or fraction|1#is one|1<is more than 1");
        String str = "org.apache.harmony.tests.java.text.ChoiceFormat";
        f.applyPattern(str);
        String ptrn = f.toPattern();
        TestCase.assertEquals("Return value should be empty string for invalid pattern", 0, ptrn.length());
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.text.ChoiceFormat.clone()
        ChoiceFormat f = ((ChoiceFormat) (f1.clone()));
        TestCase.assertTrue("Not equal", f.equals(f1));
        f.setChoices(new double[]{ 0, 1, 2 }, new String[]{ "0", "1", "2" });
        TestCase.assertTrue("Equal", (!(f.equals(f1))));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean
        // java.text.ChoiceFormat.equals(java.lang.Object)
        String patternString = "-2#Inverted Orange| 0#No Orange| 0<Almost No Orange| 1#Normal Orange| 2#Expensive Orange";
        double[] appleLimits = new double[]{ 1, 2, 3, 4, 5 };
        String[] appleFormats = new String[]{ "Tiny Apple", "Small Apple", "Medium Apple", "Large Apple", "Huge Apple" };
        double[] orangeLimits = new double[]{ -2, 0, ChoiceFormat.nextDouble(0), 1, 2 };
        String[] orangeFormats = new String[]{ "Inverted Orange", "No Orange", "Almost No Orange", "Normal Orange", "Expensive Orange" };
        ChoiceFormat appleChoiceFormat = new ChoiceFormat(appleLimits, appleFormats);
        ChoiceFormat orangeChoiceFormat = new ChoiceFormat(orangeLimits, orangeFormats);
        ChoiceFormat orangeChoiceFormat2 = new ChoiceFormat(patternString);
        ChoiceFormat hybridChoiceFormat = new ChoiceFormat(appleLimits, orangeFormats);
        TestCase.assertTrue("Apples should not equal oranges", (!(appleChoiceFormat.equals(orangeChoiceFormat))));
        TestCase.assertTrue("Different limit list--should not appear as equal", (!(orangeChoiceFormat.equals(hybridChoiceFormat))));
        TestCase.assertTrue("Different format list--should not appear as equal", (!(appleChoiceFormat.equals(hybridChoiceFormat))));
        TestCase.assertTrue("Should be equal--identical format", appleChoiceFormat.equals(appleChoiceFormat));
        TestCase.assertTrue("Should be equals--same limits, same formats", orangeChoiceFormat.equals(orangeChoiceFormat2));
        ChoiceFormat f2 = new ChoiceFormat("0#Less than one|1#one|1<Between one and two|2<Greater than two");
        TestCase.assertTrue("Not equal", f1.equals(f2));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#format(double, java.lang.StringBuffer,
    java.text.FieldPosition)
     */
    public void test_formatDLjava_lang_StringBufferLjava_text_FieldPosition() {
        // Test for method java.lang.StringBuffer
        // java.text.ChoiceFormat.format(double, java.lang.StringBuffer,
        // java.text.FieldPosition)
        FieldPosition field = new FieldPosition(0);
        StringBuffer buf = new StringBuffer();
        String r = f1.format((-1), buf, field).toString();
        TestCase.assertEquals("Wrong choice for -1", "Less than one", r);
        buf.setLength(0);
        r = f1.format(0, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 0", "Less than one", r);
        buf.setLength(0);
        r = f1.format(1, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 1", "one", r);
        buf.setLength(0);
        r = f1.format(2, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 2", "Between one and two", r);
        buf.setLength(0);
        r = f1.format(3, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 3", "Greater than two", r);
        // Regression test for HARMONY-1081
        TestCase.assertEquals(0, new ChoiceFormat("|").format(Double.NaN, new StringBuffer(), new FieldPosition(6)).length());
        TestCase.assertEquals(0, new ChoiceFormat("|").format(1, new StringBuffer(), new FieldPosition(6)).length());
        TestCase.assertEquals("Less than one", f1.format(Double.NaN, new StringBuffer(), field).toString());
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#format(long, java.lang.StringBuffer,
    java.text.FieldPosition)
     */
    public void test_formatJLjava_lang_StringBufferLjava_text_FieldPosition() {
        // Test for method java.lang.StringBuffer
        // java.text.ChoiceFormat.format(long, java.lang.StringBuffer,
        // java.text.FieldPosition)
        FieldPosition field = new FieldPosition(0);
        StringBuffer buf = new StringBuffer();
        String r = f1.format(0.5, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 0.5", "Less than one", r);
        buf.setLength(0);
        r = f1.format(1.5, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 1.5", "Between one and two", r);
        buf.setLength(0);
        r = f1.format(2.5, buf, field).toString();
        TestCase.assertEquals("Wrong choice for 2.5", "Greater than two", r);
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#getFormats()
     */
    public void test_getFormats() {
        // Test for method java.lang.Object []
        // java.text.ChoiceFormat.getFormats()
        String[] orgFormats = ((String[]) (formats.clone()));
        String[] f = ((String[]) (f1.getFormats()));
        TestCase.assertTrue("Wrong formats", f.equals(formats));
        f[0] = "Modified";
        TestCase.assertTrue("Formats copied", (!(f.equals(orgFormats))));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#getLimits()
     */
    public void test_getLimits() {
        // Test for method double [] java.text.ChoiceFormat.getLimits()
        double[] orgLimits = ((double[]) (limits.clone()));
        double[] l = f1.getLimits();
        TestCase.assertTrue("Wrong limits", l.equals(limits));
        l[0] = 3.14527;
        TestCase.assertTrue("Limits copied", (!(l.equals(orgLimits))));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#hashCode()
     */
    public void test_hashCode() {
        // Test for method int java.text.ChoiceFormat.hashCode()
        ChoiceFormat f2 = new ChoiceFormat("0#Less than one|1#one|1<Between one and two|2<Greater than two");
        TestCase.assertTrue("Different hash", ((f1.hashCode()) == (f2.hashCode())));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#nextDouble(double)
     */
    public void test_nextDoubleD() {
        // Test for method double java.text.ChoiceFormat.nextDouble(double)
        TestCase.assertTrue("Not greater 5", ((ChoiceFormat.nextDouble(5)) > 5));
        TestCase.assertTrue("Not greater 0", ((ChoiceFormat.nextDouble(0)) > 0));
        TestCase.assertTrue("Not greater -5", ((ChoiceFormat.nextDouble((-5))) > (-5)));
        TestCase.assertTrue("Not NaN", Double.isNaN(ChoiceFormat.nextDouble(Double.NaN)));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#nextDouble(double, boolean)
     */
    public void test_nextDoubleDZ() {
        // Test for method double java.text.ChoiceFormat.nextDouble(double,
        // boolean)
        TestCase.assertTrue("Not greater 0", ((ChoiceFormat.nextDouble(0, true)) > 0));
        TestCase.assertTrue("Not less 0", ((ChoiceFormat.nextDouble(0, false)) < 0));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#parse(java.lang.String,
    java.text.ParsePosition)
     */
    public void test_parseLjava_lang_StringLjava_text_ParsePosition() {
        // Test for method java.lang.Number
        // java.text.ChoiceFormat.parse(java.lang.String,
        // java.text.ParsePosition)
        ChoiceFormat format = new ChoiceFormat("1#one|2#two|3#three");
        TestCase.assertEquals("Case insensitive", 0, format.parse("One", new ParsePosition(0)).intValue());
        ParsePosition pos = new ParsePosition(0);
        Number result = f1.parse("Greater than two", pos);
        TestCase.assertTrue("Not a Double1", (result instanceof Double));
        TestCase.assertTrue("Wrong value ~>2", ((result.doubleValue()) == (ChoiceFormat.nextDouble(2))));
        TestCase.assertEquals("Wrong position ~16", 16, pos.getIndex());
        pos = new ParsePosition(0);
        TestCase.assertTrue("Incorrect result", Double.isNaN(f1.parse("12one", pos).doubleValue()));
        TestCase.assertEquals("Wrong position ~0", 0, pos.getIndex());
        pos = new ParsePosition(2);
        result = f1.parse("12one and two", pos);
        TestCase.assertTrue("Not a Double2", (result instanceof Double));
        TestCase.assertEquals("Ignored parse position", 1.0, result.doubleValue(), 0.0);
        TestCase.assertEquals("Wrong position ~5", 5, pos.getIndex());
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#previousDouble(double)
     */
    public void test_previousDoubleD() {
        // Test for method double java.text.ChoiceFormat.previousDouble(double)
        TestCase.assertTrue("Not less 5", ((ChoiceFormat.previousDouble(5)) < 5));
        TestCase.assertTrue("Not less 0", ((ChoiceFormat.previousDouble(0)) < 0));
        TestCase.assertTrue("Not less -5", ((ChoiceFormat.previousDouble((-5))) < (-5)));
        TestCase.assertTrue("Not NaN", Double.isNaN(ChoiceFormat.previousDouble(Double.NaN)));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#setChoices(double[], java.lang.String[])
     */
    public void test_setChoices$D$Ljava_lang_String() {
        // Test for method void java.text.ChoiceFormat.setChoices(double [],
        // java.lang.String [])
        ChoiceFormat f = ((ChoiceFormat) (f1.clone()));
        double[] l = new double[]{ 0, 1 };
        String[] fs = new String[]{ "0", "1" };
        f.setChoices(l, fs);
        TestCase.assertTrue("Limits copied", ((f.getLimits()) == l));
        TestCase.assertTrue("Formats copied", ((f.getFormats()) == fs));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#toPattern()
     */
    public void test_toPattern() {
        // Regression for HARMONY-59
        ChoiceFormat cf = new ChoiceFormat("");
        TestCase.assertEquals("", cf.toPattern());
        cf = new ChoiceFormat("-1#NEGATIVE_ONE|0#ZERO|1#ONE|1<GREATER_THAN_ONE");
        TestCase.assertEquals("-1.0#NEGATIVE_ONE|0.0#ZERO|1.0#ONE|1.0<GREATER_THAN_ONE", cf.toPattern());
        MessageFormat mf = new MessageFormat("CHOICE {1,choice}");
        String ptrn = mf.toPattern();
        TestCase.assertEquals("Unused message format returning incorrect pattern", "CHOICE {1,choice,}", ptrn);
        String pattern = f1.toPattern();
        TestCase.assertTrue(("Wrong pattern: " + pattern), pattern.equals("0.0#Less than one|1.0#one|1.0<Between one and two|2.0<Greater than two"));
        cf = new ChoiceFormat("-1#is negative| 0#is zero or fraction | 1#is one |1.0<is 1+|2#is two |2<is more than 2.");
        String str = "org.apache.harmony.tests.java.lang.share.MyResources2";
        cf.applyPattern(str);
        ptrn = cf.toPattern();
        TestCase.assertEquals("Return value should be empty string for invalid pattern", 0, ptrn.length());
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#format(long)
     */
    public void test_formatL() {
        ChoiceFormat fmt = new ChoiceFormat("-1#NEGATIVE_ONE|0#ZERO|1#ONE|1<GREATER_THAN_ONE");
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format(Long.MIN_VALUE));
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format((-1)));
        TestCase.assertEquals("ZERO", fmt.format(0));
        TestCase.assertEquals("ONE", fmt.format(1));
        TestCase.assertEquals("GREATER_THAN_ONE", fmt.format(Long.MAX_VALUE));
    }

    /**
     *
     *
     * @unknown java.text.ChoiceFormat#format(double)
     */
    public void test_formatD() {
        ChoiceFormat fmt = new ChoiceFormat("-1#NEGATIVE_ONE|0#ZERO|1#ONE|1<GREATER_THAN_ONE");
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format(Double.NEGATIVE_INFINITY));
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format((-9.99999999E8)));
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format((-1.1)));
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format((-1.0)));
        TestCase.assertEquals("NEGATIVE_ONE", fmt.format((-0.9)));
        TestCase.assertEquals("ZERO", fmt.format(0.0));
        TestCase.assertEquals("ZERO", fmt.format(0.9));
        TestCase.assertEquals("ONE", fmt.format(1.0));
        TestCase.assertEquals("GREATER_THAN_ONE", fmt.format(1.1));
        TestCase.assertEquals("GREATER_THAN_ONE", fmt.format(9.99999999E8));
        TestCase.assertEquals("GREATER_THAN_ONE", fmt.format(Double.POSITIVE_INFINITY));
    }

    // http://b/19149384
    public void testToPatternWithInfinities() {
        final ChoiceFormat fmt = new ChoiceFormat("-\u221e<are negative|0<are fractions|1#is one|1.0<is 1+|\u221e<are many.");
        TestCase.assertEquals("-\u221e<are negative|0.0<are fractions|1.0#is one|1.0<is 1+|\u221e<are many.", fmt.toPattern());
    }

    // http://b/19011159
    public void testEscapedPatternWithConsecutiveQuotes() {
        ChoiceFormat format = new ChoiceFormat("0#1'2''3'''4''''.");
        String formatted = format.format(0);
        TestCase.assertEquals("12'3'4''.", formatted);
        format = new ChoiceFormat("0#1'2''3'''''4''''.");
        formatted = format.format(0);
        TestCase.assertEquals("12'3''4''.", formatted);
    }
}

