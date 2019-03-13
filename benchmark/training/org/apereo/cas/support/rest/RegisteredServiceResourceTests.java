package org.apereo.cas.support.rest;


import lombok.RequiredArgsConstructor;
import org.apereo.cas.authentication.AuthenticationTransaction;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit tests for {@link RegisteredServiceResource}.
 *
 * @author Dmitriy Kopylenko
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
public class RegisteredServiceResourceTests {
    @Mock
    private ServicesManager servicesManager;

    @Test
    public void checkRegisteredServiceNotAuthorized() throws Exception {
        runTest("memberOf", "something", "test:test", status().isForbidden());
    }

    @Test
    public void checkRegisteredServiceNormal() throws Exception {
        runTest("memberOf", "admin", "test:test", status().isOk());
    }

    @Test
    public void checkRegisteredServiceNoAuthn() throws Exception {
        runTest("memberOf", "something", "testfail:something", status().isUnauthorized());
    }

    @Test
    public void checkRegisteredServiceNoAttributeValue() throws Exception {
        runTest("memberOf", null, "test:test", status().isForbidden());
    }

    @Test
    public void checkRegisteredServiceNoAttribute() throws Exception {
        runTest(null, null, "test:test", status().isForbidden());
    }

    @RequiredArgsConstructor
    private static class AuthenticationCredentialMatcher implements ArgumentMatcher<AuthenticationTransaction> {
        private final String id;

        @Override
        public boolean matches(final AuthenticationTransaction t) {
            return (t != null) && (t.getPrimaryCredential().get().getId().equalsIgnoreCase(this.id));
        }
    }
}

