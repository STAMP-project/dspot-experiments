/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For Uri.encode
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class NetworkUserControllerTest {
    // region testSignUpAsync
    @Test
    public void testSignUpAsync() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(generateBasicMockResponse(), 200, "OK");
        // Make test user state and operationSet
        ParseUser.State state = new ParseUser.State.Builder().put("username", "testUserName").put("password", "testPassword").build();
        ParseOperationSet operationSet = new ParseOperationSet();
        operationSet.put("username", new ParseSetOperation("testUserName"));
        operationSet.put("password", new ParseSetOperation("testPassword"));
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.signUpAsync(state, operationSet, "sessionToken"));
        verifyBasicUserState(mockResponse, newState);
        Assert.assertFalse(newState.isComplete());
        Assert.assertTrue(newState.isNew());
    }

    // endregion
    // region testLoginAsync
    @Test
    public void testLoginAsyncWithUserNameAndPassword() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        NetworkUserController controller = new NetworkUserController(restClient);
        ParseUser.State newState = ParseTaskUtils.wait(controller.logInAsync("userName", "password"));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertTrue(newState.isComplete());
        Assert.assertFalse(newState.isNew());
    }

    @Test
    public void testLoginAsyncWithUserStateCreated() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 201, "OK");
        // Make test user state and operationSet
        ParseUser.State state = new ParseUser.State.Builder().put("username", "testUserName").put("password", "testPassword").build();
        ParseOperationSet operationSet = new ParseOperationSet();
        operationSet.put("username", new ParseSetOperation("testUserName"));
        operationSet.put("password", new ParseSetOperation("testPassword"));
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.logInAsync(state, operationSet));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertTrue(newState.isNew());
        Assert.assertFalse(newState.isComplete());
    }

    @Test
    public void testLoginAsyncWithUserStateNotCreated() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        // Make test user state and operationSet
        ParseUser.State state = new ParseUser.State.Builder().put("username", "testUserName").put("password", "testPassword").build();
        ParseOperationSet operationSet = new ParseOperationSet();
        operationSet.put("username", new ParseSetOperation("testUserName"));
        operationSet.put("password", new ParseSetOperation("testPassword"));
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.logInAsync(state, operationSet));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertFalse(newState.isNew());
        Assert.assertTrue(newState.isComplete());
    }

    @Test
    public void testLoginAsyncWithAuthTypeCreated() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        mockResponse.put("authData", generateMockAuthData());
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 201, "OK");
        // Make test user auth data
        Map<String, String> facebookAuthInfoMap = new HashMap<>();
        facebookAuthInfoMap.put("token", "test");
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.logInAsync("facebook", facebookAuthInfoMap));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertTrue(newState.isNew());
        Assert.assertTrue(newState.isComplete());
    }

    @Test
    public void testLoginAsyncWithAuthTypeNotCreated() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        mockResponse.put("authData", generateMockAuthData());
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        // Make test user auth data
        Map<String, String> facebookAuthInfoMap = new HashMap<>();
        facebookAuthInfoMap.put("token", "test");
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.logInAsync("facebook", facebookAuthInfoMap));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertFalse(newState.isNew());
        Assert.assertTrue(newState.isComplete());
    }

    // endregion
    // region testGetUserAsync
    @Test
    public void testGetUserAsync() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 201, "OK");
        NetworkUserController controller = new NetworkUserController(restClient, true);
        ParseUser.State newState = ParseTaskUtils.wait(controller.getUserAsync("sessionToken"));
        // Verify user state
        verifyBasicUserState(mockResponse, newState);
        Assert.assertTrue(newState.isComplete());
        Assert.assertFalse(newState.isNew());
    }

    // endregion
    // region testRequestPasswordResetAsync
    @Test
    public void testRequestPasswordResetAsync() throws Exception {
        // Make mock response and client
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(new JSONObject(), 200, "OK");
        NetworkUserController controller = new NetworkUserController(restClient, true);
        // We just need to verify task is finished since sever returns an empty json here
        ParseTaskUtils.wait(controller.requestPasswordResetAsync("sessionToken"));
    }
}

