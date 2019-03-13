package org.apereo.cas.logout;


import java.net.URL;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DefaultSingleLogoutServiceLogoutUrlBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class DefaultSingleLogoutServiceLogoutUrlBuilderTests {
    @Test
    public void verifyLogoutUrlByService() {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService("https://www.google.com");
        svc.setLogoutUrl("http://www.example.com/logout");
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://www.google.com"));
        Assertions.assertEquals(url.iterator().next().getUrl(), svc.getLogoutUrl());
    }

    @Test
    public void verifyLogoutUrlByDefault() throws Exception {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://www.somewhere.com/logout?p=v"));
        Assertions.assertEquals(new URL("https://www.somewhere.com/logout?p=v").toExternalForm(), url.iterator().next().getUrl());
    }

    @Test
    public void verifyLogoutUrlUnknownUrlProtocol() {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("imaps://etc.example.org"));
        Assertions.assertTrue(url.isEmpty());
    }

    @Test
    public void verifyLocalLogoutUrlWithLocalUrlNotAllowed() {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://localhost/logout?p=v"));
        Assertions.assertTrue(url.isEmpty());
    }

    @Test
    public void verifyLocalLogoutUrlWithLocalUrlAllowed() throws Exception {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(true);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://localhost/logout?p=v"));
        Assertions.assertEquals(new URL("https://localhost/logout?p=v").toExternalForm(), url.iterator().next().getUrl());
    }

    @Test
    public void verifyLocalLogoutUrlWithValidRegExValidationAndLocalUrlNotAllowed() throws Exception {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false, "\\w*", true);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://localhost/logout?p=v"));
        Assertions.assertEquals(new URL("https://localhost/logout?p=v").toExternalForm(), url.iterator().next().getUrl());
    }

    @Test
    public void verifyLocalLogoutUrlWithInvalidRegExValidationAndLocalUrlAllowed() throws Exception {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(true, "\\d*", true);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://localhost/logout?p=v"));
        Assertions.assertEquals(new URL("https://localhost/logout?p=v").toExternalForm(), url.iterator().next().getUrl());
    }

    @Test
    public void verifyLocalLogoutUrlWithInvalidRegExValidationAndLocalUrlNotAllowed() {
        val svc = DefaultSingleLogoutServiceLogoutUrlBuilderTests.getRegisteredService(".+");
        svc.setLogoutUrl(null);
        val builder = DefaultSingleLogoutServiceLogoutUrlBuilderTests.createDefaultSingleLogoutServiceLogoutUrlBuilder(false, "\\d*", true);
        val url = builder.determineLogoutUrl(svc, DefaultSingleLogoutServiceLogoutUrlBuilderTests.getService("https://localhost/logout?p=v"));
        Assertions.assertTrue(url.isEmpty());
    }
}

