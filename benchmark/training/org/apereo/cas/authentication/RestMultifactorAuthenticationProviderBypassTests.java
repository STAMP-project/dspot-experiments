package org.apereo.cas.authentication;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.authentication.mfa.MultifactorAuthenticationTestUtils;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.configuration.model.support.mfa.MultifactorAuthenticationProviderBypassProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link RestMultifactorAuthenticationProviderBypassTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("RestfulApi")
public class RestMultifactorAuthenticationProviderBypassTests {
    @Test
    public void verifyOperationShouldProceed() {
        try (val webServer = new org.apereo.cas.util.MockWebServer(9316, new ByteArrayResource("Y".getBytes(StandardCharsets.UTF_8), "REST Output"), HttpStatus.ACCEPTED)) {
            webServer.start();
            val props = new MultifactorAuthenticationProviderBypassProperties();
            props.getRest().setUrl("http://localhost:9316");
            val provider = new TestMultifactorAuthenticationProvider();
            val r = new org.apereo.cas.authentication.bypass.RestMultifactorAuthenticationProviderBypass(props, provider.getId());
            val res = r.shouldMultifactorAuthenticationProviderExecute(MultifactorAuthenticationTestUtils.getAuthentication("casuser"), MultifactorAuthenticationTestUtils.getRegisteredService(), provider, new MockHttpServletRequest());
            Assertions.assertTrue(res);
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

