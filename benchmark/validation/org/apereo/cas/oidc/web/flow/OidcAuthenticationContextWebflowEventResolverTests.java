package org.apereo.cas.oidc.web.flow;


import TestMultifactorAuthenticationProvider.ID;
import lombok.val;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link OidcAuthenticationContextWebflowEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcAuthenticationContextWebflowEventResolverTests extends AbstractOidcTests {
    @Autowired
    @Qualifier("oidcAuthenticationContextWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private MockRequestContext context;

    @Test
    public void verifyOperationNeedsMfa() {
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }
}

