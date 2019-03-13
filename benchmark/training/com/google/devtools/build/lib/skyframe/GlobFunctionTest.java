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


import Dirent.Type;
import NullEventHandler.INSTANCE;
import PathFragment.EMPTY_FRAGMENT;
import SkyframeExecutor.DEFAULT_THREAD_COUNT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.actions.FileValue;
import com.google.devtools.build.lib.actions.InconsistentFilesystemException;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.pkgcache.PathPackageLocator;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.vfs.FileStatus;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.lib.vfs.UnixGlob;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import com.google.devtools.build.skyframe.ErrorInfo;
import com.google.devtools.build.skyframe.EvaluationContext;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GlobFunction}.
 */
public abstract class GlobFunctionTest {
    private static final EvaluationContext EVALUATION_OPTIONS = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(DEFAULT_THREAD_COUNT).setEventHander(INSTANCE).build();

    @RunWith(JUnit4.class)
    public static class GlobFunctionAlwaysUseDirListingTest extends GlobFunctionTest {
        @Override
        protected boolean alwaysUseDirListing() {
            return true;
        }
    }

    @RunWith(JUnit4.class)
    public static class RegularGlobFunctionTest extends GlobFunctionTest {
        @Override
        protected boolean alwaysUseDirListing() {
            return false;
        }
    }

    private GlobFunctionTest.CustomInMemoryFs fs;

    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private RecordingDifferencer differencer;

    private Path root;

    private Path writableRoot;

    private Path outputBase;

    private Path pkgPath;

    private AtomicReference<PathPackageLocator> pkgLocator;

    private static final PackageIdentifier PKG_ID = PackageIdentifier.createInMainRepo("pkg");

    @Test
    public void testSimple() throws Exception {
        /* => */
        assertGlobMatches("food", "food");
    }

    @Test
    public void testStartsWithStar() throws Exception {
        /* => */
        assertGlobMatches("*oo", "foo");
    }

    @Test
    public void testStartsWithStarWithMiddleStar() throws Exception {
        /* => */
        assertGlobMatches("*f*o", "foo");
    }

    @Test
    public void testSingleMatchEqual() throws Exception {
        assertGlobsEqual("*oo", "*f*o");// both produce "foo"

    }

    @Test
    public void testEndsWithStar() throws Exception {
        /* => */
        assertGlobMatches("foo*", "foo", "food", "fool");
    }

    @Test
    public void testEndsWithStarWithMiddleStar() throws Exception {
        /* => */
        assertGlobMatches("f*oo*", "foo", "food", "fool");
    }

    @Test
    public void testMultipleMatchesEqual() throws Exception {
        assertGlobsEqual("foo*", "f*oo*");// both produce "foo", "food", "fool"

    }

    @Test
    public void testMiddleStar() throws Exception {
        /* => */
        assertGlobMatches("f*o", "foo");
    }

    @Test
    public void testTwoMiddleStars() throws Exception {
        /* => */
        assertGlobMatches("f*o*o", "foo");
    }

    @Test
    public void testSingleStarPatternWithNamedChild() throws Exception {
        /* => */
        assertGlobMatches("*/bar", "foo/bar");
    }

    @Test
    public void testDeepSubpackages() throws Exception {
        /* => */
        assertGlobMatches("*/*/c", "a1/b1/c");
    }

    @Test
    public void testSingleStarPatternWithChildGlob() throws Exception {
        /* => */
        assertGlobMatches("*/bar*", "foo/bar", "foo/barnacle", "food/barnacle", "fool/barnacle");
    }

    @Test
    public void testSingleStarAsChildGlob() throws Exception {
        /* => */
        assertGlobMatches("foo/*/wiz", "foo/bar/wiz", "foo/barnacle/wiz");
    }

    @Test
    public void testNoAsteriskAndFilesDontExist() throws Exception {
        // Note un-UNIX like semantics:
        /* => nothing */
        assertGlobMatches("ceci/n'est/pas/une/globbe");
    }

    @Test
    public void testSingleAsteriskUnderNonexistentDirectory() throws Exception {
        // Note un-UNIX like semantics:
        /* => nothing */
        assertGlobMatches("not-there/*");
    }

