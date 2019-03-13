package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.config.CasAuthenticationEventExecutionPlanTestConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CouchbaseAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link CouchbaseAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Couchbase")
@EnabledIfContinuousIntegration
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CouchbaseAuthenticationConfiguration.class, CasCoreConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreServicesConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreHttpConfiguration.class, CasCoreWebConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasCoreUtilConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasAuthenticationEventExecutionPlanTestConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class }, properties = { "cas.authn.couchbase.password=password", "cas.authn.couchbase.bucket=testbucket" })
public class CouchbaseAuthenticationHandlerTests {
    @Autowired
    @Qualifier("couchbaseAuthenticationHandler")
    private AuthenticationHandler couchbaseAuthenticationHandler;

    @Test
    public void verifyAccount() throws Exception {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon");
        val result = couchbaseAuthenticationHandler.authenticate(c);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("casuser", result.getPrincipal().getId());
        val attributes = result.getPrincipal().getAttributes();
        Assertions.assertEquals(2, attributes.size());
        Assertions.assertTrue(attributes.containsKey("firstname"));
        Assertions.assertTrue(attributes.containsKey("lastname"));
    }
}

