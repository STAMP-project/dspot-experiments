package org.apereo.cas.token.authentication;


import RegisteredServiceProperty.RegisteredServiceProperties.TOKEN_SECRET_ENCRYPTION;
import RegisteredServiceProperty.RegisteredServiceProperties.TOKEN_SECRET_SIGNING;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.TokenAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.util.gen.DefaultRandomStringGenerator;
import org.apereo.cas.util.gen.RandomStringGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * This is {@link TokenAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, TokenAuthenticationHandlerTests.TestTokenAuthenticationConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreServicesConfiguration.class, TokenAuthenticationConfiguration.class })
public class TokenAuthenticationHandlerTests {
    private static final RandomStringGenerator RANDOM_STRING_GENERATOR = new DefaultRandomStringGenerator();

    private static final String SIGNING_SECRET = TokenAuthenticationHandlerTests.RANDOM_STRING_GENERATOR.getNewString(256);

    private static final String ENCRYPTION_SECRET = TokenAuthenticationHandlerTests.RANDOM_STRING_GENERATOR.getNewString(48);

    @Autowired
    @Qualifier("tokenAuthenticationHandler")
    private AuthenticationHandler tokenAuthenticationHandler;

    @Test
    @SneakyThrows
    public void verifyKeysAreSane() {
        val g = new org.pac4j.jwt.profile.JwtGenerator<CommonProfile>();
        g.setSignatureConfiguration(new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(TokenAuthenticationHandlerTests.SIGNING_SECRET, JWSAlgorithm.HS256));
        g.setEncryptionConfiguration(new org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration(TokenAuthenticationHandlerTests.ENCRYPTION_SECRET, JWEAlgorithm.DIR, EncryptionMethod.A192CBC_HS384));
        val profile = new CommonProfile();
        profile.setId("casuser");
        val token = g.generate(profile);
        val c = new TokenCredential(token, RegisteredServiceTestUtils.getService());
        val result = this.tokenAuthenticationHandler.authenticate(c);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getPrincipal().getId(), profile.getId());
    }

    @Configuration("TokenAuthenticationTests")
    public static class TestTokenAuthenticationConfiguration {
        @Bean
        public List inMemoryRegisteredServices() {
            val svc = RegisteredServiceTestUtils.getRegisteredService(".*");
            svc.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
            val p = new DefaultRegisteredServiceProperty();
            p.addValue(TokenAuthenticationHandlerTests.SIGNING_SECRET);
            svc.getProperties().put(TOKEN_SECRET_SIGNING.getPropertyName(), p);
            val p2 = new DefaultRegisteredServiceProperty();
            p2.addValue(TokenAuthenticationHandlerTests.ENCRYPTION_SECRET);
            svc.getProperties().put(TOKEN_SECRET_ENCRYPTION.getPropertyName(), p2);
            val l = new ArrayList<org.apereo.cas.services.RegisteredService>();
            l.add(svc);
            return l;
        }
    }
}

