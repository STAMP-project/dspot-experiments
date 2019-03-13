package org.apereo.cas.trusted.web.flow;


import HttpRequestUtils.USER_AGENT_HEADER;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.trusted.AbstractMultifactorAuthenticationTrustStorageTests;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.util.MultifactorAuthenticationTrustUtils;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link MultifactorAuthenticationVerifyTrustActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.authn.mfa.trusted.expiration=30", "cas.authn.mfa.trusted.timeUnit=SECONDS" })
@Getter
public class MultifactorAuthenticationVerifyTrustActionTests extends AbstractMultifactorAuthenticationTrustStorageTests {
    @Autowired
    @Qualifier("mfaTrustEngine")
    protected MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyDeviceNotTrusted() throws Exception {
        val r = AbstractMultifactorAuthenticationTrustStorageTests.getMultifactorAuthenticationTrustRecord();
        r.setRecordDate(LocalDateTime.now().minusSeconds(5));
        getMfaTrustEngine().set(r);
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(r.getPrincipal()), context);
        Assertions.assertEquals("no", mfaVerifyTrustAction.execute(context).getId());
    }

    @Test
    public void verifyDeviceTrusted() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("123.456.789.000");
        request.setLocalAddr("123.456.789.000");
        request.addHeader(USER_AGENT_HEADER, "test");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val r = AbstractMultifactorAuthenticationTrustStorageTests.getMultifactorAuthenticationTrustRecord();
        r.setRecordDate(LocalDateTime.now().minusSeconds(5));
        r.setDeviceFingerprint(deviceFingerprintStrategy.determineFingerprint(r.getPrincipal(), context, true));
        mfaTrustEngine.set(r);
        Assertions.assertNotNull(response.getCookies());
        Assertions.assertTrue(((response.getCookies().length) == 1));
        request.setCookies(response.getCookies());
        val authn = CoreAuthenticationTestUtils.getAuthentication(r.getPrincipal());
        WebUtils.putAuthentication(authn, context);
        Assertions.assertEquals("yes", mfaVerifyTrustAction.execute(context).getId());
        Assertions.assertTrue(MultifactorAuthenticationTrustUtils.isMultifactorAuthenticationTrustedInScope(context));
        Assertions.assertTrue(authn.getAttributes().containsKey(casProperties.getAuthn().getMfa().getTrusted().getAuthenticationContextAttribute()));
    }
}

