package org.apereo.cas.authentication.handler.support;


import javax.security.auth.login.LoginException;
import lombok.SneakyThrows;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.handler.support.jaas.JaasAuthenticationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
public class JaasAuthenticationHandlerSystemConfigurationTests {
    private static final String USERNAME = "test";

    private JaasAuthenticationHandler handler;

    @Test
    public void verifyWithAlternativeRealm() {
        handler.setRealm("TEST");
        Assertions.assertThrows(LoginException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(JaasAuthenticationHandlerSystemConfigurationTests.USERNAME, "test1")));
    }

    @Test
    @SneakyThrows
    public void verifyWithAlternativeRealmAndValidCredentials() {
        handler.setRealm("TEST");
        Assertions.assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(JaasAuthenticationHandlerSystemConfigurationTests.USERNAME, JaasAuthenticationHandlerSystemConfigurationTests.USERNAME)));
    }

    @Test
    @SneakyThrows
    public void verifyWithValidCredentials() {
        Assertions.assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyWithInvalidCredentials() {
        Assertions.assertThrows(LoginException.class, () -> this.handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(JaasAuthenticationHandlerSystemConfigurationTests.USERNAME, "test1")));
    }
}

