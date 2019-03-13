package org.apereo.cas.authentication.audit;


import Credential.UNKNOWN_ID;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_PRINCIPAL;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_USER;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.SurrogateAuthenticationException;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SurrogateAuditPrincipalIdProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateAuditPrincipalIdProviderTests {
    @Test
    public void verifyAction() {
        val p = new SurrogateAuditPrincipalIdProvider();
        Assertions.assertEquals(UNKNOWN_ID, p.getPrincipalIdFrom(null, null, null));
        val auth = CoreAuthenticationTestUtils.getAuthentication(CoreAuthenticationTestUtils.getPrincipal(), CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, "true", AUTHENTICATION_ATTR_SURROGATE_PRINCIPAL, "principal", AUTHENTICATION_ATTR_SURROGATE_USER, "surrogateUser"));
        Assertions.assertTrue(p.supports(auth, new Object(), new SurrogateAuthenticationException("error")));
        Assertions.assertNotNull(p.getPrincipalIdFrom(auth, new Object(), new SurrogateAuthenticationException("error")));
    }
}

