/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.ldap;


import java.util.SortedSet;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class LDAPUserGroupServiceTest extends LDAPBaseTest {
    GeoServerUserGroupService service;

    @Test
    public void testUsers() throws Exception {
        SortedSet<GeoServerUser> users = service.getUsers();
        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void testGroupByName() throws Exception {
        Assert.assertNotNull(service.getGroupByGroupname("extra"));
        Assert.assertNull(service.getGroupByGroupname("dummy"));
    }

    @Test
    public void testUserByName() throws Exception {
        GeoServerUser user = service.getUserByUsername("other");
        Assert.assertNotNull(user);
        Assert.assertEquals("other", user.getProperties().get("givenName"));
        Assert.assertEquals("dude", user.getProperties().get("sn"));
        Assert.assertEquals("2", user.getProperties().get("telephoneNumber"));
        Assert.assertNull(service.getUserByUsername("dummy"));
    }

    @Test
    public void testUsersForGroup() throws Exception {
        SortedSet<GeoServerUser> users = service.getUsersForGroup(service.getGroupByGroupname("other"));
        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
    }

    @Test
    public void testGroupsForUser() throws Exception {
        SortedSet<GeoServerUserGroup> groups = service.getGroupsForUser(service.getUserByUsername("other"));
        Assert.assertNotNull(groups);
        Assert.assertEquals(1, groups.size());
    }

    @Test
    public void testUserCount() throws Exception {
        Assert.assertEquals(3, service.getUserCount());
    }

    @Test
    public void testGroupCount() throws Exception {
        Assert.assertEquals(3, service.getGroupCount());
    }

    @Test
    public void testUsersHavingProperty() throws Exception {
        SortedSet<GeoServerUser> users = service.getUsersHavingProperty("mail");
        Assert.assertEquals(1, users.size());
        for (GeoServerUser user : users) {
            Assert.assertEquals("extra", user.getUsername());
        }
    }

    @Test
    public void testUsersNotHavingProperty() throws Exception {
        SortedSet<GeoServerUser> users = service.getUsersNotHavingProperty("telephoneNumber");
        Assert.assertEquals(1, users.size());
        for (GeoServerUser user : users) {
            Assert.assertEquals("extra", user.getUsername());
        }
    }

    @Test
    public void testCountUsersHavingProperty() throws Exception {
        Assert.assertEquals(1, service.getUserCountHavingProperty("mail"));
    }

    @Test
    public void testCountUsersNotHavingProperty() throws Exception {
        Assert.assertEquals(1, service.getUserCountNotHavingProperty("telephoneNumber"));
    }

    @Test
    public void testUsersHavingPropertyValue() throws Exception {
        SortedSet<GeoServerUser> users = service.getUsersHavingPropertyValue("telephoneNumber", "2");
        Assert.assertEquals(1, users.size());
        for (GeoServerUser user : users) {
            Assert.assertEquals("other", user.getUsername());
        }
    }

    @Test
    public void testUserCountHavingPropertyValue() throws Exception {
        Assert.assertEquals(1, service.getUserCountHavingPropertyValue("telephoneNumber", "2"));
    }
}

