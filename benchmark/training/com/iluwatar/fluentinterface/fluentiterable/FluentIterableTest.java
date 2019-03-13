/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.fluentinterface.fluentiterable;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/12/15 - 7:00 PM
 *
 * @author Jeroen Meulemeester
 */
public abstract class FluentIterableTest {
    @Test
    public void testFirst() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 10, 9, 8);
        final Optional<Integer> first = createFluentIterable(integers).first();
        Assertions.assertNotNull(first);
        Assertions.assertTrue(first.isPresent());
        Assertions.assertEquals(integers.get(0), first.get());
    }

    @Test
    public void testFirstEmptyCollection() throws Exception {
        final List<Integer> integers = Collections.emptyList();
        final Optional<Integer> first = createFluentIterable(integers).first();
        Assertions.assertNotNull(first);
        Assertions.assertFalse(first.isPresent());
    }

    @Test
    public void testFirstCount() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 10, 9, 8);
        final List<Integer> first4 = createFluentIterable(integers).first(4).asList();
        Assertions.assertNotNull(first4);
        Assertions.assertEquals(4, first4.size());
        Assertions.assertEquals(integers.get(0), first4.get(0));
        Assertions.assertEquals(integers.get(1), first4.get(1));
        Assertions.assertEquals(integers.get(2), first4.get(2));
        Assertions.assertEquals(integers.get(3), first4.get(3));
    }

    @Test
    public void testFirstCountLessItems() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3);
        final List<Integer> first4 = createFluentIterable(integers).first(4).asList();
        Assertions.assertNotNull(first4);
        Assertions.assertEquals(3, first4.size());
        Assertions.assertEquals(integers.get(0), first4.get(0));
        Assertions.assertEquals(integers.get(1), first4.get(1));
        Assertions.assertEquals(integers.get(2), first4.get(2));
    }

    @Test
    public void testLast() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 10, 9, 8);
        final Optional<Integer> last = createFluentIterable(integers).last();
        Assertions.assertNotNull(last);
        Assertions.assertTrue(last.isPresent());
        Assertions.assertEquals(integers.get(((integers.size()) - 1)), last.get());
    }

    @Test
    public void testLastEmptyCollection() throws Exception {
        final List<Integer> integers = Collections.<Integer>emptyList();
        final Optional<Integer> last = createFluentIterable(integers).last();
        Assertions.assertNotNull(last);
        Assertions.assertFalse(last.isPresent());
    }

    @Test
    public void testLastCount() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 10, 9, 8);
        final List<Integer> last4 = createFluentIterable(integers).last(4).asList();
        Assertions.assertNotNull(last4);
        Assertions.assertEquals(4, last4.size());
        Assertions.assertEquals(Integer.valueOf(3), last4.get(0));
        Assertions.assertEquals(Integer.valueOf(10), last4.get(1));
        Assertions.assertEquals(Integer.valueOf(9), last4.get(2));
        Assertions.assertEquals(Integer.valueOf(8), last4.get(3));
    }

    @Test
    public void testLastCountLessItems() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3);
        final List<Integer> last4 = createFluentIterable(integers).last(4).asList();
        Assertions.assertNotNull(last4);
        Assertions.assertEquals(3, last4.size());
        Assertions.assertEquals(Integer.valueOf(1), last4.get(0));
        Assertions.assertEquals(Integer.valueOf(2), last4.get(1));
        Assertions.assertEquals(Integer.valueOf(3), last4.get(2));
    }

    @Test
    public void testFilter() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 10, 9, 8);
        final List<Integer> evenItems = createFluentIterable(integers).filter(( i) -> (i % 2) == 0).asList();
        Assertions.assertNotNull(evenItems);
        Assertions.assertEquals(3, evenItems.size());
        Assertions.assertEquals(Integer.valueOf(2), evenItems.get(0));
        Assertions.assertEquals(Integer.valueOf(10), evenItems.get(1));
        Assertions.assertEquals(Integer.valueOf(8), evenItems.get(2));
    }

    @Test
    public void testMap() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3);
        final List<Long> longs = createFluentIterable(integers).map(Integer::longValue).asList();
        Assertions.assertNotNull(longs);
        Assertions.assertEquals(integers.size(), longs.size());
        Assertions.assertEquals(Long.valueOf(1), longs.get(0));
        Assertions.assertEquals(Long.valueOf(2), longs.get(1));
        Assertions.assertEquals(Long.valueOf(3), longs.get(2));
    }

    @Test
    public void testForEach() {
        final List<Integer> integers = Arrays.asList(1, 2, 3);
        final Consumer<Integer> consumer = Mockito.mock(Consumer.class);
        createFluentIterable(integers).forEach(consumer);
        Mockito.verify(consumer, Mockito.times(1)).accept(1);
        Mockito.verify(consumer, Mockito.times(1)).accept(2);
        Mockito.verify(consumer, Mockito.times(1)).accept(3);
        Mockito.verifyNoMoreInteractions(consumer);
    }

    @Test
    public void testSpliterator() throws Exception {
        final List<Integer> integers = Arrays.asList(1, 2, 3);
        final Spliterator<Integer> split = createFluentIterable(integers).spliterator();
        Assertions.assertNotNull(split);
    }
}

