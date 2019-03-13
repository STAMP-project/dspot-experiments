/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.api.strategy.impl;


import io.elasticjob.lite.api.strategy.JobInstance;
import io.elasticjob.lite.api.strategy.JobShardingStrategy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class AverageAllocationJobShardingStrategyTest {
    private final JobShardingStrategy jobShardingStrategy = new AverageAllocationJobShardingStrategy();

    @Test
    public void shardingForZeroServer() {
        Assert.assertThat(jobShardingStrategy.sharding(Collections.<JobInstance>emptyList(), "test_job", 3), CoreMatchers.is(Collections.<JobInstance, List<Integer>>emptyMap()));
    }

    @Test
    public void shardingForOneServer() {
        Map<JobInstance, List<Integer>> expected = new LinkedHashMap<>(1, 1);
        expected.put(new JobInstance("host0@-@0"), Arrays.asList(0, 1, 2));
        Assert.assertThat(jobShardingStrategy.sharding(Collections.singletonList(new JobInstance("host0@-@0")), "test_job", 3), CoreMatchers.is(expected));
    }

    @Test
    public void shardingForServersMoreThanShardingCount() {
        Map<JobInstance, List<Integer>> expected = new LinkedHashMap<>(3, 1);
        expected.put(new JobInstance("host0@-@0"), Collections.singletonList(0));
        expected.put(new JobInstance("host1@-@0"), Collections.singletonList(1));
        expected.put(new JobInstance("host2@-@0"), Collections.<Integer>emptyList());
        Assert.assertThat(jobShardingStrategy.sharding(Arrays.asList(new JobInstance("host0@-@0"), new JobInstance("host1@-@0"), new JobInstance("host2@-@0")), "test_job", 2), CoreMatchers.is(expected));
    }

    @Test
    public void shardingForServersLessThanShardingCountAliquot() {
        Map<JobInstance, List<Integer>> expected = new LinkedHashMap<>(3, 1);
        expected.put(new JobInstance("host0@-@0"), Arrays.asList(0, 1, 2));
        expected.put(new JobInstance("host1@-@0"), Arrays.asList(3, 4, 5));
        expected.put(new JobInstance("host2@-@0"), Arrays.asList(6, 7, 8));
        Assert.assertThat(jobShardingStrategy.sharding(Arrays.asList(new JobInstance("host0@-@0"), new JobInstance("host1@-@0"), new JobInstance("host2@-@0")), "test_job", 9), CoreMatchers.is(expected));
    }

    @Test
    public void shardingForServersLessThanShardingCountAliquantFor8ShardingCountAnd3Servers() {
        Map<JobInstance, List<Integer>> expected = new LinkedHashMap<>(3, 1);
        expected.put(new JobInstance("host0@-@0"), Arrays.asList(0, 1, 6));
        expected.put(new JobInstance("host1@-@0"), Arrays.asList(2, 3, 7));
        expected.put(new JobInstance("host2@-@0"), Arrays.asList(4, 5));
        Assert.assertThat(jobShardingStrategy.sharding(Arrays.asList(new JobInstance("host0@-@0"), new JobInstance("host1@-@0"), new JobInstance("host2@-@0")), "test_job", 8), CoreMatchers.is(expected));
    }

    @Test
    public void shardingForServersLessThanShardingCountAliquantFor10ShardingCountAnd3Servers() {
        Map<JobInstance, List<Integer>> expected = new LinkedHashMap<>(3, 1);
        expected.put(new JobInstance("host0@-@0"), Arrays.asList(0, 1, 2, 9));
        expected.put(new JobInstance("host1@-@0"), Arrays.asList(3, 4, 5));
        expected.put(new JobInstance("host2@-@0"), Arrays.asList(6, 7, 8));
        Assert.assertThat(jobShardingStrategy.sharding(Arrays.asList(new JobInstance("host0@-@0"), new JobInstance("host1@-@0"), new JobInstance("host2@-@0")), "test_job", 10), CoreMatchers.is(expected));
    }
}

