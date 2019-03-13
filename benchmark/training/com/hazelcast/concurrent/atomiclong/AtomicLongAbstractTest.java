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
package com.hazelcast.concurrent.atomiclong;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IFunction;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Assert;
import org.junit.Test;


public abstract class AtomicLongAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected IAtomicLong atomicLong;

    @Test
    public void testSet() {
        atomicLong.set(271);
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(0, atomicLong.get());
    }

    @Test
    public void testDecrementAndGet() {
        Assert.assertEquals((-1), atomicLong.decrementAndGet());
        Assert.assertEquals((-2), atomicLong.decrementAndGet());
    }

    @Test
    public void testIncrementAndGet() {
        Assert.assertEquals(1, atomicLong.incrementAndGet());
        Assert.assertEquals(2, atomicLong.incrementAndGet());
    }

    @Test
    public void testGetAndSet() {
        Assert.assertEquals(0, atomicLong.getAndSet(271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testAddAndGet() {
        Assert.assertEquals(271, atomicLong.addAndGet(271));
    }

    @Test
    public void testGetAndAdd() {
        Assert.assertEquals(0, atomicLong.getAndAdd(271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testCompareAndSet_whenSuccess() {
        Assert.assertTrue(atomicLong.compareAndSet(0, 271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testCompareAndSet_whenNotSuccess() {
        Assert.assertFalse(atomicLong.compareAndSet(172, 0));
        Assert.assertEquals(0, atomicLong.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_whenCalledWithNullFunction() {
        atomicLong.apply(null);
    }

    @Test
    public void apply() {
        Assert.assertEquals(new Long(1), atomicLong.apply(new AtomicLongAbstractTest.AddOneFunction()));
        Assert.assertEquals(0, atomicLong.get());
    }

    @Test
    public void apply_whenException() {
        atomicLong.set(1);
        try {
            atomicLong.apply(new AtomicLongAbstractTest.FailingFunction());
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(1, atomicLong.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alter_whenCalledWithNullFunction() {
        atomicLong.alter(null);
    }

    @Test
    public void alter_whenException() {
        atomicLong.set(10);
        try {
            atomicLong.alter(new AtomicLongAbstractTest.FailingFunction());
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(10, atomicLong.get());
    }

    @Test
    public void alter() {
        atomicLong.set(10);
        atomicLong.alter(new AtomicLongAbstractTest.AddOneFunction());
        Assert.assertEquals(11, atomicLong.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alterAndGet_whenCalledWithNullFunction() {
        atomicLong.alterAndGet(null);
    }

    @Test
    public void alterAndGet_whenException() {
        atomicLong.set(10);
        try {
            atomicLong.alterAndGet(new AtomicLongAbstractTest.FailingFunction());
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(10, atomicLong.get());
    }

    @Test
    public void alterAndGet() {
        atomicLong.set(10);
        Assert.assertEquals(11, atomicLong.alterAndGet(new AtomicLongAbstractTest.AddOneFunction()));
        Assert.assertEquals(11, atomicLong.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAndAlter_whenCalledWithNullFunction() {
        atomicLong.getAndAlter(null);
    }

    @Test
    public void getAndAlter_whenException() {
        atomicLong.set(10);
        try {
            atomicLong.getAndAlter(new AtomicLongAbstractTest.FailingFunction());
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(10, atomicLong.get());
    }

    @Test
    public void getAndAlter() {
        atomicLong.set(10);
        Assert.assertEquals(10, atomicLong.getAndAlter(new AtomicLongAbstractTest.AddOneFunction()));
        Assert.assertEquals(11, atomicLong.get());
    }

    @Test
    public void testDestroy() {
        atomicLong.set(23);
        atomicLong.destroy();
        Assert.assertEquals(0, atomicLong.get());
    }

    private static class AddOneFunction implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            return input + 1;
        }
    }

    protected static class FailingFunction implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            throw new ExpectedRuntimeException();
        }
    }
}

