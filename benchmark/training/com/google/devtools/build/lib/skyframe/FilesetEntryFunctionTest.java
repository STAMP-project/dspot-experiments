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


import SymlinkBehavior.COPY;
import SymlinkBehavior.DEREFERENCE;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.FilesetTraversalParams;
import com.google.devtools.build.lib.actions.FilesetTraversalParams.PackageBoundaryMode;
import com.google.devtools.build.lib.actions.FilesetTraversalParamsFactory;
import com.google.devtools.build.lib.packages.FilesetEntry.SymlinkBehavior;
import com.google.devtools.build.lib.pkgcache.PathPackageLocator;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.Fingerprint;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FilesetEntryFunction}.
 */
@RunWith(JUnit4.class)
public final class FilesetEntryFunctionTest extends FoundationTestCase {
    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private RecordingDifferencer differencer;

    private AtomicReference<PathPackageLocator> pkgLocator;

    @Test
    public void testFileTraversalForFile() throws Exception {
        Artifact file = createSourceArtifact("foo/file.real");
        FilesetTraversalParams params = /* ownerLabel= */
        /* fileToTraverse= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.fileTraversal(FilesetEntryFunctionTest.label("//foo"), file, PathFragment.create("output-name"), COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params, symlink("output-name", file));
    }

    @Test
    public void testFileTraversalForFileSymlinkNoFollow() throws Exception {
        assertFileTraversalForFileSymlink(COPY);
    }

    @Test
    public void testFileTraversalForFileSymlinkFollow() throws Exception {
        assertFileTraversalForFileSymlink(DEREFERENCE);
    }

    @Test
    public void testFileTraversalForDirectory() throws Exception {
        Artifact dir = getSourceArtifact("foo/dir_real");
        RootedPath fileA = createFile(FilesetEntryFunctionTest.childOf(dir, "file.a"), "hello");
        RootedPath fileB = createFile(FilesetEntryFunctionTest.childOf(dir, "sub/file.b"), "world");
        FilesetTraversalParams params = /* ownerLabel= */
        /* fileToTraverse= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput */
        FilesetTraversalParamsFactory.fileTraversal(FilesetEntryFunctionTest.label("//foo"), dir, PathFragment.create("output-name"), COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params, symlink("output-name/file.a", fileA), symlink("output-name/sub/file.b", fileB));
    }

    @Test
    public void testFileTraversalForDirectorySymlinkFollow() throws Exception {
        assertFileTraversalForDirectorySymlink(COPY);
    }

    @Test
    public void testFileTraversalForDirectorySymlinkNoFollow() throws Exception {
        assertFileTraversalForDirectorySymlink(DEREFERENCE);
    }

