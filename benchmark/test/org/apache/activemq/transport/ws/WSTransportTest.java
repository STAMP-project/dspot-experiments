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
package org.apache.activemq.transport.ws;


import java.io.File;
import org.apache.activemq.transport.stomp.StompConnection;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WSTransportTest extends WSTransportTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(WSTransportTest.class);

    private static final int MESSAGE_COUNT = 1000;

    private Server server;

    private WebDriver driver;

    private File profileDir;

    private String stompUri;

    private StompConnection stompConnection = new StompConnection();

    protected final int port = 61623;

    @Test
    public void testBrokerStart() throws Exception {
        Assert.assertTrue(broker.isStarted());
    }

    @Test(timeout = 10000)
    public void testGet() throws Exception {
        testGet(("http://127.0.0.1:" + (port)), null);
    }
}

