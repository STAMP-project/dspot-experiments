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
package org.apache.activemq.transport.tcp;


import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.activemq.openwire.OpenWireFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SslTransportFactoryTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SslTransportFactoryTest.class);

    private SslTransportFactory factory;

    private boolean verbose;

    public void testBindServerOptions() throws IOException {
        SslTransportServer sslTransportServer = null;
        for (int i = 0; i < 4; ++i) {
            final boolean wantClientAuth = (i & 1) == 1;
            final boolean needClientAuth = (i & 2) == 1;
            String options = (("wantClientAuth=" + (wantClientAuth ? "true" : "false")) + "&needClientAuth=") + (needClientAuth ? "true" : "false");
            try {
                sslTransportServer = ((SslTransportServer) (factory.doBind(new URI(("ssl://localhost:61616?" + options)))));
            } catch (Exception e) {
                TestCase.fail(("Unable to bind to address: " + (e.getMessage())));
            }
            TestCase.assertEquals("Created ServerSocket did not have correct wantClientAuth status.", sslTransportServer.getWantClientAuth(), wantClientAuth);
            TestCase.assertEquals("Created ServerSocket did not have correct needClientAuth status.", sslTransportServer.getNeedClientAuth(), needClientAuth);
            try {
                sslTransportServer.stop();
            } catch (Exception e) {
                TestCase.fail(("Unable to stop TransportServer: " + (e.getMessage())));
            }
        }
    }

    public void testCompositeConfigure() throws IOException {
        // The 5 options being tested.
        int[] optionSettings = new int[5];
        String[] optionNames = new String[]{ "wantClientAuth", "needClientAuth", "socket.wantClientAuth", "socket.needClientAuth", "socket.useClientMode" };
        // Using a trinary interpretation of i to set all possible values of
        // stub options for socket and transport.
        // 2 transport options, 3 socket options, 3 settings for each option =>
        // 3^5 = 243 combos.
        for (int i = 0; i < 243; ++i) {
            Map<String, String> options = new HashMap<String, String>();
            for (int j = 0; j < 5; ++j) {
                // -1 since the option range is [-1,1], not [0,2].
                optionSettings[j] = (getMthNaryDigit(i, j, 3)) - 1;
                // We now always set options to a default we default verifyHostName to true
                // so we setSSLParameters so make the not set value = 0
                if ((optionSettings[j]) == (-1)) {
                    optionSettings[j] = 0;
                }
                if ((optionSettings[j]) != (-1)) {
                    options.put(optionNames[j], ((optionSettings[j]) == 1 ? "true" : "false"));
                }
            }
            StubSSLSocket socketStub = new StubSSLSocket(null);
            StubSslTransport transport = null;
            try {
                transport = new StubSslTransport(null, socketStub);
            } catch (Exception e) {
                TestCase.fail(("Unable to create StubSslTransport: " + (e.getMessage())));
            }
            if (verbose) {
                SslTransportFactoryTest.LOG.info("");
                SslTransportFactoryTest.LOG.info(("Iteration: " + i));
                SslTransportFactoryTest.LOG.info(("Map settings: " + options));
                for (int x = 0; x < (optionSettings.length); x++) {
                    SslTransportFactoryTest.LOG.info(((("optionSetting[" + x) + "] = ") + (optionSettings[x])));
                }
            }
            factory.compositeConfigure(transport, new OpenWireFormat(), options);
            // lets start the transport to force the introspection
            try {
                start();
            } catch (Exception e) {
                // ignore bad connection
            }
            if ((socketStub.getWantClientAuthStatus()) != (optionSettings[2])) {
                SslTransportFactoryTest.LOG.info("sheiite");
            }
            TestCase.assertEquals(("wantClientAuth was not properly set for iteration: " + i), optionSettings[0], transport.getWantClientAuthStatus());
            TestCase.assertEquals(("needClientAuth was not properly set for iteration: " + i), optionSettings[1], transport.getNeedClientAuthStatus());
            TestCase.assertEquals(("socket.wantClientAuth was not properly set for iteration: " + i), optionSettings[2], socketStub.getWantClientAuthStatus());
            TestCase.assertEquals(("socket.needClientAuth was not properly set for iteration: " + i), optionSettings[3], socketStub.getNeedClientAuthStatus());
            TestCase.assertEquals(("socket.useClientMode was not properly set for iteration: " + i), optionSettings[4], socketStub.getUseClientModeStatus());
        }
    }
}

