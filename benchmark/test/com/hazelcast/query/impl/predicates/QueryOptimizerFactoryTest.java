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
package com.hazelcast.query.impl.predicates;


import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryOptimizerFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void newOptimizer_whenUnknownValue_thenThrowIllegalArgumentException() {
        HazelcastProperties hazelcastProperties = createMockHazelcastProperties(GroupProperty.QUERY_OPTIMIZER_TYPE, "foo");
        QueryOptimizerFactory.newOptimizer(hazelcastProperties);
    }

    @Test
    public void newOptimizer_whenPropertyContainsRule_thenCreateRulesBasedOptimizer() {
        HazelcastProperties hazelcastProperties = createMockHazelcastProperties(GroupProperty.QUERY_OPTIMIZER_TYPE, "RULES");
        QueryOptimizer queryOptimizer = QueryOptimizerFactory.newOptimizer(hazelcastProperties);
        Assert.assertThat(queryOptimizer, Matchers.instanceOf(RuleBasedQueryOptimizer.class));
    }

    @Test
    public void newOptimizer_whenPropertyContainsNone_thenCreateEmptyOptimizer() {
        HazelcastProperties hazelcastProperties = createMockHazelcastProperties(GroupProperty.QUERY_OPTIMIZER_TYPE, "NONE");
        QueryOptimizer queryOptimizer = QueryOptimizerFactory.newOptimizer(hazelcastProperties);
        Assert.assertThat(queryOptimizer, Matchers.instanceOf(EmptyOptimizer.class));
    }
}

