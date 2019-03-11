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


import EvaluationProgressReceiver.NullEvaluationProgressReceiver;
import OutErr.SYSTEM_OUT_ERR;
import PrecomputedValue.PATH_PACKAGE_LOCATOR;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.actions.ArtifactSkyKey;
import com.google.devtools.build.lib.actions.FileArtifactValue;
import com.google.devtools.build.lib.pkgcache.PathPackageLocator;
import com.google.devtools.build.lib.skyframe.RecursiveFilesystemTraversalFunction.FileOperationException;
import com.google.devtools.build.lib.skyframe.RecursiveFilesystemTraversalValue.ResolvedFile;
import com.google.devtools.build.lib.skyframe.RecursiveFilesystemTraversalValue.TraversalRequest;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TimestampGranularityUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.AbstractSkyKey;
import com.google.devtools.build.skyframe.ErrorInfo;
import com.google.devtools.build.skyframe.EvaluationProgressReceiver;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyFunctionException;
import com.google.devtools.build.skyframe.SkyFunctionException.Transience;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BazelSkyframeExecutorConstants.BUILD_FILES_BY_PRIORITY;
import static ResolvedFileFactory.danglingSymlink;
import static ResolvedFileFactory.regularFile;
import static ResolvedFileFactory.symlinkToDirectory;
import static ResolvedFileFactory.symlinkToFile;


/**
 * Tests for {@link RecursiveFilesystemTraversalFunction}.
 */
@RunWith(JUnit4.class)
public final class RecursiveFilesystemTraversalFunctionTest extends FoundationTestCase {
    private static final Integer EMPTY_METADATA = new Integer(0);

    private RecursiveFilesystemTraversalFunctionTest.RecordingEvaluationProgressReceiver progressReceiver;

    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private RecordingDifferencer differencer;

    private AtomicReference<PathPackageLocator> pkgLocator;

    private RecursiveFilesystemTraversalFunctionTest.NonHermeticArtifactFakeFunction artifactFunction;

    private static final class RecordingEvaluationProgressReceiver extends EvaluationProgressReceiver.NullEvaluationProgressReceiver {
        Set<SkyKey> invalidations;

        Set<SkyKey> evaluations;

        RecordingEvaluationProgressReceiver() {
            clear();
        }

        void clear() {
            invalidations = Sets.newConcurrentHashSet();
            evaluations = Sets.newConcurrentHashSet();
        }

        @Override
        public void invalidated(SkyKey skyKey, InvalidationState state) {
            invalidations.add(skyKey);
        }

        @Override
        public void evaluated(SkyKey skyKey, @Nullable
        SkyValue value, Supplier<EvaluationSuccessState> evaluationSuccessState, EvaluationState state) {
            if (evaluationSuccessState.get().succeeded()) {
                evaluations.add(skyKey);
            }
        }
    }

    @Test
    public void testTraversalOfSourceFile() throws Exception {
        assertTraversalOfFile(sourceArtifact("foo/bar.txt"), false);
    }

    @Test
    public void testTraversalOfGeneratedFile() throws Exception {
        assertTraversalOfFile(derivedArtifact("foo/bar.txt"), false);
    }

    @Test
    public void testTraversalOfGeneratedFileWithStrictOutput() throws Exception {
        assertTraversalOfFile(derivedArtifact("foo/bar.txt"), true);
    }

