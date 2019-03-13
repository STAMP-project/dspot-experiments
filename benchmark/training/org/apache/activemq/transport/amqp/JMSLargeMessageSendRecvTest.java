/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp;


import javax.jms.JMSException;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class JMSLargeMessageSendRecvTest extends AmqpClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSLargeMessageSendRecvTest.class);

    @Rule
    public TestName testName = new TestName();

    public JMSLargeMessageSendRecvTest(String connectorScheme, boolean secure) {
        super(connectorScheme, secure);
    }

    @Test(timeout = 60 * 1000)
    public void testSendSmallerTextMessage() throws JMSException {
        doTestSendTextMessageOfGivenSize(1024);
    }

    @Test(timeout = 60 * 1000)
    public void testSendSeriesOfSmallerTextMessages() throws JMSException {
        for (int i = 512; i <= (8 * 1024); i += 512) {
            doTestSendTextMessageOfGivenSize(i);
        }
    }

    @Test(timeout = 60 * 1000)
    public void testSendFixedSizedTextMessages() throws JMSException {
        doTestSendTextMessageOfGivenSize(65536);
        doTestSendTextMessageOfGivenSize((65536 * 2));
        doTestSendTextMessageOfGivenSize((65536 * 4));
    }

    @Test(timeout = 60 * 1000)
    public void testSendHugeTextMessage() throws JMSException {
        doTestSendTextMessageOfGivenSize(((1024 * 1024) * 5));
    }

    @Test(timeout = 60 * 1000)
    public void testSendSmallerBytesMessage() throws JMSException {
        doTestSendBytesMessageOfGivenSize(1024);
    }

    @Test(timeout = 60 * 1000)
    public void testSendSeriesOfSmallerBytesMessages() throws JMSException {
        for (int i = 512; i <= (8 * 1024); i += 512) {
            doTestSendBytesMessageOfGivenSize(i);
        }
    }

    @Test(timeout = 60 * 1000)
    public void testSendFixedSizedBytesMessages() throws JMSException {
        doTestSendBytesMessageOfGivenSize(65536);
        doTestSendBytesMessageOfGivenSize((65536 * 2));
        doTestSendBytesMessageOfGivenSize((65536 * 4));
    }

    @Test(timeout = 60 * 1000)
    public void testSendHugeBytesMessage() throws JMSException {
        doTestSendBytesMessageOfGivenSize(((1024 * 1024) * 5));
    }
}

