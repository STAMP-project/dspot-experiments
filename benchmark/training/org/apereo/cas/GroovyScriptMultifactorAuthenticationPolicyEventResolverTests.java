package org.apereo.cas;


import TestMultifactorAuthenticationProvider.ID;
import lombok.val;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link GroovyScriptMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
@TestPropertySource(properties = "cas.authn.mfa.groovyScript=classpath:GroovyMfaResolver.groovy")
public class GroovyScriptMultifactorAuthenticationPolicyEventResolverTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier("groovyScriptAuthenticationPolicyWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    private MockRequestContext context;

    @Test
    public void verifyOperationNeedsMfa() {
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }
}

