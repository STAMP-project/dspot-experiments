package org.apereo.cas.support.events;


import java.util.Collection;
import java.util.LinkedHashSet;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.support.events.config.CasCoreEventsConfiguration;
import org.apereo.cas.support.events.dao.AbstractCasEventRepository;
import org.apereo.cas.support.events.dao.CasEvent;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;


/**
 * This is {@link DefaultCasEventListenerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { DefaultCasEventListenerTests.TestEventConfiguration.class, CasCoreEventsConfiguration.class, RefreshAutoConfiguration.class })
public class DefaultCasEventListenerTests {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    @Qualifier("casEventRepository")
    private CasEventRepository casEventRepository;

    @Test
    public void verifyCasAuthenticationTransactionFailureEvent() {
        val event = new org.apereo.cas.support.events.authentication.CasAuthenticationTransactionFailureEvent(this, CollectionUtils.wrap("error", new FailedLoginException()), CollectionUtils.wrap(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
        eventPublisher.publishEvent(event);
        Assertions.assertFalse(casEventRepository.load().isEmpty());
    }

    @Test
    public void verifyTicketGrantingTicketCreated() {
        val tgt = new MockTicketGrantingTicket("casuser");
        val event = new org.apereo.cas.support.events.ticket.CasTicketGrantingTicketCreatedEvent(this, tgt);
        eventPublisher.publishEvent(event);
        Assertions.assertFalse(casEventRepository.load().isEmpty());
    }

    @Test
    public void verifyCasAuthenticationPolicyFailureEvent() {
        val event = new org.apereo.cas.support.events.authentication.CasAuthenticationPolicyFailureEvent(this, CollectionUtils.wrap("error", new FailedLoginException()), new org.apereo.cas.authentication.DefaultAuthenticationTransaction(CoreAuthenticationTestUtils.getService(), CollectionUtils.wrap(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword())), CoreAuthenticationTestUtils.getAuthentication());
        eventPublisher.publishEvent(event);
        Assertions.assertFalse(casEventRepository.load().isEmpty());
    }

    @Test
    public void verifyCasRiskyAuthenticationDetectedEvent() {
        val event = new org.apereo.cas.support.events.authentication.adaptive.CasRiskyAuthenticationDetectedEvent(this, CoreAuthenticationTestUtils.getAuthentication(), CoreAuthenticationTestUtils.getRegisteredService(), new Object());
        eventPublisher.publishEvent(event);
        Assertions.assertFalse(casEventRepository.load().isEmpty());
    }

    @TestConfiguration
    public static class TestEventConfiguration {
        @Bean
        public CasEventRepository casEventRepository() {
            return new AbstractCasEventRepository() {
                private final Collection<CasEvent> events = new LinkedHashSet<>();

                @Override
                public void save(final CasEvent event) {
                    events.add(event);
                }

                @Override
                public Collection<CasEvent> load() {
                    return events;
                }
            };
        }
    }
}

