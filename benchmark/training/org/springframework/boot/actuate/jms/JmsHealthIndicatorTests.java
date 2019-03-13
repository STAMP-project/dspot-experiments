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
package org.springframework.boot.actuate.jms;


import Status.DOWN;
import Status.UP;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link JmsHealthIndicator}.
 *
 * @author Stephane Nicoll
 */
public class JmsHealthIndicatorTests {
    @Test
    public void jmsBrokerIsUp() throws JMSException {
        ConnectionMetaData connectionMetaData = Mockito.mock(ConnectionMetaData.class);
        BDDMockito.given(connectionMetaData.getJMSProviderName()).willReturn("JMS test provider");
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.getMetaData()).willReturn(connectionMetaData);
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        JmsHealthIndicator indicator = new JmsHealthIndicator(connectionFactory);
        Health health = indicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("provider")).isEqualTo("JMS test provider");
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void jmsBrokerIsDown() throws JMSException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willThrow(new JMSException("test", "123"));
        JmsHealthIndicator indicator = new JmsHealthIndicator(connectionFactory);
        Health health = indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("provider")).isNull();
    }

    @Test
    public void jmsBrokerCouldNotRetrieveProviderMetadata() throws JMSException {
        ConnectionMetaData connectionMetaData = Mockito.mock(ConnectionMetaData.class);
        BDDMockito.given(connectionMetaData.getJMSProviderName()).willThrow(new JMSException("test", "123"));
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.getMetaData()).willReturn(connectionMetaData);
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        JmsHealthIndicator indicator = new JmsHealthIndicator(connectionFactory);
        Health health = indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("provider")).isNull();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void jmsBrokerUsesFailover() throws JMSException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        ConnectionMetaData connectionMetaData = Mockito.mock(ConnectionMetaData.class);
        BDDMockito.given(connectionMetaData.getJMSProviderName()).willReturn("JMS test provider");
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.getMetaData()).willReturn(connectionMetaData);
        BDDMockito.willThrow(new JMSException("Could not start", "123")).given(connection).start();
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        JmsHealthIndicator indicator = new JmsHealthIndicator(connectionFactory);
        Health health = indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("provider")).isNull();
    }

    @Test
    public void whenConnectionStartIsUnresponsiveStatusIsDown() throws JMSException {
        ConnectionMetaData connectionMetaData = Mockito.mock(ConnectionMetaData.class);
        BDDMockito.given(connectionMetaData.getJMSProviderName()).willReturn("JMS test provider");
        Connection connection = Mockito.mock(Connection.class);
        JmsHealthIndicatorTests.UnresponsiveStartAnswer unresponsiveStartAnswer = new JmsHealthIndicatorTests.UnresponsiveStartAnswer();
        BDDMockito.willAnswer(unresponsiveStartAnswer).given(connection).start();
        BDDMockito.willAnswer(( invocation) -> {
            unresponsiveStartAnswer.connectionClosed();
            return null;
        }).given(connection).close();
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        JmsHealthIndicator indicator = new JmsHealthIndicator(connectionFactory);
        Health health = indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection closed");
    }

    private static final class UnresponsiveStartAnswer implements Answer<Void> {
        private boolean connectionClosed = false;

        private final Object monitor = new Object();

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            synchronized(this.monitor) {
                while (!(this.connectionClosed)) {
                    this.monitor.wait();
                } 
            }
            throw new JMSException("Connection closed");
        }

        private void connectionClosed() {
            synchronized(this.monitor) {
                this.connectionClosed = true;
                this.monitor.notifyAll();
            }
        }
    }
}

