/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseQuery.State;
import bolts.Task;
import java.util.List;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NetworkQueryControllerTest {
    // endregion
    // region testFindAsync
    @Test
    public void testConvertFindResponse() throws Exception {
        // Make mock response
        JSONObject mockResponse = NetworkQueryControllerTest.generateBasicMockResponse();
        // Make mock state
        ParseQuery.State mockState = Mockito.mock(State.class);
        Mockito.when(mockState.className()).thenReturn("Test");
        Mockito.when(mockState.selectedKeys()).thenReturn(null);
        Mockito.when(mockState.constraints()).thenReturn(new ParseQuery.QueryConstraints());
        NetworkQueryController controller = new NetworkQueryController(Mockito.mock(ParseHttpClient.class));
        List<ParseObject> objects = controller.convertFindResponse(mockState, mockResponse);
        verifyBasicParseObjects(mockResponse, objects, "Test");
    }

    // TODO(mengyan): Add testFindAsyncWithCachePolicy to verify command is added to
    // ParseKeyValueCache
    // endregion
    // region testCountAsync
    @Test
    public void testFindAsyncWithSessionToken() throws Exception {
        // Make mock response
        JSONObject mockResponse = NetworkQueryControllerTest.generateBasicMockResponse();
        mockResponse.put("trace", "serverTrace");
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        // Make mock state
        ParseQuery.State mockState = Mockito.mock(State.class);
        Mockito.when(mockState.className()).thenReturn("Test");
        Mockito.when(mockState.selectedKeys()).thenReturn(null);
        Mockito.when(mockState.constraints()).thenReturn(new ParseQuery.QueryConstraints());
        NetworkQueryController controller = new NetworkQueryController(restClient);
        Task<List<ParseObject>> findTask = controller.findAsync(mockState, "sessionToken", null);
        ParseTaskUtils.wait(findTask);
        List<ParseObject> objects = findTask.getResult();
        verifyBasicParseObjects(mockResponse, objects, "Test");
        // TODO(mengyan): Verify PLog is called
    }

    // TODO(mengyan): Add testFindAsyncWithCachePolicy to verify command is added to
    // ParseKeyValueCache
    // endregion
    @Test
    public void testCountAsyncWithSessionToken() throws Exception {
        // Make mock response and client
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("count", 2);
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        // Make mock state
        ParseQuery.State mockState = Mockito.mock(State.class);
        Mockito.when(mockState.className()).thenReturn("Test");
        Mockito.when(mockState.selectedKeys()).thenReturn(null);
        Mockito.when(mockState.constraints()).thenReturn(new ParseQuery.QueryConstraints());
        NetworkQueryController controller = new NetworkQueryController(restClient);
        Task<Integer> countTask = controller.countAsync(mockState, "sessionToken", null);
        ParseTaskUtils.wait(countTask);
        int count = countTask.getResult();
        Assert.assertEquals(2, count);
    }
}

