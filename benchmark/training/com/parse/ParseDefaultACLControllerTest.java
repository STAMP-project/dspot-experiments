/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.Task;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


// endregion
public class ParseDefaultACLControllerTest {
    // region testSetDefaultACL
    @Test
    public void testSetDefaultACLWithACL() {
        ParseACL acl = Mockito.mock(ParseACL.class);
        ParseACL copiedACL = Mockito.mock(ParseACL.class);
        Mockito.when(acl.copy()).thenReturn(copiedACL);
        ParseDefaultACLController controller = new ParseDefaultACLController();
        controller.set(acl, true);
        Assert.assertNull(controller.defaultACLWithCurrentUser);
        Assert.assertNull(controller.lastCurrentUser);
        Assert.assertTrue(controller.defaultACLUsesCurrentUser);
        Mockito.verify(copiedACL, Mockito.times(1)).setShared(true);
        Assert.assertEquals(copiedACL, controller.defaultACL);
    }

    @Test
    public void testSetDefaultACLWithNull() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        controller.set(null, true);
        Assert.assertNull(controller.defaultACLWithCurrentUser);
        Assert.assertNull(controller.lastCurrentUser);
        Assert.assertNull(controller.defaultACL);
    }

    // endregion
    // region testGetDefaultACL
    @Test
    public void testGetDefaultACLWithNoDefaultACL() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        ParseACL defaultACL = controller.get();
        Assert.assertNull(defaultACL);
    }

    @Test
    public void testGetDefaultACLWithNoDefaultACLUsesCurrentUser() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        ParseACL acl = new ParseACL();
        controller.defaultACL = acl;
        controller.defaultACLUsesCurrentUser = false;
        ParseACL defaultACL = controller.get();
        Assert.assertSame(acl, defaultACL);
    }

    @Test
    public void testGetDefaultACLWithNoCurrentUser() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        ParseACL acl = new ParseACL();
        controller.defaultACL = acl;
        controller.defaultACLUsesCurrentUser = true;
        // Register currentUser
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.<ParseUser>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseACL defaultACL = controller.get();
        Assert.assertSame(acl, defaultACL);
    }

    @Test
    public void testGetDefaultACLWithSameCurrentUserAndLastCurrentUser() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        ParseACL acl = new ParseACL();
        controller.defaultACL = acl;
        controller.defaultACLUsesCurrentUser = true;
        ParseACL aclAgain = new ParseACL();
        controller.defaultACLWithCurrentUser = aclAgain;
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        controller.lastCurrentUser = new java.lang.ref.WeakReference(currentUser);
        // Register currentUser
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseACL defaultACL = controller.get();
        Assert.assertNotSame(acl, defaultACL);
        Assert.assertSame(aclAgain, defaultACL);
    }

    @Test
    public void testGetDefaultACLWithCurrentUserAndLastCurrentUserNotSame() {
        ParseDefaultACLController controller = new ParseDefaultACLController();
        ParseACL acl = Mockito.mock(ParseACL.class);
        ParseACL copiedACL = Mockito.mock(ParseACL.class);
        Mockito.when(acl.copy()).thenReturn(copiedACL);
        controller.defaultACL = acl;
        controller.defaultACLUsesCurrentUser = true;
        controller.defaultACLWithCurrentUser = new ParseACL();
        // Register currentUser
        ParseCurrentUserController currentUserController = Mockito.mock(ParseCurrentUserController.class);
        ParseUser currentUser = Mockito.mock(ParseUser.class);
        Mockito.when(currentUserController.getAsync(ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(currentUser));
        ParseCorePlugins.getInstance().registerCurrentUserController(currentUserController);
        ParseACL defaultACL = controller.get();
        Mockito.verify(copiedACL, Mockito.times(1)).setShared(true);
        Mockito.verify(copiedACL, Mockito.times(1)).setReadAccess(ArgumentMatchers.eq(currentUser), ArgumentMatchers.eq(true));
        Mockito.verify(copiedACL, Mockito.times(1)).setWriteAccess(ArgumentMatchers.eq(currentUser), ArgumentMatchers.eq(true));
        Assert.assertSame(currentUser, controller.lastCurrentUser.get());
        Assert.assertNotSame(acl, defaultACL);
        Assert.assertSame(copiedACL, defaultACL);
    }
}

