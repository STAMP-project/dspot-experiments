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
package com.facebook.login;


import DefaultAudience.EVERYONE;
import DefaultAudience.FRIENDS;
import LoginBehavior.NATIVE_WITH_FALLBACK;
import LoginBehavior.WEB_ONLY;
import LoginClient.OnCompletedListener;
import LoginClient.Request;
import LoginClient.Result;
import LoginClient.Result.Code;
import LoginClient.Result.Code.SUCCESS;
import RuntimeEnvironment.application;
import android.support.v4.app.Fragment;
import com.facebook.AccessToken;
import com.facebook.FacebookPowerMockTestCase;
import com.facebook.FacebookSdk;
import com.facebook.TestUtils;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static DefaultAudience.EVERYONE;
import static LoginBehavior.WEB_ONLY;


@PrepareForTest({ LoginClient.class })
public class LoginClientTest extends FacebookPowerMockTestCase {
    private static final String ACCESS_TOKEN = "An access token for user 1";

    private static final String USER_ID = "1001";

    private static final String APP_ID = "2002";

    private static final HashSet<String> PERMISSIONS = new HashSet<String>(Arrays.asList("go outside", "come back in"));

    private static final String ERROR_MESSAGE = "This is bad!";

    @Mock
    private Fragment mockFragment;

    @Test
    public void testReauthorizationWithSameFbidSucceeds() throws Exception {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.setAutoLogAppEventsEnabled(false);
        FacebookSdk.sdkInitialize(application);
        LoginClient.Request request = createRequest(LoginClientTest.ACCESS_TOKEN);
        AccessToken token = new AccessToken(LoginClientTest.ACCESS_TOKEN, LoginClientTest.APP_ID, LoginClientTest.USER_ID, LoginClientTest.PERMISSIONS, null, null, null, null, null);
        LoginClient.Result result = Result.createTokenResult(request, token);
        LoginClient.OnCompletedListener listener = mock(OnCompletedListener.class);
        LoginClient client = new LoginClient(mockFragment);
        client.setOnCompletedListener(listener);
        client.completeAndValidate(result);
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(listener).onCompleted(resultArgumentCaptor.capture());
        result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(SUCCESS, result.code);
        AccessToken resultToken = result.token;
        Assert.assertNotNull(resultToken);
        Assert.assertEquals(LoginClientTest.ACCESS_TOKEN, resultToken.getToken());
        // We don't care about ordering.
        Assert.assertEquals(LoginClientTest.PERMISSIONS, resultToken.getPermissions());
    }

    @Test
    public void testRequestParceling() {
        LoginClient.Request request = createRequest(LoginClientTest.ACCESS_TOKEN);
        LoginClient.Request unparceledRequest = TestUtils.parcelAndUnparcel(request);
        Assert.assertEquals(NATIVE_WITH_FALLBACK, unparceledRequest.getLoginBehavior());
        Assert.assertEquals(new HashSet<String>(LoginClientTest.PERMISSIONS), unparceledRequest.getPermissions());
        Assert.assertEquals(FRIENDS, unparceledRequest.getDefaultAudience());
        Assert.assertEquals("1234", unparceledRequest.getApplicationId());
        Assert.assertEquals("5678", unparceledRequest.getAuthId());
        Assert.assertFalse(unparceledRequest.isRerequest());
    }

    @Test
    public void testResultParceling() {
        LoginClient.Request request = new LoginClient.Request(WEB_ONLY, null, EVERYONE, null, null, null);
        request.setRerequest(true);
        AccessToken token1 = new AccessToken("Token2", "12345", "1000", null, null, null, null, null, null);
        LoginClient.Result result = new LoginClient.Result(request, Code.SUCCESS, token1, "error 1", "123");
        LoginClient.Result unparceledResult = TestUtils.parcelAndUnparcel(result);
        LoginClient.Request unparceledRequest = unparceledResult.request;
        Assert.assertEquals(WEB_ONLY, unparceledRequest.getLoginBehavior());
        Assert.assertEquals(new HashSet<String>(), unparceledRequest.getPermissions());
        Assert.assertEquals(EVERYONE, unparceledRequest.getDefaultAudience());
        Assert.assertEquals(null, unparceledRequest.getApplicationId());
        Assert.assertEquals(null, unparceledRequest.getAuthId());
        Assert.assertTrue(unparceledRequest.isRerequest());
        Assert.assertEquals(SUCCESS, unparceledResult.code);
        Assert.assertEquals(token1, unparceledResult.token);
        Assert.assertEquals("error 1", unparceledResult.errorMessage);
        Assert.assertEquals("123", unparceledResult.errorCode);
    }
}

