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


import LoginClient.Request;
import LoginClient.Result;
import LoginClient.Result.Code.CANCEL;
import LoginClient.Result.Code.ERROR;
import LoginClient.Result.Code.SUCCESS;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookSdk;
import com.facebook.TestUtils;
import com.facebook.internal.FetchedAppSettings;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ LoginClient.class, Validate.class, Utility.class, FacebookSdk.class, AccessToken.class, FetchedAppSettings.class, FetchedAppSettingsManager.class })
public class CustomTabLoginMethodHandlerTest extends LoginHandlerTestCase {
    private static final String SIGNED_REQUEST_STR = "ggarbage.eyJhbGdvcml0aG0iOiJITUFDSEEyNTYiLCJ" + "jb2RlIjoid2h5bm90IiwiaXNzdWVkX2F0IjoxNDIyNTAyMDkyLCJ1c2VyX2lkIjoiMTIzIn0";

    private static final String CHROME_PACKAGE = "com.android.chrome";

    private static final String DEV_PACKAGE = "com.chrome.dev";

    private static final String BETA_PACKAGE = "com.chrome.beta";

    private CustomTabLoginMethodHandler handler;

    private Request request;

    @Test
    public void testCustomTabHandlesSuccess() {
        final Bundle bundle = new Bundle();
        bundle.putString("access_token", LoginHandlerTestCase.ACCESS_TOKEN);
        bundle.putString("expires_in", String.format("%d", LoginHandlerTestCase.EXPIRES_IN_DELTA));
        bundle.putString("code", "Something else");
        bundle.putString("signed_request", CustomTabLoginMethodHandlerTest.SIGNED_REQUEST_STR);
        handler.onComplete(request, bundle, null);
        final ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        final LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(SUCCESS, result.code);
        final AccessToken token = result.token;
        Assert.assertNotNull(token);
        Assert.assertEquals(LoginHandlerTestCase.ACCESS_TOKEN, token.getToken());
        assertDateDiffersWithinDelta(new Date(), token.getExpires(), ((LoginHandlerTestCase.EXPIRES_IN_DELTA) * 1000), 1000);
        TestUtils.assertSamePermissions(LoginHandlerTestCase.PERMISSIONS, token.getPermissions());
    }

    @Test
    public void testCustomTabHandlesCancel() {
        handler.onComplete(request, null, new FacebookOperationCanceledException());
        final ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        final LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(CANCEL, result.code);
        Assert.assertNull(result.token);
        Assert.assertNotNull(result.errorMessage);
    }

    @Test
    public void testCustomTabHandlesError() {
        handler.onComplete(request, null, new FacebookException(LoginHandlerTestCase.ERROR_MESSAGE));
        final ArgumentCaptor<LoginClient.Result> resultArgumentCaptor = ArgumentCaptor.forClass(Result.class);
        Mockito.verify(mockLoginClient, Mockito.times(1)).completeAndValidate(resultArgumentCaptor.capture());
        final LoginClient.Result result = resultArgumentCaptor.getValue();
        Assert.assertNotNull(result);
        Assert.assertEquals(ERROR, result.code);
        Assert.assertNull(result.token);
        Assert.assertNotNull(result.errorMessage);
        Assert.assertEquals(LoginHandlerTestCase.ERROR_MESSAGE, result.errorMessage);
    }

    @Test
    public void testTryAuthorizeNeedsRedirectActivity() {
        mockTryAuthorize();
        mockChromeCustomTabsSupported(true, CustomTabLoginMethodHandlerTest.CHROME_PACKAGE);
        mockCustomTabsAllowed(true);
        mockCustomTabRedirectActivity(true);
        Assert.assertTrue(handler.tryAuthorize(request));
        mockCustomTabsAllowed(false);
        Assert.assertFalse(handler.tryAuthorize(request));
    }

    @Test
    public void testTryAuthorizeWithChromePackage() {
        mockTryAuthorize();
        mockCustomTabsAllowed(true);
        mockCustomTabRedirectActivity(true);
        mockChromeCustomTabsSupported(true, CustomTabLoginMethodHandlerTest.CHROME_PACKAGE);
        Assert.assertTrue(handler.tryAuthorize(request));
    }

    @Test
    public void testTryAuthorizeWithChromeBetaPackage() {
        mockTryAuthorize();
        mockCustomTabsAllowed(true);
        mockCustomTabRedirectActivity(true);
        mockChromeCustomTabsSupported(true, CustomTabLoginMethodHandlerTest.BETA_PACKAGE);
        Assert.assertTrue(handler.tryAuthorize(request));
    }

    @Test
    public void testTryAuthorizeWithChromeDevPackage() {
        mockTryAuthorize();
        mockCustomTabsAllowed(true);
        mockCustomTabRedirectActivity(true);
        mockChromeCustomTabsSupported(true, CustomTabLoginMethodHandlerTest.DEV_PACKAGE);
        Assert.assertTrue(handler.tryAuthorize(request));
    }

    @Test
    public void testTryAuthorizeWithoutChromePackage() {
        mockTryAuthorize();
        mockCustomTabsAllowed(true);
        mockCustomTabRedirectActivity(true);
        mockChromeCustomTabsSupported(true, "not.chrome.package");
        Assert.assertFalse(handler.tryAuthorize(request));
    }
}

