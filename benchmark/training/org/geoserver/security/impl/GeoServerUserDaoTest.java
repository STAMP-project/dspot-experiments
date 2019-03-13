/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.geoserver.security.PropertyFileWatcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class GeoServerUserDaoTest {
    static class TestableUserDao extends GeoServerUserDao {
        public TestableUserDao(Properties p) throws IOException {
            userMap = loadUsersFromProperties(p);
        }

        @Override
        void checkUserMap() throws DataAccessResourceFailureException {
            // do nothing, for this test we don't write on the fs by default
        }

        void loadUserMap() {
            super.checkUserMap();
        }
    }

    Properties props;

    GeoServerUserDaoTest.TestableUserDao dao;

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = getUsers();
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void testLoadUser() throws Exception {
        UserDetails admin = loadUserByUsername("admin");
        Assert.assertEquals("admin", admin.getUsername());
        Assert.assertEquals("gs", admin.getPassword());
        Assert.assertEquals(1, admin.getAuthorities().size());
        Assert.assertEquals("ROLE_ADMINISTRATOR", admin.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testMissingUser() throws Exception {
        try {
            dao.loadUserByUsername("notThere");
            Assert.fail("This user should not be there");
        } catch (Exception e) {
            // ok
        }
    }

    @Test
    public void testSetUser() throws Exception {
        setUser(new User("wfs", "pwd", true, true, true, true, Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("ROLE_WFS_ALL"), new SimpleGrantedAuthority("ROLE_WMS_ALL") })));
        UserDetails user = loadUserByUsername("wfs");
        Assert.assertEquals("wfs", user.getUsername());
        Assert.assertEquals("pwd", user.getPassword());
        Assert.assertEquals(2, user.getAuthorities().size());
        Set<String> authorities = new HashSet<String>();
        for (GrantedAuthority ga : user.getAuthorities()) {
            authorities.add(ga.getAuthority());
        }
        // order independent
        Assert.assertTrue(authorities.contains("ROLE_WFS_ALL"));
        Assert.assertTrue(authorities.contains("ROLE_WMS_ALL"));
    }

    @Test
    public void testSetMissingUser() throws Exception {
        try {
            setUser(new User("notther", "pwd", true, true, true, true, Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("ROLE_WFS_ALL") })));
            Assert.fail("The user is not there, setUser should fail");
        } catch (IllegalArgumentException e) {
            // cool
        }
    }

    @Test
    public void testAddUser() throws Exception {
        putUser(new User("newuser", "pwd", true, true, true, true, Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("ROLE_WFS_ALL") })));
        Assert.assertNotNull(dao.loadUserByUsername("newuser"));
    }

    @Test
    public void addExistingUser() throws Exception {
        try {
            putUser(new User("admin", "pwd", true, true, true, true, Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("ROLE_WFS_ALL") })));
            Assert.fail("The user is already there, addUser should fail");
        } catch (IllegalArgumentException e) {
            // cool
        }
    }

    @Test
    public void testRemoveUser() throws Exception {
        Assert.assertFalse(removeUser("notthere"));
        Assert.assertTrue(removeUser("wfs"));
        try {
            dao.loadUserByUsername("wfs");
            Assert.fail("The user is not there, loadUserByName should fail");
        } catch (UsernameNotFoundException e) {
            // cool
        }
    }

    @Test
    public void testStoreReload() throws Exception {
        File temp = File.createTempFile("sectest", "", new File("target"));
        temp.delete();
        temp.mkdir();
        File propFile = new File(temp, "users.properties");
        try {
            dao.userDefinitionsFile = new PropertyFileWatcher(propFile);
            storeUsers();
            dao.userMap.clear();
            dao.loadUserMap();
        } finally {
            temp.delete();
        }
        Assert.assertEquals(3, dao.getUsers().size());
        testLoadUser();
    }
}

