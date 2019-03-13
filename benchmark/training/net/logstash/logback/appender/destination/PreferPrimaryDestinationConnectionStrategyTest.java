/**
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
package net.logstash.logback.appender.destination;


import ch.qos.logback.core.util.Duration;
import org.junit.Test;


public class PreferPrimaryDestinationConnectionStrategyTest {
    private PreferPrimaryDestinationConnectionStrategy strategy = new PreferPrimaryDestinationConnectionStrategy();

    @Test
    public void testNoSecondaryConnectionTtl_success() {
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectSuccess(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
    }

    @Test
    public void testNoSecondaryConnectionTtl_failed() {
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectFailed(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
        strategy.connectFailed(0, 1, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(2);
        strategy.connectFailed(0, 2, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
    }

    @Test
    public void testSecondaryConnectionTtl_success() {
        strategy.setSecondaryConnectionTTL(Duration.buildByMilliseconds(1000));
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectSuccess(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
    }

    @Test
    public void testSecondaryConnectionTtl_failed() {
        strategy.setSecondaryConnectionTTL(Duration.buildByMilliseconds(1000));
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectFailed(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
        strategy.connectSuccess(1000, 1, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
    }
}

