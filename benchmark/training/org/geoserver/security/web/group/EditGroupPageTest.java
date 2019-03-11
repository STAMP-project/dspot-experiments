/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.group;


import org.geoserver.security.web.AbstractSecurityPage;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class EditGroupPageTest extends AbstractSecurityWicketTestSupport {
    EditGroupPage page;

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

    @Test
    public void testAllServicesReadOnly() throws Exception {
        activateROUGService();
        activateRORoleService();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getROUserGroupServiceName());
        tester.startPage((page = ((EditGroupPage) (new EditGroupPage(getROUserGroupServiceName(), ugService.getGroupByGroupname("group1")).setReturnPage(returnPage)))));
        tester.assertRenderedPage(EditGroupPage.class);
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:groupname").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:enabled").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:roles").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:save").isEnabled());
    }
}

