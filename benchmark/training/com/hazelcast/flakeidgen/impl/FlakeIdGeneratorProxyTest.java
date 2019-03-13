/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.flakeidgen.impl;


import com.hazelcast.core.HazelcastException;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorProxy.IdBatchAndWaitTime;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FlakeIdGeneratorProxyTest {
    /**
     * Available number of IDs per second from single member
     */
    private static final int IDS_PER_SECOND = 1 << (FlakeIdGeneratorProxy.BITS_SEQUENCE);

    private static final ILogger LOG = Logger.getLogger(FlakeIdGeneratorProxyTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FlakeIdGeneratorProxy gen;

    private ClusterService clusterService;

    @Test
    public void when_nodeIdUpdated_then_pickedUpAfterUpdateInterval() {
        Mockito.when(clusterService.getMemberListJoinVersion()).thenReturn(20);
        Assert.assertEquals(20, gen.getNodeId(0));
        Mockito.when(clusterService.getMemberListJoinVersion()).thenReturn(30);
        Assert.assertEquals(20, gen.getNodeId(0));
        Assert.assertEquals(20, gen.getNodeId(((FlakeIdGeneratorProxy.NODE_ID_UPDATE_INTERVAL_NS) - 1)));
        Assert.assertEquals(30, gen.getNodeId(FlakeIdGeneratorProxy.NODE_ID_UPDATE_INTERVAL_NS));
    }

    @Test
    public void test_timeMiddle() {
        IdBatchAndWaitTime result = gen.newIdBaseLocal(1516028439000L, 1234, 10);
        Assert.assertEquals(5300086112257234L, result.idBatch.base());
    }

    @Test
    public void test_timeLowEdge() {
        IdBatchAndWaitTime result = gen.newIdBaseLocal(FlakeIdGeneratorProxy.EPOCH_START, 1234, 10);
        Assert.assertEquals(1234L, result.idBatch.base());
    }

    @Test
    public void test_timeHighEdge() {
        IdBatchAndWaitTime result = gen.newIdBaseLocal((((FlakeIdGeneratorProxy.EPOCH_START) + (1L << (FlakeIdGeneratorProxy.BITS_TIMESTAMP))) - 1L), 1234, 10);
        Assert.assertEquals(9223372036850582738L, result.idBatch.base());
    }

    @Test
    public void test_idsOrdered() {
        long lastId = -1;
        for (long now = FlakeIdGeneratorProxy.EPOCH_START; now < ((FlakeIdGeneratorProxy.EPOCH_START) + (1L << (FlakeIdGeneratorProxy.BITS_TIMESTAMP))); now += (((365L * 24L) * 60L) * 60L) * 1000L) {
            long base = gen.newIdBaseLocal(now, 1234, 1).idBatch.base();
            FlakeIdGeneratorProxyTest.LOG.info(((("at " + (new Date(now))) + ", id=") + base));
            Assert.assertTrue(((("lastId=" + lastId) + ", newId=") + base), (lastId < base));
            lastId = base;
        }
    }

    @Test
    public void when_currentTimeBeforeAllowedRange_then_fail() {
        long lowestGoodTimestamp = (FlakeIdGeneratorProxy.EPOCH_START) - (1L << (FlakeIdGeneratorProxy.BITS_TIMESTAMP));
        gen.newIdBaseLocal(lowestGoodTimestamp, 0, 1);
        exception.expect(HazelcastException.class);
        exception.expectMessage("Current time out of allowed range");
        gen.newIdBaseLocal((lowestGoodTimestamp - 1), 0, 1);
    }

    @Test
    public void when_currentTimeAfterAllowedRange_then_fail() {
        gen.newIdBaseLocal((((FlakeIdGeneratorProxy.EPOCH_START) + (1L << (FlakeIdGeneratorProxy.BITS_TIMESTAMP))) - 1), 0, 1);
        exception.expect(HazelcastException.class);
        exception.expectMessage("Current time out of allowed range");
        gen.newIdBaseLocal(((FlakeIdGeneratorProxy.EPOCH_START) + (1L << (FlakeIdGeneratorProxy.BITS_TIMESTAMP))), 0, 1);
    }

    @Test
    public void when_twoIdsAtTheSameMoment_then_higherSeq() {
        long id1 = gen.newIdBaseLocal(1516028439000L, 1234, 1).idBatch.base();
        long id2 = gen.newIdBaseLocal(1516028439000L, 1234, 1).idBatch.base();
        Assert.assertEquals(5300086112257234L, id1);
        Assert.assertEquals((id1 + (1 << (FlakeIdGeneratorProxy.BITS_NODE_ID))), id2);
    }

    @Test
    public void test_init() {
        long currentId = gen.newId();
        Assert.assertTrue(gen.init((currentId / 2)));
        Assert.assertFalse(gen.init((currentId * 2)));
    }

    @Test
    public void test_minimumIdOffset() {
        // By assigning MIN_VALUE idOffset we'll offset the default epoch start by (Long.MIN_VALUE >> 22) ms, that is
        // by about 69 years. So the lowest working date will be:
        before(Long.MIN_VALUE, 0);
        long id = gen.newIdBaseLocal(FlakeIdGeneratorProxy.EPOCH_START, 1234, 1).idBatch.base();
        FlakeIdGeneratorProxyTest.LOG.info(("ID=" + id));
        Assert.assertEquals((-9223372036854774574L), id);
    }

    @Test
    public void test_maximumIdOffset() {
        // By assigning MIN_VALUE idOffset we'll offset the default epoch start by (Long.MIN_VALUE >> 22) ms, that is
        // by about 69 years. So the lowest working date will be:
        before(Long.MAX_VALUE, 0);
        long id = gen.newIdBaseLocal(FlakeIdGeneratorProxy.EPOCH_START, 1234, 1).idBatch.base();
        FlakeIdGeneratorProxyTest.LOG.info(("ID=" + id));
        Assert.assertEquals(9223372036850582738L, id);
    }

    @Test
    public void test_positiveNodeIdOffset() {
        int nodeIdOffset = 5;
        int memberListJoinVersion = 20;
        before(0, nodeIdOffset);
        Mockito.when(clusterService.getMemberListJoinVersion()).thenReturn(memberListJoinVersion);
        Assert.assertEquals((memberListJoinVersion + nodeIdOffset), gen.getNodeId(0));
    }

    @Test
    public void when_migrationScenario_then_idFromFlakeIdIsLarger() {
        // This simulates the migration scenario: we take the largest IdGenerator value (a constant here)
        // and the current FIG value. Then we configure the offset based on their difference plus the reserve
        // and check, that the ID after idOffset is set is larger than the value from IG.
        long largestIdGeneratorValue = 5421380884070400000L;
        long currentFlakeGenValue = gen.newId();
        // this number is mentioned in FlakeIdGeneratorConfig.setIdOffset()
        long reserve = 274877906944L;
        // This test will start failing after December 17th 2058 3:52:07 UTC: after this time no idOffset will be needed.
        Assert.assertTrue((largestIdGeneratorValue > currentFlakeGenValue));
        // the before() call will create a new gen
        before(((largestIdGeneratorValue - currentFlakeGenValue) + reserve), 0);
        // Then
        long newFlakeGenValue = gen.newId();
        Assert.assertTrue((newFlakeGenValue > largestIdGeneratorValue));
        Assert.assertTrue(((newFlakeGenValue - largestIdGeneratorValue) < ((TimeUnit.MINUTES.toMillis(10)) * (1 << ((FlakeIdGeneratorProxy.BITS_NODE_ID) + (FlakeIdGeneratorProxy.BITS_SEQUENCE))))));
    }

    // #### Tests pertaining to wait time ####
    @Test
    public void when_fewIds_then_noWaitTime() {
        Assert.assertEquals(0, gen.newIdBaseLocal(1516028439000L, 1234, 100).waitTimeMillis);
    }

    @Test
    public void when_maximumAllowedFuture_then_noWaitTime() {
        IdBatchAndWaitTime result = gen.newIdBaseLocal(1516028439000L, 1234, ((int) ((FlakeIdGeneratorProxyTest.IDS_PER_SECOND) * (FlakeIdGeneratorProxy.ALLOWED_FUTURE_MILLIS))));
        Assert.assertEquals(0, result.waitTimeMillis);
    }

    @Test
    public void when_maximumAllowedFuturePlusOne_then_1msWaitTime() {
        int batchSize = ((int) ((FlakeIdGeneratorProxyTest.IDS_PER_SECOND) * (FlakeIdGeneratorProxy.ALLOWED_FUTURE_MILLIS))) + (FlakeIdGeneratorProxyTest.IDS_PER_SECOND);
        IdBatchAndWaitTime result = gen.newIdBaseLocal(1516028439000L, 1234, batchSize);
        Assert.assertEquals(1, result.waitTimeMillis);
    }

    @Test
    public void when_10mIds_then_wait() {
        int batchSize = 10000000;
        IdBatchAndWaitTime result = gen.newIdBaseLocal(1516028439000L, 1234, batchSize);
        Assert.assertEquals(((batchSize / (FlakeIdGeneratorProxyTest.IDS_PER_SECOND)) - (FlakeIdGeneratorProxy.ALLOWED_FUTURE_MILLIS)), result.waitTimeMillis);
    }

    @Test
    public void when_10mIdsInSmallChunks_then_wait() {
        int batchSize = 100;
        for (int numIds = 0; numIds < 10000000; numIds += batchSize) {
            IdBatchAndWaitTime result = gen.newIdBaseLocal(1516028439000L, 1234, batchSize);
            Assert.assertEquals(Math.max(0, (((numIds + batchSize) / (FlakeIdGeneratorProxyTest.IDS_PER_SECOND)) - (FlakeIdGeneratorProxy.ALLOWED_FUTURE_MILLIS))), result.waitTimeMillis);
        }
    }
}

