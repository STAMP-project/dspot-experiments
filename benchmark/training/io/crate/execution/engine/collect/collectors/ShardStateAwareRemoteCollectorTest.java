/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.collect.collectors;


import io.crate.data.BatchIterator;
import io.crate.data.CollectingRowConsumer;
import io.crate.data.Row;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.shard.ShardId;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class ShardStateAwareRemoteCollectorTest extends CrateDummyClusterServiceUnitTest {
    private CollectingRowConsumer<?, List<Object[]>> consumer;

    private RemoteCollector remoteCrateCollector;

    private Index index;

    private ShardId shardId;

    private ExecutorService executor;

    private ShardStateAwareRemoteCollector shardAwareRemoteCollector;

    private BatchIterator<Row> localBatchIterator;

    @Test
    public void testIsRemoteCollectorIfRemoteNodeIsNotLocalNode() throws InterruptedException, ExecutionException, TimeoutException {
        setNewClusterStateFor(createStartedShardRouting("n2"));
        shardAwareRemoteCollector.doCollect();
        consumer.resultFuture().get(10, TimeUnit.SECONDS);
        Mockito.verify(remoteCrateCollector, Mockito.times(1)).doCollect();
    }

    @Test
    public void testIsLocalCollectorIfRemoteNodeEqualsLocalNodeAndShardStarted() throws Exception {
        setNewClusterStateFor(createStartedShardRouting("n1"));
        shardAwareRemoteCollector.doCollect();
        consumer.resultFuture().get(10, TimeUnit.SECONDS);
        // either being exhausted or closed is fine, just make sure the localBatchIterator was used.
        try {
            assertThat(localBatchIterator.moveNext(), Matchers.is(false));
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), Matchers.containsString("Iterator is closed"));
        }
    }

    @Test
    public void testCollectorWaitsForShardToRelocateBeforeRemoteCollect() throws InterruptedException, ExecutionException, TimeoutException {
        ShardRouting relocatingShardRouting = createRelocatingShardRouting("n1", "n2");
        setNewClusterStateFor(relocatingShardRouting);
        shardAwareRemoteCollector.doCollect();
        setNewClusterStateFor(createStartedShardRouting("n2"));
        consumer.resultFuture().get(10, TimeUnit.SECONDS);
        Mockito.verify(remoteCrateCollector, Mockito.times(1)).doCollect();
    }
}

