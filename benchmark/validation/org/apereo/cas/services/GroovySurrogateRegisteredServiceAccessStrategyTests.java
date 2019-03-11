package org.apereo.cas.services;


import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GroovySurrogateRegisteredServiceAccessStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
public class GroovySurrogateRegisteredServiceAccessStrategyTests {
    @Test
    public void verifySurrogateDisabled() {
        val a = new GroovySurrogateRegisteredServiceAccessStrategy();
        a.setGroovyScript("classpath:/surrogate-access.groovy");
        val result = a.doPrincipalAttributesAllowServiceAccess("casuser-disabled", CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true));
        Assertions.assertFalse(result);
    }

    @Test
    public void verifySurrogateAllowed() {
        val a = new GroovySurrogateRegisteredServiceAccessStrategy();
        a.setGroovyScript("classpath:/surrogate-access.groovy");
        val result = a.doPrincipalAttributesAllowServiceAccess("casuser-enabled", CollectionUtils.wrap(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true));
        Assertions.assertTrue(result);
    }
}

