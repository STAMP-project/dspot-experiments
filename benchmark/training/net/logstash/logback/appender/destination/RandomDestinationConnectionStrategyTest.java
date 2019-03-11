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
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RandomDestinationConnectionStrategyTest {
    private RandomDestinationConnectionStrategy strategy = Mockito.spy(new RandomDestinationConnectionStrategy());

    @Mock
    private Random random;

    @Test
    public void testNoConnectionTtl_success() {
        Mockito.when(random.nextInt(3)).thenReturn(0).thenReturn(1);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectSuccess(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
    }

    @Test
    public void testNoConnectionTtl_failed() {
        Mockito.when(random.nextInt(3)).thenReturn(0).thenReturn(2).thenReturn(1);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectFailed(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(false);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(2);
        strategy.connectFailed(0, 1, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
        strategy.connectFailed(0, 2, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
    }

    @Test
    public void testConnectionTtl_success() {
        Mockito.when(random.nextInt(3)).thenReturn(0).thenReturn(1);
        strategy.setConnectionTTL(Duration.buildByMilliseconds(1000));
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectSuccess(0, 0, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(true);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
    }

    @Test
    public void testConnectionTtl_failed() {
        Mockito.when(random.nextInt(3)).thenReturn(0).thenReturn(2).thenReturn(1);
        strategy.setConnectionTTL(Duration.buildByMilliseconds(1000));
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(0);
        strategy.connectFailed(0, 0, 3);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(2);
        strategy.connectSuccess(1000, 1, 3);
        assertThat(strategy.shouldReconnect(5000, 0, 3)).isEqualTo(true);
        assertThat(strategy.selectNextDestinationIndex(0, 3)).isEqualTo(1);
    }
}

