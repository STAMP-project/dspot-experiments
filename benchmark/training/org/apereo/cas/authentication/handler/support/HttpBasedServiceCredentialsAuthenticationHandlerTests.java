package org.apereo.cas.authentication.handler.support;


import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.http.SimpleHttpClientFactoryBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class HttpBasedServiceCredentialsAuthenticationHandlerTests {
    private HttpBasedServiceCredentialsAuthenticationHandler authenticationHandler;

    @Test
    public void verifySupportsProperUserCredentials() {
        Assertions.assertTrue(this.authenticationHandler.supports(RegisteredServiceTestUtils.getHttpBasedServiceCredentials()));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        Assertions.assertFalse(this.authenticationHandler.supports(RegisteredServiceTestUtils.getCredentialsWithDifferentUsernameAndPassword("test", "test2")));
    }

    @Test
    @SneakyThrows
    public void verifyAcceptsProperCertificateCredentials() {
        Assertions.assertNotNull(this.authenticationHandler.authenticate(RegisteredServiceTestUtils.getHttpBasedServiceCredentials()));
    }

    @Test
    public void verifyRejectsInProperCertificateCredentials() {
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(RegisteredServiceTestUtils.getHttpBasedServiceCredentials("https://clearinghouse.ja-sig.org")));
    }

    @Test
    @SneakyThrows
    public void verifyAcceptsNonHttpsCredentials() {
        Assertions.assertNotNull(this.authenticationHandler.authenticate(RegisteredServiceTestUtils.getHttpBasedServiceCredentials("http://www.google.com")));
    }

    @Test
    public void verifyNoAcceptableStatusCode() {
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(RegisteredServiceTestUtils.getHttpBasedServiceCredentials("https://clue.acs.rutgers.edu")));
    }

    @Test
    public void verifyNoAcceptableStatusCodeButOneSet() {
        val clientFactory = new SimpleHttpClientFactoryBean();
        clientFactory.setAcceptableCodes(CollectionUtils.wrapList(900));
        val httpClient = clientFactory.getObject();
        this.authenticationHandler = new HttpBasedServiceCredentialsAuthenticationHandler("", null, null, null, httpClient);
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(RegisteredServiceTestUtils.getHttpBasedServiceCredentials("https://www.ja-sig.org")));
    }
}

