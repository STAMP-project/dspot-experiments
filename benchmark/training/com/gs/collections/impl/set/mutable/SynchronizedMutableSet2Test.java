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
package com.gs.collections.impl.set.mutable;


import Bags.mutable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.test.Verify;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link SynchronizedMutableSet}.
 */
public class SynchronizedMutableSet2Test extends AbstractMutableSetTestCase {
    @Test(expected = NoSuchElementException.class)
    public void min_empty_throws_without_comparator() {
        this.newWith().min();
    }

    @Test(expected = NoSuchElementException.class)
    public void max_empty_throws_without_comparator() {
        this.newWith().max();
    }

    @Override
    @Test
    public void testToString() {
        MutableSet<Integer> integer = this.newWith(1);
        Assert.assertEquals("[1]", integer.toString());
    }

    @Override
    @Test
    public void makeString() {
        MutableSet<Integer> integer = this.newWith(1);
        Assert.assertEquals("{1}", integer.makeString("{", ",", "}"));
    }

    @Override
    @Test
    public void appendString() {
        Appendable stringBuilder = new StringBuilder();
        MutableSet<Integer> integer = this.newWith(1);
        integer.appendString(stringBuilder, "{", ",", "}");
        Assert.assertEquals("{1}", stringBuilder.toString());
    }

    @Override
    @Test
    public void removeIf() {
        MutableSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.remove(3);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 4), integers);
    }

    @Test
    @Override
    public void getFirst() {
        Assert.assertNotNull(this.newWith(1, 2, 3).getFirst());
        Assert.assertNull(this.newWith().getFirst());
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1).getFirst());
        int first = this.newWith(1, 2).getFirst().intValue();
        Assert.assertTrue(((first == 1) || (first == 2)));
    }

    @Test
    @Override
    public void getLast() {
        Assert.assertNotNull(this.newWith(1, 2, 3).getLast());
        Assert.assertNull(this.newWith().getLast());
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1).getLast());
        int last = this.newWith(1, 2).getLast().intValue();
        Assert.assertTrue(((last == 1) || (last == 2)));
    }

    @Test
    @Override
    public void iterator() {
        MutableSet<Integer> objects = this.newWith(1, 2, 3);
        MutableBag<Integer> actual = mutable.of();
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); (i--) > 0;) {
            Assert.assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(mutable.of(1, 2, 3), actual);
    }
}

