/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;


public class KeyValueSegmentIteratorTest {
    private final KeyValueSegment segmentOne = new KeyValueSegment("one", "one", 0);

    private final KeyValueSegment segmentTwo = new KeyValueSegment("two", "window", 1);

    private final HasNextCondition hasNextCondition = Iterator::hasNext;

    private SegmentIterator<KeyValueSegment> iterator = null;

    @Test
    public void shouldIterateOverAllSegments() {
        iterator = new SegmentIterator(asList(segmentOne, segmentTwo).iterator(), hasNextCondition, Bytes.wrap("a".getBytes()), Bytes.wrap("z".getBytes()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("a", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("a", "1"), toStringKeyValue(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("b", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("b", "2"), toStringKeyValue(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("c", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("c", "3"), toStringKeyValue(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("d", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("d", "4"), toStringKeyValue(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldNotThrowExceptionOnHasNextWhenStoreClosed() {
        iterator = new SegmentIterator(singletonList(segmentOne).iterator(), hasNextCondition, Bytes.wrap("a".getBytes()), Bytes.wrap("z".getBytes()));
        iterator.currentIterator = segmentOne.all();
        segmentOne.close();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldOnlyIterateOverSegmentsInRange() {
        iterator = new SegmentIterator(asList(segmentOne, segmentTwo).iterator(), hasNextCondition, Bytes.wrap("a".getBytes()), Bytes.wrap("b".getBytes()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("a", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("a", "1"), toStringKeyValue(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("b", new String(iterator.peekNextKey().get()));
        Assert.assertEquals(KeyValue.pair("b", "2"), toStringKeyValue(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementOnPeekNextKeyIfNoNext() {
        iterator = new SegmentIterator(asList(segmentOne, segmentTwo).iterator(), hasNextCondition, Bytes.wrap("f".getBytes()), Bytes.wrap("h".getBytes()));
        iterator.peekNextKey();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementOnNextIfNoNext() {
        iterator = new SegmentIterator(asList(segmentOne, segmentTwo).iterator(), hasNextCondition, Bytes.wrap("f".getBytes()), Bytes.wrap("h".getBytes()));
        iterator.next();
    }
}

