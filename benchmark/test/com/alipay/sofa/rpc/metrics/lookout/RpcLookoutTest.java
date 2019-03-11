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
package com.alipay.sofa.rpc.metrics.lookout;


import com.alipay.lookout.api.Measurement;
import com.alipay.lookout.api.Metric;
import com.alipay.lookout.api.Tag;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import java.lang.reflect.Field;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei.Liangen</a>
 */
public class RpcLookoutTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcLookoutTest.class);

    static Field corePoolSize;

    static Field maxPoolSize;

    static Field queueSize;

    /**
     * test thread pool config
     *
     * @throws Exception
     * 		Exception
     */
    @Test
    public void testThreadPoolConfig() throws Exception {
        Metric metric = fetchWithName("rpc.bolt.threadpool.config");
        Collection<Measurement> measurements = metric.measure().measurements();
        Assert.assertTrue(((measurements.size()) == 1));
        for (Measurement measurement : measurements) {
            // ??ThreadPool????
            Object obj = measurement.value();
            Assert.assertEquals(30, RpcLookoutTest.corePoolSize.get(obj));
            Assert.assertEquals(500, RpcLookoutTest.maxPoolSize.get(obj));
            Assert.assertEquals(600, RpcLookoutTest.queueSize.get(obj));
        }
    }

    /**
     * test thread pool active count
     *
     * @throws Exception
     * 		Exception
     */
    @Test
    public void testThreadPoolActiveCount() throws Exception {
        Metric metric = fetchWithName("rpc.bolt.threadpool.active.count");
        Collection<Measurement> measurements = metric.measure().measurements();
        Assert.assertTrue(((measurements.size()) == 1));
        for (Measurement measurement : measurements) {
            Assert.assertEquals(0, ((Number) (measurement.value())).intValue());
        }
    }

    /**
     * test thread pool idle count
     */
    @Test
    public void testThreadPoolIdleCount() {
        Metric metric = fetchWithName("rpc.bolt.threadpool.idle.count");
        Collection<Measurement> measurements = metric.measure().measurements();
        Assert.assertTrue(((measurements.size()) == 1));
        for (Measurement measurement : measurements) {
            Assert.assertEquals((((3 + 4) + 5) + 6), ((Number) (measurement.value())).intValue());
        }
    }

    /**
     * test thread pool queue size
     */
    @Test
    public void testThreadPoolQueueSize() {
        Metric metric = fetchWithName("rpc.bolt.threadpool.queue.size");
        Collection<Measurement> measurements = metric.measure().measurements();
        Assert.assertTrue(((measurements.size()) == 1));
        for (Measurement measurement : measurements) {
            Assert.assertEquals(0, ((Number) (measurement.value())).intValue());
        }
    }

    /**
     * test provider service stats
     */
    @Test
    public void testProviderServiceStats() {
        Metric metric = fetchWithName("rpc.provider.service.stats");
        for (Tag tag : metric.id().tags()) {
            if (tag.key().equalsIgnoreCase("method")) {
                String methodName = tag.value();
                if (methodName.equals("saySync")) {
                    assertMethod(metric, true, 3, "saySync", 0, 0);
                } else
                    if (methodName.equals("sayFuture")) {
                        assertMethod(metric, true, 4, "sayFuture", 0, 0);
                    } else
                        if (methodName.equals("sayCallback")) {
                            assertMethod(metric, true, 5, "sayCallback", 0, 0);
                        } else
                            if (methodName.equals("sayOneway")) {
                                assertMethod(metric, true, 6, "sayOneway", 0, 0);
                            }



            }
        }
    }

    /**
     * test consumer service stats
     */
    @Test
    public void testConsumerServiceStats() {
        Metric metric = fetchWithName("rpc.consumer.service.stats");
        for (Tag tag : metric.id().tags()) {
            if (tag.key().equalsIgnoreCase("method")) {
                String methodName = tag.value();
                if (methodName.equals("saySync")) {
                    assertMethod(metric, false, 3, "saySync", 1203, 352);
                } else
                    if (methodName.equals("sayFuture")) {
                        assertMethod(metric, false, 4, "sayFuture", 1620, 534);
                    } else
                        if (methodName.equals("sayCallback")) {
                            assertMethod(metric, false, 5, "sayCallback", 2045, 720);
                        } else
                            if (methodName.equals("sayOneway")) {
                                assertMethod(metric, false, 6, "sayOneway", 2430, 0);
                            }



            }
        }
    }
}

