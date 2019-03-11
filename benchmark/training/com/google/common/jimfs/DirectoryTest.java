/**
 * Copyright 2013 Google Inc.
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
package com.google.common.jimfs;


import Name.PARENT;
import Name.SELF;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Name.PARENT;
import static Name.SELF;


/**
 * Tests for {@link Directory}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class DirectoryTest {
    private Directory root;

    private Directory dir;

    @Test
    public void testRootDirectory() {
        assertThat(root.entryCount()).isEqualTo(3);// two for parent/self, one for dir

        assertThat(root.isEmpty()).isFalse();
        assertThat(root.entryInParent()).isEqualTo(DirectoryTest.entry(root, "/", root));
        assertThat(root.entryInParent().name()).isEqualTo(Name.simple("/"));
        DirectoryTest.assertParentAndSelf(root, root, root);
    }

    @Test
    public void testEmptyDirectory() {
        assertThat(dir.entryCount()).isEqualTo(2);
        assertThat(dir.isEmpty()).isTrue();
        DirectoryTest.assertParentAndSelf(dir, root, dir);
    }

    @Test
    public void testGet() {
        assertThat(root.get(Name.simple("foo"))).isEqualTo(DirectoryTest.entry(root, "foo", dir));
        assertThat(dir.get(Name.simple("foo"))).isNull();
        assertThat(root.get(Name.simple("Foo"))).isNull();
    }

    @Test
    public void testLink() {
        assertThat(dir.get(Name.simple("bar"))).isNull();
        File bar = Directory.create(2);
        dir.link(Name.simple("bar"), bar);
        assertThat(dir.get(Name.simple("bar"))).isEqualTo(DirectoryTest.entry(dir, "bar", bar));
    }

    @Test
    public void testLink_existingNameFails() {
        try {
            root.link(Name.simple("foo"), Directory.create(2));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testLink_parentAndSelfNameFails() {
        try {
            dir.link(Name.simple("."), Directory.create(2));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            dir.link(Name.simple(".."), Directory.create(2));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGet_normalizingCaseInsensitive() {
        File bar = Directory.create(2);
        Name barName = DirectoryTest.caseInsensitive("bar");
        dir.link(barName, bar);
        DirectoryEntry expected = new DirectoryEntry(dir, barName, bar);
        assertThat(dir.get(DirectoryTest.caseInsensitive("bar"))).isEqualTo(expected);
        assertThat(dir.get(DirectoryTest.caseInsensitive("BAR"))).isEqualTo(expected);
        assertThat(dir.get(DirectoryTest.caseInsensitive("Bar"))).isEqualTo(expected);
        assertThat(dir.get(DirectoryTest.caseInsensitive("baR"))).isEqualTo(expected);
    }

    @Test
    public void testUnlink() {
        assertThat(root.get(Name.simple("foo"))).isNotNull();
        root.unlink(Name.simple("foo"));
        assertThat(root.get(Name.simple("foo"))).isNull();
    }

    @Test
    public void testUnlink_nonExistentNameFails() {
        try {
            dir.unlink(Name.simple("bar"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testUnlink_parentAndSelfNameFails() {
        try {
            dir.unlink(Name.simple("."));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            dir.unlink(Name.simple(".."));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testUnlink_normalizingCaseInsensitive() {
        dir.link(DirectoryTest.caseInsensitive("bar"), Directory.create(2));
        assertThat(dir.get(DirectoryTest.caseInsensitive("bar"))).isNotNull();
        dir.unlink(DirectoryTest.caseInsensitive("BAR"));
        assertThat(dir.get(DirectoryTest.caseInsensitive("bar"))).isNull();
    }

    @Test
    public void testLinkDirectory() {
        Directory newDir = Directory.create(10);
        assertThat(newDir.entryInParent()).isNull();
        assertThat(newDir.get(SELF).file()).isEqualTo(newDir);
        assertThat(newDir.get(PARENT)).isNull();
        assertThat(newDir.links()).isEqualTo(1);
        dir.link(Name.simple("foo"), newDir);
        assertThat(newDir.entryInParent()).isEqualTo(DirectoryTest.entry(dir, "foo", newDir));
        assertThat(newDir.parent()).isEqualTo(dir);
        assertThat(newDir.entryInParent().name()).isEqualTo(Name.simple("foo"));
        assertThat(newDir.get(SELF)).isEqualTo(DirectoryTest.entry(newDir, ".", newDir));
        assertThat(newDir.get(PARENT)).isEqualTo(DirectoryTest.entry(newDir, "..", dir));
        assertThat(newDir.links()).isEqualTo(2);
    }

    @Test
    public void testUnlinkDirectory() {
        Directory newDir = Directory.create(10);
        dir.link(Name.simple("foo"), newDir);
        assertThat(dir.links()).isEqualTo(3);
        assertThat(newDir.entryInParent()).isEqualTo(DirectoryTest.entry(dir, "foo", newDir));
        assertThat(newDir.links()).isEqualTo(2);
        dir.unlink(Name.simple("foo"));
        assertThat(dir.links()).isEqualTo(2);
        assertThat(newDir.entryInParent()).isEqualTo(DirectoryTest.entry(dir, "foo", newDir));
        assertThat(newDir.get(SELF).file()).isEqualTo(newDir);
        assertThat(newDir.get(PARENT)).isEqualTo(DirectoryTest.entry(newDir, "..", dir));
        assertThat(newDir.links()).isEqualTo(1);
    }

    @Test
    public void testSnapshot() {
        root.link(Name.simple("bar"), TestUtils.regularFile(10));
        root.link(Name.simple("abc"), TestUtils.regularFile(10));
        // does not include . or .. and is sorted by the name
        assertThat(root.snapshot()).containsExactly(Name.simple("abc"), Name.simple("bar"), Name.simple("foo"));
    }

    @Test
    public void testSnapshot_sortsUsingStringAndNotCanonicalValueOfNames() {
        dir.link(DirectoryTest.caseInsensitive("FOO"), TestUtils.regularFile(10));
        dir.link(DirectoryTest.caseInsensitive("bar"), TestUtils.regularFile(10));
        ImmutableSortedSet<Name> snapshot = dir.snapshot();
        Iterable<String> strings = Iterables.transform(snapshot, Functions.toStringFunction());
        // "FOO" comes before "bar"
        // if the order were based on the normalized, canonical form of the names ("foo" and "bar"),
        // "bar" would come first
        assertThat(strings).containsExactly("FOO", "bar").inOrder();
    }

    // Tests for internal hash table implementation
    private static final Directory A = Directory.create(0);

    @Test
    public void testInitialState() {
        assertThat(dir.entryCount()).isEqualTo(2);
        assertThat(ImmutableSet.copyOf(dir)).containsExactly(new DirectoryEntry(dir, SELF, dir), new DirectoryEntry(dir, PARENT, root));
        assertThat(dir.get(Name.simple("foo"))).isNull();
    }

    @Test
    public void testPutAndGet() {
        dir.put(DirectoryTest.entry("foo"));
        assertThat(dir.entryCount()).isEqualTo(3);
        assertThat(ImmutableSet.copyOf(dir)).contains(DirectoryTest.entry("foo"));
        assertThat(dir.get(Name.simple("foo"))).isEqualTo(DirectoryTest.entry("foo"));
        dir.put(DirectoryTest.entry("bar"));
        assertThat(dir.entryCount()).isEqualTo(4);
        assertThat(ImmutableSet.copyOf(dir)).containsAllOf(DirectoryTest.entry("foo"), DirectoryTest.entry("bar"));
        assertThat(dir.get(Name.simple("foo"))).isEqualTo(DirectoryTest.entry("foo"));
        assertThat(dir.get(Name.simple("bar"))).isEqualTo(DirectoryTest.entry("bar"));
    }

    @Test
    public void testPutEntryForExistingNameIsIllegal() {
        dir.put(DirectoryTest.entry("foo"));
        try {
            dir.put(DirectoryTest.entry("foo"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testRemove() {
        dir.put(DirectoryTest.entry("foo"));
        dir.put(DirectoryTest.entry("bar"));
        dir.remove(Name.simple("foo"));
        assertThat(dir.entryCount()).isEqualTo(3);
        assertThat(ImmutableSet.copyOf(dir)).containsExactly(DirectoryTest.entry("bar"), new DirectoryEntry(dir, SELF, dir), new DirectoryEntry(dir, PARENT, root));
        assertThat(dir.get(Name.simple("foo"))).isNull();
        assertThat(dir.get(Name.simple("bar"))).isEqualTo(DirectoryTest.entry("bar"));
        dir.remove(Name.simple("bar"));
        assertThat(dir.entryCount()).isEqualTo(2);
        dir.put(DirectoryTest.entry("bar"));
        dir.put(DirectoryTest.entry("foo"));// these should just succeeded

    }

    @Test
    public void testManyPutsAndRemoves() {
        // test resizing/rehashing
        Set<DirectoryEntry> entriesInDir = new HashSet<>();
        entriesInDir.add(new DirectoryEntry(dir, SELF, dir));
        entriesInDir.add(new DirectoryEntry(dir, PARENT, root));
        // add 1000 entries
        for (int i = 0; i < 1000; i++) {
            DirectoryEntry entry = DirectoryTest.entry(String.valueOf(i));
            dir.put(entry);
            entriesInDir.add(entry);
            assertThat(ImmutableSet.copyOf(dir)).isEqualTo(entriesInDir);
            for (DirectoryEntry expected : entriesInDir) {
                assertThat(dir.get(expected.name())).isEqualTo(expected);
            }
        }
        // remove 1000 entries
        for (int i = 0; i < 1000; i++) {
            dir.remove(Name.simple(String.valueOf(i)));
            entriesInDir.remove(DirectoryTest.entry(String.valueOf(i)));
            assertThat(ImmutableSet.copyOf(dir)).isEqualTo(entriesInDir);
            for (DirectoryEntry expected : entriesInDir) {
                assertThat(dir.get(expected.name())).isEqualTo(expected);
            }
        }
        // mixed adds and removes
        for (int i = 0; i < 10000; i++) {
            DirectoryEntry entry = DirectoryTest.entry(String.valueOf(i));
            dir.put(entry);
            entriesInDir.add(entry);
            if ((i > 0) && ((i % 20) == 0)) {
                String nameToRemove = String.valueOf((i / 2));
                dir.remove(Name.simple(nameToRemove));
                entriesInDir.remove(DirectoryTest.entry(nameToRemove));
            }
        }
        // for this one, only test that the end result is correct
        // takes too long to test at each iteration
        assertThat(ImmutableSet.copyOf(dir)).isEqualTo(entriesInDir);
        for (DirectoryEntry expected : entriesInDir) {
            assertThat(dir.get(expected.name())).isEqualTo(expected);
        }
    }
}

