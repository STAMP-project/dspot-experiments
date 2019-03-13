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


public class ByteTest extends TestCase {
    /**
     * java.lang.Byte#valueOf(byte)
     */
    public void test_valueOfB() {
        TestCase.assertEquals(new Byte(Byte.MIN_VALUE), Byte.valueOf(Byte.MIN_VALUE));
        TestCase.assertEquals(new Byte(Byte.MAX_VALUE), Byte.valueOf(Byte.MAX_VALUE));
        TestCase.assertEquals(new Byte(((byte) (0))), Byte.valueOf(((byte) (0))));
        byte b = (Byte.MIN_VALUE) + 1;
        while (b < (Byte.MAX_VALUE)) {
            TestCase.assertEquals(new Byte(b), Byte.valueOf(b));
            TestCase.assertSame(Byte.valueOf(b), Byte.valueOf(b));
            b++;
        } 
    }

    /**
     * java.lang.Byte#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(1, new Byte(((byte) (1))).hashCode());
        TestCase.assertEquals(2, new Byte(((byte) (2))).hashCode());
        TestCase.assertEquals(0, new Byte(((byte) (0))).hashCode());
        TestCase.assertEquals((-1), new Byte(((byte) (-1))).hashCode());
    }

    /**
     * java.lang.Byte#Byte(String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertEquals(new Byte(((byte) (0))), new Byte("0"));
        TestCase.assertEquals(new Byte(((byte) (1))), new Byte("1"));
        TestCase.assertEquals(new Byte(((byte) (-1))), new Byte("-1"));
        try {
            new Byte("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Byte("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Byte("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            new Byte(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#Byte(byte)
     */
    public void test_ConstructorB() {
        TestCase.assertEquals(1, new Byte(((byte) (1))).byteValue());
        TestCase.assertEquals(2, new Byte(((byte) (2))).byteValue());
        TestCase.assertEquals(0, new Byte(((byte) (0))).byteValue());
        TestCase.assertEquals((-1), new Byte(((byte) (-1))).byteValue());
    }

    /**
     * java.lang.Byte#byteValue()
     */
    public void test_booleanValue() {
        TestCase.assertEquals(1, new Byte(((byte) (1))).byteValue());
        TestCase.assertEquals(2, new Byte(((byte) (2))).byteValue());
        TestCase.assertEquals(0, new Byte(((byte) (0))).byteValue());
        TestCase.assertEquals((-1), new Byte(((byte) (-1))).byteValue());
    }

    /**
     * java.lang.Byte#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertEquals(new Byte(((byte) (0))), Byte.valueOf(((byte) (0))));
        TestCase.assertEquals(new Byte(((byte) (1))), Byte.valueOf(((byte) (1))));
        TestCase.assertEquals(new Byte(((byte) (-1))), Byte.valueOf(((byte) (-1))));
        Byte fixture = new Byte(((byte) (25)));
        TestCase.assertEquals(fixture, fixture);
        TestCase.assertFalse(fixture.equals(null));
        TestCase.assertFalse(fixture.equals("Not a Byte"));
    }

    /**
     * java.lang.Byte#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("-1", new Byte(((byte) (-1))).toString());
        TestCase.assertEquals("0", new Byte(((byte) (0))).toString());
        TestCase.assertEquals("1", new Byte(((byte) (1))).toString());
        TestCase.assertEquals("-1", new Byte(((byte) (255))).toString());
    }

    /**
     * java.lang.Byte#toString(byte)
     */
    public void test_toStringB() {
        TestCase.assertEquals("-1", Byte.toString(((byte) (-1))));
        TestCase.assertEquals("0", Byte.toString(((byte) (0))));
        TestCase.assertEquals("1", Byte.toString(((byte) (1))));
        TestCase.assertEquals("-1", Byte.toString(((byte) (255))));
    }

