package com.baeldung.time;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import mockit.Expectations;
import mockit.Mock;
import org.junit.jupiter.api.Test;


public class LocalDateTimeWithJMockUnitTest {
    @Test
    public void givenLocalDateTimeWithJMock_whenNow_thenGetFixedLocalDateTime() {
        Clock clock = Clock.fixed(Instant.parse("2014-12-21T10:15:30.00Z"), ZoneId.of("UTC"));
        new mockit.MockUp<LocalDateTime>() {
            @Mock
            public LocalDateTime now() {
                return LocalDateTime.now(clock);
            }
        };
        String dateTimeExpected = "2014-12-21T10:15:30";
        LocalDateTime now = LocalDateTime.now();
        assertThat(now).isEqualTo(dateTimeExpected);
    }

    @Test
    public void givenLocalDateTimeWithExpectations_whenNow_thenGetFixedLocalDateTime() {
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime dateTimeExpected = LocalDateTime.now(clock);
        new Expectations(LocalDateTime.class) {
            {
                LocalDateTime.now();
                result = dateTimeExpected;
            }
        };
        LocalDateTime now = LocalDateTime.now();
        assertThat(now).isEqualTo(dateTimeExpected);
    }
}

