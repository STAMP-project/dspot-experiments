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
package com.hazelcast.core;


import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class DistributedObjectListenerTest extends HazelcastTestSupport {
    @Test
    public void testDestroyJustAfterCreate() {
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance();
        instance.addDistributedObjectListener(new DistributedObjectListenerTest.EventCountListener());
        IMap<Object, Object> map = instance.getMap(HazelcastTestSupport.randomString());
        map.destroy();
        AssertTask task = new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, DistributedObjectListenerTest.EventCountListener.createdCount.get());
                Assert.assertEquals(1, DistributedObjectListenerTest.EventCountListener.destroyedCount.get());
                Collection<DistributedObject> distributedObjects = instance.getDistributedObjects();
                Assert.assertTrue(distributedObjects.isEmpty());
            }
        };
        HazelcastTestSupport.assertTrueEventually(task, 5);
        HazelcastTestSupport.assertTrueAllTheTime(task, 3);
    }

    public static class EventCountListener implements DistributedObjectListener {
        public static AtomicInteger createdCount = new AtomicInteger();

        public static AtomicInteger destroyedCount = new AtomicInteger();

        public void distributedObjectCreated(DistributedObjectEvent event) {
            DistributedObjectListenerTest.EventCountListener.createdCount.incrementAndGet();
        }

        public void distributedObjectDestroyed(DistributedObjectEvent event) {
            DistributedObjectListenerTest.EventCountListener.destroyedCount.incrementAndGet();
        }
    }
}

