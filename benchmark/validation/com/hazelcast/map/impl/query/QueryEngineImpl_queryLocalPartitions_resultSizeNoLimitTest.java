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


import Target.LOCAL_NODE;
import TruePredicate.INSTANCE;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.IterationType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryEngineImpl_queryLocalPartitions_resultSizeNoLimitTest extends HazelcastTestSupport {
    private IMap<Object, Object> map;

    private QueryEngineImpl queryEngine;

    @Test
    public void checkResultSize() {
        fillMap(10000);
        Query query = Query.of().mapName(map.getName()).predicate(INSTANCE).iterationType(IterationType.ENTRY).build();
        QueryResult result = ((QueryResult) (queryEngine.execute(query, LOCAL_NODE)));
        Assert.assertEquals(10000, result.size());
    }
}

