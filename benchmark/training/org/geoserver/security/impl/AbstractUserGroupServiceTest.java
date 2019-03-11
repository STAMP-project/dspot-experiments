/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import org.geoserver.security.AbstractSecurityServiceTest;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.GeoServerUserGroupStore;
import org.geoserver.security.config.SecurityUserGroupServiceConfig;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractUserGroupServiceTest extends AbstractSecurityServiceTest {
    protected GeoServerUserGroupService service;

    protected GeoServerUserGroupStore store;

    @Test
    public void testInsert() throws Exception {
        // all is empty
        checkEmpty(service);
        checkEmpty(store);
        // transaction has values ?
        insertValues(store);
        if (!(isJDBCTest()))
            checkEmpty(service);

        checkValuesInserted(store);
        // rollback
        store.load();
        checkEmpty(store);
        checkEmpty(service);
        // commit
        insertValues(store);
        store.store();
        checkValuesInserted(store);
        checkValuesInserted(service);
    }

    @Test
    public void testModify() throws Exception {
        // all is empty
        checkEmpty(service);
        checkEmpty(store);
        insertValues(store);
        store.store();
        checkValuesInserted(store);
        checkValuesInserted(service);
        modifyValues(store);
        if (!(isJDBCTest()))
            checkValuesInserted(service);

        checkValuesModified(store);
        store.load();
        checkValuesInserted(store);
        checkValuesInserted(service);
        modifyValues(store);
        store.store();
        checkValuesModified(store);
        checkValuesModified(service);
    }

    @Test
    public void testRemove() throws Exception {
        // all is empty
        checkEmpty(service);
        checkEmpty(store);
        insertValues(store);
        store.store();
        checkValuesInserted(store);
        checkValuesInserted(service);
        removeValues(store);
        if (!(isJDBCTest()))
            checkValuesInserted(service);

        checkValuesRemoved(store);
        store.load();
        checkValuesInserted(store);
        checkValuesInserted(service);
        removeValues(store);
        store.store();
        checkValuesRemoved(store);
        checkValuesRemoved(service);
    }

    @Test
    public void testIsModified() throws Exception {
        Assert.assertFalse(store.isModified());
        insertValues(store);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        insertValues(store);
        store.store();
        Assert.assertFalse(store.isModified());
        GeoServerUser user = store.createUserObject("uuuu", "", true);
        GeoServerUserGroup group = store.createGroupObject("gggg", true);
        Assert.assertFalse(store.isModified());
        // add,remove,update
        store.addUser(user);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.addGroup(group);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.updateUser(user);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.updateGroup(group);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.removeUser(user);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.removeGroup(group);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.associateUserToGroup(user, group);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.disAssociateUserFromGroup(user, group);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.clear();
        Assert.assertTrue(store.isModified());
        store.load();
    }

    @Test
    public void testEmptyPassword() throws Exception {
        // all is empty
        checkEmpty(service);
        checkEmpty(store);
        GeoServerUser user = store.createUserObject("userNoPasswd", null, true);
        store.addUser(user);
        store.store();
        Assert.assertEquals(1, service.getUserCount());
        user = service.getUserByUsername("userNoPasswd");
        Assert.assertNull(user.getPassword());
        user = ((GeoServerUser) (service.loadUserByUsername("userNoPasswd")));
        Assert.assertNull(user.getPassword());
    }

    @Test
    public void testEraseCredentials() throws Exception {
        GeoServerUser user = store.createUserObject("user", "foobar", true);
        store.addUser(user);
        store.store();
        user = store.getUserByUsername("user");
        Assert.assertNotNull(user.getPassword());
        user.eraseCredentials();
        user = store.getUserByUsername("user");
        Assert.assertNotNull(user.getPassword());
    }

    @Test
    public void testPasswordRecoding() throws Exception {
        SecurityUserGroupServiceConfig config = getSecurityManager().loadUserGroupServiceConfig(service.getName());
        config.setPasswordEncoderName(getPlainTextPasswordEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        store = service.createStore();
        store.addUser(store.createUserObject("u1", "p1", true));
        store.addUser(store.createUserObject("u2", "p2", true));
        store.store();
        Util.recodePasswords(service.createStore());
        // no recoding
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getPlainTextPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getPlainTextPasswordEncoder().getPrefix()));
        config.setPasswordEncoderName(getPBEPasswordEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        Util.recodePasswords(service.createStore());
        // recoding
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getPBEPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getPBEPasswordEncoder().getPrefix()));
        config.setPasswordEncoderName(getDigestPasswordEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        Util.recodePasswords(service.createStore());
        // recoding
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        config.setPasswordEncoderName(getPBEPasswordEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        Util.recodePasswords(service.createStore());
        // recoding has no effect
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        // add a user with pbe encoding
        store = service.createStore();
        store.addUser(store.createUserObject("u3", "p3", true));
        store.store();
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u3").getPassword().startsWith(getPBEPasswordEncoder().getPrefix()));
        config.setPasswordEncoderName(getEmptyEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        Util.recodePasswords(service.createStore());
        // recode u3 to empty
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u3").getPassword().startsWith(getEmptyEncoder().getPrefix()));
        config.setPasswordEncoderName(getPBEPasswordEncoder().getName());
        getSecurityManager().saveUserGroupService(config);
        service.initializeFromConfig(config);
        Util.recodePasswords(service.createStore());
        // recode has no effect
        Assert.assertTrue(service.loadUserByUsername("u1").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u2").getPassword().startsWith(getDigestPasswordEncoder().getPrefix()));
        Assert.assertTrue(service.loadUserByUsername("u3").getPassword().startsWith(getEmptyEncoder().getPrefix()));
    }
}

