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
package com.hazelcast.client.map;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.IMap;
import com.hazelcast.map.LocalIndexStatsTest;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientIndexStatsTest extends LocalIndexStatsTest {
    private TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    protected IMap map1;

    protected IMap map2;

    protected IMap noStatsMap1;

    protected IMap noStatsMap2;

    @SuppressWarnings("unchecked")
    @Test
    @Override
    public void testQueryCounting_WhenPartitionPredicateIsUsed() {
        map.addIndex("this", false);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        map.entrySet(new com.hazelcast.query.PartitionPredicate(10, Predicates.equal("this", 10)));
        Assert.assertTrue(((((map1.getLocalMapStats().getQueryCount()) == 1) && ((map2.getLocalMapStats().getQueryCount()) == 0)) || (((map1.getLocalMapStats().getQueryCount()) == 0) && ((map2.getLocalMapStats().getQueryCount()) == 1))));
        Assert.assertEquals(0, map1.getLocalMapStats().getIndexedQueryCount());
        Assert.assertEquals(0, map2.getLocalMapStats().getIndexedQueryCount());
        Assert.assertEquals(0, map1.getLocalMapStats().getIndexStats().get("this").getQueryCount());
        Assert.assertEquals(0, map2.getLocalMapStats().getIndexStats().get("this").getQueryCount());
    }
}

