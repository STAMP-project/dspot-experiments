/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.jdbc.role;


import org.geoserver.security.web.role.RoleListPageTest;
import org.junit.Test;


public class JDBCRoleListPageTest extends RoleListPageTest {
    @Test
    public void testRemove() throws Exception {
        // insertValues();
        addAdditonalData();
        doRemove(((getTabbedPanelPath()) + ":panel:header:removeSelected"));
    }
}

