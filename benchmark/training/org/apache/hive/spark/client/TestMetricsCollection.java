/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.spark.client;


import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import org.apache.hive.spark.client.metrics.InputMetrics;
import org.apache.hive.spark.client.metrics.Metrics;
import org.junit.Assert;
import org.junit.Test;


public class TestMetricsCollection {
    @Test
    public void testMetricsAggregation() {
        MetricsCollection collection = new MetricsCollection();
        // 2 jobs, 2 stages per job, 2 tasks per stage.
        for (int i : Arrays.asList(1, 2)) {
            for (int j : Arrays.asList(1, 2)) {
                for (long k : Arrays.asList(1L, 2L)) {
                    collection.addMetrics(i, j, k, makeMetrics(i, j, k));
                }
            }
        }
        Assert.assertEquals(ImmutableSet.of(1, 2), collection.getJobIds());
        Assert.assertEquals(ImmutableSet.of(1, 2), collection.getStageIds(1));
        Assert.assertEquals(ImmutableSet.of(1L, 2L), collection.getTaskIds(1, 1));
        Metrics task112 = collection.getTaskMetrics(1, 1, 2);
        checkMetrics(task112, taskValue(1, 1, 2));
        Metrics stage21 = collection.getStageMetrics(2, 1);
        checkMetrics(stage21, stageValue(2, 1, 2));
        Metrics job1 = collection.getJobMetrics(1);
        checkMetrics(job1, jobValue(1, 2, 2));
        Metrics global = collection.getAllMetrics();
        checkMetrics(global, globalValue(2, 2, 2));
    }

    @Test
    public void testOptionalMetrics() {
        long value = taskValue(1, 1, 1L);
        Metrics metrics = new Metrics(value, value, value, value, value, value, value, value, value, value, null, null, null, null);
        MetricsCollection collection = new MetricsCollection();
        for (int i : Arrays.asList(1, 2)) {
            collection.addMetrics(i, 1, 1, metrics);
        }
        Metrics global = collection.getAllMetrics();
        Assert.assertNull(global.inputMetrics);
        Assert.assertNull(global.shuffleReadMetrics);
        Assert.assertNull(global.shuffleWriteMetrics);
        collection.addMetrics(3, 1, 1, makeMetrics(3, 1, 1));
        Metrics global2 = collection.getAllMetrics();
        Assert.assertNotNull(global2.inputMetrics);
        Assert.assertEquals(taskValue(3, 1, 1), global2.inputMetrics.bytesRead);
        Assert.assertNotNull(global2.shuffleReadMetrics);
        Assert.assertNotNull(global2.shuffleWriteMetrics);
    }

    @Test
    public void testInputReadMethodAggregation() {
        MetricsCollection collection = new MetricsCollection();
        long value = taskValue(1, 1, 1);
        Metrics metrics1 = new Metrics(value, value, value, value, value, value, value, value, value, value, new InputMetrics(value, value), null, null, null);
        Metrics metrics2 = new Metrics(value, value, value, value, value, value, value, value, value, value, new InputMetrics(value, value), null, null, null);
        collection.addMetrics(1, 1, 1, metrics1);
        collection.addMetrics(1, 1, 2, metrics2);
        Metrics global = collection.getAllMetrics();
        Assert.assertNotNull(global.inputMetrics);
    }
}

