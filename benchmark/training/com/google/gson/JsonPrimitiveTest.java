/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson;


import com.google.gson.common.MoreAsserts;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;


/**
 * Unit test for the {@link JsonPrimitive} class.
 *
 * @author Joel Leitch
 */
public class JsonPrimitiveTest extends TestCase {
    public void testNulls() {
        try {
            new JsonPrimitive(((Boolean) (null)));
            TestCase.fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new JsonPrimitive(((Number) (null)));
            TestCase.fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new JsonPrimitive(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new JsonPrimitive(((Character) (null)));
            TestCase.fail();
        } catch (NullPointerException ignored) {
        }
    }

    public void testBoolean() throws Exception {
        JsonPrimitive json = new JsonPrimitive(Boolean.TRUE);
        TestCase.assertTrue(json.isBoolean());
        TestCase.assertTrue(json.getAsBoolean());
        // Extra support for booleans
        json = new JsonPrimitive(1);
        TestCase.assertFalse(json.getAsBoolean());
        json = new JsonPrimitive("1");
        TestCase.assertFalse(json.getAsBoolean());
        json = new JsonPrimitive("true");
        TestCase.assertTrue(json.getAsBoolean());
        json = new JsonPrimitive("TrUe");
        TestCase.assertTrue(json.getAsBoolean());
        json = new JsonPrimitive("1.3");
        TestCase.assertFalse(json.getAsBoolean());
    }

    public void testParsingStringAsBoolean() throws Exception {
        JsonPrimitive json = new JsonPrimitive("true");
        TestCase.assertFalse(json.isBoolean());
        TestCase.assertTrue(json.getAsBoolean());
    }

    public void testParsingStringAsNumber() throws Exception {
        JsonPrimitive json = new JsonPrimitive("1");
        TestCase.assertFalse(json.isNumber());
        TestCase.assertEquals(1.0, json.getAsDouble(), 1.0E-5);
        TestCase.assertEquals(1.0F, json.getAsFloat(), 1.0E-5);
        TestCase.assertEquals(1, json.getAsInt());
        TestCase.assertEquals(1L, json.getAsLong());
        TestCase.assertEquals(((short) (1)), json.getAsShort());
        TestCase.assertEquals(((byte) (1)), json.getAsByte());
        TestCase.assertEquals(new BigInteger("1"), json.getAsBigInteger());
        TestCase.assertEquals(new BigDecimal("1"), json.getAsBigDecimal());
    }

    public void testStringsAndChar() throws Exception {
        JsonPrimitive json = new JsonPrimitive("abc");
        TestCase.assertTrue(json.isString());
        TestCase.assertEquals('a', json.getAsCharacter());
        TestCase.assertEquals("abc", json.getAsString());
        json = new JsonPrimitive('z');
        TestCase.assertTrue(json.isString());
        TestCase.assertEquals('z', json.getAsCharacter());
        TestCase.assertEquals("z", json.getAsString());
    }

    public void testExponential() throws Exception {
        JsonPrimitive json = new JsonPrimitive("1E+7");
        TestCase.assertEquals(new BigDecimal("1E+7"), json.getAsBigDecimal());
        TestCase.assertEquals(new Double("1E+7"), json.getAsDouble(), 1.0E-5);
        TestCase.assertEquals(new Float("1E+7"), json.getAsDouble(), 1.0E-5);
        try {
            json.getAsInt();
            TestCase.fail("Integers can not handle exponents like this.");
        } catch (NumberFormatException expected) {
        }
    }

    public void testByteEqualsShort() {
        JsonPrimitive p1 = new JsonPrimitive(new Byte(((byte) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new Short(((short) (10))));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testByteEqualsInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Byte(((byte) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new Integer(10));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testByteEqualsLong() {
        JsonPrimitive p1 = new JsonPrimitive(new Byte(((byte) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new Long(10L));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testByteEqualsBigInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Byte(((byte) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testShortEqualsInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Short(((short) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new Integer(10));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testShortEqualsLong() {
        JsonPrimitive p1 = new JsonPrimitive(new Short(((short) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new Long(10));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testShortEqualsBigInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Short(((short) (10))));
        JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testIntegerEqualsLong() {
        JsonPrimitive p1 = new JsonPrimitive(new Integer(10));
        JsonPrimitive p2 = new JsonPrimitive(new Long(10L));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testIntegerEqualsBigInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Integer(10));
        JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testLongEqualsBigInteger() {
        JsonPrimitive p1 = new JsonPrimitive(new Long(10L));
        JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testFloatEqualsDouble() {
        JsonPrimitive p1 = new JsonPrimitive(new Float(10.25F));
        JsonPrimitive p2 = new JsonPrimitive(new Double(10.25));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testFloatEqualsBigDecimal() {
        JsonPrimitive p1 = new JsonPrimitive(new Float(10.25F));
        JsonPrimitive p2 = new JsonPrimitive(new BigDecimal("10.25"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testDoubleEqualsBigDecimal() {
        JsonPrimitive p1 = new JsonPrimitive(new Double(10.25));
        JsonPrimitive p2 = new JsonPrimitive(new BigDecimal("10.25"));
        TestCase.assertEquals(p1, p2);
        TestCase.assertEquals(p1.hashCode(), p2.hashCode());
    }

    public void testValidJsonOnToString() throws Exception {
        JsonPrimitive json = new JsonPrimitive("Some\nEscaped\nValue");
        TestCase.assertEquals("\"Some\\nEscaped\\nValue\"", json.toString());
        json = new JsonPrimitive(new BigDecimal("1.333"));
        TestCase.assertEquals("1.333", json.toString());
    }

    public void testEquals() {
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive("A"), new JsonPrimitive("A"));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(true), new JsonPrimitive(true));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(5L), new JsonPrimitive(5L));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive('a'), new JsonPrimitive('a'));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NaN), new JsonPrimitive(Float.NaN));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NEGATIVE_INFINITY), new JsonPrimitive(Float.NEGATIVE_INFINITY));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.POSITIVE_INFINITY), new JsonPrimitive(Float.POSITIVE_INFINITY));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.NaN), new JsonPrimitive(Double.NaN));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.NEGATIVE_INFINITY), new JsonPrimitive(Double.NEGATIVE_INFINITY));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.POSITIVE_INFINITY), new JsonPrimitive(Double.POSITIVE_INFINITY));
        TestCase.assertFalse(new JsonPrimitive("a").equals(new JsonPrimitive("b")));
        TestCase.assertFalse(new JsonPrimitive(true).equals(new JsonPrimitive(false)));
        TestCase.assertFalse(new JsonPrimitive(0).equals(new JsonPrimitive(1)));
    }

    public void testEqualsAcrossTypes() {
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive("a"), new JsonPrimitive('a'));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(new BigInteger("0")), new JsonPrimitive(0));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(0), new JsonPrimitive(0L));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(new BigInteger("0")), new JsonPrimitive(0));
        MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NaN), new JsonPrimitive(Double.NaN));
    }

    public void testEqualsIntegerAndBigInteger() {
        JsonPrimitive a = new JsonPrimitive(5L);
        JsonPrimitive b = new JsonPrimitive(new BigInteger("18446744073709551621"));// 2^64 + 5

        // Ideally, the following assertion should have failed but the price is too much to pay
        // assertFalse(a + " equals " + b, a.equals(b));
        TestCase.assertTrue(((a + " equals ") + b), a.equals(b));
    }

    public void testEqualsDoesNotEquateStringAndNonStringTypes() {
        TestCase.assertFalse(new JsonPrimitive("true").equals(new JsonPrimitive(true)));
        TestCase.assertFalse(new JsonPrimitive("0").equals(new JsonPrimitive(0)));
        TestCase.assertFalse(new JsonPrimitive("NaN").equals(new JsonPrimitive(Float.NaN)));
    }

    public void testDeepCopy() {
        JsonPrimitive a = new JsonPrimitive("a");
        TestCase.assertSame(a, a.deepCopy());// Primitives are immutable!

    }
}

