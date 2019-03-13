package org.apereo.cas.adaptors.radius.authentication.handler.support;


import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.RadiusConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link RadiusAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RadiusConfiguration.class, RefreshAutoConfiguration.class, CasMultifactorAuthenticationWebflowConfiguration.class })
@TestPropertySource(properties = { "cas.authn.radius.server.protocol=PAP", "cas.authn.radius.client.sharedSecret=testing123", "cas.authn.radius.client.inetAddress=localhost" })
@Tag("Radius")
@EnabledIfContinuousIntegration
public class RadiusAuthenticationHandlerTests {
    @Autowired
    @Qualifier("radiusAuthenticationHandler")
    private AuthenticationHandler radiusAuthenticationHandler;

    @Test
    public void verifyOperation() throws Exception {
        val result = radiusAuthenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon"));
        Assertions.assertNotNull(result);
    }
}

