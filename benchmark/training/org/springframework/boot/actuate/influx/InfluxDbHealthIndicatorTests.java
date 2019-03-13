/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.influx;


import Status.DOWN;
import Status.UP;
import java.io.IOException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.Pong;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link InfluxDbHealthIndicator}.
 *
 * @author Edd? Mel?ndez
 */
public class InfluxDbHealthIndicatorTests {
    @Test
    public void influxDbIsUp() {
        Pong pong = Mockito.mock(Pong.class);
        BDDMockito.given(pong.getVersion()).willReturn("0.9");
        InfluxDB influxDB = Mockito.mock(InfluxDB.class);
        BDDMockito.given(influxDB.ping()).willReturn(pong);
        InfluxDbHealthIndicator healthIndicator = new InfluxDbHealthIndicator(influxDB);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("version")).isEqualTo("0.9");
        Mockito.verify(influxDB).ping();
    }

    @Test
    public void influxDbIsDown() {
        InfluxDB influxDB = Mockito.mock(InfluxDB.class);
        BDDMockito.given(influxDB.ping()).willThrow(new InfluxDBException(new IOException("Connection failed")));
        InfluxDbHealthIndicator healthIndicator = new InfluxDbHealthIndicator(influxDB);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection failed");
        Mockito.verify(influxDB).ping();
    }
}

