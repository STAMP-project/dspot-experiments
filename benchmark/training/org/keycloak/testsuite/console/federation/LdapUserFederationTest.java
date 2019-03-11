package org.keycloak.testsuite.console.federation;


import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.federation.CreateLdapUserProvider;
import org.keycloak.util.ldap.LDAPEmbeddedServer;


/**
 *
 *
 * @author fkiss, pdrozd
 */
public class LdapUserFederationTest extends AbstractConsoleTest {
    private static final String UNSYNCED = "UNSYNCED";

    private static final String READ_ONLY = "READ_ONLY";

    private static final String RED_HAT_DIRECTORY_SERVER = "Red Hat Directory Server";

    private static final String WRITABLE = "WRITABLE";

    private static final String ACTIVE_DIRECTORY = "Active Directory";

    @Page
    private CreateLdapUserProvider createLdapUserProvider;

    @Test
    public void configureAdProvider() {
        createLdapUserProvider.navigateTo();
        createLdapUserProvider.form().selectVendor(LdapUserFederationTest.ACTIVE_DIRECTORY);
        createLdapUserProvider.form().setConsoleDisplayNameInput("ldap");
        createLdapUserProvider.form().selectEditMode(LdapUserFederationTest.WRITABLE);
        createLdapUserProvider.form().setLdapConnectionUrlInput("ldap://localhost:389");
        createLdapUserProvider.form().setLdapBindDnInput("KEYCLOAK/Administrator");
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        // createLdapUserProvider.form().setAccountAfterPasswordUpdateEnabled(false);
        // enable kerberos
        createLdapUserProvider.form().setAllowKerberosAuthEnabled(true);
        createLdapUserProvider.form().setKerberosRealmInput("KEYCLOAK.ORG");
        createLdapUserProvider.form().setServerPrincipalInput("HTTP/localhost@KEYCLOAK.ORG");
        createLdapUserProvider.form().setKeyTabInput("http.keytab");
        createLdapUserProvider.form().setDebugEnabled(true);
        createLdapUserProvider.form().save();
        assertAlertSuccess();
        ComponentRepresentation ufpr = testRealmResource().components().query(null, "org.keycloak.storage.UserStorageProvider").get(0);
        assertLdapProviderSetting(ufpr, "ldap", "0", LdapUserFederationTest.WRITABLE, "false", "ad", "1", "true", "true", "false");
        assertLdapBasicMapping(ufpr, "cn", "cn", "objectGUID", "person, organizationalPerson, user", "ou=People,dc=keycloak,dc=org");
        assertLdapSyncSetings(ufpr, "1000", "-1", "-1");
        assertLdapKerberosSetings(ufpr, "KEYCLOAK.ORG", "HTTP/localhost@KEYCLOAK.ORG", "http.keytab", "true", "false");
    }

    @Test
    public void configureRhdsProvider() {
        createLdapUserProvider.navigateTo();
        createLdapUserProvider.form().selectVendor(LdapUserFederationTest.RED_HAT_DIRECTORY_SERVER);
        createLdapUserProvider.form().setConsoleDisplayNameInput("ldap");
        createLdapUserProvider.form().selectEditMode(LdapUserFederationTest.READ_ONLY);
        createLdapUserProvider.form().setLdapConnectionUrlInput("ldap://localhost:389");
        createLdapUserProvider.form().setLdapBindDnInput("uid=admin,ou=system");
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        createLdapUserProvider.form().save();
        assertAlertSuccess();
        ComponentRepresentation ufpr = testRealmResource().components().query(null, "org.keycloak.storage.UserStorageProvider").get(0);
        assertLdapProviderSetting(ufpr, "ldap", "0", LdapUserFederationTest.READ_ONLY, "false", "rhds", "1", "true", "true", "true");
        assertLdapBasicMapping(ufpr, "uid", "uid", "nsuniqueid", "inetOrgPerson, organizationalPerson", "ou=People,dc=keycloak,dc=org");
        assertLdapSyncSetings(ufpr, "1000", "-1", "-1");
    }

