/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class CollectionUtilsTest {
    @Test
    public void testCreateCollectionFromPotentialCollection() {
        Collection<String> collectionFromElement = CollectionUtils.asCollection("item");
        Collection<String> collectionFromNull = CollectionUtils.asCollection(null);
        Collection<String> collectionFromArray = CollectionUtils.asCollection(new String[]{ "item1", "item2" });
        Collection<String> collectionFromCollection = CollectionUtils.asCollection(Arrays.asList("item1", "item2"));
        Collection<String> collectionFromSpliterator = CollectionUtils.asCollection(Spliterators.spliterator(new String[]{ "item1", "item2" }, Spliterator.ORDERED));
        Collection<String> collectionFromIterable = CollectionUtils.asCollection(((Iterable) (() -> Arrays.asList("item1", "item2").iterator())));
        Assert.assertEquals(1, collectionFromElement.size());
        Assert.assertEquals(0, collectionFromNull.size());
        Assert.assertEquals(2, collectionFromArray.size());
        Assert.assertEquals(2, collectionFromCollection.size());
        Assert.assertEquals(2, collectionFromSpliterator.size());
        Assert.assertEquals(2, collectionFromIterable.size());
    }

    @Test
    public void testIntersect() {
        TreeSet<Integer> result1 = CollectionUtils.intersect(Arrays.asList(1, 2, 4, 5), Arrays.asList(1, 3, 5, 7), TreeSet::new);
        TreeSet<Integer> result2 = CollectionUtils.intersect(Collections.emptyList(), Arrays.asList(1, 3, 5, 7), TreeSet::new);
        List<Integer> result3 = CollectionUtils.intersect(Collections.singletonList(1), Arrays.asList(1, 3, 5, 7), ArrayList::new);
        Assert.assertEquals(new TreeSet<>(Arrays.asList(1, 5)), result1);
        Assert.assertEquals(new TreeSet<>(), result2);
        Assert.assertEquals(Collections.singletonList(1), result3);
    }
}

