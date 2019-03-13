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
package com.gs.collections.test;


import Sets.immutable;
import Sets.mutable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public interface IterableTestCase {
    @Test
    default void Object_PostSerializedEqualsAndHashCode() {
        Iterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        Object deserialized = SerializeTestHelper.serializeDeserialize(iterable);
        Assert.assertNotSame(iterable, deserialized);
    }

    @Test
    default void Object_equalsAndHashCode() {
        Verify.assertEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));
        Assert.assertNotEquals(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));
        Assert.assertNotEquals(this.newWith(2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(2, 1));
        Assert.assertNotEquals(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(3, 3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 2, 1), this.newWith(3, 3, 2, 1));
    }

    @Test
    default void Iterable_hasNext() {
        Assert.assertTrue(this.newWith(3, 2, 1).iterator().hasNext());
        Assert.assertFalse(this.newWith().iterator().hasNext());
    }

    @Test
    default void Iterable_next() {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        MutableSet<Integer> set = mutable.with();
        Assert.assertTrue(set.add(iterator.next()));
        Assert.assertTrue(set.add(iterator.next()));
        Assert.assertTrue(set.add(iterator.next()));
        IterableTestCase.assertEquals(immutable.with(3, 2, 1), set);
    }

    @Test(expected = NoSuchElementException.class)
    default void Iterable_next_throws_on_empty() {
        this.newWith().iterator().next();
    }

    @Test
    default void Iterable_next_throws_at_end() {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, ((Runnable) (iterator::next)));
        Iterator<Integer> iterator2 = iterable.iterator();
        iterator2.next();
        iterator2.next();
        iterator2.next();
        assertThrows(NoSuchElementException.class, ((Runnable) (iterator2::next)));
        assertThrows(NoSuchElementException.class, ((Runnable) (iterator2::next)));
    }
}

