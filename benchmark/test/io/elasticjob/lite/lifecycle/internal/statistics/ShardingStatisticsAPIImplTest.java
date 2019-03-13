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
package io.elasticjob.lite.lifecycle.internal.statistics;


import ShardingInfo.ShardingStatus.DISABLED;
import ShardingInfo.ShardingStatus.PENDING;
import ShardingInfo.ShardingStatus.RUNNING;
import ShardingInfo.ShardingStatus.SHARDING_FLAG;
import io.elasticjob.lite.lifecycle.api.ShardingStatisticsAPI;
import io.elasticjob.lite.lifecycle.domain.ShardingInfo;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ShardingStatisticsAPIImplTest {
    private ShardingStatisticsAPI shardingStatisticsAPI;

    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Test
    public void assertGetShardingInfo() {
        Mockito.when(regCenter.isExisted("/test_job/sharding")).thenReturn(true);
        Mockito.when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Arrays.asList("0", "1", "2", "3"));
        Mockito.when(regCenter.get("/test_job/sharding/0/instance")).thenReturn("ip1@-@1234");
        Mockito.when(regCenter.get("/test_job/sharding/1/instance")).thenReturn("ip2@-@2341");
        Mockito.when(regCenter.get("/test_job/sharding/2/instance")).thenReturn("ip3@-@3412");
        Mockito.when(regCenter.get("/test_job/sharding/3/instance")).thenReturn("ip4@-@4123");
        Mockito.when(regCenter.isExisted("/test_job/instances/ip4@-@4123")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job/sharding/0/running")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job/sharding/1/running")).thenReturn(false);
        Mockito.when(regCenter.isExisted("/test_job/sharding/2/running")).thenReturn(false);
        Mockito.when(regCenter.isExisted("/test_job/sharding/3/running")).thenReturn(false);
        Mockito.when(regCenter.isExisted("/test_job/sharding/0/failover")).thenReturn(false);
        Mockito.when(regCenter.isExisted("/test_job/sharding/1/failover")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job/sharding/2/disabled")).thenReturn(true);
        int i = 0;
        for (ShardingInfo each : shardingStatisticsAPI.getShardingInfo("test_job")) {
            i++;
            Assert.assertThat(each.getItem(), Is.is((i - 1)));
            switch (i) {
                case 1 :
                    Assert.assertThat(each.getStatus(), Is.is(RUNNING));
                    Assert.assertThat(each.getServerIp(), Is.is("ip1"));
                    Assert.assertThat(each.getInstanceId(), Is.is("1234"));
                    break;
                case 2 :
                    TestCase.assertTrue(each.isFailover());
                    Assert.assertThat(each.getStatus(), Is.is(SHARDING_FLAG));
                    Assert.assertThat(each.getServerIp(), Is.is("ip2"));
                    Assert.assertThat(each.getInstanceId(), Is.is("2341"));
                    break;
                case 3 :
                    Assert.assertThat(each.getStatus(), Is.is(DISABLED));
                    Assert.assertThat(each.getServerIp(), Is.is("ip3"));
                    Assert.assertThat(each.getInstanceId(), Is.is("3412"));
                    break;
                case 4 :
                    Assert.assertThat(each.getStatus(), Is.is(PENDING));
                    Assert.assertThat(each.getServerIp(), Is.is("ip4"));
                    Assert.assertThat(each.getInstanceId(), Is.is("4123"));
                    break;
                default :
                    break;
            }
        }
    }
}

