/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseObject.State;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For Uri.encode
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class NetworkSessionControllerTest {
    // endregion
    // region testRevokeAsync
    @Test
    public void testGetSessionAsync() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = NetworkSessionControllerTest.generateBasicMockResponse();
        mockResponse.put("installationId", "39c8e8a4-6dd0-4c39-ac85-7fd61425083b");
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        NetworkSessionController controller = new NetworkSessionController(restClient);
        ParseObject.State newState = ParseTaskUtils.wait(controller.getSessionAsync("sessionToken"));
        // Verify session state
        NetworkSessionControllerTest.verifyBasicSessionState(mockResponse, newState);
        Assert.assertEquals("39c8e8a4-6dd0-4c39-ac85-7fd61425083b", newState.get("installationId"));
        Assert.assertTrue(newState.isComplete());
    }

    // endregion
    @Test
    public void testUpgradeToRevocable() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = NetworkSessionControllerTest.generateBasicMockResponse();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        NetworkSessionController controller = new NetworkSessionController(restClient);
        ParseObject.State newState = ParseTaskUtils.wait(controller.upgradeToRevocable("sessionToken"));
        // Verify session state
        NetworkSessionControllerTest.verifyBasicSessionState(mockResponse, newState);
        Assert.assertTrue(newState.isComplete());
    }

    @Test
    public void testRevokeAsync() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = new JSONObject();
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        NetworkSessionController controller = new NetworkSessionController(restClient);
        // We just need to verify task is finished since sever returns an empty json here
        ParseTaskUtils.wait(controller.revokeAsync("sessionToken"));
    }
}

