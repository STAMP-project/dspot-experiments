/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.data;


import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.web.AbstractConfirmRemovalPanelTest;
import org.junit.Test;


public class ConfirmRemovalDataAccessRulePanelTest extends AbstractConfirmRemovalPanelTest<DataAccessRule> {
    private static final long serialVersionUID = 1L;

    @Test
    public void testRemoveRule() throws Exception {
        initializeForXML();
        removeObject();
    }
}

