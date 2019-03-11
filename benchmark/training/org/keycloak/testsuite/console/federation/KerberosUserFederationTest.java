package org.keycloak.testsuite.console.federation;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.federation.CreateKerberosUserProvider;


/**
 *
 *
 * @author pdrozd
 */
public class KerberosUserFederationTest extends AbstractConsoleTest {
    private static final String UNSYNCED = "UNSYNCED";

    private static final String READ_ONLY = "READ_ONLY";

    @Page
    private CreateKerberosUserProvider createKerberosUserProvider;

    @Test
    public void configureKerberosProvider() {
        createKerberosUserProvider.navigateTo();
        createKerberosUserProvider.form().setConsoleDisplayNameInput("kerberos");
        createKerberosUserProvider.form().setKerberosRealmInput("KEYCLOAK.ORG");
        createKerberosUserProvider.form().setServerPrincipalInput("HTTP/localhost@KEYCLOAK.ORG");
        createKerberosUserProvider.form().setKeyTabInput("http.keytab");
        createKerberosUserProvider.form().setDebugEnabled(true);
        createKerberosUserProvider.form().setAllowPasswordAuthentication(true);
        createKerberosUserProvider.form().selectEditMode(KerberosUserFederationTest.READ_ONLY);
        createKerberosUserProvider.form().setUpdateProfileFirstLogin(true);
        createKerberosUserProvider.form().save();
        assertAlertSuccess();
        ComponentRepresentation ufpr = testRealmResource().components().query(null, "org.keycloak.storage.UserStorageProvider").get(0);
        assertKerberosSetings(ufpr, "KEYCLOAK.ORG", "HTTP/localhost@KEYCLOAK.ORG", "http.keytab", "true", "true", "true");
    }

    @Test
    public void invalidSettingsTest() {
        createKerberosUserProvider.navigateTo();
        createKerberosUserProvider.form().setConsoleDisplayNameInput("kerberos");
        createKerberosUserProvider.form().setServerPrincipalInput("HTTP/localhost@KEYCLOAK.ORG");
        createKerberosUserProvider.form().setKeyTabInput("http.keytab");
        createKerberosUserProvider.form().setDebugEnabled(true);
        createKerberosUserProvider.form().setAllowPasswordAuthentication(true);
        createKerberosUserProvider.form().selectEditMode(KerberosUserFederationTest.UNSYNCED);
        createKerberosUserProvider.form().setUpdateProfileFirstLogin(true);
        createKerberosUserProvider.form().save();
        assertAlertDanger();
        createKerberosUserProvider.form().setServerPrincipalInput("");
        createKerberosUserProvider.form().setKerberosRealmInput("KEYCLOAK.ORG");
        createKerberosUserProvider.form().save();
        assertAlertDanger();
        createKerberosUserProvider.form().setServerPrincipalInput("HTTP/localhost@KEYCLOAK.ORG");
        createKerberosUserProvider.form().setKeyTabInput("");
        createKerberosUserProvider.form().save();
        assertAlertDanger();
        createKerberosUserProvider.form().setKeyTabInput("http.keytab");
        createKerberosUserProvider.form().save();
        assertAlertSuccess();
    }
}

