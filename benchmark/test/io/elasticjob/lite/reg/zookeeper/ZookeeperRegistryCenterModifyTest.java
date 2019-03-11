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
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ZookeeperRegistryCenterModifyTest {
    private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenterModifyTest.class.getName());

    private static ZookeeperRegistryCenter zkRegCenter;

    @Test
    public void assertPersist() {
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persist("/test", "test_update");
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persist("/persist/new", "new_value");
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.get("/test"), CoreMatchers.is("test_update"));
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.get("/persist/new"), CoreMatchers.is("new_value"));
    }

    @Test
    public void assertUpdate() {
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persist("/update", "before_update");
        ZookeeperRegistryCenterModifyTest.zkRegCenter.update("/update", "after_update");
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.getDirectly("/update"), CoreMatchers.is("after_update"));
    }

    @Test
    public void assertPersistEphemeral() throws Exception {
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persist("/persist", "persist_value");
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persistEphemeral("/ephemeral", "ephemeral_value");
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.get("/persist"), CoreMatchers.is("persist_value"));
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.get("/ephemeral"), CoreMatchers.is("ephemeral_value"));
        ZookeeperRegistryCenterModifyTest.zkRegCenter.close();
        CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(), new RetryOneTime(2000));
        client.start();
        client.blockUntilConnected();
        Assert.assertThat(client.getData().forPath((("/" + (ZookeeperRegistryCenterModifyTest.class.getName())) + "/persist")), CoreMatchers.is("persist_value".getBytes()));
        Assert.assertNull(client.checkExists().forPath((("/" + (ZookeeperRegistryCenterModifyTest.class.getName())) + "/ephemeral")));
        ZookeeperRegistryCenterModifyTest.zkRegCenter.init();
    }

    @Test
    public void assertPersistSequential() throws Exception {
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.persistSequential("/sequential/test_sequential", "test_value"), CoreMatchers.startsWith("/sequential/test_sequential"));
        Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.persistSequential("/sequential/test_sequential", "test_value"), CoreMatchers.startsWith("/sequential/test_sequential"));
        CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(), new RetryOneTime(2000));
        client.start();
        client.blockUntilConnected();
        List<String> actual = client.getChildren().forPath((("/" + (ZookeeperRegistryCenterModifyTest.class.getName())) + "/sequential"));
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        for (String each : actual) {
            Assert.assertThat(each, CoreMatchers.startsWith("test_sequential"));
            Assert.assertThat(ZookeeperRegistryCenterModifyTest.zkRegCenter.get(("/sequential/" + each)), CoreMatchers.startsWith("test_value"));
        }
    }

    @Test
    public void assertPersistEphemeralSequential() throws Exception {
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persistEphemeralSequential("/sequential/test_ephemeral_sequential");
        ZookeeperRegistryCenterModifyTest.zkRegCenter.persistEphemeralSequential("/sequential/test_ephemeral_sequential");
        CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(), new RetryOneTime(2000));
        client.start();
        client.blockUntilConnected();
        List<String> actual = client.getChildren().forPath((("/" + (ZookeeperRegistryCenterModifyTest.class.getName())) + "/sequential"));
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        for (String each : actual) {
            Assert.assertThat(each, CoreMatchers.startsWith("test_ephemeral_sequential"));
        }
        ZookeeperRegistryCenterModifyTest.zkRegCenter.close();
        actual = client.getChildren().forPath((("/" + (ZookeeperRegistryCenterModifyTest.class.getName())) + "/sequential"));
        Assert.assertTrue(actual.isEmpty());
        ZookeeperRegistryCenterModifyTest.zkRegCenter.init();
    }

    @Test
    public void assertRemove() {
        ZookeeperRegistryCenterModifyTest.zkRegCenter.remove("/test");
        Assert.assertFalse(ZookeeperRegistryCenterModifyTest.zkRegCenter.isExisted("/test"));
    }
}

