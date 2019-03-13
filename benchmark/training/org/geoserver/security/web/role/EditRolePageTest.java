/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.role;


import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.web.AbstractSecurityPage;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class EditRolePageTest extends AbstractSecurityWicketTestSupport {
    EditRolePage page;

    @Test
    public void testFill() throws Exception {
        doTestFill();
    }

    @Test
    public void testFill2() throws Exception {
        doTestFill2();
    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        // doInitialize();
        activateRORoleService();
        AbstractSecurityPage returnPage = initializeForRoleServiceNamed(getRORoleServiceName());
        tester.startPage((page = ((EditRolePage) (new EditRolePage(getRORoleServiceName(), GeoServerRole.ADMIN_ROLE).setReturnPage(returnPage)))));
        tester.assertRenderedPage(EditRolePage.class);
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:name").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:properties").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("form:parent").isEnabled());
        tester.assertInvisible("form:save");
    }
}

