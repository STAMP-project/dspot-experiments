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


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.component.thrift.generated.Calculator;
import org.apache.camel.component.thrift.generated.Operation;
import org.apache.camel.component.thrift.generated.Work;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerSocket;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftProducerZlibCompressionTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftProducerZlibCompressionTest.class);

    private static TServerSocket serverTransport;

    private static TServer server;

    @SuppressWarnings({ "rawtypes" })
    private static Calculator.Processor processor;

    private static final int THRIFT_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int THRIFT_TEST_NUM1 = 12;

    private static final int THRIFT_TEST_NUM2 = 13;

    private static final int THRIFT_CLIENT_TIMEOUT = 2000;

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCalculateMethodInvocation() throws Exception {
        ThriftProducerZlibCompressionTest.LOG.info("Thrift calculate method sync test start");
        List requestBody = new ArrayList();
        requestBody.add(1);
        requestBody.add(new Work(ThriftProducerZlibCompressionTest.THRIFT_TEST_NUM1, ThriftProducerZlibCompressionTest.THRIFT_TEST_NUM2, Operation.MULTIPLY));
        Object responseBody = template.requestBody("direct:thrift-zlib-calculate", requestBody);
        assertNotNull(responseBody);
        assertTrue((responseBody instanceof Integer));
        assertEquals(((ThriftProducerZlibCompressionTest.THRIFT_TEST_NUM1) * (ThriftProducerZlibCompressionTest.THRIFT_TEST_NUM2)), responseBody);
    }

    @Test
    public void testVoidMethodInvocation() throws Exception {
        ThriftProducerZlibCompressionTest.LOG.info("Thrift method with empty parameters and void output sync test start");
        Object requestBody = null;
        Object responseBody = template.requestBody("direct:thrift-zlib-ping", requestBody);
        assertNull(responseBody);
    }
}

