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
package com.gs.collections.impl.lazy.iterator;


import Lists.fixedSize;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.test.Verify;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;


/**
 * JUnit test for {@link TakeIterator}.
 */
public class TakeIteratorTest {
    @Test
    public void iterator() {
        Interval list = Interval.oneTo(5);
        Iterator<Integer> iterator1 = new TakeIterator(list.iterator(), 2);
        TakeIteratorTest.assertElements(iterator1, 2);
        Iterator<Integer> iterator2 = new TakeIterator(list, 5);
        TakeIteratorTest.assertElements(iterator2, 5);
        Iterator<Integer> iterator3 = new TakeIterator(list, 10);
        TakeIteratorTest.assertElements(iterator3, 5);
        Iterator<Integer> iterator4 = new TakeIterator(list, 0);
        TakeIteratorTest.assertElements(iterator4, 0);
        Iterator<Integer> iterator5 = new TakeIterator(fixedSize.<Integer>of(), 0);
        TakeIteratorTest.assertElements(iterator5, 0);
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> new TakeIterator<>(Lists.fixedSize.<Integer>of(), 0).remove());
    }

    @Test
    public void noSuchElementException() {
        Verify.assertThrows(NoSuchElementException.class, () -> new TakeIterator<>(Lists.fixedSize.<Integer>of(), 0).next());
        Verify.assertThrows(NoSuchElementException.class, () -> new TakeIterator<>(Lists.fixedSize.of(1, 2, 3), 0).next());
    }
}

