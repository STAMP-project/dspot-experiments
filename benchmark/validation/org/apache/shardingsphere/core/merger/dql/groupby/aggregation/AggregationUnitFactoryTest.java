/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.merger.dql.groupby.aggregation;


import AggregationType.AVG;
import AggregationType.COUNT;
import AggregationType.MAX;
import AggregationType.MIN;
import AggregationType.SUM;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class AggregationUnitFactoryTest {
    @Test
    public void assertCreateComparableAggregationUnit() {
        Assert.assertThat(AggregationUnitFactory.create(MIN), CoreMatchers.instanceOf(ComparableAggregationUnit.class));
        Assert.assertThat(AggregationUnitFactory.create(MAX), CoreMatchers.instanceOf(ComparableAggregationUnit.class));
    }

    @Test
    public void assertCreateAccumulationAggregationUnit() {
        Assert.assertThat(AggregationUnitFactory.create(SUM), CoreMatchers.instanceOf(AccumulationAggregationUnit.class));
        Assert.assertThat(AggregationUnitFactory.create(COUNT), CoreMatchers.instanceOf(AccumulationAggregationUnit.class));
    }

    @Test
    public void assertCreateAverageAggregationUnit() {
        Assert.assertThat(AggregationUnitFactory.create(AVG), CoreMatchers.instanceOf(AverageAggregationUnit.class));
    }
}

