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
import junit.framework.TestCase;


public class SslTransportServerTest extends TestCase {
    private SslTransportServer sslTransportServer;

    private StubSSLServerSocket sslServerSocket;

    public void testWantAndNeedClientAuthSetters() throws IOException {
        for (int i = 0; i < 4; ++i) {
            String options = "";
            singleTest(i, options);
        }
    }

    public void testWantAndNeedAuthReflection() throws IOException {
        for (int i = 0; i < 4; ++i) {
            String options = (("wantClientAuth=" + (getWantClientAuth(i) ? "true" : "false")) + "&needClientAuth=") + (getNeedClientAuth(i) ? "true" : "false");
            singleTest(i, options);
        }
    }
}

