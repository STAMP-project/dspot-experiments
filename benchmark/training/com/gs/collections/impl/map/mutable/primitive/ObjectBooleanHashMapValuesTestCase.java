/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.map.mutable.primitive;


import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import com.gs.collections.impl.collection.mutable.primitive.SynchronizedBooleanCollection;
import com.gs.collections.impl.collection.mutable.primitive.UnmodifiableBooleanCollection;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public abstract class ObjectBooleanHashMapValuesTestCase extends AbstractMutableBooleanCollectionTestCase {
    @Override
    @Test
    public void booleanIterator() {
        MutableBooleanCollection bag = this.newWith(true, false, true, true);
        BooleanArrayList list = BooleanArrayList.newListWith(true, false, true, true);
        BooleanIterator iterator1 = bag.booleanIterator();
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(iterator1.hasNext());
            Assert.assertTrue(list.remove(iterator1.next()));
        }
        Verify.assertEmpty(list);
        Assert.assertFalse(iterator1.hasNext());
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (iterator1::next)));
        ObjectBooleanHashMap<String> map2 = new ObjectBooleanHashMap();
        for (int each = 2; each < 100; each++) {
            map2.put(String.valueOf(each), ((each % 2) == 0));
        }
        MutableBooleanIterator iterator2 = map2.booleanIterator();
        while (iterator2.hasNext()) {
            iterator2.next();
            iterator2.remove();
        } 
        Assert.assertTrue(map2.isEmpty());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAllIterable() {
        this.classUnderTest().addAll(new BooleanArrayList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void add() {
        this.classUnderTest().add(true);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAllArray() {
        this.classUnderTest().addAll(true, false);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void with() {
        this.classUnderTest().with(false);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void without() {
        this.classUnderTest().without(true);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withAll() {
        this.classUnderTest().withAll(new BooleanArrayList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withoutAll() {
        this.classUnderTest().withoutAll(new BooleanArrayList());
    }

    @Override
    @Test
    public void remove() {
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        Assert.assertTrue(collection.remove(false));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.contains(true));
    }

    @Override
    @Test
    public void removeAll() {
        Assert.assertFalse(this.newWith().removeAll());
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assert.assertFalse(collection.removeAll());
        Assert.assertTrue(collection.removeAll(false));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.contains(true));
        Assert.assertTrue(collection.removeAll(true));
        Assert.assertTrue(collection.isEmpty());
        Assert.assertFalse(collection.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.isEmpty());
    }

    @Override
    @Test
    public void removeAll_iterable() {
        Assert.assertFalse(this.newWith().removeAll(new BooleanArrayList()));
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assert.assertFalse(collection.removeAll());
        Assert.assertTrue(collection.removeAll(BooleanArrayList.newListWith(false)));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.contains(true));
        Assert.assertTrue(collection.removeAll(BooleanArrayList.newListWith(true)));
        Assert.assertTrue(collection.isEmpty());
        Assert.assertFalse(collection.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.isEmpty());
        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assert.assertTrue(collection1.removeAll(BooleanArrayList.newListWith(true, false)));
        Assert.assertTrue(collection1.isEmpty());
        Assert.assertFalse(collection1.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map1.contains(true));
        Assert.assertFalse(map1.contains(false));
        Assert.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll() {
        Assert.assertFalse(this.newWith().retainAll());
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assert.assertFalse(collection.retainAll(false, true));
        Assert.assertTrue(collection.retainAll(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.contains(true));
        Assert.assertTrue(collection.retainAll(false));
        Assert.assertTrue(collection.isEmpty());
        Assert.assertFalse(collection.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.isEmpty());
        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assert.assertTrue(collection1.retainAll());
        Assert.assertTrue(collection1.isEmpty());
        Assert.assertFalse(collection1.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map1.contains(true));
        Assert.assertFalse(map1.contains(false));
        Assert.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll_iterable() {
        Assert.assertFalse(this.newWith().retainAll(new BooleanArrayList()));
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assert.assertFalse(collection.retainAll(BooleanArrayList.newListWith(false, true)));
        Assert.assertTrue(collection.retainAll(BooleanArrayList.newListWith(true)));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.contains(true));
        Assert.assertTrue(collection.retainAll(BooleanArrayList.newListWith(false)));
        Assert.assertTrue(collection.isEmpty());
        Assert.assertFalse(collection.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map.contains(true));
        Assert.assertFalse(map.contains(false));
        Assert.assertTrue(map.isEmpty());
        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assert.assertTrue(collection1.retainAll(new BooleanArrayList()));
        Assert.assertTrue(collection1.isEmpty());
        Assert.assertFalse(collection1.contains(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(map1.contains(true));
        Assert.assertFalse(map1.contains(false));
        Assert.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void clear() {
        MutableBooleanCollection emptyCollection = this.newWith();
        emptyCollection.clear();
        Verify.assertSize(0, emptyCollection);
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        collection.clear();
        Verify.assertEmpty(collection);
        Verify.assertEmpty(map);
        Verify.assertSize(0, collection);
        Assert.assertFalse(collection.contains(true));
        Assert.assertFalse(collection.contains(false));
    }

    @Override
    @Test
    public void contains() {
        MutableBooleanCollection collection = this.classUnderTest();
        Assert.assertTrue(collection.contains(true));
        Assert.assertTrue(collection.contains(false));
        Assert.assertTrue(collection.remove(false));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.remove(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertTrue(collection.contains(true));
        Assert.assertTrue(collection.remove(true));
        Assert.assertFalse(collection.contains(false));
        Assert.assertFalse(collection.contains(true));
    }

    @Override
    @Test
    public void reject() {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(2, iterable.reject(BooleanPredicates.isFalse()));
    }

    @Override
    @Test
    public void select() {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.select(BooleanPredicates.isFalse()));
        Verify.assertSize(2, iterable.select(BooleanPredicates.isTrue()));
    }

    @Override
    @Test
    public void collect() {
        BooleanToObjectFunction<Integer> function = ( parameter) -> parameter ? 1 : 0;
        Assert.assertEquals(this.newObjectCollectionWith(1, 0, 1).toBag(), this.newWith(true, false, true).collect(function).toBag());
        Assert.assertEquals(this.newObjectCollectionWith(), this.newWith().collect(function));
    }

    @Override
    @Test
    public void appendString() {
        StringBuilder appendable = new StringBuilder();
        this.newWith().appendString(appendable);
        Assert.assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "/");
        Assert.assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "[", "/", "]");
        Assert.assertEquals("[]", appendable.toString());
        StringBuilder appendable1 = new StringBuilder();
        this.newWith(true).appendString(appendable1);
        Assert.assertEquals("true", appendable1.toString());
        StringBuilder appendable2 = new StringBuilder();
        BooleanIterable iterable = this.newWith(true, false);
        iterable.appendString(appendable2);
        Assert.assertTrue((("true, false".equals(appendable2.toString())) || ("false, true".equals(appendable2.toString()))));
        StringBuilder appendable3 = new StringBuilder();
        iterable.appendString(appendable3, "/");
        Assert.assertTrue((("true/false".equals(appendable3.toString())) || ("false/true".equals(appendable3.toString()))));
    }

    @Override
    @Test
    public void asUnmodifiable() {
        MutableBooleanCollection unmodifiable = this.classUnderTest().asUnmodifiable();
        Verify.assertInstanceOf(UnmodifiableBooleanCollection.class, unmodifiable);
        Assert.assertTrue(unmodifiable.containsAll(this.classUnderTest()));
    }

    @Override
    @Test
    public void asSynchronized() {
        MutableBooleanCollection synch = this.classUnderTest().asSynchronized();
        Verify.assertInstanceOf(SynchronizedBooleanCollection.class, synch);
        Assert.assertTrue(synch.containsAll(this.classUnderTest()));
    }
}

