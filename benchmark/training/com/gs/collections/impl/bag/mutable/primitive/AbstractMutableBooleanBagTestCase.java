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
package com.gs.collections.impl.bag.mutable.primitive;


import com.gs.collections.api.bag.primitive.ImmutableBooleanBag;
import com.gs.collections.api.bag.primitive.MutableBooleanBag;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract JUnit test for {@link MutableBooleanBag}.
 */
public abstract class AbstractMutableBooleanBagTestCase extends AbstractMutableBooleanCollectionTestCase {
    @Test
    public void sizeDistinct() {
        Assert.assertEquals(0L, this.newWith().sizeDistinct());
        Assert.assertEquals(1L, this.newWith(true).sizeDistinct());
        Assert.assertEquals(1L, this.newWith(true, true, true).sizeDistinct());
        Assert.assertEquals(2L, this.newWith(true, false, true, false, true).sizeDistinct());
    }

    @Override
    @Test
    public void addAllIterable() {
        super.addAllIterable();
        MutableBooleanBag bag = this.newWith();
        Assert.assertTrue(bag.addAll(BooleanArrayList.newListWith(true, false, true, false, true)));
        Assert.assertFalse(bag.addAll(new BooleanArrayList()));
        Assert.assertEquals(BooleanHashBag.newBagWith(true, false, true, false, true), bag);
        Assert.assertTrue(bag.addAll(BooleanHashBag.newBagWith(true, false, true, false, true)));
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false, false, true, true, true, true, true, true), bag);
    }

    @Test
    public void addOccurrences() {
        MutableBooleanBag bag = this.newWith();
        bag.addOccurrences(false, 3);
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false), bag);
        bag.addOccurrences(false, 2);
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(false, 0);
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(true, 0);
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(true, 1);
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false, true), bag);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOccurrences_throws() {
        this.newWith().addOccurrences(true, (-1));
    }

    @Test
    public void removeOccurrences() {
        MutableBooleanBag bag1 = this.newWith();
        Assert.assertFalse(bag1.removeOccurrences(true, 5));
        bag1.addOccurrences(true, 5);
        Assert.assertTrue(bag1.removeOccurrences(true, 2));
        Assert.assertEquals(BooleanHashBag.newBagWith(true, true, true), bag1);
        Assert.assertFalse(bag1.removeOccurrences(true, 0));
        Assert.assertEquals(BooleanHashBag.newBagWith(true, true, true), bag1);
        Assert.assertTrue(bag1.removeOccurrences(true, 5));
        Assert.assertEquals(new BooleanHashBag(), bag1);
        Assert.assertFalse(bag1.removeOccurrences(true, 5));
        Assert.assertEquals(new BooleanHashBag(), bag1);
        MutableBooleanBag bag2 = this.newWith();
        Assert.assertFalse(bag2.removeOccurrences(false, 5));
        bag2.addOccurrences(false, 5);
        Assert.assertTrue(bag2.removeOccurrences(false, 2));
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false), bag2);
        Assert.assertFalse(bag2.removeOccurrences(false, 0));
        Assert.assertEquals(BooleanHashBag.newBagWith(false, false, false), bag2);
        Assert.assertTrue(bag2.removeOccurrences(false, 5));
        Assert.assertEquals(new BooleanHashBag(), bag2);
        Assert.assertFalse(bag2.removeOccurrences(false, 5));
        Assert.assertEquals(new BooleanHashBag(), bag2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeOccurrences_throws() {
        this.newWith().removeOccurrences(true, (-1));
    }

    @Test
    public void forEachWithOccurrences() {
        StringBuilder stringBuilder = new StringBuilder();
        this.classUnderTest().forEachWithOccurrences(( argument1, argument2) -> stringBuilder.append(argument1).append(argument2));
        String string = stringBuilder.toString();
        Assert.assertTrue((("true2false1".equals(string)) || ("false1true2".equals(string))));
    }

    @Override
    @Test
    public void size() {
        super.size();
        Verify.assertSize(3, this.classUnderTest());
    }

    @Override
    @Test
    public void anySatisfy() {
        super.anySatisfy();
        long[] count = new long[]{ 0 };
        MutableBooleanBag bag = this.newWith(false, true, false);
        Assert.assertTrue(bag.anySatisfy(( value) -> {
            (count[0])++;
            return value;
        }));
        Assert.assertEquals(2L, count[0]);
    }

    @Override
    @Test
    public void allSatisfy() {
        super.allSatisfy();
        int[] count = new int[]{ 0 };
        MutableBooleanBag bag = this.newWith(false, true, false);
        Assert.assertFalse(bag.allSatisfy(( value) -> {
            (count[0])++;
            return !value;
        }));
        Assert.assertEquals(2L, count[0]);
    }

    @Override
    @Test
    public void noneSatisfy() {
        super.noneSatisfy();
        MutableBooleanBag bag = this.newWith(false, true, false);
        Assert.assertFalse(bag.noneSatisfy(( value) -> value));
    }

    @Override
    @Test
    public void collect() {
        super.collect();
        MutableBooleanBag bag = this.newWith(true, false, false, true, true, true);
        BooleanToObjectFunction<String> stringValueOf = ( parameter) -> parameter ? "true" : "false";
        Assert.assertEquals(HashBag.newBagWith("true", "false", "false", "true", "true", "true"), bag.collect(stringValueOf));
        MutableBooleanBag bag1 = this.newWith(false, false);
        Assert.assertEquals(HashBag.newBagWith("false", "false"), bag1.collect(stringValueOf));
        MutableBooleanBag bag2 = this.newWith(true, true);
        Assert.assertEquals(HashBag.newBagWith("true", "true"), bag2.collect(stringValueOf));
    }

    @Override
    @Test
    public void testEquals() {
        super.testEquals();
        MutableBooleanCollection collection1 = this.newWith(true, false, true, false);
        MutableBooleanCollection collection2 = this.newWith(true, false, false, true);
        MutableBooleanCollection collection3 = this.newWith(true, false);
        MutableBooleanCollection collection4 = this.newWith(true, true, false);
        Assert.assertEquals(collection1, collection2);
        Assert.assertNotEquals(collection3, collection4);
    }

    @Override
    @Test
    public void testHashCode() {
        super.testHashCode();
        MutableBooleanCollection collection1 = this.newWith(true, false, true, false);
        MutableBooleanCollection collection2 = this.newWith(true, false, false, true);
        MutableBooleanCollection collection3 = this.newWith(true, false);
        MutableBooleanCollection collection4 = this.newWith(true, true, false);
        Verify.assertEqualsAndHashCode(collection1, collection2);
        Assert.assertNotEquals(collection3.hashCode(), collection4.hashCode());
    }

    @Override
    @Test
    public void testToString() {
        super.testToString();
        Assert.assertEquals("[true, true, true]", BooleanHashBag.newBagWith(true, true, true).toString());
    }

    @Override
    @Test
    public void makeString() {
        super.makeString();
        Assert.assertEquals("true, true, true", BooleanHashBag.newBagWith(true, true, true).makeString());
    }

    @Override
    @Test
    public void appendString() {
        super.appendString();
        StringBuilder appendable1 = new StringBuilder();
        this.newWith(true, true, true).appendString(appendable1);
        Assert.assertEquals("true, true, true", appendable1.toString());
        StringBuilder appendable2 = new StringBuilder();
        MutableBooleanBag bag1 = this.newWith(false, false, true);
        bag1.appendString(appendable2);
        Assert.assertTrue(appendable2.toString(), ((("false, false, true".equals(appendable2.toString())) || ("true, false, false".equals(appendable2.toString()))) || ("false, true, false".equals(appendable2.toString()))));
    }

    @Override
    @Test
    public void toList() {
        super.toList();
        MutableBooleanList list = this.newWith(false, false, true).toList();
        Assert.assertTrue((((list.equals(BooleanArrayList.newListWith(false, false, true))) || (list.equals(BooleanArrayList.newListWith(true, false, false)))) || (list.equals(BooleanArrayList.newListWith(false, true, false)))));
    }

    @Test
    public void toImmutable() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().toImmutable());
        Assert.assertNotSame(this.classUnderTest(), this.classUnderTest().toImmutable());
        Verify.assertInstanceOf(ImmutableBooleanBag.class, this.classUnderTest().toImmutable());
    }
}

