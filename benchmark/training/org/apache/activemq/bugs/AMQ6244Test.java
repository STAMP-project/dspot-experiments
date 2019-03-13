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
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import com.google.common.base.Throwables;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.DataFormatException;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class AMQ6244Test {
    public static final byte[] ORIG_MSG_CONTENT = AMQ6244Test.randomByteArray();

    @Rule
    public TestName name = new TestName();

    @Rule
    public EmbeddedActiveMQBroker brokerRule = new EmbeddedActiveMQBroker();

    public AMQ6244Test() {
        brokerRule.setBrokerName(this.getClass().getName());
    }

    @Test
    public void bytesMsgCompressedFlagTest() throws Exception {
        final ActiveMQConnection compressionEnabledConnection = AMQ6244Test.createConnection(brokerRule.getVmURL(), true);
        final ActiveMQConnection compressionDisabledConnection = AMQ6244Test.createConnection(brokerRule.getVmURL(), false);
        // Consumer (compression=false)
        final Session consumerSession = compressionDisabledConnection.createSession(false, AUTO_ACKNOWLEDGE);
        final Queue destination = consumerSession.createQueue(name.getMethodName());
        final MessageConsumer consumer = consumerSession.createConsumer(destination);
        // Producer (compression=false)
        final Session compressionDisabledProducerSession = compressionDisabledConnection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageProducer compressionDisabledProducer = compressionDisabledProducerSession.createProducer(destination);
        // Producer (compression=true)
        final Session compressionEnabledProducerSession = compressionEnabledConnection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageProducer compressionEnabledProducer = compressionEnabledProducerSession.createProducer(destination);
        try {
            /* Publish a BytesMessage on the compressed connection */
            final ActiveMQBytesMessage originalCompressedMsg = ((ActiveMQBytesMessage) (compressionEnabledProducerSession.createBytesMessage()));
            originalCompressedMsg.writeBytes(AMQ6244Test.ORIG_MSG_CONTENT);
            Assert.assertFalse(originalCompressedMsg.isReadOnlyBody());
            // send first message
            compressionEnabledProducer.send(originalCompressedMsg);
            Assert.assertEquals("Once sent, the Message's 'compressed' flag should match the 'useCompression' flag on the Producer's Connection", compressionEnabledConnection.isUseCompression(), originalCompressedMsg.isCompressed());
            /* Consume the compressed message and resend it decompressed */
            final ActiveMQBytesMessage compressedMsg = receiveMsg(consumer, originalCompressedMsg);
            validateMsgContent(compressedMsg);
            // make message writable so the client can reuse it
            AMQ6244Test.makeWritable(compressedMsg);
            compressedMsg.setStringProperty(this.getClass().getName(), "test");
            compressionDisabledProducer.send(compressedMsg);
            /* AMQ-6244 ERROR STATE 1: Produced Message is marked 'compressed' when its contents are not compressed */
            Assert.assertEquals("AMQ-6244 Error State Achieved: Produced Message's 'compressed' flag is enabled after message is published on a connection with 'useCompression=false'", compressionDisabledConnection.isUseCompression(), compressedMsg.isCompressed());
            /* AMQ-6244 ERROR STATE 2: Consumer cannot handle Message marked 'compressed' when its contents are not compressed */
            try {
                final ActiveMQBytesMessage uncompressedMsg = receiveMsg(consumer, compressedMsg);
                validateMsgContent(uncompressedMsg);
            } catch (JMSException jmsE) {
                final Throwable rootCause = Throwables.getRootCause(jmsE);
                if ((rootCause instanceof DataFormatException) || (rootCause instanceof NegativeArraySizeException)) {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    jmsE.printStackTrace(pw);
                    Assert.fail(("AMQ-6244 Error State Achieved: Attempted to decompress BytesMessage contents that are not compressed\n" + (sw.toString())));
                } else {
                    throw jmsE;
                }
            }
        } finally {
            compressionEnabledProducerSession.close();
            compressionEnabledConnection.close();
            consumerSession.close();
            compressionDisabledProducerSession.close();
            compressionDisabledConnection.close();
        }
    }
}

