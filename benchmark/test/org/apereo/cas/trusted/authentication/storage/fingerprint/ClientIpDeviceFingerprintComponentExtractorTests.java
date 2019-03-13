package org.apereo.cas.trusted.authentication.storage.fingerprint;


import lombok.NoArgsConstructor;
import lombok.val;
import org.apereo.cas.trusted.web.flow.fingerprint.ClientIpDeviceFingerprintComponentExtractor;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link ClientIpDeviceFingerprintComponentExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@NoArgsConstructor
public class ClientIpDeviceFingerprintComponentExtractorTests {
    @Test
    public void verifyClientIpFingerprintNotFound() {
        ClientInfoHolder.setClientInfo(null);
        val ex = new ClientIpDeviceFingerprintComponentExtractor();
        Assertions.assertFalse(ex.extractComponent("casuser", new MockRequestContext(), false).isPresent());
    }

    @Test
    public void verifyClientIpFingerprintFound() {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        val clientInfo = new org.apereo.inspektr.common.web.ClientInfo(request);
        ClientInfoHolder.setClientInfo(clientInfo);
        val ex = new ClientIpDeviceFingerprintComponentExtractor();
        Assertions.assertTrue(ex.extractComponent("casuser", new MockRequestContext(), false).isPresent());
    }
}

