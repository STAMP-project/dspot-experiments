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
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CouchbaseAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link CouchbasePersonAttributeDaoTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Couchbase")
@EnabledIfContinuousIntegration
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CouchbaseAuthenticationConfiguration.class, CasCoreConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreServicesConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreHttpConfiguration.class, CasCoreWebConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreUtilConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasAuthenticationEventExecutionPlanTestConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class }, properties = { "cas.authn.attributeRepository.couchbase.password=password", "cas.authn.attributeRepository.couchbase.bucket=testbucket" })
public class CouchbasePersonAttributeDaoTests {
    @Autowired
    @Qualifier("attributeRepository")
    private IPersonAttributeDao attributeRepository;

    @Test
    public void verifyAttributes() {
        val person = attributeRepository.getPerson("casuser");
        Assertions.assertNotNull(person);
        val attributes = person.getAttributes();
        Assertions.assertTrue(attributes.containsKey("firstname"));
        Assertions.assertTrue(attributes.containsKey("lastname"));
        Assertions.assertEquals("casuser", person.getName());
    }
}

