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


import FacebookRequestErrorClassification.EC_INVALID_TOKEN;
import FacebookRequestErrorClassification.ESC_APP_INACTIVE;
import com.facebook.internal.Utility;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ AccessToken.class, AccessTokenCache.class, FacebookSdk.class, GraphRequest.class, Utility.class })
public final class GraphErrorTest extends FacebookPowerMockTestCase {
    @Test
    public void testAccessTokenNotResetOnTokenExpirationError() throws IOException, JSONException {
        AccessToken accessToken = mock(AccessToken.class);
        suppress(method(Utility.class, "isNullOrEmpty", String.class));
        AccessToken.setCurrentAccessToken(accessToken);
        JSONObject errorBody = new JSONObject();
        errorBody.put("message", "Invalid OAuth access token.");
        errorBody.put("type", "OAuthException");
        errorBody.put("code", EC_INVALID_TOKEN);
        errorBody.put("error_subcode", ESC_APP_INACTIVE);
        JSONObject error = new JSONObject();
        error.put("error", errorBody);
        String errorString = error.toString();
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(400);
        GraphRequest request = mock(GraphRequest.class);
        when(request.getAccessToken()).thenReturn(accessToken);
        GraphRequestBatch batch = new GraphRequestBatch(request);
        Assert.assertNotNull(AccessToken.getCurrentAccessToken());
        GraphResponse.createResponsesFromString(errorString, connection, batch);
        Assert.assertNotNull(AccessToken.getCurrentAccessToken());
    }

    @Test
    public void testAccessTokenResetOnTokenInstallError() throws IOException, JSONException {
        AccessToken accessToken = mock(AccessToken.class);
        AccessToken.setCurrentAccessToken(accessToken);
        JSONObject errorBody = new JSONObject();
        errorBody.put("message", "User has not installed the application.");
        errorBody.put("type", "OAuthException");
        errorBody.put("code", EC_INVALID_TOKEN);
        JSONObject error = new JSONObject();
        error.put("error", errorBody);
        String errorString = error.toString();
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(400);
        GraphRequest request = mock(GraphRequest.class);
        when(request.getAccessToken()).thenReturn(accessToken);
        GraphRequestBatch batch = new GraphRequestBatch(request);
        Assert.assertNotNull(AccessToken.getCurrentAccessToken());
        GraphResponse.createResponsesFromString(errorString, connection, batch);
        Assert.assertNull(AccessToken.getCurrentAccessToken());
    }
}

