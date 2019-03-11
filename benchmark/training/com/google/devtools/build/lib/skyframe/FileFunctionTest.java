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


import ExternalFileAction.ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS;
import LabelConstants.EXTERNAL_PACKAGE_NAME;
import NullEventHandler.INSTANCE;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.actions.FileValue;
import com.google.devtools.build.lib.actions.FileValue.DifferentRealPathFileValueWithStoredChain;
import com.google.devtools.build.lib.actions.FileValue.DifferentRealPathFileValueWithoutStoredChain;
import com.google.devtools.build.lib.actions.FileValue.SymlinkFileValueWithStoredChain;
import com.google.devtools.build.lib.actions.FileValue.SymlinkFileValueWithoutStoredChain;
import com.google.devtools.build.lib.actions.InconsistentFilesystemException;
import com.google.devtools.build.lib.clock.BlazeClock;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.pkgcache.PathPackageLocator;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.TestUtils;
import com.google.devtools.build.lib.vfs.FileStatus;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import com.google.devtools.build.lib.vfs.util.FileSystems;
import com.google.devtools.build.skyframe.BuildDriver;
import com.google.devtools.build.skyframe.ErrorInfo;
import com.google.devtools.build.skyframe.ErrorInfoSubject;
import com.google.devtools.build.skyframe.EvaluationContext;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BazelSkyframeExecutorConstants.BUILD_FILES_BY_PRIORITY;


/**
 * Tests for {@link FileFunction}.
 */
@RunWith(JUnit4.class)
public class FileFunctionTest {
    private static final EvaluationContext EVALUATION_OPTIONS = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(SkyframeExecutor.DEFAULT_THREAD_COUNT).setEventHander(INSTANCE).build();

    private FileFunctionTest.CustomInMemoryFs fs;

    private Root pkgRoot;

    private Path outputBase;

    private PathPackageLocator pkgLocator;

    private boolean fastDigest;

    private ManualClock manualClock;

    private RecordingDifferencer differencer;

    @Test
    public void testFileValueHashCodeAndEqualsContract() throws Exception {
        Path pathA = file("a", "a");
        Path pathB = file("b", "b");
        Path pathC = symlink("c", "a");
        Path pathD = directory("d");
        Path pathDA = file("d/a", "da");
        Path pathE = symlink("e", "d");
        Path pathF = symlink("f", "a");
        FileValue valueA1 = valueForPathOutsidePkgRoot(pathA);
        FileValue valueA2 = valueForPathOutsidePkgRoot(pathA);
        FileValue valueB1 = valueForPathOutsidePkgRoot(pathB);
        FileValue valueB2 = valueForPathOutsidePkgRoot(pathB);
        FileValue valueC1 = valueForPathOutsidePkgRoot(pathC);
        FileValue valueC2 = valueForPathOutsidePkgRoot(pathC);
        FileValue valueD1 = valueForPathOutsidePkgRoot(pathD);
        FileValue valueD2 = valueForPathOutsidePkgRoot(pathD);
        FileValue valueDA1 = valueForPathOutsidePkgRoot(pathDA);
        FileValue valueDA2 = valueForPathOutsidePkgRoot(pathDA);
        FileValue valueE1 = valueForPathOutsidePkgRoot(pathE);
        FileValue valueE2 = valueForPathOutsidePkgRoot(pathE);
        FileValue valueF1 = valueForPathOutsidePkgRoot(pathF);
        FileValue valueF2 = valueForPathOutsidePkgRoot(pathF);
        // Both 'f' and 'c' are transitively symlinks to 'a', so all of these FileValues ought to be
        // equal.
        new EqualsTester().addEqualityGroup(valueA1, valueA2).addEqualityGroup(valueB1, valueB2).addEqualityGroup(valueC1, valueC2, valueF1, valueF2).addEqualityGroup(valueD1, valueD2).addEqualityGroup(valueDA1, valueDA2).addEqualityGroup(valueE1, valueE2).testEquals();
    }

    @Test
    public void testIsDirectory() throws Exception {
        assertThat(valueForPath(file("a")).isDirectory()).isFalse();
        assertThat(valueForPath(path("nonexistent")).isDirectory()).isFalse();
        assertThat(valueForPath(directory("dir")).isDirectory()).isTrue();
        assertThat(valueForPath(symlink("sa", "a")).isDirectory()).isFalse();
        assertThat(valueForPath(symlink("smissing", "missing")).isDirectory()).isFalse();
        assertThat(valueForPath(symlink("sdir", "dir")).isDirectory()).isTrue();
        assertThat(valueForPath(symlink("ssdir", "sdir")).isDirectory()).isTrue();
    }

    @Test
    public void testIsFile() throws Exception {
        assertThat(valueForPath(file("a")).isFile()).isTrue();
        assertThat(valueForPath(path("nonexistent")).isFile()).isFalse();
        assertThat(valueForPath(directory("dir")).isFile()).isFalse();
        assertThat(valueForPath(symlink("sa", "a")).isFile()).isTrue();
        assertThat(valueForPath(symlink("smissing", "missing")).isFile()).isFalse();
        assertThat(valueForPath(symlink("sdir", "dir")).isFile()).isFalse();
        assertThat(valueForPath(symlink("ssfile", "sa")).isFile()).isTrue();
    }

