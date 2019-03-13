/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import JSONCompareMode.NON_EXTENSIBLE;
import JSONObject.NULL;
import ParseException.CONNECTION_FAILED;
import bolts.Task;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.IsInstanceOf;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static ParseException.CONNECTION_FAILED;


// For android.os.Looper
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseConfigTest extends ResetPluginsParseTest {
    // region testConstructor
    @Test
    public void testDefaultConstructor() {
        ParseConfig config = new ParseConfig();
        Assert.assertEquals(0, config.params.size());
    }

    @Test
    public void testDecodeWithValidJsonObject() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        JSONObject json = new JSONObject();
        json.put("params", NoObjectsEncoder.get().encode(params));
        ParseConfig config = ParseConfig.decode(json, ParseDecoder.get());
        Assert.assertEquals(1, config.params.size());
        Assert.assertEquals("value", config.params.get("string"));
    }

    @Test(expected = RuntimeException.class)
    public void testDecodeWithInvalidJsonObject() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        JSONObject json = new JSONObject();
        json.put("error", NoObjectsEncoder.get().encode(params));
        ParseConfig.decode(json, ParseDecoder.get());
    }

    // endregion
    // region testGetInBackground
    @Test
    public void testGetInBackgroundSuccess() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        ParseConfig config = new ParseConfig(params);
        ParseConfigController controller = mockParseConfigControllerWithResponse(config);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        Task<ParseConfig> configTask = ParseConfig.getInBackground();
        ParseTaskUtils.wait(configTask);
        ParseConfig configAgain = configTask.getResult();
        Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
        Assert.assertEquals(1, configAgain.params.size());
        Assert.assertEquals("value", configAgain.params.get("string"));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testGetInBackgroundFail() throws Exception {
        ParseException exception = new ParseException(CONNECTION_FAILED, "error");
        ParseConfigController controller = mockParseConfigControllerWithException(exception);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        Task<ParseConfig> configTask = ParseConfig.getInBackground();
        configTask.waitForCompletion();
        Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
        Assert.assertThat(configTask.getError(), IsInstanceOf.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
        Assert.assertEquals("error", configTask.getError().getMessage());
    }

    @Test
    public void testGetInBackgroundWithCallbackSuccess() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        ParseConfig config = new ParseConfig(params);
        ParseConfigController controller = mockParseConfigControllerWithResponse(config);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        final Semaphore done = new Semaphore(0);
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                Assert.assertEquals(1, config.params.size());
                Assert.assertEquals("value", config.params.get("string"));
                done.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
    }

    @Test
    public void testGetInBackgroundWithCallbackFail() throws Exception {
        ParseException exception = new ParseException(CONNECTION_FAILED, "error");
        ParseConfigController controller = mockParseConfigControllerWithException(exception);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        final Semaphore done = new Semaphore(0);
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                Assert.assertEquals(CONNECTION_FAILED, e.getCode());
                Assert.assertEquals("error", e.getMessage());
                done.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
    }

    @Test
    public void testGetSyncSuccess() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        ParseConfig config = new ParseConfig(params);
        ParseConfigController controller = mockParseConfigControllerWithResponse(config);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        ParseConfig configAgain = ParseConfig.get();
        Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
        Assert.assertEquals(1, configAgain.params.size());
        Assert.assertEquals("value", configAgain.params.get("string"));
    }

    @Test
    public void testGetSyncFail() {
        ParseException exception = new ParseException(CONNECTION_FAILED, "error");
        ParseConfigController controller = mockParseConfigControllerWithException(exception);
        ParseCorePlugins.getInstance().registerConfigController(controller);
        try {
            ParseConfig.get();
            Assert.fail("Should throw an exception");
        } catch (ParseException e) {
            Mockito.verify(controller, Mockito.times(1)).getAsync(ArgumentMatchers.anyString());
            Assert.assertEquals(CONNECTION_FAILED, e.getCode());
            Assert.assertEquals("error", e.getMessage());
        }
    }

    // endregion
    // region testGetCurrentConfig
    @Test
    public void testGetCurrentConfigSuccess() {
        final Map<String, Object> params = new HashMap<>();
        params.put("string", "value");
        ParseConfig config = new ParseConfig(params);
        ParseConfigController controller = new ParseConfigController(Mockito.mock(ParseHttpClient.class), mockParseCurrentConfigControllerWithResponse(config));
        ParseCorePlugins.getInstance().registerConfigController(controller);
        ParseConfig configAgain = ParseConfig.getCurrentConfig();
        Assert.assertSame(config, configAgain);
    }

    @Test
    public void testGetCurrentConfigFail() {
        ParseException exception = new ParseException(CONNECTION_FAILED, "error");
        ParseConfigController controller = new ParseConfigController(Mockito.mock(ParseHttpClient.class), mockParseCurrentConfigControllerWithException(exception));
        ParseCorePlugins.getInstance().registerConfigController(controller);
        ParseConfig configAgain = ParseConfig.getCurrentConfig();
        // Make sure we get an empty ParseConfig
        Assert.assertEquals(0, configAgain.getParams().size());
    }

    // endregion
    // region testGetBoolean
    @Test
    public void testGetBooleanKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", true);
        ParseConfig config = new ParseConfig(params);
        Assert.assertTrue(config.getBoolean("key"));
        Assert.assertTrue(config.getBoolean("key", false));
    }

    @Test
    public void testGetBooleanKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", true);
        ParseConfig config = new ParseConfig(params);
        Assert.assertFalse(config.getBoolean("wrongKey"));
        Assert.assertFalse(config.getBoolean("wrongKey", false));
    }

    @Test
    public void testGetBooleanKeyExistValueNotBoolean() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertFalse(config.getBoolean("key"));
        Assert.assertFalse(config.getBoolean("key", false));
    }

    // endregion
    // region testGetInt
    @Test
    public void testGetIntKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 998);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getInt("key"), 998);
        Assert.assertEquals(998, config.getInt("key", 100));
    }

    @Test
    public void testGetIntKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 998);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getInt("wrongKey"), 0);
        Assert.assertEquals(100, config.getInt("wrongKey", 100));
    }

    // endregion
    // region testGetDouble
    @Test
    public void testGetDoubleKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 998.1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getDouble("key"), 998.1, 1.0E-4);
        Assert.assertEquals(998.1, config.getDouble("key", 100.1), 1.0E-4);
    }

    @Test
    public void testGetDoubleKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 998.1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getDouble("wrongKey"), 0.0, 1.0E-4);
        Assert.assertEquals(100.1, config.getDouble("wrongKey", 100.1), 1.0E-4);
    }

    // endregion
    // region testGetLong
    @Test
    public void testGetLongKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", ((long) (998)));
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getLong("key"), ((long) (998)));
        Assert.assertEquals(((long) (998)), config.getLong("key", ((long) (100))));
    }

    @Test
    public void testGetLongKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", ((long) (998)));
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getLong("wrongKey"), ((long) (0)));
        Assert.assertEquals(((long) (100)), config.getLong("wrongKey", ((long) (100))));
    }

    // endregion
    // region testGet
    @Test
    public void testGetKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.get("key"), "value");
        Assert.assertEquals("value", config.get("key", "haha"));
    }

    @Test
    public void testGetKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.get("wrongKey"));
        Assert.assertEquals("haha", config.get("wrongKey", "haha"));
    }

    @Test
    public void testGetKeyExistValueNull() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.get("key"));
        Assert.assertNull(config.get("key", "haha"));
        Assert.assertNull(config.get("keyAgain"));
        Assert.assertNull(config.get("keyAgain", "haha"));
    }

    // endregion
    // region testGetString
    @Test
    public void testGetStringKeyExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(config.getString("key"), "value");
        Assert.assertEquals("value", config.getString("key", "haha"));
    }

    @Test
    public void testGetStringKeyNotExist() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getString("wrongKey"));
        Assert.assertEquals("haha", config.getString("wrongKey", "haha"));
    }

    @Test
    public void testGetStringKeyExistValueNotString() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getString("key"));
        Assert.assertEquals("haha", config.getString("key", "haha"));
    }

    @Test
    public void testGetStringKeyExistValueNull() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getString("key"));
        Assert.assertNull(config.getString("key", "haha"));
        Assert.assertNull(config.getString("keyAgain"));
        Assert.assertNull(config.getString("keyAgain", "haha"));
    }

    // endregion
    // region testGetDate
    @Test
    public void testGetDateKeyExist() {
        final Date date = new Date();
        date.setTime(10);
        Date dateAgain = new Date();
        dateAgain.setTime(20);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", date);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(date.getTime(), config.getDate("key").getTime());
        Assert.assertEquals(date.getTime(), config.getDate("key", dateAgain).getTime());
    }

    @Test
    public void testGetDateKeyNotExist() {
        final Date date = new Date();
        date.setTime(10);
        Date dateAgain = new Date();
        dateAgain.setTime(10);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", date);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getDate("wrongKey"));
        Assert.assertSame(dateAgain, config.getDate("wrongKey", dateAgain));
    }

    @Test
    public void testGetDateKeyExistValueNotDate() {
        Date date = new Date();
        date.setTime(20);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getDate("key"));
        Assert.assertSame(date, config.getDate("key", date));
    }

    @Test
    public void testGetDateKeyExistValueNull() {
        Date date = new Date();
        date.setTime(20);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getDate("key"));
        Assert.assertNull(config.getDate("key", date));
        Assert.assertNull(config.getDate("keyAgain"));
        Assert.assertNull(config.getDate("keyAgain", date));
    }

    // endregion
    // region testGetList
    @Test
    public void testGetListKeyExist() {
        final List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        final List<String> listAgain = new ArrayList<>();
        list.add("fooAgain");
        list.add("barAgain");
        list.add("bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", list);
        ParseConfig config = new ParseConfig(params);
        Assert.assertArrayEquals(list.toArray(), config.getList("key").toArray());
        Assert.assertArrayEquals(list.toArray(), config.getList("key", listAgain).toArray());
    }

    @Test
    public void testGetListKeyNotExist() {
        final List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        final List<String> listAgain = new ArrayList<>();
        list.add("fooAgain");
        list.add("barAgain");
        list.add("bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", list);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getList("wrongKey"));
        Assert.assertSame(listAgain, config.getList("wrongKey", listAgain));
    }

    @Test
    public void testGetListKeyExistValueNotList() {
        final List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getList("key"));
        Assert.assertSame(list, config.getList("key", list));
    }

    @Test
    public void testGetListKeyExistValueNull() {
        final List<String> list = new ArrayList<>();
        list.add("fooAgain");
        list.add("barAgain");
        list.add("bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getList("key"));
        Assert.assertNull(config.getList("key", list));
        Assert.assertNull(config.getList("keyAgain"));
        Assert.assertNull(config.getList("keyAgain", list));
    }

    // endregion
    // region testGetNumber
    @Test
    public void testGetNumberKeyExist() {
        final Number number = 1;
        Number numberAgain = 2;
        final Map<String, Object> params = new HashMap<>();
        params.put("key", number);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(number, config.getNumber("key"));
        Assert.assertEquals(number, config.getNumber("key", numberAgain));
    }

    @Test
    public void testGetNumberKeyNotExist() {
        final Number number = 1;
        Number numberAgain = 2;
        final Map<String, Object> params = new HashMap<>();
        params.put("key", number);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getNumber("wrongKey"));
        Assert.assertSame(numberAgain, config.getNumber("wrongKey", numberAgain));
    }

    @Test
    public void testGetNumberKeyExistValueNotNumber() {
        Number number = 2;
        final Map<String, Object> params = new HashMap<>();
        params.put("key", new ArrayList<String>());
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getNumber("key"));
        Assert.assertSame(number, config.getNumber("key", number));
    }

    @Test
    public void testGetNumberKeyExistValueNull() {
        Number number = 2;
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getNumber("key"));
        Assert.assertNull(config.getNumber("key", number));
        Assert.assertNull(config.getNumber("keyAgain"));
        Assert.assertNull(config.getNumber("keyAgain", number));
    }

    // endregion
    // region testGetMap
    @Test
    public void testGetMapKeyExist() {
        final Map<String, Object> map = new HashMap<>();
        map.put("first", "foo");
        map.put("second", "bar");
        map.put("third", "baz");
        Map<String, Object> mapAgain = new HashMap<>();
        mapAgain.put("firstAgain", "fooAgain");
        mapAgain.put("secondAgain", "barAgain");
        mapAgain.put("thirdAgain", "bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", map);
        ParseConfig config = new ParseConfig(params);
        Map<String, Object> mapConfig = config.getMap("key");
        Assert.assertEquals(3, mapConfig.size());
        Assert.assertEquals("foo", mapConfig.get("first"));
        Assert.assertEquals("bar", mapConfig.get("second"));
        Assert.assertEquals("baz", mapConfig.get("third"));
        Assert.assertSame(mapConfig, config.getMap("key", mapAgain));
    }

    @Test
    public void testGetMapKeyNotExist() {
        final Map<String, Object> map = new HashMap<>();
        map.put("first", "foo");
        map.put("second", "bar");
        map.put("third", "baz");
        Map<String, Object> mapAgain = new HashMap<>();
        mapAgain.put("firstAgain", "fooAgain");
        mapAgain.put("secondAgain", "barAgain");
        mapAgain.put("thirdAgain", "bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", map);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getMap("wrongKey"));
        Assert.assertSame(mapAgain, config.getMap("wrongKey", mapAgain));
    }

    @Test
    public void testGetMapKeyExistValueNotMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstAgain", "fooAgain");
        map.put("secondAgain", "barAgain");
        map.put("thirdAgain", "bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getMap("key"));
        Assert.assertSame(map, config.getMap("key", map));
    }

    @Test
    public void testGetMapKeyExistValueNull() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstAgain", "fooAgain");
        map.put("secondAgain", "barAgain");
        map.put("thirdAgain", "bazAgain");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getMap("key"));
        Assert.assertNull(config.getMap("key", map));
        Assert.assertNull(config.getMap("keyAgain"));
        Assert.assertNull(config.getMap("keyAgain", map));
    }

    // endregion
    // region testGetJsonObject
    @Test
    public void testGetJsonObjectKeyExist() throws Exception {
        final Map<String, String> value = new HashMap<>();
        value.put("key", "value");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", value);
        final JSONObject json = new JSONObject(value);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(json, config.getJSONObject("key"), NON_EXTENSIBLE);
        Assert.assertEquals(json, config.getJSONObject("key", new JSONObject()), NON_EXTENSIBLE);
    }

    @Test
    public void testGetJsonObjectKeyNotExist() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final JSONObject jsonAgain = new JSONObject();
        jsonAgain.put("keyAgain", "valueAgain");
        final Map<String, Object> params;
        params = new HashMap<>();
        params.put("key", json);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONObject("wrongKey"));
        // TODO(mengyan) ParseConfig.getJSONObject should return jsonAgain, but due to the error in
        // ParseConfig.getJsonObject, this returns null right now. Revise when we correct the logic
        // for ParseConfig.getJsonObject.
        Assert.assertNull(config.getJSONObject("wrongKey", jsonAgain));
    }

    @Test
    public void testGetJsonObjectKeyExistValueNotJsonObject() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONObject("key"));
        // TODO(mengyan) ParseConfig.getJSONObject should return json, but due to the error in
        // ParseConfig.getJsonObject, this returns null right now. Revise when we correct the logic
        // for ParseConfig.getJsonObject.
        Assert.assertNull(config.getJSONObject("key", json));
    }

    @Test
    public void testGetJsonObjectKeyExistValueNull() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONObject("key"));
        Assert.assertNull(config.getJSONObject("key", json));
        Assert.assertNull(config.getJSONObject("keyAgain"));
        Assert.assertNull(config.getJSONObject("keyAgain", json));
    }

    // endregion
    // region testGetJsonArray
    @Test
    public void testGetJsonArrayKeyExist() throws Exception {
        final Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        final Map<String, String> mapAgain = new HashMap<>();
        mapAgain.put("keyAgain", "valueAgain");
        final List<Map<String, String>> value = new ArrayList<>();
        value.add(map);
        value.add(mapAgain);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", value);
        JSONArray jsonArray = new JSONArray(value);
        ParseConfig config = new ParseConfig(params);
        Assert.assertEquals(jsonArray, config.getJSONArray("key"), NON_EXTENSIBLE);
        Assert.assertEquals(jsonArray, config.getJSONArray("key", new JSONArray()), NON_EXTENSIBLE);
    }

    @Test
    public void testGetJsonArrayKeyNotExist() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final JSONObject jsonAgain = new JSONObject();
        jsonAgain.put("keyAgain", "valueAgain");
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, json);
        jsonArray.put(1, jsonAgain);
        final JSONArray jsonArrayAgain = new JSONArray();
        jsonArray.put(0, jsonAgain);
        jsonArray.put(1, json);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", jsonArray);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONArray("wrongKey"));
        // TODO(mengyan) ParseConfig.getJSONArray should return default jsonArrayAgain, but due to the
        // error in ParseConfig.getJSONArray, this returns null right now. Revise when we correct the
        // logic for ParseConfig.getJSONArray.
        Assert.assertNull(config.getJSONArray("wrongKey", jsonArrayAgain));
    }

    @Test
    public void testGetJsonArrayKeyExistValueNotJsonObject() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final JSONObject jsonAgain = new JSONObject();
        jsonAgain.put("keyAgain", "valueAgain");
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, json);
        jsonArray.put(1, jsonAgain);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONArray("key"));
        // TODO(mengyan) ParseConfig.getJSONArray should return default jsonArray, but due to the
        // error in ParseConfig.getJSONArray, this returns null right now. Revise when we correct the
        // logic for ParseConfig.getJSONArray.
        Assert.assertNull(config.getJSONArray("key", jsonArray));
    }

    @Test
    public void testGetJsonArrayKeyExistValueNull() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("key", "value");
        final JSONObject jsonAgain = new JSONObject();
        jsonAgain.put("keyAgain", "valueAgain");
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, json);
        jsonArray.put(1, jsonAgain);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getJSONArray("key"));
        Assert.assertNull(config.getJSONArray("key", jsonArray));
        Assert.assertNull(config.getJSONArray("keyAgain"));
        Assert.assertNull(config.getJSONArray("keyAgain", jsonArray));
    }

    // endregion
    // region testGetParseGeoPoint
    @Test
    public void testGetParseGeoPointKeyExist() {
        final ParseGeoPoint geoPoint = new ParseGeoPoint(44.484, 26.029);
        ParseGeoPoint geoPointAgain = new ParseGeoPoint(45.484, 27.029);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", geoPoint);
        ParseConfig config = new ParseConfig(params);
        ParseGeoPoint geoPointConfig = config.getParseGeoPoint("key");
        Assert.assertEquals(geoPoint.getLongitude(), geoPointConfig.getLongitude(), 1.0E-4);
        Assert.assertEquals(geoPoint.getLatitude(), geoPointConfig.getLatitude(), 1.0E-4);
        Assert.assertSame(geoPointConfig, config.getParseGeoPoint("key", geoPointAgain));
    }

    @Test
    public void testGetParseGeoPointKeyNotExist() {
        final ParseGeoPoint geoPoint = new ParseGeoPoint(44.484, 26.029);
        ParseGeoPoint geoPointAgain = new ParseGeoPoint(45.484, 27.029);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", geoPoint);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseGeoPoint("wrongKey"));
        Assert.assertSame(geoPointAgain, config.getParseGeoPoint("wrongKey", geoPointAgain));
    }

    @Test
    public void testGetParseGeoPointKeyExistValueNotParseGeoPoint() {
        ParseGeoPoint geoPoint = new ParseGeoPoint(45.484, 27.029);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseGeoPoint("key"));
        Assert.assertSame(geoPoint, config.getParseGeoPoint("key", geoPoint));
    }

    @Test
    public void testGetParseGeoPointKeyExistValueNull() {
        ParseGeoPoint geoPoint = new ParseGeoPoint(45.484, 27.029);
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", null);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseGeoPoint("key"));
        Assert.assertNull(config.getParseGeoPoint("key", geoPoint));
        Assert.assertNull(config.getParseGeoPoint("keyAgain"));
        Assert.assertNull(config.getParseGeoPoint("keyAgain", geoPoint));
    }

    // endregion
    // region testGetParseFile
    @Test
    public void testGetParseFileKeyExist() {
        final ParseFile file = new ParseFile(new ParseFile.State.Builder().name("image.png").url("http://yarr.com/image.png").build());
        ParseFile fileAgain = new ParseFile(new ParseFile.State.Builder().name("file.txt").url("http://yarr.com/file.txt").build());
        final Map<String, Object> params = new HashMap<>();
        params.put("key", file);
        ParseConfig config = new ParseConfig(params);
        ParseFile fileConfig = config.getParseFile("key");
        Assert.assertEquals(file.getName(), fileConfig.getName());
        Assert.assertEquals(file.getUrl(), fileConfig.getUrl());
        Assert.assertSame(fileConfig, config.getParseFile("key", fileAgain));
    }

    @Test
    public void testGetParseFileKeyNotExist() {
        final ParseFile file = new ParseFile(new ParseFile.State.Builder().name("image.png").url("http://yarr.com/image.png").build());
        ParseFile fileAgain = new ParseFile(new ParseFile.State.Builder().name("file.txt").url("http://yarr.com/file.txt").build());
        final Map<String, Object> params = new HashMap<>();
        params.put("key", file);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseFile("wrongKey"));
        Assert.assertSame(fileAgain, config.getParseFile("wrongKey", fileAgain));
    }

    @Test
    public void testGetParseFileKeyExistValueNotParseFile() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().name("file.txt").url("http://yarr.com/file.txt").build());
        final Map<String, Object> params = new HashMap<>();
        params.put("key", 1);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseFile("key"));
        Assert.assertSame(file, config.getParseFile("key", file));
    }

    @Test
    public void testGetParseFileKeyExistValueNull() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().name("file.txt").url("http://yarr.com/file.txt").build());
        final Map<String, Object> params = new HashMap<>();
        params.put("key", NULL);
        params.put("keyAgain", NULL);
        ParseConfig config = new ParseConfig(params);
        Assert.assertNull(config.getParseFile("key"));
        Assert.assertNull(config.getParseFile("key", file));
        Assert.assertNull(config.getParseFile("keyAgain"));
        Assert.assertNull(config.getParseFile("keyAgain", file));
    }

    // endregion
    // region testToString
    @Test
    public void testToStringList() {
        final List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        final Map<String, Object> params = new HashMap<>();
        params.put("list", list);
        ParseConfig config = new ParseConfig(params);
        String configStr = config.toString();
        Assert.assertTrue(configStr.contains("ParseConfig"));
        Assert.assertTrue(configStr.contains("list"));
        Assert.assertTrue(configStr.contains("bar"));
        Assert.assertTrue(configStr.contains("baz"));
        Assert.assertTrue(configStr.contains("foo"));
    }

    @Test
    public void testToStringMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("first", "foo");
        map.put("second", "bar");
        map.put("third", "baz");
        final Map<String, Object> params = new HashMap<>();
        params.put("map", map);
        ParseConfig config = new ParseConfig(params);
        String configStr = config.toString();
        Assert.assertTrue(configStr.contains("ParseConfig"));
        Assert.assertTrue(configStr.contains("map"));
        Assert.assertTrue(configStr.contains("second=bar"));
        Assert.assertTrue(configStr.contains("third=baz"));
        Assert.assertTrue(configStr.contains("first=foo"));
    }

    @Test
    public void testToStringParseGeoPoint() {
        final ParseGeoPoint geoPoint = new ParseGeoPoint(45.484, 27.029);
        final Map<String, Object> params = new HashMap<>();
        params.put("geoPoint", geoPoint);
        ParseConfig config = new ParseConfig(params);
        String configStr = config.toString();
        Assert.assertTrue(configStr.contains("ParseGeoPoint"));
        Assert.assertTrue(configStr.contains("45.484"));
        Assert.assertTrue(configStr.contains("27.029"));
    }
}

