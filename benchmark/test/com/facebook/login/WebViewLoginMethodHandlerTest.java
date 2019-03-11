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


import AccessTokenSource.FACEBOOK_APPLICATION_WEB;
import AccessTokenSource.WEB_VIEW;
import LoginClient.Request;
import LoginClient.Result;
import LoginClient.Result.Code.CANCEL;
import LoginClient.Result.Code.ERROR;
import LoginClient.Result.Code.SUCCESS;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.TestUtils;
import com.facebook.internal.Utility;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ LoginClient.class })
public class WebViewLoginMethodHandlerTest extends LoginHandlerTestCase {
    private static final String SIGNED_REQUEST_STR = "ggarbage.eyJhbGdvcml0aG0iOiJITUFDSEEyNTYiLCJ" + "jb2RlIjoid2h5bm90IiwiaXNzdWVkX2F0IjoxNDIyNTAyMDkyLCJ1c2VyX2lkIjoiMTIzIn0";

    @Test
    public void testWebViewHandlesSuccess() {
        Bundle bundle = new Bundle();
        bundle.putString("access_token", LoginHandlerTestCase.ACCESS_TOKEN);
        bundle.putString("expires_in", String.format("%d", LoginHandlerTestCase.EXPIRES_IN_DELTA));
        bundle.putString("code", "Something else");
        bundle.putString("signed_request", WebViewLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        WebViewLoginMethodHandler handler = new WebViewLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.onWebDialogComplete(request, bundle, null);
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(SUCCESS, result.code);
        AccessToken token = result.token;
        Assert.assertNotNull(token);
        Assert.assertEquals(LoginHandlerTestCase.ACCESS_TOKEN, token.getToken());
        assertDateDiffersWithinDelta(new Date(), token.getExpires(), ((LoginHandlerTestCase.EXPIRES_IN_DELTA) * 1000), 1000);
        TestUtils.assertSamePermissions(LoginHandlerTestCase.PERMISSIONS, token.getPermissions());
    }

    @Test
    public void testWebViewHandlesCancel() {
        WebViewLoginMethodHandler handler = new WebViewLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.onWebDialogComplete(request, null, new FacebookOperationCanceledException());
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(CANCEL, result.code);
        Assert.assertNull(result.token);
        Assert.assertNotNull(result.errorMessage);
    }

    @Test
    public void testWebViewHandlesError() {
        WebViewLoginMethodHandler handler = new WebViewLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.onWebDialogComplete(request, null, new FacebookException(LoginHandlerTestCase.ERROR_MESSAGE));
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(ERROR, result.code);
        Assert.assertNull(result.token);
        Assert.assertNotNull(result.errorMessage);
        Assert.assertEquals(LoginHandlerTestCase.ERROR_MESSAGE, result.errorMessage);
    }

    @Test
    public void testFromDialog() {
        List<String> permissions = Utility.arrayList("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";
        String userId = "1000";
        Bundle bundle = new Bundle();
        bundle.putString("access_token", token);
        bundle.putString("expires_in", "60");
        bundle.putString("signed_request", WebViewLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        AccessToken accessToken = LoginMethodHandler.createAccessTokenFromWebBundle(permissions, bundle, WEB_VIEW, "1234");
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(WEB_VIEW, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
    }

    @Test
    public void testFromSSOWithExpiresString() {
        List<String> permissions = Utility.arrayList("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";
        Intent intent = new Intent();
        intent.putExtra("access_token", token);
        intent.putExtra("expires_in", "60");
        intent.putExtra("extra_extra", "Something unrelated");
        intent.putExtra("signed_request", WebViewLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        AccessToken accessToken = LoginMethodHandler.createAccessTokenFromWebBundle(permissions, intent.getExtras(), FACEBOOK_APPLICATION_WEB, "1234");
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(FACEBOOK_APPLICATION_WEB, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
    }

    @Test
    public void testFromSSOWithExpiresLong() {
        List<String> permissions = Utility.arrayList("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";
        Intent intent = new Intent();
        intent.putExtra("access_token", token);
        intent.putExtra("expires_in", 60L);
        intent.putExtra("extra_extra", "Something unrelated");
        intent.putExtra("signed_request", WebViewLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        AccessToken accessToken = LoginMethodHandler.createAccessTokenFromWebBundle(permissions, intent.getExtras(), FACEBOOK_APPLICATION_WEB, "1234");
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(FACEBOOK_APPLICATION_WEB, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
    }
}

