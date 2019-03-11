/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.user;


import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.web.AbstractSecurityPage;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class EditUserPageTest extends AbstractUserPageTest {
    GeoServerUser current;

    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testReadOnlyUserGroupService() throws Exception {
        initializeForXML();
        doTestReadOnlyUserGroupService();
    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        doTestReadOnlyRoleService();
    }

    @Test
    public void testAllServicesReadOnly() throws Exception {
        insertValues();
        activateROUGService();
        activateRORoleService();
        AbstractSecurityPage returnPage = initializeForUGServiceNamed(getROUserGroupServiceName());
        current = ugService.getUserByUsername("user1");
        tester.startPage((page = ((AbstractUserPage) (new EditUserPage(getROUserGroupServiceName(), current).setReturnPage(returnPage)))));
        tester.assertRenderedPage(EditUserPage.class);
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:username").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:password").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:confirmPassword").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:enabled").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:roles").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:groups").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:properties").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:properties").isEnabled());
        // tester.assertInvisible("form:save");
    }

    @Test
    public void testPasswordsDontMatch() throws Exception {
        insertValues();
        current = ugService.getUserByUsername("user1");
        super.doTestPasswordsDontMatch(EditUserPage.class);
    }
}

