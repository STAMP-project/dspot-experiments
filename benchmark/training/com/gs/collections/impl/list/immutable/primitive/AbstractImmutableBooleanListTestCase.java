/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.list.immutable.primitive;


import com.gs.collections.api.collection.primitive.ImmutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.impl.collection.immutable.primitive.AbstractImmutableBooleanCollectionTestCase;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract JUnit test for {@link ImmutableBooleanList}.
 */
public abstract class AbstractImmutableBooleanListTestCase extends AbstractImmutableBooleanCollectionTestCase {
    @Test
    public void newWithOn64ElementCollection() {
        BooleanArrayList sixtyFourElementCollection = new BooleanArrayList();
        for (int i = 0; i < 64; i++) {
            sixtyFourElementCollection.add(true);
        }
        ImmutableBooleanList immutableBooleanList = sixtyFourElementCollection.toImmutable();
        Assert.assertEquals(sixtyFourElementCollection, immutableBooleanList);
        ImmutableBooleanList newImmutableBooleanList = immutableBooleanList.newWith(false);
        Assert.assertFalse(newImmutableBooleanList.get(64));
        ImmutableBooleanList newImmutableBooleanList1 = immutableBooleanList.newWith(true);
        Assert.assertTrue(newImmutableBooleanList1.get(64));
    }

