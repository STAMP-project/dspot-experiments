/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.thrift;


import ThriftConstants.THRIFT_METHOD_NAME_HEADER;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.thrift.generated.Calculator;
import org.apache.camel.component.thrift.generated.Operation;
import org.apache.camel.component.thrift.generated.Work;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftConsumerZlibCompressionTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftConsumerZlibCompressionTest.class);

    private static final int THRIFT_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int THRIFT_TEST_NUM1 = 12;

    private static final int THRIFT_TEST_NUM2 = 13;

    private static final int THRIFT_CLIENT_TIMEOUT = 2000;

    private static Calculator.Client thriftClient;

    private TProtocol protocol;

    private TTransport transport;

    @Test
    public void testCalculateMethodInvocation() throws Exception {
        ThriftConsumerZlibCompressionTest.LOG.info("Test Calculate method invocation");
        Work work = new Work(ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM1, ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM2, Operation.MULTIPLY);
        int calculateResult = ThriftConsumerZlibCompressionTest.thriftClient.calculate(1, work);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:thrift-secure-service");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(THRIFT_METHOD_NAME_HEADER, "calculate");
        mockEndpoint.assertIsSatisfied();
        assertEquals(((ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM1) * (ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM2)), calculateResult);
    }

    @Test
    public void testEchoMethodInvocation() throws Exception {
        ThriftConsumerZlibCompressionTest.LOG.info("Test Echo method invocation");
        Work echoResult = ThriftConsumerZlibCompressionTest.thriftClient.echo(new Work(ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM1, ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM2, Operation.MULTIPLY));
        MockEndpoint mockEndpoint = getMockEndpoint("mock:thrift-secure-service");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(THRIFT_METHOD_NAME_HEADER, "echo");
        mockEndpoint.assertIsSatisfied();
        assertNotNull(echoResult);
        assertTrue((echoResult instanceof Work));
        assertEquals(ThriftConsumerZlibCompressionTest.THRIFT_TEST_NUM1, echoResult.num1);
        assertEquals(Operation.MULTIPLY, echoResult.op);
    }

    public class CalculatorMessageBuilder {
        public Work echo(Work work) {
            return work.deepCopy();
        }
    }
}

