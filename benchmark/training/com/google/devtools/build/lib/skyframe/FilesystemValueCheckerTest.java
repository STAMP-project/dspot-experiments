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


import ModifiedFileSet.EVERYTHING_MODIFIED;
import NullEventHandler.INSTANCE;
import OutErr.SYSTEM_OUT_ERR;
import SkyframeExecutor.DEFAULT_THREAD_COUNT;
import Symlinks.NOFOLLOW;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.ActionLookupValue.ActionLookupKey;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.actions.FileArtifactValue.RemoteFileArtifactValue;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.actions.FileValue;
import com.google.devtools.build.lib.testutil.TimestampGranularityUtils;
import com.google.devtools.build.lib.vfs.BatchStat;
import com.google.devtools.build.lib.vfs.FileStatus;
import com.google.devtools.build.lib.vfs.FileStatusWithDigest;
import com.google.devtools.build.lib.vfs.FileStatusWithDigestAdapter;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import com.google.devtools.build.skyframe.Differencer.Diff;
import com.google.devtools.build.skyframe.EvaluationContext;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FilesystemValueChecker}.
 */
@RunWith(JUnit4.class)
public class FilesystemValueCheckerTest {
    private static final EvaluationContext EVALUATION_OPTIONS = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(DEFAULT_THREAD_COUNT).setEventHander(INSTANCE).build();

    private RecordingDifferencer differencer;

    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private FilesystemValueCheckerTest.MockFileSystem fs;

    private Path pkgRoot;

    @Test
    public void testEmpty() throws Exception {
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
    }

