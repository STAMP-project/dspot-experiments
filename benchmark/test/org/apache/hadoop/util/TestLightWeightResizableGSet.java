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


import LightWeightResizableGSet.LinkedElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing {@link LightWeightResizableGSet}
 */
public class TestLightWeightResizableGSet {
    public static final Logger LOG = LoggerFactory.getLogger(TestLightWeightResizableGSet.class);

    private Random random = new Random();

    private static class TestKey {
        private final long key;

        TestKey(long key) {
            this.key = key;
        }

        TestKey(TestLightWeightResizableGSet.TestKey other) {
            this.key = other.key;
        }

        long getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return ((int) ((key) ^ ((key) >>> 32)));
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof TestLightWeightResizableGSet.TestKey)) {
                return false;
            }
            TestLightWeightResizableGSet.TestKey other = ((TestLightWeightResizableGSet.TestKey) (o));
            return (key) == (other.key);
        }
    }

    private static class TestElement extends TestLightWeightResizableGSet.TestKey implements LightWeightResizableGSet.LinkedElement {
        private final long data;

        private LinkedElement next;

        TestElement(long key, long data) {
            super(key);
            this.data = data;
        }

        TestElement(TestLightWeightResizableGSet.TestKey key, long data) {
            super(key);
            this.data = data;
        }

        long getData() {
            return data;
        }

        @Override
        public void setNext(LightWeightResizableGSet.LinkedElement next) {
            this.next = next;
        }

        @Override
        public LinkedElement getNext() {
            return next;
        }
    }

    @Test(timeout = 60000)
    public void testBasicOperations() {
        TestLightWeightResizableGSet.TestElement[] elements = generateElements((1 << 16));
        final LightWeightResizableGSet<TestLightWeightResizableGSet.TestKey, TestLightWeightResizableGSet.TestElement> set = new LightWeightResizableGSet<TestLightWeightResizableGSet.TestKey, TestLightWeightResizableGSet.TestElement>();
        Assert.assertEquals(set.size(), 0);
        // put all elements
        for (int i = 0; i < (elements.length); i++) {
            TestLightWeightResizableGSet.TestElement element = set.put(elements[i]);
            Assert.assertTrue((element == null));
        }
        // check the set size
        Assert.assertEquals(set.size(), elements.length);
        // check all elements exist in the set and the data is correct
        for (int i = 0; i < (elements.length); i++) {
            Assert.assertTrue(set.contains(elements[i]));
            TestLightWeightResizableGSet.TestElement element = set.get(elements[i]);
            Assert.assertEquals(elements[i].getData(), element.getData());
        }
        TestLightWeightResizableGSet.TestKey[] keys = getKeys(elements);
        // generate new elements with same key, but new data
        TestLightWeightResizableGSet.TestElement[] newElements = generateElements(keys);
        // update the set
        for (int i = 0; i < (newElements.length); i++) {
            TestLightWeightResizableGSet.TestElement element = set.put(newElements[i]);
            Assert.assertTrue((element != null));
        }
        // check the set size
        Assert.assertEquals(set.size(), elements.length);
        // check all elements exist in the set and the data is updated to new value
        for (int i = 0; i < (keys.length); i++) {
            Assert.assertTrue(set.contains(keys[i]));
            TestLightWeightResizableGSet.TestElement element = set.get(keys[i]);
            Assert.assertEquals(newElements[i].getData(), element.getData());
        }
        // test LightWeightHashGSet#values
        Collection<TestLightWeightResizableGSet.TestElement> cElements = set.values();
        Assert.assertEquals(cElements.size(), elements.length);
        for (TestLightWeightResizableGSet.TestElement element : cElements) {
            Assert.assertTrue(set.contains(element));
        }
        // remove elements
        for (int i = 0; i < (keys.length); i++) {
            TestLightWeightResizableGSet.TestElement element = set.remove(keys[i]);
            Assert.assertTrue((element != null));
            // the element should not exist after remove
            Assert.assertFalse(set.contains(keys[i]));
        }
        // check the set size
        Assert.assertEquals(set.size(), 0);
    }

    @Test(timeout = 60000)
    public void testRemoveAll() {
        TestLightWeightResizableGSet.TestElement[] elements = generateElements((1 << 16));
        final LightWeightResizableGSet<TestLightWeightResizableGSet.TestKey, TestLightWeightResizableGSet.TestElement> set = new LightWeightResizableGSet<TestLightWeightResizableGSet.TestKey, TestLightWeightResizableGSet.TestElement>();
        Assert.assertEquals(set.size(), 0);
        // put all elements
        for (int i = 0; i < (elements.length); i++) {
            TestLightWeightResizableGSet.TestElement element = set.put(elements[i]);
            Assert.assertTrue((element == null));
        }
        // check the set size
        Assert.assertEquals(set.size(), elements.length);
        // remove all through clear
        {
            set.clear();
            Assert.assertEquals(set.size(), 0);
            // check all elements removed
            for (int i = 0; i < (elements.length); i++) {
                Assert.assertFalse(set.contains(elements[i]));
            }
            Assert.assertFalse(set.iterator().hasNext());
        }
        // put all elements back
        for (int i = 0; i < (elements.length); i++) {
            TestLightWeightResizableGSet.TestElement element = set.put(elements[i]);
            Assert.assertTrue((element == null));
        }
        // remove all through iterator
        {
            for (Iterator<TestLightWeightResizableGSet.TestElement> iter = set.iterator(); iter.hasNext();) {
                TestLightWeightResizableGSet.TestElement element = iter.next();
                // element should be there before removing
                Assert.assertTrue(set.contains(element));
                iter.remove();
                // element should not be there now
                Assert.assertFalse(set.contains(element));
            }
            // the deleted elements should not be there
            for (int i = 0; i < (elements.length); i++) {
                Assert.assertFalse(set.contains(elements[i]));
            }
            // iterator should not have next
            Assert.assertFalse(set.iterator().hasNext());
            // check the set size
            Assert.assertEquals(set.size(), 0);
        }
    }
}

