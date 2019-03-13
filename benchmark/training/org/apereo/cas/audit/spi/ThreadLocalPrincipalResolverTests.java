package org.apereo.cas.audit.spi;


import CoreAuthenticationTestUtils.CONST_USERNAME;
import PrincipalResolver.UNKNOWN_USER;
import org.apereo.cas.audit.spi.principal.DefaultAuditPrincipalIdProvider;
import org.apereo.cas.audit.spi.principal.ThreadLocalPrincipalResolver;
import org.apereo.cas.authentication.AuthenticationCredentialsThreadLocalBinder;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link ThreadLocalPrincipalResolver}.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
public class ThreadLocalPrincipalResolverTests {
    private final ThreadLocalPrincipalResolver theResolver = new ThreadLocalPrincipalResolver(new DefaultAuditPrincipalIdProvider());

    @Test
    public void noAuthenticationOrCredentialsAvailableInThreadLocal() {
        assertResolvedPrincipal(UNKNOWN_USER);
    }

    @Test
    public void singleThreadSetsSingleCredential() {
        AuthenticationCredentialsThreadLocalBinder.bindCurrent(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        assertResolvedPrincipal(CONST_USERNAME);
    }

    @Test
    public void singleThreadSetsMultipleCredentials() {
        AuthenticationCredentialsThreadLocalBinder.bindCurrent(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("test2"));
        assertResolvedPrincipal(String.format("%s, %s", CONST_USERNAME, "test2"));
    }

    @Test
    public void singleThreadSetsAuthentication() {
        AuthenticationCredentialsThreadLocalBinder.bindCurrent(CoreAuthenticationTestUtils.getAuthentication());
        assertResolvedPrincipal(CONST_USERNAME);
    }
}

