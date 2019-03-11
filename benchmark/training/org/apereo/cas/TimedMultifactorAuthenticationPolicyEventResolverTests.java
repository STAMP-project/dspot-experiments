package org.apereo.cas;


import TestMultifactorAuthenticationProvider.ID;
import lombok.val;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link TimedMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.authn.adaptive.requireTimedMultifactor[0].providerId=mfa-dummy", "cas.authn.adaptive.requireTimedMultifactor[0].onOrBeforeHour=-1", "cas.authn.adaptive.requireTimedMultifactor[0].onOrAfterHour=-1", "cas.authn.adaptive.requireTimedMultifactor[0].onDays=Saturday,Sunday,Monday,Tuesday,Wednesday,Thursday,Friday" })
public class TimedMultifactorAuthenticationPolicyEventResolverTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier("timedAuthenticationPolicyWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    private MockRequestContext context;

    @Test
    public void verifyOperationNeedsMfa() {
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }
}

