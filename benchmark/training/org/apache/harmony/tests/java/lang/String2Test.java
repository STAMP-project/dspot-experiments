/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import junit.framework.TestCase;


public class String2Test extends TestCase {
    String hw1 = "HelloWorld";

    String hw2 = "HelloWorld";

    String hwlc = "helloworld";

    String hwuc = "HELLOWORLD";

    String hello1 = "Hello";

    String world1 = "World";

    String comp11 = "Test String";

    Object obj = new Object();

    char[] buf = new char[]{ 'W', 'o', 'r', 'l', 'd' };

    char[] rbuf = new char[5];

    /**
     * java.lang.String#String()
     */
    public void test_Constructor() {
        // Test for method java.lang.String()
        TestCase.assertTrue("Created incorrect string", new String().equals(""));
    }

    /**
     * java.lang.String#String(byte[])
     */
    public void test_Constructor$B() {
        // Test for method java.lang.String(byte [])
        TestCase.assertTrue("Failed to create string", new String(hw1.getBytes()).equals(hw1));
    }

    /**
     * java.lang.String#String(byte[], int, int)
     */
    public void test_Constructor$BII() {
        // Test for method java.lang.String(byte [], int, int)
        TestCase.assertTrue("Failed to create string", new String(hw1.getBytes(), 0, hw1.getBytes().length).equals(hw1));
        boolean exception = false;
        try {
            new String(new byte[0], 0, Integer.MAX_VALUE);
        } catch (IndexOutOfBoundsException e) {
            exception = true;
        }
        TestCase.assertTrue("Did not throw exception", exception);
    }

    /**
     * java.lang.String#String(byte[], int, int, java.lang.String)
     */
    public void test_Constructor$BIILjava_lang_String() throws Exception {
        // Test for method java.lang.String(byte [], int, int, java.lang.String)
        String s = null;
        s = new String(new byte[]{ 65, 66, 67, 68, 69 }, 0, 5, "8859_1");
        TestCase.assertTrue(("Incorrect string returned: " + s), s.equals("ABCDE"));
        // Regression for HARMONY-1111
        TestCase.assertNotNull(new String(new byte[]{ ((byte) (192)) }, 0, 1, "UTF-8"));
    }

    /**
     * java.lang.String#String(byte[], java.lang.String)
     */
    public void test_Constructor$BLjava_lang_String() throws Exception {
        // Test for method java.lang.String(byte [], java.lang.String)
        String s = null;
        s = new String(new byte[]{ 65, 66, 67, 68, 69 }, "8859_1");
        TestCase.assertTrue(("Incorrect string returned: " + s), s.equals("ABCDE"));
    }

    /**
     * java.lang.String#String(char[])
     */
    public void test_Constructor$C() {
        // Test for method java.lang.String(char [])
        TestCase.assertEquals("Failed Constructor test", "World", new String(buf));
    }

    /**
     * java.lang.String#String(char[], int, int)
     */
    public void test_Constructor$CII() {
        // Test for method java.lang.String(char [], int, int)
        char[] buf = new char[]{ 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };
        String s = new String(buf, 0, buf.length);
        TestCase.assertTrue("Incorrect string created", hw1.equals(s));
        boolean exception = false;
        try {
            new String(new char[0], 0, Integer.MAX_VALUE);
        } catch (IndexOutOfBoundsException e) {
            exception = true;
        }
        TestCase.assertTrue("Did not throw exception", exception);
    }

