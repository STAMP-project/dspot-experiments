/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.security.ldap;


import java.io.Serializable;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.geoserver.security.ldap.LDAPTestUtils;
import org.geoserver.security.ldap.LDAPUserGroupServiceConfig;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Niels Charlier
 */
public class LDAPUserGroupServicePanelTest extends AbstractSecurityWicketTestSupport {
    private static final String GROUPS_BASE = "ou=Groups";

    private static final String USERS_BASE = "ou=People";

    private static final String GROUP_SEARCH_FILTER = "member=cn={0}";

    private static final String AUTH_USER = "admin";

    private static final String AUTH_PASSWORD = "secret";

    LDAPUserGroupServicePanel current;

    String relBase = "panel:";

    String base = "form:" + (relBase);

    LDAPUserGroupServiceConfig config;

    FeedbackPanel feedbackPanel = null;

    private static final String ldapServerUrl = LDAPTestUtils.LDAP_SERVER_URL;

    private static final String basePath = LDAPTestUtils.LDAP_BASE_PATH;

    @Test
    public void testDataLoadedFromConfigurationWithoutAuthentication() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPUserGroupServicePanelTest.ldapServerUrl, LDAPUserGroupServicePanelTest.basePath));
        setupPanel(false, true);
        checkBaseConfig();
        Assert.assertNull(tester.getComponentFromLastRenderedPage("form:panel:authenticationPanel:user"));
        Assert.assertNull(tester.getComponentFromLastRenderedPage("form:panel:authenticationPanel:password"));
    }

    @Test
    public void testRequiredFields() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPUserGroupServicePanelTest.ldapServerUrl, LDAPUserGroupServicePanelTest.basePath));
        setupPanel(false, false);
        tester.newFormTester("form").submit();
        tester.assertErrorMessages(((Serializable[]) (new String[]{ "Field 'Server URL' is required.", "Field 'Group search base' is required.", "Field 'User search base' is required." })));
    }

    @Test
    public void testDataLoadedFromConfigurationWithAuthentication() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPUserGroupServicePanelTest.ldapServerUrl, LDAPUserGroupServicePanelTest.basePath));
        setupPanel(true, true);
        checkBaseConfig();
        tester.assertModelValue("form:panel:authenticationPanel:user", LDAPUserGroupServicePanelTest.AUTH_USER);
        tester.assertModelValue("form:panel:authenticationPanel:password", LDAPUserGroupServicePanelTest.AUTH_PASSWORD);
    }

    @Test
    public void testAuthenticationDisabled() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPUserGroupServicePanelTest.ldapServerUrl, LDAPUserGroupServicePanelTest.basePath));
        setupPanel(false, true);
        tester.assertInvisible("form:panel:authenticationPanel");
        tester.newFormTester("form").setValue("panel:bindBeforeGroupSearch", "on");
        tester.executeAjaxEvent("form:panel:bindBeforeGroupSearch", "click");
        tester.assertVisible("form:panel:authenticationPanel");
    }

    @Test
    public void testAuthenticationEnabled() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPUserGroupServicePanelTest.ldapServerUrl, LDAPUserGroupServicePanelTest.basePath));
        setupPanel(true, true);
        tester.assertVisible("form:panel:authenticationPanel");
        tester.newFormTester("form").setValue("panel:bindBeforeGroupSearch", "");
        tester.executeAjaxEvent("form:panel:bindBeforeGroupSearch", "click");
        tester.assertInvisible("form:panel:authenticationPanelContainer:authenticationPanel");
    }
}

