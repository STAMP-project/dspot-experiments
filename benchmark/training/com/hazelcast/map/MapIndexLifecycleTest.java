/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapIndexLifecycleTest extends HazelcastTestSupport {
    private static final int BOOK_COUNT = 1000;

    private String mapName = HazelcastTestSupport.randomMapName();

    @Test
    public void recordStoresAndIndexes_createdDestroyedProperly() {
        // GIVEN
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance1 = createNode(instanceFactory);
        // THEN - initialized
        IMap bookMap = instance1.getMap(mapName);
        fillMap(bookMap);
        Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.size());
        assertAllPartitionContainersAreInitialized(instance1);
        // THEN - destroyed
        bookMap.destroy();
        assertAllPartitionContainersAreEmpty(instance1);
        // THEN - initialized
        bookMap = instance1.getMap(mapName);
        fillMap(bookMap);
        Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.size());
        assertAllPartitionContainersAreInitialized(instance1);
    }

    @Test
    public void whenIndexConfigured_existsOnAllMembers() {
        // GIVEN indexes are configured before Hazelcast starts
        int clusterSize = 3;
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(clusterSize);
        HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        instances[0] = createNode(instanceFactory);
        IMap<Integer, MapIndexLifecycleTest.Book> bookMap = instances[0].getMap(mapName);
        fillMap(bookMap);
        Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.size());
        // THEN indexes are migrated and populated on all members
        for (int i = 1; i < clusterSize; i++) {
            instances[i] = createNode(instanceFactory);
            HazelcastTestSupport.waitAllForSafeState(Arrays.copyOfRange(instances, 0, (i + 1)));
            bookMap = instances[i].getMap(mapName);
            Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.keySet().size());
            assertAllPartitionContainersAreInitialized(instances[i]);
            assertGlobalIndexesAreInitialized(instances[i]);
        }
    }

    @Test
    public void whenIndexAddedProgrammatically_existsOnAllMembers() {
        // GIVEN indexes are configured before Hazelcast starts
        int clusterSize = 3;
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(clusterSize);
        HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        Config config = getConfig().setProperty(PARTITION_COUNT.getName(), "4");
        config.getMapConfig(mapName);
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setName("SlowPostJoinAwareService").setEnabled(true).setImplementation(new MapIndexLifecycleTest.SlowPostJoinAwareService()));
        instances[0] = instanceFactory.newHazelcastInstance(config);
        IMap<Integer, MapIndexLifecycleTest.Book> bookMap = instances[0].getMap(mapName);
        fillMap(bookMap);
        bookMap.addIndex("author", false);
        bookMap.addIndex("year", true);
        Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.size());
        // THEN indexes are migrated and populated on all members
        for (int i = 1; i < clusterSize; i++) {
            instances[i] = instanceFactory.newHazelcastInstance(config);
            HazelcastTestSupport.waitAllForSafeState(Arrays.copyOfRange(instances, 0, (i + 1)));
            bookMap = instances[i].getMap(mapName);
            Assert.assertEquals(MapIndexLifecycleTest.BOOK_COUNT, bookMap.keySet().size());
            assertAllPartitionContainersAreInitialized(instances[i]);
            assertGlobalIndexesAreInitialized(instances[i]);
        }
    }

    public static class Book implements Serializable {
        private long id;

        private String title;

        private String author;

        private int year;

        private Book() {
        }

        Book(long id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }
    }

    // A CoreService with a slow post-join op. Its post-join operation will be executed before map's
    // post-join operation so we can ensure indexes are created via MapReplicationOperation,
    // even though PostJoinMapOperation has not yet been executed.
    public static class SlowPostJoinAwareService implements CoreService , PostJoinAwareService {
        @Override
        public Operation getPostJoinOperation() {
            return new MapIndexLifecycleTest.SlowOperation();
        }
    }

    public static class SlowOperation extends Operation {
        @Override
        public void run() {
            HazelcastTestSupport.sleepSeconds(60);
        }
    }
}

