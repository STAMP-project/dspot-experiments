/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import org.geoserver.security.AbstractSecurityServiceTest;
import org.geoserver.security.GeoServerRoleService;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerJ2eeRoleServiceTest extends AbstractSecurityServiceTest {
    @Test
    public void testNoRoles() throws Exception {
        copyWebXML("web1.xml");
        GeoServerRoleService service = getSecurityManager().loadRoleService("test1");
        checkEmpty(service);
    }

    @Test
    public void testRoles() throws Exception {
        copyWebXML("web2.xml");
        GeoServerRoleService service = getSecurityManager().loadRoleService("test2");
        Assert.assertEquals(4, service.getRoleCount());
        Assert.assertTrue(service.getRoles().contains(new GeoServerRole("role1")));
        Assert.assertTrue(service.getRoles().contains(new GeoServerRole("role2")));
        Assert.assertTrue(service.getRoles().contains(new GeoServerRole("employee")));
        Assert.assertTrue(service.getRoles().contains(new GeoServerRole("MGR")));
    }
}

