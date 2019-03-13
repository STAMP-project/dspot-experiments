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
package org.apache.shardingsphere.api.config.sharding.strategy;


import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class StandardShardingStrategyConfigurationTest {
    @Test(expected = IllegalArgumentException.class)
    public void assertConstructorWithoutShardingColumn() {
        new StandardShardingStrategyConfiguration("", Mockito.mock(PreciseShardingAlgorithm.class));
    }

    @Test(expected = NullPointerException.class)
    public void assertConstructorWithoutPreciseShardingAlgorithm() {
        new StandardShardingStrategyConfiguration("id", null);
    }

    @Test
    public void assertConstructorWithMinArguments() {
        PreciseShardingAlgorithm preciseShardingAlgorithm = Mockito.mock(PreciseShardingAlgorithm.class);
        StandardShardingStrategyConfiguration actual = new StandardShardingStrategyConfiguration("id", preciseShardingAlgorithm);
        Assert.assertThat(actual.getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getPreciseShardingAlgorithm(), CoreMatchers.is(preciseShardingAlgorithm));
        Assert.assertNull(actual.getRangeShardingAlgorithm());
    }

    @Test
    public void assertConstructorWithMaxArguments() {
        PreciseShardingAlgorithm preciseShardingAlgorithm = Mockito.mock(PreciseShardingAlgorithm.class);
        RangeShardingAlgorithm rangeShardingAlgorithm = Mockito.mock(RangeShardingAlgorithm.class);
        StandardShardingStrategyConfiguration actual = new StandardShardingStrategyConfiguration("id", preciseShardingAlgorithm, rangeShardingAlgorithm);
        Assert.assertThat(actual.getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getPreciseShardingAlgorithm(), CoreMatchers.is(preciseShardingAlgorithm));
        Assert.assertThat(actual.getRangeShardingAlgorithm(), CoreMatchers.is(rangeShardingAlgorithm));
    }
}

