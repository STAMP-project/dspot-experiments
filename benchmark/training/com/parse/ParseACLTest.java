/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseACL.CREATOR;
import android.os.Parcel;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseACLTest {
    private static final String UNRESOLVED_KEY = "*unresolved";

    private static final String READ_PERMISSION = "read";

    private static final String WRITE_PERMISSION = "write";

    @Test
    public void testConstructor() {
        ParseACL acl = new ParseACL();
        Assert.assertEquals(0, acl.getPermissionsById().size());
    }

    // endregion
    // region testCopy
    @Test
    public void testConstructorWithUser() {
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        ParseACL acl = new ParseACL(user);
        Assert.assertTrue(acl.getReadAccess("test"));
        Assert.assertTrue(acl.getWriteAccess("test"));
    }

    @Test
    public void testCopy() {
        ParseACL acl = new ParseACL();
        final ParseUser unresolvedUser = Mockito.mock(ParseUser.class);
        Mockito.when(unresolvedUser.isLazy()).thenReturn(true);
        // This will set unresolvedUser and permissionsById
        acl.setReadAccess(unresolvedUser, true);
        acl.setWriteAccess(unresolvedUser, true);
        // We need to reset unresolvedUser since registerSaveListener will be triggered once in
        // setReadAccess()
        Mockito.reset(unresolvedUser);
        ParseACL copiedACL = new ParseACL(acl);
        Assert.assertEquals(1, copiedACL.getPermissionsById().size());
        Assert.assertTrue(copiedACL.getPermissionsById().containsKey(ParseACLTest.UNRESOLVED_KEY));
        Assert.assertTrue(copiedACL.getReadAccess(unresolvedUser));
        Assert.assertTrue(copiedACL.getWriteAccess(unresolvedUser));
        Assert.assertFalse(copiedACL.isShared());
        Assert.assertSame(unresolvedUser, copiedACL.getUnresolvedUser());
        Mockito.verify(unresolvedUser, Mockito.times(1)).registerSaveListener(ArgumentMatchers.any(GetCallback.class));
    }

    // endregion
    // region toJson
    @Test
    public void testCopyWithSaveListener() {
        ParseACL acl = new ParseACL();
        final ParseUser unresolvedUser = Mockito.mock(ParseUser.class);
        Mockito.when(unresolvedUser.isLazy()).thenReturn(true);
        // This will set unresolvedUser and permissionsById
        acl.setReadAccess(unresolvedUser, true);
        acl.setWriteAccess(unresolvedUser, true);
        // We need to reset unresolvedUser since registerSaveListener will be triggered once in
        // setReadAccess()
        Mockito.reset(unresolvedUser);
        ParseACL copiedACL = new ParseACL(acl);
        // Make sure the callback is called
        ArgumentCaptor<GetCallback> callbackCaptor = ArgumentCaptor.forClass(GetCallback.class);
        Mockito.verify(unresolvedUser, Mockito.times(1)).registerSaveListener(callbackCaptor.capture());
        // Trigger the callback
        GetCallback callback = callbackCaptor.getValue();
        // Manually set userId and not lazy, mock user is saved
        Mockito.when(unresolvedUser.getObjectId()).thenReturn("userId");
        Mockito.when(unresolvedUser.isLazy()).thenReturn(false);
        callback.done(unresolvedUser, null);
        // Makre sure we unregister the callback
        Mockito.verify(unresolvedUser, Mockito.times(1)).unregisterSaveListener(ArgumentMatchers.any(GetCallback.class));
        Assert.assertEquals(1, copiedACL.getPermissionsById().size());
        Assert.assertTrue(copiedACL.getReadAccess(unresolvedUser));
        Assert.assertTrue(copiedACL.getWriteAccess(unresolvedUser));
        Assert.assertFalse(copiedACL.isShared());
        // No more unresolved permissions since it has been resolved in the callback.
        Assert.assertFalse(copiedACL.getPermissionsById().containsKey(ParseACLTest.UNRESOLVED_KEY));
        Assert.assertNull(copiedACL.getUnresolvedUser());
    }

    // endregion
    // region parcelable
    @Test
    public void testToJson() throws Exception {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        ParseUser unresolvedUser = new ParseUser();
        ParseACLTest.setLazy(unresolvedUser);
        acl.setReadAccess(unresolvedUser, true);
        // Mock decoder
        ParseEncoder mockEncoder = Mockito.mock(ParseEncoder.class);
        Mockito.when(mockEncoder.encode(ArgumentMatchers.eq(unresolvedUser))).thenReturn("unresolvedUserJson");
        JSONObject aclJson = acl.toJSONObject(mockEncoder);
        Assert.assertEquals("unresolvedUserJson", aclJson.getString("unresolvedUser"));
        Assert.assertEquals(aclJson.getJSONObject("userId").getBoolean("read"), true);
        Assert.assertEquals(aclJson.getJSONObject("userId").has("write"), false);
        Assert.assertEquals(aclJson.getJSONObject("*unresolved").getBoolean("read"), true);
        Assert.assertEquals(aclJson.getJSONObject("*unresolved").has("write"), false);
        Assert.assertEquals(aclJson.length(), 3);
    }

    @Test
    public void testParcelable() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        ParseUser user = new ParseUser();
        user.setObjectId("userId2");
        acl.setReadAccess(user, true);
        acl.setRoleWriteAccess("role", true);
        acl.setShared(true);
        Parcel parcel = Parcel.obtain();
        acl.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        acl = CREATOR.createFromParcel(parcel);
        Assert.assertTrue(acl.getReadAccess("userId"));
        Assert.assertTrue(acl.getReadAccess(user));
        Assert.assertTrue(acl.getRoleWriteAccess("role"));
        Assert.assertTrue(acl.isShared());
        Assert.assertFalse(acl.getPublicReadAccess());
        Assert.assertFalse(acl.getPublicWriteAccess());
    }

    // endregion
    // region testCreateACLFromJSONObject
    @Test
    public void testParcelableWithUnresolvedUser() {
        ParseFieldOperations.registerDefaultDecoders();// Needed for unparceling ParseObjects

        ParseACL acl = new ParseACL();
        ParseUser unresolved = new ParseUser();
        ParseACLTest.setLazy(unresolved);
        acl.setReadAccess(unresolved, true);
        // unresolved users need a local id when parcelling and unparcelling.
        // Since we don't have an Android environment, local id creation will fail.
        unresolved.localId = "localId";
        Parcel parcel = Parcel.obtain();
        acl.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        // Do not user ParseObjectParcelDecoder because it requires local ids
        acl = new ParseACL(parcel, new ParseParcelDecoder());
        Assert.assertTrue(acl.getReadAccess(unresolved));
    }

    // endregion
    // region testResolveUser
    @Test
    public void testCreateACLFromJSONObject() throws Exception {
        JSONObject aclJson = new JSONObject();
        JSONObject permission = new JSONObject();
        permission.put(ParseACLTest.READ_PERMISSION, true);
        permission.put(ParseACLTest.WRITE_PERMISSION, true);
        aclJson.put("userId", permission);
        ParseUser unresolvedUser = new ParseUser();
        JSONObject unresolvedUserJson = new JSONObject();
        aclJson.put("unresolvedUser", unresolvedUserJson);
        // Mock decoder
        ParseDecoder mockDecoder = Mockito.mock(ParseDecoder.class);
        Mockito.when(mockDecoder.decode(ArgumentMatchers.eq(unresolvedUserJson))).thenReturn(unresolvedUser);
        ParseACL acl = ParseACL.createACLFromJSONObject(aclJson, mockDecoder);
        Assert.assertSame(unresolvedUser, acl.getUnresolvedUser());
        Assert.assertTrue(acl.getReadAccess("userId"));
        Assert.assertTrue(acl.getWriteAccess("userId"));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test
    public void testResolveUserWithNewUser() {
        ParseUser unresolvedUser = new ParseUser();
        ParseACLTest.setLazy(unresolvedUser);
        ParseACL acl = new ParseACL();
        acl.setReadAccess(unresolvedUser, true);
        ParseUser other = new ParseUser();
        // local id creation fails if we don't have Android environment
        unresolvedUser.localId = "someId";
        other.localId = "someOtherId";
        acl.resolveUser(other);
        // Make sure unresolvedUser is not changed
        Assert.assertSame(unresolvedUser, acl.getUnresolvedUser());
    }

    // endregion
    // region testSetAccess
    @Test
    public void testResolveUserWithUnresolvedUser() {
        ParseACL acl = new ParseACL();
        ParseUser unresolvedUser = new ParseUser();
        ParseACLTest.setLazy(unresolvedUser);
        // This will set the unresolvedUser in acl
        acl.setReadAccess(unresolvedUser, true);
        acl.setWriteAccess(unresolvedUser, true);
        unresolvedUser.setObjectId("test");
        acl.resolveUser(unresolvedUser);
        Assert.assertNull(acl.getUnresolvedUser());
        Assert.assertTrue(acl.getReadAccess(unresolvedUser));
        Assert.assertTrue(acl.getWriteAccess(unresolvedUser));
        Assert.assertEquals(1, acl.getPermissionsById().size());
        Assert.assertFalse(acl.getPermissionsById().containsKey(ParseACLTest.UNRESOLVED_KEY));
    }

    @Test
    public void testSetAccessWithNoPermissionAndNotAllowed() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", false);
        // Make sure noting is set
        Assert.assertEquals(0, acl.getPermissionsById().size());
    }

    @Test
    public void testSetAccessWithAllowed() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        Assert.assertTrue(acl.getReadAccess("userId"));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test
    public void testSetAccessWithPermissionsAndNotAllowed() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        acl.setReadAccess("userId", false);
        // Make sure we remove the read access
        Assert.assertFalse(acl.getReadAccess("userId"));
        Assert.assertEquals(0, acl.getPermissionsById().size());
    }

    @Test
    public void testSetPublicReadAccessAllowed() {
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        Assert.assertTrue(acl.getPublicReadAccess());
    }

    @Test
    public void testSetPublicReadAccessNotAllowed() {
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(false);
        // Make sure noting is set
        Assert.assertEquals(0, acl.getPermissionsById().size());
    }

    @Test
    public void testSetPublicWriteAccessAllowed() {
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        Assert.assertTrue(acl.getPublicWriteAccess());
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test
    public void testSetPublicWriteAccessNotAllowed() {
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(false);
        // Make sure noting is set
        Assert.assertEquals(0, acl.getPermissionsById().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetReadAccessWithNullUserId() {
        ParseACL acl = new ParseACL();
        String userId = null;
        acl.setReadAccess(userId, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWriteAccessWithNullUserId() {
        ParseACL acl = new ParseACL();
        String userId = null;
        acl.setWriteAccess(userId, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRoleReadAccessWithInvalidRole() {
        ParseRole role = new ParseRole();
        role.setName("Player");
        ParseACL acl = new ParseACL();
        acl.setRoleReadAccess(role, true);
    }

    @Test
    public void testSetRoleReadAccess() {
        ParseRole role = new ParseRole();
        role.setName("Player");
        role.setObjectId("test");
        ParseACL acl = new ParseACL();
        acl.setRoleReadAccess(role, true);
        Assert.assertTrue(acl.getRoleReadAccess(role));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRoleWriteAccessWithInvalidRole() {
        ParseRole role = new ParseRole();
        role.setName("Player");
        ParseACL acl = new ParseACL();
        acl.setRoleWriteAccess(role, true);
    }

    @Test
    public void testSetRoleWriteAccess() {
        ParseRole role = new ParseRole();
        role.setName("Player");
        role.setObjectId("test");
        ParseACL acl = new ParseACL();
        acl.setRoleWriteAccess(role, true);
        Assert.assertTrue(acl.getRoleWriteAccess(role));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserReadAccessWithNotSavedNotLazyUser() {
        ParseUser user = new ParseUser();
        ParseACL acl = new ParseACL();
        acl.setReadAccess(user, true);
    }

    @Test
    public void testSetUserReadAccessWithLazyUser() {
        ParseUser unresolvedUser = Mockito.mock(ParseUser.class);
        Mockito.when(unresolvedUser.isLazy()).thenReturn(true);
        ParseACL acl = new ParseACL();
        acl.setReadAccess(unresolvedUser, true);
        Assert.assertSame(unresolvedUser, acl.getUnresolvedUser());
        Mockito.verify(unresolvedUser, Mockito.times(1)).registerSaveListener(ArgumentMatchers.any(GetCallback.class));
        Assert.assertTrue(acl.getPermissionsById().containsKey(ParseACLTest.UNRESOLVED_KEY));
        Assert.assertTrue(acl.getReadAccess(unresolvedUser));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test
    public void testSetUserReadAccessWithNormalUser() {
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        ParseACL acl = new ParseACL();
        acl.setReadAccess(user, true);
        Assert.assertTrue(acl.getReadAccess(user));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserWriteAccessWithNotSavedNotLazyUser() {
        ParseUser user = new ParseUser();
        ParseACL acl = new ParseACL();
        acl.setWriteAccess(user, true);
    }

    @Test
    public void testSetUserWriteAccessWithLazyUser() {
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.isLazy()).thenReturn(true);
        ParseACL acl = new ParseACL();
        acl.setWriteAccess(user, true);
        Assert.assertSame(user, acl.getUnresolvedUser());
        Mockito.verify(user, Mockito.times(1)).registerSaveListener(ArgumentMatchers.any(GetCallback.class));
        Assert.assertTrue(acl.getPermissionsById().containsKey(ParseACLTest.UNRESOLVED_KEY));
        Assert.assertTrue(acl.getWriteAccess(user));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    // endregion
    // region testGetAccess
    @Test
    public void testSetUserWriteAccessWithNormalUser() {
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        ParseACL acl = new ParseACL();
        acl.setWriteAccess(user, true);
        Assert.assertTrue(acl.getWriteAccess(user));
        Assert.assertEquals(1, acl.getPermissionsById().size());
    }

    @Test
    public void testGetAccessWithNoPermission() {
        ParseACL acl = new ParseACL();
        Assert.assertFalse(acl.getReadAccess("userId"));
    }

    @Test
    public void testGetAccessWithNoAccessType() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        Assert.assertFalse(acl.getWriteAccess("userId"));
    }

    @Test
    public void testGetAccessWithPermission() {
        ParseACL acl = new ParseACL();
        acl.setReadAccess("userId", true);
        Assert.assertTrue(acl.getReadAccess("userId"));
    }

    @Test
    public void testGetPublicReadAccess() {
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        Assert.assertTrue(acl.getPublicWriteAccess());
    }

    @Test
    public void testGetPublicWriteAccess() {
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        Assert.assertTrue(acl.getPublicWriteAccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReadAccessWithNullUserId() {
        ParseACL acl = new ParseACL();
        String userId = null;
        acl.getReadAccess(userId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWriteAccessWithNullUserId() {
        ParseACL acl = new ParseACL();
        String userId = null;
        acl.getWriteAccess(userId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRoleReadAccessWithInvalidRole() {
        ParseACL acl = new ParseACL();
        ParseRole role = new ParseRole();
        role.setName("Player");
        acl.getRoleReadAccess(role);
    }

    @Test
    public void testGetRoleReadAccess() {
        ParseACL acl = new ParseACL();
        ParseRole role = new ParseRole();
        role.setName("Player");
        role.setObjectId("test");
        acl.setRoleReadAccess(role, true);
        Assert.assertTrue(acl.getRoleReadAccess(role));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRoleWriteAccessWithInvalidRole() {
        ParseACL acl = new ParseACL();
        ParseRole role = new ParseRole();
        role.setName("Player");
        acl.getRoleWriteAccess(role);
    }

    @Test
    public void testGetRoleWriteAccess() {
        ParseACL acl = new ParseACL();
        ParseRole role = new ParseRole();
        role.setName("Player");
        role.setObjectId("test");
        acl.setRoleWriteAccess(role, true);
        Assert.assertTrue(acl.getRoleWriteAccess(role));
    }

    @Test
    public void testGetUserReadAccessWithUnresolvedUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        ParseACLTest.setLazy(user);
        // Since user is a lazy user, this will set the acl's unresolved user and give it read access
        acl.setReadAccess(user, true);
        Assert.assertTrue(acl.getReadAccess(user));
    }

    @Test
    public void testGetUserReadAccessWithLazyUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        ParseACLTest.setLazy(user);
        Assert.assertFalse(acl.getReadAccess(user));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserReadAccessWithNotSavedUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        Assert.assertFalse(acl.getReadAccess(user));
    }

    @Test
    public void testGetUserReadAccessWithNormalUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        acl.setReadAccess(user, true);
        Assert.assertTrue(acl.getReadAccess(user));
    }

    @Test
    public void testGetUserWriteAccessWithUnresolvedUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        ParseACLTest.setLazy(user);
        // Since user is a lazy user, this will set the acl's unresolved user and give it write access
        acl.setWriteAccess(user, true);
        Assert.assertTrue(acl.getWriteAccess(user));
    }

    @Test
    public void testGetUserWriteAccessWithLazyUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.isLazy()).thenReturn(true);
        Assert.assertFalse(acl.getWriteAccess(user));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserWriteAccessWithNotSavedUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        Assert.assertFalse(acl.getWriteAccess(user));
    }

    // endregion
    // region testGetter/Setter
    @Test
    public void testGetUserWriteAccessWithNormalUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        user.setObjectId("test");
        acl.setWriteAccess(user, true);
        Assert.assertTrue(acl.getWriteAccess(user));
    }

    @Test
    public void testIsShared() {
        ParseACL acl = new ParseACL();
        acl.setShared(true);
        Assert.assertTrue(acl.isShared());
    }

    // endregion
    @Test
    public void testUnresolvedUser() {
        ParseACL acl = new ParseACL();
        ParseUser user = new ParseUser();
        ParseACLTest.setLazy(user);
        // This will set unresolvedUser in acl
        acl.setReadAccess(user, true);
        Assert.assertTrue(acl.hasUnresolvedUser());
        Assert.assertSame(user, acl.getUnresolvedUser());
    }
}

