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
package org.apache.dubbo.metadata.store.redis;


import org.junit.jupiter.api.Test;
import redis.embedded.RedisServer;


/**
 * 2018/10/9
 */
public class RedisMetadataReportTest {
    RedisMetadataReport redisMetadataReport;

    RedisMetadataReport syncRedisMetadataReport;

    RedisServer redisServer;

    @Test
    public void testAsyncStoreProvider() throws ClassNotFoundException {
        testStoreProvider(redisMetadataReport, "1.0.0.redis.md.p1", 3000);
    }

    @Test
    public void testSyncStoreProvider() throws ClassNotFoundException {
        testStoreProvider(syncRedisMetadataReport, "1.0.0.redis.md.p2", 3);
    }

    @Test
    public void testAsyncStoreConsumer() throws ClassNotFoundException {
        testStoreConsumer(redisMetadataReport, "1.0.0.redis.md.c1", 3000);
    }

    @Test
    public void testSyncStoreConsumer() throws ClassNotFoundException {
        testStoreConsumer(syncRedisMetadataReport, "1.0.0.redis.md.c2", 3);
    }
}

