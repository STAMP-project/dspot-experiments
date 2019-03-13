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
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.junit.Assert;
import org.junit.Test;


public class RightTupleIndexHashTableTest {
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Test
    public void testSingleEntry() throws Exception {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        final Cheese cheddar = new Cheese("cheddar", 10);
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle(0, cheddar);
        Assert.assertEquals(0, map.size());
        Assert.assertNull(map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(cheddarHandle1, null, true)));
        final Cheese stilton1 = new Cheese("stilton", 35);
        RightTuple stiltonRighTuple = new org.drools.core.reteoo.RightTupleImpl(new DefaultFactHandle(1, stilton1), null);
        map.add(stiltonRighTuple);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(1, tablePopulationSize(map));
        final Cheese stilton2 = new Cheese("stilton", 80);
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle(2, stilton2);
        final Tuple tuple = map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(stiltonHandle2, null, true));
        Assert.assertSame(stiltonRighTuple.getFactHandle(), tuple.getFactHandle());
        Assert.assertNull(tuple.getNext());
    }

    @Test
    public void testTwoDifferentEntries() throws Exception {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        Assert.assertEquals(0, map.size());
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle(1, stilton1);
        map.add(new org.drools.core.reteoo.RightTupleImpl(stiltonHandle1, null));
        final Cheese cheddar1 = new Cheese("cheddar", 35);
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle(2, cheddar1);
        map.add(new org.drools.core.reteoo.RightTupleImpl(cheddarHandle1, null));
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(2, tablePopulationSize(map));
        final Cheese stilton2 = new Cheese("stilton", 77);
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle(2, stilton2);
        Tuple tuple = map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(stiltonHandle2, null, true));
        Assert.assertSame(stiltonHandle1, tuple.getFactHandle());
        Assert.assertNull(tuple.getNext());
        final Cheese cheddar2 = new Cheese("cheddar", 5);
        final InternalFactHandle cheddarHandle2 = new DefaultFactHandle(2, cheddar2);
        tuple = map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(cheddarHandle2, null, true));
        Assert.assertSame(cheddarHandle1, tuple.getFactHandle());
        Assert.assertNull(tuple.getNext());
    }

    @Test
    public void testTwoEqualEntries() throws Exception {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        Assert.assertEquals(0, map.size());
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle(1, stilton1);
        map.add(new org.drools.core.reteoo.RightTupleImpl(stiltonHandle1, null));
        final Cheese cheddar1 = new Cheese("cheddar", 35);
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle(2, cheddar1);
        map.add(new org.drools.core.reteoo.RightTupleImpl(cheddarHandle1, null));
        final Cheese stilton2 = new Cheese("stilton", 81);
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle(3, stilton2);
        map.add(new org.drools.core.reteoo.RightTupleImpl(stiltonHandle2, null));
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(2, tablePopulationSize(map));
        // Check they are correctly chained to the same FieldIndexEntry
        final Cheese stilton3 = new Cheese("stilton", 89);
        final InternalFactHandle stiltonHandle3 = new DefaultFactHandle(4, stilton2);
        final Tuple tuple = map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(stiltonHandle3, null, true));
        Assert.assertSame(stiltonHandle1, tuple.getFactHandle());
        Assert.assertSame(stiltonHandle2, getFactHandle());
    }

    @Test
    public void testTwoDifferentEntriesSameHashCode() throws Exception {
        final InternalReadAccessor extractor = store.getReader(RightTupleIndexHashTableTest.TestClass.class, "object");
        final Pattern pattern = new Pattern(0, new ClassObjectType(RightTupleIndexHashTableTest.TestClass.class));
        final Declaration declaration = new Declaration("theObject", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        final RightTupleIndexHashTableTest.TestClass c1 = new RightTupleIndexHashTableTest.TestClass(0, new RightTupleIndexHashTableTest.TestClass(20, "stilton"));
        final InternalFactHandle ch1 = new DefaultFactHandle(1, c1);
        map.add(new org.drools.core.reteoo.RightTupleImpl(ch1, null));
        final RightTupleIndexHashTableTest.TestClass c2 = new RightTupleIndexHashTableTest.TestClass(0, new RightTupleIndexHashTableTest.TestClass(20, "cheddar"));
        final InternalFactHandle ch2 = new DefaultFactHandle(2, c2);
        map.add(new org.drools.core.reteoo.RightTupleImpl(ch2, null));
        // same hashcode, but different values, so it should result in  a size of 2
        Assert.assertEquals(2, map.size());
        // however both are in the same table bucket
        Assert.assertEquals(1, tablePopulationSize(map));
        // this table bucket will have two FieldIndexEntries, as they are actually two different values
        Entry[] entries = getEntries(map);
        Assert.assertEquals(1, entries.length);
        TupleList list = ((TupleList) (entries[0]));
        Assert.assertSame(ch2, getFactHandle());
        Assert.assertNull(list.getFirst().getNext());
        Assert.assertSame(ch1, getFactHandle());
        Assert.assertNull(list.getNext().getFirst().getNext());
        Assert.assertNull(list.getNext().getNext());
    }

    @Test
    public void testRemove() throws Exception {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        Assert.assertEquals(0, map.size());
        final Cheese stilton1 = new Cheese("stilton", 35);
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle(1, stilton1);
        RightTuple stiltonRightTuple1 = new org.drools.core.reteoo.RightTupleImpl(stiltonHandle1, null);
        map.add(stiltonRightTuple1);
        final Cheese cheddar1 = new Cheese("cheddar", 35);
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle(2, cheddar1);
        RightTuple cheddarRightTuple1 = new org.drools.core.reteoo.RightTupleImpl(cheddarHandle1, null);
        map.add(cheddarRightTuple1);
        final Cheese stilton2 = new Cheese("stilton", 81);
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle(3, stilton2);
        RightTuple stiltonRightTuple2 = new org.drools.core.reteoo.RightTupleImpl(stiltonHandle2, null);
        map.add(stiltonRightTuple2);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(2, tablePopulationSize(map));
        // cheddar is in its own bucket, which should be removed once empty. We cannot have
        // empty FieldIndexEntries in the Map, as they get their value  from the first FactEntry.
        map.remove(cheddarRightTuple1);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(1, tablePopulationSize(map));
        // We remove t he stiltonHandle2, but there is still  one more stilton, so size  should be the same
        map.remove(stiltonRightTuple2);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(1, tablePopulationSize(map));
        // No more stiltons, so the table should be empty
        map.remove(stiltonRightTuple1);
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, tablePopulationSize(map));
    }

    @Test
    public void testResize() throws Exception {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(16, 0.75F, new FieldIndex[]{ fieldIndex }, false);
        Assert.assertEquals(0, map.size());
        final Cheese stilton1 = new Cheese("stilton", 35);
        map.add(newRightTuple(1, stilton1));
        final Cheese stilton2 = new Cheese("stilton", 81);
        map.add(newRightTuple(2, stilton2));
        final Cheese cheddar1 = new Cheese("cheddar", 35);
        map.add(newRightTuple(3, cheddar1));
        final Cheese cheddar2 = new Cheese("cheddar", 38);
        map.add(newRightTuple(4, cheddar2));
        final Cheese brie = new Cheese("brie", 293);
        map.add(newRightTuple(5, brie));
        final Cheese mozerella = new Cheese("mozerella", 15);
        map.add(newRightTuple(6, mozerella));
        final Cheese dolcelatte = new Cheese("dolcelatte", 284);
        map.add(newRightTuple(7, dolcelatte));
        final Cheese camembert1 = new Cheese("camembert", 924);
        map.add(newRightTuple(8, camembert1));
        final Cheese camembert2 = new Cheese("camembert", 765);
        map.add(newRightTuple(9, camembert2));
        final Cheese redLeicestor = new Cheese("red leicestor", 23);
        map.add(newRightTuple(10, redLeicestor));
        final Cheese wensleydale = new Cheese("wensleydale", 20);
        map.add(newRightTuple(11, wensleydale));
        final Cheese edam = new Cheese("edam", 12);
        map.add(newRightTuple(12, edam));
        final Cheese goude1 = new Cheese("goude", 93);
        map.add(newRightTuple(13, goude1));
        final Cheese goude2 = new Cheese("goude", 88);
        map.add(newRightTuple(14, goude2));
        final Cheese gruyere = new Cheese("gruyere", 82);
        map.add(newRightTuple(15, gruyere));
        final Cheese emmental = new Cheese("emmental", 98);
        map.add(newRightTuple(16, emmental));
        // At this point we have 16 facts but only 12 different types of cheeses
        // so no table resize and thus its size is 16
        Assert.assertEquals(16, map.size());
        Entry[] table = map.getTable();
        Assert.assertEquals(16, table.length);
        final Cheese feta = new Cheese("feta", 48);
        map.add(newRightTuple(2, feta));
        // This adds our 13th type of cheese. The map is set with an initial capacity of 16 and
        // a threshold of 75%, that after 12 it should resize the map to 32.
        Assert.assertEquals(17, map.size());
        table = map.getTable();
        Assert.assertEquals(32, table.length);
        final Cheese haloumi = new Cheese("haloumi", 48);
        map.add(newRightTuple(2, haloumi));
        final Cheese chevre = new Cheese("chevre", 48);
        map.add(newRightTuple(2, chevre));
    }

    public static class TestClass {
        private int hashCode;

        private Object object;

        public TestClass() {
        }

        public TestClass(final int hashCode, final Object object) {
            this.hashCode = hashCode;
            this.object = object;
        }

        public Object getObject() {
            return this.object;
        }

        public void setObject(final Object object) {
            this.object = object;
        }

        public void setHashCode(final int hashCode) {
            this.hashCode = hashCode;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final RightTupleIndexHashTableTest.TestClass other = ((RightTupleIndexHashTableTest.TestClass) (obj));
            if ((this.object) == null) {
                if ((other.object) != null) {
                    return false;
                }
            } else
                if (!(this.object.equals(other.object))) {
                    return false;
                }

            return true;
        }
    }

    @Test
    public void testEmptyIterator() {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        final FieldIndex fieldIndex = new FieldIndex(extractor, declaration, MvelConstraint.INDEX_EVALUATOR);
        final TupleIndexHashTable map = new TupleIndexHashTable(new FieldIndex[]{ fieldIndex }, false);
        final Cheese stilton = new Cheese("stilton", 55);
        final InternalFactHandle stiltonHandle = new DefaultFactHandle(2, stilton);
        Assert.assertNull(map.getFirst(new org.drools.core.reteoo.LeftTupleImpl(stiltonHandle, null, true)));
    }
}

