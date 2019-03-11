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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


/**
 * This black box test was written without inspecting the non-free org.json sourcecode.
 */
public class JSONArrayTest extends TestCase {
    public void testEmptyArray() throws JSONException {
        JSONArray array = new JSONArray();
        TestCase.assertEquals(0, array.length());
        TestCase.assertEquals("", array.join(" AND "));
        try {
            array.get(0);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.getBoolean(0);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals("[]", array.toString());
        TestCase.assertEquals("[]", array.toString(4));
        // out of bounds is co-opted with defaulting
        TestCase.assertTrue(array.isNull(0));
        TestCase.assertNull(array.opt(0));
        TestCase.assertFalse(array.optBoolean(0));
        TestCase.assertTrue(array.optBoolean(0, true));
        // bogus (but documented) behaviour: returns null rather than an empty object!
        TestCase.assertNull(array.toJSONObject(new JSONArray()));
    }

    public void testEqualsAndHashCode() throws JSONException {
        JSONArray a = new JSONArray();
        JSONArray b = new JSONArray();
        TestCase.assertTrue(a.equals(b));
        TestCase.assertEquals("equals() not consistent with hashCode()", a.hashCode(), b.hashCode());
        a.put(true);
        a.put(false);
        b.put(true);
        b.put(false);
        TestCase.assertTrue(a.equals(b));
        TestCase.assertEquals(a.hashCode(), b.hashCode());
        b.put(true);
        TestCase.assertFalse(a.equals(b));
        TestCase.assertTrue(((a.hashCode()) != (b.hashCode())));
    }

    public void testBooleans() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(true);
        array.put(false);
        array.put(2, false);
        array.put(3, false);
        array.put(2, true);
        TestCase.assertEquals("[true,false,true,false]", array.toString());
        TestCase.assertEquals(4, array.length());
        TestCase.assertEquals(Boolean.TRUE, array.get(0));
        TestCase.assertEquals(Boolean.FALSE, array.get(1));
        TestCase.assertEquals(Boolean.TRUE, array.get(2));
        TestCase.assertEquals(Boolean.FALSE, array.get(3));
        TestCase.assertFalse(array.isNull(0));
        TestCase.assertFalse(array.isNull(1));
        TestCase.assertFalse(array.isNull(2));
        TestCase.assertFalse(array.isNull(3));
        TestCase.assertEquals(true, array.optBoolean(0));
        TestCase.assertEquals(false, array.optBoolean(1, true));
        TestCase.assertEquals(true, array.optBoolean(2, false));
        TestCase.assertEquals(false, array.optBoolean(3));
        TestCase.assertEquals("true", array.getString(0));
        TestCase.assertEquals("false", array.getString(1));
        TestCase.assertEquals("true", array.optString(2));
        TestCase.assertEquals("false", array.optString(3, "x"));
        TestCase.assertEquals("[\n     true,\n     false,\n     true,\n     false\n]", array.toString(5));
        JSONArray other = new JSONArray();
        other.put(true);
        other.put(false);
        other.put(true);
        other.put(false);
        TestCase.assertTrue(array.equals(other));
        other.put(true);
        TestCase.assertFalse(array.equals(other));
        other = new JSONArray();
        other.put("true");
        other.put("false");
        other.put("truE");
        other.put("FALSE");
        TestCase.assertFalse(array.equals(other));
        TestCase.assertFalse(other.equals(array));
        TestCase.assertEquals(true, other.getBoolean(0));
        TestCase.assertEquals(false, other.optBoolean(1, true));
        TestCase.assertEquals(true, other.optBoolean(2));
        TestCase.assertEquals(false, other.getBoolean(3));
    }

    // http://code.google.com/p/android/issues/detail?id=16411
    public void testCoerceStringToBoolean() throws JSONException {
        JSONArray array = new JSONArray();
        array.put("maybe");
        try {
            array.getBoolean(0);
            TestCase.fail();
        } catch (JSONException expected) {
        }
        TestCase.assertEquals(false, array.optBoolean(0));
        TestCase.assertEquals(true, array.optBoolean(0, true));
    }