    @Test
    public void testDifferentGlobsSameResultEqual() throws Exception {
        // Once the globs are run, it doesn't matter what pattern ran; only the output.
        assertGlobsEqual("not-there/*", "syzygy/*");// Both produce nothing.

    }

    @Test
    public void testGlobUnderFile() throws Exception {
        /* => nothing */
        assertGlobMatches("foo/bar/wiz/file/*");
    }

    @Test
    public void testGlobEqualsHashCode() throws Exception {
        // Each "equality group" forms a set of elements that are all equals() to one another,
        // and also produce the same hashCode.
        // Matches foo/bar and foo/barnacle.
        // Matches lots of things.
        // Matches BUILD.
        // Matches nothing.
        new EqualsTester().addEqualityGroup(runGlob(false, "no-such-file")).addEqualityGroup(runGlob(false, "BUILD"), runGlob(true, "BUILD")).addEqualityGroup(runGlob(false, "**")).addEqualityGroup(runGlob(false, "f*o/bar*"), runGlob(false, "foo/bar*")).testEquals();
    }

    @Test
    public void testGlobDoesNotCrossPackageBoundary() throws Exception {
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/BUILD"));
        // "foo/bar" should not be in the results because foo is a separate package.
        /* => */
        assertGlobMatches("f*/*", "food/barnacle", "fool/barnacle");
    }

    @Test
    public void testGlobDirectoryMatchDoesNotCrossPackageBoundary() throws Exception {
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is a separate package.
        /* => */
        assertGlobMatches("foo/*", "foo/barnacle");
    }

    @Test
    public void testStarStarDoesNotCrossPackageBoundary() throws Exception {
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is a separate package.
        /* => */
        assertGlobMatches("foo/**", "foo/barnacle/wiz", "foo/barnacle", "foo");
    }

