/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import com.google.common.base.Function;
import com.google.common.base.Strings;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for the StringIndexer classes.
 */
public abstract class StringIndexerTest {
    private static final int ATTEMPTS = 1000;

    private SortedMap<Integer, String> mappings;

    protected StringIndexer indexer;

    private final Object lock = new Object();

    @Test
    public void concurrentAddChildNode() throws Exception {
        assertConcurrentUpdates(( from) -> Strings.repeat("a", (from + 1)));
    }

    @Test
    public void concurrentSplitNodeSuffix() throws Exception {
        assertConcurrentUpdates(( from) -> Strings.repeat("b", ((StringIndexerTest.ATTEMPTS) - from)));
    }

    @Test
    public void concurrentAddBranch() throws Exception {
        assertConcurrentUpdates(( from) -> String.format("%08o", from));
    }

    @RunWith(JUnit4.class)
    public static class CompactStringIndexerTest extends StringIndexerTest {
        @Override
        protected StringIndexer newIndexer() {
            return new CompactStringIndexer(1);
        }

        @Test
        public void basicOperations() {
            assertSize(0);
            assertNoIndex("abcdef");
            assertIndex(0, "abcdef");// root node creation

            assertIndex(0, "abcdef");// root node match

            assertSize(1);
            assertIndex(2, "abddef");// node branching, index 1 went to "ab" node.

            assertSize(3);
            assertIndex(1, "ab");
            assertSize(3);
            assertIndex(3, "abcdefghik");// new leaf creation

            assertSize(4);
            assertIndex(4, "abcdefgh");// node split

            assertSize(5);
            assertNoIndex("a");
            assertNoIndex("abc");
            assertNoIndex("abcdefg");
            assertNoIndex("abcdefghil");
            assertNoIndex("abcdefghikl");
            assertContent();
            indexer.clear();
            assertSize(0);
            assertThat(indexer.getStringForIndex(0)).isNull();
            assertThat(indexer.getStringForIndex(1000)).isNull();
        }

        @Test
        public void parentIndexUpdate() {
            assertSize(0);
            assertIndex(0, "abcdefghik");// Create 3 nodes with single common parent "abcdefgh".

            assertIndex(2, "abcdefghlm");// Index 1 went to "abcdefgh".

            assertIndex(3, "abcdefghxyz");
            assertSize(4);
            assertIndex(5, "abcdpqr");// Split parent. Index 4 went to "abcd".

            assertSize(6);
            assertIndex(1, "abcdefgh");// Check branch node indices.

            assertIndex(4, "abcd");
            assertSize(6);
            assertContent();
        }

        @Test
        public void emptyRootNode() {
            assertSize(0);
            assertIndex(0, "abc");
            assertNoIndex("");
            assertIndex(2, "def");// root node key is now empty string and has index 1.

            assertSize(3);
            assertIndex(1, "");
            assertSize(3);
            assertContent();
        }

        protected void setupTestContent() {
            assertSize(0);
            assertIndex(0, "abcdefghi");// Create leafs

            assertIndex(2, "abcdefjkl");
            assertIndex(3, "abcdefmno");
            assertIndex(4, "abcdefjklpr");
            assertIndex(6, "abcdstr");
            assertIndex(8, "012345");
            assertSize(9);
            assertIndex(1, "abcdef");// Validate inner nodes

            assertIndex(5, "abcd");
            assertIndex(7, "");
            assertSize(9);
            assertContent();
        }

        @Test
        public void dumpContent() {
            indexer = newIndexer();
            indexer.addString("abc");
            String content = indexer.toString();
            assertThat(content).contains("size = 1");
            assertThat(content).contains("contentSize = 5");
            indexer = newIndexer();
            setupTestContent();
            content = indexer.toString();
            assertThat(content).contains("size = 9");
            assertThat(content).contains("contentSize = 60");
            System.out.println(indexer);
        }

        @Test
        public void addStringResult() {
            assertSize(0);
            assertThat(indexer.addString("abcdef")).isTrue();
            assertThat(indexer.addString("abcdgh")).isTrue();
            assertThat(indexer.addString("abcd")).isFalse();
            assertThat(indexer.addString("ab")).isTrue();
        }
    }

    @RunWith(JUnit4.class)
    public static class CanonicalStringIndexerTest extends StringIndexerTest {
        @Override
        protected StringIndexer newIndexer() {
            return new CanonicalStringIndexer(new ConcurrentHashMap<String, Integer>(), new ConcurrentHashMap<Integer, String>());
        }

        @Test
        public void basicOperations() {
            assertSize(0);
            assertNoIndex("abcdef");
            assertIndex(0, "abcdef");
            assertIndex(0, "abcdef");
            assertSize(1);
            assertIndex(1, "abddef");
            assertSize(2);
            assertIndex(2, "ab");
            assertSize(3);
            assertIndex(3, "abcdefghik");
            assertSize(4);
            assertIndex(4, "abcdefgh");
            assertSize(5);
            assertNoIndex("a");
            assertNoIndex("abc");
            assertNoIndex("abcdefg");
            assertNoIndex("abcdefghil");
            assertNoIndex("abcdefghikl");
            assertContent();
            indexer.clear();
            assertSize(0);
            assertThat(indexer.getStringForIndex(0)).isNull();
            assertThat(indexer.getStringForIndex(1000)).isNull();
        }

        @Test
        public void addStringResult() {
            assertSize(0);
            assertThat(indexer.addString("abcdef")).isTrue();
            assertThat(indexer.addString("abcdgh")).isTrue();
            assertThat(indexer.addString("abcd")).isTrue();
            assertThat(indexer.addString("ab")).isTrue();
            assertThat(indexer.addString("ab")).isFalse();
        }

        protected void setupTestContent() {
            assertSize(0);
            assertIndex(0, "abcdefghi");
            assertIndex(1, "abcdefjkl");
            assertIndex(2, "abcdefmno");
            assertIndex(3, "abcdefjklpr");
            assertIndex(4, "abcdstr");
            assertIndex(5, "012345");
            assertSize(6);
            assertIndex(6, "abcdef");
            assertIndex(7, "abcd");
            assertIndex(8, "");
            assertIndex(2, "abcdefmno");
            assertSize(9);
            assertContent();
        }
    }
}

