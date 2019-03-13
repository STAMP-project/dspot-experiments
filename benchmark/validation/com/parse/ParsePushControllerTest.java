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
import ParseException.CONNECTION_FAILED;
import ParsePush.KEY_DATA_MESSAGE;
import ParseRESTPushCommand.KEY_CHANNELS;
import ParseRESTPushCommand.KEY_DATA;
import ParseRESTPushCommand.KEY_DEVICE_TYPE;
import ParseRESTPushCommand.KEY_EXPIRATION_INTERVAL;
import ParseRESTPushCommand.KEY_EXPIRATION_TIME;
import ParseRESTPushCommand.KEY_PUSH_TIME;
import ParseRESTPushCommand.KEY_WHERE;
import bolts.Task;
import com.parse.http.ParseHttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For SSLSessionCache
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParsePushControllerTest {
    @Test
    public void testBuildRESTSendPushCommandWithChannelSet() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        List<String> expectedChannelSet = new ArrayList<String>() {
            {
                add("foo");
                add("bar");
                add("yarr");
            }
        };
        ParsePush.State state = new ParsePush.State.Builder().data(data).channelSet(expectedChannelSet).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_PUSH_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_INTERVAL));
        // Verify device type and query
        Assert.assertFalse(jsonParameters.has(KEY_WHERE));
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        JSONArray pushChannels = jsonParameters.getJSONArray(KEY_CHANNELS);
        Assert.assertEquals(new JSONArray(expectedChannelSet), pushChannels, NON_EXTENSIBLE);
    }

    @Test
    public void testBuildRESTSendPushCommandWithExpirationTime() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParsePush.State state = new ParsePush.State.Builder().data(data).expirationTime(((long) (1400000000))).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_PUSH_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_INTERVAL));
        Assert.assertFalse(jsonParameters.has(KEY_CHANNELS));
        // Verify device type and query
        Assert.assertEquals("{}", jsonParameters.get(KEY_WHERE).toString());
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        Assert.assertEquals(1400000000, jsonParameters.getLong(KEY_EXPIRATION_TIME));
    }

    @Test
    public void testBuildRESTSendPushCommandWithPushTime() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        long pushTime = ((System.currentTimeMillis()) / 1000) + 1000;
        ParsePush.State state = new ParsePush.State.Builder().data(data).pushTime(pushTime).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_INTERVAL));
        Assert.assertFalse(jsonParameters.has(KEY_CHANNELS));
        // Verify device type and query
        Assert.assertEquals("{}", jsonParameters.get(KEY_WHERE).toString());
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        Assert.assertEquals(pushTime, jsonParameters.getLong(KEY_PUSH_TIME));
    }

    @Test
    public void testBuildRESTSendPushCommandWithExpirationTimeInterval() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParsePush.State state = new ParsePush.State.Builder().data(data).expirationTimeInterval(((long) (86400))).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_PUSH_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_CHANNELS));
        // Verify device type and query
        Assert.assertEquals("{}", jsonParameters.get(KEY_WHERE).toString());
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        Assert.assertEquals(86400, jsonParameters.getLong(KEY_EXPIRATION_INTERVAL));
    }

    @Test
    public void testBuildRESTSendPushCommandWithQuery() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo("language", "en/US");
        query.whereLessThan("version", "1.2");
        ParsePush.State state = new ParsePush.State.Builder().data(data).query(query).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_PUSH_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_INTERVAL));
        Assert.assertFalse(jsonParameters.getJSONObject(KEY_WHERE).has(KEY_DEVICE_TYPE));
        Assert.assertFalse(jsonParameters.has(KEY_CHANNELS));
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        JSONObject pushQuery = jsonParameters.getJSONObject(KEY_WHERE);
        Assert.assertEquals("en/US", pushQuery.getString("language"));
        JSONObject inequality = pushQuery.getJSONObject("version");
        Assert.assertEquals("1.2", inequality.getString("$lt"));
    }

    // endregion
    // region testSendInBackground
    @Test
    public void testBuildRESTSendPushCommandWithPushToIOSAndAndroid() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParsePushController controller = new ParsePushController(restClient);
        // Build PushState
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParsePush.State state = new ParsePush.State.Builder().data(data).build();
        // Build command
        ParseRESTCommand pushCommand = controller.buildRESTSendPushCommand(state, "sessionToken");
        // Verify command
        JSONObject jsonParameters = pushCommand.jsonParameters;
        Assert.assertFalse(jsonParameters.has(KEY_PUSH_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_TIME));
        Assert.assertFalse(jsonParameters.has(KEY_EXPIRATION_INTERVAL));
        Assert.assertFalse(jsonParameters.has(KEY_CHANNELS));
        Assert.assertEquals("hello world", jsonParameters.getJSONObject(KEY_DATA).getString(KEY_DATA_MESSAGE));
        Assert.assertFalse(jsonParameters.getJSONObject(KEY_WHERE).has(KEY_DEVICE_TYPE));
    }

    @Test
    public void testSendInBackgroundSuccess() throws Exception {
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("result", "OK");
        ParseHttpClient restClient = mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        ParsePushController controller = new ParsePushController(restClient);
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParsePush.State state = new ParsePush.State.Builder().data(data).build();
        Task<Void> pushTask = controller.sendInBackground(state, "sessionToken");
        // Verify task complete
        ParseTaskUtils.wait(pushTask);
        // Verify httpclient execute encough times
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
    }

    // endregion
    @Test
    public void testSendInBackgroundFailWithIOException() throws Exception {
        // TODO(mengyan): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        ParsePushController controller = new ParsePushController(restClient);
        JSONObject data = new JSONObject();
        data.put(KEY_DATA_MESSAGE, "hello world");
        ParsePush.State state = new ParsePush.State.Builder().data(data).build();
        Task<Void> pushTask = controller.sendInBackground(state, "sessionToken");
        // Do not use ParseTaskUtils.wait() since we do not want to throw the exception
        pushTask.waitForCompletion();
        // Verify httpClient is tried enough times
        // TODO(mengyan): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(restClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(pushTask.isFaulted());
        Exception error = pushTask.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
    }
}

