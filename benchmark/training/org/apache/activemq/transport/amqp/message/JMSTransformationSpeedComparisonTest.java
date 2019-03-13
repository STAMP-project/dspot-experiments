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
package org.apache.activemq.transport.amqp.message;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.transport.amqp.JMSInteroperabilityTest;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Some simple performance tests for the Message Transformers.
 */
@Ignore("Enable for profiling")
@RunWith(Parameterized.class)
public class JMSTransformationSpeedComparisonTest {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSInteroperabilityTest.class);

    @Rule
    public TestName test = new TestName();

    private final String transformer;

    private final int WARM_CYCLES = 10000;

    private final int PROFILE_CYCLES = 1000000;

    public JMSTransformationSpeedComparisonTest(String transformer) {
        this.transformer = transformer;
    }

    @Test
    public void testBodyOnlyMessage() throws Exception {
        Message message = Proton.message();
        message.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        EncodedMessage encoded = encode(message);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }

    @Test
    public void testMessageWithNoPropertiesOrAnnotations() throws Exception {
        Message message = Proton.message();
        message.setAddress("queue://test-queue");
        message.setDeliveryCount(1);
        message.setCreationTime(System.currentTimeMillis());
        message.setContentType("text/plain");
        message.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        EncodedMessage encoded = encode(message);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }

    @Test
    public void testTypicalQpidJMSMessage() throws Exception {
        EncodedMessage encoded = encode(createTypicalQpidJMSMessage());
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }

    @Test
    public void testComplexQpidJMSMessage() throws Exception {
        EncodedMessage encoded = encode(createComplexQpidJMSMessage());
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
            intermediate.onSend();
            outboundTransformer.transform(intermediate);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }

    @Test
    public void testTypicalQpidJMSMessageInBoundOnly() throws Exception {
        EncodedMessage encoded = encode(createTypicalQpidJMSMessage());
        InboundTransformer inboundTransformer = getInboundTransformer();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            inboundTransformer.transform(encoded);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            inboundTransformer.transform(encoded);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }

    @Test
    public void testTypicalQpidJMSMessageOutBoundOnly() throws Exception {
        EncodedMessage encoded = encode(createTypicalQpidJMSMessage());
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage outbound = inboundTransformer.transform(encoded);
        outbound.onSend();
        // Warm up
        for (int i = 0; i < (WARM_CYCLES); ++i) {
            outboundTransformer.transform(outbound);
        }
        long totalDuration = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < (PROFILE_CYCLES); ++i) {
            outboundTransformer.transform(outbound);
        }
        totalDuration += (System.nanoTime()) - startTime;
        JMSTransformationSpeedComparisonTest.LOG.info("[{}] Total time for {} cycles of transforms = {} ms  -> [{}]", transformer, PROFILE_CYCLES, TimeUnit.NANOSECONDS.toMillis(totalDuration), test.getMethodName());
    }
}

