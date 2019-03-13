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
package com.hazelcast.collection.impl.queue;


import com.hazelcast.core.IQueue;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueueIteratorTest extends HazelcastTestSupport {
    @Test
    public void testIterator() {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Iterator<String> iterator = queue.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Object o = iterator.next();
            Assert.assertEquals(o, ("item" + (i++)));
        } 
    }

    @Test
    public void testIterator_whenQueueEmpty() {
        IQueue<String> queue = newQueue();
        Iterator<String> iterator = queue.iterator();
        Assert.assertFalse(iterator.hasNext());
        try {
            Assert.assertNull(iterator.next());
            Assert.fail();
        } catch (NoSuchElementException e) {
            HazelcastTestSupport.ignore(e);
        }
    }

    @Test
    public void testIteratorRemove() {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Iterator<String> iterator = queue.iterator();
        iterator.next();
        try {
            iterator.remove();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(10, queue.size());
    }
}

