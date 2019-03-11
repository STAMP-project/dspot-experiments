/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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


import PathFragment.EMPTY_FRAGMENT;
import RepositoryName.MAIN;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyKey;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link LocalRepositoryLookupFunction}.
 */
// TODO(katre): Add tests for the following exceptions
// While reading dir/WORKSPACE:
// - IOException
// - FileSymlinkException
// - InconsistentFilesystemException
// While loading //external
// - BuildFileNotFoundException
// - InconsistentFilesystemException
// While reading //external:WORKSPACE
// - PackageFunctionException
// - NameConflictException
// - WorkspaceFileException
@RunWith(JUnit4.class)
public class LocalRepositoryLookupFunctionTest extends FoundationTestCase {
    private AtomicReference<ImmutableSet<PackageIdentifier>> deletedPackages;

    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private RecordingDifferencer differencer;

    @Test
    public void testNoPath() throws Exception {
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), EMPTY_FRAGMENT));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository()).isEqualTo(MAIN);
        assertThat(repositoryLookupValue.getPath()).isEqualTo(EMPTY_FRAGMENT);
    }

    @Test
    public void testActualPackage() throws Exception {
        scratch.file("some/path/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("some/path")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository()).isEqualTo(MAIN);
        assertThat(repositoryLookupValue.getPath()).isEqualTo(EMPTY_FRAGMENT);
    }

    @Test
    public void testLocalRepository() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='local/repo')");
        scratch.file("local/repo/WORKSPACE");
        scratch.file("local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository().getName()).isEqualTo("@local");
        assertThat(repositoryLookupValue.getPath()).isEqualTo(PathFragment.create("local/repo"));
    }

    @Test
    public void testLocalRepository_absolutePath() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='/abs/local/repo')");
        scratch.file("/abs/local/repo/WORKSPACE");
        scratch.file("/abs/local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory.getRelative("/abs")), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository().getName()).isEqualTo("@local");
        assertThat(repositoryLookupValue.getPath()).isEqualTo(PathFragment.create("/abs/local/repo"));
    }

    @Test
    public void testLocalRepository_nonNormalizedPath() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='./local/repo')");
        scratch.file("local/repo/WORKSPACE");
        scratch.file("local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository().getName()).isEqualTo("@local");
        assertThat(repositoryLookupValue.getPath()).isEqualTo(PathFragment.create("local/repo"));
    }

    @Test
    public void testLocalRepository_absolutePath_nonNormalized() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='/abs/local/./repo')");
        scratch.file("/abs/local/repo/WORKSPACE");
        scratch.file("/abs/local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory.getRelative("/abs")), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository().getName()).isEqualTo("@local");
        assertThat(repositoryLookupValue.getPath()).isEqualTo(PathFragment.create("/abs/local/repo"));
    }

    @Test
    public void testLocalRepositorySubPackage() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='local/repo')");
        scratch.file("local/repo/WORKSPACE");
        scratch.file("local/repo/BUILD");
        scratch.file("local/repo/sub/package/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo/sub/package")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository().getName()).isEqualTo("@local");
        assertThat(repositoryLookupValue.getPath()).isEqualTo(PathFragment.create("local/repo"));
    }

    @Test
    public void testWorkspaceButNoLocalRepository() throws Exception {
        scratch.overwriteFile("WORKSPACE", "");
        scratch.file("local/repo/WORKSPACE");
        scratch.file("local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        assertThat(repositoryLookupValue.getRepository()).isEqualTo(MAIN);
        assertThat(repositoryLookupValue.getPath()).isEqualTo(EMPTY_FRAGMENT);
    }

    @Test
    public void testLocalRepository_LocalWorkspace_SymlinkCycle() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='local', path='local/repo')");
        Path localRepoWorkspace = scratch.resolve("local/repo/WORKSPACE");
        Path localRepoWorkspaceLink = scratch.resolve("local/repo/WORKSPACE.link");
        FileSystemUtils.createDirectoryAndParents(localRepoWorkspace.getParentDirectory());
        FileSystemUtils.createDirectoryAndParents(localRepoWorkspaceLink.getParentDirectory());
        localRepoWorkspace.createSymbolicLink(localRepoWorkspaceLink);
        localRepoWorkspaceLink.createSymbolicLink(localRepoWorkspace);
        scratch.file("local/repo/BUILD");
        SkyKey localRepositoryKey = createKey(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo")));
        EvaluationResult<LocalRepositoryLookupValue> result = lookupDirectory(localRepositoryKey);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(localRepositoryKey).hasExceptionThat().hasMessage(("FileSymlinkException while checking if there is a WORKSPACE file in " + "/workspace/local/repo"));
    }

    @Test
    public void testLocalRepository_MainWorkspace_NotFound() throws Exception {
        // Do not add a local_repository to WORKSPACE.
        scratch.overwriteFile("WORKSPACE", "");
        scratch.deleteFile("WORKSPACE");
        scratch.file("local/repo/WORKSPACE");
        scratch.file("local/repo/BUILD");
        LocalRepositoryLookupValue repositoryLookupValue = lookupDirectory(RootedPath.toRootedPath(Root.fromPath(rootDirectory), PathFragment.create("local/repo")));
        assertThat(repositoryLookupValue).isNotNull();
        // In this case, the repository should be MAIN as we can't find any local_repository rules.
        assertThat(repositoryLookupValue.getRepository()).isEqualTo(MAIN);
        assertThat(repositoryLookupValue.getPath()).isEqualTo(EMPTY_FRAGMENT);
    }
}

