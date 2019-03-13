package org.apereo.cas.monitor;


import Status.DOWN;
import Status.UP;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link MemoryMonitorHealthIndicator} class.
 *
 * @author Marvin S. Addison
 * @since 3.5.0
 */
public class MemoryHealthIndicatorTests {
    @Test
    public void verifyObserveOk() {
        Assertions.assertEquals(UP, new MemoryMonitorHealthIndicator(0).health().getStatus());
    }

    @Test
    public void verifyObserveWarn() {
        val monitor = new MemoryMonitorHealthIndicator(100);
        Assertions.assertEquals(DOWN, monitor.health().getStatus());
    }
}