    /**
     * java.lang.String#String(int[], int, int)
     */
    public void test_Constructor$III() {
        // Test for method java.lang.String(int [], int, int)
        try {
            new String(new int[0], 2, Integer.MAX_VALUE);
            TestCase.fail("Did not throw exception");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.String#String(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.lang.String(java.lang.String)
        String s = new String("Hello World");
        TestCase.assertEquals("Failed to construct correct string", "Hello World", s);
    }

    /**
     * java.lang.String#String(java.lang.StringBuffer)
     */
    public void test_ConstructorLjava_lang_StringBuffer() {
        // Test for method java.lang.String(java.lang.StringBuffer)
        StringBuffer sb = new StringBuffer();
        sb.append("HelloWorld");
        TestCase.assertEquals("Created incorrect string", "HelloWorld", new String(sb));
    }

    /**
     * java.lang.String#charAt(int)
     */
    public void test_charAtI() {
        // Test for method char java.lang.String.charAt(int)
        TestCase.assertTrue("Incorrect character returned", (((hw1.charAt(5)) == 'W') && ((hw1.charAt(1)) != 'Z')));
    }

    /**
     * java.lang.String#compareTo(java.lang.String)
     */
    public void test_compareToLjava_lang_String() {
        // Test for method int java.lang.String.compareTo(java.lang.String)
        TestCase.assertTrue("Returned incorrect value for first < second", (("aaaaab".compareTo("aaaaac")) < 0));
        TestCase.assertEquals("Returned incorrect value for first = second", 0, "aaaaac".compareTo("aaaaac"));
        TestCase.assertTrue("Returned incorrect value for first > second", (("aaaaac".compareTo("aaaaab")) > 0));
        TestCase.assertTrue("Considered case to not be of importance", (!(("A".compareTo("a")) == 0)));
        try {
            "fixture".compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.String#compareToIgnoreCase(java.lang.String)
     */
    public void test_compareToIgnoreCaseLjava_lang_String() {
        // Test for method int
        // java.lang.String.compareToIgnoreCase(java.lang.String)
        TestCase.assertTrue("Returned incorrect value for first < second", (("aaaaab".compareToIgnoreCase("aaaaac")) < 0));
        TestCase.assertEquals("Returned incorrect value for first = second", 0, "aaaaac".compareToIgnoreCase("aaaaac"));
        TestCase.assertTrue("Returned incorrect value for first > second", (("aaaaac".compareToIgnoreCase("aaaaab")) > 0));
        TestCase.assertEquals("Considered case to not be of importance", 0, "A".compareToIgnoreCase("a"));
        try {
            "fixture".compareToIgnoreCase(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.String#concat(java.lang.String)
     */
    public void test_concatLjava_lang_String() {
        // Test for method java.lang.String
        // java.lang.String.concat(java.lang.String)
        TestCase.assertTrue("Concatenation failed to produce correct string", hello1.concat(world1).equals(hw1));
        boolean exception = false;
        try {
            String a = new String("test");
            String b = null;
            a.concat(b);
        } catch (NullPointerException e) {
            exception = true;
        }
        TestCase.assertTrue("Concatenation failed to throw NP exception (1)", exception);
        exception = false;
        try {
            String a = new String("");
            String b = null;
            a.concat(b);
        } catch (NullPointerException e) {
            exception = true;
        }
        TestCase.assertTrue("Concatenation failed to throw NP exception (2)", exception);
        String s1 = "";
        String s2 = "s2";
        String s3 = s1.concat(s2);
        TestCase.assertEquals(s2, s3);
        // The RI returns a new string even when it's the same as the argument string.
        // assertNotSame(s2, s3);
        s3 = s2.concat(s1);
        TestCase.assertEquals(s2, s3);
        // Neither Android nor the RI returns a new string when it's the same as *this*.
        // assertNotSame(s2, s3);
        s3 = s2.concat(s1);
        TestCase.assertSame(s2, s3);
    }

    /**
     * java.lang.String#copyValueOf(char[])
     */
    public void test_copyValueOf$C() {
        // Test for method java.lang.String java.lang.String.copyValueOf(char
        // [])
        char[] t = new char[]{ 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };
        TestCase.assertEquals("copyValueOf returned incorrect String", "HelloWorld", String.copyValueOf(t));
    }

    /**
     * java.lang.String#copyValueOf(char[], int, int)
     */
    public void test_copyValueOf$CII() {
        // Test for method java.lang.String java.lang.String.copyValueOf(char
        // [], int, int)
        char[] t = new char[]{ 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };
        TestCase.assertEquals("copyValueOf returned incorrect String", "World", String.copyValueOf(t, 5, 5));
    }

    /**
     * java.lang.String#endsWith(java.lang.String)
     */
    public void test_endsWithLjava_lang_String() {
        // Test for method boolean java.lang.String.endsWith(java.lang.String)
        TestCase.assertTrue("Failed to fine ending string", hw1.endsWith("ld"));
    }

    /**
     * java.lang.String#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertEquals("String not equal", hw1, hw2);
        TestCase.assertEquals("Empty string equals check", "", "");
        TestCase.assertEquals("Null string equals check", ((String) (null)), ((String) (null)));
        TestCase.assertFalse("Unequal strings reports as equal", hw1.equals(comp11));
        TestCase.assertFalse("Null string comparison failed", hw1.equals(((String) (null))));
    }

    /**
     * java.lang.String#equalsIgnoreCase(java.lang.String)
     */
    public void test_equalsIgnoreCaseLjava_lang_String() {
        // Test for method boolean
        // java.lang.String.equalsIgnoreCase(java.lang.String)
        TestCase.assertTrue("lc version returned unequal to uc", hwlc.equalsIgnoreCase(hwuc));
    }

    /**
     * java.lang.String#getBytes()
     */
    public void test_getBytes() {
        // Test for method byte [] java.lang.String.getBytes()
        byte[] sbytes = hw1.getBytes();
        for (int i = 0; i < (hw1.length()); i++)
            TestCase.assertTrue("Returned incorrect bytes", ((sbytes[i]) == ((byte) (hw1.charAt(i)))));

        byte[] bytes = new byte[1];
        for (int i = 0; i < 256; i++) {
            bytes[0] = ((byte) (i));
            String result = null;
            try {
                result = new String(bytes, "8859_1");
                TestCase.assertEquals("Wrong char length", 1, result.length());
                TestCase.assertTrue("Wrong char value", ((result.charAt(0)) == ((char) (i))));
            } catch (UnsupportedEncodingException e) {
            }
        }
    }

    /**
     * java.lang.String#getBytes(java.lang.String)
     */
    public void test_getBytesLjava_lang_String() throws Exception {
        // Test for method byte [] java.lang.String.getBytes(java.lang.String)
        byte[] buf = "Hello World".getBytes();
        TestCase.assertEquals("Returned incorrect bytes", "Hello World", new String(buf));
        try {
            "string".getBytes("8849_1");
            TestCase.fail("No UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e) {
        }
        byte[] bytes = "\u3048".getBytes("UTF-8");
        byte[] expected = new byte[]{ ((byte) (227)), ((byte) (129)), ((byte) (136)) };
        TestCase.assertEquals(expected[0], bytes[0]);
        TestCase.assertEquals(expected[1], bytes[1]);
        TestCase.assertEquals(expected[2], bytes[2]);
        // Regression for HARMONY-663
        try {
            "string".getBytes("?Q?D??_??_6ffa?+vG?_??\u951f\ufffd??\u0015");
            TestCase.fail("No UnsupportedEncodingException");
        } catch (UnsupportedEncodingException e) {
            // expected
        }
    }

    /* java.lang.String#getBytes() */
    public void test_getBytes_NPE() throws Exception {
        try {
            "abc".getBytes(((String) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (UnsupportedEncodingException whatTheRiDocumentsAndWeThrow) {
        } catch (NullPointerException whatTheRiActuallyThrows) {
        }
        try {
            "Hello World".getBytes(1, 2, null, 1);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.String#getChars(int, int, char[], int)
     */
    public void test_getCharsII$CI() {
        // Test for method void java.lang.String.getChars(int, int, char [],
        // int)
        hw1.getChars(5, hw1.length(), rbuf, 0);
        for (int i = 0; i < (rbuf.length); i++)
            TestCase.assertTrue("getChars returned incorrect char(s)", ((rbuf[i]) == (buf[i])));

    }

    /**
     *
     *
     * @unknown java.lang.String#indexOf(int)
     */
    public void test_indexOfI() {
        // Test for method int java.lang.String.indexOf(int)
        TestCase.assertEquals("Invalid index returned", 1, hw1.indexOf('e'));
    }

    /**
     * java.lang.String#indexOf(int, int)
     */
    public void test_indexOfII() {
        // Test for method int java.lang.String.indexOf(int, int)
        TestCase.assertEquals("Invalid character index returned", 5, hw1.indexOf('W', 2));
    }

    /**
     * java.lang.String#indexOf(java.lang.String)
     */
    public void test_indexOfLjava_lang_String() {
        // Test for method int java.lang.String.indexOf(java.lang.String)
        TestCase.assertTrue("Failed to find string", ((hw1.indexOf("World")) > 0));
        TestCase.assertTrue("Failed to find string", (!((hw1.indexOf("ZZ")) > 0)));
    }

    /**
     * java.lang.String#indexOf(java.lang.String, int)
     */
    public void test_indexOfLjava_lang_StringI() {
        // Test for method int java.lang.String.indexOf(java.lang.String, int)
        TestCase.assertTrue("Failed to find string", ((hw1.indexOf("World", 0)) > 0));
        TestCase.assertTrue("Found string outside index", (!((hw1.indexOf("Hello", 6)) > 0)));
        TestCase.assertEquals("Did not accept valid negative starting position", 0, hello1.indexOf("", (-5)));
    }

    /**
     * java.lang.String#intern()
     */
    public void test_intern() {
        // Test for method java.lang.String java.lang.String.intern()
        TestCase.assertTrue("Intern returned incorrect result", ((hw1.intern()) == (hw2.intern())));
    }

    /**
     * java.lang.String#lastIndexOf(int)
     */
    public void test_lastIndexOfI() {
        // Test for method int java.lang.String.lastIndexOf(int)
        TestCase.assertEquals("Failed to return correct index", 5, hw1.lastIndexOf('W'));
        TestCase.assertEquals("Returned index for non-existent char", (-1), hw1.lastIndexOf('Z'));
    }

    /**
     * java.lang.String#lastIndexOf(int, int)
     */
    public void test_lastIndexOfII() {
        // Test for method int java.lang.String.lastIndexOf(int, int)
        TestCase.assertEquals("Failed to return correct index", 5, hw1.lastIndexOf('W', 6));
        TestCase.assertEquals("Returned index for char out of specified range", (-1), hw1.lastIndexOf('W', 4));
        TestCase.assertEquals("Returned index for non-existent char", (-1), hw1.lastIndexOf('Z', 9));
    }

    /**
     * java.lang.String#lastIndexOf(java.lang.String)
     */
    public void test_lastIndexOfLjava_lang_String() {
        // Test for method int java.lang.String.lastIndexOf(java.lang.String)
        TestCase.assertEquals("Returned incorrect index", 5, hw1.lastIndexOf("World"));
        TestCase.assertEquals("Found String outside of index", (-1), hw1.lastIndexOf("HeKKKKKKKK"));
    }

    /**
     * java.lang.String#lastIndexOf(java.lang.String, int)
     */
    public void test_lastIndexOfLjava_lang_StringI() {
        // Test for method int java.lang.String.lastIndexOf(java.lang.String,
        // int)
        TestCase.assertEquals("Returned incorrect index", 5, hw1.lastIndexOf("World", 9));
        int result = hw1.lastIndexOf("Hello", 2);
        TestCase.assertTrue(("Found String outside of index: " + result), (result == 0));
        TestCase.assertEquals("Reported wrong error code", (-1), hello1.lastIndexOf("", (-5)));
        TestCase.assertEquals("Did not accept valid large starting position", 5, hello1.lastIndexOf("", 5));
    }

    /**
     * java.lang.String#length()
     */
    public void test_length() {
        // Test for method int java.lang.String.length()
        TestCase.assertEquals("Invalid length returned", 11, comp11.length());
    }

    /**
     * java.lang.String#regionMatches(int, java.lang.String, int, int)
     */
    public void test_regionMatchesILjava_lang_StringII() {
        // Test for method boolean java.lang.String.regionMatches(int,
        // java.lang.String, int, int)
        String bogusString = "xxcedkedkleiorem lvvwr e''' 3r3r 23r";
        TestCase.assertTrue("identical regions failed comparison", hw1.regionMatches(2, hw2, 2, 5));
        TestCase.assertTrue("Different regions returned true", (!(hw1.regionMatches(2, bogusString, 2, 5))));
    }

    /**
     * java.lang.String#regionMatches(boolean, int, java.lang.String,
     * int, int)
     */
    public void test_regionMatchesZILjava_lang_StringII() {
        // Test for method boolean java.lang.String.regionMatches(boolean, int,
        // java.lang.String, int, int)
        String bogusString = "xxcedkedkleiorem lvvwr e''' 3r3r 23r";
        TestCase.assertTrue("identical regions failed comparison", hw1.regionMatches(false, 2, hw2, 2, 5));
        TestCase.assertTrue("identical regions failed comparison with different cases", hw1.regionMatches(true, 2, hw2, 2, 5));
        TestCase.assertTrue("Different regions returned true", (!(hw1.regionMatches(true, 2, bogusString, 2, 5))));
        TestCase.assertTrue("identical regions failed comparison with different cases", hw1.regionMatches(false, 2, hw2, 2, 5));
    }

    /**
     * java.lang.String#replace(char, char)
     */
    public void test_replaceCC() {
        // Test for method java.lang.String java.lang.String.replace(char, char)
        TestCase.assertEquals("Failed replace", "HezzoWorzd", hw1.replace('l', 'z'));
    }

    /**
     * java.lang.String#replace(CharSequence, CharSequence)
     */
    public void test_replaceLjava_langCharSequenceLjava_langCharSequence() {
        TestCase.assertEquals("Failed replace", "aaccdd", "aabbdd".replace(new StringBuffer("bb"), "cc"));
        TestCase.assertEquals("Failed replace by bigger seq", "cccbccc", "aba".replace("a", "ccc"));
        TestCase.assertEquals("Failed replace by smaller seq", "$bba^", "$aaaaa^".replace(new StringBuilder("aa"), "b"));
        TestCase.assertEquals("Failed to replace with empty string", "aacc", "aabbcc".replace("b", ""));
    }

    /**
     * java.lang.String#startsWith(java.lang.String)
     */
    public void test_startsWithLjava_lang_String() {
        // Test for method boolean java.lang.String.startsWith(java.lang.String)
        TestCase.assertTrue("Failed to find string", hw1.startsWith("Hello"));
        TestCase.assertTrue("Found incorrect string", (!(hw1.startsWith("T"))));
    }

    /**
     * java.lang.String#startsWith(java.lang.String, int)
     */
    public void test_startsWithLjava_lang_StringI() {
        // Test for method boolean java.lang.String.startsWith(java.lang.String,
        // int)
        TestCase.assertTrue("Failed to find string", hw1.startsWith("World", 5));
        TestCase.assertTrue("Found incorrect string", (!(hw1.startsWith("Hello", 5))));
    }

    /**
     * java.lang.String#substring(int)
     */
    public void test_substringI() {
        // Test for method java.lang.String java.lang.String.substring(int)
        TestCase.assertEquals("Incorrect substring returned", "World", hw1.substring(5));
        TestCase.assertTrue("not identical", ((hw1.substring(0)) == (hw1)));
    }

    /**
     * java.lang.String#substring(int, int)
     */
    public void test_substringII() {
        // Test for method java.lang.String java.lang.String.substring(int, int)
        TestCase.assertTrue("Incorrect substring returned", ((hw1.substring(0, 5).equals("Hello")) && (hw1.substring(5, 10).equals("World"))));
        TestCase.assertTrue("not identical", ((hw1.substring(0, hw1.length())) == (hw1)));
    }

    /**
     * java.lang.String#substring(int, int)
     */
    public void test_substringErrorMessage() {
        try {
            hw1.substring((-1), 1);
        } catch (StringIndexOutOfBoundsException ex) {
            String msg = ex.getMessage();
            TestCase.assertTrue(("Expected message to contain -1: " + msg), ((msg.indexOf("-1")) != (-1)));
        }
        try {
            hw1.substring(4, 1);
        } catch (StringIndexOutOfBoundsException ex) {
            String msg = ex.getMessage();
            TestCase.assertTrue(("Expected message to contain -3: " + msg), ((msg.indexOf("-3")) != (-1)));
        }
        try {
            hw1.substring(0, 100);
        } catch (StringIndexOutOfBoundsException ex) {
            String msg = ex.getMessage();
            TestCase.assertTrue(("Expected message to contain 100: " + msg), ((msg.indexOf("100")) != (-1)));
        }
    }

    /**
     * java.lang.String#toCharArray()
     */
    public void test_toCharArray() {
        // Test for method char [] java.lang.String.toCharArray()
        String s = new String(buf, 0, buf.length);
        char[] schars = s.toCharArray();
        for (int i = 0; i < (s.length()); i++)
            TestCase.assertTrue("Returned incorrect char aray", ((buf[i]) == (schars[i])));

    }

    /**
     * java.lang.String#toLowerCase()
     */
    public void test_toLowerCase() {
        // Test for method java.lang.String java.lang.String.toLowerCase()
        TestCase.assertTrue("toLowerCase case conversion did not succeed", hwuc.toLowerCase().equals(hwlc));
        TestCase.assertEquals("a) Sigma has ordinary lower case value when isolated with Unicode 4.0", "\u03c3", "\u03a3".toLowerCase());
        TestCase.assertEquals("b) Sigma has final form lower case value at end of word with Unicode 4.0", "a\u03c2", "a\u03a3".toLowerCase());
        TestCase.assertEquals("toLowerCase case conversion did not succeed", "\ud801\udc44", "\ud801\udc1c".toLowerCase());
    }

    /**
     * java.lang.String#toLowerCase(java.util.Locale)
     */
    public void test_toLowerCaseLjava_util_Locale() {
        // Test for method java.lang.String
        // java.lang.String.toLowerCase(java.util.Locale)
        TestCase.assertTrue("toLowerCase case conversion did not succeed", hwuc.toLowerCase(Locale.getDefault()).equals(hwlc));
        TestCase.assertEquals("Invalid \\u0049 for English", "i", "I".toLowerCase(Locale.ENGLISH));
        TestCase.assertEquals("Invalid \\u0049 for Turkish", "\u0131", "I".toLowerCase(new Locale("tr", "")));
    }

    /**
     * java.lang.String#toString()
     */
    public void test_toString() {
        // Test for method java.lang.String java.lang.String.toString()
        TestCase.assertTrue("Incorrect string returned", hw1.toString().equals(hw1));
    }

    /**
     * java.lang.String#toUpperCase()
     */
    public void test_toUpperCase() {
        // Test for method java.lang.String java.lang.String.toUpperCase()
        TestCase.assertTrue("Returned string is not UpperCase", hwlc.toUpperCase().equals(hwuc));
        TestCase.assertEquals("Wrong conversion", "SS", "\u00df".toUpperCase());
        String s = "a\u00df\u1f56";
        TestCase.assertTrue("Invalid conversion", (!(s.toUpperCase().equals(s))));
        TestCase.assertEquals("toUpperCase case conversion did not succeed", "\ud801\udc1c", "\ud801\udc44".toUpperCase());
    }

    /**
     * java.lang.String#toUpperCase(java.util.Locale)
     */
    public void test_toUpperCaseLjava_util_Locale() {
        // Test for method java.lang.String
        // java.lang.String.toUpperCase(java.util.Locale)
        TestCase.assertTrue("Returned string is not UpperCase", hwlc.toUpperCase().equals(hwuc));
        TestCase.assertEquals("Invalid \\u0069 for English", "I", "i".toUpperCase(Locale.ENGLISH));
        TestCase.assertEquals("Invalid \\u0069 for Turkish", "\u0130", "i".toUpperCase(new Locale("tr", "")));
    }

    /**
     * java.lang.String#toUpperCase(java.util.Locale)
     */
    public void test_toUpperCaseLjava_util_Locale_subtest0() {
        // Test for method java.lang.String
        // java.lang.String.toUpperCase(java.util.Locale)
    }

    /**
     * java.lang.String#trim()
     */
    public void test_trim() {
        // Test for method java.lang.String java.lang.String.trim()
        TestCase.assertTrue("Incorrect string returned", " HelloWorld ".trim().equals(hw1));
    }

    /**
     * java.lang.String#valueOf(char[])
     */
    public void test_valueOf$C() {
        // Test for method java.lang.String java.lang.String.valueOf(char [])
        TestCase.assertEquals("Returned incorrect String", "World", String.valueOf(buf));
    }

    /**
     * java.lang.String#valueOf(char[], int, int)
     */
    public void test_valueOf$CII() {
        // Test for method java.lang.String java.lang.String.valueOf(char [],
        // int, int)
        char[] t = new char[]{ 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };
        TestCase.assertEquals("copyValueOf returned incorrect String", "World", String.valueOf(t, 5, 5));
    }

    /**
     * java.lang.String#valueOf(char)
     */
    public void test_valueOfC() {
        // Test for method java.lang.String java.lang.String.valueOf(char)
        for (int i = 0; i < 65536; i++)
            TestCase.assertTrue(("Incorrect valueOf(char) returned: " + i), ((String.valueOf(((char) (i))).charAt(0)) == ((char) (i))));

    }

    /**
     * java.lang.String#valueOf(double)
     */
    public void test_valueOfD() {
        // Test for method java.lang.String java.lang.String.valueOf(double)
        TestCase.assertEquals("Incorrect double string returned", "1.7976931348623157E308", String.valueOf(Double.MAX_VALUE));
    }

    /**
     * java.lang.String#valueOf(float)
     */
    public void test_valueOfF() {
        // Test for method java.lang.String java.lang.String.valueOf(float)
        TestCase.assertTrue((("incorrect float string returned--got: " + (String.valueOf(1.0F))) + " wanted: 1.0"), String.valueOf(1.0F).equals("1.0"));
        TestCase.assertTrue((("incorrect float string returned--got: " + (String.valueOf(0.9F))) + " wanted: 0.9"), String.valueOf(0.9F).equals("0.9"));
        TestCase.assertTrue((("incorrect float string returned--got: " + (String.valueOf(109.567F))) + " wanted: 109.567"), String.valueOf(109.567F).equals("109.567"));
    }

    /**
     * java.lang.String#valueOf(int)
     */
    public void test_valueOfI() {
        // Test for method java.lang.String java.lang.String.valueOf(int)
        TestCase.assertEquals("returned invalid int string", "1", String.valueOf(1));
    }

    /**
     * java.lang.String#valueOf(long)
     */
    public void test_valueOfJ() {
        // Test for method java.lang.String java.lang.String.valueOf(long)
        TestCase.assertEquals("returned incorrect long string", "927654321098", String.valueOf(927654321098L));
    }

    /**
     * java.lang.String#valueOf(java.lang.Object)
     */
    public void test_valueOfLjava_lang_Object() {
        // Test for method java.lang.String
        // java.lang.String.valueOf(java.lang.Object)
        TestCase.assertTrue("Incorrect Object string returned", obj.toString().equals(String.valueOf(obj)));
    }

    /**
     * java.lang.String#valueOf(boolean)
     */
    public void test_valueOfZ() {
        // Test for method java.lang.String java.lang.String.valueOf(boolean)
        TestCase.assertTrue("Incorrect boolean string returned", ((String.valueOf(false).equals("false")) && (String.valueOf(true).equals("true"))));
    }

    /**
     * java.lang.String#contentEquals(CharSequence cs)
     */
    public void test_contentEqualsLjava_lang_CharSequence() {
        // Test for method java.lang.String
        // java.lang.String.contentEquals(CharSequence cs)
        TestCase.assertFalse("Incorrect result of compare", "qwerty".contentEquals(""));
    }
}

