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


import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.runfiles.Runfiles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DexFileSplitter}.
 */
@RunWith(JUnit4.class)
public class DexFileSplitterTest {
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

    @Test
    public void testSingleInputSingleOutput() throws Exception {
        Path dexArchive = buildDexArchive();
        ImmutableList<Path> outputArchives = runDexSplitter((256 * 256), "from_single", dexArchive);
        assertThat(outputArchives).hasSize(1);
        ImmutableSet<String> expectedFiles = dexEntries(dexArchive);
        assertThat(dexEntries(outputArchives.get(0))).containsExactlyElementsIn(expectedFiles);
    }

    @Test
    public void testDuplicateInputIgnored() throws Exception {
        Path dexArchive = buildDexArchive();
        ImmutableList<Path> outputArchives = runDexSplitter((256 * 256), "from_duplicate", dexArchive, dexArchive);
        assertThat(outputArchives).hasSize(1);
        ImmutableSet<String> expectedFiles = dexEntries(dexArchive);
        assertThat(dexEntries(outputArchives.get(0))).containsExactlyElementsIn(expectedFiles);
    }

    @Test
    public void testSingleInputMultidexOutput() throws Exception {
        Path dexArchive = buildDexArchive();
        ImmutableList<Path> outputArchives = runDexSplitter(200, "multidex_from_single", dexArchive);
        assertThat(outputArchives.size()).isGreaterThan(1);// test sanity

        ImmutableSet<String> expectedEntries = dexEntries(dexArchive);
        assertExpectedEntries(outputArchives, expectedEntries);
    }

    @Test
    public void testMultipleInputsMultidexOutput() throws Exception {
        Path dexArchive = buildDexArchive();
        Path dexArchive2 = buildDexArchive(DexFileSplitterTest.INPUT_JAR2, "jar2.dex.zip");
        ImmutableList<Path> outputArchives = runDexSplitter(200, "multidex", dexArchive, dexArchive2);
        assertThat(outputArchives.size()).isGreaterThan(1);// test sanity

        HashSet<String> expectedEntries = new HashSet<>();
        expectedEntries.addAll(dexEntries(dexArchive));
        expectedEntries.addAll(dexEntries(dexArchive2));
        assertExpectedEntries(outputArchives, expectedEntries);
    }

    /**
     * Tests that the same input creates identical output in 2 runs.  Flakiness here would indicate
     * race conditions or other concurrency issues.
     */
    @Test
    public void testDeterminism() throws Exception {
        Path dexArchive = buildDexArchive();
        Path dexArchive2 = buildDexArchive(DexFileSplitterTest.INPUT_JAR2, "jar2.dex.zip");
        ImmutableList<Path> outputArchives = runDexSplitter(200, "det1", dexArchive, dexArchive2);
        assertThat(outputArchives.size()).isGreaterThan(1);// test sanity

        ImmutableList<Path> outputArchives2 = runDexSplitter(200, "det2", dexArchive, dexArchive2);
        assertThat(outputArchives2).hasSize(outputArchives.size());// paths differ though

        Path outputRoot2 = outputArchives2.get(0).getParent();
        for (Path outputArchive : outputArchives) {
            ImmutableList<ZipEntry> expectedEntries;
            try (ZipFile zip = new ZipFile(outputArchive.toFile())) {
                expectedEntries = zip.stream().collect(ImmutableList.toImmutableList(ImmutableList));
            }
            ImmutableList<ZipEntry> actualEntries;
            try (ZipFile zip2 = new ZipFile(outputRoot2.resolve(outputArchive.getFileName()).toFile())) {
                actualEntries = zip2.stream().collect(ImmutableList.toImmutableList(ImmutableList));
            }
            int len = expectedEntries.size();
            assertThat(actualEntries).hasSize(len);
            for (int i = 0; i < len; ++i) {
                ZipEntry expected = expectedEntries.get(i);
                ZipEntry actual = actualEntries.get(i);
                assertThat(actual.getName()).named(actual.getName()).isEqualTo(expected.getName());
                assertThat(actual.getSize()).named(actual.getName()).isEqualTo(expected.getSize());
                assertThat(actual.getCrc()).named(actual.getName()).isEqualTo(expected.getCrc());
            }
        }
    }

    @Test
    public void testMainDexList() throws Exception {
        Path dexArchive = buildDexArchive();
        ImmutableList<Path> outputArchives = /* inclusionFilterJar= */
        /* minimalMainDex= */
        runDexSplitter(200, null, "main_dex_list", DexFileSplitterTest.MAIN_DEX_LIST_FILE, false, dexArchive);
        ImmutableSet<String> expectedEntries = dexEntries(dexArchive);
        assertThat(outputArchives.size()).isGreaterThan(1);// test sanity

        assertThat(dexEntries(outputArchives.get(0))).containsAllIn(DexFileSplitterTest.expectedMainDexEntries());
        assertExpectedEntries(outputArchives, expectedEntries);
    }

    @Test
    public void testMainDexList_containsForbidden() throws Exception {
        Path dexArchive = buildDexArchive();
        Path mainDexFile = Files.createTempFile("main_dex_list", ".txt");
        Files.write(mainDexFile, ImmutableList.of("com/google/Ok.class", "j$/my/Bad.class"), StandardCharsets.UTF_8);
        try {
            /* inclusionFilterJar= */
            /* minimalMainDex= */
            runDexSplitter((256 * 256), null, "invalid_main_dex_list", mainDexFile, false, dexArchive);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains("j$");
        }
    }

    @Test
    public void testMinimalMainDex() throws Exception {
        Path dexArchive = buildDexArchive();
        ImmutableList<Path> outputArchives = /* inclusionFilterJar= */
        /* minimalMainDex= */
        runDexSplitter((256 * 256), null, "minimal_main_dex", DexFileSplitterTest.MAIN_DEX_LIST_FILE, true, dexArchive);
        ImmutableSet<String> expectedEntries = dexEntries(dexArchive);
        assertThat(outputArchives.size()).isGreaterThan(1);// test sanity

        assertThat(dexEntries(outputArchives.get(0))).containsExactlyElementsIn(DexFileSplitterTest.expectedMainDexEntries());
        assertExpectedEntries(outputArchives, expectedEntries);
    }

    @Test
    public void testInclusionFilterJar() throws Exception {
        Path dexArchive = buildDexArchive();
        Path dexArchive2 = buildDexArchive(DexFileSplitterTest.INPUT_JAR2, "jar2.dex.zip");
        ImmutableList<Path> outputArchives = /* mainDexList= */
        /* minimalMainDex= */
        runDexSplitter((256 * 256), DexFileSplitterTest.INPUT_JAR2, "filtered", null, false, dexArchive, dexArchive2);
        // Only expect entries from the Jar we filtered by
        assertExpectedEntries(outputArchives, dexEntries(dexArchive2));
    }

    @Test
    public void testMultidexOffWithMultidexFlags() throws Exception {
        Path dexArchive = buildDexArchive();
        try {
            /* inclusionFilterJar= */
            /* mainDexList= */
            /* minimalMainDex= */
            runDexSplitter(200, null, "should_fail", null, true, dexArchive);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("--minimal-main-dex not allowed without --main-dex-list");
        }
    }

    // Can't use lambda for Java 7 compatibility so we can run this Jar through dx without desugaring.
    private enum ZipEntryName implements Function<ZipEntry, String> {

        INSTANCE;
        @Override
        public String apply(ZipEntry input) {
            return input.getName();
        }
    }
}

