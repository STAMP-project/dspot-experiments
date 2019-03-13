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


import HCatConstants.HCAT_ADD_PARTITION_EVENT;
import HCatConstants.HCAT_ALTER_PARTITION_EVENT;
import HCatConstants.HCAT_ALTER_TABLE_EVENT;
import HCatConstants.HCAT_CREATE_DATABASE_EVENT;
import HCatConstants.HCAT_CREATE_TABLE_EVENT;
import HCatConstants.HCAT_DROP_DATABASE_EVENT;
import HCatConstants.HCAT_DROP_PARTITION_EVENT;
import HCatConstants.HCAT_DROP_TABLE_EVENT;
import PartitionEventType.LOAD_DONE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.MessageListener;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.junit.Test;


public class TestNotificationListener extends HCatBaseTest implements MessageListener {
    private List<String> actualMessages = new Vector<String>();

    private static final int MSG_RECEIVED_TIMEOUT = 30;

    private static final List<String> expectedMessages = Arrays.asList(HCAT_CREATE_DATABASE_EVENT, HCAT_CREATE_TABLE_EVENT, HCAT_ADD_PARTITION_EVENT, HCAT_ALTER_PARTITION_EVENT, HCAT_DROP_PARTITION_EVENT, HCAT_ALTER_TABLE_EVENT, HCAT_DROP_TABLE_EVENT, HCAT_DROP_DATABASE_EVENT);

    private static final CountDownLatch messageReceivedSignal = new CountDownLatch(TestNotificationListener.expectedMessages.size());

    @Test
    public void testAMQListener() throws Exception {
        driver.run("create database mydb");
        driver.run("use mydb");
        driver.run("create table mytbl (a string) partitioned by (b string)");
        driver.run("alter table mytbl add partition(b='2011')");
        Map<String, String> kvs = new HashMap<String, String>(1);
        kvs.put("b", "2011");
        client.markPartitionForEvent("mydb", "mytbl", kvs, LOAD_DONE);
        driver.run("alter table mytbl partition (b='2011') set fileformat orc");
        driver.run("alter table mytbl drop partition(b='2011')");
        driver.run("alter table mytbl add columns (c int comment 'this is an int', d decimal(3,2))");
        driver.run("drop table mytbl");
        driver.run("drop database mydb");
        // Wait until either all messages are processed or a maximum time limit is reached.
        TestNotificationListener.messageReceivedSignal.await(TestNotificationListener.MSG_RECEIVED_TIMEOUT, TimeUnit.SECONDS);
    }
}

