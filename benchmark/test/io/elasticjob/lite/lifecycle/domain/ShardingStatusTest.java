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
package io.elasticjob.lite.lifecycle.domain;


import ShardingInfo.ShardingStatus;
import ShardingInfo.ShardingStatus.DISABLED;
import ShardingInfo.ShardingStatus.PENDING;
import ShardingInfo.ShardingStatus.RUNNING;
import ShardingInfo.ShardingStatus.SHARDING_FLAG;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingStatusTest {
    @Test
    public void assertGetShardingStatusWhenIsDisabled() {
        Assert.assertThat(ShardingStatus.getShardingStatus(true, false, true), Is.is(DISABLED));
    }

    @Test
    public void assertGetShardingStatusWhenIsRunning() {
        Assert.assertThat(ShardingStatus.getShardingStatus(false, true, false), Is.is(RUNNING));
    }

    @Test
    public void assertGetShardingStatusWhenIsPending() {
        Assert.assertThat(ShardingStatus.getShardingStatus(false, false, false), Is.is(PENDING));
    }

    @Test
    public void assertGetShardingStatusWhenIsShardingError() {
        Assert.assertThat(ShardingStatus.getShardingStatus(false, false, true), Is.is(SHARDING_FLAG));
    }
}

