/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.amqp.processors;


import AbstractAMQPProcessor.CLIENT_AUTH;
import AbstractAMQPProcessor.HOST;
import AbstractAMQPProcessor.PASSWORD;
import AbstractAMQPProcessor.PORT;
import AbstractAMQPProcessor.SSL_CONTEXT_SERVICE;
import AbstractAMQPProcessor.USER;
import AbstractAMQPProcessor.USE_CERT_AUTHENTICATION;
import com.rabbitmq.client.Connection;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for the AbstractAMQPProcessor class
 */
public class AbstractAMQPProcessorTest {
    AbstractAMQPProcessorTest.MockAbstractAMQPProcessor processor;

    private TestRunner testRunner;

    @Test(expected = IllegalStateException.class)
    public void testConnectToCassandraWithSSLBadClientAuth() throws Exception {
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        testRunner.addControllerService("ssl-context", sslService);
        testRunner.enableControllerService(sslService);
        testRunner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        testRunner.setProperty(USE_CERT_AUTHENTICATION, "false");
        testRunner.setProperty(HOST, "test");
        testRunner.setProperty(PORT, "9999");
        testRunner.setProperty(USER, "test");
        testRunner.setProperty(PASSWORD, "test");
        testRunner.assertValid(sslService);
        testRunner.setProperty(CLIENT_AUTH, "BAD");
        processor.onTrigger(testRunner.getProcessContext(), testRunner.getProcessSessionFactory());
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidSSLConfiguration() throws Exception {
        // it's invalid to have use_cert_auth enabled and not have the SSL Context Service configured
        testRunner.setProperty(USE_CERT_AUTHENTICATION, "true");
        testRunner.setProperty(HOST, "test");
        testRunner.setProperty(PORT, "9999");
        testRunner.setProperty(USER, "test");
        testRunner.setProperty(PASSWORD, "test");
        processor.onTrigger(testRunner.getProcessContext(), testRunner.getProcessSessionFactory());
    }

    /**
     * Provides a stubbed processor instance for testing
     */
    public static class MockAbstractAMQPProcessor extends AbstractAMQPProcessor<AMQPConsumer> {
        @Override
        protected void processResource(Connection connection, AMQPConsumer consumer, ProcessContext context, ProcessSession session) throws ProcessException {
            // nothing to do
        }

        @Override
        protected AMQPConsumer createAMQPWorker(ProcessContext context, Connection connection) {
            return null;
        }
    }
}

