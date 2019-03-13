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
package org.apache.activemq.jmx;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.OpenTypeSupport;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpenTypeSupportTest {
    private static final Logger LOG = LoggerFactory.getLogger(OpenTypeSupportTest.class);

    private static BrokerService brokerService;

    private static String TESTQUEUE = "testQueue";

    private static ActiveMQConnectionFactory connectionFactory;

    private static String BYTESMESSAGE_TEXT = "This is a short text";

    private static String BROKER_ADDRESS = "tcp://localhost:0";

    private static ActiveMQQueue queue = new ActiveMQQueue(OpenTypeSupportTest.TESTQUEUE);

    private String connectionUri;

    @Test
    public void bytesMessagePreview() throws Exception {
        QueueViewMBean queue = getProxyToQueueViewMBean();
        Assert.assertEquals(extractText(queue.browse()[0]), extractText(queue.browse()[0]));
    }

    @Test
    public void testBrowseByteMessageFails() throws Exception {
        ActiveMQBytesMessage bm = new ActiveMQBytesMessage();
        bm.writeBytes("123456".getBytes());
        Object result = OpenTypeSupport.convert(bm);
        OpenTypeSupportTest.LOG.info(("result : " + result));
    }
}

