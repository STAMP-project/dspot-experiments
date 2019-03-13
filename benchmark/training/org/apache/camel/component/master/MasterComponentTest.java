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
package org.apache.camel.component.master;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MasterComponentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterComponentTest.class);

    private static final List<String> INSTANCES = IntStream.range(0, 3).mapToObj(Integer::toString).collect(Collectors.toList());

    private static final List<String> RESULTS = new ArrayList<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(MasterComponentTest.INSTANCES.size());

    private static final CountDownLatch LATCH = new CountDownLatch(MasterComponentTest.INSTANCES.size());

    @Test
    public void test() throws Exception {
        for (String instance : MasterComponentTest.INSTANCES) {
            MasterComponentTest.SCHEDULER.submit(() -> MasterComponentTest.run(instance));
        }
        MasterComponentTest.LATCH.await(1, TimeUnit.MINUTES);
        MasterComponentTest.SCHEDULER.shutdownNow();
        Assert.assertEquals(MasterComponentTest.INSTANCES.size(), MasterComponentTest.RESULTS.size());
        Assert.assertTrue(MasterComponentTest.RESULTS.containsAll(MasterComponentTest.INSTANCES));
    }
}

