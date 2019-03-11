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


import AccessTokenSource.FACEBOOK_APPLICATION_NATIVE;
import LoginClient.Request;
import LoginClient.Result;
import LoginClient.Result.Code.SUCCESS;
import NativeProtocol.EXTRA_ACCESS_TOKEN;
import NativeProtocol.EXTRA_EXPIRES_SECONDS_SINCE_EPOCH;
import NativeProtocol.EXTRA_PERMISSIONS;
import NativeProtocol.EXTRA_USER_ID;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.TestUtils;
import com.facebook.internal.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ LoginClient.class })
public class GetTokenLoginMethodHandlerTest extends LoginHandlerTestCase {
    @Test
    public void testGetTokenHandlesSuccessWithAllPermissions() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(EXTRA_PERMISSIONS, new ArrayList<String>(LoginHandlerTestCase.PERMISSIONS));
        bundle.putLong(EXTRA_EXPIRES_SECONDS_SINCE_EPOCH, (((new Date().getTime()) / 1000) + (LoginHandlerTestCase.EXPIRES_IN_DELTA)));
        bundle.putString(EXTRA_ACCESS_TOKEN, LoginHandlerTestCase.ACCESS_TOKEN);
        bundle.putString(EXTRA_USER_ID, LoginHandlerTestCase.USER_ID);
        GetTokenLoginMethodHandler handler = new GetTokenLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.getTokenCompleted(request, bundle);
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
    public void testGetTokenHandlesSuccessWithOnlySomePermissions() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(EXTRA_PERMISSIONS, new ArrayList<String>(Arrays.asList("go outside")));
        bundle.putLong(EXTRA_EXPIRES_SECONDS_SINCE_EPOCH, (((new Date().getTime()) / 1000) + (LoginHandlerTestCase.EXPIRES_IN_DELTA)));
        bundle.putString(EXTRA_ACCESS_TOKEN, LoginHandlerTestCase.ACCESS_TOKEN);
        GetTokenLoginMethodHandler handler = new GetTokenLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        Assert.assertEquals(LoginHandlerTestCase.PERMISSIONS.size(), request.getPermissions().size());
        handler.getTokenCompleted(request, bundle);
        Mockito.verify(mockLoginClient, Mockito.never()).completeAndValidate(ArgumentMatchers.any(Result.class));
        Mockito.verify(mockLoginClient, Mockito.times(1)).tryNextHandler();
    }

    @Test
    public void testGetTokenHandlesNoResult() {
        GetTokenLoginMethodHandler handler = new GetTokenLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        Assert.assertEquals(LoginHandlerTestCase.PERMISSIONS.size(), request.getPermissions().size());
        handler.getTokenCompleted(request, null);
        Mockito.verify(mockLoginClient, Mockito.never()).completeAndValidate(ArgumentMatchers.any(Result.class));
        Mockito.verify(mockLoginClient, Mockito.times(1)).tryNextHandler();
    }

    @Test
    public void testFromNativeLogin() {
        ArrayList<String> permissions = Utility.arrayList("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";
        String userId = "1000";
        long nowSeconds = (new Date().getTime()) / 1000;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACCESS_TOKEN, token);
        intent.putExtra(EXTRA_EXPIRES_SECONDS_SINCE_EPOCH, (nowSeconds + 60L));
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        intent.putExtra(EXTRA_USER_ID, userId);
        AccessToken accessToken = GetTokenLoginMethodHandler.createAccessTokenFromNativeLogin(intent.getExtras(), FACEBOOK_APPLICATION_NATIVE, "1234");
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(FACEBOOK_APPLICATION_NATIVE, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
    }
}

