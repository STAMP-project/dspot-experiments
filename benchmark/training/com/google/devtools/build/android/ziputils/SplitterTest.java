/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.ziputils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Splitter}.
 */
@RunWith(JUnit4.class)
public class SplitterTest {
    private static final String ARCHIVE_DIR_SUFFIX = "/";

    private static final String ARCHIVE_FILE_SEPARATOR = "/";

    private static final String CLASS_SUFFIX = ".class";

    @Test
    public void testAssign() {
        int size = 10;
        Collection<String> input;
        ArrayList<String> filter;
        Map<String, Integer> output;
        input = genEntries(size, size);
        filter = new ArrayList<>(10);
        for (int i = 0; i < size; i++) {
            filter.add(((((("dir" + i) + (SplitterTest.ARCHIVE_FILE_SEPARATOR)) + "file") + i) + (SplitterTest.CLASS_SUFFIX)));
        }
        Splitter splitter = new Splitter((size + 1), input.size());
        splitter.assign(filter);
        splitter.nextShard();
        output = new LinkedHashMap<>();
        for (String path : input) {
            output.put(path, splitter.assign(path));
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String path = (((("dir" + i) + (SplitterTest.ARCHIVE_FILE_SEPARATOR)) + "file") + j) + (SplitterTest.CLASS_SUFFIX);
                if (i == j) {
                    assertThat(output.get(path)).named(path).isEqualTo(0);
                } else {
                    assertThat(output.get(path)).named(path).isEqualTo((i + 1));
                }
            }
        }
    }

    /**
     * Test splitting of single-ile packages. Note, this is also testing for the situation
     * where input entries are unordered, and thus appearing to be in different packages,
     * to the implementation that only confiders the previous file to determine package
     * boundaries.
     */
    @Test
    public void testSingleFilePackages() {
        int[][] params = new int[][]{ new int[]{ 1, 1, 1 }// one shard, for one package with one file
        // one shard, for one package with one file
        // one shard, for one package with one file
        , new int[]{ 1, 2, 1 }// one shard, for two packages, with one file each
        // one shard, for two packages, with one file each
        // one shard, for two packages, with one file each
        , new int[]{ 1, 10, 1 }// one shard, for ten packages, with one file each
        // one shard, for ten packages, with one file each
        // one shard, for ten packages, with one file each
        , new int[]{ 2, 2, 1 }// ...
        // ...
        // ...
        , new int[]{ 2, 10, 1 }, new int[]{ 2, 100, 1 }, new int[]{ 10, 10, 1 }, new int[]{ 10, 15, 1 }, new int[]{ 10, 95, 1 }, new int[]{ 97, 10000, 1 } };
        comboRunner(params);
    }

    /**
     * Test cases where the number of shards is less than the number
     * of packages. This implies that the package size is less than
     * the average shard size. We expect shards to be multiple of
     * package size.
     */
    @Test
    public void testPackageSplit() {
        int[][] params = new int[][]{ new int[]{ 2, 3, 2 }// two shards, for three packages, with two files each
        // two shards, for three packages, with two files each
        // two shards, for three packages, with two files each
        , new int[]{ 2, 3, 9 }// ...
        // ...
        // ...
        , new int[]{ 2, 3, 10 }, new int[]{ 2, 3, 11 }, new int[]{ 2, 3, 19 }, new int[]{ 2, 10, 2 }, new int[]{ 2, 10, 9 }, new int[]{ 2, 10, 10 }, new int[]{ 2, 10, 11 }, new int[]{ 2, 10, 19 }, new int[]{ 10, 11, 2 }, new int[]{ 10, 11, 9 }, new int[]{ 10, 11, 10 }, new int[]{ 10, 11, 11 }, new int[]{ 10, 11, 19 }, new int[]{ 10, 111, 2 }, new int[]{ 10, 111, 9 }, new int[]{ 10, 111, 10 }, new int[]{ 10, 111, 11 }, new int[]{ 10, 111, 19 }, new int[]{ 25, 1000, 8 }, new int[]{ 25, 1000, 10 }, new int[]{ 25, 1000, 19 }, new int[]{ 250, 10000, 19 } };
        comboRunner(params);
    }

    /**
     * Tests situations where the number of shards exceeds the number of
     * packages (but not the number of files). That is, the implementation
     * must split at least one package.
     */
    @Test
    public void testForceSplit() {
        int[][] params = new int[][]{ new int[]{ 2, 1, 2 }// two shards, for one package, with two files
        // two shards, for one package, with two files
        // two shards, for one package, with two files
        , new int[]{ 2, 1, 9 }// ...
        // ...
        // ...
        , new int[]{ 2, 1, 10 }, new int[]{ 2, 1, 11 }, new int[]{ 3, 2, 2 }, new int[]{ 10, 9, 2 }, new int[]{ 10, 2, 9 }, new int[]{ 10, 9, 9 }, new int[]{ 10, 2, 10 }, new int[]{ 10, 9, 10 }, new int[]{ 10, 2, 11 }, new int[]{ 10, 9, 11 }, new int[]{ 10, 2, 111 }, new int[]{ 10, 9, 111 }, new int[]{ 100, 12, 9 }, new int[]{ 100, 12, 9 }, new int[]{ 100, 10, 10 }, new int[]{ 100, 10, 10 }, new int[]{ 100, 10, 11 }, new int[]{ 100, 20, 111 } };
        comboRunner(params);
    }

    /**
     * Tests situation where the number of shards requested exceeds the
     * the number of files. Empty shards are expected.
     */
    @Test
    public void testEmptyShards() {
        int[][] params = new int[][]{ new int[]{ 2, 1, 1 }// two shards, for one package, with one files
        // two shards, for one package, with one files
        // two shards, for one package, with one files
        , new int[]{ 10, 2, 2 }, new int[]{ 100, 10, 9 }, new int[]{ 100, 9, 10 } };
        comboRunner(params);
    }
}