    public void testNulls() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(3, ((Collection) (null)));
        array.put(0, NULL);
        TestCase.assertEquals(4, array.length());
        TestCase.assertEquals("[null,null,null,null]", array.toString());
        // there's 2 ways to represent null; each behaves differently!
        TestCase.assertEquals(NULL, array.get(0));
        try {
            array.get(1);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.get(2);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.get(3);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals(NULL, array.opt(0));
        TestCase.assertEquals(null, array.opt(1));
        TestCase.assertEquals(null, array.opt(2));
        TestCase.assertEquals(null, array.opt(3));
        TestCase.assertTrue(array.isNull(0));
        TestCase.assertTrue(array.isNull(1));
        TestCase.assertTrue(array.isNull(2));
        TestCase.assertTrue(array.isNull(3));
        TestCase.assertEquals("null", array.optString(0));
        TestCase.assertEquals("", array.optString(1));
        TestCase.assertEquals("", array.optString(2));
        TestCase.assertEquals("", array.optString(3));
    }

    /**
     * Our behaviour is questioned by this bug:
     * http://code.google.com/p/android/issues/detail?id=7257
     */
    public void testParseNullYieldsJSONObjectNull() throws JSONException {
        JSONArray array = new JSONArray("[\"null\",null]");
        array.put(((Collection) (null)));
        TestCase.assertEquals("null", array.get(0));
        TestCase.assertEquals(NULL, array.get(1));
        try {
            array.get(2);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals("null", array.getString(0));
        TestCase.assertEquals("null", array.getString(1));
        try {
            array.getString(2);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testNumbers() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(Double.MIN_VALUE);
        array.put(9223372036854775806L);
        array.put(Double.MAX_VALUE);
        array.put((-0.0));
        TestCase.assertEquals(4, array.length());
        // toString() and getString(int) return different values for -0d
        TestCase.assertEquals("[4.9E-324,9223372036854775806,1.7976931348623157E308,-0]", array.toString());
        TestCase.assertEquals(Double.MIN_VALUE, array.get(0));
        TestCase.assertEquals(9223372036854775806L, array.get(1));
        TestCase.assertEquals(Double.MAX_VALUE, array.get(2));
        TestCase.assertEquals((-0.0), array.get(3));
        TestCase.assertEquals(Double.MIN_VALUE, array.getDouble(0));
        TestCase.assertEquals(9.223372036854776E18, array.getDouble(1));
        TestCase.assertEquals(Double.MAX_VALUE, array.getDouble(2));
        TestCase.assertEquals((-0.0), array.getDouble(3));
        TestCase.assertEquals(0, array.getLong(0));
        TestCase.assertEquals(9223372036854775806L, array.getLong(1));
        TestCase.assertEquals(Long.MAX_VALUE, array.getLong(2));
        TestCase.assertEquals(0, array.getLong(3));
        TestCase.assertEquals(0, array.getInt(0));
        TestCase.assertEquals((-2), array.getInt(1));
        TestCase.assertEquals(Integer.MAX_VALUE, array.getInt(2));
        TestCase.assertEquals(0, array.getInt(3));
        TestCase.assertEquals(Double.MIN_VALUE, array.opt(0));
        TestCase.assertEquals(Double.MIN_VALUE, array.optDouble(0));
        TestCase.assertEquals(0, array.optLong(0, 1L));
        TestCase.assertEquals(0, array.optInt(0, 1));
        TestCase.assertEquals("4.9E-324", array.getString(0));
        TestCase.assertEquals("9223372036854775806", array.getString(1));
        TestCase.assertEquals("1.7976931348623157E308", array.getString(2));
        TestCase.assertEquals("-0.0", array.getString(3));
        JSONArray other = new JSONArray();
        other.put(Double.MIN_VALUE);
        other.put(9223372036854775806L);
        other.put(Double.MAX_VALUE);
        other.put((-0.0));
        TestCase.assertTrue(array.equals(other));
        other.put(0, 0L);
        TestCase.assertFalse(array.equals(other));
    }

    public void testStrings() throws JSONException {
        JSONArray array = new JSONArray();
        array.put("true");
        array.put("5.5");
        array.put("9223372036854775806");
        array.put("null");
        array.put("5\"8\' tall");
        TestCase.assertEquals(5, array.length());
        TestCase.assertEquals("[\"true\",\"5.5\",\"9223372036854775806\",\"null\",\"5\\\"8\' tall\"]", array.toString());
        // although the documentation doesn't mention it, join() escapes text and wraps
        // strings in quotes
        TestCase.assertEquals("\"true\" \"5.5\" \"9223372036854775806\" \"null\" \"5\\\"8\' tall\"", array.join(" "));
        TestCase.assertEquals("true", array.get(0));
        TestCase.assertEquals("null", array.getString(3));
        TestCase.assertEquals("5\"8\' tall", array.getString(4));
        TestCase.assertEquals("true", array.opt(0));
        TestCase.assertEquals("5.5", array.optString(1));
        TestCase.assertEquals("9223372036854775806", array.optString(2, null));
        TestCase.assertEquals("null", array.optString(3, "-1"));
        TestCase.assertFalse(array.isNull(0));
        TestCase.assertFalse(array.isNull(3));
        TestCase.assertEquals(true, array.getBoolean(0));
        TestCase.assertEquals(true, array.optBoolean(0));
        TestCase.assertEquals(true, array.optBoolean(0, false));
        TestCase.assertEquals(0, array.optInt(0));
        TestCase.assertEquals((-2), array.optInt(0, (-2)));
        TestCase.assertEquals(5.5, array.getDouble(1));
        TestCase.assertEquals(5L, array.getLong(1));
        TestCase.assertEquals(5, array.getInt(1));
        TestCase.assertEquals(5, array.optInt(1, 3));
        // The last digit of the string is a 6 but getLong returns a 7. It's probably parsing as a
        // double and then converting that to a long. This is consistent with JavaScript.
        TestCase.assertEquals(9223372036854775807L, array.getLong(2));
        TestCase.assertEquals(9.223372036854776E18, array.getDouble(2));
        TestCase.assertEquals(Integer.MAX_VALUE, array.getInt(2));
        TestCase.assertFalse(array.isNull(3));
        try {
            array.getDouble(3);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals(Double.NaN, array.optDouble(3));
        TestCase.assertEquals((-1.0), array.optDouble(3, (-1.0)));
    }

    public void testJoin() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(((Collection) (null)));
        TestCase.assertEquals("null", array.join(" & "));
        array.put("\"");
        TestCase.assertEquals("null & \"\\\"\"", array.join(" & "));
        array.put(5);
        TestCase.assertEquals("null & \"\\\"\" & 5", array.join(" & "));
        array.put(true);
        TestCase.assertEquals("null & \"\\\"\" & 5 & true", array.join(" & "));
        array.put(new JSONArray(Arrays.asList(true, false)));
        TestCase.assertEquals("null & \"\\\"\" & 5 & true & [true,false]", array.join(" & "));
        array.put(new JSONObject(Collections.singletonMap("x", 6)));
        TestCase.assertEquals("null & \"\\\"\" & 5 & true & [true,false] & {\"x\":6}", array.join(" & "));
    }

    public void testJoinWithNull() throws JSONException {
        JSONArray array = new JSONArray(Arrays.asList(5, 6));
        TestCase.assertEquals("5null6", array.join(null));
    }

    public void testJoinWithSpecialCharacters() throws JSONException {
        JSONArray array = new JSONArray(Arrays.asList(5, 6));
        TestCase.assertEquals("5\"6", array.join("\""));
    }

    public void testToJSONObject() throws JSONException {
        JSONArray keys = new JSONArray();
        keys.put("a");
        keys.put("b");
        JSONArray values = new JSONArray();
        values.put(5.5);
        values.put(false);
        JSONObject object = values.toJSONObject(keys);
        TestCase.assertEquals(5.5, object.get("a"));
        TestCase.assertEquals(false, object.get("b"));
        keys.put(0, "a");
        values.put(0, 11.0);
        TestCase.assertEquals(5.5, object.get("a"));
    }

    public void testToJSONObjectWithNulls() throws JSONException {
        JSONArray keys = new JSONArray();
        keys.put("a");
        keys.put("b");
        JSONArray values = new JSONArray();
        values.put(5.5);
        values.put(((Collection) (null)));
        // null values are stripped!
        JSONObject object = values.toJSONObject(keys);
        TestCase.assertEquals(1, object.length());
        TestCase.assertFalse(object.has("b"));
        TestCase.assertEquals("{\"a\":5.5}", object.toString());
    }

    public void testToJSONObjectMoreNamesThanValues() throws JSONException {
        JSONArray keys = new JSONArray();
        keys.put("a");
        keys.put("b");
        JSONArray values = new JSONArray();
        values.put(5.5);
        JSONObject object = values.toJSONObject(keys);
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(5.5, object.get("a"));
    }

    public void testToJSONObjectMoreValuesThanNames() throws JSONException {
        JSONArray keys = new JSONArray();
        keys.put("a");
        JSONArray values = new JSONArray();
        values.put(5.5);
        values.put(11.0);
        JSONObject object = values.toJSONObject(keys);
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(5.5, object.get("a"));
    }

    public void testToJSONObjectNullKey() throws JSONException {
        JSONArray keys = new JSONArray();
        keys.put(NULL);
        JSONArray values = new JSONArray();
        values.put(5.5);
        JSONObject object = values.toJSONObject(keys);
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(5.5, object.get("null"));
    }

    public void testPutUnsupportedNumbers() throws JSONException {
        JSONArray array = new JSONArray();
        try {
            array.put(Double.NaN);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.put(0, Double.NEGATIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.put(0, Double.POSITIVE_INFINITY);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testPutUnsupportedNumbersAsObject() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(Double.valueOf(Double.NaN));
        array.put(Double.valueOf(Double.NEGATIVE_INFINITY));
        array.put(Double.valueOf(Double.POSITIVE_INFINITY));
        TestCase.assertEquals(null, array.toString());
    }

    /**
     * Although JSONArray is usually defensive about which numbers it accepts,
     * it doesn't check inputs in its constructor.
     */
    public void testCreateWithUnsupportedNumbers() throws JSONException {
        JSONArray array = new JSONArray(Arrays.asList(5.5, Double.NaN));
        TestCase.assertEquals(2, array.length());
        TestCase.assertEquals(5.5, array.getDouble(0));
        TestCase.assertEquals(Double.NaN, array.getDouble(1));
    }

    public void testToStringWithUnsupportedNumbers() throws JSONException {
        // when the array contains an unsupported number, toString returns null!
        JSONArray array = new JSONArray(Arrays.asList(5.5, Double.NaN));
        TestCase.assertNull(array.toString());
    }

    public void testListConstructorCopiesContents() throws JSONException {
        List<Object> contents = Arrays.<Object>asList(5);
        JSONArray array = new JSONArray(contents);
        contents.set(0, 10);
        TestCase.assertEquals(5, array.get(0));
    }

    public void testTokenerConstructor() throws JSONException {
        JSONArray object = new JSONArray(new JSONTokener("[false]"));
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(false, object.get(0));
    }

    public void testTokenerConstructorWrongType() throws JSONException {
        try {
            new JSONArray(new JSONTokener("{\"foo\": false}"));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testTokenerConstructorNull() throws JSONException {
        try {
            new JSONArray(((JSONTokener) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testTokenerConstructorParseFail() {
        try {
            new JSONArray(new JSONTokener("["));
            TestCase.fail();
        } catch (JSONException e) {
        } catch (StackOverflowError e) {
            TestCase.fail("Stack overflowed on input: \"[\"");
        }
    }

    public void testStringConstructor() throws JSONException {
        JSONArray object = new JSONArray("[false]");
        TestCase.assertEquals(1, object.length());
        TestCase.assertEquals(false, object.get(0));
    }

    public void testStringConstructorWrongType() throws JSONException {
        try {
            new JSONArray("{\"foo\": false}");
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testStringConstructorNull() throws JSONException {
        try {
            new JSONArray(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testStringConstructorParseFail() {
        try {
            new JSONArray("[");
            TestCase.fail();
        } catch (JSONException e) {
        } catch (StackOverflowError e) {
            TestCase.fail("Stack overflowed on input: \"[\"");
        }
    }

    public void testCreate() throws JSONException {
        JSONArray array = new JSONArray(Arrays.asList(5.5, true));
        TestCase.assertEquals(2, array.length());
        TestCase.assertEquals(5.5, array.getDouble(0));
        TestCase.assertEquals(true, array.get(1));
        TestCase.assertEquals("[5.5,true]", array.toString());
    }

    public void testAccessOutOfBounds() throws JSONException {
        JSONArray array = new JSONArray();
        array.put("foo");
        TestCase.assertEquals(null, array.opt(3));
        TestCase.assertEquals(null, array.opt((-3)));
        TestCase.assertEquals("", array.optString(3));
        TestCase.assertEquals("", array.optString((-3)));
        try {
            array.get(3);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.get((-3));
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.getString(3);
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            array.getString((-3));
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void test_remove() throws Exception {
        JSONArray a = new JSONArray();
        TestCase.assertEquals(null, a.remove((-1)));
        TestCase.assertEquals(null, a.remove(0));
        a.put("hello");
        TestCase.assertEquals(null, a.remove((-1)));
        TestCase.assertEquals(null, a.remove(1));
        TestCase.assertEquals("hello", a.remove(0));
        TestCase.assertEquals(null, a.remove(0));
    }
}

