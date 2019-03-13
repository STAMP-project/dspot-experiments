/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseAnonymousUtils.AUTH_TYPE;
import bolts.Task;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ParseAnonymousUtils.AUTH_TYPE;


// endregion
@SuppressWarnings("unchecked")
public class CachedCurrentUserControllerTest extends ResetPluginsParseTest {
    private static final String KEY_AUTH_DATA = "authData";

    // region testSetAsync
    @Test
    public void testSetAsyncWithOldInMemoryCurrentUser() throws Exception {
        // Mock currentUser in memory
        ParseUser oldCurrentUser = Mockito.mock(ParseUser.class);
        Mockito.when(oldCurrentUser.logOutAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<Void>forResult(null));
        ParseUser.State state = new ParseUser.State.Builder().put("key", "value").build();
        ParseUser currentUser = ParseObject.from(state);
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.setAsync(currentUser)).thenReturn(Task.<Void>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        controller.currentUser = oldCurrentUser;
        ParseTaskUtils.wait(controller.setAsync(currentUser));
        // Make sure oldCurrentUser logout
        Mockito.verify(oldCurrentUser, Mockito.times(1)).logOutAsync(false);
        // Verify it was persisted
        Mockito.verify(store, Mockito.times(1)).setAsync(currentUser);
        // TODO(mengyan): Find a way to verify user.synchronizeAllAuthData() is called
        // Verify newUser is currentUser
        Assert.assertTrue(currentUser.isCurrentUser());
        // Make sure in memory currentUser is up to date
        Assert.assertSame(currentUser, controller.currentUser);
        Assert.assertTrue(controller.currentUserMatchesDisk);
    }

    @Test
    public void testSetAsyncWithNoInMemoryCurrentUser() throws Exception {
        ParseUser.State state = new ParseUser.State.Builder().put("key", "value").build();
        ParseUser currentUser = ParseObject.from(state);
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.setAsync(currentUser)).thenReturn(Task.<Void>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseTaskUtils.wait(controller.setAsync(currentUser));
        // Verify it was persisted
        Mockito.verify(store, Mockito.times(1)).setAsync(currentUser);
        // TODO(mengyan): Find a way to verify user.synchronizeAllAuthData() is called
        // Verify newUser is currentUser
        Assert.assertTrue(currentUser.isCurrentUser());
        // Make sure in memory currentUser is up to date
        Assert.assertSame(currentUser, controller.currentUser);
        Assert.assertTrue(controller.currentUserMatchesDisk);
    }

