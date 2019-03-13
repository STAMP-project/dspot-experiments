package org.apereo.cas.ticket.registry;


import DynamoDbTicketRegistryFacilitator.ColumnNames;
import java.util.Arrays;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.DynamoDbTicketRegistryConfiguration;
import org.apereo.cas.config.DynamoDbTicketRegistryTicketCatalogConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link DynamoDbTicketRegistryFacilitatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfContinuousIntegration
@EnabledIfPortOpen(port = 8000)
@TestPropertySource(locations = "classpath:/dynamodb-ticketregistry.properties")
@SpringBootTest(classes = { DynamoDbTicketRegistryConfiguration.class, DynamoDbTicketRegistryTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreHttpConfiguration.class, CasCoreServicesConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreConfiguration.class, CasCoreWebConfiguration.class, CasCoreUtilConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasPersonDirectoryConfiguration.class, RefreshAutoConfiguration.class })
@Tag("DynamoDb")
public class DynamoDbTicketRegistryFacilitatorTests {
    @Autowired
    @Qualifier("dynamoDbTicketRegistryFacilitator")
    private DynamoDbTicketRegistryFacilitator dynamoDbTicketRegistryFacilitator;

    @Test
    public void verifyBuildAttributeMap() {
        val ticket = new org.apereo.cas.mock.MockTicketGrantingTicket("casuser", CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), CollectionUtils.wrap("name", "CAS"));
        val map = dynamoDbTicketRegistryFacilitator.buildTableAttributeValuesMapFromTicket(ticket, ticket);
        Assertions.assertFalse(map.isEmpty());
        Arrays.stream(ColumnNames.values()).forEach(( c) -> assertTrue(map.containsKey(c.getColumnName())));
    }

    @Test
    public void verifyTicketOperations() {
        dynamoDbTicketRegistryFacilitator.createTicketTables(true);
        val ticket = new org.apereo.cas.mock.MockTicketGrantingTicket("casuser", CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), CollectionUtils.wrap("name", "CAS"));
        dynamoDbTicketRegistryFacilitator.put(ticket, ticket);
        val col = dynamoDbTicketRegistryFacilitator.getAll();
        Assertions.assertFalse(col.isEmpty());
        val ticketFetched = dynamoDbTicketRegistryFacilitator.get(ticket.getId(), ticket.getId());
        Assertions.assertEquals(ticket, ticketFetched);
        Assertions.assertFalse(dynamoDbTicketRegistryFacilitator.delete("badticket", "badticket"));
        Assertions.assertTrue(((dynamoDbTicketRegistryFacilitator.deleteAll()) > 0));
    }
}

