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
package org.apache.camel.processor;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.service.ServiceHelper;
import org.junit.Test;


public class StreamResequencerTest extends ContextTestSupport {
    @Test
    public void testSendMessagesInWrongOrderButReceiveThemInCorrectOrder() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("msg1", "msg2", "msg3", "msg4");
        sendBodyAndHeader("direct:start", "msg4", "seqnum", 4L);
        sendBodyAndHeader("direct:start", "msg1", "seqnum", 1L);
        sendBodyAndHeader("direct:start", "msg3", "seqnum", 3L);
        sendBodyAndHeader("direct:start", "msg2", "seqnum", 2L);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMultithreaded() throws Exception {
        int numMessages = 100;
        Object[] bodies = new Object[numMessages];
        for (int i = 0; i < numMessages; i++) {
            bodies[i] = "msg" + i;
        }
        getMockEndpoint("mock:result").expectedBodiesReceived(bodies);
        getMockEndpoint("mock:result").setResultWaitTime(20000);
        ProducerTemplate producerTemplate = context.createProducerTemplate();
        ProducerTemplate producerTemplate2 = context.createProducerTemplate();
        ExecutorService service = context.getExecutorServiceManager().newFixedThreadPool(this, getName(), 2);
        service.execute(new StreamResequencerTest.Sender(producerTemplate, 0, numMessages, 2));
        service.execute(new StreamResequencerTest.Sender(producerTemplate2, 1, numMessages, 2));
        assertMockEndpointsSatisfied();
        ServiceHelper.stopService(producerTemplate, producerTemplate2);
    }

    @Test
    public void testStreamResequencerTypeWithJmx() throws Exception {
        doTestStreamResequencerType();
    }

    @Test
    public void testStreamResequencerTypeWithoutJmx() throws Exception {
        doTestStreamResequencerType();
    }

    private static class Sender implements Runnable {
        private final ProducerTemplate template;

        private final int start;

        private final int end;

        private final int increment;

        private final Random random;

        Sender(ProducerTemplate template, int start, int end, int increment) {
            this.template = template;
            this.start = start;
            this.end = end;
            this.increment = increment;
            random = new Random();
        }

        @Override
        public void run() {
            for (long i = start; i < (end); i += increment) {
                try {
                    // let's sleep randomly
                    Thread.sleep(random.nextInt(20));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                template.sendBodyAndHeader("direct:start", ("msg" + i), "seqnum", i);
            }
        }
    }
}

