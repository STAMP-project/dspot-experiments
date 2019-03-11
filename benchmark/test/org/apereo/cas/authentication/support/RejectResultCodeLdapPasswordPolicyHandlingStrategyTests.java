package org.apereo.cas.authentication.support;


import AuthenticationResultCode.INVALID_CREDENTIAL;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ldaptive.auth.AuthenticationResponse;
import org.mockito.Mockito;


/**
 * This is {@link RejectResultCodeLdapPasswordPolicyHandlingStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class RejectResultCodeLdapPasswordPolicyHandlingStrategyTests {
    @Test
    public void verifyStrategySupportsDefault() {
        val s = new RejectResultCodeLdapPasswordPolicyHandlingStrategy();
        val res = Mockito.mock(AuthenticationResponse.class);
        Mockito.when(res.getAuthenticationResultCode()).thenReturn(INVALID_CREDENTIAL);
        Assertions.assertFalse(s.supports(null));
        Mockito.when(res.getResult()).thenReturn(false);
        Assertions.assertFalse(s.supports(res));
        Mockito.when(res.getResult()).thenReturn(true);
        Assertions.assertFalse(s.supports(res));
    }
}

