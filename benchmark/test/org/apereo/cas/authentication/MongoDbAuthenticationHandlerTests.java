package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.config.CasMongoAuthenticationConfiguration;
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
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;


/**
 * Test cases for {@link MongoDbAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@SpringBootTest(classes = { CasMongoAuthenticationConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreHttpConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreServicesConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, RefreshAutoConfiguration.class })
@EnableScheduling
@TestPropertySource(properties = { "cas.authn.mongo.collection=users", "cas.authn.mongo.databaseName=cas", "cas.authn.mongo.clientUri=mongodb://root:secret@localhost:27017/admin", "cas.authn.mongo.attributes=loc,state", "cas.authn.mongo.usernameAttribute=username", "cas.authn.mongo.passwordAttribute=password", "cas.authn.pac4j.typedIdUsed=false" })
@EnabledIfContinuousIntegration
@Tag("MongoDb")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MongoDbAuthenticationHandlerTests {
    @Autowired
    @Qualifier("mongoAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyAuthentication() throws Exception {
        val result = this.authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("u1", "p1"));
        Assertions.assertEquals("u1", result.getPrincipal().getId());
        val attributes = result.getPrincipal().getAttributes();
        Assertions.assertTrue(attributes.containsKey("loc"));
        Assertions.assertTrue(attributes.containsKey("state"));
    }
}

