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
package com.gs.collections.test.list;


import Lists.immutable;
import com.gs.collections.test.CollectionTestCase;
import com.gs.collections.test.IterableTestCase;
import java.util.List;
import org.junit.Test;


public interface ListTestCase extends CollectionTestCase {
    @Test
    default void List_get() {
        List<Integer> list = this.newWith(1, 2, 3);
        IterableTestCase.assertEquals(Integer.valueOf(1), list.get(0));
        IterableTestCase.assertEquals(Integer.valueOf(2), list.get(1));
        IterableTestCase.assertEquals(Integer.valueOf(3), list.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    default void List_get_negative() {
        this.newWith(1, 2, 3).get((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    default void List_get_out_of_bounds() {
        this.newWith(1, 2, 3).get(4);
    }

    @Test
    default void List_set() {
        List<Integer> list = this.newWith(1, 2, 3);
        IterableTestCase.assertEquals(Integer.valueOf(2), list.set(1, 4));
        IterableTestCase.assertEquals(immutable.with(1, 4, 3), list);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    default void List_set_negative() {
        List<Integer> list = this.newWith(1, 2, 3);
        IterableTestCase.assertEquals(Integer.valueOf(2), list.set((-1), 4));
        IterableTestCase.assertEquals(immutable.with(1, 4, 3), list);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    default void List_set_out_of_bounds() {
        List<Integer> list = this.newWith(1, 2, 3);
        IterableTestCase.assertEquals(Integer.valueOf(2), list.set(4, 4));
        IterableTestCase.assertEquals(immutable.with(1, 4, 3), list);
    }
}

