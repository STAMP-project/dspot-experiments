/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio;


import Constants.FIRST_TIER;
import Constants.LAST_TIER;
import Constants.SECOND_TIER;
import PropertyKey.MASTER_TIERED_STORE_GLOBAL_LEVELS;
import PropertyKey.Template.MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_ALIAS;
import PropertyKey.WORKER_TIERED_STORE_LEVELS;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link StorageTierAssoc}.
 */
public final class StorageTierAssocTest {
    /**
     * Tests the constructors of the {@link MasterStorageTierAssoc} and {@link WorkerStorageTierAssoc}
     * classes with a {@link ServerConfiguration}.
     */
    @Test
    public void masterWorkerConfConstructor() throws Exception {
        try (Closeable c = toResource()) {
            checkStorageTierAssoc(new MasterStorageTierAssoc(), MASTER_TIERED_STORE_GLOBAL_LEVELS, MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS);
            checkStorageTierAssoc(new WorkerStorageTierAssoc(), WORKER_TIERED_STORE_LEVELS, WORKER_TIERED_STORE_LEVEL_ALIAS);
        }
    }

    /**
     * Tests the constructors of the {@link MasterStorageTierAssoc} and {@link WorkerStorageTierAssoc}
     * classes with different storage alias.
     */
    @Test
    public void storageAliasListConstructor() {
        List<String> orderedAliases = Arrays.asList("MEM", "HDD", "SOMETHINGELSE", "SSD");
        MasterStorageTierAssoc masterAssoc = new MasterStorageTierAssoc(orderedAliases);
        WorkerStorageTierAssoc workerAssoc = new WorkerStorageTierAssoc(orderedAliases);
        Assert.assertEquals(orderedAliases.size(), masterAssoc.size());
        Assert.assertEquals(orderedAliases.size(), workerAssoc.size());
        for (int i = 0; i < (orderedAliases.size()); i++) {
            String alias = orderedAliases.get(i);
            Assert.assertEquals(alias, masterAssoc.getAlias(i));
            Assert.assertEquals(i, masterAssoc.getOrdinal(alias));
            Assert.assertEquals(alias, workerAssoc.getAlias(i));
            Assert.assertEquals(i, workerAssoc.getOrdinal(alias));
        }
        Assert.assertEquals(orderedAliases, masterAssoc.getOrderedStorageAliases());
        Assert.assertEquals(orderedAliases, workerAssoc.getOrderedStorageAliases());
    }

    @Test
    public void interpretTier() throws Exception {
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(0, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(1, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(2, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal((-1), 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal((-2), 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal((-3), 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(FIRST_TIER, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(SECOND_TIER, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(LAST_TIER, 1));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(0, 10));
        Assert.assertEquals(8, StorageTierAssoc.interpretOrdinal(8, 10));
        Assert.assertEquals(9, StorageTierAssoc.interpretOrdinal(9, 10));
        Assert.assertEquals(9, StorageTierAssoc.interpretOrdinal(10, 10));
        Assert.assertEquals(9, StorageTierAssoc.interpretOrdinal((-1), 10));
        Assert.assertEquals(1, StorageTierAssoc.interpretOrdinal((-9), 10));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal((-10), 10));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal((-11), 10));
        Assert.assertEquals(0, StorageTierAssoc.interpretOrdinal(FIRST_TIER, 10));
        Assert.assertEquals(1, StorageTierAssoc.interpretOrdinal(SECOND_TIER, 10));
        Assert.assertEquals(9, StorageTierAssoc.interpretOrdinal(LAST_TIER, 10));
    }
}

