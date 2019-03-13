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
package org.apache.activemq.transport.stomp;


import javax.jms.Connection;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


/**
 * Testcase for AMQ-6526.
 * Checks if the \<Unknown\> in the Stomp ProtocolException is replaced
 * with the proper Stomp operation.
 */
public class StompNIOSSLLargeMessageTest extends StompTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(StompNIOSSLLargeMessageTest.class);

    private Connection connection;

    private Session session;

    private ActiveMQQueue queue;

    // flag to control if the bug in AMQ-XXXX got reproduced.
    private boolean gotUnknownOperationInLog = false;

    protected int stompFrameSize = 110000000;// slightly over 105 MB


    // custom Log4J appender so we can filter the logging output in this test.
    protected Appender appender = new DefaultTestAppender() {
        // @Override
        @Override
        public void doAppend(LoggingEvent event) {
            if ((event.getMessage().toString().contains("<Unknown>")) && (event.getMessage().toString().contains("The maximum data length was exceeded"))) {
                gotUnknownOperationInLog = true;
            }
        }
    };

    /**
     * Sends a Stomp message larger than maxDataLength bytes.
     * Expects to receive an exception from the broker.
     * The broker will throw an Stomp ProtocolException of type
     * "Exception occurred processing: SEND ->
     * org.apache.activemq.transport.stomp.ProtocolException:
     * The maximum data length was exceeded"
     *
     * Before bug AMQ-6526 this exception would contain \<Unkown\> for the
     * operation name. With the fix it should print the Stomp operation.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 6000000)
    public void testSendMessageBytes() throws Exception {
        String frame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "accept-version:1.1") + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (("SEND\n" + (((((("value:newest" + "\n") + "value:older") + "\n") + "value:oldest") + "\n") + "destination:/queue/")) + (getQueueName())) + "\n\n";
        byte[] buffer = createLargeByteBuffer(stompFrameSize);
        try {
            stompConnection.sendFrame(frame, buffer);
        } catch (Exception ex) {
            StompNIOSSLLargeMessageTest.LOG.error(ex.getMessage());
        }
        Assert.assertFalse("Stomp ProtocolException still contains <Unknown> operation.", gotUnknownOperationInLog);
    }
}

