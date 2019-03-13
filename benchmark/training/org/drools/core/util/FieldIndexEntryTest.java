/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util;


import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.index.TupleList;
import org.junit.Assert;
import org.junit.Test;


public class FieldIndexEntryTest {
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Test
    public void testSingleEntry() {
        final ClassFieldReader extractor = store.getReader(Cheese.class, "type");
        final FieldIndex fieldIndex = new FieldIndex(extractor, new org.drools.core.rule.Declaration("id", extractor, null), MvelConstraint.INDEX_EVALUATOR);
        final SingleIndex singleIndex = new SingleIndex(new FieldIndex[]{ fieldIndex }, 1);
        Tuple tuple = new org.drools.core.reteoo.RightTupleImpl(new DefaultFactHandle(1, new Cheese("stilton", 10)));
        final TupleList index = new AbstractHashTable.SingleIndexTupleList(singleIndex, tuple, "stilton".hashCode(), false);
        // Test initial construction
        Assert.assertNull(index.getFirst());
        Assert.assertEquals("stilton".hashCode(), index.hashCode());
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle h1 = new DefaultFactHandle(1, stilton1);
        // test add
        RightTuple h1RightTuple = new org.drools.core.reteoo.RightTupleImpl(h1, null);
        index.add(h1RightTuple);
        final Tuple entry1 = index.getFirst();
        Assert.assertSame(h1, entry1.getFactHandle());
        Assert.assertNull(entry1.getNext());
        Assert.assertSame(entry1, index.get(h1));
        // test get
        final Tuple entry2 = index.get(new org.drools.core.reteoo.RightTupleImpl(h1, null));
        Assert.assertSame(entry1, entry2);
        // test remove
        index.remove(h1RightTuple);
        Assert.assertNull(index.getFirst());
    }

    @Test
    public void testTwoEntries() {
        final ClassFieldReader extractor = store.getReader(Cheese.class, "type");
        final FieldIndex fieldIndex = new FieldIndex(extractor, new org.drools.core.rule.Declaration("id", extractor, null), MvelConstraint.INDEX_EVALUATOR);
        final SingleIndex singleIndex = new SingleIndex(new FieldIndex[]{ fieldIndex }, 1);
        Tuple tuple = new org.drools.core.reteoo.RightTupleImpl(new DefaultFactHandle(1, new Cheese("stilton", 10)));
        final TupleList index = new AbstractHashTable.SingleIndexTupleList(singleIndex, tuple, "stilton".hashCode(), false);
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle h1 = new DefaultFactHandle(1, stilton1);
        final Cheese stilton2 = new Cheese("stilton", 59);
        final InternalFactHandle h2 = new DefaultFactHandle(2, stilton2);
        RightTuple h1RightTuple = new org.drools.core.reteoo.RightTupleImpl(h1, null);
        RightTuple h2RightTuple = new org.drools.core.reteoo.RightTupleImpl(h2, null);
        // test add
        index.add(h1RightTuple);
        index.add(h2RightTuple);
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        // test get
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        // test removal for combinations
        // remove first
        index.remove(h2RightTuple);
        Assert.assertEquals(h1RightTuple.getFactHandle(), getFactHandle());
        // remove second
        index.add(h2RightTuple);
        index.remove(h1RightTuple);
        Assert.assertEquals(h2RightTuple.getFactHandle(), getFactHandle());
        // check index type does not change, as this fact is removed
        stilton1.setType("cheddar");
    }

    @Test
    public void testThreeEntries() {
        final ClassFieldReader extractor = store.getReader(Cheese.class, "type");
        final FieldIndex fieldIndex = new FieldIndex(extractor, new org.drools.core.rule.Declaration("id", extractor, null), MvelConstraint.INDEX_EVALUATOR);
        final SingleIndex singleIndex = new SingleIndex(new FieldIndex[]{ fieldIndex }, 1);
        Tuple tuple = new org.drools.core.reteoo.RightTupleImpl(new DefaultFactHandle(1, new Cheese("stilton", 10)));
        final TupleList index = new AbstractHashTable.SingleIndexTupleList(singleIndex, tuple, "stilton".hashCode(), false);
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle h1 = new DefaultFactHandle(1, stilton1);
        final Cheese stilton2 = new Cheese("stilton", 59);
        final InternalFactHandle h2 = new DefaultFactHandle(2, stilton2);
        final Cheese stilton3 = new Cheese("stilton", 59);
        final InternalFactHandle h3 = new DefaultFactHandle(3, stilton3);
        RightTuple h1RightTuple = new org.drools.core.reteoo.RightTupleImpl(h1, null);
        RightTuple h2RightTuple = new org.drools.core.reteoo.RightTupleImpl(h2, null);
        RightTuple h3RightTuple = new org.drools.core.reteoo.RightTupleImpl(h3, null);
        // test add
        index.add(h1RightTuple);
        index.add(h2RightTuple);
        index.add(h3RightTuple);
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        Assert.assertEquals(h3, getFactHandle());
        // test get
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        Assert.assertEquals(h3, getFactHandle());
        // test removal for combinations
        // remove first
        index.remove(h3RightTuple);
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        index.add(h3RightTuple);
        index.remove(h2RightTuple);
        Assert.assertEquals(h1, getFactHandle());
        Assert.assertEquals(h3, getFactHandle());
        index.add(h2RightTuple);
        index.remove(h1RightTuple);
        Assert.assertEquals(h3, getFactHandle());
        Assert.assertEquals(h2, getFactHandle());
        index.remove(index.getFirst());
        // check index type does not change, as this fact is removed
        stilton2.setType("cheddar");
    }
}

