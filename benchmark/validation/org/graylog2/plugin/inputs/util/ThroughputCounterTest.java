/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.plugin.inputs.util;


import ThroughputCounter.READ_BYTES_1_SEC;
import ThroughputCounter.READ_BYTES_TOTAL;
import ThroughputCounter.WRITTEN_BYTES_1_SEC;
import ThroughputCounter.WRITTEN_BYTES_TOTAL;
import com.codahale.metrics.Gauge;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Map;
import org.junit.Test;


public class ThroughputCounterTest {
    private EventLoopGroup eventLoopGroup;

    private EmbeddedChannel channel;

    private ThroughputCounter throughputCounter;

    @Test
    public void counterReturnsZeroIfNoInteraction() {
        channel.finish();
        final Map<String, Gauge<Long>> gauges = throughputCounter.gauges();
        assertThat(gauges.get(READ_BYTES_1_SEC).getValue()).isEqualTo(0L);
        assertThat(gauges.get(WRITTEN_BYTES_1_SEC).getValue()).isEqualTo(0L);
        assertThat(gauges.get(READ_BYTES_TOTAL).getValue()).isEqualTo(0L);
        assertThat(gauges.get(WRITTEN_BYTES_TOTAL).getValue()).isEqualTo(0L);
    }

    @Test
    public void counterReturns4Gauges() {
        assertThat(throughputCounter.gauges()).hasSize(4);
    }
}

