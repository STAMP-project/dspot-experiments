package org.apereo.cas.services;


import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SurrogateRegisteredServiceAccessStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateRegisteredServiceAccessStrategyTests {
    @Test
    public void verifySurrogateDisabled() {
        val a = new SurrogateRegisteredServiceAccessStrategy();
        a.setSurrogateEnabled(false);
        val result = a.doPrincipalAttributesAllowServiceAccess("casuser", CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true));
        Assertions.assertFalse(result);
    }

    @Test
    public void verifySurrogateDisabledWithAttributes() {
        val a = new SurrogateRegisteredServiceAccessStrategy();
        a.setSurrogateEnabled(true);
        a.setSurrogateRequiredAttributes(CollectionUtils.wrap("surrogateA", "surrogateV"));
        val result = a.doPrincipalAttributesAllowServiceAccess("casuser", CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true));
        Assertions.assertFalse(result);
    }

    @Test
    public void verifySurrogateAllowed() {
        val a = new SurrogateRegisteredServiceAccessStrategy();
        a.setSurrogateEnabled(true);
        val result = a.doPrincipalAttributesAllowServiceAccess("casuser", CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true));
        Assertions.assertTrue(result);
    }
}

