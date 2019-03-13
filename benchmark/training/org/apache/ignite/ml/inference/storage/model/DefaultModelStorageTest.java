/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.inference.storage.model;


import java.util.concurrent.locks.Lock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link DefaultModelStorage}.
 */
public class DefaultModelStorageTest extends AbstractModelStorageTest {
    /**
     *
     */
    @Test
    public void testSynchronize() {
        Lock[] locks = new Lock[10];
        for (int i = 0; i < (locks.length); i++)
            locks[i] = Mockito.mock(Lock.class);

        DefaultModelStorage.synchronize(() -> {
        }, locks);
        for (Lock lock : locks) {
            Mockito.verify(lock, Mockito.times(1)).lock();
            Mockito.verify(lock, Mockito.times(1)).unlock();
            Mockito.verifyNoMoreInteractions(lock);
        }
    }

    /**
     *
     */
    @Test
    public void testSynchronizeWithExceptionInTask() {
        Lock[] locks = new Lock[10];
        for (int i = 0; i < (locks.length); i++)
            locks[i] = Mockito.mock(Lock.class);

        RuntimeException ex = new RuntimeException();
        try {
            DefaultModelStorage.synchronize(() -> {
                throw ex;
            }, locks);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(ex, e);
        }
        for (Lock lock : locks) {
            Mockito.verify(lock, Mockito.times(1)).lock();
            Mockito.verify(lock, Mockito.times(1)).unlock();
            Mockito.verifyNoMoreInteractions(lock);
        }
    }

    /**
     *
     */
    @Test
    public void testSynchronizeWithExceptionInLock() {
        Lock[] locks = new Lock[10];
        for (int i = 0; i < (locks.length); i++)
            locks[i] = Mockito.mock(Lock.class);

        RuntimeException ex = new RuntimeException();
        Mockito.doThrow(ex).when(locks[5]).lock();
        try {
            DefaultModelStorage.synchronize(() -> {
            }, locks);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(ex, e);
        }
        for (int i = 0; i < (locks.length); i++) {
            if (i <= 4) {
                Mockito.verify(locks[i], Mockito.times(1)).lock();
                Mockito.verify(locks[i], Mockito.times(1)).unlock();
            } else
                if (i > 5) {
                    Mockito.verify(locks[i], Mockito.times(0)).lock();
                    Mockito.verify(locks[i], Mockito.times(0)).unlock();
                } else {
                    Mockito.verify(locks[i], Mockito.times(1)).lock();
                    Mockito.verify(locks[i], Mockito.times(0)).unlock();
                }

            Mockito.verifyNoMoreInteractions(locks[i]);
        }
    }

    /**
     *
     */
    @Test
    public void testSynchronizeWithExceptionInUnlock() {
        Lock[] locks = new Lock[10];
        for (int i = 0; i < (locks.length); i++)
            locks[i] = Mockito.mock(Lock.class);

        RuntimeException ex = new RuntimeException();
        Mockito.doThrow(ex).when(locks[5]).unlock();
        try {
            DefaultModelStorage.synchronize(() -> {
            }, locks);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(ex, e);
        }
        for (Lock lock : locks) {
            Mockito.verify(lock, Mockito.times(1)).lock();
            Mockito.verify(lock, Mockito.times(1)).unlock();
            Mockito.verifyNoMoreInteractions(lock);
        }
    }
}

