/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.role;


import FeedbackMessage.ERROR;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.security.web.AbstractSecurityPage;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class NewRolePageTest extends AbstractSecurityWicketTestSupport {
    NewRolePage page;

    @Test
    public void testFill() throws Exception {
        // initializeForXML();
        doTestFill();
    }

    @Test
    public void testRoleNameConflict() throws Exception {
        insertValues();
        AbstractSecurityPage returnPage = initializeForRoleServiceNamed(getRoleServiceName());
        tester.startPage((page = ((NewRolePage) (new NewRolePage(getRoleServiceName()).setReturnPage(returnPage)))));
        FormTester form = tester.newFormTester("form");
        form.setValue("name", "ROLE_WFS");
        form.submit("save");
        Assert.assertTrue(testErrorMessagesWithRegExp(".*ROLE_WFS.*"));
        tester.getMessages(ERROR);
        tester.assertRenderedPage(NewRolePage.class);
    }

    @Test
    public void testInvalidWorkflow() throws Exception {
        activateRORoleService();
        AbstractSecurityPage returnPage = initializeForRoleServiceNamed(getRORoleServiceName());
        boolean fail = true;
        try {
            tester.startPage((page = ((NewRolePage) (new NewRolePage(getRORoleServiceName()).setReturnPage(returnPage)))));
        } catch (RuntimeException ex) {
            fail = false;
        }
        if (fail)
            Assert.fail("No runtime exception for read only RoleService");

    }
}

