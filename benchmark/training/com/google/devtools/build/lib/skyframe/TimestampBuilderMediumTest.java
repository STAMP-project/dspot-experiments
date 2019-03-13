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
package com.google.devtools.build.lib.skyframe;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.cache.CompactPersistentActionCache;
import com.google.devtools.build.lib.testutil.BlazeTestUtils;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.UnixGlob;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * These tests belong to {@link TimestampBuilderTest}, but they're in a separate class for now
 * because they are a little slower.
 */
@TestSpec(size = Suite.MEDIUM_TESTS)
@RunWith(JUnit4.class)
public class TimestampBuilderMediumTest extends TimestampBuilderTestCase {
    private Path cacheRoot;

    private CompactPersistentActionCache cache;

    // TODO(blaze-team): (2009) :
    // - test timestamp monotonicity is not required (i.e. set mtime backwards)
    // - test change of key causes rebuild
    @Test
    public void testUnneededInputs() throws Exception {
        Artifact hello = createSourceArtifact("hello");
        BlazeTestUtils.makeEmptyFile(hello.getPath());
        Artifact optional = createSourceArtifact("hello.optional");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newHashSet(hello, optional), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        cache = createCache();
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        BlazeTestUtils.makeEmptyFile(optional.getPath());
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        optional.getPath().delete();
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        cache = createCache();
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    @Test
    public void testPersistentCache_ModifyingInputCausesActionReexecution() throws Exception {
        // /hello -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        BlazeTestUtils.makeEmptyFile(hello.getPath());
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newHashSet(hello), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        hello.getPath().setWritable(true);
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "new content");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        buildArtifacts(persistentBuilder(createCache()), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    @Test
    public void testModifyingInputCausesActionReexecution() throws Exception {
        // /hello -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        FileSystemUtils.createDirectoryAndParents(hello.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "content1");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newHashSet(hello), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// still not rebuilt

        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "content2");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        buildArtifacts(persistentBuilder(createCache()), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    @Test
    public void testArtifactOrderingDoesNotMatter() throws Exception {
        // (/hello,/there) -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        Artifact there = createSourceArtifact("there");
        FileSystemUtils.createDirectoryAndParents(hello.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "hello");
        FileSystemUtils.writeContentAsLatin1(there.getPath(), "there");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newLinkedHashSet(ImmutableList.of(hello, there)), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Now create duplicate graph, with swapped order.
        clearActions();
        Artifact goodbye2 = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button2 = createActionButton(Sets.newLinkedHashSet(ImmutableList.of(there, hello)), Sets.newHashSet(goodbye2));
        button2.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button2.pressed).isFalse();// still not rebuilt

    }

    @Test
    public void testOldCacheKeysAreCleanedUp() throws Exception {
        // [action1] -> (/goodbye), cache key will be /goodbye
        Artifact goodbye = createDerivedArtifact("goodbye");
        FileSystemUtils.createDirectoryAndParents(goodbye.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(goodbye.getPath(), "test");
        TimestampBuilderTestCase.Button button = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newLinkedHashSet(ImmutableList.of(goodbye)));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        // action1 is cached using the cache key /goodbye.
        assertThat(cache.get(goodbye.getExecPathString())).isNotNull();
        // [action2] -> (/hello,/goodbye), cache key will be /hello
        clearActions();
        Artifact hello = createDerivedArtifact("hello");
        Artifact goodbye2 = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button2 = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newLinkedHashSet(ImmutableList.of(hello, goodbye2)));
        button2.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello, goodbye2);
        assertThat(button2.pressed).isTrue();// rebuilt

        // action2 is cached using the cache key /hello.
        assertThat(cache.get(hello.getExecPathString())).isNotNull();
        // Now, action1 should no longer be in the cache.
        assertThat(cache.get(goodbye.getExecPathString())).isNull();
    }

