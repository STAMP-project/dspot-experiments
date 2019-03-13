package org.keycloak.testsuite.forms;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.pages.LoginPage;


public class ThemeSelectorTest extends AbstractTestRealmKeycloakTest {
    @Page
    protected LoginPage loginPage;

    @Test
    public void clientOverride() {
        loginPage.open();
        Assert.assertEquals("keycloak", detectTheme());
        ClientRepresentation rep = testRealm().clients().findByClientId("test-app").get(0);
        rep.getAttributes().put("login_theme", "base");
        testRealm().clients().get(rep.getId()).update(rep);
        loginPage.open();
        Assert.assertEquals("base", detectTheme());
        rep.getAttributes().put("login_theme", "");
        testRealm().clients().get(rep.getId()).update(rep);
    }
}