    /**
     * java.lang.Byte#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(new Byte(((byte) (0))), Byte.valueOf("0"));
        TestCase.assertEquals(new Byte(((byte) (1))), Byte.valueOf("1"));
        TestCase.assertEquals(new Byte(((byte) (-1))), Byte.valueOf("-1"));
        try {
            Byte.valueOf("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#valueOf(String, int)
     */
    public void test_valueOfLjava_lang_StringI() {
        TestCase.assertEquals(new Byte(((byte) (0))), Byte.valueOf("0", 10));
        TestCase.assertEquals(new Byte(((byte) (1))), Byte.valueOf("1", 10));
        TestCase.assertEquals(new Byte(((byte) (-1))), Byte.valueOf("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Byte.valueOf("1", 2).byteValue());
        TestCase.assertEquals(Character.digit('F', 16), Byte.valueOf("F", 16).byteValue());
        try {
            Byte.valueOf("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.valueOf(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#parseByte(String)
     */
    public void test_parseByteLjava_lang_String() {
        TestCase.assertEquals(0, Byte.parseByte("0"));
        TestCase.assertEquals(1, Byte.parseByte("1"));
        TestCase.assertEquals((-1), Byte.parseByte("-1"));
        try {
            Byte.parseByte("0x1");
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte(null);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#parseByte(String, int)
     */
    public void test_parseByteLjava_lang_StringI() {
        TestCase.assertEquals(0, Byte.parseByte("0", 10));
        TestCase.assertEquals(1, Byte.parseByte("1", 10));
        TestCase.assertEquals((-1), Byte.parseByte("-1", 10));
        // must be consistent with Character.digit()
        TestCase.assertEquals(Character.digit('1', 2), Byte.parseByte("1", 2));
        TestCase.assertEquals(Character.digit('F', 16), Byte.parseByte("F", 16));
        try {
            Byte.parseByte("0x1", 10);
            TestCase.fail("Expected NumberFormatException with hex string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("9.2", 10);
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("", 10);
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte(null, 10);
            TestCase.fail("Expected NumberFormatException with null string.");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#decode(String)
     */
    public void test_decodeLjava_lang_String() {
        TestCase.assertEquals(new Byte(((byte) (0))), Byte.decode("0"));
        TestCase.assertEquals(new Byte(((byte) (1))), Byte.decode("1"));
        TestCase.assertEquals(new Byte(((byte) (-1))), Byte.decode("-1"));
        TestCase.assertEquals(new Byte(((byte) (15))), Byte.decode("0xF"));
        TestCase.assertEquals(new Byte(((byte) (15))), Byte.decode("#F"));
        TestCase.assertEquals(new Byte(((byte) (15))), Byte.decode("0XF"));
        TestCase.assertEquals(new Byte(((byte) (7))), Byte.decode("07"));
        try {
            Byte.decode("9.2");
            TestCase.fail("Expected NumberFormatException with floating point string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.decode("");
            TestCase.fail("Expected NumberFormatException with empty string.");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.decode(null);
            // undocumented NPE, but seems consistent across JREs
            TestCase.fail("Expected NullPointerException with null string.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Byte#doubleValue()
     */
    public void test_doubleValue() {
        TestCase.assertEquals((-1.0), new Byte(((byte) (-1))).doubleValue(), 0.0);
        TestCase.assertEquals(0.0, new Byte(((byte) (0))).doubleValue(), 0.0);
        TestCase.assertEquals(1.0, new Byte(((byte) (1))).doubleValue(), 0.0);
    }

    /**
     * java.lang.Byte#floatValue()
     */
    public void test_floatValue() {
        TestCase.assertEquals((-1.0F), new Byte(((byte) (-1))).floatValue(), 0.0F);
        TestCase.assertEquals(0.0F, new Byte(((byte) (0))).floatValue(), 0.0F);
        TestCase.assertEquals(1.0F, new Byte(((byte) (1))).floatValue(), 0.0F);
    }

    /**
     * java.lang.Byte#intValue()
     */
    public void test_intValue() {
        TestCase.assertEquals((-1), new Byte(((byte) (-1))).intValue());
        TestCase.assertEquals(0, new Byte(((byte) (0))).intValue());
        TestCase.assertEquals(1, new Byte(((byte) (1))).intValue());
    }

    /**
     * java.lang.Byte#longValue()
     */
    public void test_longValue() {
        TestCase.assertEquals((-1L), new Byte(((byte) (-1))).longValue());
        TestCase.assertEquals(0L, new Byte(((byte) (0))).longValue());
        TestCase.assertEquals(1L, new Byte(((byte) (1))).longValue());
    }

    /**
     * java.lang.Byte#shortValue()
     */
    public void test_shortValue() {
        TestCase.assertEquals((-1), new Byte(((byte) (-1))).shortValue());
        TestCase.assertEquals(0, new Byte(((byte) (0))).shortValue());
        TestCase.assertEquals(1, new Byte(((byte) (1))).shortValue());
    }

    /**
     * java.lang.Byte#compareTo(Byte)
     */
    public void test_compareToLjava_lang_Byte() {
        final Byte min = new Byte(Byte.MIN_VALUE);
        final Byte zero = new Byte(((byte) (0)));
        final Byte max = new Byte(Byte.MAX_VALUE);
        TestCase.assertTrue(((max.compareTo(max)) == 0));
        TestCase.assertTrue(((min.compareTo(min)) == 0));
        TestCase.assertTrue(((zero.compareTo(zero)) == 0));
        TestCase.assertTrue(((max.compareTo(zero)) > 0));
        TestCase.assertTrue(((max.compareTo(min)) > 0));
        TestCase.assertTrue(((zero.compareTo(max)) < 0));
        TestCase.assertTrue(((zero.compareTo(min)) > 0));
        TestCase.assertTrue(((min.compareTo(zero)) < 0));
        TestCase.assertTrue(((min.compareTo(max)) < 0));
        try {
            min.compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Byte#Byte(byte)
     */
    public void test_ConstructorB2() {
        // Test for method java.lang.Byte(byte)
        Byte b = new Byte(((byte) (127)));
        TestCase.assertTrue("Byte creation failed", ((b.byteValue()) == ((byte) (127))));
    }

    /**
     * java.lang.Byte#Byte(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String2() {
        // Test for method java.lang.Byte(java.lang.String)
        Byte b = new Byte("127");
        Byte nb = new Byte("-128");
        TestCase.assertTrue("Incorrect Byte Object created", (((b.byteValue()) == ((byte) (127))) && ((nb.byteValue()) == ((byte) (-128)))));
    }

    /**
     * java.lang.Byte#byteValue()
     */
    public void test_byteValue() {
        // Test for method byte java.lang.Byte.byteValue()
        TestCase.assertTrue("Returned incorrect byte value", ((new Byte(((byte) (127))).byteValue()) == ((byte) (127))));
    }

    /**
     * java.lang.Byte#compareTo(java.lang.Byte)
     */
    public void test_compareToLjava_lang_Byte2() {
        // Test for method int java.lang.Byte.compareTo(java.lang.Byte)
        TestCase.assertTrue("Comparison failed", ((new Byte(((byte) (1))).compareTo(new Byte(((byte) (2))))) < 0));
        TestCase.assertTrue("Comparison failed", ((new Byte(((byte) (1))).compareTo(new Byte(((byte) (-2))))) > 0));
        TestCase.assertEquals("Comparison failed", 0, new Byte(((byte) (1))).compareTo(new Byte(((byte) (1)))));
    }

    /**
     * java.lang.Byte#decode(java.lang.String)
     */
    public void test_decodeLjava_lang_String2() {
        // Test for method java.lang.Byte
        // java.lang.Byte.decode(java.lang.String)
        TestCase.assertTrue(("String decoded incorrectly, wanted: 1 got: " + (Byte.decode("1").toString())), Byte.decode("1").equals(new Byte(((byte) (1)))));
        TestCase.assertTrue(("String decoded incorrectly, wanted: -1 got: " + (Byte.decode("-1").toString())), Byte.decode("-1").equals(new Byte(((byte) (-1)))));
        TestCase.assertTrue(("String decoded incorrectly, wanted: 127 got: " + (Byte.decode("127").toString())), Byte.decode("127").equals(new Byte(((byte) (127)))));
        TestCase.assertTrue(("String decoded incorrectly, wanted: -128 got: " + (Byte.decode("-128").toString())), Byte.decode("-128").equals(new Byte(((byte) (-128)))));
        TestCase.assertTrue(("String decoded incorrectly, wanted: 127 got: " + (Byte.decode("0x7f").toString())), Byte.decode("0x7f").equals(new Byte(((byte) (127)))));
        TestCase.assertTrue(("String decoded incorrectly, wanted: -128 got: " + (Byte.decode("-0x80").toString())), Byte.decode("-0x80").equals(new Byte(((byte) (-128)))));
        boolean exception = false;
        try {
            Byte.decode("128");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MAX_VALUE + 1", exception);
        exception = false;
        try {
            Byte.decode("-129");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for MIN_VALUE - 1", exception);
        exception = false;
        try {
            Byte.decode("0x80");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MAX_VALUE + 1", exception);
        exception = false;
        try {
            Byte.decode("-0x81");
        } catch (NumberFormatException e) {
            // Correct
            exception = true;
        }
        TestCase.assertTrue("Failed to throw exception for hex MIN_VALUE - 1", exception);
    }

    /**
     * java.lang.Byte#doubleValue()
     */
    public void test_doubleValue2() {
        TestCase.assertEquals(127.0, new Byte(((byte) (127))).doubleValue(), 0.0);
    }

    /**
     * java.lang.Byte#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object2() {
        // Test for method boolean java.lang.Byte.equals(java.lang.Object)
        Byte b1 = new Byte(((byte) (90)));
        Byte b2 = new Byte(((byte) (90)));
        Byte b3 = new Byte(((byte) (-90)));
        TestCase.assertTrue("Equality test failed", b1.equals(b2));
        TestCase.assertTrue("Equality test failed", (!(b1.equals(b3))));
    }

    /**
     * java.lang.Byte#floatValue()
     */
    public void test_floatValue2() {
        TestCase.assertEquals(127.0F, new Byte(((byte) (127))).floatValue(), 0.0);
    }

    /**
     * java.lang.Byte#hashCode()
     */
    public void test_hashCode2() {
        // Test for method int java.lang.Byte.hashCode()
        TestCase.assertEquals("Incorrect hash returned", 127, new Byte(((byte) (127))).hashCode());
    }

    /**
     * java.lang.Byte#intValue()
     */
    public void test_intValue2() {
        // Test for method int java.lang.Byte.intValue()
        TestCase.assertEquals("Returned incorrect int value", 127, new Byte(((byte) (127))).intValue());
    }

    /**
     * java.lang.Byte#longValue()
     */
    public void test_longValue2() {
        // Test for method long java.lang.Byte.longValue()
        TestCase.assertEquals("Returned incorrect long value", 127L, new Byte(((byte) (127))).longValue());
    }

    /**
     * java.lang.Byte#parseByte(java.lang.String)
     */
    public void test_parseByteLjava_lang_String2() {
        TestCase.assertEquals(((byte) (127)), Byte.parseByte("127"));
        TestCase.assertEquals(((byte) (-128)), Byte.parseByte("-128"));
        TestCase.assertEquals(((byte) (0)), Byte.parseByte("0"));
        TestCase.assertEquals(((byte) (128)), Byte.parseByte("-128"));
        TestCase.assertEquals(((byte) (127)), Byte.parseByte("127"));
        try {
            Byte.parseByte("-1000");
            TestCase.fail("No NumberFormatException");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("128");
            TestCase.fail("No NumberFormatException");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("-129");
            TestCase.fail("No NumberFormatException");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#parseByte(java.lang.String, int)
     */
    public void test_parseByteLjava_lang_StringI2() {
        // Test for method byte java.lang.Byte.parseByte(java.lang.String, int)
        byte b = Byte.parseByte("127", 10);
        byte bn = Byte.parseByte("-128", 10);
        TestCase.assertTrue("Invalid parse of dec byte", ((b == ((byte) (127))) && (bn == ((byte) (-128)))));
        TestCase.assertEquals("Failed to parse hex value", 10, Byte.parseByte("A", 16));
        TestCase.assertEquals("Returned incorrect value for 0 hex", 0, Byte.parseByte("0", 16));
        TestCase.assertTrue("Returned incorrect value for most negative value hex", ((Byte.parseByte("-80", 16)) == ((byte) (128))));
        TestCase.assertTrue("Returned incorrect value for most positive value hex", ((Byte.parseByte("7f", 16)) == 127));
        TestCase.assertEquals("Returned incorrect value for 0 decimal", 0, Byte.parseByte("0", 10));
        TestCase.assertTrue("Returned incorrect value for most negative value decimal", ((Byte.parseByte("-128", 10)) == ((byte) (128))));
        TestCase.assertTrue("Returned incorrect value for most positive value decimal", ((Byte.parseByte("127", 10)) == 127));
        try {
            Byte.parseByte("-1000", 10);
            TestCase.fail("Failed to throw exception");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("128", 10);
            TestCase.fail("Failed to throw exception for MAX_VALUE + 1");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("-129", 10);
            TestCase.fail("Failed to throw exception for MIN_VALUE - 1");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("80", 16);
            TestCase.fail("Failed to throw exception for hex MAX_VALUE + 1");
        } catch (NumberFormatException e) {
        }
        try {
            Byte.parseByte("-81", 16);
            TestCase.fail("Failed to throw exception for hex MIN_VALUE + 1");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#shortValue()
     */
    public void test_shortValue2() {
        TestCase.assertEquals(((short) (127)), new Byte(((byte) (127))).shortValue());
    }

    /**
     * java.lang.Byte#toString()
     */
    public void test_toString2() {
        TestCase.assertEquals("Returned incorrect String", "127", new Byte(((byte) (127))).toString());
        TestCase.assertEquals("Returned incorrect String", "-127", new Byte(((byte) (-127))).toString());
        TestCase.assertEquals("Returned incorrect String", "-128", new Byte(((byte) (-128))).toString());
    }

    /**
     * java.lang.Byte#toString(byte)
     */
    public void test_toStringB2() {
        TestCase.assertEquals("Returned incorrect String", "127", Byte.toString(((byte) (127))));
        TestCase.assertEquals("Returned incorrect String", "-127", Byte.toString(((byte) (-127))));
        TestCase.assertEquals("Returned incorrect String", "-128", Byte.toString(((byte) (-128))));
    }

    /**
     * java.lang.Byte#valueOf(java.lang.String)
     */
    public void test_valueOfLjava_lang_String2() {
        TestCase.assertEquals("Returned incorrect byte", 0, Byte.valueOf("0").byteValue());
        TestCase.assertEquals("Returned incorrect byte", 127, Byte.valueOf("127").byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-127), Byte.valueOf("-127").byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-128), Byte.valueOf("-128").byteValue());
        try {
            Byte.valueOf("128");
            TestCase.fail("Failed to throw exception when passes value > byte");
        } catch (NumberFormatException e) {
        }
    }

    /**
     * java.lang.Byte#valueOf(java.lang.String, int)
     */
    public void test_valueOfLjava_lang_StringI2() {
        TestCase.assertEquals("Returned incorrect byte", 10, Byte.valueOf("A", 16).byteValue());
        TestCase.assertEquals("Returned incorrect byte", 127, Byte.valueOf("127", 10).byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-127), Byte.valueOf("-127", 10).byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-128), Byte.valueOf("-128", 10).byteValue());
        TestCase.assertEquals("Returned incorrect byte", 127, Byte.valueOf("7f", 16).byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-127), Byte.valueOf("-7f", 16).byteValue());
        TestCase.assertEquals("Returned incorrect byte", (-128), Byte.valueOf("-80", 16).byteValue());
        try {
            Byte.valueOf("128", 10);
            TestCase.fail("Failed to throw exception when passes value > byte");
        } catch (NumberFormatException e) {
        }
    }
}

