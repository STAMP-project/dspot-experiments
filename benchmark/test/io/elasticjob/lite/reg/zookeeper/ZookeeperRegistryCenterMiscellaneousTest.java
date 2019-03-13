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
package io.elasticjob.lite.reg.zookeeper;


import io.elasticjob.lite.fixture.EmbedTestingServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ZookeeperRegistryCenterMiscellaneousTest {
    private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenterMiscellaneousTest.class.getName());

    private static ZookeeperRegistryCenter zkRegCenter;

    @Test
    public void assertGetRawClient() {
        Assert.assertThat(ZookeeperRegistryCenterMiscellaneousTest.zkRegCenter.getRawClient(), CoreMatchers.instanceOf(CuratorFramework.class));
        Assert.assertThat(getNamespace(), CoreMatchers.is(ZookeeperRegistryCenterMiscellaneousTest.class.getName()));
    }

    @Test
    public void assertGetRawCache() {
        Assert.assertThat(ZookeeperRegistryCenterMiscellaneousTest.zkRegCenter.getRawCache("/test"), CoreMatchers.instanceOf(TreeCache.class));
    }

    @Test
    public void assertGetZkConfig() {
        ZookeeperRegistryCenter zkRegCenter = new ZookeeperRegistryCenter(ZookeeperRegistryCenterMiscellaneousTest.ZOOKEEPER_CONFIGURATION);
        Assert.assertThat(zkRegCenter.getZkConfig(), CoreMatchers.is(ZookeeperRegistryCenterMiscellaneousTest.ZOOKEEPER_CONFIGURATION));
    }
}

