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
package org.apache.activemq.transport.amqp;


import java.util.List;
import java.util.Vector;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMSClientRequestResponseTest extends AmqpTestSupport implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(JMSClientRequestResponseTest.class);

    private Connection requestorConnection;

    private Destination requestDestination;

    private Session requestorSession;

    private Connection responderConnection;

    private MessageProducer responseProducer;

    private Session responderSession;

    private Destination replyDestination;

    private final List<JMSException> failures = new Vector<JMSException>();

    private boolean dynamicallyCreateProducer;

    private final boolean useAsyncConsumer = true;

    private Thread syncThread;

    @Test(timeout = 60000)
    public void testRequestResponseToTempQueue() throws Exception {
        doSetupConnections(false);
        doTestRequestResponse();
    }

    @Test(timeout = 60000)
    public void testRequestResponseToTempTopic() throws Exception {
        doSetupConnections(true);
        doTestRequestResponse();
    }
}

