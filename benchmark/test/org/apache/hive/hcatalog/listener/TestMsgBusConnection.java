/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.listener;


import HCatConstants.HCAT_CREATE_DATABASE_EVENT;
import HCatConstants.HCAT_DROP_DATABASE_EVENT;
import HCatConstants.HCAT_EVENT;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hive.hcatalog.messaging.HCatEventMessage;
import org.apache.hive.hcatalog.messaging.jms.MessagingUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestMsgBusConnection {
    private IDriver driver;

    private BrokerService broker;

    private MessageConsumer consumer;

    private static final int TIMEOUT = 2000;

    @Test
    public void testConnection() throws Exception {
        driver.run("create database testconndb");
        Message msg = consumer.receive(TestMsgBusConnection.TIMEOUT);
        Assert.assertTrue("Expected TextMessage", (msg instanceof TextMessage));
        Assert.assertEquals(HCAT_CREATE_DATABASE_EVENT, msg.getStringProperty(HCAT_EVENT));
        Assert.assertEquals("topic://planetlab.hcat", msg.getJMSDestination().toString());
        HCatEventMessage messageObject = MessagingUtils.getMessage(msg);
        Assert.assertEquals("testconndb", messageObject.getDB());
        broker.stop();
        runQuery("drop database testconndb cascade");
        broker.start(true);
        connectClient();
        runQuery("create database testconndb");
        msg = consumer.receive(TestMsgBusConnection.TIMEOUT);
        Assert.assertEquals(HCAT_CREATE_DATABASE_EVENT, msg.getStringProperty(HCAT_EVENT));
        Assert.assertEquals("topic://planetlab.hcat", msg.getJMSDestination().toString());
        Assert.assertEquals("testconndb", messageObject.getDB());
        driver.run("drop database testconndb cascade");
        msg = consumer.receive(TestMsgBusConnection.TIMEOUT);
        Assert.assertEquals(HCAT_DROP_DATABASE_EVENT, msg.getStringProperty(HCAT_EVENT));
        Assert.assertEquals("topic://planetlab.hcat", msg.getJMSDestination().toString());
        Assert.assertEquals("testconndb", messageObject.getDB());
    }
}

