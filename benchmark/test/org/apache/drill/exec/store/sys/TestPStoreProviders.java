/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.sys;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.TestWithZookeeper;
import org.apache.drill.exec.coord.zk.PathUtils;
import org.apache.drill.exec.coord.zk.ZookeeperClient;
import org.apache.drill.exec.server.options.PersistedOptionValue;
import org.apache.drill.exec.store.sys.store.ZookeeperPersistentStore;
import org.apache.drill.exec.store.sys.store.provider.LocalPersistentStoreProvider;
import org.apache.drill.exec.store.sys.store.provider.ZookeeperPersistentStoreProvider;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class })
public class TestPStoreProviders extends TestWithZookeeper {
    @Rule
    public BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void verifyLocalStore() throws Exception {
        try (LocalPersistentStoreProvider provider = new LocalPersistentStoreProvider(DrillConfig.create())) {
            PStoreTestUtil.test(provider);
        }
    }

    @Test
    public void verifyZkStore() throws Exception {
        try (CuratorFramework curator = createCurator()) {
            curator.start();
            ZookeeperPersistentStoreProvider provider = new ZookeeperPersistentStoreProvider(zkHelper.getConfig(), curator);
            PStoreTestUtil.test(provider);
        }
    }

    /**
     * DRILL-5809
     * Note: If this test breaks you are probably breaking backward and forward compatibility. Verify with the community
     * that breaking compatibility is acceptable and planned for.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void localBackwardCompatabilityTest() throws Exception {
        localLoadTestHelper("src/test/resources/options/store/local/old/sys.options");
    }

    /**
     * DRILL-5809
     * Note: If this test breaks you are probably breaking backward and forward compatibility. Verify with the community
     * that breaking compatibility is acceptable and planned for.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void zkBackwardCompatabilityTest() throws Exception {
        final String oldName = "myOldOption";
        try (CuratorFramework curator = createCurator()) {
            curator.start();
            PersistentStoreConfig<PersistedOptionValue> storeConfig = PersistentStoreConfig.newJacksonBuilder(new ObjectMapper(), PersistedOptionValue.class).name("sys.test").build();
            try (ZookeeperClient zkClient = new ZookeeperClient(curator, PathUtils.join("/", storeConfig.getName()), CreateMode.PERSISTENT)) {
                zkClient.start();
                String oldOptionJson = DrillFileUtils.getResourceAsString("/options/old_booleanopt.json");
                zkClient.put(oldName, oldOptionJson.getBytes(), null);
            }
            try (ZookeeperPersistentStoreProvider provider = new ZookeeperPersistentStoreProvider(zkHelper.getConfig(), curator)) {
                PersistentStore<PersistedOptionValue> store = provider.getOrCreateStore(storeConfig);
                Assert.assertTrue((store instanceof ZookeeperPersistentStore));
                PersistedOptionValue oldOptionValue = ((ZookeeperPersistentStore<PersistedOptionValue>) (store)).get(oldName, null);
                PersistedOptionValue expectedValue = new PersistedOptionValue("true");
                Assert.assertEquals(expectedValue, oldOptionValue);
            }
        }
    }
}

