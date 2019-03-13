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
package org.apache.camel.component.xmpp;


import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target")
public class XmppRouteTest extends TestCase {
    protected static boolean enabled;

    protected static String xmppUrl;

    private static final Logger LOG = LoggerFactory.getLogger(XmppRouteTest.class);

    protected Exchange receivedExchange;

    protected CamelContext context = new DefaultCamelContext();

    protected CountDownLatch latch = new CountDownLatch(1);

    protected Endpoint endpoint;

    protected ProducerTemplate client;

    private EmbeddedXmppTestServer embeddedXmppTestServer;

    @Test
    public void testXmppRouteWithTextMessage() throws Exception {
        String expectedBody = "Hello there!";
        sendExchange(expectedBody);
        Object body = assertReceivedValidExchange();
        TestCase.assertEquals("body", expectedBody, body);
    }
}

