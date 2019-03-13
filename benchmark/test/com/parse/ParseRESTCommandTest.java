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
import ParseHttpRequest.Method.GET;
import ParseHttpRequest.Method.POST;
import ParseHttpRequest.Method.PUT;
import bolts.Task;
import com.parse.http.ParseHttpRequest;
import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static ParseRESTCommand.server;


// For org.json
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ParseRESTCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInitializationWithDefaultParseServerURL() throws Exception {
        server = new URL("https://api.parse.com/1/");
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath("events/Appopened").build();
        Assert.assertEquals("https://api.parse.com/1/events/Appopened", command.url);
    }

    @Test
    public void testPermanentFailures() throws Exception {
        JSONObject json = new JSONObject();
        json.put("code", 1337);
        json.put("error", "mock error");
        ParseHttpResponse response = ParseRESTCommandTest.newMockParseHttpResponse(400, json);
        ParseHttpClient client = Mockito.mock(ParseHttpClient.class);
        Mockito.when(client.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(response);
        ParseRESTCommand command = new ParseRESTCommand.Builder().method(GET).installationId("fake_installation_id").build();
        Task<JSONObject> task = command.executeAsync(client);
        task.waitForCompletion();
        Mockito.verify(client, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Assert.assertEquals(1337, getCode());
        Assert.assertEquals("mock error", task.getError().getMessage());
    }

    @Test
    public void testTemporaryFailures() throws Exception {
        JSONObject json = new JSONObject();
        json.put("code", 1337);
        json.put("error", "mock error");
        ParseHttpResponse response1 = ParseRESTCommandTest.newMockParseHttpResponse(500, json);
        ParseHttpResponse response2 = ParseRESTCommandTest.newMockParseHttpResponse(500, json);
        ParseHttpResponse response3 = ParseRESTCommandTest.newMockParseHttpResponse(500, json);
        ParseHttpResponse response4 = ParseRESTCommandTest.newMockParseHttpResponse(500, json);
        ParseHttpResponse response5 = ParseRESTCommandTest.newMockParseHttpResponse(500, json);
        ParseHttpClient client = Mockito.mock(ParseHttpClient.class);
        Mockito.when(client.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(response1, response2, response3, response4, response5);
        ParseRESTCommand command = new ParseRESTCommand.Builder().method(GET).installationId("fake_installation_id").build();
        Task<JSONObject> task = command.executeAsync(client);
        task.waitForCompletion();
        Mockito.verify(client, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Assert.assertEquals(1337, getCode());
        Assert.assertEquals("mock error", task.getError().getMessage());
    }

    /**
     * Test to verify that handle 401 unauthorized
     */
    @Test
    public void test401Unauthorized() throws Exception {
        JSONObject json = new JSONObject();
        json.put("error", "unauthorized");
        ParseHttpResponse response = ParseRESTCommandTest.newMockParseHttpResponse(401, json);
        ParseHttpClient client = Mockito.mock(ParseHttpClient.class);
        Mockito.when(client.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(response);
        ParseRESTCommand command = new ParseRESTCommand.Builder().method(GET).installationId("fake_installation_id").build();
        Task<JSONObject> task = command.executeAsync(client);
        task.waitForCompletion();
        Mockito.verify(client, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Assert.assertEquals(0, getCode());
        Assert.assertEquals("unauthorized", task.getError().getMessage());
    }

    @Test
    public void testToDeterministicString() throws Exception {
        // Make test json
        JSONArray nestedJSONArray = new JSONArray().put(true).put(1).put("test");
        JSONObject nestedJSON = new JSONObject().put("bool", false).put("int", 2).put("string", "test");
        JSONObject json = new JSONObject().put("json", nestedJSON).put("jsonArray", nestedJSONArray).put("bool", true).put("int", 3).put("string", "test");
        String jsonString = ParseRESTCommand.toDeterministicString(json);
        JSONObject jsonAgain = new JSONObject(jsonString);
        Assert.assertEquals(json, jsonAgain, NON_EXTENSIBLE);
    }

    @Test
    public void testToJSONObject() throws Exception {
        // Make test command
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = new JSONObject().put("count", 1).put("limit", 1);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        JSONObject json = command.toJSONObject();
        Assert.assertEquals(httpPath, json.getString("httpPath"));
        Assert.assertEquals("POST", json.getString("httpMethod"));
        Assert.assertEquals(jsonParameters, json.getJSONObject("parameters"), NON_EXTENSIBLE);
        Assert.assertEquals(sessionToken, json.getString("sessionToken"));
        Assert.assertEquals(localId, json.getString("localId"));
    }

    @Test
    public void testGetCacheKey() throws Exception {
        // Make test command
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = new JSONObject().put("count", 1).put("limit", 1);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        String cacheKey = command.getCacheKey();
        Assert.assertTrue(cacheKey.contains("ParseRESTCommand"));
        Assert.assertTrue(cacheKey.contains(POST.toString()));
        Assert.assertTrue(cacheKey.contains(ParseDigestUtils.md5(httpPath)));
        String str = ParseDigestUtils.md5(((ParseRESTCommand.toDeterministicString(jsonParameters)) + sessionToken));
        Assert.assertTrue(cacheKey.contains(str));
    }

    @Test
    public void testGetCacheKeyWithNoJSONParameters() {
        // Make test command
        String httpPath = "www.parse.com";
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).method(POST).sessionToken(sessionToken).localId(localId).build();
        String cacheKey = command.getCacheKey();
        Assert.assertTrue(cacheKey.contains("ParseRESTCommand"));
        Assert.assertTrue(cacheKey.contains(POST.toString()));
        Assert.assertTrue(cacheKey.contains(ParseDigestUtils.md5(httpPath)));
        Assert.assertTrue(cacheKey.contains(ParseDigestUtils.md5(sessionToken)));
    }

    @Test
    public void testReleaseLocalIds() {
        // Register LocalIdManager
        LocalIdManager localIdManager = Mockito.mock(LocalIdManager.class);
        Mockito.when(localIdManager.createLocalId()).thenReturn("localIdAgain");
        ParseCorePlugins.getInstance().registerLocalIdManager(localIdManager);
        // Make test command
        ParseObject object = new ParseObject("Test");
        object.put("key", "value");
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = PointerOrLocalIdEncoder.get().encodeRelatedObject(object);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        command.releaseLocalIds();
        Mockito.verify(localIdManager, Mockito.times(1)).releaseLocalIdOnDisk(localId);
        Mockito.verify(localIdManager, Mockito.times(1)).releaseLocalIdOnDisk("localIdAgain");
    }

    @Test
    public void testResolveLocalIdsWithNoObjectId() {
        // Register LocalIdManager
        LocalIdManager localIdManager = Mockito.mock(LocalIdManager.class);
        Mockito.when(localIdManager.createLocalId()).thenReturn("localIdAgain");
        ParseCorePlugins.getInstance().registerLocalIdManager(localIdManager);
        // Make test command
        ParseObject object = new ParseObject("Test");
        object.put("key", "value");
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = PointerOrLocalIdEncoder.get().encodeRelatedObject(object);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Tried to serialize a command referencing a new, unsaved object.");
        command.resolveLocalIds();
        // Make sure we try to get the objectId
        Mockito.verify(localIdManager, Mockito.times(1)).getObjectId("localIdAgain");
    }

    @Test
    public void testResolveLocalIds() throws Exception {
        // Register LocalIdManager
        LocalIdManager localIdManager = Mockito.mock(LocalIdManager.class);
        Mockito.when(localIdManager.createLocalId()).thenReturn("localIdAgain");
        Mockito.when(localIdManager.getObjectId("localIdAgain")).thenReturn("objectIdAgain");
        Mockito.when(localIdManager.getObjectId("localId")).thenReturn("objectId");
        ParseCorePlugins.getInstance().registerLocalIdManager(localIdManager);
        // Make test command
        ParseObject object = new ParseObject("Test");
        object.put("key", "value");
        String httpPath = "classes";
        JSONObject jsonParameters = PointerOrLocalIdEncoder.get().encodeRelatedObject(object);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        command.resolveLocalIds();
        Mockito.verify(localIdManager, Mockito.times(1)).getObjectId("localIdAgain");
        Mockito.verify(localIdManager, Mockito.times(1)).getObjectId("localId");
        // Make sure localId in jsonParameters has been replaced with objectId
        Assert.assertFalse(jsonParameters.has("localId"));
        Assert.assertEquals("objectIdAgain", jsonParameters.getString("objectId"));
        // Make sure localId in command has been replaced with objectId
        Assert.assertNull(command.getLocalId());
        // Make sure httpMethod has been changed
        Assert.assertEquals(PUT, command.method);
        // Make sure objectId has been added to httpPath
        Assert.assertTrue(command.httpPath.contains("objectId"));
    }

    @Test
    public void testRetainLocalIds() {
        // Register LocalIdManager
        LocalIdManager localIdManager = Mockito.mock(LocalIdManager.class);
        Mockito.when(localIdManager.createLocalId()).thenReturn("localIdAgain");
        ParseCorePlugins.getInstance().registerLocalIdManager(localIdManager);
        // Make test command
        ParseObject object = new ParseObject("Test");
        object.put("key", "value");
        String httpPath = "classes";
        JSONObject jsonParameters = PointerOrLocalIdEncoder.get().encodeRelatedObject(object);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(POST).sessionToken(sessionToken).localId(localId).build();
        command.retainLocalIds();
        Mockito.verify(localIdManager, Mockito.times(1)).retainLocalIdOnDisk("localIdAgain");
        Mockito.verify(localIdManager, Mockito.times(1)).retainLocalIdOnDisk(localId);
    }

    @Test
    public void testNewBodyWithNoJSONParameters() {
        // Make test command
        String httpPath = "www.parse.com";
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).method(GET).sessionToken(sessionToken).localId(localId).build();
        thrown.expect(IllegalArgumentException.class);
        String message = String.format("Trying to execute a %s command without body parameters.", GET.toString());
        thrown.expectMessage(message);
        command.newBody(null);
    }

    @Test
    public void testNewBody() throws Exception {
        // Make test command
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = new JSONObject().put("count", 1).put("limit", 1);
        String sessionToken = "sessionToken";
        String localId = "localId";
        ParseRESTCommand command = new ParseRESTCommand.Builder().httpPath(httpPath).jsonParameters(jsonParameters).method(GET).sessionToken(sessionToken).localId(localId).build();
        ParseByteArrayHttpBody body = ((ParseByteArrayHttpBody) (command.newBody(null)));
        // Verify body content is correct
        JSONObject json = new JSONObject(new String(ParseIOUtils.toByteArray(body.getContent())));
        Assert.assertEquals(1, json.getInt("count"));
        Assert.assertEquals(1, json.getInt("limit"));
        Assert.assertEquals(GET.toString(), json.getString("_method"));
        // Verify body content-type is correct
        Assert.assertEquals("application/json", body.getContentType());
    }

    @Test
    public void testFromJSONObject() throws Exception {
        // Make test command
        String httpPath = "www.parse.com";
        JSONObject jsonParameters = new JSONObject().put("count", 1).put("limit", 1);
        String sessionToken = "sessionToken";
        String localId = "localId";
        String httpMethod = "POST";
        JSONObject commandJSON = new JSONObject().put("httpPath", httpPath).put("parameters", jsonParameters).put("httpMethod", httpMethod).put("sessionToken", sessionToken).put("localId", localId);
        ParseRESTCommand command = ParseRESTCommand.fromJSONObject(commandJSON);
        Assert.assertEquals(httpPath, command.httpPath);
        Assert.assertEquals(httpMethod, command.method.toString());
        Assert.assertEquals(sessionToken, command.getSessionToken());
        Assert.assertEquals(localId, command.getLocalId());
        Assert.assertEquals(jsonParameters, command.jsonParameters, NON_EXTENSIBLE);
    }

    @Test
    public void testOnResponseCloseNetworkStreamWithNormalResponse() throws Exception {
        // Mock response stream
        int statusCode = 200;
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("key", "value");
        String bodyStr = bodyJson.toString();
        ByteArrayInputStream bodyStream = new ByteArrayInputStream(bodyStr.getBytes());
        InputStream mockResponseStream = Mockito.spy(bodyStream);
        Mockito.doNothing().when(mockResponseStream).close();
        // Mock response
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(statusCode).setTotalSize(((long) (bodyStr.length()))).setContent(mockResponseStream).build();
        ParseRESTCommand command = new ParseRESTCommand.Builder().build();
        JSONObject json = ParseTaskUtils.wait(command.onResponseAsync(mockResponse, null));
        Mockito.verify(mockResponseStream, Mockito.times(1)).close();
        Assert.assertEquals(bodyJson, json, NON_EXTENSIBLE);
    }

    @Test
    public void testOnResposneCloseNetworkStreamWithIOException() throws Exception {
        // Mock response stream
        int statusCode = 200;
        InputStream mockResponseStream = Mockito.mock(InputStream.class);
        Mockito.doNothing().when(mockResponseStream).close();
        IOException readException = new IOException("Error");
        Mockito.doThrow(readException).when(mockResponseStream).read();
        Mockito.doThrow(readException).when(mockResponseStream).read(ArgumentMatchers.any(byte[].class));
        // Mock response
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(statusCode).setContent(mockResponseStream).build();
        ParseRESTCommand command = new ParseRESTCommand.Builder().build();
        // We can not use ParseTaskUtils here since it will replace the original exception with runtime
        // exception
        Task<JSONObject> responseTask = command.onResponseAsync(mockResponse, null);
        responseTask.waitForCompletion();
        Assert.assertTrue(responseTask.isFaulted());
        Assert.assertTrue(((responseTask.getError()) instanceof IOException));
        Assert.assertEquals("Error", responseTask.getError().getMessage());
        Mockito.verify(mockResponseStream, Mockito.times(1)).close();
    }
}

