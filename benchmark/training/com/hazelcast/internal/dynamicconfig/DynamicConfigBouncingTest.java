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
package com.hazelcast.internal.dynamicconfig;


import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.bounce.BounceMemberRule;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ SlowTest.class, ParallelTest.class })
public class DynamicConfigBouncingTest extends HazelcastTestSupport {
    @Rule
    public BounceMemberRule bounceMemberRule = BounceMemberRule.with(getConfig()).clusterSize(4).driverCount(1).useTerminate().build();

    @Test
    public void doNotThrowExceptionWhenMemberIsGone() {
        Runnable[] methods = new Runnable[1];
        final String mapName = HazelcastTestSupport.randomMapName();
        final HazelcastInstance testDriver = bounceMemberRule.getNextTestDriver();
        methods[0] = new DynamicConfigBouncingTest.SubmitDynamicMapConfig(mapName, testDriver);
        bounceMemberRule.testRepeatedly(methods, 60);
        HazelcastInstance instance = bounceMemberRule.getSteadyMember();
        MapConfig mapConfig = instance.getConfig().getMapConfig(mapName);
        Assert.assertEquals(DynamicConfigBouncingTest.createMapConfig(mapName), mapConfig);
    }

    private static class MyEntryUpdatedListener implements EntryUpdatedListener , Serializable {
        @Override
        public void entryUpdated(EntryEvent event) {
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return getClass().equals(obj.getClass());
        }
    }

    private static class MyEntryListener implements EntryListener , Serializable {
        @Override
        public void entryAdded(EntryEvent event) {
        }

        @Override
        public void entryUpdated(EntryEvent event) {
        }

        @Override
        public void entryRemoved(EntryEvent event) {
        }

        @Override
        public void mapCleared(MapEvent event) {
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }

        @Override
        public void entryEvicted(EntryEvent event) {
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return getClass().equals(obj.getClass());
        }
    }

    private static class SubmitDynamicMapConfig implements Runnable {
        private final String mapName;

        private final HazelcastInstance testDriver;

        SubmitDynamicMapConfig(String mapName, HazelcastInstance testDriver) {
            this.mapName = mapName;
            this.testDriver = testDriver;
        }

        @Override
        public void run() {
            MapConfig mapConfig = DynamicConfigBouncingTest.createMapConfig(mapName);
            testDriver.getConfig().addMapConfig(mapConfig);
        }
    }
}

