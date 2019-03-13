/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.jdbc.user;


import org.geoserver.security.web.user.EditUserPageTest;
import org.junit.Test;


public class JDBCEditUserPageTest extends EditUserPageTest {
    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testReadOnlyUserGroupService() throws Exception {
        doTestReadOnlyUserGroupService();
    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        doTestReadOnlyRoleService();
    }
}