    @Test
    public void testTraversalOfSymlinkToFile() throws Exception {
        Artifact linkNameArtifact = sourceArtifact("foo/baz/qux.sym");
        Artifact linkTargetArtifact = sourceArtifact("foo/bar/baz.txt");
        PathFragment linkValue = PathFragment.create("../bar/baz.txt");
        TraversalRequest traversalRoot = RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(linkNameArtifact, DONT_CROSS);
        createFile(linkTargetArtifact);
        scratch.dir(linkNameArtifact.getExecPath().getParentDirectory().getPathString());
        rootDirectory.getRelative(linkNameArtifact.getExecPath()).createSymbolicLink(linkValue);
        // Assert that the SkyValue is built and looks right.
        RootedPath symlinkNamePath = RecursiveFilesystemTraversalFunctionTest.rootedPath(linkNameArtifact);
        RootedPath symlinkTargetPath = RecursiveFilesystemTraversalFunctionTest.rootedPath(linkTargetArtifact);
        ResolvedFile expected = symlinkToFile(symlinkTargetPath, symlinkNamePath, linkValue, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        RecursiveFilesystemTraversalValue v1 = traverseAndAssertFiles(traversalRoot, expected);
        assertThat(progressReceiver.invalidations).isEmpty();
        assertThat(progressReceiver.evaluations).contains(traversalRoot);
        progressReceiver.clear();
        // Edit the target of the symlink and verify that the value is rebuilt.
        appendToFile(linkTargetArtifact, "bar");
        RecursiveFilesystemTraversalValue v2 = traverseAndAssertFiles(traversalRoot, expected);
        assertThat(progressReceiver.invalidations).contains(traversalRoot);
        assertThat(progressReceiver.evaluations).contains(traversalRoot);
        assertThat(v2).isNotEqualTo(v1);
        assertTraversalRootHashesAreNotEqual(v1, v2);
    }

    @Test
    public void testTraversalOfTransitiveSymlinkToFile() throws Exception {
        Artifact directLinkArtifact = sourceArtifact("direct/file.sym");
        Artifact transitiveLinkArtifact = sourceArtifact("transitive/sym.sym");
        RootedPath fileA = createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(sourceArtifact("a/file.a")));
        RootedPath directLink = RecursiveFilesystemTraversalFunctionTest.rootedPath(directLinkArtifact);
        RootedPath transitiveLink = RecursiveFilesystemTraversalFunctionTest.rootedPath(transitiveLinkArtifact);
        PathFragment directLinkPath = PathFragment.create("../a/file.a");
        PathFragment transitiveLinkPath = PathFragment.create("../direct/file.sym");
        RecursiveFilesystemTraversalFunctionTest.parentOf(directLink).asPath().createDirectory();
        RecursiveFilesystemTraversalFunctionTest.parentOf(transitiveLink).asPath().createDirectory();
        directLink.asPath().createSymbolicLink(directLinkPath);
        transitiveLink.asPath().createSymbolicLink(transitiveLinkPath);
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(directLinkArtifact, DONT_CROSS), ResolvedFileFactory.symlinkToFile(fileA, directLink, directLinkPath, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(transitiveLinkArtifact, DONT_CROSS), ResolvedFileFactory.symlinkToFile(fileA, transitiveLink, transitiveLinkPath, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testTraversalOfSourceDirectory() throws Exception {
        assertTraversalOfDirectory(sourceArtifact("dir"));
    }

    @Test
    public void testTraversalOfSourceTreeArtifact() throws Exception {
        assertTraversalOfDirectory(treeArtifact("dir"));
    }

    // Note that in actual Bazel derived artifact directories are not checked for modifications on
    // incremental builds, so this test is testing a feature that Bazel does not have. It's included
    // aspirationally.
    @Test
    public void testTraversalOfGeneratedDirectory() throws Exception {
        assertTraversalOfDirectory(derivedArtifact("dir"));
    }

    @Test
    public void testTraversalOfTransitiveSymlinkToDirectory() throws Exception {
        Artifact directLinkArtifact = sourceArtifact("direct/dir.sym");
        Artifact transitiveLinkArtifact = sourceArtifact("transitive/sym.sym");
        RootedPath fileA = createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(sourceArtifact("a/file.a")));
        RootedPath directLink = RecursiveFilesystemTraversalFunctionTest.rootedPath(directLinkArtifact);
        RootedPath transitiveLink = RecursiveFilesystemTraversalFunctionTest.rootedPath(transitiveLinkArtifact);
        PathFragment directLinkPath = PathFragment.create("../a");
        PathFragment transitiveLinkPath = PathFragment.create("../direct/dir.sym");
        RecursiveFilesystemTraversalFunctionTest.parentOf(directLink).asPath().createDirectory();
        RecursiveFilesystemTraversalFunctionTest.parentOf(transitiveLink).asPath().createDirectory();
        directLink.asPath().createSymbolicLink(directLinkPath);
        transitiveLink.asPath().createSymbolicLink(transitiveLinkPath);
        // Expect the file as if was a child of the direct symlink, not of the actual directory.
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(directLinkArtifact, DONT_CROSS), symlinkToDirectory(RecursiveFilesystemTraversalFunctionTest.parentOf(fileA), directLink, directLinkPath, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(directLinkArtifact, "file.a"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
        // Expect the file as if was a child of the transitive symlink, not of the actual directory.
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(transitiveLinkArtifact, DONT_CROSS), symlinkToDirectory(RecursiveFilesystemTraversalFunctionTest.parentOf(fileA), transitiveLink, transitiveLinkPath, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(transitiveLinkArtifact, "file.a"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testTraversePackage() throws Exception {
        Artifact buildFile = sourceArtifact("pkg/BUILD");
        RootedPath buildFilePath = createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(buildFile));
        RootedPath file1 = createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(buildFile, "subdir/file.a"));
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(buildFilePath), DONT_CROSS), regularFile(buildFilePath, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(file1, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testTraversalOfSymlinkToDirectory() throws Exception {
        Artifact linkNameArtifact = sourceArtifact("link/foo.sym");
        Artifact linkTargetArtifact = sourceArtifact("dir");
        RootedPath linkName = RecursiveFilesystemTraversalFunctionTest.rootedPath(linkNameArtifact);
        PathFragment linkValue = PathFragment.create("../dir");
        RootedPath file1 = createFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkTargetArtifact, "file.1"));
        createFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkTargetArtifact, "sub/file.2"));
        scratch.dir(RecursiveFilesystemTraversalFunctionTest.parentOf(linkName).asPath().getPathString());
        linkName.asPath().createSymbolicLink(linkValue);
        // Assert that the SkyValue is built and looks right.
        TraversalRequest traversalRoot = RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(linkNameArtifact, DONT_CROSS);
        ResolvedFile expected1 = ResolvedFileFactory.symlinkToDirectory(RecursiveFilesystemTraversalFunctionTest.rootedPath(linkTargetArtifact), linkName, linkValue, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        ResolvedFile expected2 = ResolvedFileFactory.regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkNameArtifact, "file.1"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        ResolvedFile expected3 = ResolvedFileFactory.regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkNameArtifact, "sub/file.2"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        // We expect to see all the files from the symlink'd directory, under the symlink's path, not
        // under the symlink target's path.
        RecursiveFilesystemTraversalValue v1 = traverseAndAssertFiles(traversalRoot, expected1, expected2, expected3);
        assertThat(progressReceiver.invalidations).isEmpty();
        assertThat(progressReceiver.evaluations).contains(traversalRoot);
        progressReceiver.clear();
        // Add a new file to the directory and see that the value is rebuilt.
        createFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkTargetArtifact, "file.3"));
        invalidateDirectory(linkTargetArtifact);
        ResolvedFile expected4 = ResolvedFileFactory.regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(linkNameArtifact, "file.3"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        RecursiveFilesystemTraversalValue v2 = traverseAndAssertFiles(traversalRoot, expected1, expected2, expected3, expected4);
        assertThat(progressReceiver.invalidations).contains(traversalRoot);
        assertThat(progressReceiver.evaluations).contains(traversalRoot);
        assertThat(v2).isNotEqualTo(v1);
        assertTraversalRootHashesAreNotEqual(v1, v2);
        progressReceiver.clear();
        // Edit a file in the directory and see that the value is rebuilt.
        appendToFile(file1, "bar");
        RecursiveFilesystemTraversalValue v3 = traverseAndAssertFiles(traversalRoot, expected1, expected2, expected3, expected4);
        assertThat(progressReceiver.invalidations).contains(traversalRoot);
        assertThat(progressReceiver.evaluations).contains(traversalRoot);
        assertThat(v3).isNotEqualTo(v2);
        assertTraversalRootHashesAreNotEqual(v2, v3);
        progressReceiver.clear();
        // Add a new file *outside* of the directory and see that the value is *not* rebuilt.
        Artifact someFile = sourceArtifact("somewhere/else/a.file");
        createFile(someFile, "new file");
        appendToFile(someFile, "not all changes are treated equal");
        RecursiveFilesystemTraversalValue v4 = traverseAndAssertFiles(traversalRoot, expected1, expected2, expected3, expected4);
        assertThat(v4).isEqualTo(v3);
        assertTraversalRootHashesAreEqual(v3, v4);
        assertThat(progressReceiver.invalidations).doesNotContain(traversalRoot);
    }

    @Test
    public void testTraversalOfDanglingSymlink() throws Exception {
        Artifact linkArtifact = sourceArtifact("a/dangling.sym");
        RootedPath link = RecursiveFilesystemTraversalFunctionTest.rootedPath(linkArtifact);
        PathFragment linkTarget = PathFragment.create("non_existent");
        RecursiveFilesystemTraversalFunctionTest.parentOf(link).asPath().createDirectory();
        link.asPath().createSymbolicLink(linkTarget);
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(linkArtifact, DONT_CROSS), danglingSymlink(link, linkTarget, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testTraversalOfDanglingSymlinkInADirectory() throws Exception {
        Artifact dirArtifact = sourceArtifact("a");
        RootedPath file = createFile(RecursiveFilesystemTraversalFunctionTest.childOf(dirArtifact, "file.txt"));
        RootedPath link = RecursiveFilesystemTraversalFunctionTest.rootedPath(sourceArtifact("a/dangling.sym"));
        PathFragment linkTarget = PathFragment.create("non_existent");
        RecursiveFilesystemTraversalFunctionTest.parentOf(link).asPath().createDirectory();
        link.asPath().createSymbolicLink(linkTarget);
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(dirArtifact, DONT_CROSS), regularFile(file, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), danglingSymlink(link, linkTarget, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testTraverseSubpackages() throws Exception {
        assertTraverseSubpackages(CROSS);
    }

    @Test
    public void testDoNotTraverseSubpackages() throws Exception {
        assertTraverseSubpackages(DONT_CROSS);
    }

    @Test
    public void testReportErrorWhenTraversingSubpackages() throws Exception {
        assertTraverseSubpackages(REPORT_ERROR);
    }

    @Test
    public void testSwitchPackageRootsWhenUsingMultiplePackagePaths() throws Exception {
        // Layout:
        // pp1://a/BUILD
        // pp1://a/file.a
        // pp1://a/b.sym -> b/   (only created later)
        // pp1://a/b/
        // pp1://a/b/file.fake
        // pp1://a/subdir/file.b
        // 
        // pp2://a/BUILD
        // pp2://a/b/
        // pp2://a/b/BUILD
        // pp2://a/b/file.a
        // pp2://a/subdir.fake/
        // pp2://a/subdir.fake/file.fake
        // 
        // Notice that pp1://a/b will be overlaid by pp2://a/b as the latter has a BUILD file and that
        // takes precedence. On the other hand the package definition pp2://a/BUILD will be ignored
        // since package //a is already defined under pp1.
        // 
        // Notice also that pp1://a/b.sym is a relative symlink pointing to b/. This should be resolved
        // to the definition of //a/b/ under pp1, not under pp2.
        // Set the package paths.
        pkgLocator.set(new PathPackageLocator(outputBase, ImmutableList.of(Root.fromPath(rootDirectory.getRelative("pp1")), Root.fromPath(rootDirectory.getRelative("pp2"))), BUILD_FILES_BY_PRIORITY));
        PATH_PACKAGE_LOCATOR.set(differencer, pkgLocator.get());
        Artifact aBuildArtifact = sourceArtifactUnderPackagePath("a/BUILD", "pp1");
        Artifact bBuildArtifact = sourceArtifactUnderPackagePath("a/b/BUILD", "pp2");
        RootedPath pp1aBuild = createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(aBuildArtifact));
        RootedPath pp1aFileA = createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(pp1aBuild, "file.a"));
        RootedPath pp1bFileFake = createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(pp1aBuild, "b/file.fake"));
        RootedPath pp1aSubdirFileB = createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(pp1aBuild, "subdir/file.b"));
        RootedPath pp2aBuild = createFile(rootedPath("a/BUILD", "pp2"));
        RootedPath pp2bBuild = createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(bBuildArtifact));
        RootedPath pp2bFileA = createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(pp2bBuild, "file.a"));
        createFile(RecursiveFilesystemTraversalFunctionTest.siblingOf(pp2aBuild, "subdir.fake/file.fake"));
        // Traverse //a including subpackages. The result should contain the pp1-definition of //a and
        // the pp2-definition of //a/b.
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(RecursiveFilesystemTraversalFunctionTest.rootedPath(aBuildArtifact)), CROSS), regularFile(pp1aBuild, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aFileA, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aSubdirFileB, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp2bBuild, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp2bFileA, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
        // Traverse //a excluding subpackages. The result should only contain files from //a and not
        // from //a/b.
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(RecursiveFilesystemTraversalFunctionTest.rootedPath(aBuildArtifact)), DONT_CROSS), regularFile(pp1aBuild, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aFileA, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aSubdirFileB, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
        // Create a relative symlink pp1://a/b.sym -> b/. It will be resolved to the subdirectory
        // pp1://a/b, even though a package definition pp2://a/b exists.
        RootedPath pp1aBsym = RecursiveFilesystemTraversalFunctionTest.siblingOf(pp1aFileA, "b.sym");
        pp1aBsym.asPath().createSymbolicLink(PathFragment.create("b"));
        invalidateDirectory(RecursiveFilesystemTraversalFunctionTest.parentOf(pp1aBsym));
        // Traverse //a excluding subpackages. The relative symlink //a/b.sym points to the subdirectory
        // a/b, i.e. the pp1-definition, even though there is a pp2-defined package //a/b and we expect
        // to see b.sym/b.fake (not b/b.fake).
        traverseAndAssertFiles(RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(RecursiveFilesystemTraversalFunctionTest.rootedPath(aBuildArtifact)), DONT_CROSS), regularFile(pp1aBuild, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aFileA, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(RecursiveFilesystemTraversalFunctionTest.childOf(pp1aBsym, "file.fake"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), symlinkToDirectory(RecursiveFilesystemTraversalFunctionTest.parentOf(pp1bFileFake), pp1aBsym, PathFragment.create("b"), RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA), regularFile(pp1aSubdirFileB, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA));
    }

    @Test
    public void testFileDigestChangeCausesRebuild() throws Exception {
        Artifact artifact = sourceArtifact("foo/bar.txt");
        RootedPath path = RecursiveFilesystemTraversalFunctionTest.rootedPath(artifact);
        createFile(path, "hello");
        // Assert that the SkyValue is built and looks right.
        TraversalRequest params = RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(artifact, DONT_CROSS);
        ResolvedFile expected = ResolvedFileFactory.regularFile(path, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        RecursiveFilesystemTraversalValue v1 = traverseAndAssertFiles(params, expected);
        assertThat(progressReceiver.evaluations).contains(params);
        progressReceiver.clear();
        // Change the digest of the file. See that the value is rebuilt.
        appendToFile(path, "world");
        RecursiveFilesystemTraversalValue v2 = traverseAndAssertFiles(params, expected);
        assertThat(progressReceiver.invalidations).contains(params);
        assertThat(v2).isNotEqualTo(v1);
        assertTraversalRootHashesAreNotEqual(v1, v2);
    }

    @Test
    public void testFileMtimeChangeDoesNotCauseRebuildIfDigestIsUnchanged() throws Exception {
        Artifact artifact = sourceArtifact("foo/bar.txt");
        RootedPath path = RecursiveFilesystemTraversalFunctionTest.rootedPath(artifact);
        createFile(path, "hello");
        // Assert that the SkyValue is built and looks right.
        TraversalRequest params = RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(artifact, DONT_CROSS);
        ResolvedFile expected = ResolvedFileFactory.regularFile(path, RecursiveFilesystemTraversalFunctionTest.EMPTY_METADATA);
        RecursiveFilesystemTraversalValue v1 = traverseAndAssertFiles(params, expected);
        assertThat(progressReceiver.evaluations).contains(params);
        progressReceiver.clear();
        // Change the mtime of the file but not the digest. See that the value is *not* rebuilt.
        TimestampGranularityUtils.waitForTimestampGranularity(path.asPath().stat().getLastChangeTime(), SYSTEM_OUT_ERR);
        path.asPath().setLastModifiedTime(System.currentTimeMillis());
        RecursiveFilesystemTraversalValue v2 = traverseAndAssertFiles(params, expected);
        assertThat(v2).isEqualTo(v1);
        assertTraversalRootHashesAreEqual(v1, v2);
    }

    @Test
    public void testGeneratedDirectoryConflictsWithPackage() throws Exception {
        Artifact genDir = derivedArtifact("a/b");
        createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(sourceArtifact("a/b/c/file.real")));
        createFile(RecursiveFilesystemTraversalFunctionTest.rootedPath(derivedArtifact("a/b/c/file.fake")));
        createFile(sourceArtifact("a/b/c/BUILD"));
        SkyKey key = RecursiveFilesystemTraversalFunctionTest.fileLikeRoot(genDir, CROSS);
        EvaluationResult<SkyValue> result = eval(key);
        assertThat(result.hasError()).isTrue();
        ErrorInfo error = result.getError(key);
        assertThat(error.isTransitivelyTransient()).isFalse();
        assertThat(error.getException()).hasMessageThat().contains("Generated directory a/b/c conflicts with package under the same path.");
    }

    @Test
    public void unboundedSymlinkExpansionError() throws Exception {
        Artifact bazLink = sourceArtifact("foo/baz.sym");
        Path parentDir = scratch.dir("foo");
        bazLink.getPath().createSymbolicLink(parentDir);
        SkyKey key = RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(RecursiveFilesystemTraversalFunctionTest.rootedPath(bazLink)), DONT_CROSS);
        EvaluationResult<SkyValue> result = eval(key);
        assertThat(result.hasError()).isTrue();
        ErrorInfo error = result.getError(key);
        assertThat(error.getException()).isInstanceOf(FileOperationException.class);
        assertThat(error.getException()).hasMessageThat().contains("Infinite symlink expansion");
    }

    @Test
    public void symlinkChainError() throws Exception {
        scratch.dir("a");
        Artifact fooLink = sourceArtifact("a/foo.sym");
        Artifact barLink = sourceArtifact("a/bar.sym");
        Artifact bazLink = sourceArtifact("a/baz.sym");
        fooLink.getPath().createSymbolicLink(barLink.getPath());
        barLink.getPath().createSymbolicLink(bazLink.getPath());
        bazLink.getPath().createSymbolicLink(fooLink.getPath());
        SkyKey key = RecursiveFilesystemTraversalFunctionTest.pkgRoot(RecursiveFilesystemTraversalFunctionTest.parentOf(RecursiveFilesystemTraversalFunctionTest.rootedPath(bazLink)), DONT_CROSS);
        EvaluationResult<SkyValue> result = eval(key);
        assertThat(result.hasError()).isTrue();
        ErrorInfo error = result.getError(key);
        assertThat(error.getException()).isInstanceOf(FileOperationException.class);
        assertThat(error.getException()).hasMessageThat().contains("Symlink cycle");
    }

    private static class NonHermeticArtifactFakeFunction implements SkyFunction {
        private final Map<TreeFileArtifact, FileArtifactValue> allTreeFiles = new HashMap<>();

        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            try {
                if (((skyKey.argument()) instanceof Artifact) && (isTreeArtifact())) {
                    return TreeArtifactValue.create(allTreeFiles);
                }
                return FileArtifactValue.createShareable(ArtifactSkyKey.artifact(((SkyKey) (skyKey.argument()))).getPath());
            } catch (IOException e) {
                throw new SkyFunctionException(e, Transience.PERSISTENT) {};
            }
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }

        public void addNewTreeFileArtifact(TreeFileArtifact input) throws IOException {
            allTreeFiles.put(input, FileArtifactValue.createShareable(input.getPath()));
        }
    }

    private static class ArtifactFakeFunction implements SkyFunction {
        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            return env.getValue(new RecursiveFilesystemTraversalFunctionTest.NonHermeticArtifactSkyKey(skyKey));
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }

    private static class NonHermeticArtifactSkyKey extends AbstractSkyKey<SkyKey> {
        private NonHermeticArtifactSkyKey(SkyKey arg) {
            super(arg);
        }

        @Override
        public SkyFunctionName functionName() {
            return RecursiveFilesystemTraversalFunctionTest.NONHERMETIC_ARTIFACT;
        }
    }

    private static final SkyFunctionName NONHERMETIC_ARTIFACT = SkyFunctionName.createNonHermetic("NONHERMETIC_ARTIFACT");
}

