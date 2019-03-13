/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.group;


import FeedbackMessage.ERROR;
import java.util.SortedSet;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.security.web.AbstractSecurityPage;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class NewGroupPageTest extends AbstractSecurityWicketTestSupport {
    NewGroupPage page;

    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testGroupNameConflict() throws Exception {
        insertValues();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getUserGroupServiceName());
        tester.startPage((page = ((NewGroupPage) (new NewGroupPage(getUserGroupServiceName()).setReturnPage(returnPage)))));
        FormTester form = tester.newFormTester("form");
        form.setValue("groupname", "group1");
        form.submit("save");
        Assert.assertTrue(testErrorMessagesWithRegExp(".*group1.*"));
        tester.getMessages(ERROR);
        tester.assertRenderedPage(NewGroupPage.class);
    }

    @Test
    public void testInvalidWorkflow() throws Exception {
        activateROUGService();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getROUserGroupServiceName());
        boolean fail = true;
        try {
            tester.startPage((page = ((NewGroupPage) (new NewGroupPage(getROUserGroupServiceName()).setReturnPage(returnPage)))));
        } catch (RuntimeException ex) {
            fail = false;
        }
        if (fail)
            Assert.fail("No runtime exception for read only UserGroupService");

    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        activateRORoleService();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getUserGroupServiceName());
        tester.startPage((page = ((NewGroupPage) (new NewGroupPage(getUserGroupServiceName()).setReturnPage(returnPage)))));
        Assert.assertFalse(page.rolePalette.isEnabled());
        FormTester form = tester.newFormTester("form");
        form.setValue("groupname", "testgroup");
        form.submit("save");
        GeoServerUserGroup group = ugService.getGroupByGroupname("testgroup");
        Assert.assertNotNull(group);
        Assert.assertTrue(group.isEnabled());
        SortedSet<GeoServerRole> roleList = gaService.getRolesForGroup("testgroup");
        Assert.assertEquals(0, roleList.size());
    }
}

