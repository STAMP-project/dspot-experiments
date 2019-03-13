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
package com.gs.collections.impl.list.fixed;


import AddFunction.INTEGER;
import Lists.mutable;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.AbstractListTestCase;
import com.gs.collections.impl.list.mutable.ArrayListAdapter;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.UnmodifiableMutableList;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JUnit test for {@link ArrayAdapter}.
 */
public class ArrayAdapterTest extends AbstractListTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayAdapterTest.class);

    @Test
    public void testNewList() {
        MutableList<Integer> collection = this.newArray();
        Verify.assertEmpty(collection);
        Verify.assertSize(0, collection);
        MutableList<Integer> collection1 = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5, 6);
        Verify.assertSize(6, collection1);
        Verify.assertInstanceOf(ArrayAdapter.class, collection1);
    }

    @Test
    public void adapt() {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith();
        Verify.assertEmpty(collection);
    }

    @Test
    public void newListWith() {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(1, collection);
        Verify.assertContains(1, collection);
    }

    @Test
    public void newListWithWith() {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(2, collection);
        Verify.assertContainsAll(collection, 1, 2);
    }

    @Test
    public void newListWithWithWith() {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(3, collection);
        Verify.assertContainsAll(collection, 1, 2, 3);
    }

    @Test
    public void newListWithVarArgs() {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(4, collection);
        Verify.assertContainsAll(collection, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void forEach() {
        super.forEach();
        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void forEachFromTo() {
        super.forEachFromTo();
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(2, 3, result::add);
        Verify.assertSize(2, result);
        Verify.assertContainsAll(result, 3, 4);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        super.forEachWithIndex();
        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex(( object, index) -> result.add((object + index)));
        Verify.assertContainsAll(result, 1, 3, 5, 7);
    }

    @Test
    public void add() {
        MutableList<String> collection = ArrayAdapter.newArray();
        Verify.assertThrows(UnsupportedOperationException.class, () -> collection.add(null));
    }

    @Override
    @Test
    public void allSatisfy() {
        super.allSatisfy();
        Assert.assertTrue(this.newWith(1, 2, 3).allSatisfy(Integer.class::isInstance));
        Assert.assertFalse(this.newWith(1, 2, 3).allSatisfy(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void anySatisfy() {
        super.anySatisfy();
        Assert.assertFalse(this.newWith(1, 2, 3).anySatisfy(String.class::isInstance));
        Assert.assertTrue(this.newWith(1, 2, 3).anySatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void noneSatisfy() {
        super.noneSatisfy();
        Assert.assertTrue(this.newWith(1, 2, 3).noneSatisfy(String.class::isInstance));
        Assert.assertFalse(this.newWith(1, 2, 3).noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void count() {
        super.count();
        Assert.assertEquals(3, this.newWith(1, 2, 3).count(Integer.class::isInstance));
    }

    @Override
    @Test
    public void collectIf() {
        super.collectIf();
        Verify.assertContainsAll(this.newWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(this.newWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf, new ArrayList()), "1", "2", "3");
    }

    @Override
    @Test
    public void getFirst() {
        super.getFirst();
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assert.assertNotEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getFirst());
    }

    @Override
    @Test
    public void getLast() {
        super.getLast();
        Assert.assertNotEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
    }

    @Override
    @Test
    public void isEmpty() {
        super.isEmpty();
        Verify.assertEmpty(this.newArray());
        Verify.assertNotEmpty(this.newWith(1, 2));
        Assert.assertTrue(this.newWith(1, 2).notEmpty());
    }

    @Override
    @Test
    public void iterator() {
        super.iterator();
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); (i--) > 0;) {
            Integer integer = iterator.next();
            Assert.assertEquals(3, ((integer.intValue()) + i));
        }
    }

    @Override
    @Test
    public void injectInto() {
        super.injectInto();
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Integer result = objects.injectInto(1, INTEGER);
        Assert.assertEquals(Integer.valueOf(7), result);
    }

    @Override
    @Test
    public void toArray() {
        super.toArray();
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        Integer[] array2 = objects.toArray(new Integer[3]);
        Verify.assertSize(3, array2);
        Integer[] array3 = objects.toArray(new Integer[1]);
        Verify.assertSize(3, array3);
        Integer[] expected = new Integer[]{ 1, 2, 3 };
        Assert.assertArrayEquals(expected, array);
        Assert.assertArrayEquals(expected, array2);
        Assert.assertArrayEquals(expected, array3);
    }

    @Override
    @Test
    public void selectAndRejectWith() {
        super.selectAndRejectWith();
        MutableList<Integer> objects = this.newWith(1, 2);
        Twin<MutableList<Integer>> result = objects.selectAndRejectWith(Object::equals, 1);
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(1, result.getTwo());
    }

    @Override
    @Test
    public void removeIf() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).removeIf(com.gs.collections.impl.block.factory.Predicates.isNull()));
    }

    @Override
    @Test
    public void removeIndex() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).remove(0));
    }

    @Override
    @Test
    public void removeIfWith() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).removeIfWith(( each, ignored) -> each == null, null));
    }

    @Override
    @Test
    public void indexOf() {
        super.indexOf();
        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assert.assertEquals(1, objects.indexOf(2));
    }

    @Override
    @Test
    public void lastIndexOf() {
        super.lastIndexOf();
        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assert.assertEquals(1, objects.lastIndexOf(2));
    }

    @Override
    @Test
    public void set() {
        super.set();
        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assert.assertEquals(Integer.valueOf(2), objects.set(1, 4));
        Assert.assertEquals(FastList.newListWith(1, 4, 3), objects);
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        ArrayAdapter<Integer> array1 = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        ArrayAdapter<Integer> array2 = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        ArrayAdapter<Integer> array3 = ArrayAdapter.newArrayWith(2, 3, 4);
        ArrayAdapter<Integer> array4 = ArrayAdapter.newArrayWith(1, 2, 3, 5);
        Assert.assertNotEquals(array1, null);
        Verify.assertEqualsAndHashCode(array1, array1);
        Verify.assertEqualsAndHashCode(array1, array2);
        Assert.assertNotEquals(array2, array3);
        Verify.assertEqualsAndHashCode(array1, new ArrayList(array1));
        Verify.assertEqualsAndHashCode(array1, new java.util.LinkedList(array1));
        Verify.assertEqualsAndHashCode(array1, ArrayListAdapter.<Integer>newList().with(1, 2, 3, 4));
        Verify.assertEqualsAndHashCode(array1, FastList.<Integer>newList().with(1, 2, 3, 4));
        Assert.assertNotEquals(array1, new java.util.LinkedList(array4));
    }

    @Override
    @Test
    public void forEachWith() {
        super.forEachWith();
        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        collection.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void selectWith() {
        super.selectWith();
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).selectWith(com.gs.collections.impl.block.factory.Predicates2.<Integer>lessThan(), 3), 1, 2);
        Verify.denyContainsAny(ArrayAdapter.newArrayWith((-1), 2, 3, 4, 5).selectWith(com.gs.collections.impl.block.factory.Predicates2.<Integer>lessThan(), 3), 3, 4, 5);
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).selectWith(com.gs.collections.impl.block.factory.Predicates2.<Integer>lessThan(), 3, com.gs.collections.impl.set.mutable.UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Override
    @Test
    public void rejectWith() {
        super.rejectWith();
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4).rejectWith(com.gs.collections.impl.block.factory.Predicates2.<Integer>lessThan(), 3), 3, 4);
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4).rejectWith(com.gs.collections.impl.block.factory.Predicates2.<Integer>lessThan(), 3, com.gs.collections.impl.set.mutable.UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Override
    @Test
    public void detectWith() {
        Assert.assertEquals(Integer.valueOf(3), ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).detectWith(Object::equals, 3));
        Assert.assertNull(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).detectWith(Object::equals, 6));
    }

    @Test
    public void detectWithIfNone() {
        MutableList<Integer> list = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5);
        Assert.assertNull(list.detectWithIfNone(Object::equals, 6, new com.gs.collections.impl.block.function.PassThruFunction0(null)));
        Assert.assertEquals(Integer.valueOf(10000), list.detectWithIfNone(Object::equals, 6, new com.gs.collections.impl.block.function.PassThruFunction0(Integer.valueOf(10000))));
    }

    @Override
    @Test
    public void allSatisfyWith() {
        super.allSatisfyWith();
        Assert.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).allSatisfyWith(com.gs.collections.impl.block.factory.Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).allSatisfyWith(Object::equals, 1));
    }

    @Override
    @Test
    public void anySatisfyWith() {
        super.anySatisfyWith();
        Assert.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).anySatisfyWith(com.gs.collections.impl.block.factory.Predicates2.instanceOf(), String.class));
        Assert.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).anySatisfyWith(com.gs.collections.impl.block.factory.Predicates2.instanceOf(), Integer.class));
    }

    @Override
    @Test
    public void noneSatisfyWith() {
        super.noneSatisfyWith();
        Assert.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).noneSatisfyWith(com.gs.collections.impl.block.factory.Predicates2.instanceOf(), String.class));
        Assert.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).noneSatisfyWith(Object::equals, 1));
    }

    @Override
    @Test
    public void countWith() {
        super.countWith();
        Assert.assertEquals(3, ArrayAdapter.newArrayWith(1, 2, 3).countWith(com.gs.collections.impl.block.factory.Predicates2.instanceOf(), Integer.class));
    }

    @Override
    @Test
    public void collectWith() {
        super.collectWith();
        Function2<Integer, Integer, Integer> addBlock = ( each, parameter) -> each + parameter;
        Assert.assertEquals(FastList.newListWith(2, 3, 4), ArrayAdapter.newArrayWith(1, 2, 3).collectWith(addBlock, 1));
        Assert.assertEquals(FastList.newListWith(2, 3, 4), ArrayAdapter.newArrayWith(1, 2, 3).collectWith(addBlock, 1, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void injectIntoWith() {
        super.injectIntoWith();
        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Integer result = objects.injectIntoWith(1, ( injectedValued, item, parameter) -> (injectedValued + item) + parameter, 0);
        Assert.assertEquals(Integer.valueOf(7), result);
    }

    @Override
    @Test
    public void serialization() {
        super.serialization();
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2, 3, 4, 5);
        Verify.assertListsEqual(collection, deserializedCollection);
    }

    @Test
    public void testBAOSSize() {
        MutableList<Integer> mutableArrayList = ArrayAdapter.newArray();
        List<Integer> arrayList = new ArrayList<>();
        ByteArrayOutputStream stream2 = SerializeTestHelper.getByteArrayOutputStream(arrayList);
        ArrayAdapterTest.LOGGER.info("ArrayList size: {}", stream2.size());
        ArrayAdapterTest.LOGGER.info("{}", stream2);
        ByteArrayOutputStream stream1 = SerializeTestHelper.getByteArrayOutputStream(mutableArrayList);
        ArrayAdapterTest.LOGGER.info("ArrayAdapter size: {}", stream1.size());
        ArrayAdapterTest.LOGGER.info("{}", stream1);
    }

    @Override
    @Test
    public void testToString() {
        // ArrayAdapter doesn't support add and cannot contain itself
        Assert.assertEquals(FastList.newList(this.newWith(1, 2, 3, 4)).toString(), this.newWith(1, 2, 3, 4).toString());
    }

    @Override
    @Test
    public void makeString() {
        // ArrayAdapter doesn't support add and cannot contain itself
        Assert.assertEquals(FastList.newList(this.newWith(1, 2, 3, 4)).makeString(), this.newWith(1, 2, 3, 4).makeString());
    }

    @Override
    @Test
    public void appendString() {
        // ArrayAdapter doesn't support add and cannot contain itself
        StringBuilder stringBuilder = new StringBuilder();
        this.newWith(1, 2, 3, 4).appendString(stringBuilder);
        Assert.assertEquals(FastList.newList(this.newWith(1, 2, 3, 4)).makeString(), stringBuilder.toString());
    }

    @Override
    @Test
    public void asUnmodifiable() {
        super.asUnmodifiable();
        Verify.assertInstanceOf(UnmodifiableMutableList.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void clear() {
        this.newArray().clear();
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAtIndex() {
        this.newArray().add(0, null);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAllAtIndex() {
        this.newArray().addAll(0, FastList.<Integer>newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAll() {
        this.newArray().addAll(FastList.<Integer>newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addAllIterable() {
        this.newArray().addAllIterable(FastList.<Integer>newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void removeObject() {
        this.newArray().remove(null);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void removeAll() {
        this.newArray().removeAll(FastList.newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void removeAllIterable() {
        this.newArray().removeAllIterable(FastList.newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void retainAll() {
        this.newArray().retainAll(FastList.newList());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void retainAllIterable() {
        this.newArray().retainAllIterable(FastList.newList());
    }

    @Override
    @Test
    public void forEachOnRange() {
        MutableList<Integer> list = this.newWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        this.validateForEachOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void forEachWithIndexOnRange() {
        MutableList<Integer> list = this.newWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        this.validateForEachWithIndexOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachWithIndexOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachWithIndexOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachWithIndexOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachWithIndexOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachWithIndexOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void subList() {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(1, 3);
        Verify.assertSize(2, sublist);
        Verify.assertContainsAll(sublist, "B", "C");
    }

    @Override
    @Test
    public void with() {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.with(4);
        Assert.assertNotSame(coll, collWith);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), collWith);
    }

    @Override
    @Test
    public void withAll() {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.withAll(FastList.newListWith(4, 5));
        Assert.assertNotSame(coll, collWith);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), collWith);
        Assert.assertSame(collWith, collWith.withAll(FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void without() {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3, 2);
        MutableCollection<Integer> collWithout = coll.without(2);
        Assert.assertNotSame(coll, collWithout);
        Assert.assertEquals(FastList.newListWith(1, 3, 2), collWithout);
        Assert.assertSame(collWithout, collWithout.without(9));
    }

    @Override
    @Test
    public void withoutAll() {
        MutableCollection<Integer> coll = this.newWith(1, 2, 4, 2, 3, 4, 5);
        MutableCollection<Integer> collWithout = coll.withoutAll(FastList.newListWith(2, 4));
        Assert.assertNotSame(coll, collWithout);
        Assert.assertEquals(FastList.newListWith(1, 3, 5), collWithout);
        Assert.assertSame(collWithout, collWithout.withoutAll(FastList.newListWith(8, 9)));
        Assert.assertSame(collWithout, collWithout.withoutAll(FastList.<Integer>newList()));
    }
}

