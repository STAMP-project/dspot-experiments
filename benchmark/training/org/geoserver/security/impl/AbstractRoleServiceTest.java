/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import org.geoserver.security.AbstractSecurityServiceTest;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerRoleStore;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractRoleServiceTest extends AbstractSecurityServiceTest {
    protected GeoServerRoleService service;

    protected GeoServerRoleStore store;

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
        GeoServerRole role = store.createRoleObject("ROLE_DUMMY");
        GeoServerRole role_parent = store.createRoleObject("ROLE_PARENT");
        Assert.assertFalse(store.isModified());
        // add,remove,update
        store.addRole(role);
        store.addRole(role_parent);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.updateRole(role);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.removeRole(role);
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.associateRoleToGroup(role, "agroup");
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.disAssociateRoleFromGroup(role, "agroup");
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.associateRoleToUser(role, "auser");
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.disAssociateRoleFromUser(role, "auser");
        Assert.assertTrue(store.isModified());
        store.load();
        Assert.assertFalse(store.isModified());
        store.setParentRole(role, role_parent);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.setParentRole(role, null);
        Assert.assertTrue(store.isModified());
        store.store();
        Assert.assertFalse(store.isModified());
        store.clear();
        Assert.assertTrue(store.isModified());
        store.load();
    }

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
}

