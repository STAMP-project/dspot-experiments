package org.apereo.cas.authentication;


import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.mfa.MultifactorAuthenticationTestUtils;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link DefaultMultifactorTriggerSelectionStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class DefaultMultifactorTriggerSelectionStrategyTests {
    @Test
    public void verifyOperation() {
        val trigger = Mockito.mock(MultifactorAuthenticationTrigger.class);
        Mockito.when(trigger.isActivated(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.of(new TestMultifactorAuthenticationProvider()));
        val strategy = new DefaultMultifactorTriggerSelectionStrategy(Collections.singletonList(trigger));
        val result = strategy.resolve(new MockHttpServletRequest(), MultifactorAuthenticationTestUtils.getRegisteredService(), MultifactorAuthenticationTestUtils.getAuthentication("casuser"), MultifactorAuthenticationTestUtils.getService("https://www.example.org"));
        Assertions.assertNotNull(result);
    }
}

