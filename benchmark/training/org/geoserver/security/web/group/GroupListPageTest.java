/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.group;


import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.security.web.AbstractTabbedListPageTest;
import org.junit.Test;


public class GroupListPageTest extends AbstractTabbedListPageTest<GeoServerUserGroup> {
    protected boolean withRoles = false;

    @Test
    public void testReadOnlyService() throws Exception {
        // initializeForXML();
        tester.startPage(listPage(getUserGroupServiceName()));
        tester.assertVisible(getRemoveLink().getPageRelativePath());
        tester.assertVisible(getRemoveLinkWithRoles().getPageRelativePath());
        tester.assertVisible(getAddLink().getPageRelativePath());
        activateRORoleService();
        tester.startPage(listPage(getUserGroupServiceName()));
        tester.assertVisible(getRemoveLink().getPageRelativePath());
        tester.assertInvisible(getRemoveLinkWithRoles().getPageRelativePath());
        tester.assertVisible(getAddLink().getPageRelativePath());
        activateROUGService();
        tester.startPage(listPage(getROUserGroupServiceName()));
        tester.assertInvisible(getRemoveLink().getPageRelativePath());
        tester.assertInvisible(getAddLink().getPageRelativePath());
        tester.assertInvisible(getRemoveLinkWithRoles().getPageRelativePath());
    }

    @Test
    public void testRemoveWithRoles() throws Exception {
        withRoles = true;
        // initializeForXML();
        // insertValues();
        addAdditonalData();
        doRemove(((getTabbedPanelPath()) + ":panel:header:removeSelectedWithRoles"));
    }
}