    @Test
    public void invalidSettingsTest() {
        createLdapUserProvider.navigateTo();
        createLdapUserProvider.form().selectVendor(LdapUserFederationTest.ACTIVE_DIRECTORY);
        createLdapUserProvider.form().setConsoleDisplayNameInput("ldap");
        createLdapUserProvider.form().selectEditMode(LdapUserFederationTest.UNSYNCED);
        createLdapUserProvider.form().setLdapBindDnInput("uid=admin,ou=system");
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setLdapUserDnInput("");
        createLdapUserProvider.form().setLdapConnectionUrlInput("ldap://localhost:389");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindDnInput("");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setLdapBindDnInput("uid=admin,ou=system");
        createLdapUserProvider.form().setLdapBindCredentialInput("");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        createLdapUserProvider.form().setCustomUserSearchFilter("foo");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setCustomUserSearchFilter("");
        createLdapUserProvider.form().save();
        assertAlertSuccess();
        // Try updating invalid Custom LDAP Filter
        createLdapUserProvider.form().setCustomUserSearchFilter("(foo=bar");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setCustomUserSearchFilter("foo=bar)");
        createLdapUserProvider.form().save();
        assertAlertDanger();
        createLdapUserProvider.form().setCustomUserSearchFilter("(foo=bar)");
        createLdapUserProvider.form().save();
        assertAlertSuccess();
    }

    @Test
    public void testConnection() throws Exception {
        createLdapUserProvider.navigateTo();
        createLdapUserProvider.form().selectVendor(1);
        createLdapUserProvider.form().setConsoleDisplayNameInput("ldap");
        createLdapUserProvider.form().selectEditMode(LdapUserFederationTest.WRITABLE);
        createLdapUserProvider.form().setLdapConnectionUrlInput("ldap://localhost:10389");
        createLdapUserProvider.form().setLdapBindDnInput("uid=admin,ou=system");
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        // createLdapUserProvider.form().setAccountAfterPasswordUpdateEnabled(true);
        createLdapUserProvider.form().save();
        assertAlertSuccess();
        LDAPEmbeddedServer ldapServer = null;
        try {
            ldapServer = startEmbeddedLdapServer();
            createLdapUserProvider.form().testConnection();
            assertAlertSuccess();
            createLdapUserProvider.form().testAuthentication();
            assertAlertSuccess();
            createLdapUserProvider.form().synchronizeAllUsers();
            assertAlertSuccess();
            createLdapUserProvider.form().setLdapBindCredentialInput("secret1");
            createLdapUserProvider.form().testAuthentication();
            assertAlertDanger();
        } finally {
            if (ldapServer != null) {
                ldapServer.stop();
            }
        }
    }

    @Test
    public void checkVendors() throws ConfigurationException {
        createLdapUserProvider.navigateTo();
        List<String> vendorsExpected = ((List<String>) ((List<?>) (getConstantsProperties().getList("ldap-vendors"))));
        List<String> vendorsActual = createLdapUserProvider.form().getVendors();
        int vendorsExpectedSize = vendorsExpected.size();
        int vendorsActualSize = vendorsActual.size();
        Assert.assertTrue(((("Expected vendors count: " + vendorsExpectedSize) + "; actual count: ") + vendorsActualSize), (vendorsExpectedSize == vendorsActualSize));
        Assert.assertTrue("Vendors list doesn't match", vendorsExpected.containsAll(vendorsActual));
    }

    @Test
    public void configureConnectionPooling() {
        createLdapUserProvider.navigateTo();
        createLdapUserProvider.form().selectVendor(LdapUserFederationTest.ACTIVE_DIRECTORY);
        createLdapUserProvider.form().setConsoleDisplayNameInput("ldap");
        createLdapUserProvider.form().selectEditMode(LdapUserFederationTest.WRITABLE);
        createLdapUserProvider.form().setLdapConnectionUrlInput("ldap://localhost:389");
        createLdapUserProvider.form().setLdapBindDnInput("KEYCLOAK/Administrator");
        createLdapUserProvider.form().setLdapUserDnInput("ou=People,dc=keycloak,dc=org");
        createLdapUserProvider.form().setLdapBindCredentialInput("secret");
        createLdapUserProvider.form().connectionPoolingSettings();
        createLdapUserProvider.form().setConnectionPoolingAuthentication("none");
        createLdapUserProvider.form().setConnectionPoolingDebug("fine");
        createLdapUserProvider.form().setConnectionPoolingInitSize("10");
        createLdapUserProvider.form().setConnectionPoolingMaxSize("12");
        createLdapUserProvider.form().setConnectionPoolingPrefSize("11");
        createLdapUserProvider.form().setConnectionPoolingProtocol("ssl");
        createLdapUserProvider.form().setConnectionPoolingTimeout("500");
        createLdapUserProvider.form().save();
        assertAlertSuccess();
        ComponentRepresentation ufpr = testRealmResource().components().query(null, "org.keycloak.storage.UserStorageProvider").get(0);
        assertLdapConnectionPoolSettings(ufpr, "none", "fine", "10", "12", "11", "ssl", "500");
    }
}

