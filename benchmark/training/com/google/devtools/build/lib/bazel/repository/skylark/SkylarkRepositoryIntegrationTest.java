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
package com.google.devtools.build.lib.bazel.repository.skylark;


import LocalRepositoryRule.NAME;
import SkyFunctions.REPOSITORY;
import SkyFunctions.REPOSITORY_DIRECTORY;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.bazel.repository.downloader.HttpDownloader;
import com.google.devtools.build.lib.packages.BuildFileContainsErrorsException;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.rules.repository.LocalRepositoryFunction;
import com.google.devtools.build.lib.rules.repository.RepositoryDelegatorFunction;
import com.google.devtools.build.lib.rules.repository.RepositoryFunction;
import com.google.devtools.build.lib.rules.repository.RepositoryLoaderFunction;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyFunctionName;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Integration test for skylark repository not as heavyweight than shell integration tests.
 */
@RunWith(JUnit4.class)
public class SkylarkRepositoryIntegrationTest extends BuildViewTestCase {
    // The RuleClassProvider loaded with the SkylarkRepositoryModule
    private ConfiguredRuleClassProvider ruleProvider = null;

    // The Analysis mock injected with the SkylarkRepositoryFunction
    private AnalysisMock analysisMock = null;

    /**
     * Proxy to the real analysis mock to overwrite {@code #getSkyFunctions(BlazeDirectories)} to
     * inject the SkylarkRepositoryFunction in the list of SkyFunctions. In Bazel, this function is
     * injected by the corresponding @{code BlazeModule}.
     */
    private static class CustomAnalysisMock extends AnalysisMock.Delegate {
        CustomAnalysisMock(AnalysisMock proxied) {
            super(proxied);
        }

        @Override
        public ImmutableMap<SkyFunctionName, SkyFunction> getSkyFunctions(BlazeDirectories directories) {
            // Add both the local repository and the skylark repository functions
            // The RepositoryCache mock injected with the SkylarkRepositoryFunction
            HttpDownloader downloader = Mockito.mock(HttpDownloader.class);
            RepositoryFunction localRepositoryFunction = new LocalRepositoryFunction();
            SkylarkRepositoryFunction skylarkRepositoryFunction = new SkylarkRepositoryFunction(downloader);
            ImmutableMap<String, RepositoryFunction> repositoryHandlers = ImmutableMap.of(NAME, localRepositoryFunction);
            RepositoryDelegatorFunction function = new RepositoryDelegatorFunction(repositoryHandlers, skylarkRepositoryFunction, new AtomicBoolean(true), ImmutableMap::of, directories);
            return ImmutableMap.of(REPOSITORY_DIRECTORY, function, REPOSITORY, new RepositoryLoaderFunction());
        }
    }