    @Test
    public void testSimple() throws Exception {
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        Path path = getPath("/foo");
        FileSystemUtils.createEmptyFile(path);
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        SkyKey skyKey = FileStateValue.key(RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/foo")));
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(skyKey), FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        FileSystemUtils.writeContentAsLatin1(path, "hello");
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), skyKey);
        // The dirty bits are not reset until the FileValues are actually revalidated.
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), skyKey);
        differencer.invalidate(ImmutableList.of(skyKey));
        result = driver.evaluate(ImmutableList.of(skyKey), FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
    }

    /**
     * Tests that an already-invalidated value can still be marked changed: symlink points at sym1.
     * Invalidate symlink by changing sym1 from pointing at path to point to sym2. This only dirties
     * (rather than changes) symlink because sym2 still points at path, so all symlink stats remain
     * the same. Then do a null build, change sym1 back to point at path, and change symlink to not be
     * a symlink anymore. The fact that it is not a symlink should be detected.
     */
    @Test
    public void testDirtySymlink() throws Exception {
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        Path path = getPath("/foo");
        FileSystemUtils.writeContentAsLatin1(path, "foo contents");
        // We need the intermediate sym1 and sym2 so that we can dirty a child of symlink without
        // actually changing the FileValue calculated for symlink (if we changed the contents of foo,
        // the FileValue created for symlink would notice, since it stats foo).
        Path sym1 = getPath("/sym1");
        Path sym2 = getPath("/sym2");
        Path symlink = getPath("/bar");
        FileSystemUtils.ensureSymbolicLink(symlink, sym1);
        FileSystemUtils.ensureSymbolicLink(sym1, path);
        FileSystemUtils.ensureSymbolicLink(sym2, path);
        SkyKey fooKey = FileValue.key(RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/foo")));
        RootedPath symlinkRootedPath = RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/bar"));
        SkyKey symlinkKey = FileValue.key(symlinkRootedPath);
        SkyKey symlinkFileStateKey = FileStateValue.key(symlinkRootedPath);
        RootedPath sym1RootedPath = RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/sym1"));
        SkyKey sym1FileStateKey = FileStateValue.key(sym1RootedPath);
        Iterable<SkyKey> allKeys = ImmutableList.of(symlinkKey, fooKey);
        // First build -- prime the graph.
        EvaluationResult<FileValue> result = driver.evaluate(allKeys, FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FileValue symlinkValue = result.get(symlinkKey);
        FileValue fooValue = result.get(fooKey);
        assertWithMessage(symlinkValue.toString()).that(symlinkValue.isSymlink()).isTrue();
        // Digest is not always available, so use size as a proxy for contents.
        assertThat(symlinkValue.getSize()).isEqualTo(fooValue.getSize());
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        // Before second build, move sym1 to point to sym2.
        assertThat(sym1.delete()).isTrue();
        FileSystemUtils.ensureSymbolicLink(sym1, sym2);
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), sym1FileStateKey);
        differencer.invalidate(ImmutableList.of(sym1FileStateKey));
        result = driver.evaluate(ImmutableList.<SkyKey>of(), FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), sym1FileStateKey);
        // Before third build, move sym1 back to original (so change pruning will prevent signaling of
        // its parents, but change symlink for real.
        assertThat(sym1.delete()).isTrue();
        FileSystemUtils.ensureSymbolicLink(sym1, path);
        assertThat(symlink.delete()).isTrue();
        FileSystemUtils.writeContentAsLatin1(symlink, "new symlink contents");
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), symlinkFileStateKey);
        differencer.invalidate(ImmutableList.of(symlinkFileStateKey));
        result = driver.evaluate(allKeys, FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        symlinkValue = result.get(symlinkKey);
        assertWithMessage(symlinkValue.toString()).that(symlinkValue.isSymlink()).isFalse();
        assertThat(result.get(fooKey)).isEqualTo(fooValue);
        assertThat(symlinkValue.getSize()).isNotEqualTo(fooValue.getSize());
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
    }

    @Test
    public void testExplicitFiles() throws Exception {
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        Path path1 = getPath("/foo1");
        Path path2 = getPath("/foo2");
        FileSystemUtils.createEmptyFile(path1);
        FileSystemUtils.createEmptyFile(path2);
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        SkyKey key1 = FileStateValue.key(RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/foo1")));
        SkyKey key2 = FileStateValue.key(RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/foo2")));
        Iterable<SkyKey> skyKeys = ImmutableList.of(key1, key2);
        EvaluationResult<SkyValue> result = driver.evaluate(skyKeys, FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        // Wait for the timestamp granularity to elapse, so updating the files will observably advance
        // their ctime.
        TimestampGranularityUtils.waitForTimestampGranularity(System.currentTimeMillis(), SYSTEM_OUT_ERR);
        // Update path1's contents and mtime. This will update the file's ctime.
        FileSystemUtils.writeContentAsLatin1(path1, "hello1");
        path1.setLastModifiedTime(27);
        // Update path2's mtime but not its contents. We expect that an mtime change suffices to update
        // the ctime.
        path2.setLastModifiedTime(42);
        // Assert that both files changed. The change detection relies, among other things, on ctime
        // change.
        FilesystemValueCheckerTest.assertDiffWithNewValues(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker), key1, key2);
        differencer.invalidate(skyKeys);
        result = driver.evaluate(skyKeys, FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isFalse();
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
    }

    @Test
    public void testFileWithIOExceptionNotConsideredDirty() throws Exception {
        Path path = getPath("/testroot/foo");
        path.getParentDirectory().createDirectory();
        path.createSymbolicLink(PathFragment.create("bar"));
        fs.readlinkThrowsIoException = true;
        SkyKey fileKey = FileStateValue.key(RootedPath.toRootedPath(Root.fromPath(pkgRoot), PathFragment.create("foo")));
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(fileKey), FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        fs.readlinkThrowsIoException = false;
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        Diff diff = FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker);
        assertThat(diff.changedKeysWithoutNewValues()).isEmpty();
        assertThat(diff.changedKeysWithNewValues()).isEmpty();
    }

    @Test
    public void testFilesInCycleNotConsideredDirty() throws Exception {
        Path path1 = pkgRoot.getRelative("foo1");
        Path path2 = pkgRoot.getRelative("foo2");
        Path path3 = pkgRoot.getRelative("foo3");
        FileSystemUtils.ensureSymbolicLink(path1, path2);
        FileSystemUtils.ensureSymbolicLink(path2, path3);
        FileSystemUtils.ensureSymbolicLink(path3, path1);
        SkyKey fileKey1 = FileValue.key(RootedPath.toRootedPath(Root.fromPath(pkgRoot), path1));
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(fileKey1), FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        Diff diff = FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker);
        assertThat(diff.changedKeysWithoutNewValues()).isEmpty();
        assertThat(diff.changedKeysWithNewValues()).isEmpty();
    }

    @Test
    public void testDirtyActions() throws Exception {
        checkDirtyActions(null, false);
    }

    @Test
    public void testDirtyActionsBatchStat() throws Exception {
        checkDirtyActions(new BatchStat() {
            @Override
            public List<FileStatusWithDigest> batchStat(boolean useDigest, boolean includeLinks, Iterable<PathFragment> paths) throws IOException {
                List<FileStatusWithDigest> stats = new ArrayList<>();
                for (PathFragment pathFrag : paths) {
                    stats.add(FileStatusWithDigestAdapter.adapt(fs.getPath("/").getRelative(pathFrag).statIfFound(NOFOLLOW)));
                }
                return stats;
            }
        }, false);
    }

    @Test
    public void testDirtyActionsBatchStatWithDigest() throws Exception {
        checkDirtyActions(new BatchStat() {
            @Override
            public List<FileStatusWithDigest> batchStat(boolean useDigest, boolean includeLinks, Iterable<PathFragment> paths) throws IOException {
                List<FileStatusWithDigest> stats = new ArrayList<>();
                for (PathFragment pathFrag : paths) {
                    final Path path = fs.getPath("/").getRelative(pathFrag);
                    stats.add(FilesystemValueCheckerTest.statWithDigest(path, path.statIfFound(NOFOLLOW)));
                }
                return stats;
            }
        }, true);
    }

    @Test
    public void testDirtyActionsBatchStatFallback() throws Exception {
        checkDirtyActions(new BatchStat() {
            @Override
            public List<FileStatusWithDigest> batchStat(boolean useDigest, boolean includeLinks, Iterable<PathFragment> paths) throws IOException {
                throw new IOException("try again");
            }
        }, false);
    }

    @Test
    public void testDirtyTreeArtifactActions() throws Exception {
        checkDirtyTreeArtifactActions(null);
    }

    @Test
    public void testDirtyTreeArtifactActionsBatchStat() throws Exception {
        checkDirtyTreeArtifactActions(new BatchStat() {
            @Override
            public List<FileStatusWithDigest> batchStat(boolean useDigest, boolean includeLinks, Iterable<PathFragment> paths) throws IOException {
                List<FileStatusWithDigest> stats = new ArrayList<>();
                for (PathFragment pathFrag : paths) {
                    stats.add(FileStatusWithDigestAdapter.adapt(fs.getPath("/").getRelative(pathFrag).statIfFound(NOFOLLOW)));
                }
                return stats;
            }
        });
    }

    @Test
    public void testRemoteAndLocalArtifacts() throws Exception {
        // Test that injected remote artifacts are trusted by the FileSystemValueChecker
        // and that local files always takes preference over remote files.
        ActionLookupKey actionLookupKey = new ActionLookupKey() {
            @Override
            public SkyFunctionName functionName() {
                return SkyFunctionName.FOR_TESTING;
            }
        };
        SkyKey actionKey1 = ActionExecutionValue.key(actionLookupKey, 0);
        SkyKey actionKey2 = ActionExecutionValue.key(actionLookupKey, 1);
        Artifact out1 = createDerivedArtifact("foo");
        Artifact out2 = createDerivedArtifact("bar");
        Map<SkyKey, SkyValue> metadataToInject = new HashMap<>();
        metadataToInject.put(actionKey1, actionValueWithRemoteArtifact(out1, createRemoteFileArtifactValue("foo-content")));
        metadataToInject.put(actionKey2, actionValueWithRemoteArtifact(out2, createRemoteFileArtifactValue("bar-content")));
        differencer.inject(metadataToInject);
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(1).setEventHander(INSTANCE).build();
        assertThat(driver.evaluate(ImmutableList.of(actionKey1, actionKey2), evaluationContext).hasError()).isFalse();
        assertThat(/* batchStatter= */
        /* tsgm= */
        /* lastExecutionTimeRange= */
        new FilesystemValueChecker(null, null).getDirtyActionValues(evaluator.getValues(), null, EVERYTHING_MODIFIED)).isEmpty();
        // Create the "out1" artifact on the filesystem and test that it invalidates the generating
        // action's SkyKey.
        FileSystemUtils.writeContentAsLatin1(out1.getPath(), "new-foo-content");
        assertThat(/* batchStatter= */
        /* tsgm= */
        /* lastExecutionTimeRange= */
        new FilesystemValueChecker(null, null).getDirtyActionValues(evaluator.getValues(), null, EVERYTHING_MODIFIED)).containsExactly(actionKey1);
    }

    @Test
    public void testRemoteAndLocalTreeArtifacts() throws Exception {
        // Test that injected remote tree artifacts are trusted by the FileSystemValueChecker
        // and that local files always takes preference over remote files.
        ActionLookupKey actionLookupKey = new ActionLookupKey() {
            @Override
            public SkyFunctionName functionName() {
                return SkyFunctionName.FOR_TESTING;
            }
        };
        SkyKey actionKey = ActionExecutionValue.key(actionLookupKey, 0);
        SpecialArtifact treeArtifact = createTreeArtifact("dir");
        treeArtifact.getPath().createDirectoryAndParents();
        Map<PathFragment, RemoteFileArtifactValue> treeArtifactMetadata = new HashMap<>();
        treeArtifactMetadata.put(PathFragment.create("foo"), createRemoteFileArtifactValue("foo-content"));
        treeArtifactMetadata.put(PathFragment.create("bar"), createRemoteFileArtifactValue("bar-content"));
        Map<SkyKey, SkyValue> metadataToInject = new HashMap<>();
        metadataToInject.put(actionKey, actionValueWithRemoteTreeArtifact(treeArtifact, treeArtifactMetadata));
        differencer.inject(metadataToInject);
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(1).setEventHander(INSTANCE).build();
        assertThat(driver.evaluate(ImmutableList.of(actionKey), evaluationContext).hasError()).isFalse();
        assertThat(/* batchStatter= */
        /* tsgm= */
        /* lastExecutionTimeRange= */
        new FilesystemValueChecker(null, null).getDirtyActionValues(evaluator.getValues(), null, EVERYTHING_MODIFIED)).isEmpty();
        // Create dir/foo on the local disk and test that it invalidates the associated sky key.
        TreeFileArtifact fooArtifact = ActionInputHelper.treeFileArtifact(treeArtifact, "foo");
        FileSystemUtils.writeContentAsLatin1(fooArtifact.getPath(), "new-foo-content");
        assertThat(/* batchStatter= */
        /* tsgm= */
        /* lastExecutionTimeRange= */
        new FilesystemValueChecker(null, null).getDirtyActionValues(evaluator.getValues(), null, EVERYTHING_MODIFIED)).containsExactly(actionKey);
    }

    @Test
    public void testPropagatesRuntimeExceptions() throws Exception {
        Collection<SkyKey> values = ImmutableList.of(FileValue.key(RootedPath.toRootedPath(Root.fromPath(pkgRoot), PathFragment.create("foo"))));
        driver.evaluate(values, FilesystemValueCheckerTest.EVALUATION_OPTIONS);
        FilesystemValueChecker checker = new FilesystemValueChecker(null, null);
        FilesystemValueCheckerTest.assertEmptyDiff(FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker));
        fs.statThrowsRuntimeException = true;
        try {
            FilesystemValueCheckerTest.getDirtyFilesystemKeys(evaluator, checker);
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(e).hasMessage("bork");
        }
    }

    private class MockFileSystem extends InMemoryFileSystem {
        boolean statThrowsRuntimeException;

        boolean readlinkThrowsIoException;

        MockFileSystem() {
            super();
        }

        @Override
        public FileStatus statIfFound(Path path, boolean followSymlinks) throws IOException {
            if (statThrowsRuntimeException) {
                throw new RuntimeException("bork");
            }
            return super.statIfFound(path, followSymlinks);
        }

        @Override
        protected PathFragment readSymbolicLink(Path path) throws IOException {
            if (readlinkThrowsIoException) {
                throw new IOException("readlink failed");
            }
            return super.readSymbolicLink(path);
        }
    }
}

