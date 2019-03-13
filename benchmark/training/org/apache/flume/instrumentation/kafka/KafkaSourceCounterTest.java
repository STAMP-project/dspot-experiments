/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.instrumentation.kafka;


import org.junit.Assert;
import org.junit.Test;


public class KafkaSourceCounterTest {
    KafkaSourceCounter counter;

    @Test
    public void testAddToKafkaEventGetTimer() throws Exception {
        Assert.assertEquals(1L, counter.addToKafkaEventGetTimer(1L));
    }

    @Test
    public void testAddToKafkaCommitTimer() throws Exception {
        Assert.assertEquals(1L, counter.addToKafkaCommitTimer(1L));
    }

    @Test
    public void testIncrementKafkaEmptyCount() throws Exception {
        Assert.assertEquals(1L, counter.incrementKafkaEmptyCount());
    }

    @Test
    public void testGetKafkaCommitTimer() throws Exception {
        Assert.assertEquals(0, counter.getKafkaCommitTimer());
    }

    @Test
    public void testGetKafkaEventGetTimer() throws Exception {
        Assert.assertEquals(0, counter.getKafkaEventGetTimer());
    }

    @Test
    public void testGetKafkaEmptyCount() throws Exception {
        Assert.assertEquals(0, counter.getKafkaEmptyCount());
    }
}

