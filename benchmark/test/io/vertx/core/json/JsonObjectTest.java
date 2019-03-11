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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JsonObjectTest {
    protected JsonObject jsonObject;

    @Test
    public void testGetInteger() {
        jsonObject.put("foo", 123);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getInteger("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("foo", Long.MAX_VALUE);
        Assert.assertEquals(Integer.valueOf((-1)), jsonObject.getInteger("foo"));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getInteger("foo"));
        Assert.assertNull(jsonObject.getInteger("absent"));
        try {
            jsonObject.getInteger(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetIntegerDefault() {
        jsonObject.put("foo", 123);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", null));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getInteger("bar", 123);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
        jsonObject.put("foo", Long.MAX_VALUE);
        Assert.assertEquals(Integer.valueOf((-1)), jsonObject.getInteger("foo", 321));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getInteger("foo", 321));
        Assert.assertEquals(Integer.valueOf(321), jsonObject.getInteger("absent", 321));
        Assert.assertNull(jsonObject.getInteger("foo", null));
        Assert.assertNull(jsonObject.getInteger("absent", null));
        try {
            jsonObject.getInteger(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetLong() {
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getLong("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("foo", Long.MAX_VALUE);
        Assert.assertEquals(Long.valueOf(Long.MAX_VALUE), jsonObject.getLong("foo"));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getLong("foo"));
        Assert.assertNull(jsonObject.getLong("absent"));
        try {
            jsonObject.getLong(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetLongDefault() {
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
        Assert.assertEquals(Long.valueOf(123), jsonObject.getLong("foo", null));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getLong("bar", 123L);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
        jsonObject.put("foo", Long.MAX_VALUE);
        Assert.assertEquals(Long.valueOf(Long.MAX_VALUE), jsonObject.getLong("foo", 321L));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getLong("foo", 321L));
        Assert.assertEquals(Long.valueOf(321L), jsonObject.getLong("absent", 321L));
        Assert.assertNull(jsonObject.getLong("foo", null));
        Assert.assertNull(jsonObject.getLong("absent", null));
        try {
            jsonObject.getLong(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetFloat() {
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo"));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getFloat("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo"));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo"));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Float.valueOf(123L), jsonObject.getFloat("foo"));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getFloat("foo"));
        Assert.assertNull(jsonObject.getFloat("absent"));
        try {
            jsonObject.getFloat(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetFloatDefault() {
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo", 321.0F));
        Assert.assertEquals(Float.valueOf(123), jsonObject.getFloat("foo", null));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getFloat("bar", 123.0F);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo", 321.0F));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo", 321.0F));
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo", 321.0F));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getFloat("foo", 321.0F));
        Assert.assertEquals(Float.valueOf(321.0F), jsonObject.getFloat("absent", 321.0F));
        Assert.assertNull(jsonObject.getFloat("foo", null));
        Assert.assertNull(jsonObject.getFloat("absent", null));
        try {
            jsonObject.getFloat(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetDouble() {
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getDouble("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getDouble("foo"));
        Assert.assertNull(jsonObject.getDouble("absent"));
        try {
            jsonObject.getDouble(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetDoubleDefault() {
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo", 321.0));
        Assert.assertEquals(Double.valueOf(123), jsonObject.getDouble("foo", null));
        jsonObject.put("bar", "hello");
        try {
            jsonObject.getDouble("bar", 123.0);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Put as different Number types
        jsonObject.put("foo", 123);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo", 321.0));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo", 321.0));
        jsonObject.put("foo", 123L);
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo", 321.0));
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getDouble("foo", 321.0));
        Assert.assertEquals(Double.valueOf(321.0), jsonObject.getDouble("absent", 321.0));
        Assert.assertNull(jsonObject.getDouble("foo", null));
        Assert.assertNull(jsonObject.getDouble("absent", null));
        try {
            jsonObject.getDouble(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetString() {
        jsonObject.put("foo", "bar");
        Assert.assertEquals("bar", jsonObject.getString("foo"));
        jsonObject.put("bar", 123);
        try {
            jsonObject.getString("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getString("foo"));
        Assert.assertNull(jsonObject.getString("absent"));
        try {
            jsonObject.getString(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetStringDefault() {
        jsonObject.put("foo", "bar");
        Assert.assertEquals("bar", jsonObject.getString("foo", "wibble"));
        Assert.assertEquals("bar", jsonObject.getString("foo", null));
        jsonObject.put("bar", 123);
        try {
            jsonObject.getString("bar", "wibble");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getString("foo", "wibble"));
        Assert.assertEquals("wibble", jsonObject.getString("absent", "wibble"));
        Assert.assertNull(jsonObject.getString("foo", null));
        Assert.assertNull(jsonObject.getString("absent", null));
        try {
            jsonObject.getString(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetBoolean() {
        jsonObject.put("foo", true);
        Assert.assertEquals(true, jsonObject.getBoolean("foo"));
        jsonObject.put("foo", false);
        Assert.assertEquals(false, jsonObject.getBoolean("foo"));
        jsonObject.put("bar", 123);
        try {
            jsonObject.getBoolean("bar");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getBoolean("foo"));
        Assert.assertNull(jsonObject.getBoolean("absent"));
        try {
            jsonObject.getBoolean(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetBooleanDefault() {
        jsonObject.put("foo", true);
        Assert.assertEquals(true, jsonObject.getBoolean("foo", false));
        Assert.assertEquals(true, jsonObject.getBoolean("foo", null));
        jsonObject.put("foo", false);
        Assert.assertEquals(false, jsonObject.getBoolean("foo", true));
        Assert.assertEquals(false, jsonObject.getBoolean("foo", null));
        jsonObject.put("bar", 123);
        try {
            jsonObject.getBoolean("bar", true);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        // Null and absent values
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getBoolean("foo", true));
        Assert.assertNull(jsonObject.getBoolean("foo", false));
        Assert.assertEquals(true, jsonObject.getBoolean("absent", true));
        Assert.assertEquals(false, jsonObject.getBoolean("absent", false));
        try {
            jsonObject.getBoolean(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetBinary() {
        byte[] bytes = TestUtils.randomByteArray(100);
        jsonObject.put("foo", bytes);
        Assert.assertArrayEquals(bytes, jsonObject.getBinary("foo"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonObject.getValue("foo"));
        // Can also get as string:
        String val = jsonObject.getString("foo");
        Assert.assertNotNull(val);
        byte[] retrieved = Base64.getDecoder().decode(val);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, retrieved));
        jsonObject.put("foo", 123);
        try {
            jsonObject.getBinary("foo");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getBinary("foo"));
        Assert.assertNull(jsonObject.getValue("foo"));
        Assert.assertNull(jsonObject.getBinary("absent"));
        Assert.assertNull(jsonObject.getValue("absent"));
        try {
            jsonObject.getBinary(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
        try {
            jsonObject.getBinary(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetInstant() {
        Instant now = Instant.now();
        jsonObject.put("foo", now);
        Assert.assertEquals(now, jsonObject.getInstant("foo"));
        Assert.assertEquals(now.toString(), jsonObject.getValue("foo"));
        // Can also get as string:
        String val = jsonObject.getString("foo");
        Assert.assertNotNull(val);
        Instant retrieved = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(val));
        Assert.assertEquals(now, retrieved);
        jsonObject.put("foo", 123);
        try {
            jsonObject.getInstant("foo");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getInstant("foo"));
        Assert.assertNull(jsonObject.getValue("foo"));
        Assert.assertNull(jsonObject.getInstant("absent"));
        Assert.assertNull(jsonObject.getValue("absent"));
        try {
            jsonObject.getInstant(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
        try {
            jsonObject.getValue(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
        try {
            jsonObject.getInstant(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
        try {
            jsonObject.getValue(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetBinaryDefault() {
        byte[] bytes = TestUtils.randomByteArray(100);
        byte[] defBytes = TestUtils.randomByteArray(100);
        jsonObject.put("foo", bytes);
        Assert.assertArrayEquals(bytes, jsonObject.getBinary("foo", defBytes));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonObject.getValue("foo", Base64.getEncoder().encode(defBytes)));
        Assert.assertArrayEquals(bytes, jsonObject.getBinary("foo", null));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonObject.getValue("foo", null));
        jsonObject.put("foo", 123);
        try {
            jsonObject.getBinary("foo", defBytes);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getBinary("foo", defBytes));
        Assert.assertTrue(TestUtils.byteArraysEqual(defBytes, jsonObject.getBinary("absent", defBytes)));
        Assert.assertNull(jsonObject.getBinary("foo", null));
        Assert.assertNull(jsonObject.getBinary("absent", null));
        try {
            jsonObject.getBinary(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetInstantDefault() {
        Instant now = Instant.now();
        Instant later = now.plus(1, ChronoUnit.DAYS);
        jsonObject.put("foo", now);
        Assert.assertEquals(now, jsonObject.getInstant("foo", later));
        Assert.assertEquals(now.toString(), jsonObject.getValue("foo", later));
        Assert.assertEquals(now, jsonObject.getInstant("foo", null));
        Assert.assertEquals(now.toString(), jsonObject.getValue("foo", null));
        jsonObject.put("foo", 123);
        try {
            jsonObject.getInstant("foo", later);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getInstant("foo", later));
        Assert.assertEquals(later, jsonObject.getInstant("absent", later));
        Assert.assertEquals(later, jsonObject.getValue("absent", later));
        Assert.assertNull(jsonObject.getInstant("foo", null));
        Assert.assertNull(jsonObject.getValue("foo", null));
        Assert.assertNull(jsonObject.getInstant("absent", null));
        Assert.assertNull(jsonObject.getValue("absent", null));
        try {
            jsonObject.getInstant(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
        try {
            jsonObject.getValue(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetJsonObject() {
        JsonObject obj = new JsonObject().put("blah", "wibble");
        jsonObject.put("foo", obj);
        Assert.assertEquals(obj, jsonObject.getJsonObject("foo"));
        jsonObject.put("foo", "hello");
        try {
            jsonObject.getJsonObject("foo");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getJsonObject("foo"));
        Assert.assertNull(jsonObject.getJsonObject("absent"));
        try {
            jsonObject.getJsonObject(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetJsonObjectDefault() {
        JsonObject obj = new JsonObject().put("blah", "wibble");
        JsonObject def = new JsonObject().put("eek", "quuz");
        jsonObject.put("foo", obj);
        Assert.assertEquals(obj, jsonObject.getJsonObject("foo", def));
        Assert.assertEquals(obj, jsonObject.getJsonObject("foo", null));
        jsonObject.put("foo", "hello");
        try {
            jsonObject.getJsonObject("foo", def);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getJsonObject("foo", def));
        Assert.assertEquals(def, jsonObject.getJsonObject("absent", def));
        Assert.assertNull(jsonObject.getJsonObject("foo", null));
        Assert.assertNull(jsonObject.getJsonObject("absent", null));
        try {
            jsonObject.getJsonObject(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetJsonArray() {
        JsonArray arr = new JsonArray().add("blah").add("wibble");
        jsonObject.put("foo", arr);
        Assert.assertEquals(arr, jsonObject.getJsonArray("foo"));
        jsonObject.put("foo", "hello");
        try {
            jsonObject.getJsonArray("foo");
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getJsonArray("foo"));
        Assert.assertNull(jsonObject.getJsonArray("absent"));
        try {
            jsonObject.getJsonArray(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetJsonArrayDefault() {
        JsonArray arr = new JsonArray().add("blah").add("wibble");
        JsonArray def = new JsonArray().add("quux").add("eek");
        jsonObject.put("foo", arr);
        Assert.assertEquals(arr, jsonObject.getJsonArray("foo", def));
        Assert.assertEquals(arr, jsonObject.getJsonArray("foo", null));
        jsonObject.put("foo", "hello");
        try {
            jsonObject.getJsonArray("foo", def);
            Assert.fail();
        } catch (ClassCastException e) {
            // Ok
        }
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getJsonArray("foo", def));
        Assert.assertEquals(def, jsonObject.getJsonArray("absent", def));
        Assert.assertNull(jsonObject.getJsonArray("foo", null));
        Assert.assertNull(jsonObject.getJsonArray("absent", null));
        try {
            jsonObject.getJsonArray(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testGetValue() {
        jsonObject.put("foo", 123);
        Assert.assertEquals(123, jsonObject.getValue("foo"));
        jsonObject.put("foo", 123L);
        Assert.assertEquals(123L, jsonObject.getValue("foo"));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(123.0F, jsonObject.getValue("foo"));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(123.0, jsonObject.getValue("foo"));
        jsonObject.put("foo", false);
        Assert.assertEquals(false, jsonObject.getValue("foo"));
        jsonObject.put("foo", true);
        Assert.assertEquals(true, jsonObject.getValue("foo"));
        jsonObject.put("foo", "bar");
        Assert.assertEquals("bar", jsonObject.getValue("foo"));
        JsonObject obj = new JsonObject().put("blah", "wibble");
        jsonObject.put("foo", obj);
        Assert.assertEquals(obj, jsonObject.getValue("foo"));
        JsonArray arr = new JsonArray().add("blah").add("wibble");
        jsonObject.put("foo", arr);
        Assert.assertEquals(arr, jsonObject.getValue("foo"));
        byte[] bytes = TestUtils.randomByteArray(100);
        jsonObject.put("foo", bytes);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, Base64.getDecoder().decode(((String) (jsonObject.getValue("foo"))))));
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getValue("foo"));
        Assert.assertNull(jsonObject.getValue("absent"));
        // JsonObject with inner Map
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("blah", "wibble");
        map.put("foo", innerMap);
        jsonObject = new JsonObject(map);
        obj = ((JsonObject) (jsonObject.getValue("foo")));
        Assert.assertEquals("wibble", obj.getString("blah"));
        // JsonObject with inner List
        map = new HashMap<>();
        List<Object> innerList = new ArrayList<>();
        innerList.add("blah");
        map.put("foo", innerList);
        jsonObject = new JsonObject(map);
        arr = ((JsonArray) (jsonObject.getValue("foo")));
        Assert.assertEquals("blah", arr.getString(0));
    }

    @Test
    public void testGetValueDefault() {
        jsonObject.put("foo", 123);
        Assert.assertEquals(123, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(123, jsonObject.getValue("foo", null));
        jsonObject.put("foo", 123L);
        Assert.assertEquals(123L, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(123L, jsonObject.getValue("foo", null));
        jsonObject.put("foo", 123.0F);
        Assert.assertEquals(123.0F, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(123.0F, jsonObject.getValue("foo", null));
        jsonObject.put("foo", 123.0);
        Assert.assertEquals(123.0, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(123.0, jsonObject.getValue("foo", null));
        jsonObject.put("foo", false);
        Assert.assertEquals(false, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(false, jsonObject.getValue("foo", null));
        jsonObject.put("foo", true);
        Assert.assertEquals(true, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(true, jsonObject.getValue("foo", null));
        jsonObject.put("foo", "bar");
        Assert.assertEquals("bar", jsonObject.getValue("foo", "blah"));
        Assert.assertEquals("bar", jsonObject.getValue("foo", null));
        JsonObject obj = new JsonObject().put("blah", "wibble");
        jsonObject.put("foo", obj);
        Assert.assertEquals(obj, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(obj, jsonObject.getValue("foo", null));
        JsonArray arr = new JsonArray().add("blah").add("wibble");
        jsonObject.put("foo", arr);
        Assert.assertEquals(arr, jsonObject.getValue("foo", "blah"));
        Assert.assertEquals(arr, jsonObject.getValue("foo", null));
        byte[] bytes = TestUtils.randomByteArray(100);
        jsonObject.put("foo", bytes);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, Base64.getDecoder().decode(((String) (jsonObject.getValue("foo", "blah"))))));
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, Base64.getDecoder().decode(((String) (jsonObject.getValue("foo", null))))));
        jsonObject.putNull("foo");
        Assert.assertNull(jsonObject.getValue("foo", "blah"));
        Assert.assertNull(jsonObject.getValue("foo", null));
        Assert.assertEquals("blah", jsonObject.getValue("absent", "blah"));
        Assert.assertNull(jsonObject.getValue("absent", null));
    }

    @Test
    public void testContainsKey() {
        jsonObject.put("foo", "bar");
        Assert.assertTrue(jsonObject.containsKey("foo"));
        jsonObject.putNull("foo");
        Assert.assertTrue(jsonObject.containsKey("foo"));
        Assert.assertFalse(jsonObject.containsKey("absent"));
    }

    @Test
    public void testFieldNames() {
        jsonObject.put("foo", "bar");
        jsonObject.put("eek", 123);
        jsonObject.put("flib", new JsonObject());
        Set<String> fieldNames = jsonObject.fieldNames();
        Assert.assertEquals(3, fieldNames.size());
        Assert.assertTrue(fieldNames.contains("foo"));
        Assert.assertTrue(fieldNames.contains("eek"));
        Assert.assertTrue(fieldNames.contains("flib"));
        jsonObject.remove("foo");
        Assert.assertEquals(2, fieldNames.size());
        Assert.assertFalse(fieldNames.contains("foo"));
    }

    @Test
    public void testSize() {
        Assert.assertEquals(0, jsonObject.size());
        jsonObject.put("foo", "bar");
        Assert.assertEquals(1, jsonObject.size());
        jsonObject.put("bar", 123);
        Assert.assertEquals(2, jsonObject.size());
        jsonObject.putNull("wibble");
        Assert.assertEquals(3, jsonObject.size());
        jsonObject.remove("wibble");
        Assert.assertEquals(2, jsonObject.size());
        jsonObject.clear();
        Assert.assertEquals(0, jsonObject.size());
    }

    enum SomeEnum {

        FOO,
        BAR;}

    @Test
    public void testPutEnum() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", JsonObjectTest.SomeEnum.FOO));
        Assert.assertEquals(JsonObjectTest.SomeEnum.FOO.toString(), jsonObject.getString("foo"));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, JsonObjectTest.SomeEnum.FOO);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutString() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", "bar"));
        Assert.assertEquals("bar", jsonObject.getString("foo"));
        jsonObject.put("quux", "wibble");
        Assert.assertEquals("wibble", jsonObject.getString("quux"));
        Assert.assertEquals("bar", jsonObject.getString("foo"));
        jsonObject.put("foo", "blah");
        Assert.assertEquals("blah", jsonObject.getString("foo"));
        jsonObject.put("foo", ((String) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, "blah");
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutCharSequence() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", new StringBuilder("bar")));
        Assert.assertEquals("bar", jsonObject.getString("foo"));
        Assert.assertEquals("bar", jsonObject.getString("foo", "def"));
        jsonObject.put("quux", new StringBuilder("wibble"));
        Assert.assertEquals("wibble", jsonObject.getString("quux"));
        Assert.assertEquals("bar", jsonObject.getString("foo"));
        jsonObject.put("foo", new StringBuilder("blah"));
        Assert.assertEquals("blah", jsonObject.getString("foo"));
        jsonObject.put("foo", ((CharSequence) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, ((CharSequence) ("blah")));
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutInteger() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", 123));
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("quux", 321);
        Assert.assertEquals(Integer.valueOf(321), jsonObject.getInteger("quux"));
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
        jsonObject.put("foo", 456);
        Assert.assertEquals(Integer.valueOf(456), jsonObject.getInteger("foo"));
        jsonObject.put("foo", ((Integer) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, 123);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutLong() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", 123L));
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("quux", 321L);
        Assert.assertEquals(Long.valueOf(321L), jsonObject.getLong("quux"));
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
        jsonObject.put("foo", 456L);
        Assert.assertEquals(Long.valueOf(456L), jsonObject.getLong("foo"));
        jsonObject.put("foo", ((Long) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, 123L);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutFloat() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", 123.0F));
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo"));
        jsonObject.put("quux", 321.0F);
        Assert.assertEquals(Float.valueOf(321.0F), jsonObject.getFloat("quux"));
        Assert.assertEquals(Float.valueOf(123.0F), jsonObject.getFloat("foo"));
        jsonObject.put("foo", 456.0F);
        Assert.assertEquals(Float.valueOf(456.0F), jsonObject.getFloat("foo"));
        jsonObject.put("foo", ((Float) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, 1.2F);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutDouble() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", 123.0));
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        jsonObject.put("quux", 321.0);
        Assert.assertEquals(Double.valueOf(321.0), jsonObject.getDouble("quux"));
        Assert.assertEquals(Double.valueOf(123.0), jsonObject.getDouble("foo"));
        jsonObject.put("foo", 456.0);
        Assert.assertEquals(Double.valueOf(456.0), jsonObject.getDouble("foo"));
        jsonObject.put("foo", ((Double) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, 1.23);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutBoolean() {
        Assert.assertSame(jsonObject, jsonObject.put("foo", true));
        Assert.assertEquals(true, jsonObject.getBoolean("foo"));
        jsonObject.put("quux", true);
        Assert.assertEquals(true, jsonObject.getBoolean("quux"));
        Assert.assertEquals(true, jsonObject.getBoolean("foo"));
        jsonObject.put("foo", true);
        Assert.assertEquals(true, jsonObject.getBoolean("foo"));
        jsonObject.put("foo", ((Boolean) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, false);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutJsonObject() {
        JsonObject obj1 = new JsonObject().put("blah", "wibble");
        JsonObject obj2 = new JsonObject().put("eeek", "flibb");
        JsonObject obj3 = new JsonObject().put("floob", "plarp");
        Assert.assertSame(jsonObject, jsonObject.put("foo", obj1));
        Assert.assertEquals(obj1, jsonObject.getJsonObject("foo"));
        jsonObject.put("quux", obj2);
        Assert.assertEquals(obj2, jsonObject.getJsonObject("quux"));
        Assert.assertEquals(obj1, jsonObject.getJsonObject("foo"));
        jsonObject.put("foo", obj3);
        Assert.assertEquals(obj3, jsonObject.getJsonObject("foo"));
        jsonObject.put("foo", ((JsonObject) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, new JsonObject());
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutJsonArray() {
        JsonArray obj1 = new JsonArray().add("parp");
        JsonArray obj2 = new JsonArray().add("fleep");
        JsonArray obj3 = new JsonArray().add("woob");
        Assert.assertSame(jsonObject, jsonObject.put("foo", obj1));
        Assert.assertEquals(obj1, jsonObject.getJsonArray("foo"));
        jsonObject.put("quux", obj2);
        Assert.assertEquals(obj2, jsonObject.getJsonArray("quux"));
        Assert.assertEquals(obj1, jsonObject.getJsonArray("foo"));
        jsonObject.put("foo", obj3);
        Assert.assertEquals(obj3, jsonObject.getJsonArray("foo"));
        jsonObject.put("foo", ((JsonArray) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, new JsonArray());
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutBinary() {
        byte[] bin1 = TestUtils.randomByteArray(100);
        byte[] bin2 = TestUtils.randomByteArray(100);
        byte[] bin3 = TestUtils.randomByteArray(100);
        Assert.assertSame(jsonObject, jsonObject.put("foo", bin1));
        Assert.assertArrayEquals(bin1, jsonObject.getBinary("foo"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bin1), jsonObject.getValue("foo"));
        jsonObject.put("quux", bin2);
        Assert.assertArrayEquals(bin2, jsonObject.getBinary("quux"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bin2), jsonObject.getValue("quux"));
        Assert.assertArrayEquals(bin1, jsonObject.getBinary("foo"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bin1), jsonObject.getValue("foo"));
        jsonObject.put("foo", bin3);
        Assert.assertArrayEquals(bin3, jsonObject.getBinary("foo"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bin3), jsonObject.getValue("foo"));
        jsonObject.put("foo", ((byte[]) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, bin1);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutInstant() {
        Instant bin1 = Instant.now();
        Instant bin2 = bin1.plus(1, ChronoUnit.DAYS);
        Instant bin3 = bin1.plus(1, ChronoUnit.MINUTES);
        Assert.assertSame(jsonObject, jsonObject.put("foo", bin1));
        Assert.assertEquals(bin1, jsonObject.getInstant("foo"));
        Assert.assertEquals(bin1.toString(), jsonObject.getValue("foo"));
        jsonObject.put("quux", bin2);
        Assert.assertEquals(bin2, jsonObject.getInstant("quux"));
        Assert.assertEquals(bin2.toString(), jsonObject.getValue("quux"));
        Assert.assertEquals(bin1, jsonObject.getInstant("foo"));
        Assert.assertEquals(bin1.toString(), jsonObject.getValue("foo"));
        jsonObject.put("foo", bin3);
        Assert.assertEquals(bin3, jsonObject.getInstant("foo"));
        Assert.assertEquals(bin3.toString(), jsonObject.getValue("foo"));
        jsonObject.put("foo", ((Instant) (null)));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        try {
            jsonObject.put(null, bin1);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutNull() {
        Assert.assertSame(jsonObject, jsonObject.putNull("foo"));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        Assert.assertSame(jsonObject, jsonObject.putNull("bar"));
        Assert.assertTrue(jsonObject.containsKey("bar"));
        try {
            jsonObject.putNull(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testPutValue() {
        jsonObject.put("str", ((Object) ("bar")));
        jsonObject.put("int", ((Object) (Integer.valueOf(123))));
        jsonObject.put("long", ((Object) (Long.valueOf(123L))));
        jsonObject.put("float", ((Object) (Float.valueOf(1.23F))));
        jsonObject.put("double", ((Object) (Double.valueOf(1.23))));
        jsonObject.put("boolean", ((Object) (true)));
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonObject.put("binary", ((Object) (bytes)));
        Instant now = Instant.now();
        jsonObject.put("instant", now);
        JsonObject obj = new JsonObject().put("foo", "blah");
        JsonArray arr = new JsonArray().add("quux");
        jsonObject.put("obj", ((Object) (obj)));
        jsonObject.put("arr", ((Object) (arr)));
        Assert.assertEquals("bar", jsonObject.getString("str"));
        Assert.assertEquals(Integer.valueOf(123), jsonObject.getInteger("int"));
        Assert.assertEquals(Long.valueOf(123L), jsonObject.getLong("long"));
        Assert.assertEquals(Float.valueOf(1.23F), jsonObject.getFloat("float"));
        Assert.assertEquals(Double.valueOf(1.23), jsonObject.getDouble("double"));
        Assert.assertArrayEquals(bytes, jsonObject.getBinary("binary"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), jsonObject.getValue("binary"));
        Assert.assertEquals(now, jsonObject.getInstant("instant"));
        Assert.assertEquals(now.toString(), jsonObject.getValue("instant"));
        Assert.assertEquals(obj, jsonObject.getJsonObject("obj"));
        Assert.assertEquals(arr, jsonObject.getJsonArray("arr"));
        try {
            jsonObject.put("inv", new JsonObjectTest.SomeClass());
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            jsonObject.put("inv", new BigDecimal(123));
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            jsonObject.put("inv", new Date());
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testMergeIn1() {
        JsonObject obj1 = new JsonObject().put("foo", "bar");
        JsonObject obj2 = new JsonObject().put("eek", "flurb");
        obj1.mergeIn(obj2);
        Assert.assertEquals(2, obj1.size());
        Assert.assertEquals("bar", obj1.getString("foo"));
        Assert.assertEquals("flurb", obj1.getString("eek"));
        Assert.assertEquals(1, obj2.size());
        Assert.assertEquals("flurb", obj2.getString("eek"));
    }

    @Test
    public void testMergeIn2() {
        JsonObject obj1 = new JsonObject().put("foo", "bar");
        JsonObject obj2 = new JsonObject().put("foo", "flurb");
        obj1.mergeIn(obj2);
        Assert.assertEquals(1, obj1.size());
        Assert.assertEquals("flurb", obj1.getString("foo"));
        Assert.assertEquals(1, obj2.size());
        Assert.assertEquals("flurb", obj2.getString("foo"));
    }

    @Test
    public void testMergeInDepth0() {
        JsonObject obj1 = new JsonObject("{ \"foo\": { \"bar\": \"flurb\" }}");
        JsonObject obj2 = new JsonObject("{ \"foo\": { \"bar\": \"eek\" }}");
        obj1.mergeIn(obj2, 0);
        Assert.assertEquals(1, obj1.size());
        Assert.assertEquals(1, obj1.getJsonObject("foo").size());
        Assert.assertEquals("flurb", getString("bar"));
    }

    @Test
    public void testMergeInFlat() {
        JsonObject obj1 = new JsonObject("{ \"foo\": { \"bar\": \"flurb\", \"eek\": 32 }}");
        JsonObject obj2 = new JsonObject("{ \"foo\": { \"bar\": \"eek\" }}");
        obj1.mergeIn(obj2, false);
        Assert.assertEquals(1, obj1.size());
        Assert.assertEquals(1, obj1.getJsonObject("foo").size());
        Assert.assertEquals("eek", getString("bar"));
    }

    @Test
    public void testMergeInDepth1() {
        JsonObject obj1 = new JsonObject("{ \"foo\": \"bar\", \"flurb\": { \"eek\": \"foo\", \"bar\": \"flurb\"}}");
        JsonObject obj2 = new JsonObject("{ \"flurb\": { \"bar\": \"flurb1\" }}");
        obj1.mergeIn(obj2, 1);
        Assert.assertEquals(2, obj1.size());
        Assert.assertEquals(1, obj1.getJsonObject("flurb").size());
        Assert.assertEquals("flurb1", getString("bar"));
    }

    @Test
    public void testMergeInDepth2() {
        JsonObject obj1 = new JsonObject("{ \"foo\": \"bar\", \"flurb\": { \"eek\": \"foo\", \"bar\": \"flurb\"}}");
        JsonObject obj2 = new JsonObject("{ \"flurb\": { \"bar\": \"flurb1\" }}");
        obj1.mergeIn(obj2, 2);
        Assert.assertEquals(2, obj1.size());
        Assert.assertEquals(2, obj1.getJsonObject("flurb").size());
        Assert.assertEquals("foo", getString("eek"));
        Assert.assertEquals("flurb1", getString("bar"));
    }

    @Test
    public void testEncode() throws Exception {
        jsonObject.put("mystr", "foo");
        jsonObject.put("mycharsequence", new StringBuilder("oob"));
        jsonObject.put("myint", 123);
        jsonObject.put("mylong", 1234L);
        jsonObject.put("myfloat", 1.23F);
        jsonObject.put("mydouble", 2.34);
        jsonObject.put("myboolean", true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonObject.put("mybinary", bytes);
        Instant now = Instant.now();
        jsonObject.put("myinstant", now);
        jsonObject.putNull("mynull");
        jsonObject.put("myobj", new JsonObject().put("foo", "bar"));
        jsonObject.put("myarr", new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        String expected = (((("{\"mystr\":\"foo\",\"mycharsequence\":\"oob\",\"myint\":123,\"mylong\":1234,\"myfloat\":1.23,\"mydouble\":2.34,\"" + "myboolean\":true,\"mybinary\":\"") + strBytes) + "\",\"myinstant\":\"") + (DateTimeFormatter.ISO_INSTANT.format(now))) + "\",\"mynull\":null,\"myobj\":{\"foo\":\"bar\"},\"myarr\":[\"foo\",123]}";
        String json = jsonObject.encode();
        Assert.assertEquals(expected, json);
    }

    @Test
    public void testEncodeToBuffer() throws Exception {
        jsonObject.put("mystr", "foo");
        jsonObject.put("mycharsequence", new StringBuilder("oob"));
        jsonObject.put("myint", 123);
        jsonObject.put("mylong", 1234L);
        jsonObject.put("myfloat", 1.23F);
        jsonObject.put("mydouble", 2.34);
        jsonObject.put("myboolean", true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonObject.put("mybinary", bytes);
        Instant now = Instant.now();
        jsonObject.put("myinstant", now);
        jsonObject.putNull("mynull");
        jsonObject.put("myobj", new JsonObject().put("foo", "bar"));
        jsonObject.put("myarr", new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        Buffer expected = Buffer.buffer(((((("{\"mystr\":\"foo\",\"mycharsequence\":\"oob\",\"myint\":123,\"mylong\":1234,\"myfloat\":1.23,\"mydouble\":2.34,\"" + "myboolean\":true,\"mybinary\":\"") + strBytes) + "\",\"myinstant\":\"") + (DateTimeFormatter.ISO_INSTANT.format(now))) + "\",\"mynull\":null,\"myobj\":{\"foo\":\"bar\"},\"myarr\":[\"foo\",123]}"), "UTF-8");
        Buffer json = jsonObject.toBuffer();
        Assert.assertArrayEquals(expected.getBytes(), json.getBytes());
    }

    @Test
    public void testDecode() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(10);
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        Instant now = Instant.now();
        String strInstant = DateTimeFormatter.ISO_INSTANT.format(now);
        String json = (((("{\"mystr\":\"foo\",\"myint\":123,\"mylong\":1234,\"myfloat\":1.23,\"mydouble\":2.34,\"" + "myboolean\":true,\"mybinary\":\"") + strBytes) + "\",\"myinstant\":\"") + strInstant) + "\",\"mynull\":null,\"myobj\":{\"foo\":\"bar\"},\"myarr\":[\"foo\",123]}";
        JsonObject obj = new JsonObject(json);
        Assert.assertEquals(json, obj.encode());
        Assert.assertEquals("foo", obj.getString("mystr"));
        Assert.assertEquals(Integer.valueOf(123), obj.getInteger("myint"));
        Assert.assertEquals(Long.valueOf(1234), obj.getLong("mylong"));
        Assert.assertEquals(Float.valueOf(1.23F), obj.getFloat("myfloat"));
        Assert.assertEquals(Double.valueOf(2.34), obj.getDouble("mydouble"));
        Assert.assertTrue(obj.getBoolean("myboolean"));
        Assert.assertArrayEquals(bytes, obj.getBinary("mybinary"));
        Assert.assertEquals(Base64.getEncoder().encodeToString(bytes), obj.getValue("mybinary"));
        Assert.assertEquals(now, obj.getInstant("myinstant"));
        Assert.assertEquals(now.toString(), obj.getValue("myinstant"));
        Assert.assertTrue(obj.containsKey("mynull"));
        JsonObject nestedObj = obj.getJsonObject("myobj");
        Assert.assertEquals("bar", nestedObj.getString("foo"));
        JsonArray nestedArr = obj.getJsonArray("myarr");
        Assert.assertEquals("foo", nestedArr.getString(0));
        Assert.assertEquals(Integer.valueOf(123), Integer.valueOf(nestedArr.getInteger(1)));
    }

    @Test
    public void testToString() {
        jsonObject.put("foo", "bar");
        Assert.assertEquals(jsonObject.encode(), jsonObject.toString());
    }

    @Test
    public void testEncodePrettily() throws Exception {
        jsonObject.put("mystr", "foo");
        jsonObject.put("myint", 123);
        jsonObject.put("mylong", 1234L);
        jsonObject.put("myfloat", 1.23F);
        jsonObject.put("mydouble", 2.34);
        jsonObject.put("myboolean", true);
        byte[] bytes = TestUtils.randomByteArray(10);
        jsonObject.put("mybinary", bytes);
        Instant now = Instant.now();
        jsonObject.put("myinstant", now);
        jsonObject.put("myobj", new JsonObject().put("foo", "bar"));
        jsonObject.put("myarr", new JsonArray().add("foo").add(123));
        String strBytes = Base64.getEncoder().encodeToString(bytes);
        String strInstant = DateTimeFormatter.ISO_INSTANT.format(now);
        String expected = ((((((((((((((((((((((((((((("{" + (Utils.LINE_SEPARATOR)) + "  \"mystr\" : \"foo\",") + (Utils.LINE_SEPARATOR)) + "  \"myint\" : 123,") + (Utils.LINE_SEPARATOR)) + "  \"mylong\" : 1234,") + (Utils.LINE_SEPARATOR)) + "  \"myfloat\" : 1.23,") + (Utils.LINE_SEPARATOR)) + "  \"mydouble\" : 2.34,") + (Utils.LINE_SEPARATOR)) + "  \"myboolean\" : true,") + (Utils.LINE_SEPARATOR)) + "  \"mybinary\" : \"") + strBytes) + "\",") + (Utils.LINE_SEPARATOR)) + "  \"myinstant\" : \"") + strInstant) + "\",") + (Utils.LINE_SEPARATOR)) + "  \"myobj\" : {") + (Utils.LINE_SEPARATOR)) + "    \"foo\" : \"bar\"") + (Utils.LINE_SEPARATOR)) + "  },") + (Utils.LINE_SEPARATOR)) + "  \"myarr\" : [ \"foo\", 123 ]") + (Utils.LINE_SEPARATOR)) + "}";
        String json = jsonObject.encodePrettily();
        Assert.assertEquals(expected, json);
    }

    // Strict JSON doesn't allow comments but we do so users can add comments to config files etc
    @Test
    public void testCommentsInJson() {
        String jsonWithComments = "// single line comment\n" + ((((((((((("/*\n" + "  This is a multi \n") + "  line comment\n") + "*/\n") + "{\n") + "// another single line comment this time inside the JSON object itself\n") + "  \"foo\": \"bar\" // and a single line comment at end of line \n") + "/*\n") + "  This is a another multi \n") + "  line comment this time inside the JSON object itself\n") + "*/\n") + "}");
        JsonObject json = new JsonObject(jsonWithComments);
        Assert.assertEquals("{\"foo\":\"bar\"}", json.encode());
    }

    @Test
    public void testInvalidJson() {
        String invalid = "qiwjdoiqwjdiqwjd";
        try {
            new JsonObject(invalid);
            Assert.fail();
        } catch (DecodeException e) {
            // OK
        }
    }

    @Test
    public void testClear() {
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        Assert.assertEquals(2, jsonObject.size());
        jsonObject.clear();
        Assert.assertEquals(0, jsonObject.size());
        Assert.assertNull(jsonObject.getValue("foo"));
        Assert.assertNull(jsonObject.getValue("quux"));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(jsonObject.isEmpty());
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        Assert.assertFalse(jsonObject.isEmpty());
        jsonObject.clear();
        Assert.assertTrue(jsonObject.isEmpty());
    }

    @Test
    public void testRemove() {
        jsonObject.put("mystr", "bar");
        jsonObject.put("myint", 123);
        Assert.assertEquals("bar", jsonObject.remove("mystr"));
        Assert.assertNull(jsonObject.getValue("mystr"));
        Assert.assertEquals(123, jsonObject.remove("myint"));
        Assert.assertNull(jsonObject.getValue("myint"));
        Assert.assertTrue(jsonObject.isEmpty());
    }

    @Test
    public void testIterator() {
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        JsonObject obj = createJsonObject();
        jsonObject.put("wibble", obj);
        Iterator<Map.Entry<String, Object>> iter = jsonObject.iterator();
        Assert.assertTrue(iter.hasNext());
        Map.Entry<String, Object> entry = iter.next();
        Assert.assertEquals("foo", entry.getKey());
        Assert.assertEquals("bar", entry.getValue());
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals("quux", entry.getKey());
        Assert.assertEquals(123, entry.getValue());
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals("wibble", entry.getKey());
        Assert.assertEquals(obj, entry.getValue());
        Assert.assertFalse(iter.hasNext());
        iter.remove();
        Assert.assertFalse(obj.containsKey("wibble"));
        Assert.assertEquals(2, jsonObject.size());
    }

    @Test
    public void testIteratorDoesntChangeObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nestedMap", new HashMap<>());
        map.put("nestedList", new ArrayList<>());
        JsonObject obj = new JsonObject(map);
        Iterator<Map.Entry<String, Object>> iter = obj.iterator();
        Map.Entry<String, Object> entry1 = iter.next();
        Assert.assertEquals("nestedMap", entry1.getKey());
        Object val1 = entry1.getValue();
        Assert.assertTrue((val1 instanceof JsonObject));
        Map.Entry<String, Object> entry2 = iter.next();
        Assert.assertEquals("nestedList", entry2.getKey());
        Object val2 = entry2.getValue();
        Assert.assertTrue((val2 instanceof JsonArray));
        Assert.assertTrue(((map.get("nestedMap")) instanceof HashMap));
        Assert.assertTrue(((map.get("nestedList")) instanceof ArrayList));
    }

    @Test
    public void testStream() {
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        JsonObject obj = createJsonObject();
        jsonObject.put("wibble", obj);
        List<Map.Entry<String, Object>> list = jsonObject.stream().collect(Collectors.toList());
        Iterator<Map.Entry<String, Object>> iter = list.iterator();
        Assert.assertTrue(iter.hasNext());
        Map.Entry<String, Object> entry = iter.next();
        Assert.assertEquals("foo", entry.getKey());
        Assert.assertEquals("bar", entry.getValue());
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals("quux", entry.getKey());
        Assert.assertEquals(123, entry.getValue());
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals("wibble", entry.getKey());
        Assert.assertEquals(obj, entry.getValue());
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testCopy() {
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        JsonObject obj = createJsonObject();
        jsonObject.put("wibble", obj);
        jsonObject.put("eek", new StringBuilder("blah"));// CharSequence

        JsonObject copy = jsonObject.copy();
        Assert.assertNotSame(jsonObject, copy);
        Assert.assertEquals(jsonObject, copy);
        copy.put("blah", "flib");
        Assert.assertFalse(jsonObject.containsKey("blah"));
        copy.remove("foo");
        Assert.assertFalse(copy.containsKey("foo"));
        Assert.assertTrue(jsonObject.containsKey("foo"));
        jsonObject.put("oob", "flarb");
        Assert.assertFalse(copy.containsKey("oob"));
        jsonObject.remove("quux");
        Assert.assertFalse(jsonObject.containsKey("quux"));
        Assert.assertTrue(copy.containsKey("quux"));
        JsonObject nested = jsonObject.getJsonObject("wibble");
        JsonObject nestedCopied = copy.getJsonObject("wibble");
        Assert.assertNotSame(nested, nestedCopied);
        Assert.assertEquals(nested, nestedCopied);
        Assert.assertEquals("blah", copy.getString("eek"));
    }

    @Test
    public void testInvalidValsOnCopy1() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("foo", new JsonObjectTest.SomeClass());
        JsonObject object = new JsonObject(invalid);
        try {
            object.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidValsOnCopy2() {
        Map<String, Object> invalid = new HashMap<>();
        Map<String, Object> invalid2 = new HashMap<>();
        invalid2.put("foo", new JsonObjectTest.SomeClass());
        invalid.put("bar", invalid2);
        JsonObject object = new JsonObject(invalid);
        try {
            object.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidValsOnCopy3() {
        Map<String, Object> invalid = new HashMap<>();
        List<Object> invalid2 = new ArrayList<>();
        invalid2.add(new JsonObjectTest.SomeClass());
        invalid.put("bar", invalid2);
        JsonObject object = new JsonObject(invalid);
        try {
            object.copy();
            Assert.fail();
        } catch (IllegalStateException e) {
            // OK
        }
    }

    class SomeClass {}

    @Test
    public void testGetMap() {
        jsonObject.put("foo", "bar");
        jsonObject.put("quux", 123);
        JsonObject obj = createJsonObject();
        jsonObject.put("wibble", obj);
        Map<String, Object> map = jsonObject.getMap();
        map.remove("foo");
        Assert.assertFalse(jsonObject.containsKey("foo"));
        map.put("bleep", "flarp");
        Assert.assertTrue(jsonObject.containsKey("bleep"));
        jsonObject.remove("quux");
        Assert.assertFalse(map.containsKey("quux"));
        jsonObject.put("wooble", "plink");
        Assert.assertTrue(map.containsKey("wooble"));
        Assert.assertSame(obj, map.get("wibble"));
    }

    @Test
    public void testCreateFromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("quux", 123);
        JsonObject obj = new JsonObject(map);
        Assert.assertEquals("bar", obj.getString("foo"));
        Assert.assertEquals(Integer.valueOf(123), obj.getInteger("quux"));
        Assert.assertSame(map, obj.getMap());
    }

    @Test
    public void testCreateFromBuffer() {
        JsonObject excepted = new JsonObject();
        excepted.put("foo", "bar");
        excepted.put("quux", 123);
        Buffer buf = Buffer.buffer(excepted.encode());
        Assert.assertEquals(excepted, new JsonObject(buf));
    }

    @Test
    public void testCreateFromMapCharSequence() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("quux", 123);
        map.put("eeek", new StringBuilder("blah"));
        JsonObject obj = new JsonObject(map);
        Assert.assertEquals("bar", obj.getString("foo"));
        Assert.assertEquals(Integer.valueOf(123), obj.getInteger("quux"));
        Assert.assertEquals("blah", obj.getString("eeek"));
        Assert.assertSame(map, obj.getMap());
    }

    @Test
    public void testCreateFromMapNestedJsonObject() {
        Map<String, Object> map = new HashMap<>();
        JsonObject nestedObj = new JsonObject().put("foo", "bar");
        map.put("nested", nestedObj);
        JsonObject obj = new JsonObject(map);
        JsonObject nestedRetrieved = obj.getJsonObject("nested");
        Assert.assertEquals("bar", nestedRetrieved.getString("foo"));
    }

    @Test
    public void testCreateFromMapNestedMap() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("foo", "bar");
        map.put("nested", nestedMap);
        JsonObject obj = new JsonObject(map);
        JsonObject nestedRetrieved = obj.getJsonObject("nested");
        Assert.assertEquals("bar", nestedRetrieved.getString("foo"));
    }

    @Test
    public void testCreateFromMapNestedJsonArray() {
        Map<String, Object> map = new HashMap<>();
        JsonArray nestedArr = new JsonArray().add("foo");
        map.put("nested", nestedArr);
        JsonObject obj = new JsonObject(map);
        JsonArray nestedRetrieved = obj.getJsonArray("nested");
        Assert.assertEquals("foo", nestedRetrieved.getString(0));
    }

    @Test
    public void testCreateFromMapNestedList() {
        Map<String, Object> map = new HashMap<>();
        List<String> nestedArr = Arrays.asList("foo");
        map.put("nested", nestedArr);
        JsonObject obj = new JsonObject(map);
        JsonArray nestedRetrieved = obj.getJsonArray("nested");
        Assert.assertEquals("foo", nestedRetrieved.getString(0));
    }

    @Test
    public void testClusterSerializable() {
        jsonObject.put("foo", "bar").put("blah", 123);
        Buffer buff = Buffer.buffer();
        jsonObject.writeToBuffer(buff);
        JsonObject deserialized = new JsonObject();
        deserialized.readFromBuffer(0, buff);
        Assert.assertEquals(jsonObject, deserialized);
    }

    @Test
    public void testNumberEquality() {
        assertNumberEquals(4, 4);
        assertNumberEquals(4, ((long) (4)));
        assertNumberEquals(4, 4.0F);
        assertNumberEquals(4, 4.0);
        assertNumberEquals(((long) (4)), ((long) (4)));
        assertNumberEquals(((long) (4)), 4.0F);
        assertNumberEquals(((long) (4)), 4.0);
        assertNumberEquals(4.0F, 4.0F);
        assertNumberEquals(4.0F, 4.0);
        assertNumberEquals(4.0, 4.0);
        assertNumberEquals(4.1, 4.1);
        assertNumberEquals(4.1F, 4.1F);
        assertNumberNotEquals(4.1F, 4.1);
        assertNumberEquals(4.5, 4.5);
        assertNumberEquals(4.5F, 4.5F);
        assertNumberEquals(4.5F, 4.5);
        assertNumberNotEquals(4, 5);
        assertNumberNotEquals(4, ((long) (5)));
        assertNumberNotEquals(4, 5.0);
        assertNumberNotEquals(4, 5.0F);
        assertNumberNotEquals(((long) (4)), ((long) (5)));
        assertNumberNotEquals(((long) (4)), 5.0);
        assertNumberNotEquals(((long) (4)), 5.0F);
        assertNumberNotEquals(4.0F, 5.0F);
        assertNumberNotEquals(4.0F, 5.0);
        assertNumberNotEquals(4.0, 5.0);
    }

    @Test
    public void testJsonObjectEquality() {
        JsonObject obj = new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3)));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3L))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 3))));
        Assert.assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 3L))));
        Assert.assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 4))));
        Assert.assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 4))));
        JsonArray array = new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3)));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3L))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 3))));
        Assert.assertEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 3L))));
        Assert.assertNotEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 4))));
        Assert.assertNotEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 4))));
    }

    @Test
    public void testJsonObjectEquality2() {
        JsonObject obj1 = new JsonObject().put("arr", new JsonArray().add("x"));
        List<Object> list = new ArrayList<>();
        list.add("x");
        Map<String, Object> map = new HashMap<>();
        map.put("arr", list);
        JsonObject obj2 = new JsonObject(map);
        Iterator<Map.Entry<String, Object>> iter = obj2.iterator();
        // There was a bug where iteration of entries caused the underlying object to change resulting in a
        // subsequent equals changing
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
        } 
        Assert.assertEquals(obj2, obj1);
    }

    @Test
    public void testPutInstantAsObject() {
        Object instant = Instant.now();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("instant", instant);
        // assert data is stored as String
        Assert.assertTrue(((jsonObject.getValue("instant")) instanceof String));
    }

    @Test
    public void testStreamCorrectTypes() throws Exception {
        String json = "{\"object1\": {\"object2\": 12}}";
        JsonObject object = new JsonObject(json);
        testStreamCorrectTypes(object.copy());
        testStreamCorrectTypes(object);
    }

    @Test
    public void testRemoveMethodReturnedObject() {
        JsonObject obj = new JsonObject();
        obj.put("simple", "bar").put("object", new JsonObject().put("name", "vert.x").put("count", 2)).put("array", new JsonArray().add(1.0).add(2.0));
        Object removed = obj.remove("missing");
        Assert.assertNull(removed);
        removed = obj.remove("simple");
        Assert.assertTrue((removed instanceof String));
        removed = obj.remove("object");
        Assert.assertTrue((removed instanceof JsonObject));
        Assert.assertEquals(getString("name"), "vert.x");
        removed = obj.remove("array");
        Assert.assertTrue((removed instanceof JsonArray));
        Assert.assertEquals(getDouble(0), 1.0, 0.0);
    }

    @Test
    public void testOrder() {
        List<String> expectedKeys = new ArrayList<>();
        int size = 100;
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < size; i++) {
            sb.append("\"key-").append(i).append("\":").append(i).append(",");
            expectedKeys.add(("key-" + i));
        }
        sb.setCharAt(((sb.length()) - 1), '}');
        JsonObject obj = new JsonObject(sb.toString());
        List<String> keys = new ArrayList<>();
        // ordered because of Jackson uses a LinkedHashMap
        obj.forEach(( e) -> keys.add(e.getKey()));
        Assert.assertEquals(expectedKeys, keys);
        keys.clear();
        // Ordered because we preserve the LinkedHashMap
        obj.copy().forEach(( e) -> keys.add(e.getKey()));
        Assert.assertEquals(expectedKeys, keys);
    }

    @Test
    public void testMergeInNullValue() {
        JsonObject obj = new JsonObject();
        obj.put("key", "value");
        JsonObject otherObj = new JsonObject();
        otherObj.putNull("key");
        obj.mergeIn(otherObj, true);
        Assert.assertNull(obj.getString("key", "other"));
    }
}

