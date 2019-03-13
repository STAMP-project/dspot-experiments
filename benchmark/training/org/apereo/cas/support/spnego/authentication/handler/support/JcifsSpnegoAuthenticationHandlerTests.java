package org.apereo.cas.support.spnego.authentication.handler.support;


import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.spnego.MockJcifsAuthentication;
import org.apereo.cas.support.spnego.MockUnsuccessfulJcifsAuthentication;
import org.apereo.cas.support.spnego.authentication.principal.SpnegoCredential;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Marc-Antoine Garrigue
 * @author Arnaud Lesueur
 * @since 4.2.0
 */
public class JcifsSpnegoAuthenticationHandlerTests {
    private static final String USERNAME = "Username";

    @Test
    public void verifySuccessfulAuthenticationWithDomainName() throws Exception {
        val credentials = new SpnegoCredential(new byte[]{ 0, 1, 2 });
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockJcifsAuthentication()), true, true, null);
        Assertions.assertNotNull(authenticationHandler.authenticate(credentials));
        Assertions.assertEquals("test", credentials.getPrincipal().getId());
        Assertions.assertNotNull(credentials.getNextToken());
    }

    @Test
    public void verifySuccessfulAuthenticationWithoutDomainName() throws Exception {
        val credentials = new SpnegoCredential(new byte[]{ 0, 1, 2 });
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockJcifsAuthentication()), false, true, null);
        Assertions.assertNotNull(authenticationHandler.authenticate(credentials));
        Assertions.assertEquals("test", credentials.getPrincipal().getId());
        Assertions.assertNotNull(credentials.getNextToken());
    }

    @Test
    public void verifyUnsuccessfulAuthenticationWithExceptionOnProcess() throws Exception {
        val credentials = new SpnegoCredential(new byte[]{ 0, 1, 2 });
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockUnsuccessfulJcifsAuthentication(true)), true, true, null);
        JcifsSpnegoAuthenticationHandlerTests.authenticate(credentials, authenticationHandler);
    }

    @Test
    public void verifyUnsuccessfulAuthentication() throws Exception {
        val credentials = new SpnegoCredential(new byte[]{ 0, 1, 2 });
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockUnsuccessfulJcifsAuthentication(false)), true, true, null);
        JcifsSpnegoAuthenticationHandlerTests.authenticate(credentials, authenticationHandler);
    }

    @Test
    public void verifySupports() {
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockJcifsAuthentication()), true, true, null);
        Assertions.assertFalse(authenticationHandler.supports(((SpnegoCredential) (null))));
        Assertions.assertTrue(authenticationHandler.supports(new SpnegoCredential(new byte[]{ 0, 1, 2 })));
        Assertions.assertFalse(authenticationHandler.supports(new UsernamePasswordCredential()));
    }

    @Test
    public void verifyGetSimpleCredentials() {
        val myNtlmUser = "DOMAIN\\Username";
        val myNtlmUserWithNoDomain = JcifsSpnegoAuthenticationHandlerTests.USERNAME;
        val myKerberosUser = "Username@DOMAIN.COM";
        val factory = new DefaultPrincipalFactory();
        val authenticationHandler = new JcifsSpnegoAuthenticationHandler("", null, null, CollectionUtils.wrapList(new MockJcifsAuthentication()), true, true, null);
        Assertions.assertEquals(factory.createPrincipal(myNtlmUser), authenticationHandler.getPrincipal(myNtlmUser, true));
        Assertions.assertEquals(factory.createPrincipal(myNtlmUserWithNoDomain), authenticationHandler.getPrincipal(myNtlmUserWithNoDomain, false));
        Assertions.assertEquals(factory.createPrincipal(myKerberosUser), authenticationHandler.getPrincipal(myKerberosUser, false));
        val handlerNoDomain = new JcifsSpnegoAuthenticationHandler(StringUtils.EMPTY, Mockito.mock(ServicesManager.class), new DefaultPrincipalFactory(), CollectionUtils.wrapList(new MockJcifsAuthentication()), false, true, null);
        Assertions.assertEquals(factory.createPrincipal(JcifsSpnegoAuthenticationHandlerTests.USERNAME), handlerNoDomain.getPrincipal(myNtlmUser, true));
        Assertions.assertEquals(factory.createPrincipal(JcifsSpnegoAuthenticationHandlerTests.USERNAME), handlerNoDomain.getPrincipal(myNtlmUserWithNoDomain, true));
        Assertions.assertEquals(factory.createPrincipal(JcifsSpnegoAuthenticationHandlerTests.USERNAME), handlerNoDomain.getPrincipal(myKerberosUser, false));
    }
}

