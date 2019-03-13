package org.apereo.cas.trusted.authentication.storage.fingerprint;


import HttpRequestUtils.USER_AGENT_HEADER;
import lombok.val;
import org.apereo.cas.trusted.web.flow.fingerprint.UserAgentDeviceFingerprintComponentExtractor;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link UserAgentDeviceFingerprintComponentExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class UserAgentDeviceFingerprintComponentExtractorTests {
    @Test
    public void verifyAgentFingerprintNotFound() {
        ClientInfoHolder.setClientInfo(null);
        val ex = new UserAgentDeviceFingerprintComponentExtractor();
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        Assertions.assertFalse(ex.extractComponent("casuser", context, false).isPresent());
    }

    @Test
    public void verifyAgentFingerprintFound() {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.addHeader(USER_AGENT_HEADER, "TestAgent");
        val ex = new UserAgentDeviceFingerprintComponentExtractor();
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        Assertions.assertTrue(ex.extractComponent("casuser", context, false).isPresent());
    }
}

