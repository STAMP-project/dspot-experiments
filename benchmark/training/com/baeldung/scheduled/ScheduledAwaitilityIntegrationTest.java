package com.baeldung.scheduled;


import Duration.ONE_SECOND;
import com.baeldung.config.ScheduledConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


@SpringJUnitConfig(ScheduledConfig.class)
public class ScheduledAwaitilityIntegrationTest {
    @SpyBean
    private Counter counter;

    @Test
    public void whenWaitOneSecond_thenScheduledIsCalledAtLeastTenTimes() {
        await().atMost(ONE_SECOND).untilAsserted(() -> verify(counter, atLeast(10)).scheduled());
    }
}