    @Test
    public void testGlobDoesNotCrossPackageBoundaryUnderOtherPackagePath() throws Exception {
        FileSystemUtils.createDirectoryAndParents(writableRoot.getRelative("pkg/foo/bar"));
        FileSystemUtils.createEmptyFile(writableRoot.getRelative("pkg/foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is detected as a separate package,
        // even though it is under a different package path.
        /* => */
        assertGlobMatches("foo/**", "foo/barnacle/wiz", "foo/barnacle", "foo");
    }

    @Test
    public void testGlobDoesNotCrossRepositoryBoundary() throws Exception {
        FileSystemUtils.appendIsoLatin1(root.getRelative("WORKSPACE"), "local_repository(name='local', path='pkg/foo')");
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/WORKSPACE"));
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/BUILD"));
        // "foo/bar" should not be in the results because foo is a separate repository.
        /* => */
        assertGlobMatches("f*/*", "food/barnacle", "fool/barnacle");
    }

    @Test
    public void testGlobDirectoryMatchDoesNotCrossRepositoryBoundary() throws Exception {
        FileSystemUtils.appendIsoLatin1(root.getRelative("WORKSPACE"), "local_repository(name='local', path='pkg/foo/bar')");
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/WORKSPACE"));
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is a separate repository.
        /* => */
        assertGlobMatches("foo/*", "foo/barnacle");
    }

    @Test
    public void testStarStarDoesNotCrossRepositoryBoundary() throws Exception {
        FileSystemUtils.appendIsoLatin1(root.getRelative("WORKSPACE"), "local_repository(name='local', path='pkg/foo/bar')");
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/WORKSPACE"));
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is a separate repository.
        /* => */
        assertGlobMatches("foo/**", "foo/barnacle/wiz", "foo/barnacle", "foo");
    }

    @Test
    public void testGlobDoesNotCrossRepositoryBoundaryUnderOtherPackagePath() throws Exception {
        FileSystemUtils.appendIsoLatin1(root.getRelative("WORKSPACE"), (("local_repository(name='local', path='" + (writableRoot.getRelative("pkg/foo/bar").getPathString())) + "')"));
        FileSystemUtils.createDirectoryAndParents(writableRoot.getRelative("pkg/foo/bar"));
        FileSystemUtils.createEmptyFile(writableRoot.getRelative("pkg/foo/bar/WORKSPACE"));
        FileSystemUtils.createEmptyFile(writableRoot.getRelative("pkg/foo/bar/BUILD"));
        // "foo/bar" should not be in the results because foo/bar is detected as a separate package,
        // even though it is under a different package path.
        /* => */
        assertGlobMatches("foo/**", "foo/barnacle/wiz", "foo/barnacle", "foo");
    }

    @Test
    public void testGlobWithoutWildcards() throws Exception {
        String pattern = "foo/bar/wiz/file";
        assertGlobMatches(pattern, "foo/bar/wiz/file");
        // Ensure that the glob depends on the FileValue and not on the DirectoryListingValue.
        pkgPath.getRelative("foo/bar/wiz/file").delete();
        // Nothing has been invalidated yet, so the cached result is returned.
        assertGlobMatches(pattern, "foo/bar/wiz/file");
        if (alwaysUseDirListing()) {
            differencer.invalidate(ImmutableList.of(FileStateValue.key(RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz/file")))));
            // The result should not rely on the FileStateValue, so it's still a cache hit.
            assertGlobMatches(pattern, "foo/bar/wiz/file");
            differencer.invalidate(ImmutableList.of(DirectoryListingStateValue.key(RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz")))));
            // This should have invalidated the glob result.
            /* => nothing */
            assertGlobMatches(pattern);
        } else {
            differencer.invalidate(ImmutableList.of(DirectoryListingStateValue.key(RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz")))));
            // The result should not rely on the DirectoryListingValue, so it's still a cache hit.
            assertGlobMatches(pattern, "foo/bar/wiz/file");
            differencer.invalidate(ImmutableList.of(FileStateValue.key(RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz/file")))));
            // This should have invalidated the glob result.
            /* => nothing */
            assertGlobMatches(pattern);
        }
    }

    @Test
    public void testIllegalPatterns() throws Exception {
        assertIllegalPattern("foo**bar");
        assertIllegalPattern("?");
        assertIllegalPattern("");
        assertIllegalPattern(".");
        assertIllegalPattern("/foo");
        assertIllegalPattern("./foo");
        assertIllegalPattern("foo/");
        assertIllegalPattern("foo/./bar");
        assertIllegalPattern("../foo/bar");
        assertIllegalPattern("foo//bar");
    }

    @Test
    public void testIllegalRecursivePatterns() throws Exception {
        for (String prefix : Lists.newArrayList("", "*/", "**/", "ba/")) {
            String suffix = ("/" + prefix).substring(0, prefix.length());
            for (String pattern : Lists.newArrayList("**fo", "fo**", "**fo**", "fo**fo", "fo**fo**fo")) {
                assertIllegalPattern((prefix + pattern));
                assertIllegalPattern((pattern + suffix));
            }
        }
    }

    /**
     * Tests that globs can contain Java regular expression special characters
     */
    @Test
    public void testSpecialRegexCharacter() throws Exception {
        Path aDotB = pkgPath.getChild("a.b");
        FileSystemUtils.createEmptyFile(aDotB);
        FileSystemUtils.createEmptyFile(pkgPath.getChild("aab"));
        // Note: this contains two asterisks because otherwise a RE is not built,
        // as an optimization.
        assertThat(UnixGlob.forPath(pkgPath).addPattern("*a.b*").globInterruptible()).containsExactly(aDotB);
    }

    @Test
    public void testMatchesCallWithNoCache() {
        assertThat(UnixGlob.matches("*a*b", "CaCb", null)).isTrue();
    }

    @Test
    public void testHiddenFiles() throws Exception {
        for (String dir : ImmutableList.of(".hidden", "..also.hidden", "not.hidden")) {
            FileSystemUtils.createDirectoryAndParents(pkgPath.getRelative(dir));
        }
        // Note that these are not in the result: ".", ".."
        assertGlobMatches("*", "..also.hidden", ".hidden", "BUILD", "a1", "a2", "foo", "food", "fool", "not.hidden");
        assertGlobMatches("*.hidden", "not.hidden");
    }

    @Test
    public void testDoubleStar() throws Exception {
        assertGlobMatches("**", "a1/b1/c", "a1/b1", "a1", "a2", "foo/bar/wiz", "foo/bar/wiz/file", "foo/bar", "foo/barnacle/wiz", "foo/barnacle", "foo", "food/barnacle/wiz", "food/barnacle", "food", "fool/barnacle/wiz", "fool/barnacle", "fool", "BUILD");
    }

    @Test
    public void testDoubleStarExcludeDirs() throws Exception {
        assertGlobWithoutDirsMatches("**", "foo/bar/wiz/file", "BUILD");
    }

    @Test
    public void testDoubleDoubleStar() throws Exception {
        assertGlobMatches("**/**", "a1/b1/c", "a1/b1", "a1", "a2", "foo/bar/wiz", "foo/bar/wiz/file", "foo/bar", "foo/barnacle/wiz", "foo/barnacle", "foo", "food/barnacle/wiz", "food/barnacle", "food", "fool/barnacle/wiz", "fool/barnacle", "fool", "BUILD");
    }

    @Test
    public void testDirectoryWithDoubleStar() throws Exception {
        assertGlobMatches("foo/**", "foo/bar/wiz", "foo/bar/wiz/file", "foo/bar", "foo/barnacle/wiz", "foo/barnacle", "foo");
    }

    @Test
    public void testDoubleStarPatternWithNamedChild() throws Exception {
        assertGlobMatches("**/bar", "foo/bar");
    }

    @Test
    public void testDoubleStarPatternWithChildGlob() throws Exception {
        assertGlobMatches("**/ba*", "foo/bar", "foo/barnacle", "food/barnacle", "fool/barnacle");
    }

    @Test
    public void testDoubleStarAsChildGlob() throws Exception {
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("foo/barnacle/wiz/wiz"));
        FileSystemUtils.createDirectoryAndParents(pkgPath.getRelative("foo/barnacle/baz/wiz"));
        assertGlobMatches("foo/**/wiz", "foo/bar/wiz", "foo/barnacle/wiz", "foo/barnacle/baz/wiz", "foo/barnacle/wiz/wiz");
    }

    @Test
    public void testDoubleStarUnderNonexistentDirectory() throws Exception {
        /* => nothing */
        assertGlobMatches("not-there/**");
    }

    @Test
    public void testDoubleStarUnderFile() throws Exception {
        /* => nothing */
        assertGlobMatches("foo/bar/wiz/file/**");
    }

    /**
     * Regression test for b/13319874: Directory listing crash.
     */
    @Test
    public void testResilienceToFilesystemInconsistencies_DirectoryExistence() throws Exception {
        // Our custom filesystem says "pkgPath/BUILD" exists but "pkgPath" does not exist.
        fs.stubStat(pkgPath, null);
        RootedPath pkgRootedPath = RootedPath.toRootedPath(Root.fromPath(root), pkgPath);
        FileStateValue pkgDirFileStateValue = FileStateValue.create(pkgRootedPath, null);
        FileValue pkgDirValue = FileValue.value(ImmutableList.of(pkgRootedPath), pkgRootedPath, pkgDirFileStateValue, pkgRootedPath, pkgDirFileStateValue);
        differencer.inject(ImmutableMap.of(FileValue.key(pkgRootedPath), pkgDirValue));
        String expectedMessage = "/root/workspace/pkg is no longer an existing directory";
        SkyKey skyKey = GlobValue.key(GlobFunctionTest.PKG_ID, Root.fromPath(root), "*/foo", false, EMPTY_FRAGMENT);
        EvaluationResult<GlobValue> result = driver.evaluate(ImmutableList.of(skyKey), GlobFunctionTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).isInstanceOf(InconsistentFilesystemException.class);
        assertThat(errorInfo.getException()).hasMessageThat().contains(expectedMessage);
    }

    @Test
    public void testResilienceToFilesystemInconsistencies_SubdirectoryExistence() throws Exception {
        // Our custom filesystem says directory "pkgPath/foo/bar" contains a subdirectory "wiz" but a
        // direct stat on "pkgPath/foo/bar/wiz" says it does not exist.
        Path fooBarDir = pkgPath.getRelative("foo/bar");
        fs.stubStat(fooBarDir.getRelative("wiz"), null);
        RootedPath fooBarDirRootedPath = RootedPath.toRootedPath(Root.fromPath(root), fooBarDir);
        SkyValue fooBarDirListingValue = DirectoryListingStateValue.create(ImmutableList.of(new com.google.devtools.build.lib.vfs.Dirent("wiz", Type.DIRECTORY)));
        differencer.inject(ImmutableMap.of(DirectoryListingStateValue.key(fooBarDirRootedPath), fooBarDirListingValue));
        String expectedMessage = "/root/workspace/pkg/foo/bar/wiz is no longer an existing directory.";
        SkyKey skyKey = GlobValue.key(GlobFunctionTest.PKG_ID, Root.fromPath(root), "**/wiz", false, EMPTY_FRAGMENT);
        EvaluationResult<GlobValue> result = driver.evaluate(ImmutableList.of(skyKey), GlobFunctionTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).isInstanceOf(InconsistentFilesystemException.class);
        assertThat(errorInfo.getException()).hasMessageThat().contains(expectedMessage);
    }

    @Test
    public void testResilienceToFilesystemInconsistencies_SymlinkType() throws Exception {
        RootedPath wizRootedPath = RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz"));
        RootedPath fileRootedPath = RootedPath.toRootedPath(Root.fromPath(root), pkgPath.getRelative("foo/bar/wiz/file"));
        final FileStatus realStat = fileRootedPath.asPath().stat();
        fs.stubStat(fileRootedPath.asPath(), new FileStatus() {
            @Override
            public boolean isFile() {
                // The stat says foo/bar/wiz/file is a real file, not a symlink.
                return true;
            }

            @Override
            public boolean isSpecialFile() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public long getSize() throws IOException {
                return realStat.getSize();
            }

            @Override
            public long getLastModifiedTime() throws IOException {
                return realStat.getLastModifiedTime();
            }

            @Override
            public long getLastChangeTime() throws IOException {
                return realStat.getLastChangeTime();
            }

            @Override
            public long getNodeId() throws IOException {
                return realStat.getNodeId();
            }
        });
        // But the dir listing say foo/bar/wiz/file is a symlink.
        SkyValue wizDirListingValue = DirectoryListingStateValue.create(ImmutableList.of(new com.google.devtools.build.lib.vfs.Dirent("file", Type.SYMLINK)));
        differencer.inject(ImmutableMap.of(DirectoryListingStateValue.key(wizRootedPath), wizDirListingValue));
        String expectedMessage = ("readdir and stat disagree about whether " + (fileRootedPath.asPath())) + " is a symlink";
        SkyKey skyKey = GlobValue.key(GlobFunctionTest.PKG_ID, Root.fromPath(root), "foo/bar/wiz/*", false, EMPTY_FRAGMENT);
        EvaluationResult<GlobValue> result = driver.evaluate(ImmutableList.of(skyKey), GlobFunctionTest.EVALUATION_OPTIONS);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).isInstanceOf(InconsistentFilesystemException.class);
        assertThat(errorInfo.getException()).hasMessageThat().contains(expectedMessage);
    }

    @Test
    public void testSymlinks() throws Exception {
        FileSystemUtils.createDirectoryAndParents(pkgPath.getRelative("symlinks"));
        FileSystemUtils.ensureSymbolicLink(pkgPath.getRelative("symlinks/dangling.txt"), "nope");
        FileSystemUtils.createEmptyFile(pkgPath.getRelative("symlinks/yup"));
        FileSystemUtils.ensureSymbolicLink(pkgPath.getRelative("symlinks/existing.txt"), "yup");
        assertGlobMatches("symlinks/*.txt", "symlinks/existing.txt");
    }

    private static final class CustomInMemoryFs extends InMemoryFileSystem {
        private Map<Path, FileStatus> stubbedStats = Maps.newHashMap();

        public CustomInMemoryFs(ManualClock manualClock) {
            super(manualClock);
        }

        public void stubStat(Path path, @Nullable
        FileStatus stubbedResult) {
            stubbedStats.put(path, stubbedResult);
        }

        @Override
        public FileStatus statIfFound(Path path, boolean followSymlinks) throws IOException {
            if (stubbedStats.containsKey(path)) {
                return stubbedStats.get(path);
            }
            return super.statIfFound(path, followSymlinks);
        }
    }
}

