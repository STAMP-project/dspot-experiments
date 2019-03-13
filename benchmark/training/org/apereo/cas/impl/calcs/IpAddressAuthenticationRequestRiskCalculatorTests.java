package org.apereo.cas.impl.calcs;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link IpAddressAuthenticationRequestRiskCalculatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@TestPropertySource(properties = "cas.authn.adaptive.risk.ip.enabled=true")
public class IpAddressAuthenticationRequestRiskCalculatorTests extends BaseAuthenticationRequestRiskCalculatorTests {
    @Test
    public void verifyTestWhenNoAuthnEventsFoundForUser() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("nobody");
        val service = RegisteredServiceTestUtils.getRegisteredService("test");
        val request = new MockHttpServletRequest();
        val score = authenticationRiskEvaluator.eval(authentication, service, request);
        Assertions.assertTrue(score.isHighestRisk());
    }

    @Test
    public void verifyTestWhenAuthnEventsFoundForUser() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = RegisteredServiceTestUtils.getRegisteredService("test");
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("107.181.69.221");
        request.setLocalAddr("127.0.0.1");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val score = authenticationRiskEvaluator.eval(authentication, service, request);
        Assertions.assertTrue(score.isRiskGreaterThan(casProperties.getAuthn().getAdaptive().getRisk().getThreshold()));
    }
}

