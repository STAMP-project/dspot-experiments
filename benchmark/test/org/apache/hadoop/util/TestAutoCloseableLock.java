/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * A test class for AutoCloseableLock.
 */
public class TestAutoCloseableLock {
    /**
     * Test the basic lock and unlock operation.
     */
    @Test
    public void testLockAcquireRelease() {
        AutoCloseableLock lock = new AutoCloseableLock();
        AutoCloseableLock newlock = lock.acquire();
        // Ensure acquire the same lock object.
        Assert.assertEquals(newlock, lock);
        // Ensure it locked now.
        Assert.assertTrue(lock.isLocked());
        lock.release();
        // Ensure it is unlocked now.
        Assert.assertFalse(lock.isLocked());
    }

    /**
     * Test when lock is acquired, no other thread can
     * lock it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMultipleThread() throws Exception {
        AutoCloseableLock lock = new AutoCloseableLock();
        lock.acquire();
        Assert.assertTrue(lock.isLocked());
        Thread competingThread = new Thread() {
            @Override
            public void run() {
                Assert.assertTrue(lock.isLocked());
                Assert.assertFalse(lock.tryLock());
            }
        };
        competingThread.start();
        competingThread.join();
        Assert.assertTrue(lock.isLocked());
        lock.release();
        Assert.assertFalse(lock.isLocked());
    }

    /**
     * Test the correctness under try-with-resource syntax.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTryWithResourceSyntax() throws Exception {
        AutoCloseableLock lock = new AutoCloseableLock();
        try (AutoCloseableLock localLock = lock.acquire()) {
            Assert.assertEquals(localLock, lock);
            Assert.assertTrue(lock.isLocked());
            Thread competingThread = new Thread() {
                @Override
                public void run() {
                    Assert.assertTrue(lock.isLocked());
                    Assert.assertFalse(lock.tryLock());
                }
            };
            competingThread.start();
            competingThread.join();
            Assert.assertTrue(localLock.isLocked());
        }
        Assert.assertFalse(lock.isLocked());
    }
}

