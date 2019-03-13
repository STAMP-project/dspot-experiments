package org.apereo.cas.authentication.support;


import AuthenticationResultCode.INVALID_CREDENTIAL;
import javax.security.auth.login.AccountExpiredException;
import lombok.val;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.ldaptive.auth.AuthenticationResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link GroovyPasswordPolicyHandlingStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class })
@Tag("Groovy")
public class GroovyPasswordPolicyHandlingStrategyTests {
    @Test
    public void verifyStrategySupportsDefault() {
        val resource = new ClassPathResource("lppe-strategy.groovy");
        val s = new org.apereo.cas.authentication.support.password.GroovyPasswordPolicyHandlingStrategy<AuthenticationResponse>(resource);
        val res = Mockito.mock(AuthenticationResponse.class);
        Mockito.when(res.getAuthenticationResultCode()).thenReturn(INVALID_CREDENTIAL);
        Mockito.when(res.getResult()).thenReturn(false);
        val results = s.handle(res, Mockito.mock(PasswordPolicyConfiguration.class));
        Assertions.assertFalse(s.supports(null));
        Assertions.assertTrue(s.supports(res));
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    public void verifyStrategyHandlesErrors() {
        val resource = new ClassPathResource("lppe-strategy-throws-error.groovy");
        val s = new org.apereo.cas.authentication.support.password.GroovyPasswordPolicyHandlingStrategy<AuthenticationResponse>(resource);
        val res = Mockito.mock(AuthenticationResponse.class);
        Assertions.assertThrows(AccountExpiredException.class, () -> s.handle(res, Mockito.mock(PasswordPolicyConfiguration.class)));
    }
}

