/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.jdbc.role;


import org.geoserver.security.web.role.EditRolePageTest;
import org.junit.Test;


public class JDBCEditRolePageTest extends EditRolePageTest {
    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testFill2() throws Exception {
        doTestFill2();
    }
}

