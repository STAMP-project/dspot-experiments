/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.role;


import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.web.AbstractConfirmRemovalPanelTest;
import org.junit.Test;


public class ConfirmRemovalRolePanelTest extends AbstractConfirmRemovalPanelTest<GeoServerRole> {
    private static final long serialVersionUID = 1L;

    @Test
    public void testRemoveRole() throws Exception {
        initializeForXML();
        removeObject();
    }
}

