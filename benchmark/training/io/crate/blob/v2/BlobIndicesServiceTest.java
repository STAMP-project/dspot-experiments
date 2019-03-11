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
package io.crate.blob.v2;


import io.crate.test.integration.CrateUnitTest;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.threadpool.TestThreadPool;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class BlobIndicesServiceTest extends CrateUnitTest {
    private BlobIndicesService blobIndicesService;

    private ClusterService clusterService;

    private TestThreadPool threadPool;

    @Test
    public void testBlobComponentsAreNotCreatedForNonBlobIndex() throws Exception {
        IndexService indexService = Mockito.mock(IndexService.class);
        Index index = new Index("dummy", UUIDs.randomBase64UUID());
        Mockito.when(indexService.index()).thenReturn(index);
        blobIndicesService.afterIndexCreated(indexService);
        IndexShard indexShard = Mockito.mock(IndexShard.class);
        Mockito.when(indexShard.shardId()).thenReturn(new org.elasticsearch.index.shard.ShardId(index, 0));
        blobIndicesService.afterIndexShardCreated(indexShard);
        assertThat(blobIndicesService.indices.keySet(), Matchers.empty());
    }
}

