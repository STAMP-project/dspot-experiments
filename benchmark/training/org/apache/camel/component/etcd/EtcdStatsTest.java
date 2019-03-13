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
package org.apache.camel.component.etcd;


import EtcdConstants.ETCD_LEADER_STATS_PATH;
import EtcdConstants.ETCD_SELF_STATS_PATH;
import EtcdConstants.ETCD_STORE_STATS_PATH;
import mousio.etcd4j.responses.EtcdLeaderStatsResponse;
import mousio.etcd4j.responses.EtcdSelfStatsResponse;
import mousio.etcd4j.responses.EtcdStoreStatsResponse;
import org.junit.Test;


public class EtcdStatsTest extends EtcdTestSupport {
    @Test
    public void testStats() throws Exception {
        testStatsConsumer("mock:stats-leader-consumer", ETCD_LEADER_STATS_PATH, EtcdLeaderStatsResponse.class);
        testStatsConsumer("mock:stats-self-consumer", ETCD_SELF_STATS_PATH, EtcdSelfStatsResponse.class);
        testStatsConsumer("mock:stats-store-consumer", ETCD_STORE_STATS_PATH, EtcdStoreStatsResponse.class);
        testStatsProducer("direct:stats-leader", "mock:stats-leader-producer", ETCD_LEADER_STATS_PATH, EtcdLeaderStatsResponse.class);
        testStatsProducer("direct:stats-self", "mock:stats-self-producer", ETCD_SELF_STATS_PATH, EtcdSelfStatsResponse.class);
        testStatsProducer("direct:stats-store", "mock:stats-store-producer", ETCD_STORE_STATS_PATH, EtcdStoreStatsResponse.class);
    }
}

