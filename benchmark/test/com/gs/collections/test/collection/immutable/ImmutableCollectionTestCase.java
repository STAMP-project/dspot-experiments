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
package com.gs.collections.test.collection.immutable;


import com.gs.collections.api.collection.ImmutableCollection;
import com.gs.collections.test.IterableTestCase;
import com.gs.collections.test.RichIterableTestCase;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface ImmutableCollectionTestCase extends RichIterableTestCase {
    @Test
    default void ImmutableCollection_sanity_check() {
        String s = "";
        if (this.allowsDuplicates()) {
            IterableTestCase.assertEquals(2, this.newWith(s, s).size());
        } else {
            assertThrows(IllegalStateException.class, () -> this.newWith(s, s));
        }
        ImmutableCollection<String> collection = newWith(s);
        ImmutableCollection<String> newCollection = collection.newWith(s);
        if (this.allowsDuplicates()) {
            IterableTestCase.assertEquals(2, newCollection.size());
            IterableTestCase.assertEquals(this.newWith(s, s), newCollection);
        } else {
            IterableTestCase.assertEquals(1, newCollection.size());
            Assert.assertSame(collection, newCollection);
        }
    }

    @Override
    @Test
    default void Iterable_remove() {
        ImmutableCollection<Integer> collection = newWith(3, 2, 1);
        Iterator<Integer> iterator = collection.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    default void ImmutableCollection_newWith() {
        ImmutableCollection<Integer> immutableCollection = newWith(3, 3, 3, 2, 2, 1);
        ImmutableCollection<Integer> newWith = immutableCollection.newWith(4);
        IterableTestCase.assertEquals(this.newWith(3, 3, 3, 2, 2, 1, 4), newWith);
        Assert.assertNotSame(immutableCollection, newWith);
        Assert.assertThat(newWith, Matchers.instanceOf(ImmutableCollection.class));
        ImmutableCollection<Integer> newWith2 = newWith.newWith(4);
        IterableTestCase.assertEquals(this.newWith(3, 3, 3, 2, 2, 1, 4, 4), newWith2);
    }
}

