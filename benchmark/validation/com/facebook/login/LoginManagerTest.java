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
import CallbackManagerImpl.RequestCodeOffset.Login;
import DefaultAudience.EVERYONE;
import DefaultAudience.FRIENDS;
import LoginBehavior.NATIVE_ONLY;
import LoginBehavior.NATIVE_WITH_FALLBACK;
import LoginFragment.RESULT_KEY;
import SharedPreferences.Editor;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.facebook.AccessToken;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookPowerMockTestCase;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.Profile;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ FacebookSdk.class, AccessToken.class, Profile.class })
public class LoginManagerTest extends FacebookPowerMockTestCase {
    private static final String MOCK_APP_ID = "1234";

    private static final String USER_ID = "1000";

    private final String TOKEN_STRING = "A token of my esteem";

    private final List<String> PERMISSIONS = Arrays.asList("walk", "chew gum");

    private final Date EXPIRES = new Date(2025, 5, 3);

    private final Date LAST_REFRESH = new Date(2023, 8, 15);

    private final Date DATA_ACCESS_EXPIRATION_TIME = new Date(2025, 5, 3);

    @Mock
    private Activity mockActivity;

    @Mock
    private Fragment mockFragment;

    @Mock
    private Context mockApplicationContext;

    @Mock
    private PackageManager mockPackageManager;

    @Mock
    private FacebookCallback<LoginResult> mockCallback;

    @Mock
    private ThreadPoolExecutor threadExecutor;

    @Mock
    private FragmentActivity mockFragmentActivity;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private Editor mockEditor;

    @Test
    public void testRequiresSdkToBeInitialized() {
        try {
            Mockito.when(FacebookSdk.isInitialized()).thenReturn(false);
            LoginManager loginManager = new LoginManager();
            Assert.fail();
        } catch (FacebookSdkNotInitializedException exception) {
        }
    }

    @Test
    public void testGetInstance() {
        LoginManager loginManager = LoginManager.getInstance();
        Assert.assertNotNull(loginManager);
    }

    @Test
    public void testLoginBehaviorDefaultsToSsoWithFallback() {
        LoginManager loginManager = new LoginManager();
        Assert.assertEquals(NATIVE_WITH_FALLBACK, loginManager.getLoginBehavior());
    }

    @Test
    public void testCanChangeLoginBehavior() {
        LoginManager loginManager = new LoginManager();
        loginManager.setLoginBehavior(NATIVE_ONLY);
        Assert.assertEquals(NATIVE_ONLY, loginManager.getLoginBehavior());
    }

    @Test
    public void testDefaultAudienceDefaultsToFriends() {
        LoginManager loginManager = new LoginManager();
        Assert.assertEquals(FRIENDS, loginManager.getDefaultAudience());
    }

    @Test
    public void testCanChangeDefaultAudience() {
        LoginManager loginManager = new LoginManager();
        loginManager.setDefaultAudience(EVERYONE);
        Assert.assertEquals(EVERYONE, loginManager.getDefaultAudience());
    }

