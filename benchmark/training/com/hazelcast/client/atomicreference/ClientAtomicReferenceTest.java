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
package com.hazelcast.client.atomicreference;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IFunction;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientAtomicReferenceTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private IAtomicReference<String> clientReference;

    private IAtomicReference<String> serverReference;

    @Test
    public void get() throws Exception {
        Assert.assertNull(clientReference.get());
        serverReference.set("foo");
        Assert.assertEquals("foo", clientReference.get());
    }

    @Test
    public void isNull() throws Exception {
        Assert.assertTrue(clientReference.isNull());
        serverReference.set("foo");
        Assert.assertFalse(clientReference.isNull());
    }

    @Test
    public void contains() {
        Assert.assertTrue(clientReference.contains(null));
        Assert.assertFalse(clientReference.contains("foo"));
        serverReference.set("foo");
        Assert.assertFalse(clientReference.contains(null));
        Assert.assertTrue(clientReference.contains("foo"));
        Assert.assertFalse(clientReference.contains("bar"));
    }

    @Test
    public void set() throws Exception {
        clientReference.set(null);
        Assert.assertTrue(serverReference.isNull());
        clientReference.set("foo");
        Assert.assertEquals("foo", serverReference.get());
        clientReference.set("foo");
        Assert.assertEquals("foo", serverReference.get());
        clientReference.set("bar");
        Assert.assertEquals("bar", serverReference.get());
        clientReference.set(null);
        Assert.assertTrue(serverReference.isNull());
    }

    @Test
    public void clear() throws Exception {
        clientReference.clear();
        Assert.assertTrue(serverReference.isNull());
        serverReference.set("foo");
        clientReference.clear();
        Assert.assertTrue(serverReference.isNull());
    }

    @Test
    public void getAndSet() throws Exception {
        Assert.assertNull(clientReference.getAndSet(null));
        Assert.assertTrue(serverReference.isNull());
        Assert.assertNull(clientReference.getAndSet("foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertEquals("foo", clientReference.getAndSet("foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertEquals("foo", clientReference.getAndSet("bar"));
        Assert.assertEquals("bar", serverReference.get());
        Assert.assertEquals("bar", clientReference.getAndSet(null));
        Assert.assertTrue(serverReference.isNull());
    }

    @Test
    public void setAndGet() throws Exception {
        Assert.assertNull(clientReference.setAndGet(null));
        Assert.assertTrue(serverReference.isNull());
        Assert.assertEquals("foo", clientReference.setAndGet("foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertEquals("foo", clientReference.setAndGet("foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertEquals("bar", clientReference.setAndGet("bar"));
        Assert.assertEquals("bar", serverReference.get());
        Assert.assertNull(clientReference.setAndGet(null));
        Assert.assertTrue(serverReference.isNull());
    }

    @Test
    public void compareAndSet() throws Exception {
        Assert.assertTrue(clientReference.compareAndSet(null, null));
        Assert.assertTrue(serverReference.isNull());
        Assert.assertFalse(clientReference.compareAndSet("foo", null));
        Assert.assertTrue(serverReference.isNull());
        Assert.assertTrue(clientReference.compareAndSet(null, "foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertTrue(clientReference.compareAndSet("foo", "foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertFalse(clientReference.compareAndSet("bar", "foo"));
        Assert.assertEquals("foo", serverReference.get());
        Assert.assertTrue(clientReference.compareAndSet("foo", "bar"));
        Assert.assertEquals("bar", serverReference.get());
        Assert.assertTrue(clientReference.compareAndSet("bar", null));
        Assert.assertNull(serverReference.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_whenCalledWithNullFunction() {
        clientReference.apply(null);
    }

    @Test
    public void apply() {
        Assert.assertEquals("null", clientReference.apply(new ClientAtomicReferenceTest.AppendFunction("")));
        Assert.assertNull(clientReference.get());
        clientReference.set("foo");
        Assert.assertEquals("foobar", clientReference.apply(new ClientAtomicReferenceTest.AppendFunction("bar")));
        Assert.assertEquals("foo", clientReference.get());
        Assert.assertNull(clientReference.apply(new ClientAtomicReferenceTest.NullFunction()));
        Assert.assertEquals("foo", clientReference.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alter_whenCalledWithNullFunction() {
        clientReference.alter(null);
    }

    @Test
    public void alter() {
        clientReference.alter(new ClientAtomicReferenceTest.NullFunction());
        Assert.assertNull(clientReference.get());
        clientReference.set("foo");
        clientReference.alter(new ClientAtomicReferenceTest.AppendFunction("bar"));
        Assert.assertEquals("foobar", clientReference.get());
        clientReference.alter(new ClientAtomicReferenceTest.NullFunction());
        Assert.assertNull(clientReference.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alterAndGet_whenCalledWithNullFunction() {
        clientReference.alterAndGet(null);
    }

    @Test
    public void alterAndGet() {
        Assert.assertNull(clientReference.alterAndGet(new ClientAtomicReferenceTest.NullFunction()));
        Assert.assertNull(clientReference.get());
        clientReference.set("foo");
        Assert.assertEquals("foobar", clientReference.alterAndGet(new ClientAtomicReferenceTest.AppendFunction("bar")));
        Assert.assertEquals("foobar", clientReference.get());
        Assert.assertNull(clientReference.alterAndGet(new ClientAtomicReferenceTest.NullFunction()));
        Assert.assertNull(clientReference.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAndAlter_whenCalledWithNullFunction() {
        clientReference.alterAndGet(null);
    }

    @Test
    public void getAndAlter() {
        Assert.assertNull(clientReference.getAndAlter(new ClientAtomicReferenceTest.NullFunction()));
        Assert.assertNull(clientReference.get());
        clientReference.set("foo");
        Assert.assertEquals("foo", clientReference.getAndAlter(new ClientAtomicReferenceTest.AppendFunction("bar")));
        Assert.assertEquals("foobar", clientReference.get());
        Assert.assertEquals("foobar", clientReference.getAndAlter(new ClientAtomicReferenceTest.NullFunction()));
        Assert.assertNull(clientReference.get());
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
}

