/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common.admin;


import org.apache.rocketmq.remoting.protocol.RemotingSerializable;
import org.junit.Test;


public class TopicStatsTableTest {
    private volatile TopicStatsTable topicStatsTable;

    private static final String TEST_TOPIC = "test_topic";

    private static final String TEST_BROKER = "test_broker";

    private static final int QUEUE_ID = 1;

    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    private static final long MAX_OFFSET = (TopicStatsTableTest.CURRENT_TIME_MILLIS) + 100;

    private static final long MIN_OFFSET = (TopicStatsTableTest.CURRENT_TIME_MILLIS) - 100;

    @Test
    public void testGetOffsetTable() throws Exception {
        TopicStatsTableTest.validateTopicStatsTable(topicStatsTable);
    }

    @Test
    public void testFromJson() throws Exception {
        String json = RemotingSerializable.toJson(topicStatsTable, true);
        TopicStatsTable fromJson = RemotingSerializable.fromJson(json, TopicStatsTable.class);
        TopicStatsTableTest.validateTopicStatsTable(fromJson);
    }
}

