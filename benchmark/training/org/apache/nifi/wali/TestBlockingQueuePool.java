/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.wali;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class TestBlockingQueuePool {
    private static final Consumer<AtomicBoolean> DO_NOTHING = ( ab) -> {
    };

    @Test
    public void testReuse() {
        final BlockingQueuePool<AtomicBoolean> pool = new BlockingQueuePool(10, AtomicBoolean::new, AtomicBoolean::get, TestBlockingQueuePool.DO_NOTHING);
        final AtomicBoolean firstObject = pool.borrowObject();
        firstObject.set(true);
        pool.returnObject(firstObject);
        for (int i = 0; i < 100; i++) {
            final AtomicBoolean value = pool.borrowObject();
            Assert.assertSame(firstObject, value);
            pool.returnObject(value);
        }
    }

    @Test
    public void testCreateOnExhaustion() {
        final BlockingQueuePool<AtomicBoolean> pool = new BlockingQueuePool(10, AtomicBoolean::new, AtomicBoolean::get, TestBlockingQueuePool.DO_NOTHING);
        final AtomicBoolean firstObject = pool.borrowObject();
        final AtomicBoolean secondObject = pool.borrowObject();
        Assert.assertNotSame(firstObject, secondObject);
    }

    @Test
    public void testCreateMoreThanMaxCapacity() {
        final BlockingQueuePool<AtomicBoolean> pool = new BlockingQueuePool(10, AtomicBoolean::new, AtomicBoolean::get, TestBlockingQueuePool.DO_NOTHING);
        for (int i = 0; i < 50; i++) {
            final AtomicBoolean value = pool.borrowObject();
            Assert.assertNotNull(value);
        }
    }

    @Test
    public void testDoesNotBufferMoreThanCapacity() {
        final BlockingQueuePool<AtomicBoolean> pool = new BlockingQueuePool(10, AtomicBoolean::new, AtomicBoolean::get, TestBlockingQueuePool.DO_NOTHING);
        final AtomicBoolean[] seen = new AtomicBoolean[50];
        for (int i = 0; i < 50; i++) {
            final AtomicBoolean value = pool.borrowObject();
            Assert.assertNotNull(value);
            value.set(true);
            seen[i] = value;
        }
        for (final AtomicBoolean value : seen) {
            pool.returnObject(value);
        }
        for (int i = 0; i < 10; i++) {
            final AtomicBoolean value = pool.borrowObject();
            // verify that the object exists in the 'seen' array
            boolean found = false;
            for (final AtomicBoolean seenBoolean : seen) {
                if (value == seenBoolean) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
        for (int i = 0; i < 40; i++) {
            final AtomicBoolean value = pool.borrowObject();
            // verify that the object does not exist in the 'seen' array
            boolean found = false;
            for (final AtomicBoolean seenBoolean : seen) {
                if (value == seenBoolean) {
                    found = true;
                    break;
                }
            }
            Assert.assertFalse(found);
        }
    }
}

