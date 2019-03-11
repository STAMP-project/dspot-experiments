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
package com.google.devtools.build.lib.skyframe.packages;


import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Simple tests for {@link BazelPackageLoader}.
 *
 * <p>Bazel's unit and integration tests do sanity checks with {@link BazelPackageLoader} under the
 * covers, so we get pretty exhaustive correctness tests for free.
 */
@RunWith(JUnit4.class)
public final class BazelPackageLoaderTest extends AbstractPackageLoaderTest {
    private Path installBase;

    private Path outputBase;

    @Test
    public void simpleLocalRepositoryPackage() throws Exception {
        file("WORKSPACE", "local_repository(name = 'r', path='r')");
        file("r/WORKSPACE", "workspace(name = 'r')");
        file("r/good/BUILD", "sh_library(name = 'good')");
        RepositoryName rRepoName = RepositoryName.create("@r");
        fetchExternalRepo(rRepoName);
        PackageLoader pkgLoader = newPackageLoader();
        PackageIdentifier pkgId = PackageIdentifier.create(rRepoName, PathFragment.create("good"));
        Package goodPkg = pkgLoader.loadPackage(pkgId);
        assertThat(containsErrors()).isFalse();
        assertThat(getTarget("good").getAssociatedRule().getRuleClass()).isEqualTo("sh_library");
        MoreAsserts.assertNoEvents(getEvents());
        MoreAsserts.assertNoEvents(handler.getEvents());
    }

    @Test
    public void newLocalRepository() throws Exception {
        file("WORKSPACE", ("new_local_repository(name = 'r', path = '/r', " + "build_file_content = \'sh_library(name = \"good\")\')"));
        fs.getPath("/r").createDirectoryAndParents();
        RepositoryName rRepoName = RepositoryName.create("@r");
        fetchExternalRepo(rRepoName);
        PackageLoader pkgLoader = newPackageLoader();
        PackageIdentifier pkgId = PackageIdentifier.create(rRepoName, PathFragment.create(""));
        Package goodPkg = pkgLoader.loadPackage(pkgId);
        assertThat(containsErrors()).isFalse();
        assertThat(getTarget("good").getAssociatedRule().getRuleClass()).isEqualTo("sh_library");
        MoreAsserts.assertNoEvents(getEvents());
        MoreAsserts.assertNoEvents(handler.getEvents());
    }

    @Test
    public void buildDotBazelForSubpackageCheckDuringGlobbing() throws Exception {
        file("a/BUILD", "filegroup(name = 'fg', srcs = glob(['sub/a.txt']))");
        file("a/sub/a.txt");
        file("a/sub/BUILD.bazel");
        PackageLoader pkgLoader = newPackageLoader();
        PackageIdentifier pkgId = PackageIdentifier.createInMainRepo(PathFragment.create("a"));
        Package aPkg = pkgLoader.loadPackage(pkgId);
        assertThat(containsErrors()).isFalse();
        MoreAsserts.assertThrows(NoSuchTargetException.class, () -> aPkg.getTarget("sub/a.txt"));
        MoreAsserts.assertNoEvents(getEvents());
        MoreAsserts.assertNoEvents(handler.getEvents());
    }
}

