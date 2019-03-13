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
package org.springframework.boot.actuate.amqp;


import Status.DOWN;
import Status.UP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.util.Collections;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link RabbitHealthIndicator}.
 *
 * @author Phillip Webb
 */
public class RabbitHealthIndicatorTests {
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Channel channel;

    @Test
    public void createWhenRabbitTemplateIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RabbitHealthIndicator(null)).withMessageContaining("RabbitTemplate must not be null");
    }

    @Test
    public void healthWhenConnectionSucceedsShouldReturnUpWithVersion() {
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(this.channel.getConnection()).willReturn(connection);
        BDDMockito.given(connection.getServerProperties()).willReturn(Collections.singletonMap("version", "123"));
        Health health = health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsEntry("version", "123");
    }

    @Test
    public void healthWhenConnectionFailsShouldReturnDown() {
        BDDMockito.given(this.channel.getConnection()).willThrow(new RuntimeException());
        Health health = health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
    }
}

