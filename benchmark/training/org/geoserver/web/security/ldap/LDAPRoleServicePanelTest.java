/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.security.ldap;


import java.io.Serializable;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.geoserver.security.ldap.LDAPRoleServiceConfig;
import org.geoserver.security.ldap.LDAPTestUtils;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 */
public class LDAPRoleServicePanelTest extends AbstractSecurityWicketTestSupport {
    private static final String GROUPS_BASE = "ou=Groups";

    private static final String GROUP_SEARCH_FILTER = "member=cn={0}";

    private static final String AUTH_USER = "admin";

    private static final String AUTH_PASSWORD = "secret";

    LDAPRoleServicePanel current;

    String relBase = "panel:";

    String base = "form:" + (relBase);

    LDAPRoleServiceConfig config;

    FeedbackPanel feedbackPanel = null;

    private static final String ldapServerUrl = LDAPTestUtils.LDAP_SERVER_URL;

    private static final String basePath = LDAPTestUtils.LDAP_BASE_PATH;

    @Test
    public void testDataLoadedFromConfigurationWithoutAuthentication() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPRoleServicePanelTest.ldapServerUrl, LDAPRoleServicePanelTest.basePath));
        setupPanel(false, true);
        checkBaseConfig();
        Assert.assertNull(tester.getComponentFromLastRenderedPage("form:panel:authenticationPanelContainer:authenticationPanel:user"));
        Assert.assertNull(tester.getComponentFromLastRenderedPage("form:panel:authenticationPanelContainer:authenticationPanel:password"));
    }

    @Test
    public void testRequiredFields() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPRoleServicePanelTest.ldapServerUrl, LDAPRoleServicePanelTest.basePath));
        setupPanel(false, false);
        tester.newFormTester("form").submit();
        tester.assertErrorMessages(((Serializable[]) (new String[]{ "Field 'Server URL' is required.", "Field 'Group search base' is required." })));
    }

    @Test
    public void testDataLoadedFromConfigurationWithAuthentication() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPRoleServicePanelTest.ldapServerUrl, LDAPRoleServicePanelTest.basePath));
        setupPanel(true, true);
        checkBaseConfig();
        tester.assertModelValue("form:panel:authenticationPanelContainer:authenticationPanel:user", LDAPRoleServicePanelTest.AUTH_USER);
        tester.assertModelValue("form:panel:authenticationPanelContainer:authenticationPanel:password", LDAPRoleServicePanelTest.AUTH_PASSWORD);
    }

    @Test
    public void testAuthenticationDisabled() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPRoleServicePanelTest.ldapServerUrl, LDAPRoleServicePanelTest.basePath));
        setupPanel(false, true);
        tester.assertInvisible("form:panel:authenticationPanelContainer:authenticationPanel");
        tester.newFormTester("form").setValue("panel:bindBeforeGroupSearch", "on");
        tester.executeAjaxEvent("form:panel:bindBeforeGroupSearch", "click");
        tester.assertVisible("form:panel:authenticationPanelContainer:authenticationPanel");
    }

    @Test
    public void testAuthenticationEnabled() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPRoleServicePanelTest.ldapServerUrl, LDAPRoleServicePanelTest.basePath));
        setupPanel(true, true);
        tester.assertVisible("form:panel:authenticationPanelContainer:authenticationPanel");
        tester.newFormTester("form").setValue("panel:bindBeforeGroupSearch", "");
        tester.executeAjaxEvent("form:panel:bindBeforeGroupSearch", "click");
        tester.assertInvisible("form:panel:authenticationPanelContainer:authenticationPanel");
    }
}

