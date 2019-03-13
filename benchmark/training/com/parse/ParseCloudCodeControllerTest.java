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
import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ParseCloudCodeControllerTest {
    // region testConstructor
    @Test
    public void testConstructor() {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Assert.assertSame(restClient, controller.restClient);
    }

    // endregion
    // region testConvertCloudResponse
    @Test
    public void testConvertCloudResponseNullResponse() {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Object result = controller.convertCloudResponse(null);
        Assert.assertNull(result);
    }

    @Test
    public void testConvertCloudResponseJsonResponseWithResultField() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        JSONObject response = new JSONObject();
        response.put("result", "test");
        Object result = controller.convertCloudResponse(response);
        Assert.assertThat(result, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("test", result);
    }

    @Test
    public void testConvertCloudResponseJsonArrayResponse() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        JSONArray response = new JSONArray();
        response.put(0, "test");
        response.put(1, true);
        response.put(2, 2);
        Object result = controller.convertCloudResponse(response);
        Assert.assertThat(result, CoreMatchers.instanceOf(List.class));
        List listResult = ((List) (result));
        Assert.assertEquals(3, listResult.size());
        Assert.assertEquals("test", listResult.get(0));
        Assert.assertEquals(true, listResult.get(1));
        Assert.assertEquals(2, listResult.get(2));
    }

    // endregion
    // region testCallFunctionInBackground
    @Test
    public void testCallFunctionInBackgroundCommand() {
        // TODO(mengyan): Verify proper command is constructed
    }

    @Test
    public void testCallFunctionInBackgroundSuccessWithResult() throws Exception {
        JSONObject json = new JSONObject();
        json.put("result", "test");
        String content = json.toString();
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (content.length()))).setContent(new ByteArrayInputStream(content.getBytes())).build();
        ParseHttpClient restClient = mockParseHttpClientWithReponse(mockResponse);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Task<String> cloudCodeTask = controller.callFunctionInBackground("test", new HashMap<String, Object>(), "sessionToken");
        ParseTaskUtils.wait(cloudCodeTask);
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertEquals("test", cloudCodeTask.getResult());
    }

    @Test
    public void testCallFunctionInBackgroundSuccessWithoutResult() throws Exception {
        JSONObject json = new JSONObject();
        String content = json.toString();
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (content.length()))).setContent(new ByteArrayInputStream(content.getBytes())).build();
        ParseHttpClient restClient = mockParseHttpClientWithReponse(mockResponse);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Task<String> cloudCodeTask = controller.callFunctionInBackground("test", new HashMap<String, Object>(), "sessionToken");
        ParseTaskUtils.wait(cloudCodeTask);
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertNull(cloudCodeTask.getResult());
    }

    @Test
    public void testCallFunctionInBackgroundFailure() throws Exception {
        // TODO(mengyan): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Task<String> cloudCodeTask = controller.callFunctionInBackground("test", new HashMap<String, Object>(), "sessionToken");
        // Do not use ParseTaskUtils.wait() since we do not want to throw the exception
        cloudCodeTask.waitForCompletion();
        // TODO(mengyan): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(restClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(cloudCodeTask.isFaulted());
        Exception error = cloudCodeTask.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
    }

    // endregion
    @Test
    public void testCallFunctionWithNullResult() throws Exception {
        String content = "{ result: null }";
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (content.length()))).setContent(new ByteArrayInputStream(content.getBytes())).build();
        ParseHttpClient restClient = mockParseHttpClientWithReponse(mockResponse);
        ParseCloudCodeController controller = new ParseCloudCodeController(restClient);
        Task<String> cloudCodeTask = controller.callFunctionInBackground("test", new HashMap<String, Object>(), "sessionToken");
        ParseTaskUtils.wait(cloudCodeTask);
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        String result = cloudCodeTask.getResult();
        Assert.assertEquals(null, result);
    }
}

