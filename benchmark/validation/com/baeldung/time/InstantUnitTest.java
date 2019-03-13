package com.baeldung.time;


import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Instant.class })
public class InstantUnitTest {
    @Test
    public void givenInstantMock_whenNow_thenGetFixedInstant() {
        String instantExpected = "2014-12-22T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant instant = Instant.now(clock);
        mockStatic(Instant.class);
        Mockito.when(Instant.now()).thenReturn(instant);
        Instant now = Instant.now();
        assertThat(now.toString()).isEqualTo(instantExpected);
    }

    @Test
    public void givenFixedClock_whenNow_thenGetFixedInstant() {
        String instantExpected = "2014-12-22T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant instant = Instant.now(clock);
        assertThat(instant.toString()).isEqualTo(instantExpected);
    }
}

