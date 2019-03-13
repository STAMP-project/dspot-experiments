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
package org.apache.camel.itest.jms;


import org.apache.camel.Body;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsResequencerTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsResequencerTest.class);

    private JmsResequencerTest.ReusableBean b1 = new JmsResequencerTest.ReusableBean("myBean1");

    private JmsResequencerTest.ReusableBean b2 = new JmsResequencerTest.ReusableBean("myBean2");

    private JmsResequencerTest.ReusableBean b3 = new JmsResequencerTest.ReusableBean("myBean3");

    private MockEndpoint resultEndpoint;

    @Test
    public void testSendMessagesInWrongOrderButReceiveThemInCorrectOrder() throws Exception {
        sendAndVerifyMessages("activemq:queue:batch");
    }

    @Test
    public void testSendMessageToStream() throws Exception {
        sendAndVerifyMessages("activemq:queue:stream");
    }

    public class ReusableBean {
        public String body;

        private String name;

        public ReusableBean(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "MyBean:" + (name);
        }

        public void read(@Body
        String body) {
            this.body = body;
            JmsResequencerTest.LOG.info((((((name) + " read() method on ") + (this)) + " with body: ") + body));
        }

        public void execute() {
            JmsResequencerTest.LOG.info(((name) + " started"));
            JmsResequencerTest.LOG.info(((name) + " finished"));
        }
    }
}

