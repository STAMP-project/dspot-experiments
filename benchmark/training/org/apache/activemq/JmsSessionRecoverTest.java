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
package org.apache.activemq;


import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;


/**
 * Testcases to see if Session.recover() work.
 */
public class JmsSessionRecoverTest extends TestCase {
    private Connection connection;

    private ActiveMQConnectionFactory factory;

    private Destination dest;

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testQueueSynchRecover() throws InterruptedException, JMSException {
        dest = new ActiveMQQueue(("Queue-" + (System.currentTimeMillis())));
        doTestSynchRecover();
    }

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testQueueAsynchRecover() throws InterruptedException, JMSException {
        dest = new ActiveMQQueue(("Queue-" + (System.currentTimeMillis())));
        doTestAsynchRecover();
    }

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testTopicSynchRecover() throws InterruptedException, JMSException {
        dest = new ActiveMQTopic(("Topic-" + (System.currentTimeMillis())));
        doTestSynchRecover();
    }

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testTopicAsynchRecover() throws InterruptedException, JMSException {
        dest = new ActiveMQTopic(("Topic-" + (System.currentTimeMillis())));
        doTestAsynchRecover();
    }

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testQueueAsynchRecoverWithAutoAck() throws InterruptedException, JMSException {
        dest = new ActiveMQQueue(("Queue-" + (System.currentTimeMillis())));
        doTestAsynchRecoverWithAutoAck();
    }

    /**
     *
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    public void testTopicAsynchRecoverWithAutoAck() throws InterruptedException, JMSException {
        dest = new ActiveMQTopic(("Topic-" + (System.currentTimeMillis())));
        doTestAsynchRecoverWithAutoAck();
    }
}

