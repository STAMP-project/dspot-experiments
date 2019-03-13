package org.keycloak.testsuite.error;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.StreamUtil;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.arquillian.annotation.UncaughtServerErrorExpected;
import org.keycloak.testsuite.pages.ErrorPage;


public class UncaughtErrorPageTest extends AbstractKeycloakTest {
    @Page
    private ErrorPage errorPage;

    @Test
    public void invalidResource() throws MalformedURLException {
        checkPageNotFound("/auth/nosuch");
    }

    @Test
    public void invalidRealm() throws MalformedURLException {
        checkPageNotFound("/auth/realms/nosuch");
    }

    @Test
    public void invalidRealmResource() throws MalformedURLException {
        checkPageNotFound("/auth/realms/master/nosuch");
    }

    @Test
    @UncaughtServerErrorExpected
    public void uncaughtErrorJson() throws IOException {
        Response response = testingClient.testing().uncaughtError();
        Assert.assertEquals(500, response.getStatus());
        InputStream is = ((InputStream) (response.getEntity()));
        String responseString = StreamUtil.readString(is, Charset.forName("UTF-8"));
        Assert.assertTrue(responseString.contains("An internal server error has occurred"));
    }

    @Test
    @UncaughtServerErrorExpected
    public void uncaughtError() throws MalformedURLException {
        URI uri = suiteContext.getAuthServerInfo().getUriBuilder().path("/auth/realms/master/testing/uncaught-error").build();
        driver.navigate().to(uri.toURL());
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("An internal server error has occurred", errorPage.getError());
    }

    @Test
    public void errorPageException() {
        oauth.realm("master");
        oauth.clientId("nosuch");
        oauth.openLoginForm();
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("Client not found.", errorPage.getError());
    }

    @Test
    public void internationalisationEnabled() throws MalformedURLException {
        RealmResource testRealm = realmsResouce().realm("master");
        RealmRepresentation rep = testRealm.toRepresentation();
        rep.setInternationalizationEnabled(true);
        rep.setDefaultLocale("en");
        rep.setSupportedLocales(Collections.singleton("en"));
        testRealm.update(rep);
        try {
            checkPageNotFound("/auth/realms/master/nosuch");
            checkPageNotFound("/auth/nosuch");
        } finally {
            rep.setInternationalizationEnabled(false);
            testRealm.update(rep);
        }
    }
}

