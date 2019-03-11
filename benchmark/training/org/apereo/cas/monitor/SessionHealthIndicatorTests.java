package org.apereo.cas.monitor;


import Status.UP;
import lombok.val;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.UniqueTicketIdGenerator;
import org.apereo.cas.ticket.registry.DefaultTicketRegistry;
import org.apereo.cas.ticket.support.HardTimeoutExpirationPolicy;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link TicketRegistryHealthIndicator} class.
 *
 * @author Marvin S. Addison
 * @since 3.5.0
 */
public class SessionHealthIndicatorTests {
    private static final ExpirationPolicy TEST_EXP_POLICY = new HardTimeoutExpirationPolicy(10000);

    private static final UniqueTicketIdGenerator GENERATOR = new DefaultUniqueTicketIdGenerator();

    private DefaultTicketRegistry defaultRegistry;

    @Test
    public void verifyObserveOk() {
        SessionHealthIndicatorTests.addTicketsToRegistry(this.defaultRegistry, 5, 10);
        val monitor = new TicketRegistryHealthIndicator(defaultRegistry, (-1), (-1));
        val status = monitor.health();
        Assertions.assertEquals(UP, status.getStatus());
    }

    @Test
    public void verifyObserveWarnSessionsExceeded() {
        SessionHealthIndicatorTests.addTicketsToRegistry(this.defaultRegistry, 10, 1);
        val monitor = new TicketRegistryHealthIndicator(defaultRegistry, 0, 5);
        val status = monitor.health();
        Assertions.assertEquals("WARN", status.getStatus().getCode());
    }

    @Test
    public void verifyObserveWarnServiceTicketsExceeded() {
        SessionHealthIndicatorTests.addTicketsToRegistry(this.defaultRegistry, 1, 10);
        val monitor = new TicketRegistryHealthIndicator(defaultRegistry, 5, 0);
        val status = monitor.health();
        Assertions.assertEquals("WARN", status.getStatus().getCode());
    }
}

