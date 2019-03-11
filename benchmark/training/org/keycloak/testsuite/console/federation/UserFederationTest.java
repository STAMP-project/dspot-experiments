package org.keycloak.testsuite.console.federation;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.federation.CreateKerberosUserProvider;
import org.keycloak.testsuite.console.page.federation.UserFederation;


/**
 *
 *
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class UserFederationTest extends AbstractConsoleTest {
    @Page
    private UserFederation userFederationPage;

    @Page
    private CreateKerberosUserProvider createKerberosUserProviderPage;

    @Test
    public void availableProviders() {
        Assert.assertTrue(userFederationPage.hasProvider("ldap"));
        Assert.assertTrue(userFederationPage.hasProvider("kerberos"));
    }

    @Test
    public void addRemoveFederation() {
        final String federationName = "KerberosProvider";
        userFederationPage.addFederation("kerberos");
        Assert.assertTrue(createKerberosUserProviderPage.isCurrent());
        createKerberosUserProviderPage.form().setConsoleDisplayNameInput(federationName);
        createKerberosUserProviderPage.form().setKerberosRealmInput("KEYCLOAK.ORG");
        createKerberosUserProviderPage.form().setServerPrincipalInput("HTTP/localhost@KEYCLOAK.ORG");
        createKerberosUserProviderPage.form().setKeyTabInput("http.keytab");
        createKerberosUserProviderPage.form().save();
        assertAlertSuccess();
        userFederationPage.navigateTo();
        Assert.assertEquals(1, userFederationPage.table().getFederationsCount());
        userFederationPage.table().editFederation(federationName);
        Assert.assertEquals(federationName, createKerberosUserProviderPage.form().getConsoleDisplayNameInput());
        userFederationPage.navigateTo();
        userFederationPage.table().removeFederation(federationName);
        Assert.assertEquals(0, userFederationPage.table().getFederationsCount());
    }
}

