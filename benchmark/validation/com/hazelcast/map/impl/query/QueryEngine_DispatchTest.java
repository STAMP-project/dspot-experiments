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
package com.hazelcast.map.impl.query;


import IterationType.ENTRY;
import Target.ALL_NODES;
import Target.LOCAL_NODE;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.FutureUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class QueryEngine_DispatchTest extends HazelcastTestSupport {
    private HazelcastInstance instance;

    private IMap<String, String> map;

    private QueryEngineImpl queryEngine;

    private int partitionId;

    private String key;

    private String value;

    @Test
    public void dispatchFullQueryOnQueryThread_localMembers() {
        dispatchFullQueryOnQueryThread(LOCAL_NODE);
    }

    @Test
    public void dispatchFullQueryOnQueryThread_allMembers() {
        dispatchFullQueryOnQueryThread(ALL_NODES);
    }

    @Test
    public void dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread_singlePartition() {
        Query query = Query.of().mapName(map.getName()).predicate(Predicates.equal("this", value)).iterationType(ENTRY).build();
        Future<Result> future = queryEngine.dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(query, partitionId);
        Collection<Result> results = FutureUtil.returnWithDeadline(Collections.singletonList(future), 1, TimeUnit.MINUTES);
        QueryResult result = ((QueryResult) (results.iterator().next()));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(map.get(key), toObject(result.getRows().iterator().next().getValue()));
    }
}

