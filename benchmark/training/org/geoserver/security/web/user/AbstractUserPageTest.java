/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.user;


import org.apache.wicket.util.tester.FormTester;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractUserPageTest extends AbstractSecurityWicketTestSupport {
    protected AbstractUserPage page;

    protected FormTester form;

    @Test
    public void testReadOnlyRoleService() throws Exception {
        doInitialize();
        activateRORoleService();
        initializeTester();
        Assert.assertTrue(page.userGroupPalette.isEnabled());
    }
}

