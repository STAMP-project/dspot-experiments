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
package org.apache.rocketmq.common.stats;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Test;


public class StatsItemSetTest {
    private ThreadPoolExecutor executor;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Test
    public void test_getAndCreateStatsItem_multiThread() throws InterruptedException {
        Assert.assertEquals(20L, test_unit().longValue());
    }

    @Test
    public void test_getAndCreateMomentStatsItem_multiThread() throws InterruptedException {
        Assert.assertEquals(10, test_unit_moment().longValue());
    }
}

