/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseException.CONNECTION_FAILED;
import bolts.Task;
import com.parse.http.ParseHttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ParseConfigControllerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    // region testConstructor
    @Test
    public void testConstructor() {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseCurrentConfigController currentConfigController = Mockito.mock(ParseCurrentConfigController.class);
        ParseConfigController controller = new ParseConfigController(restClient, currentConfigController);
        Assert.assertSame(currentConfigController, controller.getCurrentConfigController());
    }

    // endregion
    // region testGetAsync
    @Test
    public void testGetAsyncSuccess() throws Exception {
        // Construct sample response from server
        final Date date = new Date();
        final ParseFile file = new ParseFile(new ParseFile.State.Builder().name("image.png").url("http://yarr.com/image.png").build());
        final ParseGeoPoint geoPoint = new ParseGeoPoint(44.484, 26.029);
        final List<Object> list = new ArrayList<Object>() {
            {
                add("foo");
                add("bar");
                add("baz");
            }
        };
        final Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("first", "foo");
                put("second", "bar");
                put("third", "baz");
            }
        };
        final Map<String, Object> sampleConfigParameters = new HashMap<String, Object>() {
            {
                put("string", "value");
                put("int", 42);
                put("double", 0.2778);
                put("trueBool", true);
                put("falseBool", false);
                put("date", date);
                put("file", file);
                put("geoPoint", geoPoint);
                put("array", list);
                put("object", map);
            }
        };
        JSONObject responseJson = new JSONObject() {
            {
                put("params", NoObjectsEncoder.get().encode(sampleConfigParameters));
            }
        };
        // Make ParseConfigController and call getAsync
        ParseHttpClient restClient = mockParseHttpClientWithResponse(responseJson, 200, "OK");
        ParseCurrentConfigController currentConfigController = mockParseCurrentConfigController();
        ParseConfigController configController = new ParseConfigController(restClient, currentConfigController);
        Task<ParseConfig> configTask = configController.getAsync(null);
        ParseConfig config = ParseTaskUtils.wait(configTask);
        // Verify httpClient is called
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        // Verify currentConfigController is called
        Mockito.verify(currentConfigController, Mockito.times(1)).setCurrentConfigAsync(ArgumentMatchers.eq(config));
        // Verify ParseConfig we get, do not use ParseConfig getter to keep test separately
        Map<String, Object> paramsAgain = config.getParams();
        Assert.assertEquals(10, paramsAgain.size());
        Assert.assertEquals("value", paramsAgain.get("string"));
        Assert.assertEquals(42, paramsAgain.get("int"));
        Assert.assertEquals(0.2778, paramsAgain.get("double"));
        Assert.assertTrue(((Boolean) (paramsAgain.get("trueBool"))));
        Assert.assertFalse(((Boolean) (paramsAgain.get("falseBool"))));
        Assert.assertEquals(date, paramsAgain.get("date"));
        ParseFile fileAgain = ((ParseFile) (paramsAgain.get("file")));
        Assert.assertEquals(file.getUrl(), fileAgain.getUrl());
        Assert.assertEquals(file.getName(), fileAgain.getName());
        ParseGeoPoint geoPointAgain = ((ParseGeoPoint) (paramsAgain.get("geoPoint")));
        Assert.assertEquals(geoPoint.getLatitude(), geoPointAgain.getLatitude(), 1.0E-7);
        Assert.assertEquals(geoPoint.getLongitude(), geoPointAgain.getLongitude(), 1.0E-7);
        List<Object> listAgain = ((List<Object>) (paramsAgain.get("array")));
        Assert.assertArrayEquals(list.toArray(), listAgain.toArray());
        Map<String, Object> mapAgain = ((Map<String, Object>) (paramsAgain.get("object")));
        Assert.assertEquals(map.size(), mapAgain.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Assert.assertEquals(entry.getValue(), mapAgain.get(entry.getKey()));
        }
    }

    @Test
    public void testGetAsyncFailureWithConnectionFailure() throws Exception {
        // TODO(mengyan): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        // Make ParseConfigController and call getAsync
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        ParseCurrentConfigController currentConfigController = mockParseCurrentConfigController();
        ParseConfigController configController = new ParseConfigController(restClient, currentConfigController);
        Task<ParseConfig> configTask = configController.getAsync(null);
        // Do not use ParseTaskUtils.wait() since we do not want to throw the exception
        configTask.waitForCompletion();
        // Verify httpClient is tried enough times
        // TODO(mengyan): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(restClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(configTask.isFaulted());
        Exception error = configTask.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
        // Verify currentConfigController is not called
        Mockito.verify(currentConfigController, Mockito.times(0)).setCurrentConfigAsync(ArgumentMatchers.any(ParseConfig.class));
    }
}

