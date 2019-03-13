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
import com.facebook.AccessToken;
import com.facebook.FacebookTestCase;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class LoginResultTest extends FacebookTestCase {
    private final Set<String> EMAIL_SET = new HashSet<String>() {
        {
            add("email");
        }
    };

    private final Set<String> LIKES_EMAIL_SET = new HashSet<String>() {
        {
            add("user_likes");
            add("email");
        }
    };

    private final Set<String> PROFILE_EMAIL_SET = new HashSet<String>() {
        {
            add("user_profile");
            add("email");
        }
    };

    @Test
    public void testInitialLogin() {
        LoginClient.Request request = createRequest(EMAIL_SET, false);
        AccessToken accessToken = createAccessToken(PROFILE_EMAIL_SET, new HashSet<String>());
        LoginResult result = LoginManager.computeLoginResult(request, accessToken);
        Assert.assertEquals(accessToken, result.getAccessToken());
        Assert.assertEquals(PROFILE_EMAIL_SET, result.getRecentlyGrantedPermissions());
        Assert.assertEquals(0, result.getRecentlyDeniedPermissions().size());
    }

    @Test
    public void testReAuth() {
        LoginClient.Request request = createRequest(EMAIL_SET, true);
        AccessToken accessToken = createAccessToken(PROFILE_EMAIL_SET, new HashSet<String>());
        LoginResult result = LoginManager.computeLoginResult(request, accessToken);
        Assert.assertEquals(accessToken, result.getAccessToken());
        Assert.assertEquals(EMAIL_SET, result.getRecentlyGrantedPermissions());
        Assert.assertEquals(0, result.getRecentlyDeniedPermissions().size());
    }

    @Test
    public void testDeniedPermissions() {
        LoginClient.Request request = createRequest(LIKES_EMAIL_SET, true);
        AccessToken accessToken = createAccessToken(EMAIL_SET, new HashSet<String>());
        LoginResult result = LoginManager.computeLoginResult(request, accessToken);
        Assert.assertEquals(accessToken, result.getAccessToken());
        Assert.assertEquals(EMAIL_SET, result.getRecentlyGrantedPermissions());
        Assert.assertEquals(new HashSet<String>() {
            {
                add("user_likes");
            }
        }, result.getRecentlyDeniedPermissions());
    }
}

