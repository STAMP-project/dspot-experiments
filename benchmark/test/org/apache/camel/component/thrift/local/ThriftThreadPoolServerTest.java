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
package org.apache.camel.component.thrift.local;


import TSSLTransportFactory.TSSLTransportParameters;
import org.apache.camel.component.thrift.ThriftProducerSecurityTest;
import org.apache.camel.component.thrift.generated.Calculator;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TBD
 */
public class ThriftThreadPoolServerTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftProducerSecurityTest.class);

    private static final int THRIFT_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int THRIFT_TEST_NUM1 = 12;

    private static final int THRIFT_TEST_NUM2 = 13;

    private static final String TRUST_STORE_PATH = "src/test/resources/certs/truststore.jks";

    private static final String KEY_STORE_PATH = "src/test/resources/certs/keystore.jks";

    private static final String SECURITY_STORE_PASSWORD = "camelinaction";

    private static final int THRIFT_CLIENT_TIMEOUT = 2000;

    private static TServerSocket serverTransport;

    private static TTransport clientTransport;

    private static TServer server;

    private static TProtocol protocol;

    @SuppressWarnings({ "rawtypes" })
    private static Calculator.Processor processor;

    @Test
    public void clientConnectionTest() throws TException {
        TSSLTransportFactory.TSSLTransportParameters sslParams = new TSSLTransportFactory.TSSLTransportParameters();
        sslParams.setTrustStore(ThriftThreadPoolServerTest.TRUST_STORE_PATH, ThriftThreadPoolServerTest.SECURITY_STORE_PASSWORD);
        ThriftThreadPoolServerTest.clientTransport = TSSLTransportFactory.getClientSocket("localhost", ThriftThreadPoolServerTest.THRIFT_TEST_PORT, 1000, sslParams);
        ThriftThreadPoolServerTest.protocol = new org.apache.thrift.protocol.TBinaryProtocol(ThriftThreadPoolServerTest.clientTransport);
        Calculator.Client client = new Calculator.Client(ThriftThreadPoolServerTest.protocol);
        int addResult = client.add(ThriftThreadPoolServerTest.THRIFT_TEST_NUM1, ThriftThreadPoolServerTest.THRIFT_TEST_NUM2);
        assertEquals(addResult, ((ThriftThreadPoolServerTest.THRIFT_TEST_NUM1) + (ThriftThreadPoolServerTest.THRIFT_TEST_NUM2)));
    }
}

