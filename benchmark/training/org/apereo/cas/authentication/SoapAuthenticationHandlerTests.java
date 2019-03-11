package org.apereo.cas.authentication;


import SpringBootTest.WebEnvironment;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.SoapAuthenticationConfiguration;
import org.apereo.cas.config.SoapAuthenticationServerTestConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link SoapAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, SoapAuthenticationServerTestConfiguration.class, CasCoreServicesConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreUtilConfiguration.class, SoapAuthenticationConfiguration.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = { "server.port=8080", "cas.authn.soap.url=http://localhost:8080/ws/users" })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class SoapAuthenticationHandlerTests {
    @Autowired
    @Qualifier("soapAuthenticationAuthenticationHandler")
    private AuthenticationHandler soapAuthenticationAuthenticationHandler;

    @Test
    @SneakyThrows
    public void verifyAction() {
        val creds = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon");
        val result = soapAuthenticationAuthenticationHandler.authenticate(creds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("CAS", result.getPrincipal().getId());
        Assertions.assertEquals(1, result.getPrincipal().getAttributes().size());
        Assertions.assertTrue(result.getPrincipal().getAttributes().containsKey("givenName"));
    }
}

