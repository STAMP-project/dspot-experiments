/**
 * Copyright 2016-present Open Networking Foundation
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
package io.atomix.core.map;


import com.google.common.collect.Sets;
import io.atomix.core.AbstractPrimitiveTest;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AtomicNavigableMapProxy}.
 */
public class AtomicNavigableMapTest extends AbstractPrimitiveTest {
    @Test
    public void testSubMaps() throws Throwable {
        AtomicNavigableMap<String, String> map = createResource("testSubMaps").sync();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            map.put(String.valueOf(letter), String.valueOf(letter));
        }
        Assert.assertEquals("a", map.firstKey());
        Assert.assertTrue(map.navigableKeySet().remove("a"));
        Assert.assertEquals("b", map.firstKey());
        Assert.assertTrue("b", map.navigableKeySet().descendingSet().remove("b"));
        Assert.assertEquals("c", map.firstKey());
        Assert.assertEquals("c", map.firstEntry().getValue().value());
        Assert.assertEquals("z", map.lastKey());
        Assert.assertTrue(map.navigableKeySet().remove("z"));
        Assert.assertEquals("y", map.lastKey());
        Assert.assertTrue(map.navigableKeySet().descendingSet().remove("y"));
        Assert.assertEquals("x", map.lastKey());
        Assert.assertEquals("x", map.lastEntry().getValue().value());
        Assert.assertEquals("d", map.subMap("d", true, "w", false).firstKey());
        Assert.assertEquals("d", map.subMap("d", true, "w", false).firstEntry().getValue().value());
        Assert.assertEquals("e", map.subMap("d", false, "w", false).firstKey());
        Assert.assertEquals("e", map.subMap("d", false, "w", false).firstEntry().getValue().value());
        Assert.assertEquals("d", map.tailMap("d", true).firstKey());
        Assert.assertEquals("d", map.tailMap("d", true).firstEntry().getValue().value());
        Assert.assertEquals("e", map.tailMap("d", false).firstKey());
        Assert.assertEquals("e", map.tailMap("d", false).firstEntry().getValue().value());
        Assert.assertEquals("w", map.headMap("w", true).navigableKeySet().descendingSet().first());
        Assert.assertEquals("v", map.headMap("w", false).navigableKeySet().descendingSet().first());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).lastKey());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).lastEntry().getValue().value());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).lastKey());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).lastEntry().getValue().value());
        Assert.assertEquals("w", map.headMap("w", true).lastKey());
        Assert.assertEquals("w", map.headMap("w", true).lastEntry().getValue().value());
        Assert.assertEquals("v", map.headMap("w", false).lastKey());
        Assert.assertEquals("v", map.headMap("w", false).lastEntry().getValue().value());
        Assert.assertEquals("d", map.tailMap("d", true).navigableKeySet().descendingSet().last());
        Assert.assertEquals("d", map.tailMap("d", true).navigableKeySet().descendingSet().last());
        Assert.assertEquals("e", map.tailMap("d", false).navigableKeySet().descendingSet().last());
        Assert.assertEquals("e", map.tailMap("d", false).navigableKeySet().descendingSet().last());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).navigableKeySet().descendingSet().first());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).navigableKeySet().descendingSet().first());
        Assert.assertEquals(20, map.subMap("d", true, "w", true).size());
        Assert.assertEquals(19, map.subMap("d", true, "w", false).size());
        Assert.assertEquals(19, map.subMap("d", false, "w", true).size());
        Assert.assertEquals(18, map.subMap("d", false, "w", false).size());
        Assert.assertEquals(20, map.subMap("d", true, "w", true).entrySet().stream().count());
        Assert.assertEquals(19, map.subMap("d", true, "w", false).entrySet().stream().count());
        Assert.assertEquals(19, map.subMap("d", false, "w", true).entrySet().stream().count());
        Assert.assertEquals(18, map.subMap("d", false, "w", false).entrySet().stream().count());
        Assert.assertEquals("d", map.subMap("d", true, "w", true).entrySet().stream().findFirst().get().getValue().value());
        Assert.assertEquals("d", map.subMap("d", true, "w", false).entrySet().stream().findFirst().get().getValue().value());
        Assert.assertEquals("e", map.subMap("d", false, "w", true).entrySet().stream().findFirst().get().getValue().value());
        Assert.assertEquals("e", map.subMap("d", false, "w", false).entrySet().stream().findFirst().get().getValue().value());
        Assert.assertEquals("w", map.subMap("d", true, "w", true).navigableKeySet().descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", map.subMap("d", true, "w", false).navigableKeySet().descendingSet().stream().findFirst().get());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).navigableKeySet().descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).navigableKeySet().descendingSet().stream().findFirst().get());
        Assert.assertEquals("d", map.subMap("d", true, "w", true).entrySet().iterator().next().getKey());
        Assert.assertEquals("w", map.subMap("d", true, "w", true).navigableKeySet().descendingIterator().next());
        Assert.assertEquals("w", map.subMap("d", true, "w", true).navigableKeySet().descendingSet().iterator().next());
        Assert.assertEquals("e", map.subMap("d", false, "w", true).entrySet().iterator().next().getKey());
        Assert.assertEquals("e", map.subMap("d", false, "w", true).navigableKeySet().descendingSet().descendingIterator().next());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).navigableKeySet().descendingIterator().next());
        Assert.assertEquals("w", map.subMap("d", false, "w", true).navigableKeySet().descendingSet().iterator().next());
        Assert.assertEquals("d", map.subMap("d", true, "w", false).entrySet().iterator().next().getKey());
        Assert.assertEquals("d", map.subMap("d", true, "w", false).navigableKeySet().descendingSet().descendingIterator().next());
        Assert.assertEquals("v", map.subMap("d", true, "w", false).navigableKeySet().descendingIterator().next());
        Assert.assertEquals("v", map.subMap("d", true, "w", false).navigableKeySet().descendingSet().iterator().next());
        Assert.assertEquals("e", map.subMap("d", false, "w", false).entrySet().iterator().next().getKey());
        Assert.assertEquals("e", map.subMap("d", false, "w", false).navigableKeySet().descendingSet().descendingIterator().next());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).navigableKeySet().descendingIterator().next());
        Assert.assertEquals("v", map.subMap("d", false, "w", false).navigableKeySet().descendingSet().iterator().next());
        Assert.assertEquals("d", map.subMap("d", true, "w", true).navigableKeySet().headSet("m", true).iterator().next());
        Assert.assertEquals("m", map.subMap("d", true, "w", true).navigableKeySet().headSet("m", true).descendingIterator().next());
        Assert.assertEquals("d", map.subMap("d", true, "w", true).navigableKeySet().headSet("m", false).iterator().next());
        Assert.assertEquals("l", map.subMap("d", true, "w", true).navigableKeySet().headSet("m", false).descendingIterator().next());
        Assert.assertEquals("m", map.subMap("d", true, "w", true).navigableKeySet().tailSet("m", true).iterator().next());
        Assert.assertEquals("w", map.subMap("d", true, "w", true).navigableKeySet().tailSet("m", true).descendingIterator().next());
        Assert.assertEquals("n", map.subMap("d", true, "w", true).navigableKeySet().tailSet("m", false).iterator().next());
        Assert.assertEquals("w", map.subMap("d", true, "w", true).navigableKeySet().tailSet("m", false).descendingIterator().next());
        Assert.assertEquals(18, map.subMap("d", true, "w", true).subMap("e", true, "v", true).subMap("d", true, "w", true).size());
        Assert.assertEquals("x", map.tailMap("d", true).navigableKeySet().descendingIterator().next());
        Assert.assertEquals("x", map.tailMap("d", true).navigableKeySet().descendingSet().iterator().next());
        Assert.assertEquals("c", map.headMap("w", true).navigableKeySet().iterator().next());
        Assert.assertEquals("c", map.headMap("w", true).navigableKeySet().descendingSet().descendingSet().iterator().next());
        map.headMap("e", false).clear();
        Assert.assertEquals("e", map.navigableKeySet().first());
        Assert.assertEquals(20, map.navigableKeySet().size());
        map.headMap("g", true).clear();
        Assert.assertEquals("h", map.navigableKeySet().first());
        Assert.assertEquals(17, map.navigableKeySet().size());
        map.tailMap("t", false).clear();
        Assert.assertEquals("t", map.navigableKeySet().last());
        Assert.assertEquals(13, map.navigableKeySet().size());
        map.tailMap("o", true).clear();
        Assert.assertEquals("n", map.navigableKeySet().last());
        Assert.assertEquals(7, map.navigableKeySet().size());
        map.navigableKeySet().subSet("k", false, "n", false).clear();
        Assert.assertEquals(5, map.navigableKeySet().size());
        Assert.assertEquals(Sets.newHashSet("h", "i", "j", "k", "n"), Sets.newHashSet(map.navigableKeySet()));
    }

    @Test
    public void testKeySetOperations() throws Throwable {
        AtomicNavigableMap<String, String> map = createResource("testKeySetOperations").sync();
        try {
            map.navigableKeySet().first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().subSet("a", false, "z", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().subSet("a", false, "z", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        Assert.assertEquals(0, map.navigableKeySet().size());
        Assert.assertTrue(map.navigableKeySet().isEmpty());
        Assert.assertEquals(0, map.navigableKeySet().subSet("a", true, "b", true).size());
        Assert.assertTrue(map.navigableKeySet().subSet("a", true, "b", true).isEmpty());
        Assert.assertEquals(0, map.navigableKeySet().headSet("a").size());
        Assert.assertTrue(map.navigableKeySet().headSet("a").isEmpty());
        Assert.assertEquals(0, map.navigableKeySet().tailSet("b").size());
        Assert.assertTrue(map.navigableKeySet().tailSet("b").isEmpty());
        for (char letter = 'a'; letter <= 'z'; letter++) {
            map.put(String.valueOf(letter), String.valueOf(letter));
        }
        Assert.assertEquals("a", map.navigableKeySet().first());
        Assert.assertEquals("z", map.navigableKeySet().last());
        Assert.assertTrue(map.navigableKeySet().remove("a"));
        Assert.assertTrue(map.navigableKeySet().remove("z"));
        Assert.assertEquals("b", map.navigableKeySet().first());
        Assert.assertEquals("y", map.navigableKeySet().last());
        try {
            map.navigableKeySet().subSet("A", false, "Z", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().subSet("A", false, "Z", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().subSet("a", true, "b", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            map.navigableKeySet().subSet("a", true, "b", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).subSet("c", true, "x", true).first());
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).subSet("c", true, "x", true).last());
        Assert.assertEquals("y", map.navigableKeySet().headSet("y", true).last());
        Assert.assertEquals("x", map.navigableKeySet().headSet("y", false).last());
        Assert.assertEquals("y", map.navigableKeySet().headSet("y", true).subSet("a", true, "z", false).last());
        Assert.assertEquals("b", map.navigableKeySet().tailSet("b", true).first());
        Assert.assertEquals("c", map.navigableKeySet().tailSet("b", false).first());
        Assert.assertEquals("b", map.navigableKeySet().tailSet("b", true).subSet("a", false, "z", true).first());
        Assert.assertEquals("b", map.navigableKeySet().higher("a"));
        Assert.assertEquals("c", map.navigableKeySet().higher("b"));
        Assert.assertEquals("y", map.navigableKeySet().lower("z"));
        Assert.assertEquals("x", map.navigableKeySet().lower("y"));
        Assert.assertEquals("b", map.navigableKeySet().ceiling("a"));
        Assert.assertEquals("b", map.navigableKeySet().ceiling("b"));
        Assert.assertEquals("y", map.navigableKeySet().floor("z"));
        Assert.assertEquals("y", map.navigableKeySet().floor("y"));
        Assert.assertEquals("c", map.navigableKeySet().subSet("c", true, "x", true).higher("b"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", true, "x", true).higher("c"));
        Assert.assertEquals("x", map.navigableKeySet().subSet("c", true, "x", true).lower("y"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", true, "x", true).lower("x"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).higher("b"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).higher("c"));
        Assert.assertEquals("e", map.navigableKeySet().subSet("c", false, "x", false).higher("d"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).lower("y"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).lower("x"));
        Assert.assertEquals("v", map.navigableKeySet().subSet("c", false, "x", false).lower("w"));
        Assert.assertEquals("c", map.navigableKeySet().subSet("c", true, "x", true).ceiling("b"));
        Assert.assertEquals("c", map.navigableKeySet().subSet("c", true, "x", true).ceiling("c"));
        Assert.assertEquals("x", map.navigableKeySet().subSet("c", true, "x", true).floor("y"));
        Assert.assertEquals("x", map.navigableKeySet().subSet("c", true, "x", true).floor("x"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).ceiling("b"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).ceiling("c"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("c", false, "x", false).ceiling("d"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).floor("y"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).floor("x"));
        Assert.assertEquals("w", map.navigableKeySet().subSet("c", false, "x", false).floor("w"));
    }

    @Test
    public void testKeySetSubSets() throws Throwable {
        AtomicNavigableMap<String, String> map = createResource("testKeySetSubSets").sync();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            map.put(String.valueOf(letter), String.valueOf(letter));
        }
        Assert.assertEquals("a", map.navigableKeySet().first());
        Assert.assertTrue(map.navigableKeySet().remove("a"));
        Assert.assertEquals("b", map.navigableKeySet().descendingSet().last());
        Assert.assertTrue("b", map.navigableKeySet().descendingSet().remove("b"));
        Assert.assertEquals("z", map.navigableKeySet().last());
        Assert.assertTrue(map.navigableKeySet().remove("z"));
        Assert.assertEquals("y", map.navigableKeySet().descendingSet().first());
        Assert.assertTrue(map.navigableKeySet().descendingSet().remove("y"));
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", false).first());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", false).first());
        Assert.assertEquals("d", map.navigableKeySet().tailSet("d", true).first());
        Assert.assertEquals("e", map.navigableKeySet().tailSet("d", false).first());
        Assert.assertEquals("w", map.navigableKeySet().headSet("w", true).descendingSet().first());
        Assert.assertEquals("v", map.navigableKeySet().headSet("w", false).descendingSet().first());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", false, "w", true).last());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", false, "w", false).last());
        Assert.assertEquals("w", map.navigableKeySet().headSet("w", true).last());
        Assert.assertEquals("v", map.navigableKeySet().headSet("w", false).last());
        Assert.assertEquals("d", map.navigableKeySet().tailSet("d", true).descendingSet().last());
        Assert.assertEquals("e", map.navigableKeySet().tailSet("d", false).descendingSet().last());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", false, "w", true).descendingSet().first());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", false, "w", false).descendingSet().first());
        Assert.assertEquals(20, map.navigableKeySet().subSet("d", true, "w", true).size());
        Assert.assertEquals(19, map.navigableKeySet().subSet("d", true, "w", false).size());
        Assert.assertEquals(19, map.navigableKeySet().subSet("d", false, "w", true).size());
        Assert.assertEquals(18, map.navigableKeySet().subSet("d", false, "w", false).size());
        Assert.assertEquals(20, map.navigableKeySet().subSet("d", true, "w", true).stream().count());
        Assert.assertEquals(19, map.navigableKeySet().subSet("d", true, "w", false).stream().count());
        Assert.assertEquals(19, map.navigableKeySet().subSet("d", false, "w", true).stream().count());
        Assert.assertEquals(18, map.navigableKeySet().subSet("d", false, "w", false).stream().count());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", true).stream().findFirst().get());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", false).stream().findFirst().get());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", true).stream().findFirst().get());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", false).stream().findFirst().get());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", true, "w", true).descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", true, "w", false).descendingSet().stream().findFirst().get());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", false, "w", true).descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", false, "w", false).descendingSet().stream().findFirst().get());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", true).iterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", true, "w", true).descendingIterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", true, "w", true).descendingSet().iterator().next());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", true).iterator().next());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", true).descendingSet().descendingIterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", false, "w", true).descendingIterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", false, "w", true).descendingSet().iterator().next());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", false).iterator().next());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", false).descendingSet().descendingIterator().next());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", true, "w", false).descendingIterator().next());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", true, "w", false).descendingSet().iterator().next());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", false).iterator().next());
        Assert.assertEquals("e", map.navigableKeySet().subSet("d", false, "w", false).descendingSet().descendingIterator().next());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", false, "w", false).descendingIterator().next());
        Assert.assertEquals("v", map.navigableKeySet().subSet("d", false, "w", false).descendingSet().iterator().next());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", true).headSet("m", true).iterator().next());
        Assert.assertEquals("m", map.navigableKeySet().subSet("d", true, "w", true).headSet("m", true).descendingIterator().next());
        Assert.assertEquals("d", map.navigableKeySet().subSet("d", true, "w", true).headSet("m", false).iterator().next());
        Assert.assertEquals("l", map.navigableKeySet().subSet("d", true, "w", true).headSet("m", false).descendingIterator().next());
        Assert.assertEquals("m", map.navigableKeySet().subSet("d", true, "w", true).tailSet("m", true).iterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", true, "w", true).tailSet("m", true).descendingIterator().next());
        Assert.assertEquals("n", map.navigableKeySet().subSet("d", true, "w", true).tailSet("m", false).iterator().next());
        Assert.assertEquals("w", map.navigableKeySet().subSet("d", true, "w", true).tailSet("m", false).descendingIterator().next());
        Assert.assertEquals(18, map.navigableKeySet().subSet("d", true, "w", true).subSet("e", true, "v", true).subSet("d", true, "w", true).size());
        Assert.assertEquals("x", map.navigableKeySet().tailSet("d", true).descendingIterator().next());
        Assert.assertEquals("x", map.navigableKeySet().tailSet("d", true).descendingSet().iterator().next());
        Assert.assertEquals("c", map.navigableKeySet().headSet("w", true).iterator().next());
        Assert.assertEquals("c", map.navigableKeySet().headSet("w", true).descendingSet().descendingSet().iterator().next());
        map.navigableKeySet().headSet("e", false).clear();
        Assert.assertEquals("e", map.navigableKeySet().first());
        Assert.assertEquals(20, map.navigableKeySet().size());
        map.navigableKeySet().headSet("g", true).clear();
        Assert.assertEquals("h", map.navigableKeySet().first());
        Assert.assertEquals(17, map.navigableKeySet().size());
        map.navigableKeySet().tailSet("t", false).clear();
        Assert.assertEquals("t", map.navigableKeySet().last());
        Assert.assertEquals(13, map.navigableKeySet().size());
        map.navigableKeySet().tailSet("o", true).clear();
        Assert.assertEquals("n", map.navigableKeySet().last());
        Assert.assertEquals(7, map.navigableKeySet().size());
        map.navigableKeySet().subSet("k", false, "n", false).clear();
        Assert.assertEquals(5, map.navigableKeySet().size());
        Assert.assertEquals(Sets.newHashSet("h", "i", "j", "k", "n"), Sets.newHashSet(map.navigableKeySet()));
    }
}

