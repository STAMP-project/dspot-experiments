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
package com.hazelcast.map;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.bounce.BounceMemberRule;
import com.hazelcast.test.jitter.JitterRule;
import java.util.Collection;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Query map while members of the cluster are being shutdown and started
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class QueryBounceTest {
    private static final String TEST_MAP_NAME = "employees";

    private static final int COUNT_ENTRIES = 10000;

    private static final int CONCURRENCY = 10;

    @Rule
    public BounceMemberRule bounceMemberRule = BounceMemberRule.with(getConfig()).clusterSize(4).driverCount(4).build();

    @Rule
    public JitterRule jitterRule = new JitterRule();

    @Test
    public void testQuery() {
        prepareAndRunQueryTasks(false);
    }

    @Test
    public void testQueryWithIndexes() {
        prepareAndRunQueryTasks(true);
    }

    public static class QueryRunnable implements Runnable {
        private final HazelcastInstance hazelcastInstance;

        // query age min-max range, min is randomized, max = min+1000
        private final Random random = new Random();

        private final int numberOfResults = 1000;

        private IMap<String, SampleTestObjects.Employee> map;

        public QueryRunnable(HazelcastInstance hazelcastInstance) {
            this.hazelcastInstance = hazelcastInstance;
        }

        @Override
        public void run() {
            if ((map) == null) {
                map = hazelcastInstance.getMap(QueryBounceTest.TEST_MAP_NAME);
            }
            int min = random.nextInt(((QueryBounceTest.COUNT_ENTRIES) - (numberOfResults)));
            int max = min + (numberOfResults);
            String sql = ((min % 2) == 0) ? (("age >= " + min) + " AND age < ") + max// may use sorted index
             : (("id >= " + min) + " AND id < ") + max;// may use unsorted index

            Collection<SampleTestObjects.Employee> employees = map.values(new SqlPredicate(sql));
            Assert.assertEquals("There is data loss", QueryBounceTest.COUNT_ENTRIES, map.size());
            Assert.assertEquals((((("Obtained " + (employees.size())) + " results for query '") + sql) + "'"), numberOfResults, employees.size());
        }
    }
}