    @Test
    public void testArtifactNamesMatter() throws Exception {
        // /hello -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        FileSystemUtils.createDirectoryAndParents(hello.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "hello");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newHashSet(hello), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Now create duplicate graph, replacing "hello" with "hi".
        clearActions();
        Artifact hi = createSourceArtifact("hi");
        FileSystemUtils.createDirectoryAndParents(hi.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hi.getPath(), "hello");
        Artifact goodbye2 = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button2 = createActionButton(Sets.newHashSet(hi), Sets.newHashSet(goodbye2));
        button2.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye2);
        assertThat(button2.pressed).isTrue();// name changed. must rebuild.

    }

    @Test
    public void testDuplicateInputs() throws Exception {
        // (/hello,/hello) -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        FileSystemUtils.createDirectoryAndParents(hello.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "hello");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Lists.<Artifact>newArrayList(hello, hello), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "hello2");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        buildArtifacts(persistentBuilder(createCache()), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    /**
     * Tests that changing timestamp of the input file without changing it content
     * does not cause action reexecution when metadata cache uses file digests in
     * addition to the timestamp.
     */
    @Test
    public void testModifyingTimestampOnlyDoesNotCauseActionReexecution() throws Exception {
        // /hello -> [action] -> /goodbye
        Artifact hello = createSourceArtifact("hello");
        FileSystemUtils.createDirectoryAndParents(hello.getPath().getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "content1");
        Artifact goodbye = createDerivedArtifact("goodbye");
        TimestampBuilderTestCase.Button button = createActionButton(Sets.newHashSet(hello), Sets.newHashSet(goodbye));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent caches, including metadata cache does not cause
        // a rebuild
        cache.save();
        Builder builder = persistentBuilder(createCache());
        buildArtifacts(builder, goodbye);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    @Test
    public void testPersistentCache_ModifyingOutputCausesActionReexecution() throws Exception {
        // [action] -> /hello
        Artifact hello = createDerivedArtifact("hello");
        TimestampBuilderTestCase.Button button = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newHashSet(hello));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        hello.getPath().setWritable(true);
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "new content");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        buildArtifacts(persistentBuilder(createCache()), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

    }

    @Test
    public void testPersistentCache_missingFilenameIndexCausesActionReexecution() throws Exception {
        // [action] -> /hello
        Artifact hello = createDerivedArtifact("hello");
        TimestampBuilderTestCase.Button button = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newHashSet(hello));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        hello.getPath().setWritable(true);
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "new content");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Creating a new persistent cache does not cause a rebuild
        cache.save();
        // Remove filename index file.
        assertThat(Iterables.getOnlyElement(UnixGlob.forPath(cacheRoot).addPattern("filename_index*").globInterruptible()).delete()).isTrue();
        // Now first cache creation attempt should cause IOException while renaming corrupted files.
        // Second attempt will initialize empty cache, causing rebuild.
        try {
            createCache();
            Assert.fail("Expected IOException");
        } catch (IOException e) {
            assertThat(e).hasMessage("Failed action cache referential integrity check: empty index");
        }
        buildArtifacts(persistentBuilder(createCache()), hello);
        assertThat(button.pressed).isTrue();// rebuilt due to the missing filename index

    }

    @Test
    public void testPersistentCache_failedIntegrityCheckCausesActionReexecution() throws Exception {
        // [action] -> /hello
        Artifact hello = createDerivedArtifact("hello");
        TimestampBuilderTestCase.Button button = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newHashSet(hello));
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// built

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        hello.getPath().setWritable(true);
        FileSystemUtils.writeContentAsLatin1(hello.getPath(), "new content");
        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isTrue();// rebuilt

        button.pressed = false;
        buildArtifacts(persistentBuilder(cache), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        cache.save();
        // Get filename index path and store a copy of it.
        Path indexPath = Iterables.getOnlyElement(UnixGlob.forPath(cacheRoot).addPattern("filename_index*").globInterruptible());
        Path indexCopy = scratch.resolve("index_copy");
        FileSystemUtils.copyFile(indexPath, indexCopy);
        // Add extra records to the action cache and indexer.
        Artifact helloExtra = createDerivedArtifact("hello_extra");
        TimestampBuilderTestCase.Button buttonExtra = createActionButton(TimestampBuilderTestCase.emptySet, Sets.newHashSet(helloExtra));
        buildArtifacts(persistentBuilder(cache), helloExtra);
        assertThat(buttonExtra.pressed).isTrue();// built

        cache.save();
        assertThat(indexPath.getFileSize()).isGreaterThan(indexCopy.getFileSize());
        // Validate current cache.
        buildArtifacts(persistentBuilder(createCache()), hello);
        assertThat(button.pressed).isFalse();// not rebuilt

        // Restore outdated file index.
        FileSystemUtils.copyFile(indexCopy, indexPath);
        // Now first cache creation attempt should cause IOException while renaming corrupted files.
        // Second attempt will initialize empty cache, causing rebuild.
        try {
            createCache();
            Assert.fail("Expected IOException");
        } catch (IOException e) {
            assertThat(e).hasMessageThat().contains("Failed action cache referential integrity check");
        }
        // Validate cache with incorrect (out-of-date) filename index.
        buildArtifacts(persistentBuilder(createCache()), hello);
        assertThat(button.pressed).isTrue();// rebuilt due to the out-of-date index

    }
}

