/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.validation;


import java.io.IOException;
import java.util.logging.Logger;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.test.SystemTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class UserGroupStoreValidationWrapperTest extends GeoServerSecurityTestSupport {
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    @Test
    public void testUserGroupStoreWrapper() throws Exception {
        boolean failed;
        UserGroupStoreValidationWrapper store = createStore("test");
        failed = false;
        try {
            store.addUser(store.createUserObject("", "", true));
        } catch (IOException ex) {
            assertSecurityException(ex, USERNAME_REQUIRED);
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.addGroup(store.createGroupObject(null, true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUPNAME_REQUIRED);
            failed = true;
        }
        Assert.assertTrue(failed);
        store.addUser(store.createUserObject("user1", "abc", true));
        store.addGroup(store.createGroupObject("group1", true));
        Assert.assertEquals(1, store.getUsers().size());
        Assert.assertEquals(1, store.getUserCount());
        Assert.assertEquals(1, store.getUserGroups().size());
        Assert.assertEquals(1, store.getGroupCount());
        failed = false;
        try {
            store.addUser(store.createUserObject("user1", "abc", true));
        } catch (IOException ex) {
            assertSecurityException(ex, USER_ALREADY_EXISTS_$1, "user1");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.addGroup(store.createGroupObject("group1", true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUP_ALREADY_EXISTS_$1, "group1");
            failed = true;
        }
        Assert.assertTrue(failed);
        store.updateUser(store.createUserObject("user1", "abc", false));
        store.updateGroup(store.createGroupObject("group1", false));
        failed = false;
        try {
            store.updateUser(store.createUserObject("user1xxxx", "abc", true));
        } catch (IOException ex) {
            assertSecurityException(ex, USER_NOT_FOUND_$1, "user1xxxx");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.updateGroup(store.createGroupObject("group1xxx", true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUP_NOT_FOUND_$1, "group1xxx");
            failed = true;
        }
        Assert.assertTrue(failed);
        GeoServerUser user1 = store.getUserByUsername("user1");
        GeoServerUserGroup group1 = store.getGroupByGroupname("group1");
        failed = false;
        try {
            store.associateUserToGroup(store.createUserObject("xxx", "abc", true), group1);
        } catch (IOException ex) {
            assertSecurityException(ex, USER_NOT_FOUND_$1, "xxx");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.associateUserToGroup(user1, store.createGroupObject("yyy", true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUP_NOT_FOUND_$1, "yyy");
            failed = true;
        }
        Assert.assertTrue(failed);
        store.associateUserToGroup(user1, group1);
        Assert.assertEquals(1, store.getUsersForGroup(group1).size());
        Assert.assertEquals(1, store.getGroupsForUser(user1).size());
        failed = false;
        try {
            store.getGroupsForUser(store.createUserObject("xxx", "abc", true));
        } catch (IOException ex) {
            assertSecurityException(ex, USER_NOT_FOUND_$1, "xxx");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.getUsersForGroup(store.createGroupObject("yyy", true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUP_NOT_FOUND_$1, "yyy");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.disAssociateUserFromGroup(store.createUserObject("xxx", "abc", true), group1);
        } catch (IOException ex) {
            assertSecurityException(ex, USER_NOT_FOUND_$1, "xxx");
            failed = true;
        }
        Assert.assertTrue(failed);
        failed = false;
        try {
            store.disAssociateUserFromGroup(user1, store.createGroupObject("yyy", true));
        } catch (IOException ex) {
            assertSecurityException(ex, GROUP_NOT_FOUND_$1, "yyy");
            failed = true;
        }
        Assert.assertTrue(failed);
        store.disAssociateUserFromGroup(user1, group1);
        store.removeUser(user1);
        store.removeGroup(group1);
    }
}

