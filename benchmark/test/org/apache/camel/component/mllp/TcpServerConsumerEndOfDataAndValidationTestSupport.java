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
package org.apache.camel.component.mllp;


import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit.rule.mllp.MllpClientResource;
import org.apache.camel.test.junit.rule.mllp.MllpJUnitResourceException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.mllp.Hl7TestMessageGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;


public abstract class TcpServerConsumerEndOfDataAndValidationTestSupport extends CamelTestSupport {
    static final int CONNECT_TIMEOUT = 500;

    static final int RECEIVE_TIMEOUT = 1000;

    static final int READ_TIMEOUT = 500;

    @Rule
    public MllpClientResource mllpClient = new MllpClientResource();

    @EndpointInject(uri = "mock://complete")
    MockEndpoint complete;

    @EndpointInject(uri = "mock://failed")
    MockEndpoint failed;

    @EndpointInject(uri = "mock://invalid-ex")
    MockEndpoint invalid;

    int expectedCompleteCount;

    int expectedFailedCount;

    int expectedInvalidCount;

    @Test
    public void testReceiveSingleMessage() throws Exception {
        expectedCompleteCount = 1;
        setExpectedCounts();
        mllpClient.connect();
        mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(), 10000);
    }

    @Test
    public void testReceiveSingleMessageWithDelayAfterConnection() throws Exception {
        expectedCompleteCount = 1;
        setExpectedCounts();
        mllpClient.connect();
        Thread.sleep(5000);
        mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(), 10000);
    }

    @Test
    public void testReceiveMultipleMessages() throws Exception {
        expectedCompleteCount = 5;
        setExpectedCounts();
        mllpClient.connect();
        for (int i = 1; i <= (expectedCompleteCount); ++i) {
            mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(i));
        }
    }

    @Test
    public void testOpenMllpEnvelopeWithReset() throws Exception {
        expectedCompleteCount = 4;
        expectedInvalidCount = 1;
        setExpectedCounts();
        NotifyBuilder notify1 = whenDone(2).create();
        NotifyBuilder notify2 = whenDone(5).create();
        mllpClient.connect();
        mllpClient.setSoTimeout(10000);
        log.info("Sending TEST_MESSAGE_1");
        String acknowledgement1 = mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(1));
        log.info("Sending TEST_MESSAGE_2");
        String acknowledgement2 = mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(2));
        assertTrue("First two normal exchanges did not complete", notify1.matches(TcpServerConsumerEndOfDataAndValidationTestSupport.RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
        log.info("Sending TEST_MESSAGE_3");
        mllpClient.setSendEndOfBlock(false);
        mllpClient.setSendEndOfData(false);
        // Acknowledgement won't come here
        try {
            mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(3));
        } catch (MllpJUnitResourceException resourceEx) {
            log.info("Expected exception reading response");
        }
        mllpClient.disconnect();
        Thread.sleep(1000);
        mllpClient.connect();
        log.info("Sending TEST_MESSAGE_4");
        mllpClient.setSendEndOfBlock(true);
        mllpClient.setSendEndOfData(true);
        String acknowledgement4 = mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(4));
        log.info("Sending TEST_MESSAGE_5");
        String acknowledgement5 = mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(5));
        assertTrue("Remaining exchanges did not complete", notify2.matches(TcpServerConsumerEndOfDataAndValidationTestSupport.RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
        assertTrue("Should be acknowledgment for message 1", acknowledgement1.contains("MSA|AA|00001"));
        assertTrue("Should be acknowledgment for message 2", acknowledgement2.contains("MSA|AA|00002"));
        assertTrue("Should be acknowledgment for message 4", acknowledgement4.contains("MSA|AA|00004"));
        assertTrue("Should be acknowledgment for message 5", acknowledgement5.contains("MSA|AA|00005"));
    }

    @Test
    public void testMessageReadTimeout() throws Exception {
        expectedCompleteCount = 1;
        expectedInvalidCount = 1;
        setExpectedCounts();
        NotifyBuilder oneDone = whenDone(1).create();
        NotifyBuilder twoDone = whenDone(2).create();
        // Send one message to establish the connection and start the ConsumerClientSocketThread
        mllpClient.sendFramedData(Hl7TestMessageGenerator.generateMessage());
        assertTrue("One exchange should have completed", oneDone.matches(TcpServerConsumerEndOfDataAndValidationTestSupport.RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
        mllpClient.setSendEndOfBlock(false);
        mllpClient.setSendEndOfData(false);
        mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Two exchanges should have completed", twoDone.matches(TcpServerConsumerEndOfDataAndValidationTestSupport.RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testInitialMessageReadTimeout() throws Exception {
        expectedCompleteCount = 1;
        setExpectedCounts();
        mllpClient.setSendEndOfBlock(false);
        mllpClient.setSendEndOfData(false);
        log.info("Sending first message");
        mllpClient.sendFramedData(Hl7TestMessageGenerator.generateMessage(10001));
        Thread.sleep(((TcpServerConsumerEndOfDataAndValidationTestSupport.RECEIVE_TIMEOUT) * 5));
        mllpClient.setSendEndOfBlock(true);
        mllpClient.setSendEndOfData(true);
        try {
            log.info("Attempting to send second message");
            String acknowledgement = mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(10002));
            assertEquals("If the send doesn't throw an exception, the acknowledgement should be empty", "", acknowledgement);
        } catch (MllpJUnitResourceException expected) {
            assertThat("If the send throws an exception, the cause should be a SocketException", expected.getCause(), CoreMatchers.instanceOf(SocketException.class));
        }
        mllpClient.disconnect();
        mllpClient.connect();
        log.info("Sending third message");
        mllpClient.sendMessageAndWaitForAcknowledgement(Hl7TestMessageGenerator.generateMessage(10003));
    }

    @Test
    public void testInitialMessageWithoutEndOfDataByte() throws Exception {
        setExpectedCounts();
        mllpClient.setSendEndOfData(false);
        mllpClient.sendFramedData(Hl7TestMessageGenerator.generateMessage());
    }
}