    @Test
    public void testSetAsyncWithPersistFailure() throws Exception {
        // Mock currentUser in memory
        ParseUser oldCurrentUser = Mockito.mock(ParseUser.class);
        Mockito.when(oldCurrentUser.logOutAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<Void>forResult(null));
        ParseUser currentUser = new ParseUser();
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.setAsync(currentUser)).thenReturn(Task.<Void>forError(new RuntimeException("failure")));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        controller.currentUser = oldCurrentUser;
        ParseTaskUtils.wait(controller.setAsync(currentUser));
        // Make sure oldCurrentUser logout
        Mockito.verify(oldCurrentUser, Mockito.times(1)).logOutAsync(false);
        // Verify we tried to persist
        Mockito.verify(store, Mockito.times(1)).setAsync(currentUser);
        // TODO(mengyan): Find a way to verify user.synchronizeAllAuthData() is called
        // Verify newUser is currentUser
        Assert.assertTrue(currentUser.isCurrentUser());
        // Make sure in memory currentUser is up to date
        Assert.assertSame(currentUser, controller.currentUser);
        // Make sure in currentUserMatchesDisk since we can not write to disk
        Assert.assertFalse(controller.currentUserMatchesDisk);
    }

    // endregion
    // region testGetAsync
    @Test
    public void testGetAsyncWithInMemoryCurrentUserSet() throws Exception {
        ParseUser currentUserInMemory = new ParseUser();
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        controller.currentUser = currentUserInMemory;
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertSame(currentUserInMemory, currentUser);
    }

    @Test
    public void testGetAsyncWithNoInMemoryCurrentUserAndLazyLogin() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseCorePlugins.getInstance().registerCurrentUserController(controller);
        // CurrentUser is null but currentUserMatchesDisk is true happens when a user logout
        controller.currentUserMatchesDisk = true;
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(true));
        // We need to make sure the user is created by lazy login
        Assert.assertTrue(currentUser.isLazy());
        Assert.assertTrue(currentUser.isCurrentUser());
        Assert.assertSame(controller.currentUser, currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
        // We do not test the lazy login auth data here, it is covered in lazyLogin() unit test
    }

    @Test
    public void testGetAsyncWithNoInMemoryAndInDiskCurrentUserAndNoLazyLogin() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        // CurrentUser is null but currentUserMatchesDisk is true happens when a user logout
        controller.currentUserMatchesDisk = true;
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertNull(currentUser);
    }

    @Test
    public void testGetAsyncWithCurrentUserReadFromDiskSuccess() throws Exception {
        ParseUser.State state = new ParseUser.State.Builder().put("key", "value").build();
        ParseUser currentUserInDisk = ParseObject.from(state);
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.forResult(currentUserInDisk));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertSame(currentUser, currentUserInDisk);
        Assert.assertSame(currentUser, controller.currentUser);
        Assert.assertTrue(controller.currentUserMatchesDisk);
        Assert.assertTrue(currentUser.isCurrentUser());
        Assert.assertEquals("value", currentUser.get("key"));
    }

    @Test
    public void testGetAsyncAnonymousUser() throws Exception {
        ParseUser.State state = new ParseUser.State.Builder().objectId("fake").putAuthData(AUTH_TYPE, new HashMap<String, String>()).build();
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.forResult(ParseObject.<ParseUser>from(state)));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseUser user = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertFalse(user.isLazy());
    }

    @Test
    public void testGetAsyncLazyAnonymousUser() throws Exception {
        ParseUser.State state = new ParseUser.State.Builder().putAuthData(AUTH_TYPE, new HashMap<String, String>()).build();
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.forResult(ParseObject.<ParseUser>from(state)));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseUser user = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertTrue(user.isLazy());
    }

    @Test
    public void testGetAsyncWithCurrentUserReadFromDiskFailure() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forError(new RuntimeException("failure")));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(false));
        Assert.assertNull(currentUser);
    }

    @Test
    public void testGetAsyncWithCurrentUserReadFromDiskFailureAndLazyLogin() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forError(new RuntimeException("failure")));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseUser currentUser = ParseTaskUtils.wait(controller.getAsync(true));
        // We need to make sure the user is created by lazy login
        Assert.assertTrue(currentUser.isLazy());
        Assert.assertTrue(currentUser.isCurrentUser());
        Assert.assertSame(controller.currentUser, currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
        // We do not test the lazy login auth data here, it is covered in lazyLogin() unit test
    }

    // endregion
    // region testLogoOutAsync
    @Test
    public void testLogOutAsyncWithDeleteInDiskCurrentUserSuccess() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        // We set the currentUser to make sure getAsync() return a mock user
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        Mockito.when(currentUser.logOutAsync()).thenReturn(Task.<Void>forResult(null));
        controller.currentUser = currentUser;
        ParseTaskUtils.wait(controller.logOutAsync());
        // Make sure currentUser.logout() is called
        Mockito.verify(currentUser, Mockito.times(1)).logOutAsync();
        // Make sure in disk currentUser is deleted
        Mockito.verify(store, Mockito.times(1)).deleteAsync();
        // Make sure controller state is correct
        Assert.assertNull(controller.currentUser);
        Assert.assertTrue(controller.currentUserMatchesDisk);
    }

    @Test
    public void testLogOutAsyncWithDeleteInDiskCurrentUserFailure() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forResult(null));
        Mockito.when(store.deleteAsync()).thenReturn(Task.<Void>forError(new RuntimeException("failure")));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        ParseTaskUtils.wait(controller.logOutAsync());
        // Make sure controller state is correct
        Assert.assertNull(controller.currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
    }

    // endregion
    // region testLazyLogin
    @Test
    public void testLazyLogin() {
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        String authType = AUTH_TYPE;
        Map<String, String> authData = new HashMap<>();
        authData.put("sessionToken", "testSessionToken");
        ParseUser user = controller.lazyLogIn(authType, authData);
        // Make sure use is generated through lazyLogin
        Assert.assertTrue(user.isLazy());
        Assert.assertTrue(user.isCurrentUser());
        Map<String, Map<String, String>> authPair = user.getMap(CachedCurrentUserControllerTest.KEY_AUTH_DATA);
        Assert.assertEquals(1, authPair.size());
        Map<String, String> authDataAgain = authPair.get(authType);
        Assert.assertEquals(1, authDataAgain.size());
        Assert.assertEquals("testSessionToken", authDataAgain.get("sessionToken"));
        // Make sure controller state is correct
        Assert.assertSame(user, controller.currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
    }

    // endregion
    // region testGetCurrentSessionTokenAsync
    @Test
    public void testGetCurrentSessionTokenAsyncWithCurrentUserSet() throws Exception {
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        // We set the currentUser to make sure getAsync() return a mock user
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        Mockito.when(currentUser.getSessionToken()).thenReturn("sessionToken");
        controller.currentUser = currentUser;
        String sessionToken = ParseTaskUtils.wait(controller.getCurrentSessionTokenAsync());
        Assert.assertEquals("sessionToken", sessionToken);
    }

    @Test
    public void testGetCurrentSessionTokenAsyncWithNoCurrentUserSet() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseUser>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        String sessionToken = ParseTaskUtils.wait(controller.getCurrentSessionTokenAsync());
        Assert.assertNull(sessionToken);
    }

    // endregion
    // region testClearFromMemory
    @Test
    public void testClearFromMemory() {
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        controller.currentUser = Mockito.mock(ParseUser.class);
        controller.clearFromMemory();
        Assert.assertNull(controller.currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
    }

    // endregion
    // region testClearFromDisk()
    @Test
    public void testClearFromDisk() {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        controller.currentUser = new ParseUser();
        controller.clearFromDisk();
        Assert.assertNull(controller.currentUser);
        Assert.assertFalse(controller.currentUserMatchesDisk);
        Mockito.verify(store, Mockito.times(1)).deleteAsync();
    }

    // endregion
    // region testExistsAsync()
    @Test
    public void testExistsAsyncWithInMemoryCurrentUserSet() throws Exception {
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        controller.currentUser = new ParseUser();
        Assert.assertTrue(ParseTaskUtils.wait(controller.existsAsync()));
    }

    @Test
    public void testExistsAsyncWithInDiskCurrentUserSet() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.existsAsync()).thenReturn(Task.forResult(true));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        Assert.assertTrue(ParseTaskUtils.wait(controller.existsAsync()));
    }

    @Test
    public void testExistsAsyncWithNoInMemoryAndInDiskCurrentUserSet() throws Exception {
        ParseObjectStore<ParseUser> store = ((ParseObjectStore<ParseUser>) (Mockito.mock(ParseObjectStore.class)));
        Mockito.when(store.existsAsync()).thenReturn(Task.forResult(false));
        CachedCurrentUserController controller = new CachedCurrentUserController(store);
        Assert.assertFalse(ParseTaskUtils.wait(controller.existsAsync()));
    }

    // endregion
    // region testIsCurrent
    @Test
    public void testIsCurrent() {
        CachedCurrentUserController controller = new CachedCurrentUserController(null);
        ParseUser currentUser = new ParseUser();
        controller.currentUser = currentUser;
        Assert.assertTrue(controller.isCurrent(currentUser));
        Assert.assertFalse(controller.isCurrent(new ParseUser()));
    }
}

