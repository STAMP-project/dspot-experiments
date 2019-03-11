/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.user;


import FeedbackMessage.ERROR;
import org.geoserver.security.web.AbstractSecurityPage;
import org.junit.Assert;
import org.junit.Test;


public class NewUserPageTest extends AbstractUserPageTest {
    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testFill3() throws Exception {
        doTestFill3();
    }

    @Test
    public void testFill2() throws Exception {
        // initializeForXML();
        doTestFill2();
    }

    @Test
    public void testUserNameConflict() throws Exception {
        insertValues();
        initializeTester();
        tester.assertRenderedPage(NewUserPage.class);
        newFormTester();
        form.setValue("username", "user1");
        form.setValue("password", "pwd");
        form.setValue("confirmPassword", "pwd");
        form.submit("save");
        Assert.assertTrue(testErrorMessagesWithRegExp(".*user1.*"));
        tester.getMessages(ERROR);
        tester.assertRenderedPage(NewUserPage.class);
    }

    @Test
    public void testInvalidWorkflow() throws Exception {
        activateROUGService();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getROUserGroupServiceName());
        boolean fail = true;
        try {
            tester.startPage((page = ((AbstractUserPage) (new NewUserPage(getROUserGroupServiceName()).setReturnPage(returnPage)))));
        } catch (RuntimeException ex) {
            fail = false;
        }
        if (fail)
            Assert.fail("No runtime exception for read only UserGroupService");

    }

    @Test
    public void testPasswordsDontMatch() throws Exception {
        super.doTestPasswordsDontMatch(NewUserPage.class);
    }
}

