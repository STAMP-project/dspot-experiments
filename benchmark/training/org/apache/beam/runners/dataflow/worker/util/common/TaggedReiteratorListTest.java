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
package org.apache.beam.runners.dataflow.worker.util.common;


import java.util.Iterator;
import org.apache.beam.sdk.util.common.Reiterator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TaggedReiteratorList}.
 */
@RunWith(JUnit4.class)
public class TaggedReiteratorListTest {
    @Test
    public void testSingleIterator() {
        TaggedReiteratorList iter = create(new String[]{ "a", "b", "c" });
        assertEquals(iter.get(0), "a", "b", "c");
        assertEquals(iter.get(0), "a", "b", "c");
        /* empty */
        assertEquals(iter.get(1));
        assertEquals(iter.get(0), "a", "b", "c");
    }

    @Test
    public void testSequentialAccess() {
        TaggedReiteratorList iter = create(3, new String[]{ "a", "b", "c" });
        for (int i = 0; i < 2; i++) {
            assertEquals(iter.get(0), "a0", "b0", "c0");
            assertEquals(iter.get(1), "a1", "b1", "c1");
            assertEquals(iter.get(2), "a2", "b2", "c2");
        }
        for (int i = 0; i < 2; i++) {
            assertEquals(iter.get(2), "a2", "b2", "c2");
            assertEquals(iter.get(1), "a1", "b1", "c1");
            assertEquals(iter.get(0), "a0", "b0", "c0");
        }
    }

    @Test
    public void testRandomAccess() {
        TaggedReiteratorList iter = create(6, new String[]{ "a", "b" });
        assertEquals(iter.get(3), "a3", "b3");
        assertEquals(iter.get(1), "a1", "b1");
        assertEquals(iter.get(5), "a5", "b5");
        assertEquals(iter.get(0), "a0", "b0");
        assertEquals(iter.get(4), "a4", "b4");
        assertEquals(iter.get(4), "a4", "b4");
        assertEquals(iter.get(1), "a1", "b1");
    }

    @Test
    public void testPartialIteration() {
        TaggedReiteratorList iter = create(6, new String[]{ "a", "b", "c" });
        Iterator<?> get0 = iter.get(0);
        Iterator<?> get1 = iter.get(1);
        Iterator<?> get3 = iter.get(3);
        assertEquals(asList(get0, 1), "a0");
        assertEquals(asList(get1, 2), "a1", "b1");
        assertEquals(asList(get3, 3), "a3", "b3", "c3");
        Iterator<?> get2 = iter.get(2);
        Iterator<?> get0Again = iter.get(0);
        assertEquals(asList(get0, 1), "b0");
        assertEquals(get2, "a2", "b2", "c2");
        assertEquals(get0Again, "a0", "b0", "c0");
        assertEquals(asList(get0), "c0");
        Iterator<?> get4 = iter.get(4);
        assertEquals(get4, "a4", "b4", "c4");
        /* empty */
        assertEquals(get4);
        assertEquals(iter.get(4), "a4", "b4", "c4");
    }

    @Test
    public void testNextIteration() {
        TaggedReiteratorList iter = create(2, new String[]{ "a", "b", "c" });
        Reiterator<?> get0 = iter.get(0);
        assertEquals(get0, "a0", "b0", "c0");
        Iterator<?> get1 = iter.get(1);
        Assert.assertEquals("a1", get1.next());
        /* empty */
        assertEquals(get0.copy());
        Assert.assertEquals("b1", get1.next());
        assertEquals(iter.get(1), "a1", "b1", "c1");
    }

    @Test
    public void testEmpties() {
        TaggedReiteratorList iter = create(new String[]{  }, new String[]{ "a", "b", "c" }, new String[]{  }, new String[]{  }, new String[]{ "d" });
        /* empty */
        assertEquals(iter.get(2));
        assertEquals(iter.get(1), "a", "b", "c");
        /* empty */
        assertEquals(iter.get(2));
        /* empty */
        assertEquals(iter.get(0));
        /* empty */
        assertEquals(iter.get(2));
        assertEquals(iter.get(4), "d");
        /* empty */
        assertEquals(iter.get(3));
    }

    private static class TestReiterator implements Reiterator<TaggedReiteratorListTest.TaggedValue> {
        private final TaggedReiteratorListTest.TaggedValue[] values;

        private int pos = 0;

        public TestReiterator(TaggedReiteratorListTest.TaggedValue... values) {
            this(values, 0);
        }

        private TestReiterator(TaggedReiteratorListTest.TaggedValue[] values, int pos) {
            this.values = values;
            this.pos = pos;
        }

        @Override
        public boolean hasNext() {
            return (pos) < (values.length);
        }

        @Override
        public TaggedReiteratorListTest.TaggedValue next() {
            return values[((pos)++)];
        }

        @Override
        public void remove() {
            throw new IllegalArgumentException();
        }

        @Override
        public TaggedReiteratorListTest.TestReiterator copy() {
            return new TaggedReiteratorListTest.TestReiterator(values, pos);
        }
    }

    private static class TaggedValueExtractor implements TaggedReiteratorList.TagExtractor<TaggedReiteratorListTest.TaggedValue> {
        @Override
        public int getTag(TaggedReiteratorListTest.TaggedValue elem) {
            return elem.tag;
        }

        @Override
        public String getValue(TaggedReiteratorListTest.TaggedValue elem) {
            return elem.value;
        }
    }

    private static class TaggedValue {
        public final int tag;

        public final String value;

        public TaggedValue(int tag, String value) {
            this.tag = tag;
            this.value = value;
        }
    }
}

