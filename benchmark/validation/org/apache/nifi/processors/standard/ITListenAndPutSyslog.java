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
package org.apache.nifi.processors.standard;


import ListenSyslog.SSL_CONTEXT_SERVICE;
import ListenSyslog.TCP_VALUE;
import ListenSyslog.UDP_VALUE;
import java.io.IOException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests PutSyslog sending messages to ListenSyslog to simulate a syslog server forwarding
 * to ListenSyslog, or PutSyslog sending to a syslog server.
 */
public class ITListenAndPutSyslog {
    static final Logger LOGGER = LoggerFactory.getLogger(ITListenAndPutSyslog.class);

    private ListenSyslog listenSyslog;

    private TestRunner listenSyslogRunner;

    private PutSyslog putSyslog;

    private TestRunner putSyslogRunner;

    @Test
    public void testUDP() throws IOException, InterruptedException {
        run(UDP_VALUE.getValue(), 5, 5);
    }

    @Test
    public void testTCP() throws IOException, InterruptedException {
        run(TCP_VALUE.getValue(), 5, 5);
    }

    @Test
    public void testTLS() throws IOException, InterruptedException, InitializationException {
        configureSSLContextService(listenSyslogRunner);
        listenSyslogRunner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        configureSSLContextService(putSyslogRunner);
        putSyslogRunner.setProperty(PutSyslog.SSL_CONTEXT_SERVICE, "ssl-context");
        run(TCP_VALUE.getValue(), 7, 7);
    }

    @Test
    public void testTLSListenerNoTLSPut() throws IOException, InterruptedException, InitializationException {
        configureSSLContextService(listenSyslogRunner);
        listenSyslogRunner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        // send 7 but expect 0 because sender didn't use TLS
        run(TCP_VALUE.getValue(), 7, 0);
    }
}

