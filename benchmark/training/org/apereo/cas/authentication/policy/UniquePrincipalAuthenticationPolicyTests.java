package org.apereo.cas.authentication.policy;


import java.util.LinkedHashSet;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link UniquePrincipalAuthenticationPolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreWebConfiguration.class, CasCoreUtilConfiguration.class, CasCoreHttpConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreTicketCatalogConfiguration.class })
@DirtiesContext
public class UniquePrincipalAuthenticationPolicyTests {
    @Autowired
    @Qualifier("ticketRegistry")
    private TicketRegistry ticketRegistry;

    @Test
    @SneakyThrows
    public void verifyPolicyIsGoodUserNotFound() {
        this.ticketRegistry.deleteAll();
        val p = new UniquePrincipalAuthenticationPolicy(this.ticketRegistry);
        Assertions.assertTrue(p.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication("casuser"), new LinkedHashSet()));
    }

    @Test
    @SneakyThrows
    public void verifyPolicyFailsUserFoundOnce() {
        this.ticketRegistry.deleteAll();
        this.ticketRegistry.addTicket(new org.apereo.cas.ticket.TicketGrantingTicketImpl("TGT-1", CoreAuthenticationTestUtils.getAuthentication("casuser"), new NeverExpiresExpirationPolicy()));
        val p = new UniquePrincipalAuthenticationPolicy(this.ticketRegistry);
        Assertions.assertFalse(p.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication("casuser"), new LinkedHashSet()));
    }
}

