/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.user;


import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.web.AbstractConfirmRemovalPanelTest;
import org.junit.Test;


public class ConfirmRemovalUserPanelTest extends AbstractConfirmRemovalPanelTest<GeoServerUser> {
    private static final long serialVersionUID = 1L;

    protected boolean disassociateRoles = false;

    @Test
    public void testRemoveUser() throws Exception {
        disassociateRoles = false;
        removeObject();
    }

    @Test
    public void testRemoveUserWithRoles() throws Exception {
        disassociateRoles = true;
        removeObject();
    }
}

