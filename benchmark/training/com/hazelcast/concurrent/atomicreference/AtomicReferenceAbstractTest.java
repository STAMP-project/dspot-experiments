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
package com.hazelcast.concurrent.atomicreference;


import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IFunction;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;


public abstract class AtomicReferenceAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected IAtomicReference<String> ref;

    @Test
    public void getAndSet() {
        Assert.assertNull(ref.getAndSet("foo"));
        Assert.assertEquals("foo", ref.getAndSet("bar"));
        Assert.assertEquals("bar", ref.getAndSet("bar"));
    }

    @Test
    public void isNull() {
        Assert.assertTrue(ref.isNull());
        ref.set("foo");
        Assert.assertFalse(ref.isNull());
    }

    @Test
    public void get() {
        Assert.assertNull(ref.get());
        ref.set("foo");
        Assert.assertEquals("foo", ref.get());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void setAndGet() {
        Assert.assertNull(ref.setAndGet(null));
        Assert.assertNull(ref.get());
        Assert.assertEquals("foo", ref.setAndGet("foo"));
        Assert.assertEquals("foo", ref.get());
        Assert.assertEquals("bar", ref.setAndGet("bar"));
        Assert.assertEquals("bar", ref.get());
        Assert.assertNull(ref.setAndGet(null));
        Assert.assertNull(ref.get());
    }

    @Test
    public void set() {
        ref.set(null);
        Assert.assertNull(ref.get());
        ref.set("foo");
        Assert.assertEquals("foo", ref.get());
        ref.set("bar");
        Assert.assertEquals("bar", ref.get());
        ref.set(null);
        Assert.assertNull(ref.get());
    }

    @Test
    public void clear() {
        ref.clear();
        Assert.assertNull(ref.get());
        ref.set("foo");
        ref.clear();
        Assert.assertNull(ref.get());
        ref.set(null);
        Assert.assertNull(ref.get());
    }

    @Test
    public void contains() {
        Assert.assertTrue(ref.contains(null));
        Assert.assertFalse(ref.contains("foo"));
        ref.set("foo");
        Assert.assertFalse(ref.contains(null));
        Assert.assertTrue(ref.contains("foo"));
        Assert.assertFalse(ref.contains("bar"));
    }

    @Test
    public void compareAndSet() {
        Assert.assertTrue(ref.compareAndSet(null, null));
        Assert.assertNull(ref.get());
        Assert.assertFalse(ref.compareAndSet("foo", "bar"));
        Assert.assertNull(ref.get());
        Assert.assertTrue(ref.compareAndSet(null, "foo"));
        Assert.assertEquals("foo", ref.get());
        ref.set("foo");
        Assert.assertTrue(ref.compareAndSet("foo", "foo"));
        Assert.assertEquals("foo", ref.get());
        Assert.assertTrue(ref.compareAndSet("foo", "bar"));
        Assert.assertEquals("bar", ref.get());
        Assert.assertTrue(ref.compareAndSet("bar", null));
        Assert.assertNull(ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_whenCalledWithNullFunction() {
        ref.apply(null);
    }

    @Test
    public void apply() {
        Assert.assertEquals("null", ref.apply(new AtomicReferenceAbstractTest.AppendFunction("")));
        Assert.assertNull(ref.get());
        ref.set("foo");
        Assert.assertEquals("foobar", ref.apply(new AtomicReferenceAbstractTest.AppendFunction("bar")));
        Assert.assertEquals("foo", ref.get());
        Assert.assertNull(ref.apply(new AtomicReferenceAbstractTest.NullFunction()));
        Assert.assertEquals("foo", ref.get());
    }

    @Test
    public void apply_whenException() {
        ref.set("foo");
        try {
            ref.apply(new AtomicReferenceAbstractTest.FailingFunction());
            Assert.fail();
        } catch (HazelcastException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals("foo", ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alter_whenCalledWithNullFunction() {
        ref.alter(null);
    }

    @Test
    public void alter_whenException() {
        ref.set("foo");
        try {
            ref.alter(new AtomicReferenceAbstractTest.FailingFunction());
            Assert.fail();
        } catch (HazelcastException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals("foo", ref.get());
    }

    @Test
    public void alter() {
        ref.alter(new AtomicReferenceAbstractTest.NullFunction());
        Assert.assertNull(ref.get());
        ref.set("foo");
        ref.alter(new AtomicReferenceAbstractTest.AppendFunction("bar"));
        Assert.assertEquals("foobar", ref.get());
        ref.alter(new AtomicReferenceAbstractTest.NullFunction());
        Assert.assertNull(ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alterAndGet_whenCalledWithNullFunction() {
        ref.alterAndGet(null);
    }

    @Test
    public void alterAndGet_whenException() {
        ref.set("foo");
        try {
            ref.alterAndGet(new AtomicReferenceAbstractTest.FailingFunction());
            Assert.fail();
        } catch (HazelcastException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals("foo", ref.get());
    }

    @Test
    public void alterAndGet() {
        Assert.assertNull(ref.alterAndGet(new AtomicReferenceAbstractTest.NullFunction()));
        Assert.assertNull(ref.get());
        ref.set("foo");
        Assert.assertEquals("foobar", ref.alterAndGet(new AtomicReferenceAbstractTest.AppendFunction("bar")));
        Assert.assertEquals("foobar", ref.get());
        Assert.assertNull(ref.alterAndGet(new AtomicReferenceAbstractTest.NullFunction()));
        Assert.assertNull(ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAndAlter_whenCalledWithNullFunction() {
        ref.getAndAlter(null);
    }

    @Test
    public void getAndAlter_whenException() {
        ref.set("foo");
        try {
            ref.getAndAlter(new AtomicReferenceAbstractTest.FailingFunction());
            Assert.fail();
        } catch (HazelcastException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals("foo", ref.get());
    }

    @Test
    public void getAndAlter() {
        Assert.assertNull(ref.getAndAlter(new AtomicReferenceAbstractTest.NullFunction()));
        Assert.assertNull(ref.get());
        ref.set("foo");
        Assert.assertEquals("foo", ref.getAndAlter(new AtomicReferenceAbstractTest.AppendFunction("bar")));
        Assert.assertEquals("foobar", ref.get());
        Assert.assertEquals("foobar", ref.getAndAlter(new AtomicReferenceAbstractTest.NullFunction()));
        Assert.assertNull(ref.get());
    }

    private static class AppendFunction implements IFunction<String, String> {
        private String add;

        private AppendFunction(String add) {
            this.add = add;
        }

        @Override
        public String apply(String input) {
            return input + (add);
        }
    }

    private static class NullFunction implements IFunction<String, String> {
        @Override
        public String apply(String input) {
            return null;
        }
    }

    protected static class FailingFunction implements IFunction<String, String> {
        @Override
        public String apply(String input) {
            throw new HazelcastException();
        }
    }

    @Test
    public void testToString() {
        String name = ref.getName();
        Assert.assertEquals(String.format("IAtomicReference{name='%s'}", name), ref.toString());
    }

    @Test
    public void getAndAlter_when_same_reference() {
        BitSet bitSet = new BitSet();
        IAtomicReference<BitSet> ref2 = newInstance();
        ref2.set(bitSet);
        Assert.assertEquals(bitSet, ref2.getAndAlter(new AtomicReferenceAbstractTest.FailingFunctionAlter()));
        bitSet.set(100);
        Assert.assertEquals(bitSet, ref2.get());
    }

    @Test
    public void alterAndGet_when_same_reference() {
        BitSet bitSet = new BitSet();
        IAtomicReference<BitSet> ref2 = newInstance();
        ref2.set(bitSet);
        bitSet.set(100);
        Assert.assertEquals(bitSet, ref2.alterAndGet(new AtomicReferenceAbstractTest.FailingFunctionAlter()));
        Assert.assertEquals(bitSet, ref2.get());
    }

    @Test
    public void alter_when_same_reference() {
        BitSet bitSet = new BitSet();
        IAtomicReference<BitSet> ref2 = newInstance();
        ref2.set(bitSet);
        bitSet.set(100);
        ref2.alter(new AtomicReferenceAbstractTest.FailingFunctionAlter());
        Assert.assertEquals(bitSet, ref2.get());
    }

    private static class FailingFunctionAlter implements IFunction<BitSet, BitSet> {
        @Override
        public BitSet apply(BitSet input) {
            input.set(100);
            return input;
        }
    }
}

