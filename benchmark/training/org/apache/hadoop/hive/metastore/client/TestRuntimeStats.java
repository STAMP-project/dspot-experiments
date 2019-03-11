/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.client;


import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.ObjectStore;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.RuntimeStat;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category(MetastoreUnitTest.class)
public class TestRuntimeStats extends MetaStoreClientTest {
    private final AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    public TestRuntimeStats(String name, AbstractMetaStoreService metaStore) throws Exception {
        this.metaStore = metaStore;
    }

    @Test
    public void testRuntimeStatHandling() throws Exception {
        List<RuntimeStat> rs0 = getRuntimeStats();
        Assert.assertNotNull(rs0);
        Assert.assertEquals(0, rs0.size());
        RuntimeStat stat = createStat(1);
        client.addRuntimeStat(stat);
        List<RuntimeStat> rs1 = getRuntimeStats();
        Assert.assertNotNull(rs1);
        Assert.assertEquals(1, rs1.size());
        Assert.assertArrayEquals(stat.getPayload(), rs1.get(0).getPayload());
        Assert.assertEquals(stat.getWeight(), rs1.get(0).getWeight());
        // server sets createtime
        Assert.assertNotEquals(stat.getCreateTime(), rs1.get(0).getCreateTime());
        client.addRuntimeStat(createStat(2));
        client.addRuntimeStat(createStat(3));
        client.addRuntimeStat(createStat(4));
        List<RuntimeStat> rs2 = getRuntimeStats();
        Assert.assertEquals(4, rs2.size());
    }

    @Test
    public void testCleanup() throws Exception {
        ObjectStore objStore = new ObjectStore();
        objStore.setConf(metaStore.getConf());
        objStore.deleteRuntimeStats(0);
        objStore.addRuntimeStat(createStat(1));
        TimeUnit.SECONDS.sleep(6);
        objStore.addRuntimeStat(createStat(2));
        int deleted = objStore.deleteRuntimeStats(5);
        Assert.assertEquals(1, deleted);
        List<RuntimeStat> all = getRuntimeStats();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(2, all.get(0).getWeight());
    }

    @Test
    public void testReading() throws Exception {
        ObjectStore objStore = new ObjectStore();
        objStore.setConf(metaStore.getConf());
        objStore.deleteRuntimeStats(0);
        objStore.addRuntimeStat(createStat(1));
        Thread.sleep(1000);
        objStore.addRuntimeStat(createStat(2));
        Thread.sleep(1000);
        objStore.addRuntimeStat(createStat(3));
        List<RuntimeStat> g0 = client.getRuntimeStats(3, (-1));
        Assert.assertEquals(1, g0.size());
        Assert.assertEquals(3, g0.get(0).getWeight());
        int ct = g0.get(0).getCreateTime();
        List<RuntimeStat> g1 = client.getRuntimeStats(3, ct);
        Assert.assertEquals(2, g1.size());
        Assert.assertEquals(2, g1.get(0).getWeight());
        Assert.assertEquals(1, g1.get(1).getWeight());
        int ct1 = g1.get(1).getCreateTime();
        List<RuntimeStat> g2 = client.getRuntimeStats(3, ct1);
        Assert.assertEquals(0, g2.size());
    }
}