    @Test
    public void testSimpleIndependentFiles() throws Exception {
        file("a");
        file("b");
        Set<RootedPath> seenFiles = Sets.newHashSet();
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("a", false, "b"));
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("b", false, "a"));
        assertThat(seenFiles).containsExactly(rootedPath("a"), rootedPath("b"), rootedPath(""));
    }

    @Test
    public void testSimpleSymlink() throws Exception {
        symlink("a", "b");
        file("b");
        assertValueChangesIfContentsOfFileChanges("a", false, "b");
        assertValueChangesIfContentsOfFileChanges("b", true, "a");
    }

    @Test
    public void testTransitiveSymlink() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        file("c");
        assertValueChangesIfContentsOfFileChanges("a", false, "b");
        assertValueChangesIfContentsOfFileChanges("a", false, "c");
        assertValueChangesIfContentsOfFileChanges("b", true, "a");
        assertValueChangesIfContentsOfFileChanges("c", true, "b");
        assertValueChangesIfContentsOfFileChanges("c", true, "a");
    }

    @Test
    public void testFileUnderBrokenDirectorySymlink() throws Exception {
        symlink("a", "b/c");
        symlink("b", "d");
        assertValueChangesIfContentsOfDirectoryChanges("b", true, "a/e");
    }

    @Test
    public void testFileUnderDirectorySymlink() throws Exception {
        symlink("a", "b/c");
        symlink("b", "d");
        file("d/c/e");
        assertValueChangesIfContentsOfDirectoryChanges("b", true, "a/e");
    }

    @Test
    public void testSymlinkInDirectory() throws Exception {
        symlink("a/aa", "ab");
        file("a/ab");
        assertValueChangesIfContentsOfFileChanges("a/aa", false, "a/ab");
        assertValueChangesIfContentsOfFileChanges("a/ab", true, "a/aa");
    }

    @Test
    public void testRelativeSymlink() throws Exception {
        symlink("a/aa/aaa", "../ab/aba");
        file("a/ab/aba");
        assertValueChangesIfContentsOfFileChanges("a/ab/aba", true, "a/aa/aaa");
    }

    @Test
    public void testDoubleRelativeSymlink() throws Exception {
        symlink("a/b/c/d", "../../e/f");
        file("a/e/f");
        assertValueChangesIfContentsOfFileChanges("a/e/f", true, "a/b/c/d");
    }

    @Test
    public void testExternalRelativeSymlink() throws Exception {
        symlink("a", "../outside");
        file("b");
        file("../outside");
        Set<RootedPath> seenFiles = Sets.newHashSet();
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("b", false, "a"));
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("../outside", true, "a"));
        assertThat(seenFiles).containsExactly(rootedPath("a"), rootedPath(""), RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/")), RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/outside")));
    }

    @Test
    public void testAbsoluteSymlink() throws Exception {
        symlink("a", "/absolute");
        file("b");
        file("/absolute");
        Set<RootedPath> seenFiles = Sets.newHashSet();
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("b", false, "a"));
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("/absolute", true, "a"));
        assertThat(seenFiles).containsExactly(rootedPath("a"), rootedPath(""), RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/")), RootedPath.toRootedPath(Root.absoluteRoot(fs), PathFragment.create("/absolute")));
    }

    @Test
    public void testAbsoluteSymlinkToExternal() throws Exception {
        String externalPath = outputBase.getRelative(EXTERNAL_PACKAGE_NAME).getRelative("a/b").getPathString();
        symlink("a", externalPath);
        file("b");
        file(externalPath);
        Set<RootedPath> seenFiles = Sets.newHashSet();
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("b", false, "a"));
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges(externalPath, true, "a"));
        Root root = Root.absoluteRoot(fs);
        assertThat(seenFiles).containsExactly(rootedPath("WORKSPACE"), rootedPath("a"), rootedPath(""), RootedPath.toRootedPath(root, PathFragment.create("/")), RootedPath.toRootedPath(root, PathFragment.create("/output_base")), RootedPath.toRootedPath(root, PathFragment.create("/output_base/external")), RootedPath.toRootedPath(root, PathFragment.create("/output_base/external/a")), RootedPath.toRootedPath(root, PathFragment.create("/output_base/external/a/b")));
    }

    @Test
    public void testSymlinkAsAncestor() throws Exception {
        file("a/b/c/d");
        symlink("f", "a/b/c");
        assertValueChangesIfContentsOfFileChanges("a/b/c/d", true, "f/d");
    }

    @Test
    public void testSymlinkAsAncestorNested() throws Exception {
        file("a/b/c/d");
        symlink("f", "a/b");
        assertValueChangesIfContentsOfFileChanges("a/b/c/d", true, "f/c/d");
    }

    @Test
    public void testTwoSymlinksInAncestors() throws Exception {
        file("a/aa/aaa/aaaa");
        symlink("b/ba/baa", "../../a/aa");
        symlink("c/ca", "../b/ba");
        assertValueChangesIfContentsOfFileChanges("c/ca", true, "c/ca/baa/aaa/aaaa");
        assertValueChangesIfContentsOfFileChanges("b/ba/baa", true, "c/ca/baa/aaa/aaaa");
        assertValueChangesIfContentsOfFileChanges("a/aa/aaa/aaaa", true, "c/ca/baa/aaa/aaaa");
    }

    @Test
    public void testSelfReferencingSymlink() throws Exception {
        symlink("a", "a");
        assertError("a");
    }

    @Test
    public void testMutuallyReferencingSymlinks() throws Exception {
        symlink("a", "b");
        symlink("b", "a");
        assertError("a");
    }

    @Test
    public void testRecursiveNestingSymlink() throws Exception {
        symlink("a/a", "../a");
        assertError("a/a/b");
    }

    @Test
    public void testBrokenSymlink() throws Exception {
        symlink("a", "b");
        Set<RootedPath> seenFiles = Sets.newHashSet();
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("b", true, "a"));
        seenFiles.addAll(getFilesSeenAndAssertValueChangesIfContentsOfFileChanges("a", false, "b"));
        assertThat(seenFiles).containsExactly(rootedPath("a"), rootedPath("b"), rootedPath(""));
    }

    @Test
    public void testBrokenDirectorySymlink() throws Exception {
        symlink("a", "b");
        file("c");
        assertValueChangesIfContentsOfDirectoryChanges("a", true, "a/aa");
        // This just creates the directory "b", which doesn't change the value for "a/aa", since "a/aa"
        // still has real path "b/aa" and still doesn't exist.
        assertValueChangesIfContentsOfDirectoryChanges("b", false, "a/aa");
        assertValueChangesIfContentsOfFileChanges("c", false, "a/aa");
    }

    @Test
    public void testTraverseIntoVirtualNonDirectory() throws Exception {
        file("dir/a");
        symlink("vdir", "dir");
        // The following evaluation should not throw IOExceptions.
        assertNoError("vdir/a/aa/aaa");
    }

    @Test
    public void testFileCreation() throws Exception {
        FileValue a = valueForPath(path("file"));
        Path p = file("file");
        FileValue b = valueForPath(p);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    public void testEmptyFile() throws Exception {
        final byte[] digest = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        createFsAndRoot(new FileFunctionTest.CustomInMemoryFs(manualClock) {
            @Override
            protected byte[] getFastDigest(Path path) throws IOException {
                return digest;
            }
        });
        Path p = file("file");
        p.setLastModifiedTime(0L);
        FileValue a = valueForPath(p);
        p.setLastModifiedTime(1L);
        assertThat(valueForPath(p)).isEqualTo(a);
        FileSystemUtils.writeContentAsLatin1(p, "content");
        // Same digest, but now non-empty.
        assertThat(valueForPath(p)).isNotEqualTo(a);
    }

    @Test
    public void testUnreadableFileWithNoFastDigest() throws Exception {
        Path p = file("unreadable");
        p.chmod(0);
        p.setLastModifiedTime(0L);
        FileValue value = valueForPath(p);
        assertThat(value.exists()).isTrue();
        assertThat(value.getDigest()).isNull();
        p.setLastModifiedTime(10L);
        assertThat(valueForPath(p)).isEqualTo(value);
        p.setLastModifiedTime(0L);
        assertThat(valueForPath(p)).isEqualTo(value);
    }

    @Test
    public void testUnreadableFileWithFastDigest() throws Exception {
        final byte[] expectedDigest = MessageDigest.getInstance("md5").digest("blah".getBytes(StandardCharsets.UTF_8));
        createFsAndRoot(new FileFunctionTest.CustomInMemoryFs(manualClock) {
            @Override
            protected byte[] getFastDigest(Path path) {
                return path.getBaseName().equals("unreadable") ? expectedDigest : null;
            }
        });
        Path p = file("unreadable");
        p.chmod(0);
        FileValue value = valueForPath(p);
        assertThat(value.exists()).isTrue();
        assertThat(value.getDigest()).isNotNull();
    }

    @Test
    public void testFileModificationDigest() throws Exception {
        fastDigest = true;
        Path p = file("file");
        FileValue a = valueForPath(p);
        FileSystemUtils.writeContentAsLatin1(p, "goop");
        FileValue b = valueForPath(p);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    public void testModTimeVsDigest() throws Exception {
        Path p = file("somefile", "fizzley");
        fastDigest = true;
        FileValue aMd5 = valueForPath(p);
        fastDigest = false;
        FileValue aModTime = valueForPath(p);
        assertThat(aModTime).isNotEqualTo(aMd5);
        new EqualsTester().addEqualityGroup(aMd5).addEqualityGroup(aModTime).testEquals();
    }

    @Test
    public void testFileDeletion() throws Exception {
        Path p = file("file");
        FileValue a = valueForPath(p);
        p.delete();
        FileValue b = valueForPath(p);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    public void testFileTypeChange() throws Exception {
        Path p = file("file");
        FileValue a = valueForPath(p);
        p.delete();
        p = symlink("file", "foo");
        FileValue b = valueForPath(p);
        p.delete();
        FileSystemUtils.createDirectoryAndParents(pkgRoot.getRelative("file"));
        FileValue c = valueForPath(p);
        assertThat(a.equals(b)).isFalse();
        assertThat(b.equals(c)).isFalse();
        assertThat(a.equals(c)).isFalse();
    }

    @Test
    public void testSymlinkTargetChange() throws Exception {
        Path p = symlink("symlink", "foo");
        FileValue a = valueForPath(p);
        p.delete();
        p = symlink("symlink", "bar");
        FileValue b = valueForPath(p);
        assertThat(b).isNotEqualTo(a);
    }

    @Test
    public void testSymlinkTargetContentsChangeCTime() throws Exception {
        fastDigest = false;
        Path fooPath = file("foo");
        FileSystemUtils.writeContentAsLatin1(fooPath, "foo");
        Path p = symlink("symlink", "foo");
        FileValue a = valueForPath(p);
        manualClock.advanceMillis(1);
        fooPath.chmod(365);
        manualClock.advanceMillis(1);
        FileValue b = valueForPath(p);
        assertThat(b).isNotEqualTo(a);
    }

    @Test
    public void testSymlinkTargetContentsChangeDigest() throws Exception {
        fastDigest = true;
        Path fooPath = file("foo");
        FileSystemUtils.writeContentAsLatin1(fooPath, "foo");
        Path p = symlink("symlink", "foo");
        FileValue a = valueForPath(p);
        FileSystemUtils.writeContentAsLatin1(fooPath, "bar");
        FileValue b = valueForPath(p);
        assertThat(b).isNotEqualTo(a);
    }

    @Test
    public void testRealPath() throws Exception {
        file("file");
        directory("directory");
        file("directory/file");
        symlink("directory/link", "file");
        symlink("directory/doublelink", "link");
        symlink("directory/parentlink", "../file");
        symlink("directory/doubleparentlink", "../link");
        symlink("link", "file");
        symlink("deadlink", "missing_file");
        symlink("dirlink", "directory");
        symlink("doublelink", "link");
        symlink("doubledirlink", "dirlink");
        checkRealPath("file");
        checkRealPath("link");
        checkRealPath("doublelink");
        for (String dir : new String[]{ "directory", "dirlink", "doubledirlink" }) {
            checkRealPath(dir);
            checkRealPath((dir + "/file"));
            checkRealPath((dir + "/link"));
            checkRealPath((dir + "/doublelink"));
            checkRealPath((dir + "/parentlink"));
        }
        assertRealPath("missing", "missing");
        assertRealPath("deadlink", "missing_file");
    }

    @Test
    public void testRealPathRelativeSymlink() throws Exception {
        directory("dir");
        symlink("dir/link", "../dir2");
        directory("dir2");
        symlink("dir2/filelink", "../dest");
        file("dest");
        checkRealPath("dir/link/filelink");
    }

    @Test
    public void testSymlinkAcrossPackageRoots() throws Exception {
        Path otherPkgRoot = getPath("/other_root");
        pkgLocator = new PathPackageLocator(outputBase, ImmutableList.of(pkgRoot, Root.fromPath(otherPkgRoot)), BUILD_FILES_BY_PRIORITY);
        symlink("a", "/other_root/b");
        assertValueChangesIfContentsOfFileChanges("/other_root/b", true, "a");
    }

    @Test
    public void testFilesOutsideRootIsReEvaluated() throws Exception {
        Path file = file("/outsideroot");
        SequentialBuildDriver driver = makeDriver();
        SkyKey key = skyKey("/outsideroot");
        EvaluationResult<SkyValue> result;
        result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        if (result.hasError()) {
            Assert.fail(String.format("Evaluation error for %s: %s", key, result.getError()));
        }
        FileValue oldValue = ((FileValue) (result.get(key)));
        assertThat(oldValue.exists()).isTrue();
        file.delete();
        differencer.invalidate(ImmutableList.of(fileStateSkyKey("/outsideroot")));
        result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        if (result.hasError()) {
            Assert.fail(String.format("Evaluation error for %s: %s", key, result.getError()));
        }
        FileValue newValue = ((FileValue) (result.get(key)));
        assertThat(newValue).isNotSameAs(oldValue);
        assertThat(newValue.exists()).isFalse();
    }

    @Test
    public void testFilesOutsideRootWhenExternalAssumedNonExistentAndImmutable() throws Exception {
        file("/outsideroot");
        SequentialBuildDriver driver = makeDriver(ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS);
        SkyKey key = skyKey("/outsideroot");
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        FileValue value = ((FileValue) (result.get(key)));
        assertThat(value).isNotNull();
        assertThat(value.exists()).isFalse();
    }

    @Test
    public void testAbsoluteSymlinksToFilesOutsideRootWhenExternalAssumedNonExistentAndImmutable() throws Exception {
        file("/outsideroot");
        symlink("a", "/outsideroot");
        SequentialBuildDriver driver = makeDriver(ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS);
        SkyKey key = skyKey("a");
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        FileValue value = ((FileValue) (result.get(key)));
        assertThat(value).isNotNull();
        assertThat(value.exists()).isFalse();
    }

    @Test
    public void testAbsoluteSymlinksReferredByInternalFilesToFilesOutsideRootWhenExternalAssumedNonExistentAndImmutable() throws Exception {
        file("/outsideroot/src/foo/bar");
        symlink("/root/src", "/outsideroot/src");
        SequentialBuildDriver driver = makeDriver(ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS);
        SkyKey key = skyKey("/root/src/foo/bar");
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        FileValue value = ((FileValue) (result.get(key)));
        assertThat(value).isNotNull();
        assertThat(value.exists()).isFalse();
    }

    @Test
    public void testRelativeSymlinksToFilesOutsideRootWhenExternalAssumedNonExistentAndImmutable() throws Exception {
        file("../outsideroot");
        symlink("a", "../outsideroot");
        SequentialBuildDriver driver = makeDriver(ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS);
        SkyKey key = skyKey("a");
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        FileValue value = ((FileValue) (result.get(key)));
        assertThat(value).isNotNull();
        assertThat(value.exists()).isFalse();
    }

    @Test
    public void testAbsoluteSymlinksBackIntoSourcesOkWhenExternalDisallowed() throws Exception {
        Path file = file("insideroot");
        symlink("a", file.getPathString());
        SequentialBuildDriver driver = makeDriver(ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS);
        SkyKey key = skyKey("a");
        EvaluationResult<SkyValue> result = driver.evaluate(ImmutableList.of(key), FileFunctionTest.EVALUATION_OPTIONS);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        FileValue value = ((FileValue) (result.get(key)));
        assertThat(value).isNotNull();
        assertThat(value.exists()).isTrue();
        assertThat(value.realRootedPath().getRootRelativePath().getPathString()).isEqualTo("insideroot");
    }

    @Test
    public void testSize() throws Exception {
        Path file = file("file");
        int fileSize = 20;
        FileSystemUtils.writeContentAsLatin1(file, Strings.repeat("a", fileSize));
        assertThat(valueForPath(file).getSize()).isEqualTo(fileSize);
        Path dir = directory("directory");
        file(dir.getChild("child").getPathString());
        try {
            valueForPath(dir).getSize();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        Path nonexistent = getPath("/root/noexist");
        try {
            valueForPath(nonexistent).getSize();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        Path symlink = symlink("link", "/root/file");
        // Symlink stores size of target, not link.
        assertThat(valueForPath(symlink).getSize()).isEqualTo(fileSize);
        assertThat(symlink.delete()).isTrue();
        symlink = symlink("link", "/root/directory");
        try {
            valueForPath(symlink).getSize();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        assertThat(symlink.delete()).isTrue();
        symlink = symlink("link", "/root/noexist");
        try {
            valueForPath(symlink).getSize();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void testDigest() throws Exception {
        final AtomicInteger digestCalls = new AtomicInteger(0);
        int expectedCalls = 0;
        fs = new FileFunctionTest.CustomInMemoryFs(manualClock) {
            @Override
            protected byte[] getDigest(Path path) throws IOException {
                digestCalls.incrementAndGet();
                return super.getDigest(path);
            }
        };
        pkgRoot = Root.fromPath(fs.getPath("/root"));
        Path file = file("file");
        FileSystemUtils.writeContentAsLatin1(file, Strings.repeat("a", 20));
        byte[] digest = file.getDigest();
        expectedCalls++;
        assertThat(digestCalls.get()).isEqualTo(expectedCalls);
        FileValue value = valueForPath(file);
        expectedCalls++;
        assertThat(digestCalls.get()).isEqualTo(expectedCalls);
        assertThat(value.getDigest()).isEqualTo(digest);
        // Digest is cached -- no filesystem access.
        assertThat(digestCalls.get()).isEqualTo(expectedCalls);
        fastDigest = false;
        digestCalls.set(0);
        value = valueForPath(file);
        // No new digest calls.
        assertThat(digestCalls.get()).isEqualTo(0);
        assertThat(value.getDigest()).isNull();
        assertThat(digestCalls.get()).isEqualTo(0);
        fastDigest = true;
        Path dir = directory("directory");
        try {
            assertThat(valueForPath(dir).getDigest()).isNull();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        assertThat(digestCalls.get()).isEqualTo(0);// No digest calls made for directory.

        Path nonexistent = getPath("/root/noexist");
        try {
            assertThat(valueForPath(nonexistent).getDigest()).isNull();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        assertThat(digestCalls.get()).isEqualTo(0);// No digest calls made for nonexistent file.

        Path symlink = symlink("link", "/root/file");
        value = valueForPath(symlink);
        assertThat(digestCalls.get()).isEqualTo(1);
        // Symlink stores digest of target, not link.
        assertThat(value.getDigest()).isEqualTo(digest);
        assertThat(digestCalls.get()).isEqualTo(1);
        digestCalls.set(0);
        assertThat(symlink.delete()).isTrue();
        symlink = symlink("link", "/root/directory");
        // Symlink stores digest of target, not link, for directories too.
        try {
            assertThat(valueForPath(symlink).getDigest()).isNull();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        assertThat(digestCalls.get()).isEqualTo(0);
    }

    @Test
    public void testDoesntStatChildIfParentDoesntExist() throws Exception {
        // Our custom filesystem says "a" does not exist, so FileFunction shouldn't bother trying to
        // think about "a/b". Test for this by having a stat of "a/b" fail with an io error, and
        // observing that we don't encounter the error.
        fs.stubStat(path("a"), null);
        fs.stubStatError(path("a/b"), new IOException("ouch!"));
        assertThat(valueForPath(path("a/b")).exists()).isFalse();
    }

    @Test
    public void testFilesystemInconsistencies_GetFastDigest() throws Exception {
        file("a");
        // Our custom filesystem says "a/b" exists but "a" does not exist.
        fs.stubFastDigestError(path("a"), new IOException("nope"));
        SequentialBuildDriver driver = makeDriver();
        SkyKey skyKey = skyKey("a");
        EvaluationResult<FileValue> result = driver.evaluate(ImmutableList.of(skyKey), FileFunctionTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).isInstanceOf(InconsistentFilesystemException.class);
        assertThat(errorInfo.getException()).hasMessageThat().contains("encountered error 'nope'");
        assertThat(errorInfo.getException()).hasMessageThat().contains("/root/a is no longer a file");
    }

    @Test
    public void testFilesystemInconsistencies_GetFastDigestAndIsReadableFailure() throws Exception {
        createFsAndRoot(new FileFunctionTest.CustomInMemoryFs(manualClock) {
            @Override
            protected boolean isReadable(Path path) throws IOException {
                if (path.getBaseName().equals("unreadable")) {
                    throw new IOException("isReadable failed");
                }
                return super.isReadable(path);
            }
        });
        Path p = file("unreadable");
        p.chmod(0);
        SequentialBuildDriver driver = makeDriver();
        SkyKey skyKey = skyKey("unreadable");
        EvaluationResult<FileValue> result = driver.evaluate(ImmutableList.of(skyKey), FileFunctionTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).isInstanceOf(InconsistentFilesystemException.class);
        assertThat(errorInfo.getException()).hasMessageThat().contains("encountered error 'isReadable failed'");
        assertThat(errorInfo.getException()).hasMessageThat().contains("/root/unreadable is no longer a file");
    }

    @Test
    public void testSymlinkCycle_AncestorCycle_StartInCycle() throws Exception {
        /* ancestorCycle= */
        /* startInCycle= */
        runTestSymlinkCycle(true, true);
    }

    @Test
    public void testSymlinkCycle_AncestorCycle_StartOutOfCycle() throws Exception {
        /* ancestorCycle= */
        /* startInCycle= */
        runTestSymlinkCycle(true, false);
    }

    @Test
    public void testSymlinkCycle_RegularCycle_StartInCycle() throws Exception {
        /* ancestorCycle= */
        /* startInCycle= */
        runTestSymlinkCycle(false, true);
    }

    @Test
    public void testSymlinkCycle_RegularCycle_StartOutOfCycle() throws Exception {
        /* ancestorCycle= */
        /* startInCycle= */
        runTestSymlinkCycle(false, false);
    }

    @Test
    public void testSerialization() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        FileSystem oldFileSystem = Path.getFileSystemForSerialization();
        try {
            // InMemoryFS is not supported for serialization.
            FileSystem fs = FileSystems.getJavaIoFileSystem();
            Path.setFileSystemForSerialization(fs);
            pkgRoot = Root.absoluteRoot(fs);
            FileValue a = valueForPath(fs.getPath("/"));
            Path tmp = fs.getPath(((TestUtils.tmpDirFile().getAbsoluteFile()) + "/file.txt"));
            FileSystemUtils.writeContentAsLatin1(tmp, "test contents");
            FileValue b = valueForPath(tmp);
            Preconditions.checkState(b.isFile());
            FileValue c = valueForPath(fs.getPath("/does/not/exist"));
            oos.writeObject(a);
            oos.writeObject(b);
            oos.writeObject(c);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            FileValue a2 = ((FileValue) (ois.readObject()));
            FileValue b2 = ((FileValue) (ois.readObject()));
            FileValue c2 = ((FileValue) (ois.readObject()));
            assertThat(a2).isEqualTo(a);
            assertThat(b2).isEqualTo(b);
            assertThat(c2).isEqualTo(c);
            assertThat(a2.equals(b2)).isFalse();
        } finally {
            Path.setFileSystemForSerialization(oldFileSystem);
        }
    }

    @Test
    public void testFileStateEquality() throws Exception {
        file("a");
        symlink("b1", "a");
        symlink("b2", "a");
        symlink("b3", "zzz");
        directory("d1");
        directory("d2");
        SkyKey file = fileStateSkyKey("a");
        SkyKey symlink1 = fileStateSkyKey("b1");
        SkyKey symlink2 = fileStateSkyKey("b2");
        SkyKey symlink3 = fileStateSkyKey("b3");
        SkyKey missing1 = fileStateSkyKey("c1");
        SkyKey missing2 = fileStateSkyKey("c2");
        SkyKey directory1 = fileStateSkyKey("d1");
        SkyKey directory2 = fileStateSkyKey("d2");
        ImmutableList<SkyKey> keys = ImmutableList.of(file, symlink1, symlink2, symlink3, missing1, missing2, directory1, directory2);
        SequentialBuildDriver driver = makeDriver();
        EvaluationResult<SkyValue> result = driver.evaluate(keys, FileFunctionTest.EVALUATION_OPTIONS);
        new EqualsTester().addEqualityGroup(result.get(file)).addEqualityGroup(result.get(symlink1), result.get(symlink2)).addEqualityGroup(result.get(symlink3)).addEqualityGroup(result.get(missing1), result.get(missing2)).addEqualityGroup(result.get(directory1), result.get(directory2)).testEquals();
    }

    @Test
    public void testSymlinkToPackagePathBoundary() throws Exception {
        Path path = path("this/is/a/path");
        FileSystemUtils.ensureSymbolicLink(path, pkgRoot.asPath());
        assertError("this/is/a/path");
    }

    @Test
    public void testInfiniteSymlinkExpansion_AbsoluteSymlinkToDescendant() throws Exception {
        /* symlinkToAncestor= */
        /* absoluteSymlink= */
        runTestSimpleInfiniteSymlinkExpansion(false, true);
    }

    @Test
    public void testInfiniteSymlinkExpansion_RelativeSymlinkToDescendant() throws Exception {
        /* symlinkToAncestor= */
        /* absoluteSymlink= */
        runTestSimpleInfiniteSymlinkExpansion(false, false);
    }

    @Test
    public void testInfiniteSymlinkExpansion_AbsoluteSymlinkToAncestor() throws Exception {
        /* symlinkToAncestor= */
        /* absoluteSymlink= */
        runTestSimpleInfiniteSymlinkExpansion(true, true);
    }

    @Test
    public void testInfiniteSymlinkExpansion_RelativeSymlinkToAncestor() throws Exception {
        /* symlinkToAncestor= */
        /* absoluteSymlink= */
        runTestSimpleInfiniteSymlinkExpansion(true, false);
    }

    @Test
    public void testInfiniteSymlinkExpansion_SymlinkToReferrerToAncestor() throws Exception {
        symlink("d", "a");
        Path abPath = directory("a/b");
        Path abcPath = abPath.getChild("c");
        symlink("a/b/c", "../../d/b");
        RootedPath rootedPathABC = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(abcPath));
        RootedPath rootedPathAB = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(abPath));
        RootedPath rootedPathDB = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(path("d/b")));
        SkyKey keyABC = FileValue.key(rootedPathABC);
        StoredEventHandler eventHandler = new StoredEventHandler();
        SequentialBuildDriver driver = makeDriver();
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(true).setNumThreads(SkyframeExecutor.DEFAULT_THREAD_COUNT).setEventHander(eventHandler).build();
        EvaluationResult<FileValue> result = driver.evaluate(ImmutableList.of(keyABC), evaluationContext);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(keyABC).isNotTransient();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(keyABC).hasExceptionThat().isInstanceOf(FileSymlinkInfiniteExpansionException.class);
        FileSymlinkInfiniteExpansionException fiee = ((FileSymlinkInfiniteExpansionException) (result.getError(keyABC).getException()));
        assertThat(fiee).hasMessageThat().contains("Infinite symlink expansion");
        assertThat(fiee.getPathToChain()).isEmpty();
        assertThat(fiee.getChain()).containsExactly(rootedPathABC, rootedPathDB, rootedPathAB).inOrder();
        assertThat(eventHandler.getEvents()).hasSize(1);
        assertThat(Iterables.getOnlyElement(eventHandler.getEvents()).getMessage()).contains("infinite symlink expansion detected");
    }

    @Test
    public void testInfiniteSymlinkExpansion_SymlinkToReferrerToAncestor_LevelsOfDirectorySymlinks() throws Exception {
        symlink("dir1/a", "../dir2");
        symlink("dir2/b", "../dir1");
        RootedPath rootedPathDir1AB = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(path("dir1/a/b")));
        RootedPath rootedPathDir2B = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(path("dir2/b")));
        RootedPath rootedPathDir1 = RootedPath.toRootedPath(pkgRoot, pkgRoot.relativize(path("dir1")));
        SkyKey keyDir1AB = FileValue.key(rootedPathDir1AB);
        StoredEventHandler eventHandler = new StoredEventHandler();
        SequentialBuildDriver driver = makeDriver();
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(true).setNumThreads(SkyframeExecutor.DEFAULT_THREAD_COUNT).setEventHander(eventHandler).build();
        EvaluationResult<FileValue> result = driver.evaluate(ImmutableList.of(keyDir1AB), evaluationContext);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(keyDir1AB).isNotTransient();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(keyDir1AB).hasExceptionThat().isInstanceOf(FileSymlinkInfiniteExpansionException.class);
        FileSymlinkInfiniteExpansionException fiee = ((FileSymlinkInfiniteExpansionException) (result.getError(keyDir1AB).getException()));
        assertThat(fiee).hasMessageThat().contains("Infinite symlink expansion");
        assertThat(fiee.getPathToChain()).isEmpty();
        assertThat(fiee.getChain()).containsExactly(rootedPathDir1AB, rootedPathDir2B, rootedPathDir1).inOrder();
        assertThat(eventHandler.getEvents()).hasSize(1);
        assertThat(Iterables.getOnlyElement(eventHandler.getEvents()).getMessage()).contains("infinite symlink expansion detected");
    }

    @Test
    public void testChildOfNonexistentParent() throws Exception {
        Path ancestor = directory("this/is/an/ancestor");
        Path parent = ancestor.getChild("parent");
        Path child = parent.getChild("child");
        assertThat(valueForPath(parent).exists()).isFalse();
        assertThat(valueForPath(child).exists()).isFalse();
    }

    @Test
    public void testInjectionOverIOException() throws Exception {
        Path foo = file("foo");
        SkyKey fooKey = skyKey("foo");
        fs.stubStatError(foo, new IOException("bork"));
        BuildDriver driver = makeDriver();
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(true).setNumThreads(1).setEventHander(INSTANCE).build();
        EvaluationResult<FileValue> result = driver.evaluate(ImmutableList.of(fooKey), evaluationContext);
        ErrorInfoSubject errorInfoSubject = EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(fooKey);
        errorInfoSubject.isTransient();
        errorInfoSubject.hasExceptionThat().hasMessageThat().isEqualTo("bork");
        fs.stubbedStatErrors.remove(foo);
        differencer.inject(fileStateSkyKey("foo"), FileStateValue.create(RootedPath.toRootedPath(pkgRoot, foo), new com.google.devtools.build.lib.util.io.TimestampGranularityMonitor(BlazeClock.instance())));
        result = driver.evaluate(ImmutableList.of(fooKey), evaluationContext);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        assertThat(result.get(fooKey).exists()).isTrue();
    }

    @Test
    public void testMultipleLevelsOfDirectorySymlinks_Clean() throws Exception {
        symlink("a/b/c", "../c");
        Path abcd = path("a/b/c/d");
        symlink("a/c/d", "../d");
        assertThat(valueForPath(abcd).isSymlink()).isTrue();
    }

    @Test
    public void testMultipleLevelsOfDirectorySymlinks_Incremental() throws Exception {
        SequentialBuildDriver driver = makeDriver();
        symlink("a/b/c", "../c");
        Path acd = directory("a/c/d");
        Path abcd = path("a/b/c/d");
        FileValue abcdFileValue = valueForPathHelper(pkgRoot, abcd, driver);
        assertThat(abcdFileValue.isDirectory()).isTrue();
        assertThat(abcdFileValue.isSymlink()).isFalse();
        acd.delete();
        symlink("a/c/d", "../d");
        differencer.invalidate(ImmutableList.of(fileStateSkyKey("a/c/d")));
        abcdFileValue = valueForPathHelper(pkgRoot, abcd, driver);
        assertThat(abcdFileValue.isSymlink()).isTrue();
    }

    @Test
    public void testLogicalChainDuringResolution_Directory_SimpleSymlink() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        directory("c");
        FileValue fileValue = valueForPath(path("a"));
        assertThat(fileValue).isInstanceOf(SymlinkFileValueWithStoredChain.class);
        assertThat(fileValue.getUnresolvedLinkTarget()).isEqualTo(PathFragment.create("b"));
        assertThat(fileValue.logicalChainDuringResolution()).isEqualTo(ImmutableList.of(rootedPath("a"), rootedPath("b"), rootedPath("c")));
    }

    @Test
    public void testLogicalChainDuringResolution_Directory_SimpleAncestorSymlink() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        directory("c/d");
        FileValue fileValue = valueForPath(path("a/d"));
        assertThat(fileValue).isInstanceOf(DifferentRealPathFileValueWithStoredChain.class);
        assertThat(fileValue.realRootedPath()).isEqualTo(rootedPath("c/d"));
        assertThat(fileValue.logicalChainDuringResolution()).containsExactly(rootedPath("a/d"), rootedPath("b/d"), rootedPath("c/d")).inOrder();
    }

    @Test
    public void testLogicalChainDuringResolution_File_SimpleSymlink() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        file("c");
        FileValue fileValue = valueForPath(path("a"));
        assertThat(fileValue).isInstanceOf(SymlinkFileValueWithoutStoredChain.class);
        assertThat(fileValue.getUnresolvedLinkTarget()).isEqualTo(PathFragment.create("b"));
        MoreAsserts.assertThrows(IllegalStateException.class, () -> fileValue.logicalChainDuringResolution());
    }

    @Test
    public void testLogicalChainDuringResolution_File_SimpleAncestorSymlink() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        file("c/d");
        FileValue fileValue = valueForPath(path("a/d"));
        assertThat(fileValue).isInstanceOf(DifferentRealPathFileValueWithoutStoredChain.class);
        assertThat(fileValue.realRootedPath()).isEqualTo(rootedPath("c/d"));
        MoreAsserts.assertThrows(IllegalStateException.class, () -> fileValue.logicalChainDuringResolution());
    }

    @Test
    public void testLogicalChainDuringResolution_Complicated() throws Exception {
        symlink("a", "b");
        symlink("b", "c");
        directory("c");
        symlink("c/d", "../e/f");
        symlink("e", "g");
        directory("g");
        symlink("g/f", "../h");
        directory("h");
        FileValue fileValue = valueForPath(path("a/d"));
        assertThat(fileValue).isInstanceOf(DifferentRealPathFileValueWithStoredChain.class);
        assertThat(fileValue.realRootedPath()).isEqualTo(rootedPath("h"));
        assertThat(fileValue.logicalChainDuringResolution()).containsExactly(rootedPath("a/d"), rootedPath("b/d"), rootedPath("c/d"), rootedPath("e/f"), rootedPath("g/f"), rootedPath("h")).inOrder();
    }

    private class CustomInMemoryFs extends InMemoryFileSystem {
        private final Map<Path, FileStatus> stubbedStats = Maps.newHashMap();

        private final Map<Path, IOException> stubbedStatErrors = Maps.newHashMap();

        private final Map<Path, IOException> stubbedFastDigestErrors = Maps.newHashMap();

        public CustomInMemoryFs(ManualClock manualClock) {
            super(manualClock);
        }

        public void stubFastDigestError(Path path, IOException error) {
            stubbedFastDigestErrors.put(path, error);
        }

        @Override
        protected byte[] getFastDigest(Path path) throws IOException {
            if (stubbedFastDigestErrors.containsKey(path)) {
                throw stubbedFastDigestErrors.get(path);
            }
            return fastDigest ? getDigest(path) : null;
        }

        public void stubStat(Path path, @Nullable
        FileStatus stubbedResult) {
            stubbedStats.put(path, stubbedResult);
        }

        public void stubStatError(Path path, IOException error) {
            stubbedStatErrors.put(path, error);
        }

        @Override
        public FileStatus statIfFound(Path path, boolean followSymlinks) throws IOException {
            if (stubbedStatErrors.containsKey(path)) {
                throw stubbedStatErrors.get(path);
            }
            if (stubbedStats.containsKey(path)) {
                return stubbedStats.get(path);
            }
            return super.statIfFound(path, followSymlinks);
        }
    }
}

