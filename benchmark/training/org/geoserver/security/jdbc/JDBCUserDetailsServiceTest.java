/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.jdbc;


import org.geoserver.security.impl.AbstractUserDetailsServiceTest;
import org.junit.Assert;
import org.junit.Test;


public abstract class JDBCUserDetailsServiceTest extends AbstractUserDetailsServiceTest {
    @Test
    public void testConfiguration() throws Exception {
        setServices("config");
        Assert.assertEquals(roleService, getSecurityManager().getActiveRoleService());
        // assertEquals(usergroupService,getSecurityManager().getActiveUserGroupService());
        Assert.assertEquals(usergroupService.getName(), getSecurityManager().loadUserGroupService(getFixtureId()).getName());
        Assert.assertTrue(roleService.canCreateStore());
        Assert.assertTrue(usergroupService.canCreateStore());
    }
}

