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
package io.crate.execution.engine.indexing;


import RowSourceInfo.EMPTY_INSTANCE;
import ShardRequest.Item;
import io.crate.execution.dml.ShardRequest;
import io.crate.test.integration.CrateUnitTest;
import java.io.IOException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Test;

import static BulkShardCreationLimiter.MAX_NEW_SHARDS_PER_NODE;


public class BulkShardCreationLimiterTest extends CrateUnitTest {
    private static class DummyShardRequest extends ShardRequest<BulkShardCreationLimiterTest.DummyShardRequest, BulkShardCreationLimiterTest.DummyRequestItem> {
        @Override
        protected BulkShardCreationLimiterTest.DummyRequestItem readItem(StreamInput input) throws IOException {
            return null;
        }
    }

    private static class DummyRequestItem extends ShardRequest.Item {
        DummyRequestItem(String id) {
            super(id);
        }
    }

    private static final ShardedRequests<BulkShardCreationLimiterTest.DummyShardRequest, BulkShardCreationLimiterTest.DummyRequestItem> SHARED_REQUESTS = new ShardedRequests(( s) -> new io.crate.execution.engine.indexing.DummyShardRequest());

    static {
        BulkShardCreationLimiterTest.SHARED_REQUESTS.add(new BulkShardCreationLimiterTest.DummyRequestItem("1"), "dummy", null, EMPTY_INSTANCE);
    }

    @Test
    public void testNumberOfShardsGreaterEqualThanLimit() throws Exception {
        int numberOfShards = MAX_NEW_SHARDS_PER_NODE;
        int numberOfReplicas = 0;
        BulkShardCreationLimiter bulkShardCreationLimiter = new BulkShardCreationLimiter(numberOfShards, numberOfReplicas, 1);
        assertThat(bulkShardCreationLimiter.test(BulkShardCreationLimiterTest.SHARED_REQUESTS), Matchers.is(true));
    }

    @Test
    public void testNumberOfShardsLessThanLimit() throws Exception {
        int numberOfShards = (MAX_NEW_SHARDS_PER_NODE) - 1;
        int numberOfReplicas = 0;
        BulkShardCreationLimiter bulkShardCreationLimiter = new BulkShardCreationLimiter(numberOfShards, numberOfReplicas, 1);
        assertThat(bulkShardCreationLimiter.test(BulkShardCreationLimiterTest.SHARED_REQUESTS), Matchers.is(false));
    }

    @Test
    public void testNumberOfShardsLessThanLimitWithTwoNodes() throws Exception {
        int numberOfShards = (MAX_NEW_SHARDS_PER_NODE) - 1;
        int numberOfReplicas = 0;
        BulkShardCreationLimiter bulkShardCreationLimiter = new BulkShardCreationLimiter(numberOfShards, numberOfReplicas, 2);
        assertThat(bulkShardCreationLimiter.test(BulkShardCreationLimiterTest.SHARED_REQUESTS), Matchers.is(false));
    }
}

