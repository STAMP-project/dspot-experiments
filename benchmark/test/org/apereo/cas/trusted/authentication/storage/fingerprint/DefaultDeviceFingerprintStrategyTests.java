package org.apereo.cas.trusted.authentication.storage.fingerprint;


import HttpRequestUtils.USER_AGENT_HEADER;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.trusted.AbstractMultifactorAuthenticationTrustStorageTests;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link DefaultDeviceFingerprintStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
public class DefaultDeviceFingerprintStrategyTests extends AbstractMultifactorAuthenticationTrustStorageTests {
    @Autowired
    @Qualifier("mfaTrustEngine")
    protected MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @Test
    public void verifyAction() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("123.456.789.000");
        request.setLocalAddr("123.456.789.000");
        request.addHeader(USER_AGENT_HEADER, "test");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val f1 = deviceFingerprintStrategy.determineFingerprint("casuser", context, false);
        val f2 = deviceFingerprintStrategy.determineFingerprint("casuser", context, false);
        Assertions.assertNotEquals(f1, f2);
        val f3 = deviceFingerprintStrategy.determineFingerprint("casuser", context, true);
        Assertions.assertNotNull(response.getCookies());
        Assertions.assertTrue(((response.getCookies().length) == 1));
        request.setCookies(response.getCookies());
        val f4 = deviceFingerprintStrategy.determineFingerprint("casuser", context, false);
        Assertions.assertEquals(f3, f4);
    }
}

