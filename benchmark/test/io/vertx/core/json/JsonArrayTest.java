/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.json;


import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.Utils;
import io.vertx.test.core.TestUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

import static io.vertx.core.json.JsonObjectTest.SomeEnum.FOO;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JsonArrayTest {
    private JsonArray jsonArray;

    @Test
    public void testGetInteger() {
        jsonArray.add(123);
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
        try {
            jsonArray.getInteger((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getInteger(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        // Different number types
        jsonArray.add(123L);
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(1));
        jsonArray.add(123.0F);
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(2));
        jsonArray.add(123.0);
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(3));
        jsonArray.add("foo");
        try {
            jsonArray.getInteger(4);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getInteger(5));
    }

    @Test
    public void testGetLong() {
        jsonArray.add(123L);
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(0));
        try {
            jsonArray.getLong((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getLong(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        // Different number types
        jsonArray.add(123);
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(1));
        jsonArray.add(123.0F);
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(2));
        jsonArray.add(123.0);
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(3));
        jsonArray.add("foo");
        try {
            jsonArray.getLong(4);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getLong(5));
    }

    @Test
    public void testGetFloat() {
        jsonArray.add(123.0F);
        Assert.assertEquals(Float.valueOf(123.0F), jsonArray.getFloat(0));
        try {
            jsonArray.getFloat((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getFloat(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        // Different number types
        jsonArray.add(123);
        Assert.assertEquals(Float.valueOf(123.0F), jsonArray.getFloat(1));
        jsonArray.add(123);
        Assert.assertEquals(Float.valueOf(123.0F), jsonArray.getFloat(2));
        jsonArray.add(123.0);
        Assert.assertEquals(Float.valueOf(123.0F), jsonArray.getFloat(3));
        jsonArray.add("foo");
        try {
            jsonArray.getFloat(4);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getFloat(5));
    }

    @Test
    public void testGetDouble() {
        jsonArray.add(123.0);
        Assert.assertEquals(Double.valueOf(123.0), jsonArray.getDouble(0));
        try {
            jsonArray.getDouble((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getDouble(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        // Different number types
        jsonArray.add(123);
        Assert.assertEquals(Double.valueOf(123.0), jsonArray.getDouble(1));
        jsonArray.add(123);
        Assert.assertEquals(Double.valueOf(123.0), jsonArray.getDouble(2));
        jsonArray.add(123.0);
        Assert.assertEquals(Double.valueOf(123.0), jsonArray.getDouble(3));
        jsonArray.add("foo");
        try {
            jsonArray.getDouble(4);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getDouble(5));
    }

    @Test
    public void testGetString() {
        jsonArray.add("foo");
        Assert.assertEquals("foo", jsonArray.getString(0));
        try {
            jsonArray.getString((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getString(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getString(1);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getString(2));
    }

    @Test
    public void testGetBoolean() {
        jsonArray.add(true);
        Assert.assertEquals(true, jsonArray.getBoolean(0));
        jsonArray.add(false);
        Assert.assertEquals(false, jsonArray.getBoolean(1));
        try {
            jsonArray.getBoolean((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getBoolean(2);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getBoolean(2);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getBoolean(3));
    }

    @Test
    public void testGetBinary() {
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonArray.add(bytes);
        Assert.assertArrayEquals(bytes, jsonArray.getBinary(0));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonArray.getValue(0));
        Assert.assertArrayEquals(bytes, Base64.getDecoder().decode(jsonArray.getString(0)));
        try {
            jsonArray.getBinary((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getBinary(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getBinary(1);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getBinary(2));
    }

    @Test
    public void testGetInstant() {
        Instant now = Instant.now();
        jsonArray.add(now);
        Assert.assertEquals(now, jsonArray.getInstant(0));
        Assert.assertEquals(now.toString(), jsonArray.getValue(0));
        Assert.assertEquals(now, Instant.from(DateTimeFormatter.ISO_INSTANT.parse(jsonArray.getString(0))));
        try {
            jsonArray.getInstant((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getValue((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getInstant(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getValue(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getInstant(1);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getInstant(2));
        Assert.assertNull(jsonArray.getValue(2));
    }

    @Test
    public void testGetJsonObject() {
        JsonObject obj = new JsonObject().put("foo", "bar");
        jsonArray.add(obj);
        Assert.assertEquals(obj, jsonArray.getJsonObject(0));
        try {
            jsonArray.getJsonObject((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getJsonObject(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getJsonObject(1);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getJsonObject(2));
    }

    @Test
    public void testGetJsonArray() {
        JsonArray arr = new JsonArray().add("foo");
        jsonArray.add(arr);
        Assert.assertEquals(arr, jsonArray.getJsonArray(0));
        try {
            jsonArray.getJsonArray((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getJsonArray(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        jsonArray.add(123);
        try {
            jsonArray.getJsonArray(1);
            Assert.fail();
        } catch (ClassCastException e) {
            // OK
        }
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getJsonArray(2));
    }

    @Test
    public void testGetValue() {
        jsonArray.add(123);
        Assert.assertEquals(123, jsonArray.getValue(0));
        jsonArray.add(123L);
        Assert.assertEquals(123L, jsonArray.getValue(1));
        jsonArray.add(123.0F);
        Assert.assertEquals(123.0F, jsonArray.getValue(2));
        jsonArray.add(123.0);
        Assert.assertEquals(123.0, jsonArray.getValue(3));
        jsonArray.add(false);
        Assert.assertEquals(false, jsonArray.getValue(4));
        jsonArray.add(true);
        Assert.assertEquals(true, jsonArray.getValue(5));
        jsonArray.add("bar");
        Assert.assertEquals("bar", jsonArray.getValue(6));
        JsonObject obj = new JsonObject().put("blah", "wibble");
        jsonArray.add(obj);
        Assert.assertEquals(obj, jsonArray.getValue(7));
        JsonArray arr = new JsonArray().add("blah").add("wibble");
        jsonArray.add(arr);
        Assert.assertEquals(arr, jsonArray.getValue(8));
        byte[] bytes = TestUtils.randomByteArray(100);
        jsonArray.add(bytes);
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonArray.getValue(9));
        Instant now = Instant.now();
        jsonArray.add(now);
        Assert.assertEquals(now, jsonArray.getInstant(10));
        Assert.assertEquals(now.toString(), jsonArray.getValue(10));
        jsonArray.addNull();
        Assert.assertNull(jsonArray.getValue(11));
        try {
            jsonArray.getValue((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        try {
            jsonArray.getValue(12);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        // JsonObject with inner Map
        List<Object> list = new ArrayList<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("blah", "wibble");
        list.add(innerMap);
        jsonArray = new JsonArray(list);
        obj = ((JsonObject) (jsonArray.getValue(0)));
        Assert.assertEquals("wibble", obj.getString("blah"));
        // JsonObject with inner List
        list = new ArrayList<>();
        List<Object> innerList = new ArrayList<>();
        innerList.add("blah");
        list.add(innerList);
        jsonArray = new JsonArray(list);
        arr = ((JsonArray) (jsonArray.getValue(0)));
        Assert.assertEquals("blah", arr.getString(0));
    }

    enum SomeEnum {

        FOO,
        BAR;}

    @Test
    public void testAddEnum() {
        Assert.assertSame(jsonArray, jsonArray.add(FOO));
        Assert.assertEquals(FOO.toString(), jsonArray.getString(0));
        try {
            jsonArray.add(((JsonObjectTest.SomeEnum) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddString() {
        Assert.assertSame(jsonArray, jsonArray.add("foo"));
        Assert.assertEquals("foo", jsonArray.getString(0));
        try {
            jsonArray.add(((String) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddCharSequence() {
        Assert.assertSame(jsonArray, jsonArray.add(new StringBuilder("bar")));
        Assert.assertEquals("bar", jsonArray.getString(0));
        try {
            jsonArray.add(((CharSequence) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddInteger() {
        Assert.assertSame(jsonArray, jsonArray.add(123));
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
        try {
            jsonArray.add(((Integer) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddLong() {
        Assert.assertSame(jsonArray, jsonArray.add(123L));
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(0));
        try {
            jsonArray.add(((Long) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddFloat() {
        Assert.assertSame(jsonArray, jsonArray.add(123.0F));
        Assert.assertEquals(Float.valueOf(123.0F), jsonArray.getFloat(0));
        try {
            jsonArray.add(((Float) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddDouble() {
        Assert.assertSame(jsonArray, jsonArray.add(123.0));
        Assert.assertEquals(Double.valueOf(123.0), jsonArray.getDouble(0));
        try {
            jsonArray.add(((Double) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddBoolean() {
        Assert.assertSame(jsonArray, jsonArray.add(true));
        Assert.assertEquals(true, jsonArray.getBoolean(0));
        jsonArray.add(false);
        Assert.assertEquals(false, jsonArray.getBoolean(1));
        try {
            jsonArray.add(((Boolean) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddJsonObject() {
        JsonObject obj = new JsonObject().put("foo", "bar");
        Assert.assertSame(jsonArray, jsonArray.add(obj));
        Assert.assertEquals(obj, jsonArray.getJsonObject(0));
        try {
            jsonArray.add(((JsonObject) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddJsonArray() {
        JsonArray arr = new JsonArray().add("foo");
        Assert.assertSame(jsonArray, jsonArray.add(arr));
        Assert.assertEquals(arr, jsonArray.getJsonArray(0));
        try {
            jsonArray.add(((JsonArray) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddBinary() {
        byte[] bytes = TestUtils.randomByteArray(10);
        Assert.assertSame(jsonArray, jsonArray.add(bytes));
        Assert.assertArrayEquals(bytes, jsonArray.getBinary(0));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonArray.getValue(0));
        try {
            jsonArray.add(((byte[]) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddInstant() {
        Instant now = Instant.now();
        Assert.assertSame(jsonArray, jsonArray.add(now));
        Assert.assertEquals(now, jsonArray.getInstant(0));
        Assert.assertEquals(now.toString(), jsonArray.getValue(0));
        try {
            jsonArray.add(((Instant) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddObject() {
        jsonArray.add(((Object) ("bar")));
        jsonArray.add(((Object) (Integer.valueOf(123))));
        jsonArray.add(((Object) (Long.valueOf(123L))));
        jsonArray.add(((Object) (Float.valueOf(1.23F))));
        jsonArray.add(((Object) (Double.valueOf(1.23))));
        jsonArray.add(((Object) (true)));
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonArray.add(((Object) (bytes)));
        Instant now = Instant.now();
        jsonArray.add(now);
        JsonObject obj = new JsonObject().put("foo", "blah");
        JsonArray arr = new JsonArray().add("quux");
        jsonArray.add(((Object) (obj)));
        jsonArray.add(((Object) (arr)));
        Assert.assertEquals("bar", jsonArray.getString(0));
        Assert.assertEquals(Integer.valueOf(123), jsonArray.getInteger(1));
        Assert.assertEquals(Long.valueOf(123L), jsonArray.getLong(2));
        Assert.assertEquals(Float.valueOf(1.23F), jsonArray.getFloat(3));
        Assert.assertEquals(Double.valueOf(1.23), jsonArray.getDouble(4));
        Assert.assertEquals(true, jsonArray.getBoolean(5));
        Assert.assertArrayEquals(bytes, jsonArray.getBinary(6));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonArray.getValue(6));
        Assert.assertEquals(now, jsonArray.getInstant(7));
        Assert.assertEquals(now.toString(), jsonArray.getValue(7));
        Assert.assertEquals(obj, jsonArray.getJsonObject(8));
        Assert.assertEquals(arr, jsonArray.getJsonArray(9));
        try {
            jsonArray.add(new JsonArrayTest.SomeClass());
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            jsonArray.add(new BigDecimal(123));
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            jsonArray.add(new Date());
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testAddAllJsonArray() {
        jsonArray.add("bar");
        JsonArray arr = new JsonArray().add("foo").add(48);
        Assert.assertSame(jsonArray, jsonArray.addAll(arr));
        Assert.assertEquals(arr.getString(0), jsonArray.getString(1));
        Assert.assertEquals(arr.getInteger(1), jsonArray.getInteger(2));
        try {
            jsonArray.add(((JsonArray) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testAddNull() {
        Assert.assertSame(jsonArray, jsonArray.addNull());
        Assert.assertEquals(null, jsonArray.getString(0));
        Assert.assertTrue(jsonArray.hasNull(0));
    }

    @Test
    public void testHasNull() {
        jsonArray.addNull();
        jsonArray.add("foo");
        Assert.assertEquals(null, jsonArray.getString(0));
        Assert.assertTrue(jsonArray.hasNull(0));
        Assert.assertFalse(jsonArray.hasNull(1));
    }

    @Test
    public void testContains() {
        jsonArray.add("wibble");
        jsonArray.add(true);
        jsonArray.add(123);
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        jsonArray.add(obj);
        jsonArray.add(arr);
        Assert.assertFalse(jsonArray.contains("eek"));
        Assert.assertFalse(jsonArray.contains(false));
        Assert.assertFalse(jsonArray.contains(321));
        Assert.assertFalse(jsonArray.contains(new JsonObject().put("blah", "flib")));
        Assert.assertFalse(jsonArray.contains(new JsonArray().add("oob")));
        Assert.assertTrue(jsonArray.contains("wibble"));
        Assert.assertTrue(jsonArray.contains(true));
        Assert.assertTrue(jsonArray.contains(123));
        Assert.assertTrue(jsonArray.contains(obj));
        Assert.assertTrue(jsonArray.contains(arr));
    }

    @Test
    public void testRemoveByObject() {
        jsonArray.add("wibble");
        jsonArray.add(true);
        jsonArray.add(123);
        Assert.assertEquals(3, jsonArray.size());
        Assert.assertTrue(jsonArray.remove("wibble"));
        Assert.assertEquals(2, jsonArray.size());
        Assert.assertFalse(jsonArray.remove("notthere"));
        Assert.assertTrue(jsonArray.remove(true));
        Assert.assertTrue(jsonArray.remove(Integer.valueOf(123)));
        Assert.assertTrue(jsonArray.isEmpty());
    }

    @Test
    public void testRemoveByPos() {
        jsonArray.add("wibble");
        jsonArray.add(true);
        jsonArray.add(123);
        Assert.assertEquals(3, jsonArray.size());
        Assert.assertEquals("wibble", jsonArray.remove(0));
        Assert.assertEquals(2, jsonArray.size());
        Assert.assertEquals(123, jsonArray.remove(1));
        Assert.assertEquals(1, jsonArray.size());
        Assert.assertEquals(true, jsonArray.remove(0));
        Assert.assertTrue(jsonArray.isEmpty());
    }

    @Test
    public void testSize() {
        jsonArray.add("wibble");
        jsonArray.add(true);
        jsonArray.add(123);
        Assert.assertEquals(3, jsonArray.size());
    }

    @Test
    public void testClear() {
        jsonArray.add("wibble");
        jsonArray.add(true);
        jsonArray.add(123);
        Assert.assertEquals(3, jsonArray.size());
        Assert.assertEquals(jsonArray, jsonArray.clear());
        Assert.assertEquals(0, jsonArray.size());
        Assert.assertTrue(jsonArray.isEmpty());
    }

    @Test
    public void testIterator() {
        jsonArray.add("foo");
        jsonArray.add(123);
        JsonObject obj = new JsonObject().put("foo", "bar");
        jsonArray.add(obj);
        Iterator<Object> iter = jsonArray.iterator();
        Assert.assertTrue(iter.hasNext());
        Object entry = iter.next();
        Assert.assertEquals("foo", entry);
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals(123, entry);
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals(obj, entry);
        Assert.assertFalse(iter.hasNext());
        iter.remove();
        Assert.assertFalse(jsonArray.contains(obj));
        Assert.assertEquals(2, jsonArray.size());
    }

    @Test
    public void testStream() {
        jsonArray.add("foo");
        jsonArray.add(123);
        JsonObject obj = new JsonObject().put("foo", "bar");
        jsonArray.add(obj);
        List<Object> list = jsonArray.stream().collect(Collectors.toList());
        Iterator<Object> iter = list.iterator();
        Assert.assertTrue(iter.hasNext());
        Object entry = iter.next();
        Assert.assertEquals("foo", entry);
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals(123, entry);
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals(obj, entry);
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testCopy() {
        jsonArray.add("foo");
        jsonArray.add(123);
        JsonObject obj = new JsonObject().put("foo", "bar");
        jsonArray.add(obj);
        jsonArray.add(new StringBuilder("eeek"));
        JsonArray copy = jsonArray.copy();
        Assert.assertEquals("eeek", copy.getString(3));
        Assert.assertNotSame(jsonArray, copy);
        Assert.assertEquals(jsonArray, copy);
        Assert.assertEquals(4, copy.size());
        Assert.assertEquals("foo", copy.getString(0));
        Assert.assertEquals(Integer.valueOf(123), copy.getInteger(1));
        Assert.assertEquals(obj, copy.getJsonObject(2));
        Assert.assertNotSame(obj, copy.getJsonObject(2));
        copy.add("foo");
        Assert.assertEquals(4, jsonArray.size());
        jsonArray.add("bar");
        Assert.assertEquals(5, copy.size());
    }

    @Test
    public void testInvalidValsOnCopy() {
        List<Object> invalid = new ArrayList<>();
        invalid.add(new JsonArrayTest.SomeClass());
        JsonArray arr = new JsonArray(invalid);
        try {
            arr.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidValsOnCopy2() {
        List<Object> invalid = new ArrayList<>();
        List<Object> invalid2 = new ArrayList<>();
        invalid2.add(new JsonArrayTest.SomeClass());
        invalid.add(invalid2);
        JsonArray arr = new JsonArray(invalid);
        try {
            arr.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidValsOnCopy3() {
        List<Object> invalid = new ArrayList<>();
        Map<String, Object> invalid2 = new HashMap<>();
        invalid2.put("foo", new JsonArrayTest.SomeClass());
        invalid.add(invalid2);
        JsonArray arr = new JsonArray(invalid);
        try {
            arr.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    class SomeClass {}

    @Test
    public void testEncode() throws Exception {
        jsonArray.add("foo");
        jsonArray.add(123);
        jsonArray.add(1234L);
        jsonArray.add(1.23F);
        jsonArray.add(2.34);
        jsonArray.add(true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonArray.add(bytes);
        jsonArray.addNull();
        jsonArray.add(new JsonObject().put("foo", "bar"));
        jsonArray.add(new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        String expected = ("[\"foo\",123,1234,1.23,2.34,true,\"" + strBytes) + "\",null,{\"foo\":\"bar\"},[\"foo\",123]]";
        String json = jsonArray.encode();
        Assert.assertEquals(expected, json);
    }

    @Test
    public void testEncodeToBuffer() throws Exception {
        jsonArray.add("foo");
        jsonArray.add(123);
        jsonArray.add(1234L);
        jsonArray.add(1.23F);
        jsonArray.add(2.34);
        jsonArray.add(true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonArray.add(bytes);
        jsonArray.addNull();
        jsonArray.add(new JsonObject().put("foo", "bar"));
        jsonArray.add(new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        Buffer expected = Buffer.buffer((("[\"foo\",123,1234,1.23,2.34,true,\"" + strBytes) + "\",null,{\"foo\":\"bar\"},[\"foo\",123]]"), "UTF-8");
        Buffer json = jsonArray.toBuffer();
        Assert.assertArrayEquals(expected.getBytes(), json.getBytes());
    }

    @Test
    public void testDecode() {
        byte[] bytes = TestUtils.randomByteArray(10);
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        Instant now = Instant.now();
        String strInstant = DateTimeFormatter.ISO_INSTANT.format(now);
        String json = ((("[\"foo\",123,1234,1.23,2.34,true,\"" + strBytes) + "\",\"") + strInstant) + "\",null,{\"foo\":\"bar\"},[\"foo\",123]]";
        JsonArray arr = new JsonArray(json);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertEquals(Long.valueOf(1234L), arr.getLong(2));
        Assert.assertEquals(Float.valueOf(1.23F), arr.getFloat(3));
        Assert.assertEquals(Double.valueOf(2.34), arr.getDouble(4));
        Assert.assertEquals(true, arr.getBoolean(5));
        Assert.assertArrayEquals(bytes, arr.getBinary(6));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), arr.getValue(6));
        Assert.assertEquals(now, arr.getInstant(7));
        Assert.assertEquals(now.toString(), arr.getValue(7));
        Assert.assertTrue(arr.hasNull(8));
        JsonObject obj = arr.getJsonObject(9);
        Assert.assertEquals("bar", obj.getString("foo"));
        JsonArray arr2 = arr.getJsonArray(10);
        Assert.assertEquals("foo", arr2.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr2.getInteger(1));
    }

    @Test
    public void testEncodePrettily() throws Exception {
        jsonArray.add("foo");
        jsonArray.add(123);
        jsonArray.add(1234L);
        jsonArray.add(1.23F);
        jsonArray.add(2.34);
        jsonArray.add(true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonArray.add(bytes);
        jsonArray.addNull();
        jsonArray.add(new JsonObject().put("foo", "bar"));
        jsonArray.add(new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        String expected = ((((("[ \"foo\", 123, 1234, 1.23, 2.34, true, \"" + strBytes) + "\", null, {") + (Utils.LINE_SEPARATOR)) + "  \"foo\" : \"bar\"") + (Utils.LINE_SEPARATOR)) + "}, [ \"foo\", 123 ] ]";
        String json = jsonArray.encodePrettily();
        Assert.assertEquals(expected, json);
    }

    @Test
    public void testToString() {
        jsonArray.add("foo").add(123);
        Assert.assertEquals(jsonArray.encode(), jsonArray.toString());
    }

    // Strict JSON doesn't allow comments but we do so users can add comments to config files etc
    @Test
    public void testCommentsInJson() {
        String jsonWithComments = "// single line comment\n" + ((((((((((("/*\n" + "  This is a multi \n") + "  line comment\n") + "*/\n") + "[\n") + "// another single line comment this time inside the JSON array itself\n") + "  \"foo\", \"bar\" // and a single line comment at end of line \n") + "/*\n") + "  This is a another multi \n") + "  line comment this time inside the JSON array itself\n") + "*/\n") + "]");
        JsonArray json = new JsonArray(jsonWithComments);
        Assert.assertEquals("[\"foo\",\"bar\"]", json.encode());
    }

    @Test
    public void testInvalidJson() {
        String invalid = "qiwjdoiqwjdiqwjd";
        try {
            new JsonArray(invalid);
            Assert.fail();
        } catch (DecodeException e) {
            // OK
        }
    }

    @Test
    public void testGetList() {
        JsonObject obj = new JsonObject().put("quux", "wibble");
        jsonArray.add("foo").add(123).add(obj);
        List<Object> list = jsonArray.getList();
        list.remove("foo");
        Assert.assertFalse(jsonArray.contains("foo"));
        list.add("floob");
        Assert.assertTrue(jsonArray.contains("floob"));
        Assert.assertSame(obj, list.get(1));
        obj.remove("quux");
    }

    @Test
    public void testCreateFromList() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertSame(list, arr.getList());
    }

    @Test
    public void testCreateFromListCharSequence() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        list.add(new StringBuilder("eek"));
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertEquals("eek", arr.getString(2));
        Assert.assertSame(list, arr.getList());
    }

    @Test
    public void testCreateFromListNestedJsonObject() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        JsonObject obj = new JsonObject().put("blah", "wibble");
        list.add(obj);
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertSame(list, arr.getList());
        Assert.assertSame(obj, arr.getJsonObject(2));
    }

    @Test
    public void testCreateFromListNestedMap() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        Map<String, Object> map = new HashMap<>();
        map.put("blah", "wibble");
        list.add(map);
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertSame(list, arr.getList());
        JsonObject obj = arr.getJsonObject(2);
        Assert.assertSame(map, obj.getMap());
    }

    @Test
    public void testCreateFromListNestedJsonArray() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        JsonArray arr2 = new JsonArray().add("blah").add("wibble");
        list.add(arr2);
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertSame(list, arr.getList());
        Assert.assertSame(arr2, arr.getJsonArray(2));
    }

    @Test
    public void testCreateFromListNestedList() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(123);
        List<Object> list2 = new ArrayList<>();
        list2.add("blah");
        list2.add("wibble");
        list.add(list2);
        JsonArray arr = new JsonArray(list);
        Assert.assertEquals("foo", arr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), arr.getInteger(1));
        Assert.assertSame(list, arr.getList());
        JsonArray arr2 = arr.getJsonArray(2);
        Assert.assertSame(list2, arr2.getList());
    }

    @Test
    public void testCreateFromBuffer() {
        JsonArray excepted = new JsonArray();
        excepted.add("foobar");
        excepted.add(123);
        Buffer buf = Buffer.buffer(excepted.encode());
        Assert.assertEquals(excepted, new JsonArray(buf));
    }

    @Test
    public void testClusterSerializable() {
        jsonArray.add("foo").add(123);
        Buffer buff = Buffer.buffer();
        jsonArray.writeToBuffer(buff);
        JsonArray deserialized = new JsonArray();
        deserialized.readFromBuffer(0, buff);
        Assert.assertEquals(jsonArray, deserialized);
    }

    @Test
    public void testJsonArrayEquality() {
        JsonObject obj = new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3)));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3L))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(3))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(3L))));
        Assert.assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(4))));
        Assert.assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(4))));
        JsonArray array = new JsonArray(Collections.singletonList(Collections.singletonList(3)));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(3))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(3L))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(3))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(3L))));
        Assert.assertNotEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(4))));
        Assert.assertNotEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(4))));
    }

    @Test
    public void testStreamCorrectTypes() throws Exception {
        String json = "{\"object1\": [{\"object2\": 12}]}";
        JsonObject object = new JsonObject(json);
        testStreamCorrectTypes(object.copy());
        testStreamCorrectTypes(object);
    }

    @Test
    public void testRemoveMethodReturnedObject() {
        JsonArray obj = new JsonArray();
        obj.add("bar").add(new JsonObject().put("name", "vert.x").put("count", 2)).add(new JsonArray().add(1.0).add(2.0));
        Object removed = obj.remove(0);
        Assert.assertTrue((removed instanceof String));
        removed = obj.remove(0);
        Assert.assertTrue((removed instanceof JsonObject));
        Assert.assertEquals(getString("name"), "vert.x");
        removed = obj.remove(0);
        Assert.assertTrue((removed instanceof JsonArray));
        Assert.assertEquals(getDouble(0), 1.0, 0.0);
    }
}

