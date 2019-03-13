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
package org.apache.activemq.spring;


import javax.annotation.Resource;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ListenerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ListenerTest.class);

    int msgNum = 10;

    protected String bindAddress = "vm://localhost";

    @Resource
    Listener listener;

    @Test
    @DirtiesContext
    public void testSimple() throws Exception {
        sendMessages("SIMPLE", msgNum);
        Thread.sleep(3000);
        ListenerTest.LOG.info(("messages received= " + (listener.messages.size())));
        Assert.assertEquals(msgNum, listener.messages.size());
    }

    @Test
    @DirtiesContext
    public void testComposite() throws Exception {
        sendMessages("TEST.1,TEST.2,TEST.3,TEST.4,TEST.5,TEST.6", msgNum);
        Wait.waitFor(new Wait.Condition() {
            public boolean isSatisified() throws Exception {
                return (6 * (msgNum)) == (listener.messages.size());
            }
        });
        ListenerTest.LOG.info(("messages received= " + (listener.messages.size())));
        Assert.assertEquals((6 * (msgNum)), listener.messages.size());
    }
}

