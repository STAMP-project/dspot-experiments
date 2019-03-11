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


public class LongTest extends TestCase {
    private Properties orgProps;

    /**
     * java.lang.Long#byteValue()
     */
    public void test_byteValue() {
        // Test for method byte java.lang.Long.byteValue()
        Long l = new Long(127);
        TestCase.assertEquals("Returned incorrect byte value", 127, l.byteValue());
        TestCase.assertEquals("Returned incorrect byte value", (-1), new Long(Long.MAX_VALUE).byteValue());
    }

    /**
     * java.lang.Long#compareTo(java.lang.Long)
     */
    public void test_compareToLjava_lang_Long() {
        // Test for method int java.lang.Long.compareTo(java.lang.Long)
        TestCase.assertTrue("-2 compared to 1 gave non-negative answer", ((new Long((-2L)).compareTo(new Long(1L))) < 0));
        TestCase.assertEquals("-2 compared to -2 gave non-zero answer", 0, new Long((-2L)).compareTo(new Long((-2L))));
        TestCase.assertTrue("3 compared to 2 gave non-positive answer", ((new Long(3L).compareTo(new Long(2L))) > 0));
        try {
            new Long(0).compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Long#decode(java.lang.String)
     */
    public void test_decodeLjava_lang_String2() {
        // Test for method java.lang.Long
        // java.lang.Long.decode(java.lang.String)
        TestCase.assertEquals("Returned incorrect value for hex string", 255L, Long.decode("0xFF").longValue());
        TestCase.assertEquals("Returned incorrect value for dec string", (-89000L), Long.decode("-89000").longValue());
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Long.decode("0").longValue());
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Long.decode("0x0").longValue());
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Long.decode("-9223372036854775808").longValue()) == -9223372036854775808L));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Long.decode("-0x8000000000000000").longValue()) == -9223372036854775808L));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Long.decode("9223372036854775807").longValue()) == 9223372036854775807L));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Long.decode("0x7fffffffffffffff").longValue()) == 9223372036854775807L));
        TestCase.assertTrue("Failed for 07654321765432", ((Long.decode("07654321765432").longValue()) == 538536569626L));
        boolean exception = false;
        try {
            Long.decode("999999999999999999999999999999999999999999999999999999");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for value > ilong", exception);
        exception = false;
        try {
            Long.decode("9223372036854775808");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Long.decode("-9223372036854775809");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Long.decode("0x8000000000000000");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Long.decode("-0x8000000000000001");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE - 1", exception);
        exception = false;
        try {
            Long.decode("42325917317067571199");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for 42325917317067571199", exception);
    }

    /**
     * java.lang.Long#getLong(java.lang.String)
     */
    public void test_getLongLjava_lang_String() {
        // Test for method java.lang.Long
        // java.lang.Long.getLong(java.lang.String)
        Properties tProps = new Properties();
        tProps.put("testLong", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Long", Long.getLong("testLong").equals(new Long(99)));
        TestCase.assertNull("returned incorrect default Long", Long.getLong("ff"));
    }

    /**
     * java.lang.Long#getLong(java.lang.String, long)
     */
    public void test_getLongLjava_lang_StringJ() {
        // Test for method java.lang.Long
        // java.lang.Long.getLong(java.lang.String, long)
        Properties tProps = new Properties();
        tProps.put("testLong", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Long", Long.getLong("testLong", 4L).equals(new Long(99)));
        TestCase.assertTrue("returned incorrect default Long", Long.getLong("ff", 4L).equals(new Long(4)));
    }

    /**
     * java.lang.Long#getLong(java.lang.String, java.lang.Long)
     */
    public void test_getLongLjava_lang_StringLjava_lang_Long() {
        // Test for method java.lang.Long
        // java.lang.Long.getLong(java.lang.String, java.lang.Long)
        Properties tProps = new Properties();
        tProps.put("testLong", "99");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect Long", Long.getLong("testLong", new Long(4)).equals(new Long(99)));
        TestCase.assertTrue("returned incorrect default Long", Long.getLong("ff", new Long(4)).equals(new Long(4)));
    }

    /**
     * java.lang.Long#parseLong(java.lang.String)
     */
    public void test_parseLongLjava_lang_String2() {
        // Test for method long java.lang.Long.parseLong(java.lang.String)
        long l = Long.parseLong("89000000005");
        TestCase.assertEquals("Parsed to incorrect long value", 89000000005L, l);
        TestCase.assertEquals("Returned incorrect value for 0", 0, Long.parseLong("0"));
        TestCase.assertTrue("Returned incorrect value for most negative value", ((Long.parseLong("-9223372036854775808")) == -9223372036854775808L));
        TestCase.assertTrue("Returned incorrect value for most positive value", ((Long.parseLong("9223372036854775807")) == 9223372036854775807L));
        boolean exception = false;
        try {
            Long.parseLong("9223372036854775808");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Long.parseLong("-9223372036854775809");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Long#parseLong(java.lang.String, int)
     */
    public void test_parseLongLjava_lang_StringI() {
        // Test for method long java.lang.Long.parseLong(java.lang.String, int)
        TestCase.assertEquals("Returned incorrect value", 100000000L, Long.parseLong("100000000", 10));
        TestCase.assertEquals("Returned incorrect value from hex string", 68719476735L, Long.parseLong("FFFFFFFFF", 16));
        TestCase.assertTrue(("Returned incorrect value from octal string: " + (Long.parseLong("77777777777"))), ((Long.parseLong("77777777777", 8)) == 8589934591L));
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Long.parseLong("0", 16));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Long.parseLong("-8000000000000000", 16)) == -9223372036854775808L));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Long.parseLong("7fffffffffffffff", 16)) == 9223372036854775807L));
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Long.parseLong("0", 10));
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Long.parseLong("-9223372036854775808", 10)) == -9223372036854775808L));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Long.parseLong("9223372036854775807", 10)) == 9223372036854775807L));
        boolean exception = false;
        try {
            Long.parseLong("999999999999", 8);
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
        exception = false;
        try {
            Long.parseLong("9223372036854775808", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Long.parseLong("-9223372036854775809", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Long.parseLong("8000000000000000", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Long.parseLong("-8000000000000001", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE + 1", exception);
        exception = false;
        try {
            Long.parseLong("42325917317067571199", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for 42325917317067571199", exception);
    }

    /**
     * java.lang.Long#toBinaryString(long)
     */
    public void test_toBinaryStringJ() {
        // Test for method java.lang.String java.lang.Long.toBinaryString(long)
        TestCase.assertEquals("Incorrect binary string returned", "11011001010010010000", Long.toBinaryString(890000L));
        TestCase.assertEquals("Incorrect binary string returned", "1000000000000000000000000000000000000000000000000000000000000000", Long.toBinaryString(Long.MIN_VALUE));
        TestCase.assertEquals("Incorrect binary string returned", "111111111111111111111111111111111111111111111111111111111111111", Long.toBinaryString(Long.MAX_VALUE));
    }

    /**
     * java.lang.Long#toHexString(long)
     */
    public void test_toHexStringJ() {
        // Test for method java.lang.String java.lang.Long.toHexString(long)
        TestCase.assertEquals("Incorrect hex string returned", "54e0845", Long.toHexString(89000005L));
        TestCase.assertEquals("Incorrect hex string returned", "8000000000000000", Long.toHexString(Long.MIN_VALUE));
        TestCase.assertEquals("Incorrect hex string returned", "7fffffffffffffff", Long.toHexString(Long.MAX_VALUE));
    }

    /**
     * java.lang.Long#toOctalString(long)
     */
    public void test_toOctalStringJ() {
        // Test for method java.lang.String java.lang.Long.toOctalString(long)
        TestCase.assertEquals("Returned incorrect oct string", "77777777777", Long.toOctalString(8589934591L));
        TestCase.assertEquals("Returned incorrect oct string", "1000000000000000000000", Long.toOctalString(Long.MIN_VALUE));
        TestCase.assertEquals("Returned incorrect oct string", "777777777777777777777", Long.toOctalString(Long.MAX_VALUE));
    }

    /**
     * java.lang.Long#toString()
     */
    public void test_toString2() {
        // Test for method java.lang.String java.lang.Long.toString()
        Long l = new Long(89000000005L);
        TestCase.assertEquals("Returned incorrect String", "89000000005", l.toString());
        TestCase.assertEquals("Returned incorrect String", "-9223372036854775808", new Long(Long.MIN_VALUE).toString());
        TestCase.assertEquals("Returned incorrect String", "9223372036854775807", new Long(Long.MAX_VALUE).toString());
    }

    /**
     * java.lang.Long#toString(long)
     */
    public void test_toStringJ2() {
        // Test for method java.lang.String java.lang.Long.toString(long)
        TestCase.assertEquals("Returned incorrect String", "89000000005", Long.toString(89000000005L));
        TestCase.assertEquals("Returned incorrect String", "-9223372036854775808", Long.toString(Long.MIN_VALUE));
        TestCase.assertEquals("Returned incorrect String", "9223372036854775807", Long.toString(Long.MAX_VALUE));
    }

    /**
     * java.lang.Long#toString(long, int)
     */
    public void test_toStringJI() {
        // Test for method java.lang.String java.lang.Long.toString(long, int)
        TestCase.assertEquals("Returned incorrect dec string", "100000000", Long.toString(100000000L, 10));
        TestCase.assertEquals("Returned incorrect hex string", "fffffffff", Long.toString(68719476735L, 16));
        TestCase.assertEquals("Returned incorrect oct string", "77777777777", Long.toString(8589934591L, 8));
        TestCase.assertEquals("Returned incorrect bin string", "1111111111111111111111111111111111111111111", Long.toString(8796093022207L, 2));
        TestCase.assertEquals("Returned incorrect min string", "-9223372036854775808", Long.toString(-9223372036854775808L, 10));
        TestCase.assertEquals("Returned incorrect max string", "9223372036854775807", Long.toString(9223372036854775807L, 10));
        TestCase.assertEquals("Returned incorrect min string", "-8000000000000000", Long.toString(-9223372036854775808L, 16));
        TestCase.assertEquals("Returned incorrect max string", "7fffffffffffffff", Long.toString(9223372036854775807L, 16));
    }

    /**
     * java.lang.Long#valueOf(java.lang.String)
     */
    public void test_valueOfLjava_lang_String2() {
        // Test for method java.lang.Long
        // java.lang.Long.valueOf(java.lang.String)
        TestCase.assertEquals("Returned incorrect value", 100000000L, Long.valueOf("100000000").longValue());
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("9223372036854775807").longValue()) == (Long.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("-9223372036854775808").longValue()) == (Long.MIN_VALUE)));
        boolean exception = false;
        try {
            Long.valueOf("999999999999999999999999999999999999999999999999999999999999");
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
        exception = false;
        try {
            Long.valueOf("9223372036854775808");
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
        exception = false;
        try {
            Long.valueOf("-9223372036854775809");
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
    }

    /**
     * java.lang.Long#valueOf(java.lang.String, int)
     */
    public void test_valueOfLjava_lang_StringI() {
        // Test for method java.lang.Long
        // java.lang.Long.valueOf(java.lang.String, int)
        TestCase.assertEquals("Returned incorrect value", 100000000L, Long.valueOf("100000000", 10).longValue());
        TestCase.assertEquals("Returned incorrect value from hex string", 68719476735L, Long.valueOf("FFFFFFFFF", 16).longValue());
        TestCase.assertTrue(("Returned incorrect value from octal string: " + (Long.valueOf("77777777777", 8).toString())), ((Long.valueOf("77777777777", 8).longValue()) == 8589934591L));
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("9223372036854775807", 10).longValue()) == (Long.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("-9223372036854775808", 10).longValue()) == (Long.MIN_VALUE)));
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("7fffffffffffffff", 16).longValue()) == (Long.MAX_VALUE)));
        TestCase.assertTrue("Returned incorrect value", ((Long.valueOf("-8000000000000000", 16).longValue()) == (Long.MIN_VALUE)));
        boolean exception = false;
        try {
            Long.valueOf("999999999999", 8);
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
        exception = false;
        try {
            Long.valueOf("9223372036854775808", 10);
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
        exception = false;
        try {
            Long.valueOf("-9223372036854775809", 10);
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed invalid string", exception);
    }

    /**
     * java.lang.Long#valueOf(long)
     */
    public void test_valueOfJ() {
        TestCase.assertEquals(new Long(Long.MIN_VALUE), Long.valueOf(Long.MIN_VALUE));
        TestCase.assertEquals(new Long(Long.MAX_VALUE), Long.valueOf(Long.MAX_VALUE));
        TestCase.assertEquals(new Long(0), Long.valueOf(0));
        long lng = -128;
        while (lng < 128) {
            TestCase.assertEquals(new Long(lng), Long.valueOf(lng));
            TestCase.assertSame(Long.valueOf(lng), Long.valueOf(lng));
            lng++;
        } 
    }

    /**
     * java.lang.Long#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(((int) (1L ^ (1L >>> 32))), new Long(1).hashCode());
        TestCase.assertEquals(((int) (2L ^ (2L >>> 32))), new Long(2).hashCode());
        TestCase.assertEquals(((int) (0L ^ (0L >>> 32))), new Long(0).hashCode());
        TestCase.assertEquals(((int) ((-1L) ^ ((-1L) >>> 32))), new Long((-1)).hashCode());
    }

    /**
     * java.lang.Long#Long(String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertEquals(new Long(0), new Long("0"));
        TestCase.assertEquals(new Long(1), new Long("1"));
        TestCase.assertEquals(new Long((-1)), new Long("-1"));
        try {
            new Long("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Long("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Long("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Long(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Long#Long
     */
    public void test_ConstructorJ() {
        TestCase.assertEquals(1, new Long(1).intValue());
        TestCase.assertEquals(2, new Long(2).intValue());
        TestCase.assertEquals(0, new Long(0).intValue());
        TestCase.assertEquals((-1), new Long((-1)).intValue());
    }

    /**
     * java.lang.Long#byteValue()
     */
    public void test_booleanValue() {
        TestCase.assertEquals(1, new Long(1).byteValue());
        TestCase.assertEquals(2, new Long(2).byteValue());
        TestCase.assertEquals(0, new Long(0).byteValue());
        TestCase.assertEquals((-1), new Long((-1)).byteValue());
    }

    /**
     * java.lang.Long#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertEquals(new Long(0), Long.valueOf(0));
        TestCase.assertEquals(new Long(1), Long.valueOf(1));
        TestCase.assertEquals(new Long((-1)), Long.valueOf((-1)));
        Long fixture = new Long(25);
        TestCase.assertEquals(fixture, fixture);
        TestCase.assertFalse(fixture.equals(null));
        TestCase.assertFalse(fixture.equals("Not a Long"));
    }

    /**
     * java.lang.Long#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("-1", new Long((-1)).toString());
        TestCase.assertEquals("0", new Long(0).toString());
        TestCase.assertEquals("1", new Long(1).toString());
        TestCase.assertEquals("-1", new Long(-1).toString());
    }

    /**
     * java.lang.Long#toString
     */
    public void test_toStringJ() {
        TestCase.assertEquals("-1", Long.toString((-1)));
        TestCase.assertEquals("0", Long.toString(0));
        TestCase.assertEquals("1", Long.toString(1));
        TestCase.assertEquals("-1", Long.toString(-1));
    }

    /**
     * java.lang.Long#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(new Long(0), Long.valueOf("0"));
        TestCase.assertEquals(new Long(1), Long.valueOf("1"));
        TestCase.assertEquals(new Long((-1)), Long.valueOf("-1"));
        try {
            Long.valueOf("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Long#valueOf(String, long)
     */
    public void test_valueOfLjava_lang_StringJ() {
        TestCase.assertEquals(new Long(0), Long.valueOf("0", 10));
        TestCase.assertEquals(new Long(1), Long.valueOf("1", 10));
        TestCase.assertEquals(new Long((-1)), Long.valueOf("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Long.valueOf("1", 2).byteValue());
        TestCase.assertEquals(Character.digit('F', 16), Long.valueOf("F", 16).byteValue());
        try {
            Long.valueOf("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.valueOf(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Long#parseLong(String)
     */
    public void test_parseLongLjava_lang_String() {
        TestCase.assertEquals(0, Long.parseLong("0"));
        TestCase.assertEquals(1, Long.parseLong("1"));
        TestCase.assertEquals((-1), Long.parseLong("-1"));
        try {
            Long.parseLong("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Long#parseLong(String, long)
     */
    public void test_parseLongLjava_lang_StringJ() {
        TestCase.assertEquals(0, Long.parseLong("0", 10));
        TestCase.assertEquals(1, Long.parseLong("1", 10));
        TestCase.assertEquals((-1), Long.parseLong("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Long.parseLong("1", 2));
        TestCase.assertEquals(Character.digit('F', 16), Long.parseLong("F", 16));
        try {
            Long.parseLong("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.parseLong(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Long#decode(String)
     */
    public void test_decodeLjava_lang_String() {
        TestCase.assertEquals(new Long(0), Long.decode("0"));
        TestCase.assertEquals(new Long(1), Long.decode("1"));
        TestCase.assertEquals(new Long((-1)), Long.decode("-1"));
        TestCase.assertEquals(new Long(15), Long.decode("0xF"));
        TestCase.assertEquals(new Long(15), Long.decode("#F"));
        TestCase.assertEquals(new Long(15), Long.decode("0XF"));
        TestCase.assertEquals(new Long(7), Long.decode("07"));
        try {
            Long.decode("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.decode("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Long.decode(null);
            // undocumented NPE, but seems consistent across JREs
            TestCase.fail("Expected NullPointerException with null string.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Long#doubleValue()
     */
    public void test_doubleValue() {
        TestCase.assertEquals((-1.0), new Long((-1)).doubleValue(), 0.0);
        TestCase.assertEquals(0.0, new Long(0).doubleValue(), 0.0);
        TestCase.assertEquals(1.0, new Long(1).doubleValue(), 0.0);
    }

    /**
     * java.lang.Long#floatValue()
     */
    public void test_floatValue() {
        TestCase.assertEquals((-1.0F), new Long((-1)).floatValue(), 0.0F);
        TestCase.assertEquals(0.0F, new Long(0).floatValue(), 0.0F);
        TestCase.assertEquals(1.0F, new Long(1).floatValue(), 0.0F);
    }

    /**
     * java.lang.Long#intValue()
     */
    public void test_intValue() {
        TestCase.assertEquals((-1), new Long((-1)).intValue());
        TestCase.assertEquals(0, new Long(0).intValue());
        TestCase.assertEquals(1, new Long(1).intValue());
    }

    /**
     * java.lang.Long#longValue()
     */
    public void test_longValue() {
        TestCase.assertEquals((-1L), new Long((-1)).longValue());
        TestCase.assertEquals(0L, new Long(0).longValue());
        TestCase.assertEquals(1L, new Long(1).longValue());
    }

    /**
     * java.lang.Long#shortValue()
     */
    public void test_shortValue() {
        TestCase.assertEquals((-1), new Long((-1)).shortValue());
        TestCase.assertEquals(0, new Long(0).shortValue());
        TestCase.assertEquals(1, new Long(1).shortValue());
    }

    /**
     * java.lang.Long#highestOneBit(long)
     */
    public void test_highestOneBitJ() {
        TestCase.assertEquals(8, Long.highestOneBit(10));
        TestCase.assertEquals(8, Long.highestOneBit(11));
        TestCase.assertEquals(8, Long.highestOneBit(12));
        TestCase.assertEquals(8, Long.highestOneBit(15));
        TestCase.assertEquals(128, Long.highestOneBit(255));
        TestCase.assertEquals(524288, Long.highestOneBit(987700));
        TestCase.assertEquals(8388608, Long.highestOneBit(16750967));
        TestCase.assertEquals(-9223372036854775808L, Long.highestOneBit(-1L));
        TestCase.assertEquals(0, Long.highestOneBit(0));
        TestCase.assertEquals(1, Long.highestOneBit(1));
        TestCase.assertEquals(-9223372036854775808L, Long.highestOneBit((-1)));
    }

    /**
     * java.lang.Long#lowestOneBit(long)
     */
    public void test_lowestOneBitJ() {
        TestCase.assertEquals(16, Long.lowestOneBit(240));
        TestCase.assertEquals(16, Long.lowestOneBit(144));
        TestCase.assertEquals(16, Long.lowestOneBit(208));
        TestCase.assertEquals(16, Long.lowestOneBit(1193104));
        TestCase.assertEquals(16, Long.lowestOneBit(1193168));
        TestCase.assertEquals(1048576, Long.lowestOneBit(9437184));
        TestCase.assertEquals(1048576, Long.lowestOneBit(13631488));
        TestCase.assertEquals(64, Long.lowestOneBit(64));
        TestCase.assertEquals(64, Long.lowestOneBit(192));
        TestCase.assertEquals(16384, Long.lowestOneBit(16384));
        TestCase.assertEquals(16384, Long.lowestOneBit(49152));
        TestCase.assertEquals(16384, Long.lowestOneBit(-1718009856));
        TestCase.assertEquals(16384, Long.lowestOneBit(-1717977088));
        TestCase.assertEquals(0, Long.lowestOneBit(0));
        TestCase.assertEquals(1, Long.lowestOneBit(1));
        TestCase.assertEquals(1, Long.lowestOneBit((-1)));
    }

    /**
     * java.lang.Long#numberOfLeadingZeros(long)
     */
    public void test_numberOfLeadingZerosJ() {
        TestCase.assertEquals(64, Long.numberOfLeadingZeros(0L));
        TestCase.assertEquals(63, Long.numberOfLeadingZeros(1));
        TestCase.assertEquals(62, Long.numberOfLeadingZeros(2));
        TestCase.assertEquals(62, Long.numberOfLeadingZeros(3));
        TestCase.assertEquals(61, Long.numberOfLeadingZeros(4));
        TestCase.assertEquals(61, Long.numberOfLeadingZeros(5));
        TestCase.assertEquals(61, Long.numberOfLeadingZeros(6));
        TestCase.assertEquals(61, Long.numberOfLeadingZeros(7));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(8));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(9));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(10));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(11));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(12));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(13));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(14));
        TestCase.assertEquals(60, Long.numberOfLeadingZeros(15));
        TestCase.assertEquals(59, Long.numberOfLeadingZeros(16));
        TestCase.assertEquals(56, Long.numberOfLeadingZeros(128));
        TestCase.assertEquals(56, Long.numberOfLeadingZeros(240));
        TestCase.assertEquals(55, Long.numberOfLeadingZeros(256));
        TestCase.assertEquals(52, Long.numberOfLeadingZeros(2048));
        TestCase.assertEquals(52, Long.numberOfLeadingZeros(3840));
        TestCase.assertEquals(51, Long.numberOfLeadingZeros(4096));
        TestCase.assertEquals(48, Long.numberOfLeadingZeros(32768));
        TestCase.assertEquals(48, Long.numberOfLeadingZeros(61440));
        TestCase.assertEquals(47, Long.numberOfLeadingZeros(65536));
        TestCase.assertEquals(44, Long.numberOfLeadingZeros(524288));
        TestCase.assertEquals(44, Long.numberOfLeadingZeros(983040));
        TestCase.assertEquals(43, Long.numberOfLeadingZeros(1048576));
        TestCase.assertEquals(40, Long.numberOfLeadingZeros(8388608));
        TestCase.assertEquals(40, Long.numberOfLeadingZeros(15728640));
        TestCase.assertEquals(39, Long.numberOfLeadingZeros(16777216));
        TestCase.assertEquals(36, Long.numberOfLeadingZeros(134217728));
        TestCase.assertEquals(36, Long.numberOfLeadingZeros(251658240));
        TestCase.assertEquals(35, Long.numberOfLeadingZeros(268435456));
        TestCase.assertEquals(0, Long.numberOfLeadingZeros(-2147483648));
        TestCase.assertEquals(0, Long.numberOfLeadingZeros(-268435456));
        TestCase.assertEquals(1, Long.numberOfLeadingZeros(Long.MAX_VALUE));
        TestCase.assertEquals(0, Long.numberOfLeadingZeros(Long.MIN_VALUE));
    }

    /**
     * java.lang.Long#numberOfTrailingZeros(long)
     */
    public void test_numberOfTrailingZerosJ() {
        TestCase.assertEquals(64, Long.numberOfTrailingZeros(0));
        TestCase.assertEquals(63, Long.numberOfTrailingZeros(Long.MIN_VALUE));
        TestCase.assertEquals(0, Long.numberOfTrailingZeros(Long.MAX_VALUE));
        TestCase.assertEquals(0, Long.numberOfTrailingZeros(1));
        TestCase.assertEquals(3, Long.numberOfTrailingZeros(8));
        TestCase.assertEquals(0, Long.numberOfTrailingZeros(15));
        TestCase.assertEquals(4, Long.numberOfTrailingZeros(16));
        TestCase.assertEquals(7, Long.numberOfTrailingZeros(128));
        TestCase.assertEquals(4, Long.numberOfTrailingZeros(240));
        TestCase.assertEquals(8, Long.numberOfTrailingZeros(256));
        TestCase.assertEquals(11, Long.numberOfTrailingZeros(2048));
        TestCase.assertEquals(8, Long.numberOfTrailingZeros(3840));
        TestCase.assertEquals(12, Long.numberOfTrailingZeros(4096));
        TestCase.assertEquals(15, Long.numberOfTrailingZeros(32768));
        TestCase.assertEquals(12, Long.numberOfTrailingZeros(61440));
        TestCase.assertEquals(16, Long.numberOfTrailingZeros(65536));
        TestCase.assertEquals(19, Long.numberOfTrailingZeros(524288));
        TestCase.assertEquals(16, Long.numberOfTrailingZeros(983040));
        TestCase.assertEquals(20, Long.numberOfTrailingZeros(1048576));
        TestCase.assertEquals(23, Long.numberOfTrailingZeros(8388608));
        TestCase.assertEquals(20, Long.numberOfTrailingZeros(15728640));
        TestCase.assertEquals(24, Long.numberOfTrailingZeros(16777216));
        TestCase.assertEquals(27, Long.numberOfTrailingZeros(134217728));
        TestCase.assertEquals(24, Long.numberOfTrailingZeros(251658240));
        TestCase.assertEquals(28, Long.numberOfTrailingZeros(268435456));
        TestCase.assertEquals(31, Long.numberOfTrailingZeros(-2147483648));
        TestCase.assertEquals(28, Long.numberOfTrailingZeros(-268435456));
    }

    /**
     * java.lang.Long#bitCount(long)
     */
    public void test_bitCountJ() {
        TestCase.assertEquals(0, Long.bitCount(0));
        TestCase.assertEquals(1, Long.bitCount(1));
        TestCase.assertEquals(1, Long.bitCount(2));
        TestCase.assertEquals(2, Long.bitCount(3));
        TestCase.assertEquals(1, Long.bitCount(4));
        TestCase.assertEquals(2, Long.bitCount(5));
        TestCase.assertEquals(2, Long.bitCount(6));
        TestCase.assertEquals(3, Long.bitCount(7));
        TestCase.assertEquals(1, Long.bitCount(8));
        TestCase.assertEquals(2, Long.bitCount(9));
        TestCase.assertEquals(2, Long.bitCount(10));
        TestCase.assertEquals(3, Long.bitCount(11));
        TestCase.assertEquals(2, Long.bitCount(12));
        TestCase.assertEquals(3, Long.bitCount(13));
        TestCase.assertEquals(3, Long.bitCount(14));
        TestCase.assertEquals(4, Long.bitCount(15));
        TestCase.assertEquals(8, Long.bitCount(255));
        TestCase.assertEquals(12, Long.bitCount(4095));
        TestCase.assertEquals(16, Long.bitCount(65535));
        TestCase.assertEquals(20, Long.bitCount(1048575));
        TestCase.assertEquals(24, Long.bitCount(16777215));
        TestCase.assertEquals(28, Long.bitCount(268435455));
        TestCase.assertEquals(64, Long.bitCount(-1L));
    }

    /**
     * java.lang.Long#rotateLeft(long, long)
     */
    public void test_rotateLeftJI() {
        TestCase.assertEquals(15, Long.rotateLeft(15, 0));
        TestCase.assertEquals(240, Long.rotateLeft(15, 4));
        TestCase.assertEquals(3840, Long.rotateLeft(15, 8));
        TestCase.assertEquals(61440, Long.rotateLeft(15, 12));
        TestCase.assertEquals(983040, Long.rotateLeft(15, 16));
        TestCase.assertEquals(15728640, Long.rotateLeft(15, 20));
        TestCase.assertEquals(251658240, Long.rotateLeft(15, 24));
        TestCase.assertEquals(4026531840L, Long.rotateLeft(15, 28));
        TestCase.assertEquals(-1152921504606846976L, Long.rotateLeft(-1152921504606846976L, 64));
    }

    /**
     * java.lang.Long#rotateRight(long, long)
     */
    public void test_rotateRightJI() {
        TestCase.assertEquals(15, Long.rotateRight(240, 4));
        TestCase.assertEquals(15, Long.rotateRight(3840, 8));
        TestCase.assertEquals(15, Long.rotateRight(61440, 12));
        TestCase.assertEquals(15, Long.rotateRight(983040, 16));
        TestCase.assertEquals(15, Long.rotateRight(15728640, 20));
        TestCase.assertEquals(15, Long.rotateRight(251658240, 24));
        TestCase.assertEquals(15, Long.rotateRight(4026531840L, 28));
        TestCase.assertEquals(-1152921504606846976L, Long.rotateRight(-1152921504606846976L, 64));
        TestCase.assertEquals(-1152921504606846976L, Long.rotateRight(-1152921504606846976L, 0));
    }

    /**
     * java.lang.Long#reverseBytes(long)
     */
    public void test_reverseBytesJ() {
        TestCase.assertEquals(-6144092017055948237L, Long.reverseBytes(3684526140561341354L));
        TestCase.assertEquals(1234605616436508552L, Long.reverseBytes(-8613303245920329199L));
        TestCase.assertEquals(4822678189205111L, Long.reverseBytes(8603657889541918976L));
        TestCase.assertEquals(2305843009213693954L, Long.reverseBytes(144115188075855904L));
    }

    /**
     * java.lang.Long#reverse(long)
     */
    public void test_reverseJ() {
        TestCase.assertEquals(0, Long.reverse(0));
        TestCase.assertEquals((-1), Long.reverse((-1)));
        TestCase.assertEquals(-9223372036854775808L, Long.reverse(1));
    }

    /**
     * java.lang.Long#signum(long)
     */
    public void test_signumJ() {
        for (int i = -128; i < 0; i++) {
            TestCase.assertEquals((-1), Long.signum(i));
        }
        TestCase.assertEquals(0, Long.signum(0));
        for (int i = 1; i <= 127; i++) {
            TestCase.assertEquals(1, Long.signum(i));
        }
    }
}