    @Test
    public void testRecursiveTraversalForDirectoryCrossNoFollow() throws Exception {
        assertRecursiveTraversalForDirectory(COPY, CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectoryDontCrossNoFollow() throws Exception {
        assertRecursiveTraversalForDirectory(COPY, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectoryReportErrorNoFollow() throws Exception {
        assertRecursiveTraversalForDirectory(COPY, REPORT_ERROR);
    }

    @Test
    public void testRecursiveTraversalForDirectoryCrossFollow() throws Exception {
        assertRecursiveTraversalForDirectory(DEREFERENCE, CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectoryDontCrossFollow() throws Exception {
        assertRecursiveTraversalForDirectory(DEREFERENCE, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectoryReportErrorFollow() throws Exception {
        assertRecursiveTraversalForDirectory(DEREFERENCE, REPORT_ERROR);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkNoFollowCross() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(COPY, CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkNoFollowDontCross() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(COPY, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkNoFollowReportError() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(COPY, REPORT_ERROR);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkFollowCross() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(DEREFERENCE, CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkFollowDontCross() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(DEREFERENCE, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForDirectorySymlinkFollowReportError() throws Exception {
        assertRecursiveTraversalForDirectorySymlink(DEREFERENCE, REPORT_ERROR);
    }

    @Test
    public void testRecursiveTraversalForPackageNoFollowCross() throws Exception {
        assertRecursiveTraversalForPackage(COPY, CROSS);
    }

    @Test
    public void testRecursiveTraversalForPackageNoFollowDontCross() throws Exception {
        assertRecursiveTraversalForPackage(COPY, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForPackageNoFollowReportError() throws Exception {
        assertRecursiveTraversalForPackage(COPY, REPORT_ERROR);
    }

    @Test
    public void testRecursiveTraversalForPackageFollowCross() throws Exception {
        assertRecursiveTraversalForPackage(DEREFERENCE, CROSS);
    }

    @Test
    public void testRecursiveTraversalForPackageFollowDontCross() throws Exception {
        assertRecursiveTraversalForPackage(DEREFERENCE, DONT_CROSS);
    }

    @Test
    public void testRecursiveTraversalForPackageFollowReportError() throws Exception {
        assertRecursiveTraversalForPackage(DEREFERENCE, REPORT_ERROR);
    }

    @Test
    public void testFileTraversalForDanglingSymlink() throws Exception {
        Artifact linkName = getSourceArtifact("foo/dangling.sym");
        RootedPath linkTarget = createFile(FilesetEntryFunctionTest.siblingOf(linkName, "target.file"), "blah");
        linkName.getPath().createSymbolicLink(PathFragment.create("target.file"));
        linkTarget.asPath().delete();
        FilesetTraversalParams params = /* ownerLabel= */
        /* fileToTraverse= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.fileTraversal(FilesetEntryFunctionTest.label("//foo"), linkName, PathFragment.create("output-name"), COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params);// expect empty results

    }

    @Test
    public void testExclusionOfDanglingSymlinkWithSymlinkModeCopy() throws Exception {
        assertExclusionOfDanglingSymlink(COPY);
    }

    @Test
    public void testExclusionOfDanglingSymlinkWithSymlinkModeDereference() throws Exception {
        assertExclusionOfDanglingSymlink(DEREFERENCE);
    }

    @Test
    public void testExcludes() throws Exception {
        Artifact buildFile = getSourceArtifact("foo/BUILD");
        createFile(buildFile);
        Artifact outerFile = getSourceArtifact("foo/outerfile.txt");
        createFile(outerFile);
        Artifact innerFile = getSourceArtifact("foo/dir/innerfile.txt");
        createFile(innerFile);
        FilesetTraversalParams params = /* ownerLabel */
        /* buildFile */
        /* excludes */
        /* symlinkBehaviorMode */
        /* pkgBoundaryMode */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.recursiveTraversalOfPackage(FilesetEntryFunctionTest.label("//foo"), buildFile, PathFragment.create("output-name"), ImmutableSet.of(), COPY, PackageBoundaryMode.DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params, symlink("output-name/BUILD", buildFile), symlink("output-name/outerfile.txt", outerFile), symlink("output-name/dir/innerfile.txt", innerFile));
        // Make sure the file within the excluded directory is no longer present.
        params = /* ownerLabel */
        /* buildFile */
        /* excludes */
        /* symlinkBehaviorMode */
        /* pkgBoundaryMode */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.recursiveTraversalOfPackage(FilesetEntryFunctionTest.label("//foo"), buildFile, PathFragment.create("output-name"), ImmutableSet.of("dir"), COPY, PackageBoundaryMode.DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params, symlink("output-name/BUILD", buildFile), symlink("output-name/outerfile.txt", outerFile));
    }

    @Test
    public void testFileTraversalForNonExistentFile() throws Exception {
        Artifact path = getSourceArtifact("foo/non-existent");
        FilesetTraversalParams params = /* ownerLabel= */
        /* fileToTraverse= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.fileTraversal(FilesetEntryFunctionTest.label("//foo"), path, PathFragment.create("output-name"), COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params);// expect empty results

    }

    @Test
    public void testRecursiveTraversalForDanglingSymlink() throws Exception {
        Artifact linkName = getSourceArtifact("foo/dangling.sym");
        RootedPath linkTarget = createFile(FilesetEntryFunctionTest.siblingOf(linkName, "target.file"), "blah");
        linkName.getPath().createSymbolicLink(PathFragment.create("target.file"));
        linkTarget.asPath().delete();
        FilesetTraversalParams params = /* ownerLabel= */
        /* directoryToTraverse= */
        /* excludes= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.recursiveTraversalOfDirectory(FilesetEntryFunctionTest.label("//foo"), linkName, PathFragment.create("output-name"), null, COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params);// expect empty results

    }

    @Test
    public void testRecursiveTraversalForNonExistentFile() throws Exception {
        Artifact path = getSourceArtifact("foo/non-existent");
        FilesetTraversalParams params = /* ownerLabel= */
        /* directoryToTraverse= */
        /* excludes= */
        /* symlinkBehaviorMode= */
        /* pkgBoundaryMode= */
        /* strictFilesetOutput= */
        FilesetTraversalParamsFactory.recursiveTraversalOfDirectory(FilesetEntryFunctionTest.label("//foo"), path, PathFragment.create("output-name"), null, COPY, DONT_CROSS, false);
        assertSymlinksCreatedInOrder(params);// expect empty results

    }

    /**
     * Tests that the fingerprint is a function of all arguments of the factory method.
     *
     * <p>Implementations must provide:
     * <ul>
     * <li>two different values (a domain) for each argument of the factory method and whether or not
     * it is expected to influence the fingerprint
     * <li>a way to instantiate {@link FilesetTraversalParams} with a given set of arguments from the
     * specified domains
     * </ul>
     *
     * <p>The tests will instantiate pairs of {@link FilesetTraversalParams} objects with only a given
     * attribute differing, and observe whether the fingerprints differ (if they are expected to) or
     * are the same (otherwise).
     */
    private abstract static class FingerprintTester {
        private final Map<String, FilesetEntryFunctionTest.Domain> domains;

        FingerprintTester(Map<String, FilesetEntryFunctionTest.Domain> domains) {
            this.domains = domains;
        }

        abstract FilesetTraversalParams create(Map<String, ?> kwArgs) throws Exception;

        private Map<String, ?> getDefaultArgs() {
            return getKwArgs(null);
        }

        private Map<String, ?> getKwArgs(@Nullable
        String useAlternateFor) {
            Map<String, Object> values = new HashMap<>();
            for (Map.Entry<String, FilesetEntryFunctionTest.Domain> d : domains.entrySet()) {
                values.put(d.getKey(), (d.getKey().equals(useAlternateFor) ? d.getValue().valueA : d.getValue().valueB));
            }
            return values;
        }

        public void doTest() throws Exception {
            Fingerprint fp = new Fingerprint();
            create(getDefaultArgs()).fingerprint(fp);
            String primary = fp.hexDigestAndReset();
            for (String argName : domains.keySet()) {
                create(getKwArgs(argName)).fingerprint(fp);
                String secondary = fp.hexDigestAndReset();
                if (domains.get(argName).includedInFingerprint) {
                    assertWithMessage(((("Argument '" + argName) + "' was expected to be included in the") + " fingerprint, but wasn't")).that(primary).isNotEqualTo(secondary);
                } else {
                    assertWithMessage(((("Argument '" + argName) + "' was expected not to be included in the") + " fingerprint, but was")).that(primary).isEqualTo(secondary);
                }
            }
        }
    }

    private static final class Domain {
        boolean includedInFingerprint;

        Object valueA;

        Object valueB;

        Domain(boolean includedInFingerprint, Object valueA, Object valueB) {
            this.includedInFingerprint = includedInFingerprint;
            this.valueA = valueA;
            this.valueB = valueB;
        }
    }

    @Test
    public void testFingerprintOfFileTraversal() throws Exception {
        new FilesetEntryFunctionTest.FingerprintTester(ImmutableMap.<String, FilesetEntryFunctionTest.Domain>builder().put("ownerLabel", FilesetEntryFunctionTest.notPartOfFingerprint("//foo", "//bar")).put("fileToTraverse", FilesetEntryFunctionTest.partOfFingerprint("foo/file.a", "bar/file.b")).put("destPath", FilesetEntryFunctionTest.partOfFingerprint("out1", "out2")).put("symlinkBehaviorMode", FilesetEntryFunctionTest.partOfFingerprint(COPY, DEREFERENCE)).put("pkgBoundaryMode", FilesetEntryFunctionTest.partOfFingerprint(CROSS, DONT_CROSS)).put("strictFilesetOutput", FilesetEntryFunctionTest.partOfFingerprint(true, false)).build()) {
            @Override
            FilesetTraversalParams create(Map<String, ?> kwArgs) throws Exception {
                return FilesetTraversalParamsFactory.fileTraversal(FilesetEntryFunctionTest.label(((String) (kwArgs.get("ownerLabel")))), getSourceArtifact(((String) (kwArgs.get("fileToTraverse")))), PathFragment.create(((String) (kwArgs.get("destPath")))), ((SymlinkBehavior) (kwArgs.get("symlinkBehaviorMode"))), ((PackageBoundaryMode) (kwArgs.get("pkgBoundaryMode"))), ((Boolean) (kwArgs.get("strictFilesetOutput"))));
            }
        }.doTest();
    }

    @Test
    public void testFingerprintOfDirectoryTraversal() throws Exception {
        new FilesetEntryFunctionTest.FingerprintTester(ImmutableMap.<String, FilesetEntryFunctionTest.Domain>builder().put("ownerLabel", FilesetEntryFunctionTest.notPartOfFingerprint("//foo", "//bar")).put("directoryToTraverse", FilesetEntryFunctionTest.partOfFingerprint("foo/dir_a", "bar/dir_b")).put("destPath", FilesetEntryFunctionTest.partOfFingerprint("out1", "out2")).put("excludes", FilesetEntryFunctionTest.partOfFingerprint(ImmutableSet.<String>of(), ImmutableSet.<String>of("blah"))).put("symlinkBehaviorMode", FilesetEntryFunctionTest.partOfFingerprint(COPY, DEREFERENCE)).put("pkgBoundaryMode", FilesetEntryFunctionTest.partOfFingerprint(CROSS, DONT_CROSS)).put("strictFilesetOutput", FilesetEntryFunctionTest.partOfFingerprint(true, false)).build()) {
            @SuppressWarnings("unchecked")
            @Override
            FilesetTraversalParams create(Map<String, ?> kwArgs) throws Exception {
                return FilesetTraversalParamsFactory.recursiveTraversalOfDirectory(FilesetEntryFunctionTest.label(((String) (kwArgs.get("ownerLabel")))), getSourceArtifact(((String) (kwArgs.get("directoryToTraverse")))), PathFragment.create(((String) (kwArgs.get("destPath")))), ((Set<String>) (kwArgs.get("excludes"))), ((SymlinkBehavior) (kwArgs.get("symlinkBehaviorMode"))), ((PackageBoundaryMode) (kwArgs.get("pkgBoundaryMode"))), ((Boolean) (kwArgs.get("strictFilesetOutput"))));
            }
        }.doTest();
    }

    @Test
    public void testFingerprintOfPackageTraversal() throws Exception {
        new FilesetEntryFunctionTest.FingerprintTester(ImmutableMap.<String, FilesetEntryFunctionTest.Domain>builder().put("ownerLabel", FilesetEntryFunctionTest.notPartOfFingerprint("//foo", "//bar")).put("buildFile", FilesetEntryFunctionTest.partOfFingerprint("foo/BUILD", "bar/BUILD")).put("destPath", FilesetEntryFunctionTest.partOfFingerprint("out1", "out2")).put("excludes", FilesetEntryFunctionTest.partOfFingerprint(ImmutableSet.<String>of(), ImmutableSet.<String>of("blah"))).put("symlinkBehaviorMode", FilesetEntryFunctionTest.partOfFingerprint(COPY, DEREFERENCE)).put("pkgBoundaryMode", FilesetEntryFunctionTest.partOfFingerprint(CROSS, DONT_CROSS)).build()) {
            @SuppressWarnings("unchecked")
            @Override
            FilesetTraversalParams create(Map<String, ?> kwArgs) throws Exception {
                return /* strictFilesetOutput= */
                FilesetTraversalParamsFactory.recursiveTraversalOfPackage(FilesetEntryFunctionTest.label(((String) (kwArgs.get("ownerLabel")))), getSourceArtifact(((String) (kwArgs.get("buildFile")))), PathFragment.create(((String) (kwArgs.get("destPath")))), ((Set<String>) (kwArgs.get("excludes"))), ((SymlinkBehavior) (kwArgs.get("symlinkBehaviorMode"))), ((PackageBoundaryMode) (kwArgs.get("pkgBoundaryMode"))), false);
            }
        }.doTest();
    }

    @Test
    public void testFingerprintOfNestedTraversal() throws Exception {
        Artifact nested1 = getSourceArtifact("a/b");
        Artifact nested2 = getSourceArtifact("a/c");
        new FilesetEntryFunctionTest.FingerprintTester(ImmutableMap.<String, FilesetEntryFunctionTest.Domain>of("ownerLabel", FilesetEntryFunctionTest.notPartOfFingerprint("//foo", "//bar"), "nestedArtifact", FilesetEntryFunctionTest.partOfFingerprint(nested1, nested2), "destDir", FilesetEntryFunctionTest.partOfFingerprint("out1", "out2"), "excludes", FilesetEntryFunctionTest.partOfFingerprint(ImmutableSet.<String>of(), ImmutableSet.<String>of("x")))) {
            @SuppressWarnings("unchecked")
            @Override
            FilesetTraversalParams create(Map<String, ?> kwArgs) throws Exception {
                return FilesetTraversalParamsFactory.nestedTraversal(FilesetEntryFunctionTest.label(((String) (kwArgs.get("ownerLabel")))), ((Artifact) (kwArgs.get("nestedArtifact"))), PathFragment.create(((String) (kwArgs.get("destDir")))), ((Set<String>) (kwArgs.get("excludes"))));
            }
        }.doTest();
    }
}

