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
package org.apache.activemq.perf;


import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SimpleTopicTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTopicTest.class);

    protected BrokerService broker;

    protected String clientURI = "tcp://localhost:61616?wireFormat.cacheEnabled=true&wireFormat.tightEncodingEnabled=true&jms.useAsyncSend=false&wireFormat.maxInactivityDuration=0";

    // protected String clientURI="tcp://localhost:61616";
    protected String bindAddress = "tcp://localhost:61616?wireFormat.maxInactivityDuration=0";

    // protected String bindAddress = "tcp://localhost:61616";
    // protected String bindAddress="vm://localhost?marshal=true";
    // protected String bindAddress="vm://localhost";
    protected PerfProducer[] producers;

    protected PerfConsumer[] consumers;

    protected String destinationName = getClass().getName();

    protected int sampleCount = 20;

    protected long sampleInternal = 10000;

    protected int numberOfDestinations = 1;

    protected int numberOfConsumers = 1;

    protected int numberofProducers = 1;

    protected int totalNumberOfProducers;

    protected int totalNumberOfConsumers;

    protected int playloadSize = 12;

    protected byte[] array;

    protected ConnectionFactory factory;

    public void testPerformance() throws InterruptedException, JMSException {
        for (int i = 0; i < (totalNumberOfConsumers); i++) {
            consumers[i].start();
        }
        for (int i = 0; i < (totalNumberOfProducers); i++) {
            producers[i].start();
        }
        SimpleTopicTest.LOG.info((((("Sampling performance " + (sampleCount)) + " times at a ") + (sampleInternal)) + " ms interval."));
        for (int i = 0; i < (sampleCount); i++) {
            Thread.sleep(sampleInternal);
            dumpProducerRate();
            dumpConsumerRate();
        }
        for (int i = 0; i < (totalNumberOfProducers); i++) {
            producers[i].stop();
        }
        for (int i = 0; i < (totalNumberOfConsumers); i++) {
            consumers[i].stop();
        }
    }
}

