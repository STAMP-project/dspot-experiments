/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.json;


import JSONObject.NULL;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import junit.framework.TestCase;


/**
 * This black box test was written without inspecting the non-free org.json sourcecode.
 */
public class JSONObjectTest extends TestCase {
    public void testEmptyObject() throws JSONException {
        JSONObject object = new JSONObject();
        TestCase.assertEquals(0, object.length());
        // bogus (but documented) behaviour: returns null rather than the empty object!
        TestCase.assertNull(object.names());
        // returns null rather than an empty array!
        TestCase.assertNull(object.toJSONArray(new JSONArray()));
        TestCase.assertEquals("{}", object.toString());
        TestCase.assertEquals("{}", object.toString(5));
        try {
            object.get("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getBoolean("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getDouble("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getInt("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getJSONArray("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getJSONObject("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getLong("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getString("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertFalse(object.has("foo"));
        TestCase.assertTrue(object.isNull("foo"));// isNull also means "is not present"

        TestCase.assertNull(object.opt("foo"));
        TestCase.assertEquals(false, object.optBoolean("foo"));
        TestCase.assertEquals(true, object.optBoolean("foo", true));
        TestCase.assertEquals(Double.NaN, object.optDouble("foo"));
        TestCase.assertEquals(5.0, object.optDouble("foo", 5.0));
        TestCase.assertEquals(0, object.optInt("foo"));
        TestCase.assertEquals(5, object.optInt("foo", 5));
        TestCase.assertEquals(null, object.optJSONArray("foo"));
        TestCase.assertEquals(null, object.optJSONObject("foo"));
        TestCase.assertEquals(0, object.optLong("foo"));
        TestCase.assertEquals(((Long.MAX_VALUE) - 1), object.optLong("foo", ((Long.MAX_VALUE) - 1)));
        TestCase.assertEquals("", object.optString("foo"));// empty string is default!

        TestCase.assertEquals("bar", object.optString("foo", "bar"));
        TestCase.assertNull(object.remove("foo"));
    }

    public void testEqualsAndHashCode() throws JSONException {
        JSONObject a = new JSONObject();
        JSONObject b = new JSONObject();
        // JSON object doesn't override either equals or hashCode (!)
        TestCase.assertFalse(a.equals(b));
        TestCase.assertEquals(a.hashCode(), System.identityHashCode(a));
    }

    public void testGet() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", value);
        object.put("bar", new Object());
        object.put("baz", new Object());
        TestCase.assertSame(value, object.get("foo"));
        try {
            object.get("FOO");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put(null, value);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.get(null);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testPut() throws JSONException {
        JSONObject object = new JSONObject();
        TestCase.assertSame(object, object.put("foo", true));
        object.put("foo", false);
        TestCase.assertEquals(false, object.get("foo"));
        object.put("foo", 5.0);
        TestCase.assertEquals(5.0, object.get("foo"));
        object.put("foo", 0);
        TestCase.assertEquals(0, object.get("foo"));
        object.put("bar", ((Long.MAX_VALUE) - 1));
        TestCase.assertEquals(((Long.MAX_VALUE) - 1), object.get("bar"));
        object.put("baz", "x");
        TestCase.assertEquals("x", object.get("baz"));
        object.put("bar", NULL);
        TestCase.assertSame(NULL, object.get("bar"));
    }

    public void testPutNullRemoves() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.put("foo", ((Collection) (null)));
        TestCase.assertEquals(0, object.length());
        TestCase.assertFalse(object.has("foo"));
        try {
            object.get("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testPutOpt() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.putOpt("foo", null);
        TestCase.assertEquals("bar", object.get("foo"));
        object.putOpt(null, null);
        TestCase.assertEquals(1, object.length());
        object.putOpt(null, "bar");
        TestCase.assertEquals(1, object.length());
    }

    public void testPutOptUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("foo", Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.putOpt("foo", Double.NEGATIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.putOpt("foo", Double.POSITIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testRemove() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        TestCase.assertEquals(null, object.remove(null));
        TestCase.assertEquals(null, object.remove(""));
        TestCase.assertEquals(null, object.remove("bar"));
        TestCase.assertEquals("bar", object.remove("foo"));
        TestCase.assertEquals(null, object.remove("foo"));
    }

    public void testBooleans() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", false);
        object.put("baz", "true");
        object.put("quux", "false");
        TestCase.assertEquals(4, object.length());
        TestCase.assertEquals(true, object.getBoolean("foo"));
        TestCase.assertEquals(false, object.getBoolean("bar"));
        TestCase.assertEquals(true, object.getBoolean("baz"));
        TestCase.assertEquals(false, object.getBoolean("quux"));
        TestCase.assertFalse(object.isNull("foo"));
        TestCase.assertFalse(object.isNull("quux"));
        TestCase.assertTrue(object.has("foo"));
        TestCase.assertTrue(object.has("quux"));
        TestCase.assertFalse(object.has("missing"));
        TestCase.assertEquals(true, object.optBoolean("foo"));
        TestCase.assertEquals(false, object.optBoolean("bar"));
        TestCase.assertEquals(true, object.optBoolean("baz"));
        TestCase.assertEquals(false, object.optBoolean("quux"));
        TestCase.assertEquals(false, object.optBoolean("missing"));
        TestCase.assertEquals(true, object.optBoolean("foo", true));
        TestCase.assertEquals(false, object.optBoolean("bar", true));
        TestCase.assertEquals(true, object.optBoolean("baz", true));
        TestCase.assertEquals(false, object.optBoolean("quux", true));
        TestCase.assertEquals(true, object.optBoolean("missing", true));
        object.put("foo", "truE");
        object.put("bar", "FALSE");
        TestCase.assertEquals(true, object.getBoolean("foo"));
        TestCase.assertEquals(false, object.getBoolean("bar"));
        TestCase.assertEquals(true, object.optBoolean("foo"));
        TestCase.assertEquals(false, object.optBoolean("bar"));
        TestCase.assertEquals(true, object.optBoolean("foo", false));
        TestCase.assertEquals(false, object.optBoolean("bar", false));
    }

    // http://code.google.com/p/android/issues/detail?id=16411
    public void testCoerceStringToBoolean() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "maybe");
        try {
            object.getBoolean("foo");
            TestCase.fail();
        } catch (JSONException expected) {
        }
        TestCase.assertEquals(false, object.optBoolean("foo"));
        TestCase.assertEquals(true, object.optBoolean("foo", true));
    }

    public void testNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", Double.MIN_VALUE);
        object.put("bar", 9223372036854775806L);
        object.put("baz", Double.MAX_VALUE);
        object.put("quux", (-0.0));
        TestCase.assertEquals(4, object.length());
        String toString = object.toString();
        TestCase.assertTrue(toString, toString.contains("\"foo\":4.9E-324"));
        TestCase.assertTrue(toString, toString.contains("\"bar\":9223372036854775806"));
        TestCase.assertTrue(toString, toString.contains("\"baz\":1.7976931348623157E308"));
        // toString() and getString() return different values for -0d!
        TestCase.assertTrue(toString, ((toString.contains("\"quux\":-0}"))// no trailing decimal point
         || (toString.contains("\"quux\":-0,"))));
        TestCase.assertEquals(Double.MIN_VALUE, object.get("foo"));
        TestCase.assertEquals(9223372036854775806L, object.get("bar"));
        TestCase.assertEquals(Double.MAX_VALUE, object.get("baz"));
        TestCase.assertEquals((-0.0), object.get("quux"));
        TestCase.assertEquals(Double.MIN_VALUE, object.getDouble("foo"));
        TestCase.assertEquals(9.223372036854776E18, object.getDouble("bar"));
        TestCase.assertEquals(Double.MAX_VALUE, object.getDouble("baz"));
        TestCase.assertEquals((-0.0), object.getDouble("quux"));
        TestCase.assertEquals(0, object.getLong("foo"));
        TestCase.assertEquals(9223372036854775806L, object.getLong("bar"));
        TestCase.assertEquals(Long.MAX_VALUE, object.getLong("baz"));
        TestCase.assertEquals(0, object.getLong("quux"));
        TestCase.assertEquals(0, object.getInt("foo"));
        TestCase.assertEquals((-2), object.getInt("bar"));
        TestCase.assertEquals(Integer.MAX_VALUE, object.getInt("baz"));
        TestCase.assertEquals(0, object.getInt("quux"));
        TestCase.assertEquals(Double.MIN_VALUE, object.opt("foo"));
        TestCase.assertEquals(9223372036854775806L, object.optLong("bar"));
        TestCase.assertEquals(Double.MAX_VALUE, object.optDouble("baz"));
        TestCase.assertEquals(0, object.optInt("quux"));
        TestCase.assertEquals(Double.MIN_VALUE, object.opt("foo"));
        TestCase.assertEquals(9223372036854775806L, object.optLong("bar"));
        TestCase.assertEquals(Double.MAX_VALUE, object.optDouble("baz"));
        TestCase.assertEquals(0, object.optInt("quux"));
        TestCase.assertEquals(Double.MIN_VALUE, object.optDouble("foo", 5.0));
        TestCase.assertEquals(9223372036854775806L, object.optLong("bar", 1L));
        TestCase.assertEquals(Long.MAX_VALUE, object.optLong("baz", 1L));
        TestCase.assertEquals(0, object.optInt("quux", (-1)));
        TestCase.assertEquals("4.9E-324", object.getString("foo"));
        TestCase.assertEquals("9223372036854775806", object.getString("bar"));
        TestCase.assertEquals("1.7976931348623157E308", object.getString("baz"));
        TestCase.assertEquals("-0.0", object.getString("quux"));
    }

    public void testFloats() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", ((Float) (Float.NaN)));
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", ((Float) (Float.NEGATIVE_INFINITY)));
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", ((Float) (Float.POSITIVE_INFINITY)));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testOtherNumbers() throws JSONException {
        Number nan = new Number() {
            public int intValue() {
                throw new UnsupportedOperationException();
            }

            public long longValue() {
                throw new UnsupportedOperationException();
            }

            public float floatValue() {
                throw new UnsupportedOperationException();
            }

            public double doubleValue() {
                return Double.NaN;
            }

            @Override
            public String toString() {
                return "x";
            }
        };
        JSONObject object = new JSONObject();
        try {
            object.put("foo", nan);
            TestCase.fail("Object.put() accepted a NaN (via a custom Number class)");
        } catch (JSONException e) {
        }
    }

    public void testForeignObjects() throws JSONException {
        Object foreign = new Object() {
            @Override
            public String toString() {
                return "x";
            }
        };
        // foreign object types are accepted and treated as Strings!
        JSONObject object = new JSONObject();
        object.put("foo", foreign);
        TestCase.assertEquals("{\"foo\":\"x\"}", object.toString());
    }

    public void testNullKeys() {
        try {
            new JSONObject().put(null, false);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            new JSONObject().put(null, 0.0);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            new JSONObject().put(null, 5);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            new JSONObject().put(null, 5L);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            new JSONObject().put(null, "foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testStrings() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "true");
        object.put("bar", "5.5");
        object.put("baz", "9223372036854775806");
        object.put("quux", "null");
        object.put("height", "5\"8\' tall");
        TestCase.assertTrue(object.toString().contains("\"foo\":\"true\""));
        TestCase.assertTrue(object.toString().contains("\"bar\":\"5.5\""));
        TestCase.assertTrue(object.toString().contains("\"baz\":\"9223372036854775806\""));
        TestCase.assertTrue(object.toString().contains("\"quux\":\"null\""));
        TestCase.assertTrue(object.toString().contains("\"height\":\"5\\\"8\' tall\""));
        TestCase.assertEquals("true", object.get("foo"));
        TestCase.assertEquals("null", object.getString("quux"));
        TestCase.assertEquals("5\"8\' tall", object.getString("height"));
        TestCase.assertEquals("true", object.opt("foo"));
        TestCase.assertEquals("5.5", object.optString("bar"));
        TestCase.assertEquals("true", object.optString("foo", "x"));
        TestCase.assertFalse(object.isNull("foo"));
        TestCase.assertEquals(true, object.getBoolean("foo"));
        TestCase.assertEquals(true, object.optBoolean("foo"));
        TestCase.assertEquals(true, object.optBoolean("foo", false));
        TestCase.assertEquals(0, object.optInt("foo"));
        TestCase.assertEquals((-2), object.optInt("foo", (-2)));
        TestCase.assertEquals(5.5, object.getDouble("bar"));
        TestCase.assertEquals(5L, object.getLong("bar"));
        TestCase.assertEquals(5, object.getInt("bar"));
        TestCase.assertEquals(5, object.optInt("bar", 3));
        // The last digit of the string is a 6 but getLong returns a 7. It's probably parsing as a
        // double and then converting that to a long. This is consistent with JavaScript.
        TestCase.assertEquals(9223372036854775807L, object.getLong("baz"));
        TestCase.assertEquals(9.223372036854776E18, object.getDouble("baz"));
        TestCase.assertEquals(Integer.MAX_VALUE, object.getInt("baz"));
        TestCase.assertFalse(object.isNull("quux"));
        try {
            object.getDouble("quux");
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals(Double.NaN, object.optDouble("quux"));
        TestCase.assertEquals((-1.0), object.optDouble("quux", (-1.0)));
        object.put("foo", "TRUE");
        TestCase.assertEquals(true, object.getBoolean("foo"));
    }

    public void testJSONObjects() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray a = new JSONArray();
        JSONObject b = new JSONObject();
        object.put("foo", a);
        object.put("bar", b);
        TestCase.assertSame(a, object.getJSONArray("foo"));
        TestCase.assertSame(b, object.getJSONObject("bar"));
        try {
            object.getJSONObject("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.getJSONArray("bar");
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals(a, object.optJSONArray("foo"));
        TestCase.assertEquals(b, object.optJSONObject("bar"));
        TestCase.assertEquals(null, object.optJSONArray("bar"));
        TestCase.assertEquals(null, object.optJSONObject("foo"));
    }

    public void testNullCoercionToString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", NULL);
        TestCase.assertEquals("null", object.getString("foo"));
    }

    public void testArrayCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "[true]");
        try {
            object.getJSONArray("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testObjectCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "{}");
        try {
            object.getJSONObject("foo");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testAccumulateValueChecking() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.accumulate("foo", Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        object.accumulate("foo", 1);
        try {
            object.accumulate("foo", Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        object.accumulate("foo", 2);
        try {
            object.accumulate("foo", Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testToJSONArray() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", true);
        object.put("bar", 5.0);
        object.put("baz", (-0.0));
        object.put("quux", value);
        JSONArray names = new JSONArray();
        names.put("baz");
        names.put("quux");
        names.put("foo");
        JSONArray array = object.toJSONArray(names);
        TestCase.assertEquals((-0.0), array.get(0));
        TestCase.assertEquals(value, array.get(1));
        TestCase.assertEquals(true, array.get(2));
        object.put("foo", false);
        TestCase.assertEquals(true, array.get(2));
    }

    public void testToJSONArrayMissingNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", 5.0);
        object.put("baz", NULL);
        JSONArray names = new JSONArray();
        names.put("bar");
        names.put("foo");
        names.put("quux");
        names.put("baz");
        JSONArray array = object.toJSONArray(names);
        TestCase.assertEquals(4, array.length());
        TestCase.assertEquals(5.0, array.get(0));
        TestCase.assertEquals(true, array.get(1));
        try {
            array.get(2);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals(NULL, array.get(3));
    }

    public void testToJSONArrayNull() throws JSONException {
        JSONObject object = new JSONObject();
        TestCase.assertEquals(null, object.toJSONArray(null));
        object.put("foo", 5);
        try {
            object.toJSONArray(null);
        } catch (JSONException e) {
        }
    }

    public void testToJSONArrayEndsUpEmpty() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        JSONArray array = new JSONArray();
        array.put("bar");
        TestCase.assertEquals(1, object.toJSONArray(array).length());
    }

    public void testToJSONArrayNonString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("null", 10);
        object.put("false", 15);
        JSONArray names = new JSONArray();
        names.put(NULL);
        names.put(false);
        names.put("foo");
        // array elements are converted to strings to do name lookups on the map!
        JSONArray array = object.toJSONArray(names);
        TestCase.assertEquals(3, array.length());
        TestCase.assertEquals(10, array.get(0));
        TestCase.assertEquals(15, array.get(1));
        TestCase.assertEquals(5, array.get(2));
    }

    public void testPutUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", Double.NEGATIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", Double.POSITIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testPutUnsupportedNumbersAsObjects() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", ((Double) (Double.NaN)));
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", ((Double) (Double.NEGATIVE_INFINITY)));
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            object.put("foo", ((Double) (Double.POSITIVE_INFINITY)));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    /**
     * Although JSONObject is usually defensive about which numbers it accepts,
     * it doesn't check inputs in its constructor.
     */
    public void testCreateWithUnsupportedNumbers() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", Double.NaN);
        contents.put("bar", Double.NEGATIVE_INFINITY);
        contents.put("baz", Double.POSITIVE_INFINITY);
        JSONObject object = new JSONObject(contents);
        TestCase.assertEquals(Double.NaN, object.get("foo"));
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, object.get("bar"));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, object.get("baz"));
    }

    public void testToStringWithUnsupportedNumbers() {
        // when the object contains an unsupported number, toString returns null!
        JSONObject object = new JSONObject(Collections.singletonMap("foo", Double.NaN));
        TestCase.assertEquals(null, object.toString());
    }

    public void testMapConstructorCopiesContents() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", 5);
        JSONObject object = new JSONObject(contents);
        contents.put("foo", 10);
        TestCase.assertEquals(5, object.get("foo"));
    }

    public void testMapConstructorWithBogusEntries() {
        Map<Object, Object> contents = new HashMap<Object, Object>();
        contents.put(5, 5);
        try {
            new JSONObject(contents);
            TestCase.fail("JSONObject constructor doesn't validate its input!");
        } catch (Exception e) {
        }
    }

    public void testTokenerConstructor() throws JSONException {
        JSONObject object = new JSONObject(new JSONTokener("{\"foo\": false}"));
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(false, object.get("foo"));
    }

    public void testTokenerConstructorWrongType() throws JSONException {
        try {
            new JSONObject(new JSONTokener("[\"foo\", false]"));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testTokenerConstructorNull() throws JSONException {
        try {
            new JSONObject(((JSONTokener) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testTokenerConstructorParseFail() {
        try {
            new JSONObject(new JSONTokener("{"));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testStringConstructor() throws JSONException {
        JSONObject object = new JSONObject("{\"foo\": false}");
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(false, object.get("foo"));
    }

    public void testStringConstructorWrongType() throws JSONException {
        try {
            new JSONObject("[\"foo\", false]");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testStringConstructorNull() throws JSONException {
        try {
            new JSONObject(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testStringConstructorParseFail() {
        try {
            new JSONObject("{");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testCopyConstructor() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", NULL);
        source.put("b", false);
        source.put("c", 5);
        JSONObject copy = new JSONObject(source, new String[]{ "a", "c" });
        TestCase.assertEquals(2, copy.length());
        TestCase.assertEquals(NULL, copy.get("a"));
        TestCase.assertEquals(5, copy.get("c"));
        TestCase.assertEquals(null, copy.opt("b"));
    }

    public void testCopyConstructorMissingName() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", NULL);
        source.put("b", false);
        source.put("c", 5);
        JSONObject copy = new JSONObject(source, new String[]{ "a", "c", "d" });
        TestCase.assertEquals(2, copy.length());
        TestCase.assertEquals(NULL, copy.get("a"));
        TestCase.assertEquals(5, copy.get("c"));
        TestCase.assertEquals(0, copy.optInt("b"));
    }

    public void testAccumulateMutatesInPlace() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.accumulate("foo", 6);
        JSONArray array = object.getJSONArray("foo");
        TestCase.assertEquals("[5,6]", array.toString());
        object.accumulate("foo", 7);
        TestCase.assertEquals("[5,6,7]", array.toString());
    }

    public void testAccumulateExistingArray() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("foo", array);
        object.accumulate("foo", 5);
        TestCase.assertEquals("[5]", array.toString());
    }

    public void testAccumulatePutArray() throws JSONException {
        JSONObject object = new JSONObject();
        object.accumulate("foo", 5);
        TestCase.assertEquals("{\"foo\":5}", object.toString());
        object.accumulate("foo", new JSONArray());
        TestCase.assertEquals("{\"foo\":[5,[]]}", object.toString());
    }

    public void testAccumulateNull() {
        JSONObject object = new JSONObject();
        try {
            object.accumulate(null, 5);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testEmptyStringKey() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("", 5);
        TestCase.assertEquals(5, object.get(""));
        TestCase.assertEquals("{\"\":5}", object.toString());
    }

    public void testNullValue() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", NULL);
        object.put("bar", ((Collection) (null)));
        // there are two ways to represent null; each behaves differently!
        TestCase.assertTrue(object.has("foo"));
        TestCase.assertFalse(object.has("bar"));
        TestCase.assertTrue(object.isNull("foo"));
        TestCase.assertTrue(object.isNull("bar"));
    }

    public void testHas() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        TestCase.assertTrue(object.has("foo"));
        TestCase.assertFalse(object.has("bar"));
        TestCase.assertFalse(object.has(null));
    }

    public void testOptNull() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        TestCase.assertEquals(null, object.opt(null));
        TestCase.assertEquals(false, object.optBoolean(null));
        TestCase.assertEquals(Double.NaN, object.optDouble(null));
        TestCase.assertEquals(0, object.optInt(null));
        TestCase.assertEquals(0L, object.optLong(null));
        TestCase.assertEquals(null, object.optJSONArray(null));
        TestCase.assertEquals(null, object.optJSONObject(null));
        TestCase.assertEquals("", object.optString(null));
        TestCase.assertEquals(true, object.optBoolean(null, true));
        TestCase.assertEquals(0.0, object.optDouble(null, 0.0));
        TestCase.assertEquals(1, object.optInt(null, 1));
        TestCase.assertEquals(1L, object.optLong(null, 1L));
        TestCase.assertEquals("baz", object.optString(null, "baz"));
    }

    public void testToStringWithIndentFactor() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", new JSONArray(Arrays.asList(5, 6)));
        object.put("bar", new JSONObject());
        String foobar = "{\n" + ((((("     \"foo\": [\n" + "          5,\n") + "          6\n") + "     ],\n") + "     \"bar\": {}\n") + "}");
        String barfoo = "{\n" + ((((("     \"bar\": {},\n" + "     \"foo\": [\n") + "          5,\n") + "          6\n") + "     ]\n") + "}");
        String string = object.toString(5);
        TestCase.assertTrue(string, ((foobar.equals(string)) || (barfoo.equals(string))));
    }

    public void testNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("baz", 7);
        JSONArray array = object.names();
        TestCase.assertTrue(array.toString().contains("foo"));
        TestCase.assertTrue(array.toString().contains("bar"));
        TestCase.assertTrue(array.toString().contains("baz"));
    }

    public void testKeysEmptyObject() {
        JSONObject object = new JSONObject();
        TestCase.assertFalse(object.keys().hasNext());
        try {
            object.keys().next();
            TestCase.fail();
        } catch (NoSuchElementException e) {
        }
    }

    public void testKeys() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("foo", 7);
        @SuppressWarnings("unchecked")
        Iterator<String> keys = ((Iterator<String>) (object.keys()));
        Set<String> result = new HashSet<String>();
        TestCase.assertTrue(keys.hasNext());
        result.add(keys.next());
        TestCase.assertTrue(keys.hasNext());
        result.add(keys.next());
        TestCase.assertFalse(keys.hasNext());
        TestCase.assertEquals(new HashSet<String>(Arrays.asList("foo", "bar")), result);
        try {
            keys.next();
            TestCase.fail();
        } catch (NoSuchElementException e) {
        }
    }

    public void testMutatingKeysMutatesObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        Iterator keys = object.keys();
        keys.next();
        keys.remove();
        TestCase.assertEquals(0, object.length());
    }

    public void testQuote() {
        // covered by JSONStringerTest.testEscaping
    }

    public void testQuoteNull() throws JSONException {
        TestCase.assertEquals("\"\"", JSONObject.quote(null));
    }

    public void testNumberToString() throws JSONException {
        TestCase.assertEquals("5", JSONObject.numberToString(5));
        TestCase.assertEquals("-0", JSONObject.numberToString((-0.0)));
        TestCase.assertEquals("9223372036854775806", JSONObject.numberToString(9223372036854775806L));
        TestCase.assertEquals("4.9E-324", JSONObject.numberToString(Double.MIN_VALUE));
        TestCase.assertEquals("1.7976931348623157E308", JSONObject.numberToString(Double.MAX_VALUE));
        try {
            JSONObject.numberToString(Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            JSONObject.numberToString(Double.NEGATIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            JSONObject.numberToString(Double.POSITIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals("0.001", JSONObject.numberToString(new BigDecimal("0.001")));
        TestCase.assertEquals("9223372036854775806", JSONObject.numberToString(new BigInteger("9223372036854775806")));
        try {
            JSONObject.numberToString(null);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void test_wrap() throws Exception {
        TestCase.assertEquals(NULL, JSONObject.wrap(null));
        JSONArray a = new JSONArray();
        TestCase.assertEquals(a, JSONObject.wrap(a));
        JSONObject o = new JSONObject();
        TestCase.assertEquals(o, JSONObject.wrap(o));
        TestCase.assertEquals(NULL, JSONObject.wrap(NULL));
        TestCase.assertTrue(((JSONObject.wrap(new byte[0])) instanceof JSONArray));
        TestCase.assertTrue(((JSONObject.wrap(new ArrayList<String>())) instanceof JSONArray));
        TestCase.assertTrue(((JSONObject.wrap(new HashMap<String, String>())) instanceof JSONObject));
        TestCase.assertTrue(((JSONObject.wrap(Double.valueOf(0))) instanceof Double));
        TestCase.assertTrue(((JSONObject.wrap("hello")) instanceof String));
        TestCase.assertTrue(((JSONObject.wrap(Selector.open())) instanceof String));
    }

    // https://code.google.com/p/android/issues/detail?id=55114
    public void test_toString_listAsMapValue() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("a");
        list.add(new ArrayList<String>());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("x", "l");
        map.put("y", list);
        TestCase.assertEquals("{\"y\":[\"a\",[]],\"x\":\"l\"}", new JSONObject(map).toString());
    }
}

