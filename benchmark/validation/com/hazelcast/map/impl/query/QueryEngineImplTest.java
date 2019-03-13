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


import Target.ALL_NODES;
import Target.LOCAL_NODE;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.IterationType;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class QueryEngineImplTest extends HazelcastTestSupport {
    private HazelcastInstance instance;

    private IMap<String, String> map;

    private QueryEngine queryEngine;

    private int partitionId;

    private String key;

    private String value;

    @Test
    public void runQueryOnAllPartitions() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(IterationType.KEY).build();
        QueryResult result = queryEngine.execute(query, ALL_NODES);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(key, toObject(result.iterator().next().getKey()));
    }

    @Test
    public void runQueryOnLocalPartitions() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(IterationType.KEY).build();
        QueryResult result = queryEngine.execute(query, LOCAL_NODE);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(key, toObject(result.iterator().next().getKey()));
    }

    @Test
    public void runQueryOnAllPartitions_key() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(IterationType.KEY).build();
        QueryResult result = queryEngine.execute(query, ALL_NODES);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(key, toObject(result.iterator().next().getKey()));
    }

    @Test
    public void runQueryOnAllPartitions_value() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(IterationType.VALUE).build();
        QueryResult result = queryEngine.execute(query, ALL_NODES);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(value, toObject(result.iterator().next().getValue()));
    }

    @Test
    public void runQueryOnGivenPartition() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(IterationType.ENTRY).build();
        QueryResult result = queryEngine.execute(query, Target.createPartitionTarget(partitionId));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(key, toObject(((Map.Entry) (result.iterator().next())).getKey()));
        Assert.assertEquals(map.get(key), toObject(((Map.Entry) (result.iterator().next())).getValue()));
    }
}

