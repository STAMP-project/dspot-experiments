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


import Activity.RESULT_CANCELED;
import Activity.RESULT_OK;
import LoginClient.Request;
import LoginClient.Result;
import LoginClient.Result.Code.CANCEL;
import LoginClient.Result.Code.SUCCESS;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.TestUtils;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ LoginClient.class })
public class KatanaProxyLoginMethodHandlerTest extends LoginHandlerTestCase {
    private static final String SIGNED_REQUEST_STR = "ggarbage.eyJhbGdvcml0aG0iOiJITUFDSEEyNTYiLCJ" + "jb2RlIjoid2h5bm90IiwiaXNzdWVkX2F0IjoxNDIyNTAyMDkyLCJ1c2VyX2lkIjoiMTIzIn0";

    @Test
    public void testProxyAuthHandlesSuccess() {
        Bundle bundle = new Bundle();
        bundle.putLong("expires_in", LoginHandlerTestCase.EXPIRES_IN_DELTA);
        bundle.putString("access_token", LoginHandlerTestCase.ACCESS_TOKEN);
        bundle.putString("signed_request", KatanaProxyLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        KatanaProxyLoginMethodHandler handler = new KatanaProxyLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        when(mockLoginClient.getPendingRequest()).thenReturn(request);
        handler.tryAuthorize(request);
        handler.onActivityResult(0, RESULT_OK, intent);
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
    public void testProxyAuthHandlesCancel() {
        Bundle bundle = new Bundle();
        bundle.putString("error", LoginHandlerTestCase.ERROR_MESSAGE);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        KatanaProxyLoginMethodHandler handler = new KatanaProxyLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.tryAuthorize(request);
        handler.onActivityResult(0, RESULT_CANCELED, intent);
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(CANCEL, result.code);
        Assert.assertNull(result.token);
        Assert.assertNotNull(result.errorMessage);
        Assert.assertTrue(result.errorMessage.contains(LoginHandlerTestCase.ERROR_MESSAGE));
    }

    @Test
    public void testProxyAuthHandlesCancelErrorMessage() {
        Bundle bundle = new Bundle();
        bundle.putString("error", "access_denied");
        Intent intent = new Intent();
        intent.putExtras(bundle);
        KatanaProxyLoginMethodHandler handler = new KatanaProxyLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.tryAuthorize(request);
        handler.onActivityResult(0, RESULT_CANCELED, intent);
        ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(CANCEL, result.code);
        Assert.assertNull(result.token);
    }

    @Test
    public void testProxyAuthHandlesDisabled() {
        Bundle bundle = new Bundle();
        bundle.putString("error", "service_disabled");
        Intent intent = new Intent();
        intent.putExtras(bundle);
        KatanaProxyLoginMethodHandler handler = new KatanaProxyLoginMethodHandler(mockLoginClient);
        LoginClient.Request request = createRequest();
        handler.tryAuthorize(request);
        handler.onActivityResult(0, RESULT_OK, intent);
        Mockito.verify(mockLoginClient, Mockito.never()).completeAndValidate(ArgumentMatchers.any(Result.class));
        Mockito.verify(mockLoginClient, Mockito.times(1)).tryNextHandler();
    }
}

