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
package org.apache.hadoop.hbase.util;


import java.util.Arrays;
import java.util.Iterator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestConcatenatedLists {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestConcatenatedLists.class);

    @Test
    public void testUnsupportedOps() {
        // If adding support, add tests.
        ConcatenatedLists<Long> c = new ConcatenatedLists();
        c.addSublist(Arrays.asList(0L, 1L));
        try {
            c.add(2L);
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            c.addAll(Arrays.asList(2L, 3L));
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            c.remove(0L);
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            c.removeAll(Arrays.asList(0L, 1L));
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            c.clear();
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            c.retainAll(Arrays.asList(0L, 2L));
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
        Iterator<Long> iter = c.iterator();
        iter.next();
        try {
            iter.remove();
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException ex) {
        }
    }

    @Test
    public void testEmpty() {
        verify(new ConcatenatedLists(), (-1));
    }

    @Test
    public void testOneOne() {
        ConcatenatedLists<Long> c = new ConcatenatedLists();
        c.addSublist(Arrays.asList(0L));
        verify(c, 0);
    }

    @Test
    public void testOneMany() {
        ConcatenatedLists<Long> c = new ConcatenatedLists();
        c.addSublist(Arrays.asList(0L, 1L, 2L));
        verify(c, 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManyOne() {
        ConcatenatedLists<Long> c = new ConcatenatedLists();
        c.addSublist(Arrays.asList(0L));
        c.addAllSublists(Arrays.asList(Arrays.asList(1L), Arrays.asList(2L)));
        verify(c, 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManyMany() {
        ConcatenatedLists<Long> c = new ConcatenatedLists();
        c.addAllSublists(Arrays.asList(Arrays.asList(0L, 1L)));
        c.addSublist(Arrays.asList(2L, 3L, 4L));
        c.addAllSublists(Arrays.asList(Arrays.asList(5L), Arrays.asList(6L, 7L)));
        verify(c, 7);
    }
}

