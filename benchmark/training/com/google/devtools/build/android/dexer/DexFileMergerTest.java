/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.dexer;


import MultidexStrategy.GIVEN_SHARD;
import MultidexStrategy.MINIMAL;
import MultidexStrategy.OFF;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.runfiles.Runfiles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DexFileMerger}.
 */
@RunWith(JUnit4.class)
public class DexFileMergerTest {
    private static final Path INPUT_JAR;

    private static final Path INPUT_JAR2;

    private static final Path MAIN_DEX_LIST_FILE;

    static final String DEX_PREFIX = "classes";

    static {
        try {
            Runfiles runfiles = Runfiles.create();
            INPUT_JAR = Paths.get(runfiles.rlocation(System.getProperty("testinputjar")));
            INPUT_JAR2 = Paths.get(runfiles.rlocation(System.getProperty("testinputjar2")));
            MAIN_DEX_LIST_FILE = Paths.get(runfiles.rlocation(System.getProperty("testmaindexlist")));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Exercises DexFileMerger to write a single .dex file.
     */
    @Test
    public void testMergeDexArchive_singleOutputDex() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = runDexFileMerger(dexArchive, (256 * 256), "from_dex_archive.dex.zip");
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "classes.dex");
    }

    @Test
    public void testMergeDexArchive_duplicateInputDeduped() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = /* forceJumbo= */
        /* mainDexList= */
        /* minimalMainDex= */
        runDexFileMerger((256 * 256), false, "duplicate.dex.zip", MINIMAL, null, false, DexFileMergerTest.DEX_PREFIX, dexArchive, dexArchive);// input Jar twice to induce duplicates

        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "classes.dex");
    }

    /**
     * Similar to {@link #testMergeDexArchive_singleOutputDex} but uses --multidex=given_shard.
     */
    @Test
    public void testMergeDexArchive_givenShard() throws Exception {
        Path dexArchive = buildDexArchive(DexFileMergerTest.INPUT_JAR, "3.classes.jar");
        Path outputArchive = /* forceJumbo= */
        /* mainDexList= */
        /* minimalMainDex= */
        runDexFileMerger((256 * 256), false, "given_shard.dex.zip", GIVEN_SHARD, null, false, DexFileMergerTest.DEX_PREFIX, dexArchive);
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "classes3.dex");
    }

    /**
     * Similar to {@link #testMergeDexArchive_singleOutputDex} but different name for output dex file.
     */
    @Test
    public void testMergeDexArchive_singleOutputPrefixDex() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = /* forceJumbo= */
        /* mainDexList= */
        /* minimalMainDex= */
        runDexFileMerger((256 * 256), false, "prefix.dex.zip", MINIMAL, null, false, "noname", dexArchive);
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "noname.dex");
    }

    /**
     * Exercises DexFileMerger with two input archives.
     */
    @Test
    public void testMergeDexArchive_multipleInputs() throws Exception {
        Path dexArchive = buildDexArchive();
        Path dexArchive2 = buildDexArchive(DexFileMergerTest.INPUT_JAR2, "libtestdata.jar.dex.zip");
        Path outputArchive = /* forceJumbo= */
        /* mainDexList= */
        /* minimalMainDex= */
        runDexFileMerger((256 * 256), false, "multiple_inputs.dex.zip", MINIMAL, null, false, DexFileMergerTest.DEX_PREFIX, dexArchive, dexArchive2);
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        expectedClassCount += matchingFileCount(dexArchive2, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "classes.dex");
    }

    /**
     * Similar to {@link #testMergeDexArchive_singleOutputDex} but forces multiple output dex files.
     */
    @Test
    public void testMergeDexArchive_multidex() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = runDexFileMerger(dexArchive, 200, "multidex_from_dex_archive.dex.zip");
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertMultidexOutput(expectedClassCount, outputArchive, ImmutableSet.<String>of());
    }

    @Test
    public void testMergeDexArchive_mainDexList() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = /* forceJumbo= */
        /* minimalMainDex= */
        runDexFileMerger(200, false, "main_dex_list.dex.zip", MINIMAL, DexFileMergerTest.MAIN_DEX_LIST_FILE, false, DexFileMergerTest.DEX_PREFIX, dexArchive);
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertMainDexOutput(expectedClassCount, outputArchive, false);
    }

    @Test
    public void testMergeDexArchive_minimalMainDex() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive = /* forceJumbo= */
        /* minimalMainDex= */
        runDexFileMerger((256 * 256), false, "minimal_main_dex.dex.zip", MINIMAL, DexFileMergerTest.MAIN_DEX_LIST_FILE, true, DexFileMergerTest.DEX_PREFIX, dexArchive);
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertMainDexOutput(expectedClassCount, outputArchive, true);
    }

    @Test
    public void testMultidexOffWithMultidexFlags() throws Exception {
        Path dexArchive = buildDexArchive();
        try {
            /* forceJumbo= */
            /* mainDexList= */
            /* minimalMainDex= */
            runDexFileMerger(200, false, "classes.dex.zip", OFF, null, true, DexFileMergerTest.DEX_PREFIX, dexArchive);
            Assert.fail("Expected DexFileMerger to fail");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("--minimal-main-dex is only supported with multidex enabled, but mode is: OFF");
        }
        try {
            /* forceJumbo= */
            /* minimalMainDex= */
            runDexFileMerger(200, false, "classes.dex.zip", OFF, DexFileMergerTest.MAIN_DEX_LIST_FILE, false, DexFileMergerTest.DEX_PREFIX, dexArchive);
            Assert.fail("Expected DexFileMerger to fail");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("--main-dex-list is only supported with multidex enabled, but mode is: OFF");
        }
    }

    /**
     * Exercises --forceJumbo support.
     */
    @Test
    public void testMergeDexArchive_forceJumbo() throws Exception {
        Path dexArchive = buildDexArchive();
        Path outputArchive;
        try {
            outputArchive = /* forceJumbo= */
            /* mainDexList= */
            /* minimalMainDex= */
            runDexFileMerger((256 * 256), true, "from_dex_archive.dex.zip", OFF, null, false, DexFileMergerTest.DEX_PREFIX, dexArchive);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("--forceJumbo flag not supported");
            System.err.println("Skipping this test due to missing --forceJumbo support in Android SDK.");
            e.printStackTrace();
            return;
        }
        int expectedClassCount = matchingFileCount(dexArchive, ".*\\.class.dex$");
        assertSingleDexOutput(expectedClassCount, outputArchive, "classes.dex");
    }

    private enum ZipEntryName implements Function<ZipEntry, String> {

        INSTANCE;
        @Override
        public String apply(ZipEntry input) {
            return input.getName();
        }
    }
}

