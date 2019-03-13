/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook;


import FacebookRequestError.Category.LOGIN_RECOVERABLE;
import FacebookRequestError.Category.OTHER;
import FacebookRequestError.Category.TRANSIENT;
import FacebookRequestError.INVALID_ERROR_CODE;
import FacebookRequestError.INVALID_HTTP_STATUS_CODE;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class FacebookGraphRequestErrorTest extends FacebookTestCase {
    public static final String ERROR_SINGLE_RESPONSE = "{\n" + ((((("  \"error\": {\n" + "    \"message\": \"Unknown path components: /unknown\",\n") + "    \"type\": \"OAuthException\",\n") + "    \"code\": 2500\n") + "  }\n") + "}");

    public static final String ERROR_BATCH_RESPONSE = "[\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("  {\n" + "    \"headers\": [\n") + "      {\n") + "        \"value\": \"*\",\n") + "        \"name\": \"Access-Control-Allow-Origin\"\n") + "      },\n") + "      {\n") + "        \"value\": \"no-store\",\n") + "        \"name\": \"Cache-Control\"\n") + "      },\n") + "      {\n") + "        \"value\": \"close\",\n") + "        \"name\": \"Connection\"\n") + "      },\n") + "      {\n") + "        \"value\": \"text\\/javascript; charset=UTF-8\",\n") + "        \"name\": \"Content-Type\"\n") + "      },\n") + "      {\n") + "        \"value\": \"Sat, 01 Jan 2000 00:00:00 GMT\",\n") + "        \"name\": \"Expires\"\n") + "      },\n") + "      {\n") + "        \"value\": \"no-cache\",\n") + "        \"name\": \"Pragma\"\n") + "      },\n") + "      {\n") + "        \"value\": \"OAuth \\\"Facebook Platform\\\" \\\"invalid_request\\\" \\\"An active access token must be used to query information about the current user.\\\"\",\n") + "        \"name\": \"WWW-Authenticate\"\n") + "      }\n") + "    ],\n") + "    \"body\": \"{\\\"error\\\":{\\\"message\\\":\\\"An active access token must be used to query information about the current user.\\\",\\\"type\\\":\\\"OAuthException\\\",\\\"code\\\":2500}}\",\n") + "    \"code\": 400\n") + "  },\n") + "  {\n") + "    \"headers\": [\n") + "      {\n") + "        \"value\": \"*\",\n") + "        \"name\": \"Access-Control-Allow-Origin\"\n") + "      },\n") + "      {\n") + "        \"value\": \"no-store\",\n") + "        \"name\": \"Cache-Control\"\n") + "      },\n") + "      {\n") + "        \"value\": \"close\",\n") + "        \"name\": \"Connection\"\n") + "      },\n") + "      {\n") + "        \"value\": \"text\\/javascript; charset=UTF-8\",\n") + "        \"name\": \"Content-Type\"\n") + "      },\n") + "      {\n") + "        \"value\": \"Sat, 01 Jan 2000 00:00:00 GMT\",\n") + "        \"name\": \"Expires\"\n") + "      },\n") + "      {\n") + "        \"value\": \"no-cache\",\n") + "        \"name\": \"Pragma\"\n") + "      },\n") + "      {\n") + "        \"value\": \"OAuth \\\"Facebook Platform\\\" \\\"invalid_request\\\" \\\"An active access token must be used to query information about the current user.\\\"\",\n") + "        \"name\": \"WWW-Authenticate\"\n") + "      }\n") + "    ],\n") + "    \"body\": \"{\\\"error\\\":{\\\"message\\\":\\\"An active access token must be used to query information about the current user.\\\",\\\"type\\\":\\\"OAuthException\\\",\\\"code\\\":2500}}\",\n") + "    \"code\": 400\n") + "  }\n") + "]");

    public static final String ERROR_SINGLE_RESPONSE_THROTTLE = "{\n" + (((("  \"error\": {\n" + "    \"message\": \"Application request limit reached\",\n") + "    \"code\": 4\n") + "  }\n") + "}");

    public static final String ERROR_SINGLE_RESPONSE_SERVER = "{\n" + (((("  \"error\": {\n" + "    \"message\": \"Some Server Error\",\n") + "    \"code\": 2\n") + "  }\n") + "}");

    public static final String ERROR_SINGLE_RESPONSE_PERMISSION = "{\n" + ((((("  \"error\": {\n" + "    \"type\": \"OAuthException\",\n") + "    \"message\": \"(#200) Requires extended permission: publish_actions\",\n") + "    \"code\": 200\n") + "  }\n") + "}");

    public static final String ERROR_SINGLE_RESPONSE_WEB_LOGIN = "{\n" + (((((("  \"error\": {\n" + "    \"message\": \"User need to login\",\n") + "    \"type\": \"OAuthException\",\n") + "    \"code\": 102,\n") + "    \"error_subcode\": 459\n") + "  }\n") + "}");

    public static final String ERROR_SINGLE_RESPONSE_RELOGIN = "{\n" + ((((("  \"error\": {\n" + "    \"message\": \"User need to relogin\",\n") + "    \"type\": \"OAuthException\",\n") + "    \"code\": 102\n") + "  }\n") + "}");

    public static final String ERROR_SINGLE_RESPONSE_RELOGIN_DELETED_APP = "{\n" + (((((("  \"error\": {\n" + "    \"message\": \"User need to relogin\",\n") + "    \"type\": \"OAuthException\",\n") + "    \"code\": 190,\n") + "    \"error_subcode\": 458\n") + "  }\n") + "}");

    @Test
    public void testClientException() {
        final String errorMsg = "some error happened";
        FacebookRequestError error = new FacebookRequestError(null, new FacebookException(errorMsg));
        Assert.assertEquals(errorMsg, error.getErrorMessage());
        Assert.assertEquals(OTHER, error.getCategory());
        Assert.assertEquals(INVALID_ERROR_CODE, error.getErrorCode());
        Assert.assertEquals(INVALID_HTTP_STATUS_CODE, error.getRequestStatusCode());
    }

    @Test
    public void testSingleRequestWithoutBody() throws JSONException {
        JSONObject withStatusCode = new JSONObject();
        withStatusCode.put("code", 400);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCode, withStatusCode, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals(OTHER, error.getCategory());
    }

    @Test
    public void testSingleErrorWithBody() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 400);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("Unknown path components: /unknown", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(2500, error.getErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(OTHER, error.getCategory());
    }

    @Test
    public void testBatchRequest() throws JSONException {
        JSONArray batchResponse = new JSONArray(FacebookGraphRequestErrorTest.ERROR_BATCH_RESPONSE);
        Assert.assertEquals(2, batchResponse.length());
        JSONObject firstResponse = ((JSONObject) (batchResponse.get(0)));
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(firstResponse, batchResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("An active access token must be used to query information about the current user.", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(2500, error.getErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONArray));
        Assert.assertEquals(OTHER, error.getCategory());
    }

    @Test
    public void testSingleThrottledError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_THROTTLE);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 403);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(403, error.getRequestStatusCode());
        Assert.assertEquals("Application request limit reached", error.getErrorMessage());
        Assert.assertNull(error.getErrorType());
        Assert.assertEquals(4, error.getErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(TRANSIENT, error.getCategory());
    }

    @Test
    public void testSingleServerError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_SERVER);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 500);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(500, error.getRequestStatusCode());
        Assert.assertEquals("Some Server Error", error.getErrorMessage());
        Assert.assertNull(error.getErrorType());
        Assert.assertEquals(2, error.getErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(TRANSIENT, error.getCategory());
    }

    @Test
    public void testSinglePermissionError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_PERMISSION);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 400);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("(#200) Requires extended permission: publish_actions", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(200, error.getErrorCode());
        Assert.assertEquals(INVALID_ERROR_CODE, error.getSubErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(OTHER, error.getCategory());
    }

    @Test
    public void testSingleWebLoginError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_WEB_LOGIN);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 400);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("User need to login", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(102, error.getErrorCode());
        Assert.assertEquals(459, error.getSubErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(LOGIN_RECOVERABLE, error.getCategory());
    }

    @Test
    public void testSingleReloginError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_RELOGIN);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 400);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("User need to relogin", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(102, error.getErrorCode());
        Assert.assertEquals(INVALID_ERROR_CODE, error.getSubErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(LOGIN_RECOVERABLE, error.getCategory());
    }

    @Test
    public void testSingleReloginDeletedAppError() throws JSONException {
        JSONObject originalResponse = new JSONObject(FacebookGraphRequestErrorTest.ERROR_SINGLE_RESPONSE_RELOGIN_DELETED_APP);
        JSONObject withStatusCodeAndBody = new JSONObject();
        withStatusCodeAndBody.put("code", 400);
        withStatusCodeAndBody.put("body", originalResponse);
        FacebookRequestError error = FacebookRequestError.checkResponseAndCreateError(withStatusCodeAndBody, originalResponse, null);
        Assert.assertNotNull(error);
        Assert.assertEquals(400, error.getRequestStatusCode());
        Assert.assertEquals("User need to relogin", error.getErrorMessage());
        Assert.assertEquals("OAuthException", error.getErrorType());
        Assert.assertEquals(190, error.getErrorCode());
        Assert.assertEquals(458, error.getSubErrorCode());
        Assert.assertTrue(((error.getBatchRequestResult()) instanceof JSONObject));
        Assert.assertEquals(LOGIN_RECOVERABLE, error.getCategory());
    }
}

