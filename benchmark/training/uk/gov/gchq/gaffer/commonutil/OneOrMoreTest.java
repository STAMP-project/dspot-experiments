/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class OneOrMoreTest {
    @Test
    public void shouldDeduplicateByDefault() {
        // Given
        final OneOrMore<Integer> collection = new OneOrMore();
        // When
        collection.add(1);
        collection.add(1);
        // Then
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(1, ((int) (collection.iterator().next())));
    }

    @Test
    public void shouldAddItemInConstructor() {
        // Given
        final boolean deduplicate = false;
        // When
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate, 1);
        // Then
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(1, ((int) (collection.iterator().next())));
    }

    @Test
    public void shouldRemoveAnyItem() {
        // Given
        final boolean deduplicate = true;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate, 1);
        // When
        collection.removeAnyItem();
        // Then
        Assert.assertTrue(collection.isEmpty());
        Assert.assertEquals(0, collection.size());
    }

    @Test
    public void shouldRemoveLastItemInList() {
        // Given
        final boolean deduplicate = false;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate, 1);
        collection.add(2);
        collection.add(3);
        // When
        collection.removeAnyItem();
        // Then
        Assert.assertEquals(2, collection.size());
        Assert.assertEquals(Arrays.asList(1, 2), Lists.newArrayList(collection));
    }

    @Test
    public void shouldAddItemsWithoutDeduplicate() {
        // Given
        final boolean deduplicate = false;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate);
        final Set<Integer> expectedItems = new HashSet<>();
        IntStream.rangeClosed(1, 200).forEach(expectedItems::add);
        // When
        for (int i = 200; 0 < i; i--) {
            collection.add(i);
            collection.add(i);
        }
        // Then
        Assert.assertEquals(400, collection.size());
        Assert.assertEquals(expectedItems, Sets.newHashSet(collection));
    }

    @Test
    public void shouldAddItemsWithDeduplicate() {
        // Given
        final boolean deduplicate = true;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate);
        final Set<Integer> expectedItems = new HashSet<>();
        IntStream.rangeClosed(1, 200).forEach(expectedItems::add);
        // When
        for (int i = 200; 0 < i; i--) {
            collection.add(i);
            collection.add(i);
        }
        // Then
        Assert.assertEquals(expectedItems, Sets.newHashSet(collection));
    }

    @Test
    public void shouldAddAllItemsWithoutDeduplicate() {
        // Given
        final boolean deduplicate = false;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate);
        final Set<Integer> expectedItems = new HashSet<>();
        IntStream.rangeClosed(1, 200).forEach(expectedItems::add);
        // When
        collection.addAll(expectedItems);
        collection.addAll(expectedItems);
        // Then
        Assert.assertEquals(((expectedItems.size()) * 2), collection.size());
        Assert.assertEquals(expectedItems, Sets.newHashSet(collection));
    }

    @Test
    public void shouldAddAllItemsWithDeduplicate() {
        // Given
        final boolean deduplicate = true;
        final OneOrMore<Integer> collection = new OneOrMore(deduplicate);
        final Set<Integer> expectedItems = new HashSet<>();
        IntStream.rangeClosed(1, 200).forEach(expectedItems::add);
        // When
        collection.addAll(expectedItems);
        collection.addAll(expectedItems);
        // Then
        Assert.assertEquals(expectedItems, Sets.newHashSet(collection));
    }

    @Test
    public void shouldGetEmptyIterator() {
        // Given
        final OneOrMore<Integer> collection = new OneOrMore();
        // When
        final Iterator<Integer> itr = collection.iterator();
        // Then
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void shouldGetSingletonIterator() {
        // Given
        final OneOrMore<Integer> collection = new OneOrMore();
        collection.add(1);
        // When
        final Iterator<Integer> itr = collection.iterator();
        // Then
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(1, ((int) (itr.next())));
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void shouldBeEqual() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(false, 1);
        final OneOrMore<Integer> collection2 = new OneOrMore(false, 1);
        Assert.assertEquals(collection1, collection2);
        Assert.assertEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldBeEqualWithDeduplicate() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(true, 1);
        final OneOrMore<Integer> collection2 = new OneOrMore(true, 1);
        Assert.assertEquals(collection1, collection2);
        Assert.assertEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldBeEqualWithMultipleValues() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(false, 1);
        collection1.add(2);
        final OneOrMore<Integer> collection2 = new OneOrMore(false, 1);
        collection2.add(2);
        Assert.assertEquals(collection1, collection2);
        Assert.assertEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldBeEqualWithMultipleValuesWithDeduplicate() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(true, 1);
        collection1.add(2);
        final OneOrMore<Integer> collection2 = new OneOrMore(true, 1);
        collection2.add(2);
        Assert.assertEquals(collection1, collection2);
        Assert.assertEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldNotBeEqual() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(false, 1);
        final OneOrMore<Integer> collection2 = new OneOrMore(false, 2);
        Assert.assertNotEquals(collection1, collection2);
        Assert.assertNotEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldNotBeEqualWhenDeduplicateDifferent() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(false, 1);
        final OneOrMore<Integer> collection2 = new OneOrMore(true, 1);
        Assert.assertNotEquals(collection1, collection2);
        Assert.assertNotEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    public void shouldNotBeEqualWithMultipleValues() {
        // Given
        final OneOrMore<Integer> collection1 = new OneOrMore(false, 1);
        collection1.add(2);
        final OneOrMore<Integer> collection2 = new OneOrMore(false, 1);
        collection2.add(3);
        Assert.assertNotEquals(collection1, collection2);
        Assert.assertNotEquals(collection1.hashCode(), collection2.hashCode());
    }
}

