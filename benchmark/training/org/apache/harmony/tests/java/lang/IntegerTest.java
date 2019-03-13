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
package org.apache.harmony.tests.java.lang;


import java.util.Properties;
import junit.framework.TestCase;


public class IntegerTest extends TestCase {
    private Properties orgProps;

    /**
     * java.lang.Integer#byteValue()
     */
    public void test_byteValue() {
        // Test for method byte java.lang.Integer.byteValue()
        TestCase.assertEquals("Returned incorrect byte value", (-1), new Integer(65535).byteValue());
        TestCase.assertEquals("Returned incorrect byte value", 127, new Integer(127).byteValue());
    }

    /**
     * java.lang.Integer#compareTo(java.lang.Integer)
     */
    public void test_compareToLjava_lang_Integer() {
        // Test for method int java.lang.Integer.compareTo(java.lang.Integer)
        TestCase.assertTrue("-2 compared to 1 gave non-negative answer", ((new Integer((-2)).compareTo(new Integer(1))) < 0));
        TestCase.assertEquals("-2 compared to -2 gave non-zero answer", 0, new Integer((-2)).compareTo(new Integer((-2))));
        TestCase.assertTrue("3 compared to 2 gave non-positive answer", ((new Integer(3).compareTo(new Integer(2))) > 0));
        try {
            new Integer(0).compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Integer#decode(java.lang.String)
     */
    public void test_decodeLjava_lang_String2() {
        // Test for method java.lang.Integer
        // java.lang.Integer.decode(java.lang.String)
        TestCase.assertEquals("Failed for 132233", 132233, Integer.decode("132233").intValue());
        TestCase.assertEquals("Failed for 07654321", 2054353, Integer.decode("07654321").intValue());
        TestCase.assertTrue("Failed for #1234567", ((Integer.decode("#1234567").intValue()) == 19088743));
        TestCase.assertTrue("Failed for 0xdAd", ((Integer.decode("0xdAd").intValue()) == 3501));
        TestCase.assertEquals("Failed for -23", (-23), Integer.decode("-23").intValue());
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Integer.decode("0").intValue());
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Integer.decode("0x0").intValue());
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Integer.decode("-2147483648").intValue()) == -2147483648));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Integer.decode("-0x80000000").intValue()) == -2147483648));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Integer.decode("2147483647").intValue()) == 2147483647));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Integer.decode("0x7fffffff").intValue()) == 2147483647));
        boolean exception = false;
        try {
            Integer.decode("0a");
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw NumberFormatException for \"Oa\"", exception);
        exception = false;
        try {
            Integer.decode("2147483648");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.decode("-2147483649");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Integer.decode("0x80000000");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.decode("-0x80000001");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE - 1", exception);
        exception = false;
        try {
            Integer.decode("9999999999");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for 9999999999", exception);
        try {
            Integer.decode("-");
            TestCase.fail("Expected exception for -");
        } catch (NumberFormatException e) {
            // Expected
        }
        try {
            Integer.decode("0x");
            TestCase.fail("Expected exception for 0x");
        } catch (NumberFormatException e) {
            // Expected
        }
        try {
            Integer.decode("#");
            TestCase.fail("Expected exception for #");
        } catch (NumberFormatException e) {
            // Expected
        }
        try {
            Integer.decode("x123");
            TestCase.fail("Expected exception for x123");
        } catch (NumberFormatException e) {
            // Expected
        }
        try {
            Integer.decode(null);
            TestCase.fail("Expected exception for null");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            Integer.decode("");
            TestCase.fail("Expected exception for empty string");
        } catch (NumberFormatException ex) {
            // Expected
        }
        try {
            Integer.decode(" ");
            TestCase.fail("Expected exception for single space");
        } catch (NumberFormatException ex) {
            // Expected
        }
    }

    /**
     * java.lang.Integer#doubleValue()
     */
    public void test_doubleValue2() {
        // Test for method double java.lang.Integer.doubleValue()
        TestCase.assertEquals("Returned incorrect double value", 2.147483647E9, new Integer(2147483647).doubleValue(), 0.0);
        TestCase.assertEquals("Returned incorrect double value", (-2.147483647E9), new Integer((-2147483647)).doubleValue(), 0.0);
    }

    /**
     * java.lang.Integer#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object2() {
        // Test for method boolean java.lang.Integer.equals(java.lang.Object)
        Integer i1 = new Integer(1000);
        Integer i2 = new Integer(1000);
        Integer i3 = new Integer((-1000));
        TestCase.assertTrue("Equality test failed", ((i1.equals(i2)) && (!(i1.equals(i3)))));
    }

    /**
     * java.lang.Integer#floatValue()
     */
    public void test_floatValue2() {
        // Test for method float java.lang.Integer.floatValue()
        TestCase.assertTrue("Returned incorrect float value", ((new Integer(65535).floatValue()) == 65535.0F));
        TestCase.assertTrue("Returned incorrect float value", ((new Integer((-65535)).floatValue()) == (-65535.0F)));
    }

    /**
     * java.lang.Integer#getInteger(java.lang.String)
     */
    public void test_getIntegerLjava_lang_String() {
        // Test for method java.lang.Integer
        // java.lang.Integer.getInteger(java.lang.String)
        Properties tProps = new Properties();
        tProps.put("testInt", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Integer", Integer.getInteger("testInt").equals(new Integer(99)));
        TestCase.assertNull("returned incorrect default Integer", Integer.getInteger("ff"));
    }

    /**
     * java.lang.Integer#getInteger(java.lang.String, int)
     */
    public void test_getIntegerLjava_lang_StringI() {
        // Test for method java.lang.Integer
        // java.lang.Integer.getInteger(java.lang.String, int)
        Properties tProps = new Properties();
        tProps.put("testInt", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Integer", Integer.getInteger("testInt", 4).equals(new Integer(99)));
        TestCase.assertTrue("returned incorrect default Integer", Integer.getInteger("ff", 4).equals(new Integer(4)));
    }

    /**
     * java.lang.Integer#getInteger(java.lang.String, java.lang.Integer)
     */
    public void test_getIntegerLjava_lang_StringLjava_lang_Integer() {
        // Test for method java.lang.Integer
        // java.lang.Integer.getInteger(java.lang.String, java.lang.Integer)
        Properties tProps = new Properties();
        tProps.put("testInt", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Integer", Integer.getInteger("testInt", new Integer(4)).equals(new Integer(99)));
        TestCase.assertTrue("returned incorrect default Integer", Integer.getInteger("ff", new Integer(4)).equals(new Integer(4)));
    }

    /**
     * java.lang.Integer#hashCode()
     */
    public void test_hashCode2() {
        // Test for method int java.lang.Integer.hashCode()
        Integer i1 = new Integer(1000);
        Integer i2 = new Integer((-1000));
        TestCase.assertTrue("Returned incorrect hashcode", (((i1.hashCode()) == 1000) && ((i2.hashCode()) == (-1000))));
    }

    /**
     * java.lang.Integer#intValue()
     */
    public void test_intValue2() {
        // Test for method int java.lang.Integer.intValue()
        Integer i = new Integer(8900);
        TestCase.assertEquals("Returned incorrect int value", 8900, i.intValue());
    }

    /**
     * java.lang.Integer#longValue()
     */
    public void test_longValue2() {
        // Test for method long java.lang.Integer.longValue()
        Integer i = new Integer(8900);
        TestCase.assertEquals("Returned incorrect long value", 8900L, i.longValue());
    }

    /**
     * java.lang.Integer#parseInt(java.lang.String)
     */
    public void test_parseIntLjava_lang_String2() {
        // Test for method int java.lang.Integer.parseInt(java.lang.String)
        int i = Integer.parseInt("-8900");
        TestCase.assertEquals("Returned incorrect int", (-8900), i);
        TestCase.assertEquals("Returned incorrect value for 0", 0, Integer.parseInt("0"));
        TestCase.assertTrue("Returned incorrect value for most negative value", ((Integer.parseInt("-2147483648")) == -2147483648));
        TestCase.assertTrue("Returned incorrect value for most positive value", ((Integer.parseInt("2147483647")) == 2147483647));
        boolean exception = false;
        try {
            Integer.parseInt("999999999999");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for value > int", exception);
        exception = false;
        try {
            Integer.parseInt("2147483648");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.parseInt("-2147483649");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Integer#parseInt(java.lang.String, int)
     */
    public void test_parseIntLjava_lang_StringI2() {
        // Test for method int java.lang.Integer.parseInt(java.lang.String, int)
        TestCase.assertEquals("Parsed dec val incorrectly", (-8000), Integer.parseInt("-8000", 10));
        TestCase.assertEquals("Parsed hex val incorrectly", 255, Integer.parseInt("FF", 16));
        TestCase.assertEquals("Parsed oct val incorrectly", 16, Integer.parseInt("20", 8));
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Integer.parseInt("0", 16));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Integer.parseInt("-80000000", 16)) == -2147483648));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Integer.parseInt("7fffffff", 16)) == 2147483647));
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Integer.parseInt("0", 10));
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Integer.parseInt("-2147483648", 10)) == -2147483648));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Integer.parseInt("2147483647", 10)) == 2147483647));
        boolean exception = false;
        try {
            Integer.parseInt("FFFF", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passes hex string and dec parm", exception);
        exception = false;
        try {
            Integer.parseInt("2147483648", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.parseInt("-2147483649", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Integer.parseInt("80000000", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.parseInt("-80000001", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE + 1", exception);
        exception = false;
        try {
            Integer.parseInt("9999999999", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for 9999999999", exception);
    }

    /**
     * java.lang.Integer#shortValue()
     */
    public void test_shortValue2() {
        // Test for method short java.lang.Integer.shortValue()
        Integer i = new Integer(2147450880);
        TestCase.assertEquals("Returned incorrect long value", (-32768), i.shortValue());
    }

    /**
     * java.lang.Integer#toBinaryString(int)
     */
    public void test_toBinaryStringI() {
        // Test for method java.lang.String
        // java.lang.Integer.toBinaryString(int)
        TestCase.assertEquals("Incorrect string returned", "1111111111111111111111111111111", Integer.toBinaryString(Integer.MAX_VALUE));
        TestCase.assertEquals("Incorrect string returned", "10000000000000000000000000000000", Integer.toBinaryString(Integer.MIN_VALUE));
    }

    /**
     * java.lang.Integer#toHexString(int)
     */
    public void test_toHexStringI() {
        // Test for method java.lang.String java.lang.Integer.toHexString(int)
        String[] hexvals = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        for (int i = 0; i < 16; i++) {
            TestCase.assertTrue(("Incorrect string returned " + (hexvals[i])), Integer.toHexString(i).equals(hexvals[i]));
        }
        TestCase.assertTrue(("Returned incorrect hex string: " + (Integer.toHexString(Integer.MAX_VALUE))), Integer.toHexString(Integer.MAX_VALUE).equals("7fffffff"));
        TestCase.assertTrue(("Returned incorrect hex string: " + (Integer.toHexString(Integer.MIN_VALUE))), Integer.toHexString(Integer.MIN_VALUE).equals("80000000"));
    }

    /**
     * java.lang.Integer#toOctalString(int)
     */
    public void test_toOctalStringI() {
        // Test for method java.lang.String java.lang.Integer.toOctalString(int)
        // Spec states that the int arg is treated as unsigned
        TestCase.assertEquals("Returned incorrect octal string", "17777777777", Integer.toOctalString(Integer.MAX_VALUE));
        TestCase.assertEquals("Returned incorrect octal string", "20000000000", Integer.toOctalString(Integer.MIN_VALUE));
    }

    /**
     * java.lang.Integer#toString()
     */
    public void test_toString2() {
        // Test for method java.lang.String java.lang.Integer.toString()
        Integer i = new Integer((-80001));
        TestCase.assertEquals("Returned incorrect String", "-80001", i.toString());
    }

    /**
     * java.lang.Integer#toString(int)
     */
    public void test_toStringI2() {
        // Test for method java.lang.String java.lang.Integer.toString(int)
        TestCase.assertEquals("Returned incorrect String", "-80765", Integer.toString((-80765)));
        TestCase.assertEquals("Returned incorrect octal string", "2147483647", Integer.toString(Integer.MAX_VALUE));
        TestCase.assertEquals("Returned incorrect octal string", "-2147483647", Integer.toString((-(Integer.MAX_VALUE))));
        TestCase.assertEquals("Returned incorrect octal string", "-2147483648", Integer.toString(Integer.MIN_VALUE));
        // Test for HARMONY-6068
        TestCase.assertEquals("Returned incorrect octal String", "-1000", Integer.toString((-1000)));
        TestCase.assertEquals("Returned incorrect octal String", "1000", Integer.toString(1000));
        TestCase.assertEquals("Returned incorrect octal String", "0", Integer.toString(0));
        TestCase.assertEquals("Returned incorrect octal String", "708", Integer.toString(708));
        TestCase.assertEquals("Returned incorrect octal String", "-100", Integer.toString((-100)));
        TestCase.assertEquals("Returned incorrect octal String", "-1000000008", Integer.toString((-1000000008)));
        TestCase.assertEquals("Returned incorrect octal String", "2000000008", Integer.toString(2000000008));
    }

    /**
     * java.lang.Integer#toString(int, int)
     */
    public void test_toStringII() {
        // Test for method java.lang.String java.lang.Integer.toString(int, int)
        TestCase.assertEquals("Returned incorrect octal string", "17777777777", Integer.toString(2147483647, 8));
        TestCase.assertTrue(("Returned incorrect hex string--wanted 7fffffff but got: " + (Integer.toString(2147483647, 16))), Integer.toString(2147483647, 16).equals("7fffffff"));
        TestCase.assertEquals("Incorrect string returned", "1111111111111111111111111111111", Integer.toString(2147483647, 2));
        TestCase.assertEquals("Incorrect string returned", "2147483647", Integer.toString(2147483647, 10));
        TestCase.assertEquals("Returned incorrect octal string", "-17777777777", Integer.toString((-2147483647), 8));
        TestCase.assertTrue(("Returned incorrect hex string--wanted -7fffffff but got: " + (Integer.toString((-2147483647), 16))), Integer.toString((-2147483647), 16).equals("-7fffffff"));
        TestCase.assertEquals("Incorrect string returned", "-1111111111111111111111111111111", Integer.toString((-2147483647), 2));
        TestCase.assertEquals("Incorrect string returned", "-2147483647", Integer.toString((-2147483647), 10));
        TestCase.assertEquals("Returned incorrect octal string", "-20000000000", Integer.toString(-2147483648, 8));
        TestCase.assertTrue(("Returned incorrect hex string--wanted -80000000 but got: " + (Integer.toString(-2147483648, 16))), Integer.toString(-2147483648, 16).equals("-80000000"));
        TestCase.assertEquals("Incorrect string returned", "-10000000000000000000000000000000", Integer.toString(-2147483648, 2));
        TestCase.assertEquals("Incorrect string returned", "-2147483648", Integer.toString(-2147483648, 10));
    }

    /**
     * java.lang.Integer#valueOf(java.lang.String)
     */
    public void test_valueOfLjava_lang_String2() {
        // Test for method java.lang.Integer
        // java.lang.Integer.valueOf(java.lang.String)
        TestCase.assertEquals("Returned incorrect int", 8888888, Integer.valueOf("8888888").intValue());
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("2147483647").intValue()) == (Integer.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("-2147483648").intValue()) == (Integer.MIN_VALUE)));
        boolean exception = false;
        try {
            Integer.valueOf("2147483648");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.valueOf("-2147483649");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Integer#valueOf(java.lang.String, int)
     */
    public void test_valueOfLjava_lang_StringI2() {
        // Test for method java.lang.Integer
        // java.lang.Integer.valueOf(java.lang.String, int)
        TestCase.assertEquals("Returned incorrect int for hex string", 255, Integer.valueOf("FF", 16).intValue());
        TestCase.assertEquals("Returned incorrect int for oct string", 16, Integer.valueOf("20", 8).intValue());
        TestCase.assertEquals("Returned incorrect int for bin string", 4, Integer.valueOf("100", 2).intValue());
        TestCase.assertEquals("Returned incorrect int for - hex string", (-255), Integer.valueOf("-FF", 16).intValue());
        TestCase.assertEquals("Returned incorrect int for - oct string", (-16), Integer.valueOf("-20", 8).intValue());
        TestCase.assertEquals("Returned incorrect int for - bin string", (-4), Integer.valueOf("-100", 2).intValue());
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("2147483647", 10).intValue()) == (Integer.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("-2147483648", 10).intValue()) == (Integer.MIN_VALUE)));
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("7fffffff", 16).intValue()) == (Integer.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect int", ((Integer.valueOf("-80000000", 16).intValue()) == (Integer.MIN_VALUE)));
        boolean exception = false;
        try {
            Integer.valueOf("FF", 2);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with hex string and base 2 radix", exception);
        exception = false;
        try {
            Integer.valueOf("2147483648", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.valueOf("-2147483649", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with MIN_VALUE - 1", exception);
        exception = false;
        try {
            Integer.valueOf("80000000", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Integer.valueOf("-80000001", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception with hex MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Integer#valueOf(byte)
     */
    public void test_valueOfI() {
        TestCase.assertEquals(new Integer(Integer.MIN_VALUE), Integer.valueOf(Integer.MIN_VALUE));
        TestCase.assertEquals(new Integer(Integer.MAX_VALUE), Integer.valueOf(Integer.MAX_VALUE));
        TestCase.assertEquals(new Integer(0), Integer.valueOf(0));
        short s = -128;
        while (s < 128) {
            TestCase.assertEquals(new Integer(s), Integer.valueOf(s));
            TestCase.assertSame(Integer.valueOf(s), Integer.valueOf(s));
            s++;
        } 
    }

    /**
     * java.lang.Integer#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(1, new Integer(1).hashCode());
        TestCase.assertEquals(2, new Integer(2).hashCode());
        TestCase.assertEquals(0, new Integer(0).hashCode());
        TestCase.assertEquals((-1), new Integer((-1)).hashCode());
    }

    /**
     * java.lang.Integer#Integer(String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertEquals(new Integer(0), new Integer("0"));
        TestCase.assertEquals(new Integer(1), new Integer("1"));
        TestCase.assertEquals(new Integer((-1)), new Integer("-1"));
        try {
            new Integer("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Integer("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Integer("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Integer(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Integer#Integer
     */
    public void test_ConstructorI() {
        TestCase.assertEquals(1, new Integer(1).intValue());
        TestCase.assertEquals(2, new Integer(2).intValue());
        TestCase.assertEquals(0, new Integer(0).intValue());
        TestCase.assertEquals((-1), new Integer((-1)).intValue());
        Integer i = new Integer((-89000));
        TestCase.assertEquals("Incorrect Integer created", (-89000), i.intValue());
    }

    /**
     * java.lang.Integer#byteValue()
     */
    public void test_booleanValue() {
        TestCase.assertEquals(1, new Integer(1).byteValue());
        TestCase.assertEquals(2, new Integer(2).byteValue());
        TestCase.assertEquals(0, new Integer(0).byteValue());
        TestCase.assertEquals((-1), new Integer((-1)).byteValue());
    }

    /**
     * java.lang.Integer#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertEquals(new Integer(0), Integer.valueOf(0));
        TestCase.assertEquals(new Integer(1), Integer.valueOf(1));
        TestCase.assertEquals(new Integer((-1)), Integer.valueOf((-1)));
        Integer fixture = new Integer(25);
        TestCase.assertEquals(fixture, fixture);
        TestCase.assertFalse(fixture.equals(null));
        TestCase.assertFalse(fixture.equals("Not a Integer"));
    }

    /**
     * java.lang.Integer#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("-1", new Integer((-1)).toString());
        TestCase.assertEquals("0", new Integer(0).toString());
        TestCase.assertEquals("1", new Integer(1).toString());
        TestCase.assertEquals("-1", new Integer(-1).toString());
    }

    /**
     * java.lang.Integer#toString
     */
    public void test_toStringI() {
        TestCase.assertEquals("-1", Integer.toString((-1)));
        TestCase.assertEquals("0", Integer.toString(0));
        TestCase.assertEquals("1", Integer.toString(1));
        TestCase.assertEquals("-1", Integer.toString(-1));
    }

    /**
     * java.lang.Integer#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(new Integer(0), Integer.valueOf("0"));
        TestCase.assertEquals(new Integer(1), Integer.valueOf("1"));
        TestCase.assertEquals(new Integer((-1)), Integer.valueOf("-1"));
        try {
            Integer.valueOf("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Integer#valueOf(String, int)
     */
    public void test_valueOfLjava_lang_StringI() {
        TestCase.assertEquals(new Integer(0), Integer.valueOf("0", 10));
        TestCase.assertEquals(new Integer(1), Integer.valueOf("1", 10));
        TestCase.assertEquals(new Integer((-1)), Integer.valueOf("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Integer.valueOf("1", 2).byteValue());
        TestCase.assertEquals(Character.digit('F', 16), Integer.valueOf("F", 16).byteValue());
        try {
            Integer.valueOf("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.valueOf(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Integer#parseInt(String)
     */
    public void test_parseIntLjava_lang_String() {
        TestCase.assertEquals(0, Integer.parseInt("0"));
        TestCase.assertEquals(1, Integer.parseInt("1"));
        TestCase.assertEquals((-1), Integer.parseInt("-1"));
        try {
            Integer.parseInt("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Integer#parseInt(String, int)
     */
    public void test_parseIntLjava_lang_StringI() {
        TestCase.assertEquals(0, Integer.parseInt("0", 10));
        TestCase.assertEquals(1, Integer.parseInt("1", 10));
        TestCase.assertEquals((-1), Integer.parseInt("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Integer.parseInt("1", 2));
        TestCase.assertEquals(Character.digit('F', 16), Integer.parseInt("F", 16));
        try {
            Integer.parseInt("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.parseInt(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Integer#decode(String)
     */
    public void test_decodeLjava_lang_String() {
        TestCase.assertEquals(new Integer(0), Integer.decode("0"));
        TestCase.assertEquals(new Integer(1), Integer.decode("1"));
        TestCase.assertEquals(new Integer((-1)), Integer.decode("-1"));
        TestCase.assertEquals(new Integer(15), Integer.decode("0xF"));
        TestCase.assertEquals(new Integer(15), Integer.decode("#F"));
        TestCase.assertEquals(new Integer(15), Integer.decode("0XF"));
        TestCase.assertEquals(new Integer(7), Integer.decode("07"));
        try {
            Integer.decode("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.decode("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Integer.decode(null);
            // undocumented NPE, but seems consistent across JREs
            TestCase.fail("Expected NullPointerException with null string.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Integer#doubleValue()
     */
    public void test_doubleValue() {
        TestCase.assertEquals((-1.0), new Integer((-1)).doubleValue(), 0.0);
        TestCase.assertEquals(0.0, new Integer(0).doubleValue(), 0.0);
        TestCase.assertEquals(1.0, new Integer(1).doubleValue(), 0.0);
    }

    /**
     * java.lang.Integer#floatValue()
     */
    public void test_floatValue() {
        TestCase.assertEquals((-1.0F), new Integer((-1)).floatValue(), 0.0F);
        TestCase.assertEquals(0.0F, new Integer(0).floatValue(), 0.0F);
        TestCase.assertEquals(1.0F, new Integer(1).floatValue(), 0.0F);
    }

    /**
     * java.lang.Integer#intValue()
     */
    public void test_intValue() {
        TestCase.assertEquals((-1), new Integer((-1)).intValue());
        TestCase.assertEquals(0, new Integer(0).intValue());
        TestCase.assertEquals(1, new Integer(1).intValue());
    }

    /**
     * java.lang.Integer#longValue()
     */
    public void test_longValue() {
        TestCase.assertEquals((-1L), new Integer((-1)).longValue());
        TestCase.assertEquals(0L, new Integer(0).longValue());
        TestCase.assertEquals(1L, new Integer(1).longValue());
    }

    /**
     * java.lang.Integer#shortValue()
     */
    public void test_shortValue() {
        TestCase.assertEquals((-1), new Integer((-1)).shortValue());
        TestCase.assertEquals(0, new Integer(0).shortValue());
        TestCase.assertEquals(1, new Integer(1).shortValue());
    }

    /**
     * java.lang.Integer#highestOneBit(int)
     */
    public void test_highestOneBitI() {
        TestCase.assertEquals(8, Integer.highestOneBit(10));
        TestCase.assertEquals(8, Integer.highestOneBit(11));
        TestCase.assertEquals(8, Integer.highestOneBit(12));
        TestCase.assertEquals(8, Integer.highestOneBit(15));
        TestCase.assertEquals(128, Integer.highestOneBit(255));
        TestCase.assertEquals(524288, Integer.highestOneBit(987700));
        TestCase.assertEquals(8388608, Integer.highestOneBit(16750967));
        TestCase.assertEquals(-2147483648, Integer.highestOneBit(-1));
        TestCase.assertEquals(0, Integer.highestOneBit(0));
        TestCase.assertEquals(1, Integer.highestOneBit(1));
        TestCase.assertEquals(-2147483648, Integer.highestOneBit((-1)));
    }

    /**
     * java.lang.Integer#lowestOneBit(int)
     */
    public void test_lowestOneBitI() {
        TestCase.assertEquals(16, Integer.lowestOneBit(240));
        TestCase.assertEquals(16, Integer.lowestOneBit(144));
        TestCase.assertEquals(16, Integer.lowestOneBit(208));
        TestCase.assertEquals(16, Integer.lowestOneBit(1193104));
        TestCase.assertEquals(16, Integer.lowestOneBit(1193168));
        TestCase.assertEquals(1048576, Integer.lowestOneBit(9437184));
        TestCase.assertEquals(1048576, Integer.lowestOneBit(13631488));
        TestCase.assertEquals(64, Integer.lowestOneBit(64));
        TestCase.assertEquals(64, Integer.lowestOneBit(192));
        TestCase.assertEquals(16384, Integer.lowestOneBit(16384));
        TestCase.assertEquals(16384, Integer.lowestOneBit(49152));
        TestCase.assertEquals(16384, Integer.lowestOneBit(-1718009856));
        TestCase.assertEquals(16384, Integer.lowestOneBit(-1717977088));
        TestCase.assertEquals(0, Integer.lowestOneBit(0));
        TestCase.assertEquals(1, Integer.lowestOneBit(1));
        TestCase.assertEquals(1, Integer.lowestOneBit((-1)));
    }

    /**
     * java.lang.Integer#numberOfLeadingZeros(int)
     */
    public void test_numberOfLeadingZerosI() {
        TestCase.assertEquals(32, Integer.numberOfLeadingZeros(0));
        TestCase.assertEquals(31, Integer.numberOfLeadingZeros(1));
        TestCase.assertEquals(30, Integer.numberOfLeadingZeros(2));
        TestCase.assertEquals(30, Integer.numberOfLeadingZeros(3));
        TestCase.assertEquals(29, Integer.numberOfLeadingZeros(4));
        TestCase.assertEquals(29, Integer.numberOfLeadingZeros(5));
        TestCase.assertEquals(29, Integer.numberOfLeadingZeros(6));
        TestCase.assertEquals(29, Integer.numberOfLeadingZeros(7));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(8));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(9));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(10));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(11));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(12));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(13));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(14));
        TestCase.assertEquals(28, Integer.numberOfLeadingZeros(15));
        TestCase.assertEquals(27, Integer.numberOfLeadingZeros(16));
        TestCase.assertEquals(24, Integer.numberOfLeadingZeros(128));
        TestCase.assertEquals(24, Integer.numberOfLeadingZeros(240));
        TestCase.assertEquals(23, Integer.numberOfLeadingZeros(256));
        TestCase.assertEquals(20, Integer.numberOfLeadingZeros(2048));
        TestCase.assertEquals(20, Integer.numberOfLeadingZeros(3840));
        TestCase.assertEquals(19, Integer.numberOfLeadingZeros(4096));
        TestCase.assertEquals(16, Integer.numberOfLeadingZeros(32768));
        TestCase.assertEquals(16, Integer.numberOfLeadingZeros(61440));
        TestCase.assertEquals(15, Integer.numberOfLeadingZeros(65536));
        TestCase.assertEquals(12, Integer.numberOfLeadingZeros(524288));
        TestCase.assertEquals(12, Integer.numberOfLeadingZeros(983040));
        TestCase.assertEquals(11, Integer.numberOfLeadingZeros(1048576));
        TestCase.assertEquals(8, Integer.numberOfLeadingZeros(8388608));
        TestCase.assertEquals(8, Integer.numberOfLeadingZeros(15728640));
        TestCase.assertEquals(7, Integer.numberOfLeadingZeros(16777216));
        TestCase.assertEquals(4, Integer.numberOfLeadingZeros(134217728));
        TestCase.assertEquals(4, Integer.numberOfLeadingZeros(251658240));
        TestCase.assertEquals(3, Integer.numberOfLeadingZeros(268435456));
        TestCase.assertEquals(0, Integer.numberOfLeadingZeros(-2147483648));
        TestCase.assertEquals(0, Integer.numberOfLeadingZeros(-268435456));
        TestCase.assertEquals(1, Integer.numberOfLeadingZeros(Integer.MAX_VALUE));
        TestCase.assertEquals(0, Integer.numberOfLeadingZeros(Integer.MIN_VALUE));
    }

    /**
     * java.lang.Integer#numberOfTrailingZeros(int)
     */
    public void test_numberOfTrailingZerosI() {
        TestCase.assertEquals(32, Integer.numberOfTrailingZeros(0));
        TestCase.assertEquals(31, Integer.numberOfTrailingZeros(Integer.MIN_VALUE));
        TestCase.assertEquals(0, Integer.numberOfTrailingZeros(Integer.MAX_VALUE));
        TestCase.assertEquals(0, Integer.numberOfTrailingZeros(1));
        TestCase.assertEquals(3, Integer.numberOfTrailingZeros(8));
        TestCase.assertEquals(0, Integer.numberOfTrailingZeros(15));
        TestCase.assertEquals(4, Integer.numberOfTrailingZeros(16));
        TestCase.assertEquals(7, Integer.numberOfTrailingZeros(128));
        TestCase.assertEquals(4, Integer.numberOfTrailingZeros(240));
        TestCase.assertEquals(8, Integer.numberOfTrailingZeros(256));
        TestCase.assertEquals(11, Integer.numberOfTrailingZeros(2048));
        TestCase.assertEquals(8, Integer.numberOfTrailingZeros(3840));
        TestCase.assertEquals(12, Integer.numberOfTrailingZeros(4096));
        TestCase.assertEquals(15, Integer.numberOfTrailingZeros(32768));
        TestCase.assertEquals(12, Integer.numberOfTrailingZeros(61440));
        TestCase.assertEquals(16, Integer.numberOfTrailingZeros(65536));
        TestCase.assertEquals(19, Integer.numberOfTrailingZeros(524288));
        TestCase.assertEquals(16, Integer.numberOfTrailingZeros(983040));
        TestCase.assertEquals(20, Integer.numberOfTrailingZeros(1048576));
        TestCase.assertEquals(23, Integer.numberOfTrailingZeros(8388608));
        TestCase.assertEquals(20, Integer.numberOfTrailingZeros(15728640));
        TestCase.assertEquals(24, Integer.numberOfTrailingZeros(16777216));
        TestCase.assertEquals(27, Integer.numberOfTrailingZeros(134217728));
        TestCase.assertEquals(24, Integer.numberOfTrailingZeros(251658240));
        TestCase.assertEquals(28, Integer.numberOfTrailingZeros(268435456));
        TestCase.assertEquals(31, Integer.numberOfTrailingZeros(-2147483648));
        TestCase.assertEquals(28, Integer.numberOfTrailingZeros(-268435456));
    }

    /**
     * java.lang.Integer#bitCount(int)
     */
    public void test_bitCountI() {
        TestCase.assertEquals(0, Integer.bitCount(0));
        TestCase.assertEquals(1, Integer.bitCount(1));
        TestCase.assertEquals(1, Integer.bitCount(2));
        TestCase.assertEquals(2, Integer.bitCount(3));
        TestCase.assertEquals(1, Integer.bitCount(4));
        TestCase.assertEquals(2, Integer.bitCount(5));
        TestCase.assertEquals(2, Integer.bitCount(6));
        TestCase.assertEquals(3, Integer.bitCount(7));
        TestCase.assertEquals(1, Integer.bitCount(8));
        TestCase.assertEquals(2, Integer.bitCount(9));
        TestCase.assertEquals(2, Integer.bitCount(10));
        TestCase.assertEquals(3, Integer.bitCount(11));
        TestCase.assertEquals(2, Integer.bitCount(12));
        TestCase.assertEquals(3, Integer.bitCount(13));
        TestCase.assertEquals(3, Integer.bitCount(14));
        TestCase.assertEquals(4, Integer.bitCount(15));
        TestCase.assertEquals(8, Integer.bitCount(255));
        TestCase.assertEquals(12, Integer.bitCount(4095));
        TestCase.assertEquals(16, Integer.bitCount(65535));
        TestCase.assertEquals(20, Integer.bitCount(1048575));
        TestCase.assertEquals(24, Integer.bitCount(16777215));
        TestCase.assertEquals(28, Integer.bitCount(268435455));
        TestCase.assertEquals(32, Integer.bitCount(-1));
    }

    /**
     * java.lang.Integer#rotateLeft(int, int)
     */
    public void test_rotateLeftII() {
        TestCase.assertEquals(15, Integer.rotateLeft(15, 0));
        TestCase.assertEquals(240, Integer.rotateLeft(15, 4));
        TestCase.assertEquals(3840, Integer.rotateLeft(15, 8));
        TestCase.assertEquals(61440, Integer.rotateLeft(15, 12));
        TestCase.assertEquals(983040, Integer.rotateLeft(15, 16));
        TestCase.assertEquals(15728640, Integer.rotateLeft(15, 20));
        TestCase.assertEquals(251658240, Integer.rotateLeft(15, 24));
        TestCase.assertEquals(-268435456, Integer.rotateLeft(15, 28));
        TestCase.assertEquals(-268435456, Integer.rotateLeft(-268435456, 32));
    }

    /**
     * java.lang.Integer#rotateRight(int, int)
     */
    public void test_rotateRightII() {
        TestCase.assertEquals(15, Integer.rotateRight(240, 4));
        TestCase.assertEquals(15, Integer.rotateRight(3840, 8));
        TestCase.assertEquals(15, Integer.rotateRight(61440, 12));
        TestCase.assertEquals(15, Integer.rotateRight(983040, 16));
        TestCase.assertEquals(15, Integer.rotateRight(15728640, 20));
        TestCase.assertEquals(15, Integer.rotateRight(251658240, 24));
        TestCase.assertEquals(15, Integer.rotateRight(-268435456, 28));
        TestCase.assertEquals(-268435456, Integer.rotateRight(-268435456, 32));
        TestCase.assertEquals(-268435456, Integer.rotateRight(-268435456, 0));
    }

    /**
     * java.lang.Integer#reverseBytes(int)
     */
    public void test_reverseBytesI() {
        TestCase.assertEquals(-1430532899, Integer.reverseBytes(-573785174));
        TestCase.assertEquals(287454020, Integer.reverseBytes(1144201745));
        TestCase.assertEquals(1122867, Integer.reverseBytes(857870592));
        TestCase.assertEquals(536870914, Integer.reverseBytes(33554464));
    }

    /**
     * java.lang.Integer#reverse(int)
     */
    public void test_reverseI() {
        TestCase.assertEquals((-1), Integer.reverse((-1)));
        TestCase.assertEquals(-2147483648, Integer.reverse(1));
    }

    /**
     * java.lang.Integer#signum(int)
     */
    public void test_signumI() {
        for (int i = -128; i < 0; i++) {
            TestCase.assertEquals((-1), Integer.signum(i));
        }
        TestCase.assertEquals(0, Integer.signum(0));
        for (int i = 1; i <= 127; i++) {
            TestCase.assertEquals(1, Integer.signum(i));
        }
    }
}

