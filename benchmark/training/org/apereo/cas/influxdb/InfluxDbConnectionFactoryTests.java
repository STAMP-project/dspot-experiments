package org.apereo.cas.influxdb;


import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.influxdb.impl.InfluxDBResultMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link InfluxDbConnectionFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
@Tag("InfluxDb")
@EnabledIfContinuousIntegration
public class InfluxDbConnectionFactoryTests {
    private static final String CAS_EVENTS_DATABASE = "casEventsDatabase";

    private InfluxDbConnectionFactory factory;

    @Test
    public void verifyWritePoint() {
        val p = Point.measurement("events").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("hostname", "cas.example.org").build();
        factory.write(p, InfluxDbConnectionFactoryTests.CAS_EVENTS_DATABASE);
        val result = factory.query("*", "events", InfluxDbConnectionFactoryTests.CAS_EVENTS_DATABASE);
        val resultMapper = new InfluxDBResultMapper();
        val resultEvents = resultMapper.toPOJO(result, InfluxDbConnectionFactoryTests.InfluxEvent.class);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("cas.example.org", resultEvents.iterator().next().hostname);
    }

    @Measurement(name = "events")
    public static class InfluxEvent {
        @Column(name = "time")
        private Instant time;

        @Column(name = "hostname", tag = true)
        private String hostname;
    }
}

