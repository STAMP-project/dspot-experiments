/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import BuildConfig.APPLICATION_ID;
import Manifest.permission.ACCESS_NETWORK_STATE;
import Parse.Configuration;
import ParseAnonymousUtils.AUTH_TYPE;
import ParseException.SCRIPT_ERROR;
import ParseObject.CREATOR;
import RuntimeEnvironment.application;
import android.os.Parcel;
import bolts.Capture;
import bolts.Task;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static ParseException.ACCOUNT_ALREADY_LINKED;
import static ParseException.OTHER_CAUSE;


// For ParseExecutors.main()
// endregion
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseUserTest extends ResetPluginsParseTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // region Parcelable
    @Test
    public void testImmutableKeys() {
        ParseUser user = new ParseUser();
        user.put("foo", "bar");
        try {
            user.put("sessionToken", "blah");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot modify"));
        }
        try {
            user.remove("sessionToken");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot modify"));
        }
        try {
            user.removeAll("sessionToken", Collections.emptyList());
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot modify"));
        }
    }

    @Test
    public void testOnSaveRestoreState() {
        ParseUser user = new ParseUser();
        user.setObjectId("objId");
        user.setIsCurrentUser(true);
        Parcel parcel = Parcel.obtain();
        user.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        user = ((ParseUser) (CREATOR.createFromParcel(parcel)));
        Assert.assertTrue(user.isCurrentUser());
    }

    // endregion
    // region SignUpAsync
    @Test
    public void testParcelableState() {
        ParseUser.State state = new ParseUser.State.Builder().objectId("test").isNew(true).build();
        ParseUser user = ParseObject.from(state);
        Assert.assertTrue(user.isNew());
        Parcel parcel = Parcel.obtain();
        user.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        user = ((ParseUser) (CREATOR.createFromParcel(parcel)));
        Assert.assertTrue(user.isNew());
    }

    @Test
    public void testSignUpAsyncWithNoUserName() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username cannot be missing or blank");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
    }

    @Test
    public void testSignUpAsyncWithNoPassword() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Password cannot be missing or blank");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
    }

    @Test
    public void testSignUpAsyncWithObjectIdSetAndAuthDataNotSet() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").build();
        ParseUser user = ParseObject.from(userState);
        user.setUsername("userName");
        user.setPassword("password");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot sign up a user that has already signed up.");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
    }

    @Test
    public void testSignUpAsyncWithObjectIdSetAndAuthDataSet() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        Mockito.when(currentUser.getSessionToken()).thenReturn("sessionToken");
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").putAuthData(AUTH_TYPE, null).build();
        ParseUser user = ParseObject.from(userState);
        user.setUsername("userName");
        user.setPassword("password");
        // TODO (mengyan): Avoid using partial mock after we have ParseObjectInstanceController
        ParseUser partialMockUser = Mockito.spy(user);
        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockUser).saveAsync(ArgumentMatchers.anyString(), Matchers.<Task<Void>>any());
        ParseTaskUtils.wait(partialMockUser.signUpAsync(Task.<Void>forResult(null)));
        // Verify user is saved
        Mockito.verify(partialMockUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("sessionToken"), Matchers.<Task<Void>>any());
    }

    @Test
    public void testSignUpAsyncWithAnotherSignUpAlreadyRunning() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        user.startSave();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot sign up a user that is already signing up.");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
    }

    @Test
    public void testSignUpAsyncWithSignUpSameAnonymousUser() throws Exception {
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        Map<String, String> anonymousAuthData = new HashMap<>();
        anonymousAuthData.put("key", "token");
        user.putAuthData(AUTH_TYPE, anonymousAuthData);
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(user));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Attempt to merge currentUser with itself.");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
    }

    @Test
    public void testSignUpAsyncWithMergeInDiskAnonymousUser() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        Mockito.when(currentUser.getUsername()).thenReturn("oldUserName");
        Mockito.when(currentUser.getPassword()).thenReturn("oldPassword");
        Mockito.when(currentUser.isLazy()).thenReturn(false);
        Mockito.when(currentUser.isLinked(AUTH_TYPE)).thenReturn(true);
        Mockito.when(currentUser.getSessionToken()).thenReturn("oldSessionToken");
        Mockito.when(currentUser.getAuthData()).thenReturn(new HashMap<String, Map<String, String>>());
        Mockito.when(currentUser.saveAsync(ArgumentMatchers.anyString(), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any())).thenReturn(Task.<Void>forResult(null));
        ParseUser.State state = new ParseUser.State.Builder().put("oldKey", "oldValue").build();
        Mockito.when(currentUser.getState()).thenReturn(state);
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        Map<String, String> anonymousAuthData = new HashMap<>();
        anonymousAuthData.put("key", "token");
        user.putAuthData(AUTH_TYPE, anonymousAuthData);
        Task<Void> signUpTask = user.signUpAsync(Task.<Void>forResult(null));
        signUpTask.waitForCompletion();
        // Make sure currentUser copy changes from user
        Mockito.verify(currentUser, Mockito.times(1)).copyChangesFrom(user);
        // Make sure we update currentUser username and password
        Mockito.verify(currentUser, Mockito.times(1)).setUsername("userName");
        Mockito.verify(currentUser, Mockito.times(1)).setPassword("password");
        // Make sure we save currentUser
        Mockito.verify(currentUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("oldSessionToken"), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        // Make sure we merge currentUser with user after save
        Assert.assertEquals("oldValue", user.get("oldKey"));
        // Make sure set currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(ArgumentMatchers.eq(user));
    }

    @Test
    public void testSignUpAsyncWithMergeInDiskAnonymousUserSaveFailure() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        Map<String, String> oldAnonymousAuthData = new HashMap<>();
        oldAnonymousAuthData.put("oldKey", "oldToken");
        currentUser.putAuthData(AUTH_TYPE, oldAnonymousAuthData);
        ParseUser partialMockCurrentUser = Mockito.spy(currentUser);// Spy since we need mutex

        Mockito.when(partialMockCurrentUser.getUsername()).thenReturn("oldUserName");
        Mockito.when(partialMockCurrentUser.getPassword()).thenReturn("oldPassword");
        Mockito.when(partialMockCurrentUser.getSessionToken()).thenReturn("oldSessionToken");
        Mockito.when(partialMockCurrentUser.isLazy()).thenReturn(false);
        ParseException saveException = new ParseException(OTHER_CAUSE, "");
        Mockito.doReturn(Task.<Void>forError(saveException)).when(partialMockCurrentUser).saveAsync(ArgumentMatchers.anyString(), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(partialMockCurrentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        Map<String, String> anonymousAuthData = new HashMap<>();
        anonymousAuthData.put("key", "token");
        user.putAuthData(AUTH_TYPE, anonymousAuthData);
        Task<Void> signUpTask = user.signUpAsync(Task.<Void>forResult(null));
        signUpTask.waitForCompletion();
        // Make sure we update currentUser username and password
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).setUsername("userName");
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).setPassword("password");
        // Make sure we sync user with currentUser
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).copyChangesFrom(ArgumentMatchers.eq(user));
        // Make sure we save currentUser
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("oldSessionToken"), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        // Make sure we restore old username and password after save fails
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).setUsername("oldUserName");
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).setPassword("oldPassword");
        // Make sure we restore anonymity
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).putAuthData(AUTH_TYPE, oldAnonymousAuthData);
        // Make sure task is failed
        Assert.assertTrue(signUpTask.isFaulted());
        Assert.assertSame(saveException, signUpTask.getError());
    }

    @Test
    public void testSignUpAsyncWithNoCurrentUserAndSignUpSuccess() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").build();
        Mockito.when(userController.signUpAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.anyString())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        ParseTaskUtils.wait(user.signUpAsync(Task.<Void>forResult(null)));
        // Make sure we sign up the user
        Mockito.verify(userController, Mockito.times(1)).signUpAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.anyString());
        // Make sure user's data is correct
        Assert.assertEquals("newSessionToken", user.getSessionToken());
        Assert.assertEquals("newValue", user.getString("newKey"));
        Assert.assertFalse(user.isLazy());
        // Make sure we set the current user
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(user);
    }

    // endregion
    // region testLogInWithAsync
    @Test
    public void testSignUpAsyncWithNoCurrentUserAndSignUpFailure() {
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseException signUpException = new ParseException(OTHER_CAUSE, "test");
        Mockito.when(userController.signUpAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.anyString())).thenReturn(Task.<ParseUser.State>forError(signUpException));
        ParseCorePlugins.getInstance().registerUserController(userController);
        ParseUser user = new ParseUser();
        user.put("key", "value");
        user.setUsername("userName");
        user.setPassword("password");
        Task<Void> signUpTask = user.signUpAsync(Task.<Void>forResult(null));
        // Make sure we sign up the user
        Mockito.verify(userController, Mockito.times(1)).signUpAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.anyString());
        // Make sure user's data is correct
        Assert.assertEquals("value", user.getString("key"));
        // Make sure we never set the current user
        Mockito.verify(currentUserController, Mockito.never()).setAsync(user);
        // Make sure task is failed
        Assert.assertTrue(signUpTask.isFaulted());
        Assert.assertSame(signUpException, signUpTask.getError());
    }

    @Test
    public void testLoginWithAsyncWithoutExistingLazyUser() throws ParseException {
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(false)).thenReturn(Task.<ParseUser>forResult(null));
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseUser.State userState = Mockito.mock(ParseUser.State.class);
        Mockito.when(userState.className()).thenReturn("_User");
        Mockito.when(userState.objectId()).thenReturn("1234");
        Mockito.when(userState.isComplete()).thenReturn(true);
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        Mockito.when(userController.logInAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn(Task.forResult(userState));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseCorePlugins.getInstance().registerUserController(userController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        ParseUser user = ParseTaskUtils.wait(ParseUser.logInWithInBackground(authType, authData));
        Mockito.verify(currentUserController).getAsync(false);
        Mockito.verify(userController).logInAsync(authType, authData);
        Mockito.verify(currentUserController).setAsync(user);
        Assert.assertSame(userState, user.getState());
        Mockito.verifyNoMoreInteractions(currentUserController);
        Mockito.verifyNoMoreInteractions(userController);
    }

    @Test
    public void testLoginWithAsyncWithLinkedLazyUser() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.putAuthData(AUTH_TYPE, new HashMap<String, String>());
        ParseUserTest.setLazy(currentUser);
        ParseUser partialMockCurrentUser = Mockito.spy(currentUser);
        Mockito.when(partialMockCurrentUser.getSessionToken()).thenReturn("oldSessionToken");
        Mockito.doReturn(Task.<ParseUser>forResult(null)).when(partialMockCurrentUser).resolveLazinessAsync(Matchers.<Task<Void>>any());
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(false)).thenReturn(Task.forResult(partialMockCurrentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        ParseUser userAfterLogin = ParseTaskUtils.wait(ParseUser.logInWithInBackground(authType, authData));
        // Make sure we stripAnonymity
        Assert.assertNull(userAfterLogin.getAuthData().get(AUTH_TYPE));
        // Make sure we update authData
        Assert.assertEquals(authData, userAfterLogin.getAuthData().get("facebook"));
        // Make sure we resolveLaziness
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).resolveLazinessAsync(Matchers.<Task<Void>>any());
    }

    @Test
    public void testLoginWithAsyncWithLinkedLazyUseAndResolveLazinessFailure() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        Map<String, String> oldAnonymousAuthData = new HashMap<>();
        oldAnonymousAuthData.put("oldKey", "oldToken");
        currentUser.putAuthData(AUTH_TYPE, oldAnonymousAuthData);
        ParseUser partialMockCurrentUser = Mockito.spy(currentUser);
        Mockito.when(partialMockCurrentUser.getSessionToken()).thenReturn("oldSessionToken");
        Mockito.doReturn(Task.<ParseUser>forError(new Exception())).when(partialMockCurrentUser).resolveLazinessAsync(Matchers.<Task<Void>>any());
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(false)).thenReturn(Task.forResult(partialMockCurrentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        Task<ParseUser> loginTask = ParseUser.logInWithInBackground(authType, authData);
        loginTask.waitForCompletion();
        // Make sure we try to resolveLaziness
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).resolveLazinessAsync(Matchers.<Task<Void>>any());
        // Make sure we do not save new authData
        Assert.assertNull(partialMockCurrentUser.getAuthData().get("facebook"));
        // Make sure we restore anonymity after resolve laziness failure
        Assert.assertEquals(oldAnonymousAuthData, partialMockCurrentUser.getAuthData().get(AUTH_TYPE));
        // Make sure task fails
        Assert.assertTrue(loginTask.isFaulted());
    }

    @Test
    public void testLoginWithAsyncWithLinkedNotLazyUser() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser.State state = // Make it not lazy
        new ParseUser.State.Builder().objectId("objectId").putAuthData(AUTH_TYPE, new HashMap<String, String>()).build();
        ParseUser currentUser = ParseUser.from(state);
        ParseUser partialMockCurrentUser = Mockito.spy(currentUser);// ParseUser.mutex

        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockCurrentUser).linkWithInBackground(ArgumentMatchers.anyString(), Matchers.<Map<String, String>>any());
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync()).thenReturn(Task.forResult(partialMockCurrentUser));
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(partialMockCurrentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        ParseUser userAfterLogin = ParseTaskUtils.wait(ParseUser.logInWithInBackground(authType, authData));
        // Make sure we link authData
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).linkWithInBackground(authType, authData);
        Assert.assertSame(partialMockCurrentUser, userAfterLogin);
    }

    @Test
    public void testLoginWithAsyncWithLinkedNotLazyUserLinkFailure() throws Exception {
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.anyString(), Matchers.<Map<String, String>>any())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.putAuthData(AUTH_TYPE, new HashMap<String, String>());
        currentUser.setObjectId("objectId");// Make it not lazy.

        ParseUser partialMockCurrentUser = Mockito.spy(currentUser);
        Mockito.when(partialMockCurrentUser.getSessionToken()).thenReturn("sessionToken");
        ParseException linkException = new ParseException(ACCOUNT_ALREADY_LINKED, "Account already linked");
        Mockito.doReturn(Task.<Void>forError(linkException)).when(partialMockCurrentUser).linkWithInBackground(ArgumentMatchers.anyString(), Matchers.<Map<String, String>>any());
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(false)).thenReturn(Task.forResult(partialMockCurrentUser));
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        ParseUser userAfterLogin = ParseTaskUtils.wait(ParseUser.logInWithInBackground(authType, authData));
        // Make sure we link authData
        Mockito.verify(partialMockCurrentUser, Mockito.times(1)).linkWithInBackground(authType, authData);
        // Make sure we login authData
        Mockito.verify(userController, Mockito.times(1)).logInAsync("facebook", authData);
        // Make sure we save the new created user as currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(ArgumentMatchers.any(ParseUser.class));
        // Make sure the new created user has correct data
        Assert.assertEquals("newValue", userAfterLogin.get("newKey"));
        Assert.assertEquals("newSessionToken", userAfterLogin.getSessionToken());
    }

    // endregion
    // region testlinkWithInBackground
    @Test
    public void testLoginWithAsyncWithNoCurrentUser() throws Exception {
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.anyString(), Matchers.<Map<String, String>>any())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(false)).thenReturn(Task.<ParseUser>forResult(null));
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "123");
        ParseUser userAfterLogin = ParseTaskUtils.wait(ParseUser.logInWithInBackground(authType, authData));
        // Make sure we login authData
        Mockito.verify(userController, Mockito.times(1)).logInAsync("facebook", authData);
        // Make sure we save the new created user as currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(ArgumentMatchers.any(ParseUser.class));
        // Make sure the new created user has correct data
        Assert.assertEquals("newValue", userAfterLogin.get("newKey"));
        Assert.assertEquals("newSessionToken", userAfterLogin.getSessionToken());
    }

    @Test
    public void testlinkWithInBackgroundWithSaveAsyncSuccess() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getCurrentSessionTokenAsync()).thenReturn(Task.<String>forResult(null));
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register mock callbacks
        AuthenticationCallback callbacks = Mockito.mock(AuthenticationCallback.class);
        Mockito.when(callbacks.onRestore(Matchers.<Map<String, String>>any())).thenReturn(true);
        ParseUser.registerAuthenticationCallback("facebook", callbacks);
        ParseUser user = new ParseUser();
        // To make synchronizeAuthData work
        user.setIsCurrentUser(true);
        // To verify stripAnonymity
        user.setObjectId("objectId");
        user.putAuthData(AUTH_TYPE, new HashMap<String, String>());
        ParseUser partialMockUser = Mockito.spy(user);
        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockUser).saveAsync(ArgumentMatchers.anyString(), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        Mockito.doReturn("sessionTokenAgain").when(partialMockUser).getSessionToken();
        Map<String, String> authData = new HashMap<>();
        authData.put("token", "test");
        ParseTaskUtils.wait(partialMockUser.linkWithInBackground("facebook", authData));
        // Make sure we stripAnonymity
        Assert.assertNull(partialMockUser.getAuthData().get(AUTH_TYPE));
        // Make sure new authData is added
        Assert.assertSame(authData, partialMockUser.getAuthData().get("facebook"));
        // Make sure we save the user
        Mockito.verify(partialMockUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("sessionTokenAgain"), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        // Make sure synchronizeAuthData() is called
        Mockito.verify(callbacks, Mockito.times(1)).onRestore(authData);
    }

    // endregion
    // region testResolveLazinessAsync
    @Test
    public void testlinkWithInBackgroundWithSaveAsyncFailure() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getCurrentSessionTokenAsync()).thenReturn(Task.forResult("sessionToken"));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        Map<String, String> anonymousAuthData = new HashMap<>();
        anonymousAuthData.put("anonymousToken", "anonymousTest");
        // To verify stripAnonymity
        user.setObjectId("objectId");
        user.putAuthData(AUTH_TYPE, anonymousAuthData);
        ParseUser partialMockUser = Mockito.spy(user);
        Exception saveException = new Exception();
        Mockito.doReturn(Task.<Void>forError(saveException)).when(partialMockUser).saveAsync(ArgumentMatchers.anyString(), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        Mockito.doReturn("sessionTokenAgain").when(partialMockUser).getSessionToken();
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("facebookToken", "facebookTest");
        Task<Void> linkTask = partialMockUser.linkWithInBackground(authType, authData);
        linkTask.waitForCompletion();
        // Make sure we save the user
        Mockito.verify(partialMockUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("sessionTokenAgain"), ArgumentMatchers.eq(false), Matchers.<Task<Void>>any());
        // Make sure old authData is restored
        Assert.assertSame(anonymousAuthData, partialMockUser.getAuthData().get(AUTH_TYPE));
        // Make sure failed new authData is cleared
        Assert.assertNull(partialMockUser.getAuthData().get("facebook"));
        // Verify exception
        Assert.assertSame(saveException, linkTask.getError());
    }

    @Test
    public void testResolveLazinessAsyncWithAuthDataAndNotNewUser() throws Exception {
        ParseUser user = new ParseUser();
        ParseUserTest.setLazy(user);
        user.putAuthData("facebook", new HashMap<String, String>());
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").isNew(false).build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class))).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseTaskUtils.wait(user.resolveLazinessAsync(Task.<Void>forResult(null)));
        ArgumentCaptor<ParseUser> userAfterResolveLazinessCaptor = ArgumentCaptor.forClass(ParseUser.class);
        // Make sure we logIn the lazy user
        Mockito.verify(userController, Mockito.times(1)).logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class));
        // Make sure we save currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(userAfterResolveLazinessCaptor.capture());
        ParseUser userAfterResolveLaziness = userAfterResolveLazinessCaptor.getValue();
        // Make sure user's data is correct
        Assert.assertEquals("newSessionToken", userAfterResolveLaziness.getSessionToken());
        Assert.assertEquals("newValue", userAfterResolveLaziness.get("newKey"));
        // Make sure userAfterResolveLaziness is not lazy
        Assert.assertFalse(userAfterResolveLaziness.isLazy());
        // Make sure we create new user
        Assert.assertNotSame(user, userAfterResolveLaziness);
    }

    @Test
    public void testResolveLazinessAsyncWithAuthDataAndNewUser() throws Exception {
        ParseUser user = new ParseUser();
        ParseUserTest.setLazy(user);
        user.putAuthData("facebook", new HashMap<String, String>());
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().objectId("objectId").put("newKey", "newValue").sessionToken("newSessionToken").isNew(true).build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class))).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        // Register a mock currentUserController to verify setAsync
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseTaskUtils.wait(user.resolveLazinessAsync(Task.<Void>forResult(null)));
        // Make sure we logIn the lazy user
        Mockito.verify(userController, Mockito.times(1)).logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class));
        // Make sure we do not save currentUser
        Mockito.verify(currentUserController, Mockito.never()).setAsync(ArgumentMatchers.any(ParseUser.class));
        // Make sure userAfterResolveLaziness's data is correct
        Assert.assertEquals("newSessionToken", user.getSessionToken());
        Assert.assertEquals("newValue", user.get("newKey"));
        // Make sure userAfterResolveLaziness is not lazy
        Assert.assertFalse(user.isLazy());
    }

    // endregion
    // region testValidateSave
    @Test
    public void testResolveLazinessAsyncWithAuthDataAndNotNewUserAndLDSEnabled() throws Exception {
        ParseUser user = new ParseUser();
        ParseUserTest.setLazy(user);
        user.putAuthData("facebook", new HashMap<String, String>());
        // To verify handleSaveResultAsync is not called
        user.setPassword("password");
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").isNew(false).build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class))).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        // Register a mock currentUserController to make getCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Enable LDS
        Parse.enableLocalDatastore(null);
        ParseTaskUtils.wait(user.resolveLazinessAsync(Task.<Void>forResult(null)));
        ArgumentCaptor<ParseUser> userAfterResolveLazinessCaptor = ArgumentCaptor.forClass(ParseUser.class);
        // Make sure we logIn the lazy user
        Mockito.verify(userController, Mockito.times(1)).logInAsync(ArgumentMatchers.any(ParseUser.State.class), ArgumentMatchers.any(ParseOperationSet.class));
        // Make sure handleSaveResultAsync() is not called, if handleSaveResultAsync is called, password
        // field should be cleaned
        Assert.assertEquals("password", user.getPassword());
        // Make sure we do not save currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(userAfterResolveLazinessCaptor.capture());
        ParseUser userAfterResolveLaziness = userAfterResolveLazinessCaptor.getValue();
        // Make sure userAfterResolveLaziness's data is correct
        Assert.assertEquals("newSessionToken", userAfterResolveLaziness.getSessionToken());
        Assert.assertEquals("newValue", userAfterResolveLaziness.get("newKey"));
        // Make sure userAfterResolveLaziness is not lazy
        Assert.assertFalse(userAfterResolveLaziness.isLazy());
        // Make sure we create new user
        Assert.assertNotSame(user, userAfterResolveLaziness);
    }

    // TODO(mengyan): Add testValidateSaveWithIsAuthenticatedWithNotDirty
    // TODO(mengyan): Add testValidateSaveWithIsAuthenticatedWithIsCurrentUser
    @Test
    public void testValidateSaveWithNoObjectId() {
        ParseUser user = new ParseUser();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot save a ParseUser until it has been signed up. Call signUp first.");
        user.validateSave();
    }

    @Test
    public void testValidateSaveWithLDSNotEnabled() {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.setObjectId("test");
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        // Make isDirty return true
        user.put("key", "value");
        // Make isCurrent return false
        user.setIsCurrentUser(false);
        user.validateSave();
    }

    // endregion
    // region testSaveAsync
    @Test
    public void testValidateSaveWithLDSNotEnabledAndCurrentUserNotMatch() {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.setObjectId("testAgain");
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        // Make isDirty return true
        user.put("key", "value");
        // Make isCurrent return false
        user.setIsCurrentUser(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot save a ParseUser that is not authenticated.");
        user.validateSave();
    }

    @Test
    public void testSaveAsyncWithLazyAndCurrentUser() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Set facebook authData to null to verify cleanAuthData()
        ParseUser.State userState = new ParseUser.State.Builder().putAuthData("facebook", null).build();
        ParseUser user = ParseObject.from(userState);
        ParseUserTest.setLazy(user);
        user.setIsCurrentUser(true);
        ParseUser partialMockUser = Mockito.spy(user);
        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockUser).resolveLazinessAsync(Matchers.<Task<Void>>any());
        ParseTaskUtils.wait(partialMockUser.saveAsync("sessionToken", Task.<Void>forResult(null)));
        // Make sure we clean authData
        Assert.assertFalse(partialMockUser.getAuthData().containsKey("facebook"));
        // Make sure we save new currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(partialMockUser);
    }

    // TODO(mengyan): Add testSaveAsyncWithNotLazyAndNotCurrentUser, right now we can not mock
    // super.save()
    // endregion
    // region testLogOutAsync
    @Test
    public void testSaveAsyncWithLazyAndNotCurrentUser() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Set facebook authData to null to verify cleanAuthData()
        ParseUser.State userState = new ParseUser.State.Builder().putAuthData("facebook", null).build();
        ParseUser user = ParseObject.from(userState);
        ParseUserTest.setLazy(user);
        user.setIsCurrentUser(false);
        ParseUser partialMockUser = Mockito.spy(user);
        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockUser).resolveLazinessAsync(Matchers.<Task<Void>>any());
        ParseTaskUtils.wait(partialMockUser.saveAsync("sessionToken", Task.<Void>forResult(null)));
        // Make sure we do not clean authData
        Assert.assertTrue(partialMockUser.getAuthData().containsKey("facebook"));
        // Make sure we do not save new currentUser
        Mockito.verify(currentUserController, Mockito.never()).setAsync(partialMockUser);
    }

    // endregion
    // region testEnable/UpgradeSessionToken
    @Test
    public void testLogOutAsync() throws Exception {
        // Register a mock sessionController to verify revokeAsync()
        NetworkSessionController sessionController = Mockito.mock(NetworkSessionController.class);
        Mockito.when(sessionController.revokeAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerSessionController(sessionController);
        ParseAuthenticationManager manager = Mockito.mock(ParseAuthenticationManager.class);
        Mockito.when(manager.deauthenticateAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerAuthenticationManager(manager);
        // Set user initial state
        String facebookAuthType = "facebook";
        Map<String, String> facebookAuthData = new HashMap<>();
        facebookAuthData.put("facebookToken", "facebookTest");
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").putAuthData(facebookAuthType, facebookAuthData).sessionToken("r:oldSessionToken").build();
        ParseUser user = ParseObject.from(userState);
        ParseTaskUtils.wait(user.logOutAsync());
        Mockito.verify(manager).deauthenticateAsync("facebook");
        // Verify we revoke session
        Mockito.verify(sessionController, Mockito.times(1)).revokeAsync("r:oldSessionToken");
    }

    @Test
    public void testEnableRevocableSessionInBackgroundWithCurrentUser() throws Exception {
        // Register a mock ParsePlugins to make restClient() work
        ParsePlugins mockPlugins = Mockito.mock(ParsePlugins.class);
        Mockito.when(mockPlugins.restClient()).thenReturn(null);
        ParsePlugins.set(mockPlugins);
        // Register a mock currentUserController to verify setAsync
        ParseUser mockUser = Mockito.mock(ParseUser.class);
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(mockUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseTaskUtils.wait(ParseUser.enableRevocableSessionInBackground());
        Mockito.verify(currentUserController, Mockito.times(1)).getAsync(false);
        Mockito.verify(mockUser, Mockito.times(1)).upgradeToRevocableSessionAsync();
    }

    @Test
    public void testEnableRevocableSessionInBackgroundWithNoCurrentUser() throws Exception {
        // Register a mock ParsePlugins to make restClient() work
        ParsePlugins mockPlugins = Mockito.mock(ParsePlugins.class);
        Mockito.when(mockPlugins.restClient()).thenReturn(null);
        ParsePlugins.set(mockPlugins);
        // Register a mock currentUserController to verify setAsync
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseTaskUtils.wait(ParseUser.enableRevocableSessionInBackground());
        Mockito.verify(currentUserController, Mockito.times(1)).getAsync(false);
    }

    @Test
    public void testUpgradeToRevocableSessionAsync() throws Exception {
        // Register a mock currentUserController to verify setAsync
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock sessionController to verify revokeAsync()
        NetworkSessionController sessionController = Mockito.mock(NetworkSessionController.class);
        ParseSession.State state = new ParseSession.State.Builder("_Session").put("sessionToken", "r:newSessionToken").build();
        Mockito.when(sessionController.upgradeToRevocable(ArgumentMatchers.anyString())).thenReturn(Task.forResult(state));
        ParseCorePlugins.getInstance().registerSessionController(sessionController);
        // Set user initial state
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").sessionToken("oldSessionToken").build();
        ParseUser user = ParseObject.from(userState);
        ParseTaskUtils.wait(user.upgradeToRevocableSessionAsync());
        // Make sure we update to new sessionToken
        Assert.assertEquals("r:newSessionToken", user.getSessionToken());
        // Make sure we update currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(user);
    }

    // endregion
    // region testUnlinkFromAsync
    @Test
    public void testDontOverwriteSessionTokenForCurrentUser() {
        ParseUser.State sessionTokenState = new ParseUser.State.Builder().sessionToken("sessionToken").put("key0", "value0").put("key1", "value1").isComplete(true).build();
        ParseUser.State newState = new ParseUser.State.Builder().put("key0", "newValue0").put("key2", "value2").isComplete(true).build();
        ParseUser.State emptyState = new ParseUser.State.Builder().isComplete(true).build();
        ParseUser user = ParseObject.from(sessionTokenState);
        user.setIsCurrentUser(true);
        Assert.assertEquals(user.getSessionToken(), "sessionToken");
        Assert.assertEquals(user.getString("key0"), "value0");
        Assert.assertEquals(user.getString("key1"), "value1");
        user.setState(newState);
        Assert.assertEquals(user.getSessionToken(), "sessionToken");
        Assert.assertEquals(user.getString("key0"), "newValue0");
        Assert.assertNull(user.getString("key1"));
        Assert.assertEquals(user.getString("key2"), "value2");
        user.setIsCurrentUser(false);
        user.setState(emptyState);
        Assert.assertNull(user.getSessionToken());
        Assert.assertNull(user.getString("key0"));
        Assert.assertNull(user.getString("key1"));
        Assert.assertNull(user.getString("key2"));
    }

    // endregion
    // region testLogin
    @Test
    public void testUnlinkFromAsyncWithAuthType() throws Exception {
        // Register a mock currentUserController to make getAsync work
        ParseUser mockUser = Mockito.mock(ParseUser.class);
        Mockito.when(mockUser.getSessionToken()).thenReturn("sessionToken");
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync()).thenReturn(Task.forResult(mockUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Set user initial state
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("facebookToken", "facebookTest");
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").putAuthData(authType, authData).build();
        ParseUser user = ParseObject.from(userState);
        ParseUser partialMockUser = Mockito.spy(user);
        Mockito.doReturn(Task.<Void>forResult(null)).when(partialMockUser).saveAsync(ArgumentMatchers.anyString(), Matchers.<Task<Void>>any());
        ParseTaskUtils.wait(partialMockUser.unlinkFromInBackground(authType));
        // Verify we delete authData
        Assert.assertNull(user.getAuthData().get("facebook"));
        // Verify we save the user
        Mockito.verify(partialMockUser, Mockito.times(1)).saveAsync(ArgumentMatchers.eq("sessionToken"), Matchers.<Task<Void>>any());
    }

    @Test
    public void testLogInInWithNoUserName() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must specify a username for the user to log in with");
        ParseTaskUtils.wait(ParseUser.logInInBackground(null, "password"));
    }

    @Test
    public void testLogInWithNoPassword() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must specify a password for the user to log in with");
        ParseTaskUtils.wait(ParseUser.logInInBackground("userName", null));
    }

    @Test
    public void testLogIn() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        ParseUser user = ParseUser.logIn("userName", "password");
        // Make sure user is login
        Mockito.verify(userController, Mockito.times(1)).logInAsync("userName", "password");
        // Make sure we set currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(user);
        // Make sure user's data is correct
        Assert.assertEquals("newSessionToken", user.getSessionToken());
        Assert.assertEquals("newValue", user.get("newKey"));
    }

    // endregion
    // region testBecome
    @Test
    public void testLogInWithCallback() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make logIn work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("newKey", "newValue").sessionToken("newSessionToken").build();
        Mockito.when(userController.logInAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        final Semaphore done = new Semaphore(0);
        ParseUser.logInInBackground("userName", "password", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                done.release();
                Assert.assertNull(e);
                // Make sure user's data is correct
                Assert.assertEquals("newSessionToken", user.getSessionToken());
                Assert.assertEquals("newValue", user.get("newKey"));
            }
        });
        Assert.assertTrue(done.tryAcquire(5, TimeUnit.SECONDS));
        // Make sure user is login
        Mockito.verify(userController, Mockito.times(1)).logInAsync("userName", "password");
        // Make sure we set currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(ArgumentMatchers.any(ParseUser.class));
    }

    @Test
    public void testBecomeWithNoSessionToken() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must specify a sessionToken for the user to log in with");
        ParseUser.become(null);
    }

    @Test
    public void testBecome() throws Exception {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make getUsreAsync work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("key", "value").sessionToken("sessionToken").build();
        Mockito.when(userController.getUserAsync(ArgumentMatchers.anyString())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        ParseUser user = ParseUser.become("sessionToken");
        // Make sure we call getUserAsync
        Mockito.verify(userController, Mockito.times(1)).getUserAsync("sessionToken");
        // Make sure we set currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(user);
        // Make sure user's data is correct
        Assert.assertEquals("sessionToken", user.getSessionToken());
        Assert.assertEquals("value", user.get("key"));
    }

    // endregion
    // region testToRest
    @Test
    public void testBecomeWithCallback() {
        // Register a mock currentUserController to make setCurrentUser work
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.setAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register a mock userController to make getUsreAsync work
        ParseUserController userController = Mockito.mock(ParseUserController.class);
        ParseUser.State newUserState = new ParseUser.State.Builder().put("key", "value").sessionToken("sessionToken").build();
        Mockito.when(userController.getUserAsync(ArgumentMatchers.anyString())).thenReturn(Task.forResult(newUserState));
        ParseCorePlugins.getInstance().registerUserController(userController);
        final Semaphore done = new Semaphore(0);
        ParseUser.becomeInBackground("sessionToken", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                done.release();
                Assert.assertNull(e);
                // Make sure user's data is correct
                Assert.assertEquals("sessionToken", user.getSessionToken());
                Assert.assertEquals("value", user.get("key"));
            }
        });
        // Make sure we call getUserAsync
        Mockito.verify(userController, Mockito.times(1)).getUserAsync("sessionToken");
        // Make sure we set currentUser
        Mockito.verify(currentUserController, Mockito.times(1)).setAsync(ArgumentMatchers.any(ParseUser.class));
    }

    // endregion
    // region testValidateDelete
    @Test
    public void testToRest() throws Exception {
        ParseUser user = new ParseUser();
        user.setUsername("userName");
        user.setPassword("password");
        JSONObject json = user.toRest(user.getState(), user.operationSetQueue, PointerEncoder.get());
        // Make sure we delete password operations
        Assert.assertFalse(json.getJSONArray("__operations").getJSONObject(0).has("password"));
        // Make sure we have username operations
        Assert.assertEquals("userName", json.getJSONArray("__operations").getJSONObject(0).getString("username"));
    }

    // endregion
    // region testValidateDelete
    @Test
    public void testValidDelete() {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.setObjectId("test");
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        // Make isDirty return true
        user.put("key", "value");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot delete a ParseUser that is not authenticated.");
        user.validateDelete();
    }

    // endregion
    // region testSynchronizeAuthData
    @Test
    public void testValidateSaveEventually() throws Exception {
        ParseUser user = new ParseUser();
        user.setPassword("password");
        thrown.expect(ParseException.class);
        thrown.expectMessage("Unable to saveEventually on a ParseUser with dirty password");
        user.validateSaveEventually();
    }

    @Test
    public void testSynchronizeAuthData() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register mock callbacks
        AuthenticationCallback callbacks = Mockito.mock(AuthenticationCallback.class);
        Mockito.when(callbacks.onRestore(Matchers.<Map<String, String>>any())).thenReturn(true);
        ParseUser.registerAuthenticationCallback("facebook", callbacks);
        // Set user initial state
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("facebookToken", "facebookTest");
        ParseUser.State userState = new ParseUser.State.Builder().putAuthData(authType, authData).build();
        ParseUser user = ParseObject.from(userState);
        user.setIsCurrentUser(true);
        ParseTaskUtils.wait(user.synchronizeAuthDataAsync(authType));
        // Make sure we restore authentication
        Mockito.verify(callbacks, Mockito.times(1)).onRestore(authData);
    }

    // endregion
    // region testAutomaticUser
    @Test
    public void testSynchronizeAllAuthData() throws Exception {
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        // Register mock callbacks
        AuthenticationCallback callbacks = Mockito.mock(AuthenticationCallback.class);
        Mockito.when(callbacks.onRestore(Matchers.<Map<String, String>>any())).thenReturn(true);
        ParseUser.registerAuthenticationCallback("facebook", callbacks);
        // Set user initial state
        String facebookAuthType = "facebook";
        Map<String, String> facebookAuthData = new HashMap<>();
        facebookAuthData.put("facebookToken", "facebookTest");
        ParseUser.State userState = new ParseUser.State.Builder().putAuthData(facebookAuthType, facebookAuthData).build();
        ParseUser user = ParseObject.from(userState);
        user.setIsCurrentUser(true);
        ParseTaskUtils.wait(user.synchronizeAllAuthDataAsync());
        // Make sure we restore authentication
        Mockito.verify(callbacks, Mockito.times(1)).onRestore(facebookAuthData);
    }

    // endregion
    // region testAutomaticUser
    @Test
    public void testAutomaticUser() {
        new ParseUser();
        ParseUser.disableAutomaticUser();
        Assert.assertFalse(ParseUser.isAutomaticUserEnabled());
        ParseUser.enableAutomaticUser();
        Assert.assertTrue(ParseUser.isAutomaticUserEnabled());
    }

    // endregion
    // region testPinCurrentUserIfNeededAsync
    @Test
    public void testPinCurrentUserIfNeededAsyncWithNoLDSEnabled() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Method requires Local Datastore.");
        ParseUser.pinCurrentUserIfNeededAsync(new ParseUser());
    }

    // endregion
    // region testRemove
    @Test
    public void testPinCurrentUserIfNeededAsync() {
        // Enable LDS
        Parse.enableLocalDatastore(null);
        // Register a mock currentUserController to make getCurrentUser work
        ParseUser currentUser = new ParseUser();
        currentUser.setObjectId("test");
        CachedCurrentUserController currentUserController = Mockito.mock(CachedCurrentUserController.class);
        Mockito.when(currentUserController.setIfNeededAsync(ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseUser user = new ParseUser();
        ParseUser.pinCurrentUserIfNeededAsync(user);
        // Make sure we pin the user
        Mockito.verify(currentUserController, Mockito.times(1)).setIfNeededAsync(user);
    }

    // endregion
    // region testSetState
    @Test
    public void testRemoveWithUserName() {
        ParseUser user = new ParseUser();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Can't remove the username key.");
        user.remove("username");
    }

    @Test
    public void testSetCurrentUserStateWithoutAuthData() {
        // Set user initial state
        String authType = "facebook";
        Map<String, String> authData = new HashMap<>();
        authData.put("facebookToken", "facebookTest");
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").put("oldKey", "oldValue").put("key", "value").putAuthData(authType, authData).build();
        ParseUser user = ParseObject.from(userState);
        user.setIsCurrentUser(true);
        // Build new state
        ParseUser.State newUserState = new ParseUser.State.Builder().objectId("testAgain").put("key", "valueAgain").build();
        user.setState(newUserState);
        // Make sure we keep the authData
        Assert.assertEquals(1, user.getAuthData().size());
        Assert.assertEquals(authData, user.getAuthData().get(authType));
        // Make sure old state is replaced
        Assert.assertFalse(user.has("oldKey"));
        // Make sure new state is set
        Assert.assertEquals("testAgain", user.getObjectId());
        Assert.assertEquals("valueAgain", user.get("key"));
    }

    // endregion
    @Test
    public void testSetStateDoesNotAddNonExistentAuthData() {
        // Set user initial state
        ParseUser.State userState = new ParseUser.State.Builder().objectId("test").put("oldKey", "oldValue").put("key", "value").build();
        ParseUser user = ParseObject.from(userState);
        user.setIsCurrentUser(true);
        // Build new state
        ParseUser.State newUserState = new ParseUser.State.Builder().objectId("testAgain").put("key", "valueAgain").build();
        user.setState(newUserState);
        // Make sure we do not add authData when it did not exist before
        Assert.assertFalse(user.keySet().contains("authData"));
        Assert.assertEquals(1, user.keySet().size());
        Assert.assertEquals(0, user.getAuthData().size());
        // Make sure old state is replaced
        Assert.assertFalse(user.has("oldKey"));
        // Make sure new state is set
        Assert.assertEquals("testAgain", user.getObjectId());
        Assert.assertEquals("valueAgain", user.get("key"));
    }

    // region testSaveEventuallyWhenServerError
    @Test
    public void testSaveEventuallyWhenServerError() throws Exception {
        Shadows.shadowOf(application).grantPermissions(ACCESS_NETWORK_STATE);
        Parse.Configuration configuration = new Parse.Configuration.Builder(RuntimeEnvironment.application).applicationId(APPLICATION_ID).server("https://api.parse.com/1").enableLocalDataStore().build();
        ParsePlugins plugins = ParseTestUtils.mockParsePlugins(configuration);
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("objectId", "objectId");
        mockResponse.put("email", "email@parse.com");
        mockResponse.put("username", "username");
        mockResponse.put("sessionToken", "r:sessionToken");
        mockResponse.put("createdAt", ParseDateFormat.getInstance().format(new Date(1000)));
        mockResponse.put("updatedAt", ParseDateFormat.getInstance().format(new Date(2000)));
        ParseHttpClient restClient = ParseTestUtils.mockParseHttpClientWithResponse(mockResponse, 200, "OK");
        Mockito.when(plugins.restClient()).thenReturn(restClient);
        Parse.initialize(configuration, plugins);
        ParseUser user = ParseUser.logIn("username", "password");
        Assert.assertFalse(user.isDirty());
        user.put("field", "data");
        Assert.assertTrue(user.isDirty());
        mockResponse = new JSONObject();
        mockResponse.put("updatedAt", ParseDateFormat.getInstance().format(new Date(3000)));
        ParseTestUtils.updateMockParseHttpClientWithResponse(restClient, mockResponse, 200, "OK");
        final CountDownLatch saveCountDown1 = new CountDownLatch(1);
        final Capture<Exception> exceptionCapture = new Capture();
        user.saveInBackground().continueWith(new bolts.Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) {
                exceptionCapture.set(task.getError());
                saveCountDown1.countDown();
                return null;
            }
        });
        Assert.assertTrue(saveCountDown1.await(5, TimeUnit.SECONDS));
        Assert.assertNull(exceptionCapture.get());
        Assert.assertFalse(user.isDirty());
        user.put("field", "other data");
        Assert.assertTrue(user.isDirty());
        mockResponse = new JSONObject();
        mockResponse.put("error", "Save is not allowed");
        mockResponse.put("code", 141);
        ParseTestUtils.updateMockParseHttpClientWithResponse(restClient, mockResponse, 400, "Bad Request");
        final CountDownLatch saveEventuallyCountDown = new CountDownLatch(1);
        user.saveEventually().continueWith(new bolts.Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) {
                exceptionCapture.set(task.getError());
                saveEventuallyCountDown.countDown();
                return null;
            }
        });
        Assert.assertTrue(saveEventuallyCountDown.await(5, TimeUnit.SECONDS));
        Assert.assertTrue(((exceptionCapture.get()) instanceof ParseException));
        Assert.assertEquals(SCRIPT_ERROR, getCode());
        Assert.assertEquals("Save is not allowed", exceptionCapture.get().getMessage());
        Assert.assertTrue(user.isDirty());
        // Simulate reboot
        Parse.destroy();
        Parse.initialize(configuration, plugins);
        user = ParseUser.getCurrentUser();
        Assert.assertTrue(user.isDirty());
        Assert.assertEquals("other data", user.get("field"));
        user.put("field", "another data");
        mockResponse = new JSONObject();
        mockResponse.put("updatedAt", ParseDateFormat.getInstance().format(new Date(4000)));
        ParseTestUtils.updateMockParseHttpClientWithResponse(restClient, mockResponse, 200, "OK");
        final CountDownLatch saveCountDown2 = new CountDownLatch(1);
        user.saveInBackground().continueWith(new bolts.Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) {
                exceptionCapture.set(task.getError());
                saveCountDown2.countDown();
                return null;
            }
        });
        Assert.assertTrue(saveCountDown2.await(5, TimeUnit.SECONDS));
        Assert.assertNull(exceptionCapture.get());
        Assert.assertFalse(user.isDirty());
    }
}

