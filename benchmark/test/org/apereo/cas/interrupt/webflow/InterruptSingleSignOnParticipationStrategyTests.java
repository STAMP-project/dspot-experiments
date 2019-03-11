package org.apereo.cas.interrupt.webflow;


import lombok.val;
import org.apereo.cas.interrupt.InterruptResponse;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link InterruptSingleSignOnParticipationStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class InterruptSingleSignOnParticipationStrategyTests {
    @Test
    public void verifyStrategyWithoutInterrupt() {
        val s = new InterruptSingleSignOnParticipationStrategy(Mockito.mock(ServicesManager.class), true, true);
        Assertions.assertTrue(s.isParticipating(new MockRequestContext()));
    }

    @Test
    public void verifyStrategyWithInterruptDisabled() {
        val s = new InterruptSingleSignOnParticipationStrategy(Mockito.mock(ServicesManager.class), true, true);
        val ctx = new MockRequestContext();
        val response = new InterruptResponse();
        response.setSsoEnabled(false);
        InterruptUtils.putInterruptIn(ctx, response);
        Assertions.assertFalse(s.isParticipating(ctx));
    }
}