    @Test
    public void get() {
        ImmutableBooleanList list = this.classUnderTest();
        for (int i = 0; i < (list.size()); i++) {
            Assert.assertEquals(((i & 1) == 0), list.get(i));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_throws_index_greater_than_size() {
        ImmutableBooleanList list = this.classUnderTest();
        list.get(list.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_throws_index_negative() {
        this.classUnderTest().get((-1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void subList() {
        this.classUnderTest().subList(0, 1);
    }

    @Test
    public void getFirst() {
        Assert.assertTrue(this.classUnderTest().getFirst());
    }

    @Test
    public void getLast() {
        Assert.assertEquals((((this.classUnderTest().size()) & 1) != 0), this.classUnderTest().getLast());
    }

    @Test
    public void indexOf() {
        ImmutableBooleanList list = this.classUnderTest();
        Assert.assertEquals(0L, list.indexOf(true));
        Assert.assertEquals(((list.size()) > 2 ? 1L : -1L), list.indexOf(false));
        MutableBooleanList mutableList = this.newMutableCollectionWith();
        for (int i = 0; i < (list.size()); i++) {
            mutableList.add(false);
        }
        Assert.assertEquals((-1L), mutableList.toImmutable().indexOf(true));
    }

    @Test
    public void lastIndexOf() {
        ImmutableBooleanList list = this.classUnderTest();
        int size = list.size();
        Assert.assertEquals(((size & 1) == 0 ? size - 2 : size - 1), list.lastIndexOf(true));
        Assert.assertEquals(((size & 1) == 0 ? size - 1 : size - 2), list.lastIndexOf(false));
        MutableBooleanList mutableList = this.newMutableCollectionWith();
        for (int i = 0; i < (list.size()); i++) {
            mutableList.add(false);
        }
        Assert.assertEquals((-1L), mutableList.toImmutable().lastIndexOf(true));
    }

    @Override
    @Test
    public void booleanIterator() {
        BooleanIterator iterator = this.classUnderTest().booleanIterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Assert.assertEquals(((i % 2) == 0), iterator.next());
        }
        Assert.assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    public void forEach() {
        super.forEach();
        String[] sum = new String[1];
        sum[0] = "";
        this.classUnderTest().forEach(( each) -> sum[0] += each);
        StringBuilder expectedString = new StringBuilder();
        for (int i = 0; i < (this.classUnderTest().size()); i++) {
            expectedString.append(((i & 1) == 0));
        }
        Assert.assertEquals(expectedString.toString(), sum[0]);
    }

    @Test
    public void forEachWithIndex() {
        String[] sum = new String[2];
        sum[0] = "";
        sum[1] = "";
        this.classUnderTest().forEachWithIndex(( each, index) -> sum[0] += (index + ":") + each);
        this.newWith().forEachWithIndex(( each, index) -> sum[1] += (index + ":") + each);
        Assert.assertEquals("0:true1:false2:true", sum[0]);
        Assert.assertEquals("", sum[1]);
    }

    @Test
    public void toReversed() {
        Assert.assertEquals(BooleanArrayList.newListWith(true, true, false, false), this.newWith(false, false, true, true).toReversed());
        ImmutableBooleanList originalList = this.newWith(true, true, false, false);
        Assert.assertNotSame(originalList, originalList.toReversed());
    }

    @Override
    @Test
    public void toArray() {
        super.toArray();
        ImmutableBooleanList list = this.classUnderTest();
        Assert.assertEquals(this.classUnderTest().size(), list.toArray().length);
        for (int i = 0; i < (this.classUnderTest().size()); i++) {
            Assert.assertEquals(((i & 1) == 0), list.toArray()[i]);
        }
    }

    @Test
    public void injectIntoWithIndex() {
        ImmutableBooleanList list = this.newWith(true, false, true);
        MutableInteger result = list.injectIntoWithIndex(new MutableInteger(0), ( object, value, index) -> object.add(((value ? 1 : 0) + index)));
        Assert.assertEquals(new MutableInteger(5), result);
    }

    @Override
    @Test
    public void testEquals() {
        super.testEquals();
        ImmutableBooleanList list1 = this.newWith(true, false, true, true);
        ImmutableBooleanList list2 = this.newWith(true, true, false, true);
        Assert.assertNotEquals(list1, list2);
    }

    @Override
    @Test
    public void testToString() {
        super.testToString();
        StringBuilder expectedString = new StringBuilder("[");
        int size = this.classUnderTest().size();
        for (int i = 0; i < size; i++) {
            expectedString.append(((i & 1) == 0));
            expectedString.append((i == (size - 1) ? "" : ", "));
        }
        expectedString.append(']');
        Assert.assertEquals(expectedString.toString(), this.classUnderTest().toString());
    }

    @Override
    @Test
    public void makeString() {
        super.makeString();
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = this.classUnderTest().size();
        for (int i = 0; i < size; i++) {
            boolean isEven = (i & 1) == 0;
            expectedString.append(isEven);
            expectedString1.append(isEven);
            expectedString.append((i == (size - 1) ? "" : ", "));
            expectedString1.append((i == (size - 1) ? "" : "/"));
        }
        Assert.assertEquals(expectedString.toString(), this.classUnderTest().makeString());
        Assert.assertEquals(expectedString1.toString(), this.classUnderTest().makeString("/"));
        Assert.assertEquals(this.classUnderTest().toString(), this.classUnderTest().makeString("[", ", ", "]"));
    }

    @Override
    @Test
    public void appendString() {
        super.appendString();
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = this.classUnderTest().size();
        for (int i = 0; i < size; i++) {
            boolean isEven = (i & 1) == 0;
            expectedString.append(isEven);
            expectedString1.append(isEven);
            expectedString.append((i == (size - 1) ? "" : ", "));
            expectedString1.append((i == (size - 1) ? "" : "/"));
        }
        StringBuilder appendable2 = new StringBuilder();
        this.classUnderTest().appendString(appendable2);
        Assert.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.classUnderTest().appendString(appendable3, "/");
        Assert.assertEquals(expectedString1.toString(), appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.classUnderTest().appendString(appendable4, "[", ", ", "]");
        Assert.assertEquals(this.classUnderTest().toString(), appendable4.toString());
    }

    @Override
    @Test
    public void toList() {
        super.toList();
        MutableBooleanList list = this.classUnderTest().toList();
        Verify.assertEqualsAndHashCode(this.classUnderTest(), list);
        Assert.assertNotSame(this.classUnderTest(), list);
    }

    @Override
    @Test
    public void testNewWith() {
        ImmutableBooleanCollection booleanCollection = this.classUnderTest();
        MutableBooleanList list = booleanCollection.toList();
        ImmutableBooleanCollection collection = booleanCollection.newWith(true);
        ImmutableBooleanCollection collection0 = booleanCollection.newWith(true).newWith(false);
        ImmutableBooleanCollection collection1 = booleanCollection.newWith(true).newWith(false).newWith(true);
        ImmutableBooleanCollection collection2 = booleanCollection.newWith(true).newWith(false).newWith(true).newWith(false);
        ImmutableBooleanCollection collection3 = booleanCollection.newWith(true).newWith(false).newWith(true).newWith(false).newWith(true);
        ImmutableBooleanCollection collection4 = collection3.newWith(true).newWith(false).newWith(true).newWith(false).newWith(true);
        Assert.assertEquals(list, booleanCollection);
        Assert.assertEquals(list.with(true), collection);
        Assert.assertEquals(list.with(false), collection0);
        Assert.assertEquals(list.with(true), collection1);
        Assert.assertEquals(list.with(false), collection2);
        Assert.assertEquals(list.with(true), collection3);
        list.addAll(true, false, true, false, true);
        Assert.assertEquals(list, collection4);
    }

    @Override
    @Test
    public void newWithAll() {
        ImmutableBooleanCollection booleanCollection = this.classUnderTest();
        MutableBooleanList list = booleanCollection.toList();
        ImmutableBooleanCollection collection = booleanCollection.newWithAll(this.newMutableCollectionWith(true));
        ImmutableBooleanCollection collection0 = booleanCollection.newWithAll(this.newMutableCollectionWith(true, false));
        ImmutableBooleanCollection collection1 = booleanCollection.newWithAll(this.newMutableCollectionWith(true, false, true));
        ImmutableBooleanCollection collection2 = booleanCollection.newWithAll(this.newMutableCollectionWith(true, false, true, false));
        ImmutableBooleanCollection collection3 = booleanCollection.newWithAll(this.newMutableCollectionWith(true, false, true, false, true));
        ImmutableBooleanCollection collection4 = collection3.newWithAll(this.newMutableCollectionWith(true, false, true, false, true));
        Assert.assertEquals(list, booleanCollection);
        Assert.assertEquals(list.with(true), collection);
        Assert.assertEquals(list.with(false), collection0);
        Assert.assertEquals(list.with(true), collection1);
        Assert.assertEquals(list.with(false), collection2);
        Assert.assertEquals(list.with(true), collection3);
        list.addAll(true, false, true, false, true);
        Assert.assertEquals(list, collection4);
    }

    @Override
    @Test
    public void newWithout() {
        ImmutableBooleanCollection trueCollection = this.getTrueCollection(this.classUnderTest()).toImmutable();
        Assert.assertSame(trueCollection, trueCollection.newWithout(false));
        Assert.assertNotSame(trueCollection, trueCollection.newWithout(true));
        ImmutableBooleanCollection collection = this.classUnderTest();
        MutableBooleanList list = collection.toList();
        Assert.assertEquals(list.without(true), collection.newWithout(true));
        MutableBooleanList list1 = collection.toList();
        Assert.assertEquals(list1.without(false), collection.newWithout(false));
        Assert.assertEquals(this.classUnderTest(), collection);
    }

    @Override
    @Test
    public void newWithoutAll() {
        ImmutableBooleanCollection immutableBooleanCollection = this.classUnderTest();
        MutableBooleanCollection mutableTrueCollection = this.getTrueCollection(immutableBooleanCollection);
        ImmutableBooleanCollection trueCollection = mutableTrueCollection.toImmutable();
        Assert.assertEquals(this.newMutableCollectionWith(), trueCollection.newWithoutAll(this.newMutableCollectionWith(true, false)));
        Assert.assertEquals(mutableTrueCollection, trueCollection);
        MutableBooleanList list = immutableBooleanCollection.toList();
        list.removeAll(true);
        Assert.assertEquals(list, immutableBooleanCollection.newWithoutAll(this.newMutableCollectionWith(true)));
        Assert.assertEquals(this.newMutableCollectionWith(), immutableBooleanCollection.newWithoutAll(this.newMutableCollectionWith(true, false)));
        ImmutableBooleanCollection collection = this.newWith(true, false, true, false, true);
        Assert.assertEquals(this.newMutableCollectionWith(false, false), collection.newWithoutAll(this.newMutableCollectionWith(true, true)));
        Assert.assertEquals(this.newMutableCollectionWith(), collection.newWithoutAll(this.newMutableCollectionWith(true, false)));
    }
}

