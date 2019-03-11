/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.group;


import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.security.web.AbstractConfirmRemovalPanelTest;
import org.junit.Test;


public class ConfirmRemovalGroupPanelTest extends AbstractConfirmRemovalPanelTest<GeoServerUserGroup> {
    private static final long serialVersionUID = 1L;

    protected boolean disassociateRoles = false;

    @Test
    public void testRemoveGroup() throws Exception {
        disassociateRoles = false;
        // initializeForXML();
        removeObject();
    }

    @Test
    public void testRemoveGroupWithRoles() throws Exception {
        disassociateRoles = true;
        // initializeForXML();
        removeObject();
    }
}

