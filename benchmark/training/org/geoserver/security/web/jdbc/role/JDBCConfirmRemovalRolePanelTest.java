/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.jdbc.role;


import org.geoserver.security.web.role.ConfirmRemovalRolePanelTest;
import org.junit.Test;


public class JDBCConfirmRemovalRolePanelTest extends ConfirmRemovalRolePanelTest {
    private static final long serialVersionUID = -7197515540318374854L;

    @Test
    public void testRemoveRole() throws Exception {
        initializeForJDBC();
        removeObject();
    }
}