    @Test
    public void testLogInWithReadAndActivityThrowsIfPublishPermissionGiven() {
        LoginManager loginManager = new LoginManager();
        try {
            loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "publish_actions"));
            Assert.fail();
        } catch (FacebookException exception) {
        }
    }

    @Test
    public void testLogInWithPublishAndActivityThrowsIfPublishPermissionGiven() {
        LoginManager loginManager = new LoginManager();
        try {
            loginManager.logInWithPublishPermissions(mockActivity, Arrays.asList("public_profile", "publish_actions"));
            Assert.fail();
        } catch (FacebookException exception) {
        }
    }

    @Test
    public void testLogInThrowsIfCannotResolveFacebookActivity() {
        Mockito.when(mockPackageManager.resolveActivity(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt())).thenReturn(null);
        LoginManager loginManager = new LoginManager();
        try {
            loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
            Assert.fail();
        } catch (FacebookException exception) {
        }
    }

    @Test
    public void testLogInThrowsIfCannotStartFacebookActivity() {
        doThrow(new ActivityNotFoundException()).when(mockActivity).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        LoginManager loginManager = new LoginManager();
        try {
            loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
            Assert.fail();
        } catch (FacebookException exception) {
        }
    }

    @Test
    public void testRequiresNonNullActivity() {
        try {
            LoginManager loginManager = new LoginManager();
            loginManager.logInWithReadPermissions(((Activity) (null)), Arrays.asList("public_profile", "user_friends"));
            Assert.fail();
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testRequiresNonNullFragment() {
        try {
            LoginManager loginManager = new LoginManager();
            loginManager.logInWithReadPermissions(((Fragment) (null)), Arrays.asList("public_profile", "user_friends"));
            Assert.fail();
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testLogInWithReadDoesNotThrowWithReadPermissions() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
    }

    @Test
    public void testLogInWithReadListCreatesPendingRequestWithCorrectValues() {
        LoginManager loginManager = new LoginManager();
        // Change some defaults so we can verify the pending request picks them up.
        loginManager.setLoginBehavior(NATIVE_ONLY);
        loginManager.setDefaultAudience(EVERYONE);
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        implTestLogInCreatesPendingRequestWithCorrectValues(loginManager, Arrays.asList("public_profile", "user_friends"));
    }

    @Test
    public void testLogInWithReadAndAccessTokenCreatesReauthRequest() {
        AccessToken accessToken = createAccessToken();
        stub(method(AccessToken.class, "getCurrentAccessToken")).toReturn(accessToken);
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        int loginRequestCode = Login.toRequestCode();
        Mockito.verify(mockActivity, Mockito.times(1)).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.eq(loginRequestCode));
    }

    @Test
    public void testLogInWithReadAndActivityStartsFacebookActivityWithCorrectRequest() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mockActivity).startActivityForResult(intentArgumentCaptor.capture(), ArgumentMatchers.anyInt());
        Intent intent = intentArgumentCaptor.getValue();
        ComponentName componentName = intent.getComponent();
        Assert.assertEquals(FacebookActivity.class.getName(), componentName.getClassName());
        Assert.assertEquals(NATIVE_WITH_FALLBACK.name(), intent.getAction());
    }

    @Test
    public void testLogInWithReadAndFragmentStartsFacebookActivityWithCorrectRequest() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockFragment, Arrays.asList("public_profile", "user_friends"));
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mockFragment).startActivityForResult(intentArgumentCaptor.capture(), ArgumentMatchers.anyInt());
        Intent intent = intentArgumentCaptor.getValue();
        ComponentName componentName = intent.getComponent();
        Assert.assertEquals(FacebookActivity.class.getName(), componentName.getClassName());
        Assert.assertEquals(NATIVE_WITH_FALLBACK.name(), intent.getAction());
    }

    @Test
    public void testLogInWitPublishDoesNotThrowWithPublishPermissions() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithPublishPermissions(mockActivity, Arrays.asList("publish_actions", "publish_stream"));
    }

    @Test
    public void testLogInWithPublishListCreatesPendingRequestWithCorrectValues() {
        LoginManager loginManager = new LoginManager();
        // Change some defaults so we can verify the pending request picks them up.
        loginManager.setLoginBehavior(NATIVE_ONLY);
        loginManager.setDefaultAudience(EVERYONE);
        loginManager.logInWithPublishPermissions(mockActivity, Arrays.asList("publish_actions", "publish_stream"));
        implTestLogInCreatesPendingRequestWithCorrectValues(loginManager, Arrays.asList("publish_actions", "publish_stream"));
    }

    @Test
    public void testLogInWithPublishAndAccessTokenCreatesReauthRequest() {
        AccessToken accessToken = createAccessToken();
        stub(method(AccessToken.class, "getCurrentAccessToken")).toReturn(accessToken);
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithPublishPermissions(mockActivity, Arrays.asList("publish_actions", "publish_stream"));
        int loginRequestCode = Login.toRequestCode();
        Mockito.verify(mockActivity, Mockito.times(1)).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.eq(loginRequestCode));
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnCancelResultCode() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_CANCELED, null, mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onCancel();
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnCancelResultCodeEvenWithData() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        Intent intent = createSuccessResultIntent();
        boolean result = loginManager.onActivityResult(RESULT_CANCELED, intent, mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onCancel();
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnActivityResultDoesNotModifyCurrentAccessTokenOnCancelResultCode() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        loginManager.onActivityResult(RESULT_CANCELED, null, mockCallback);
        verifyStatic(Mockito.never());
        AccessToken.setCurrentAccessToken(ArgumentMatchers.any(AccessToken.class));
    }

    @Test
    public void testOnActivityResultHandlesMissingCallbackOnCancelResultCode() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_CANCELED, null);
        Assert.assertTrue(result);
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnNullData() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_OK, null, mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onError(ArgumentMatchers.isA(FacebookException.class));
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnMissingResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        Intent intent = createSuccessResultIntent();
        intent.removeExtra(RESULT_KEY);
        boolean result = loginManager.onActivityResult(RESULT_OK, intent, mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onError(ArgumentMatchers.isA(FacebookException.class));
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnErrorResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_OK, createErrorResultIntent(), mockCallback);
        ArgumentCaptor<FacebookException> exceptionArgumentCaptor = ArgumentCaptor.forClass(FacebookException.class);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onError(exceptionArgumentCaptor.capture());
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
        Assert.assertEquals("foo: bar", exceptionArgumentCaptor.getValue().getMessage());
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnCancelResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_CANCELED, createCancelResultIntent(), mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.times(1)).onCancel();
        Mockito.verify(mockCallback, Mockito.never()).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnActivityResultDoesNotModifyCurrentAccessTokenOnErrorResultCode() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        loginManager.onActivityResult(RESULT_CANCELED, createErrorResultIntent(), mockCallback);
        verifyStatic(Mockito.never());
        AccessToken.setCurrentAccessToken(ArgumentMatchers.any(AccessToken.class));
    }

    @Test
    public void testOnActivityResultReturnsTrueAndCallsCallbackOnSuccessResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_OK, createSuccessResultIntent(), mockCallback);
        Assert.assertTrue(result);
        Mockito.verify(mockCallback, Mockito.never()).onError(ArgumentMatchers.any(FacebookException.class));
        Mockito.verify(mockCallback, Mockito.times(1)).onSuccess(ArgumentMatchers.isA(LoginResult.class));
    }

    @Test
    public void testOnHandlesMissingCallbackkOnSuccessResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_OK, createSuccessResultIntent(), null);
        Assert.assertTrue(result);
    }

    @Test
    public void testOnActivityResultSetsCurrentAccessTokenOnSuccessResult() {
        LoginManager loginManager = new LoginManager();
        loginManager.logInWithReadPermissions(mockActivity, Arrays.asList("public_profile", "user_friends"));
        boolean result = loginManager.onActivityResult(RESULT_OK, createSuccessResultIntent(), mockCallback);
        verifyStatic(Mockito.times(1));
        AccessToken.setCurrentAccessToken(ArgumentMatchers.eq(createAccessToken()));
    }
}

