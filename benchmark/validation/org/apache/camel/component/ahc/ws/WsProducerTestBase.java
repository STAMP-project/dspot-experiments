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
package org.apache.camel.component.ahc.ws;


import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.AvailablePortFinder;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public abstract class WsProducerTestBase extends Assert {
    protected static final String TEST_MESSAGE = "Hello World!";

    protected static final int PORT = AvailablePortFinder.getNextAvailable();

    protected CamelContext camelContext;

    protected ProducerTemplate template;

    protected Server server;

    @Test
    public void testWriteToWebsocket() throws Exception {
        String testMessage = getTextTestMessage();
        testWriteToWebsocket(testMessage);
        Assert.assertEquals(1, TestMessages.getInstance().getMessages().size());
        verifyMessage(testMessage, TestMessages.getInstance().getMessages().get(0));
    }
}

