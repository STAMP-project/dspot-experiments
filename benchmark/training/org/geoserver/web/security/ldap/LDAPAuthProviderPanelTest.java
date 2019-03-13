/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.security.ldap;


import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.geoserver.security.ldap.LDAPSecurityServiceConfig;
import org.geoserver.security.ldap.LDAPTestUtils;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 */
public class LDAPAuthProviderPanelTest extends AbstractSecurityWicketTestSupport {
    private static final String USER_FORMAT = "uid={0},ou=People,dc=example,dc=com";

    private static final String USER_FILTER = "(telephonenumber=1)";

    private static final String USER_DN_PATTERN = "uid={0},ou=People";

    LDAPAuthProviderPanel current;

    String relBase = "panel:";

    String base = "form:" + (relBase);

    LDAPSecurityServiceConfig config;

    FeedbackPanel feedbackPanel = null;

    private static final String ldapServerUrl = LDAPTestUtils.LDAP_SERVER_URL;

    private static final String basePath = LDAPTestUtils.LDAP_BASE_PATH;

    @Test
    public void testTestConnectionWithDnLookup() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPAuthProviderPanelTest.ldapServerUrl, LDAPAuthProviderPanelTest.basePath));
        setupPanel(LDAPAuthProviderPanelTest.USER_DN_PATTERN, null, null, null);
        testSuccessfulConnection();
    }

    @Test
    public void testTestConnectionWitUserGroupService() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPAuthProviderPanelTest.ldapServerUrl, LDAPAuthProviderPanelTest.basePath));
        setupPanel(LDAPAuthProviderPanelTest.USER_DN_PATTERN, null, null, "default");
        testSuccessfulConnection();
    }

    @Test
    public void testTestConnectionWithUserFilter() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPAuthProviderPanelTest.ldapServerUrl, LDAPAuthProviderPanelTest.basePath));
        setupPanel(null, LDAPAuthProviderPanelTest.USER_FILTER, LDAPAuthProviderPanelTest.USER_FORMAT, null);
        testSuccessfulConnection();
    }

    @Test
    public void testTestConnectionFailedWithDnLookup() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPAuthProviderPanelTest.ldapServerUrl, LDAPAuthProviderPanelTest.basePath));
        setupPanel(LDAPAuthProviderPanelTest.USER_DN_PATTERN, null, null, null);
        testFailedConnection();
    }

    @Test
    public void testTestConnectionFailedWithUserFilter() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPAuthProviderPanelTest.ldapServerUrl, LDAPAuthProviderPanelTest.basePath));
        setupPanel(null, LDAPAuthProviderPanelTest.USER_FILTER, LDAPAuthProviderPanelTest.USER_FORMAT, null);
        testFailedConnection();
    }

    private class LDAPFormTestPage extends FormTestPage {
        public LDAPFormTestPage(ComponentBuilder builder, CompoundPropertyModel<Object> model) {
            super(builder, model);
        }

        private static final long serialVersionUID = 3150973967583096118L;

        @Override
        protected void onBeforeRender() {
            feedbackPanel = new FeedbackPanel("topFeedback");
            feedbackPanel.setOutputMarkupId(true);
            addOrReplace(feedbackPanel);
            super.onBeforeRender();
        }
    }
}