    @Test
    public void testSkylarkLocalRepository() throws Exception {
        // A simple test that recreates local_repository with Skylark.
        scratch.file("/repo2/WORKSPACE");
        scratch.file("/repo2/bar.txt");
        scratch.file("/repo2/BUILD", "filegroup(name='bar', srcs=['bar.txt'], path='foo')");
        scratch.file("def.bzl", "def _impl(repository_ctx):", "  repository_ctx.symlink(repository_ctx.attr.path, '')", "", "repo = repository_rule(", "    implementation=_impl,", "    local=True,", "    attrs={'path': attr.string(mandatory=True)})");
        scratch.file(rootDirectory.getRelative("BUILD").getPathString());
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("load('//:def.bzl', 'repo')").add("repo(name='foo', path='/repo2')").build());
        invalidatePackages();
        ConfiguredTargetAndData target = getConfiguredTargetAndData("@foo//:bar");
        Object path = target.getTarget().getAssociatedRule().getAttributeContainer().getAttr("path");
        assertThat(path).isEqualTo("foo");
    }

    @Test
    public void testSkylarkSymlinkFileFromRepository() throws Exception {
        scratch.file("/repo2/bar.txt", "filegroup(name='bar', srcs=['foo.txt'], path='foo')");
        scratch.file("/repo2/BUILD");
        scratch.file("/repo2/WORKSPACE");
        scratch.file("def.bzl", "def _impl(repository_ctx):", "  repository_ctx.symlink(Label('@repo2//:bar.txt'), 'BUILD')", "  repository_ctx.file('foo.txt', 'foo')", "", "repo = repository_rule(", "    implementation=_impl,", "    local=True)");
        scratch.file(rootDirectory.getRelative("BUILD").getPathString());
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("local_repository(name='repo2', path='/repo2')").add("load('//:def.bzl', 'repo')").add("repo(name='foo')").build());
        invalidatePackages();
        ConfiguredTargetAndData target = getConfiguredTargetAndData("@foo//:bar");
        Object path = target.getTarget().getAssociatedRule().getAttributeContainer().getAttr("path");
        assertThat(path).isEqualTo("foo");
    }

    @Test
    public void testSkylarkRepositoryTemplate() throws Exception {
        scratch.file("/repo2/bar.txt", "filegroup(name='{target}', srcs=['foo.txt'], path='{path}')");
        scratch.file("/repo2/BUILD");
        scratch.file("/repo2/WORKSPACE");
        scratch.file("def.bzl", "def _impl(repository_ctx):", ("  repository_ctx.template('BUILD', Label('@repo2//:bar.txt'), " + "{'{target}': 'bar', '{path}': 'foo'})"), "  repository_ctx.file('foo.txt', 'foo')", "", "repo = repository_rule(", "    implementation=_impl,", "    local=True)");
        scratch.file(rootDirectory.getRelative("BUILD").getPathString());
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("local_repository(name='repo2', path='/repo2')").add("load('//:def.bzl', 'repo')").add("repo(name='foo')").build());
        invalidatePackages();
        ConfiguredTargetAndData target = getConfiguredTargetAndData("@foo//:bar");
        Object path = target.getTarget().getAssociatedRule().getAttributeContainer().getAttr("path");
        assertThat(path).isEqualTo("foo");
    }

    @Test
    public void testSkylarkRepositoryName() throws Exception {
        // Variation of the template rule to test the repository_ctx.name field.
        scratch.file("/repo2/bar.txt", "filegroup(name='bar', srcs=['foo.txt'], path='{path}')");
        scratch.file("/repo2/BUILD");
        scratch.file("/repo2/WORKSPACE");
        scratch.file("def.bzl", "def _impl(repository_ctx):", ("  repository_ctx.template('BUILD', Label('@repo2//:bar.txt'), " + "{'{path}': repository_ctx.name})"), "  repository_ctx.file('foo.txt', 'foo')", "", "repo = repository_rule(", "    implementation=_impl,", "    local=True)");
        scratch.file(rootDirectory.getRelative("BUILD").getPathString());
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("local_repository(name='repo2', path='/repo2')").add("load('//:def.bzl', 'repo')").add("repo(name='foobar')").build());
        invalidatePackages();
        ConfiguredTargetAndData target = getConfiguredTargetAndData("@foobar//:bar");
        Object path = target.getTarget().getAssociatedRule().getAttributeContainer().getAttr("path");
        assertThat(path).isEqualTo("foobar");
    }

    @Test
    public void testCycleErrorWhenCallingRandomTarget() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("/repo2/data.txt", "data");
        scratch.file("/repo2/BUILD", "exports_files_(['data.txt'])");
        scratch.file("/repo2/def.bzl", "def macro():", "  print('bleh')");
        scratch.file("/repo2/WORKSPACE");
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), "load('@foo//:def.bzl', 'repo')", "repo(name='foobar')", "local_repository(name='foo', path='/repo2')");
        try {
            invalidatePackages();
            getTarget("@foobar//:data.txt");
            Assert.fail();
        } catch (BuildFileContainsErrorsException e) {
            // This is expected
        }
        assertDoesNotContainEvent("cycle");
        assertContainsEvent("repository 'foo' was defined too late in your WORKSPACE file");
        assertContainsEvent("Failed to load Starlark extension '@foo//:def.bzl'.");
    }

    @Test
    public void testCycleErrorWhenCallingCycleTarget() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("/repo2/data.txt", "data");
        scratch.file("/repo2/BUILD", "exports_files_(['data.txt'])");
        scratch.file("/repo2/def.bzl", "def macro():", "  print('bleh')");
        scratch.file("/repo2/WORKSPACE");
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), "load('@foo//:def.bzl', 'repo')", "repo(name='foobar')", "local_repository(name='foo', path='/repo2')");
        try {
            invalidatePackages();
            getTarget("@foo//:data.txt");
            Assert.fail();
        } catch (BuildFileContainsErrorsException e) {
            // This is expected
        }
        assertDoesNotContainEvent("cycle");
        assertContainsEvent("the repository 'foo' was defined too late in your WORKSPACE file");
        assertContainsEvent("Failed to load Starlark extension '@foo//:def.bzl'.");
    }

    @Test
    public void testCycleErrorInWorkspaceFileWithExternalRepo() throws Exception {
        try (OutputStream output = /* append= */
        scratch.resolve("WORKSPACE").getOutputStream(true)) {
            output.write(("\nload(\'//foo:bar.bzl\', \'foobar\')" + "\ngit_repository(name = \'git_repo\')").getBytes(StandardCharsets.UTF_8));
        }
        scratch.file("BUILD", "");
        scratch.file("foo/BUILD", "");
        scratch.file("foo/bar.bzl", "load('@git_repo//xyz:foo.bzl', 'rule_from_git')", "rule_from_git(name = 'foobar')");
        invalidatePackages();
        try {
            getTarget("@//:git_repo");
            Assert.fail();
        } catch (AssertionError expected) {
            assertThat(expected).hasMessageThat().contains(("Failed to load Starlark extension " + ("\'@git_repo//xyz:foo.bzl\'.\n" + "It usually happens when the repository is not defined prior to being used.\n")));
        }
    }

    @Test
    public void testLoadDoesNotHideWorkspaceError() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("/repo2/data.txt", "data");
        scratch.file("/repo2/BUILD", "exports_files_(['data.txt'])");
        scratch.file("/repo2/def.bzl", "def macro():", "  print('bleh')");
        scratch.file("/repo2/WORKSPACE");
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("local_repository(name='bleh')").add("local_repository(name='foo', path='/repo2')").add("load('@foo//:def.bzl', 'repo')").add("repo(name='foobar')").build());
        try {
            invalidatePackages();
            getTarget("@foo//:data.txt");
            Assert.fail();
        } catch (NoSuchPackageException e) {
            // This is expected
            assertThat(e).hasMessageThat().contains("Could not load //external package");
        }
        assertContainsEvent("missing value for mandatory attribute 'path' in 'local_repository' rule");
    }

    @Test
    public void testLoadDoesNotHideWorkspaceFunction() throws Exception {
        scratch.file("def.bzl", "def macro():", "  print('bleh')");
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("workspace(name='bleh')").add("load('//:def.bzl', 'macro')").build());
        scratch.file("data.txt");
        scratch.file("BUILD", "filegroup(", "  name='files', ", "  srcs=['data.txt'])");
        invalidatePackages();
        assertThat(getRuleContext(getConfiguredTarget("//:files")).getWorkspaceName()).isEqualTo("bleh");
    }

    @Test
    public void testSkylarkRepositoryCannotOverrideBuiltInAttribute() throws Exception {
        scratch.file("def.bzl", "def _impl(ctx):", "  print(ctx.attr.name)", "", "repo = repository_rule(", "    implementation=_impl,", "    attrs={'name': attr.string(mandatory=True)})");
        scratch.file(rootDirectory.getRelative("BUILD").getPathString());
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), "load('//:def.bzl', 'repo')", "repo(name='foo')");
        invalidatePackages();
        try {
            getConfiguredTarget("@foo//:bar");
            Assert.fail();
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(("There is already a built-in attribute 'name' " + "which cannot be overridden"));
        }
    }

    @Test
    public void testMultipleLoadSameExtension() throws Exception {
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("load('//:def.bzl', 'f1')").add("f1()").add("load('//:def.bzl', 'f2')").add("f2()").add("load('//:def.bzl', 'f1')").add("f1()").add("local_repository(name = 'foo', path = '')").build());
        scratch.file(rootDirectory.getRelative("BUILD").getPathString(), "filegroup(name = 'bar', srcs = [])");
        scratch.file(rootDirectory.getRelative("def.bzl").getPathString(), "def f1():", "  print('f1')", "", "def f2():", "  print('f2')");
        invalidatePackages();
        // Just request the last external repository to force the whole loading.
        getConfiguredTarget("@foo//:bar");
    }

    @Test
    public void testBindAndRepoSameNameDoesNotCrash() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("/repo2/data.txt", "data");
        scratch.file("/repo2/BUILD", "load('@//:rulez.bzl', 'r')", "r(name = 'z')");
        scratch.file("/repo2/WORKSPACE");
        scratch.file("rulez.bzl", "def _impl(ctx):", "    pass", "r = rule(_impl, attrs = { 'deps' : attr.label_list() })");
        scratch.file("BUILD", "load(':rulez.bzl', 'r')", "r(name = 'x', deps = ['//external:zlib'])");
        scratch.overwriteFile(rootDirectory.getRelative("WORKSPACE").getPathString(), new ImmutableList.Builder<String>().addAll(analysisMock.getWorkspaceContents(mockToolsConfig)).add("bind(name = 'zlib', actual = '@zlib//:z')").add("local_repository(name = 'zlib', path = '/repo2')").build());
        invalidatePackages();
        getConfiguredTarget("//:x");
        assertContainsEvent(("target '//external:zlib' is not visible from target '//:x'. " + ("Check the visibility declaration of the former target if you think the " + "dependency is legitimate")));
    }
}

