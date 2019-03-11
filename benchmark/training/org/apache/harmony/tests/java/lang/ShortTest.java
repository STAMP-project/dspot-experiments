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


import junit.framework.TestCase;


public class ShortTest extends TestCase {
    private Short sp = new Short(((short) (18000)));

    private Short sn = new Short(((short) (-19000)));

    /**
     * java.lang.Short#byteValue()
     */
    public void test_byteValue() {
        // Test for method byte java.lang.Short.byteValue()
        TestCase.assertEquals("Returned incorrect byte value", 0, new Short(Short.MIN_VALUE).byteValue());
        TestCase.assertEquals("Returned incorrect byte value", (-1), new Short(Short.MAX_VALUE).byteValue());
    }

    /**
     * java.lang.Short#compareTo(java.lang.Short)
     */
    public void test_compareToLjava_lang_Short() {
        // Test for method int java.lang.Short.compareTo(java.lang.Short)
        Short s = new Short(((short) (1)));
        Short x = new Short(((short) (3)));
        TestCase.assertTrue("Should have returned negative value when compared to greater short", ((s.compareTo(x)) < 0));
        x = new Short(((short) (-1)));
        TestCase.assertTrue("Should have returned positive value when compared to lesser short", ((s.compareTo(x)) > 0));
        x = new Short(((short) (1)));
        TestCase.assertEquals("Should have returned zero when compared to equal short", 0, s.compareTo(x));
        try {
            new Short(((short) (0))).compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Short#decode(java.lang.String)
     */
    public void test_decodeLjava_lang_String2() {
        // Test for method java.lang.Short
        // java.lang.Short.decode(java.lang.String)
        TestCase.assertTrue("Did not decode -1 correctly", ((Short.decode("-1").shortValue()) == ((short) (-1))));
        TestCase.assertTrue("Did not decode -100 correctly", ((Short.decode("-100").shortValue()) == ((short) (-100))));
        TestCase.assertTrue("Did not decode 23 correctly", ((Short.decode("23").shortValue()) == ((short) (23))));
        TestCase.assertTrue("Did not decode 0x10 correctly", ((Short.decode("0x10").shortValue()) == ((short) (16))));
        TestCase.assertTrue("Did not decode 32767 correctly", ((Short.decode("32767").shortValue()) == ((short) (32767))));
        TestCase.assertTrue("Did not decode -32767 correctly", ((Short.decode("-32767").shortValue()) == ((short) (-32767))));
        TestCase.assertTrue("Did not decode -32768 correctly", ((Short.decode("-32768").shortValue()) == ((short) (-32768))));
        boolean exception = false;
        try {
            Short.decode("123s");
        } catch (NumberFormatException e) {
            // correct
            exception = true;
        }
        TestCase.assertTrue("Did not throw NumberFormatException decoding 123s", exception);
        exception = false;
        try {
            Short.decode("32768");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Short.decode("-32769");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Short.decode("0x8000");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Short.decode("-0x8001");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Short#parseShort(java.lang.String)
     */
    public void test_parseShortLjava_lang_String2() {
        // Test for method short java.lang.Short.parseShort(java.lang.String)
        short sp = Short.parseShort("32746");
        short sn = Short.parseShort("-32746");
        TestCase.assertTrue("Incorrect parse of short", ((sp == ((short) (32746))) && (sn == ((short) (-32746)))));
        TestCase.assertEquals("Returned incorrect value for 0", 0, Short.parseShort("0"));
        TestCase.assertTrue("Returned incorrect value for most negative value", ((Short.parseShort("-32768")) == ((short) (32768))));
        TestCase.assertTrue("Returned incorrect value for most positive value", ((Short.parseShort("32767")) == 32767));
        boolean exception = false;
        try {
            Short.parseShort("32768");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Short.parseShort("-32769");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Short#parseShort(java.lang.String, int)
     */
    public void test_parseShortLjava_lang_StringI2() {
        // Test for method short java.lang.Short.parseShort(java.lang.String,
        // int)
        boolean aThrow = true;
        TestCase.assertEquals("Incorrectly parsed hex string", 255, Short.parseShort("FF", 16));
        TestCase.assertEquals("Incorrectly parsed oct string", 16, Short.parseShort("20", 8));
        TestCase.assertEquals("Incorrectly parsed dec string", 20, Short.parseShort("20", 10));
        TestCase.assertEquals("Incorrectly parsed bin string", 4, Short.parseShort("100", 2));
        TestCase.assertEquals("Incorrectly parsed -hex string", (-255), Short.parseShort("-FF", 16));
        TestCase.assertEquals("Incorrectly parsed -oct string", (-16), Short.parseShort("-20", 8));
        TestCase.assertEquals("Incorrectly parsed -bin string", (-4), Short.parseShort("-100", 2));
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Short.parseShort("0", 16));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Short.parseShort("-8000", 16)) == ((short) (32768))));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Short.parseShort("7fff", 16)) == 32767));
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Short.parseShort("0", 10));
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Short.parseShort("-32768", 10)) == ((short) (32768))));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Short.parseShort("32767", 10)) == 32767));
        try {
            Short.parseShort("FF", 2);
        } catch (NumberFormatException e) {
            // Correct
            aThrow = false;
        }
        if (aThrow) {
            TestCase.fail("Failed to throw exception when passed hex string and base 2 radix");
        }
        boolean exception = false;
        try {
            Short.parseShort("10000000000", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception when passed string larger than 16 bits", exception);
        exception = false;
        try {
            Short.parseShort("32768", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Short.parseShort("-32769", 10);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Short.parseShort("8000", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Short.parseShort("-8001", 16);
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE + 1", exception);
    }

    /**
     * java.lang.Short#toString()
     */
    public void test_toString2() {
        // Test for method java.lang.String java.lang.Short.toString()
        TestCase.assertTrue("Invalid string returned", ((sp.toString().equals("18000")) && (sn.toString().equals("-19000"))));
        TestCase.assertEquals("Returned incorrect string", "32767", new Short(((short) (32767))).toString());
        TestCase.assertEquals("Returned incorrect string", "-32767", new Short(((short) (-32767))).toString());
        TestCase.assertEquals("Returned incorrect string", "-32768", new Short(((short) (-32768))).toString());
    }

    /**
     * java.lang.Short#toString(short)
     */
    public void test_toStringS2() {
        // Test for method java.lang.String java.lang.Short.toString(short)
        TestCase.assertEquals("Returned incorrect string", "32767", Short.toString(((short) (32767))));
        TestCase.assertEquals("Returned incorrect string", "-32767", Short.toString(((short) (-32767))));
        TestCase.assertEquals("Returned incorrect string", "-32768", Short.toString(((short) (-32768))));
    }

    /**
     * java.lang.Short#valueOf(java.lang.String)
     */
    public void test_valueOfLjava_lang_String2() {
        // Test for method java.lang.Short
        // java.lang.Short.valueOf(java.lang.String)
        TestCase.assertEquals("Returned incorrect short", (-32768), Short.valueOf("-32768").shortValue());
        TestCase.assertEquals("Returned incorrect short", 32767, Short.valueOf("32767").shortValue());
    }

    /**
     * java.lang.Short#valueOf(java.lang.String, int)
     */
    public void test_valueOfLjava_lang_StringI2() {
        // Test for method java.lang.Short
        // java.lang.Short.valueOf(java.lang.String, int)
        boolean aThrow = true;
        TestCase.assertEquals("Incorrectly parsed hex string", 255, Short.valueOf("FF", 16).shortValue());
        TestCase.assertEquals("Incorrectly parsed oct string", 16, Short.valueOf("20", 8).shortValue());
        TestCase.assertEquals("Incorrectly parsed dec string", 20, Short.valueOf("20", 10).shortValue());
        TestCase.assertEquals("Incorrectly parsed bin string", 4, Short.valueOf("100", 2).shortValue());
        TestCase.assertEquals("Incorrectly parsed -hex string", (-255), Short.valueOf("-FF", 16).shortValue());
        TestCase.assertEquals("Incorrectly parsed -oct string", (-16), Short.valueOf("-20", 8).shortValue());
        TestCase.assertEquals("Incorrectly parsed -bin string", (-4), Short.valueOf("-100", 2).shortValue());
        TestCase.assertTrue("Did not decode 32767 correctly", ((Short.valueOf("32767", 10).shortValue()) == ((short) (32767))));
        TestCase.assertTrue("Did not decode -32767 correctly", ((Short.valueOf("-32767", 10).shortValue()) == ((short) (-32767))));
        TestCase.assertTrue("Did not decode -32768 correctly", ((Short.valueOf("-32768", 10).shortValue()) == ((short) (-32768))));
        try {
            Short.valueOf("FF", 2);
        } catch (NumberFormatException e) {
            // Correct
            aThrow = false;
        }
        if (aThrow) {
            TestCase.fail("Failed to throw exception when passed hex string and base 2 radix");
        }
        try {
            Short.valueOf("10000000000", 10);
        } catch (NumberFormatException e) {
            // Correct
            return;
        }
        TestCase.fail("Failed to throw exception when passed string larger than 16 bits");
    }

    /**
     * java.lang.Short#valueOf(byte)
     */
    public void test_valueOfS() {
        TestCase.assertEquals(new Short(Short.MIN_VALUE), Short.valueOf(Short.MIN_VALUE));
        TestCase.assertEquals(new Short(Short.MAX_VALUE), Short.valueOf(Short.MAX_VALUE));
        TestCase.assertEquals(new Short(((short) (0))), Short.valueOf(((short) (0))));
        short s = -128;
        while (s < 128) {
            TestCase.assertEquals(new Short(s), Short.valueOf(s));
            TestCase.assertSame(Short.valueOf(s), Short.valueOf(s));
            s++;
        } 
    }

    /**
     * java.lang.Short#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(1, new Short(((short) (1))).hashCode());
        TestCase.assertEquals(2, new Short(((short) (2))).hashCode());
        TestCase.assertEquals(0, new Short(((short) (0))).hashCode());
        TestCase.assertEquals((-1), new Short(((short) (-1))).hashCode());
    }

    /**
     * java.lang.Short#Short(String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertEquals(new Short(((short) (0))), new Short("0"));
        TestCase.assertEquals(new Short(((short) (1))), new Short("1"));
        TestCase.assertEquals(new Short(((short) (-1))), new Short("-1"));
        try {
            new Short("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Short("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Short("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Short(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Short#Short(short)
     */
    public void test_ConstructorS() {
        TestCase.assertEquals(1, new Short(((short) (1))).shortValue());
        TestCase.assertEquals(2, new Short(((short) (2))).shortValue());
        TestCase.assertEquals(0, new Short(((short) (0))).shortValue());
        TestCase.assertEquals((-1), new Short(((short) (-1))).shortValue());
    }

    /**
     * java.lang.Short#byteValue()
     */
    public void test_booleanValue() {
        TestCase.assertEquals(1, new Short(((short) (1))).byteValue());
        TestCase.assertEquals(2, new Short(((short) (2))).byteValue());
        TestCase.assertEquals(0, new Short(((short) (0))).byteValue());
        TestCase.assertEquals((-1), new Short(((short) (-1))).byteValue());
    }

    /**
     * java.lang.Short#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertEquals(new Short(((short) (0))), Short.valueOf(((short) (0))));
        TestCase.assertEquals(new Short(((short) (1))), Short.valueOf(((short) (1))));
        TestCase.assertEquals(new Short(((short) (-1))), Short.valueOf(((short) (-1))));
        Short fixture = new Short(((short) (25)));
        TestCase.assertEquals(fixture, fixture);
        TestCase.assertFalse(fixture.equals(null));
        TestCase.assertFalse(fixture.equals("Not a Short"));
    }

    /**
     * java.lang.Short#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("-1", new Short(((short) (-1))).toString());
        TestCase.assertEquals("0", new Short(((short) (0))).toString());
        TestCase.assertEquals("1", new Short(((short) (1))).toString());
        TestCase.assertEquals("-1", new Short(((short) (65535))).toString());
    }

    /**
     * java.lang.Short#toString(short)
     */
    public void test_toStringS() {
        TestCase.assertEquals("-1", Short.toString(((short) (-1))));
        TestCase.assertEquals("0", Short.toString(((short) (0))));
        TestCase.assertEquals("1", Short.toString(((short) (1))));
        TestCase.assertEquals("-1", Short.toString(((short) (65535))));
    }

    /**
     * java.lang.Short#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(new Short(((short) (0))), Short.valueOf("0"));
        TestCase.assertEquals(new Short(((short) (1))), Short.valueOf("1"));
        TestCase.assertEquals(new Short(((short) (-1))), Short.valueOf("-1"));
        try {
            Short.valueOf("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Short#valueOf(String, int)
     */
    public void test_valueOfLjava_lang_StringI() {
        TestCase.assertEquals(new Short(((short) (0))), Short.valueOf("0", 10));
        TestCase.assertEquals(new Short(((short) (1))), Short.valueOf("1", 10));
        TestCase.assertEquals(new Short(((short) (-1))), Short.valueOf("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Short.valueOf("1", 2).byteValue());
        TestCase.assertEquals(Character.digit('F', 16), Short.valueOf("F", 16).byteValue());
        try {
            Short.valueOf("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.valueOf(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Short#parseShort(String)
     */
    public void test_parseShortLjava_lang_String() {
        TestCase.assertEquals(0, Short.parseShort("0"));
        TestCase.assertEquals(1, Short.parseShort("1"));
        TestCase.assertEquals((-1), Short.parseShort("-1"));
        try {
            Short.parseShort("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Short#parseShort(String, int)
     */
    public void test_parseShortLjava_lang_StringI() {
        TestCase.assertEquals(0, Short.parseShort("0", 10));
        TestCase.assertEquals(1, Short.parseShort("1", 10));
        TestCase.assertEquals((-1), Short.parseShort("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Short.parseShort("1", 2));
        TestCase.assertEquals(Character.digit('F', 16), Short.parseShort("F", 16));
        try {
            Short.parseShort("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.parseShort(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Short#decode(String)
     */
    public void test_decodeLjava_lang_String() {
        TestCase.assertEquals(new Short(((short) (0))), Short.decode("0"));
        TestCase.assertEquals(new Short(((short) (1))), Short.decode("1"));
        TestCase.assertEquals(new Short(((short) (-1))), Short.decode("-1"));
        TestCase.assertEquals(new Short(((short) (15))), Short.decode("0xF"));
        TestCase.assertEquals(new Short(((short) (15))), Short.decode("#F"));
        TestCase.assertEquals(new Short(((short) (15))), Short.decode("0XF"));
        TestCase.assertEquals(new Short(((short) (7))), Short.decode("07"));
        try {
            Short.decode("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.decode("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Short.decode(null);
            // undocumented NPE, but seems consistent across JREs
            TestCase.fail("Expected NullPointerException with null string.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Short#doubleValue()
     */
    public void test_doubleValue() {
        TestCase.assertEquals((-1.0), new Short(((short) (-1))).doubleValue(), 0.0);
        TestCase.assertEquals(0.0, new Short(((short) (0))).doubleValue(), 0.0);
        TestCase.assertEquals(1.0, new Short(((short) (1))).doubleValue(), 0.0);
    }

    /**
     * java.lang.Short#floatValue()
     */
    public void test_floatValue() {
        TestCase.assertEquals((-1.0F), new Short(((short) (-1))).floatValue(), 0.0F);
        TestCase.assertEquals(0.0F, new Short(((short) (0))).floatValue(), 0.0F);
        TestCase.assertEquals(1.0F, new Short(((short) (1))).floatValue(), 0.0F);
    }

    /**
     * java.lang.Short#intValue()
     */
    public void test_intValue() {
        TestCase.assertEquals((-1), new Short(((short) (-1))).intValue());
        TestCase.assertEquals(0, new Short(((short) (0))).intValue());
        TestCase.assertEquals(1, new Short(((short) (1))).intValue());
    }

    /**
     * java.lang.Short#longValue()
     */
    public void test_longValue() {
        TestCase.assertEquals((-1L), new Short(((short) (-1))).longValue());
        TestCase.assertEquals(0L, new Short(((short) (0))).longValue());
        TestCase.assertEquals(1L, new Short(((short) (1))).longValue());
    }

    /**
     * java.lang.Short#shortValue()
     */
    public void test_shortValue() {
        TestCase.assertEquals((-1), new Short(((short) (-1))).shortValue());
        TestCase.assertEquals(0, new Short(((short) (0))).shortValue());
        TestCase.assertEquals(1, new Short(((short) (1))).shortValue());
    }

    /**
     * java.lang.Short#reverseBytes(short)
     */
    public void test_reverseBytesS() {
        TestCase.assertEquals(((short) (43981)), Short.reverseBytes(((short) (52651))));
        TestCase.assertEquals(((short) (4660)), Short.reverseBytes(((short) (13330))));
        TestCase.assertEquals(((short) (17)), Short.reverseBytes(((short) (4352))));
        TestCase.assertEquals(((short) (8194)), Short.reverseBytes(((short) (544))));
    }
}

