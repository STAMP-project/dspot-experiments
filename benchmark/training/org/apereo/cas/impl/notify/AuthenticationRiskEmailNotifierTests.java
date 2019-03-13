package org.apereo.cas.impl.notify;


import java.math.BigDecimal;
import lombok.val;
import org.apereo.cas.api.AuthenticationRiskScore;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.impl.calcs.BaseAuthenticationRequestRiskCalculatorTests;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link AuthenticationRiskEmailNotifierTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfPortOpen(port = 25000)
@EnabledIfContinuousIntegration
@TestPropertySource(locations = "classpath:risk-electrofence.properties")
@Import(MailSenderAutoConfiguration.class)
public class AuthenticationRiskEmailNotifierTests extends BaseAuthenticationRequestRiskCalculatorTests {
    @Test
    public void verifyOperation() {
        try {
            authenticationRiskEmailNotifier.setRegisteredService(CoreAuthenticationTestUtils.getRegisteredService());
            val principal = CoreAuthenticationTestUtils.getPrincipal(CollectionUtils.wrap("mail", "cas@example.org"));
            val authentication = CoreAuthenticationTestUtils.getAuthentication(principal);
            authenticationRiskEmailNotifier.setAuthentication(authentication);
            authenticationRiskEmailNotifier.setAuthenticationRiskScore(new AuthenticationRiskScore(BigDecimal.ONE));
            authenticationRiskEmailNotifier.publish();
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

