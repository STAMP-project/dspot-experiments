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
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.actions.FileValue;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.packages.WorkspaceFileValue;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link WorkspaceFileFunction}.
 */
@RunWith(JUnit4.class)
public class WorkspaceFileFunctionTest extends BuildViewTestCase {
    private WorkspaceFileFunction workspaceSkyFunc;

    private ExternalPackageFunction externalSkyFunc;

    private WorkspaceASTFunction astSkyFunc;

    private WorkspaceFileFunctionTest.FakeFileValue fakeWorkspaceFileValue;

    static class FakeFileValue extends FileValue {
        private boolean exists;

        private long size;

        FakeFileValue() {
            super();
            exists = true;
            size = 0L;
        }

        @Override
        public RootedPath realRootedPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileStateValue realFileStateValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean exists() {
            return exists;
        }

        @Override
        public ImmutableList<RootedPath> logicalChainDuringResolution() {
            throw new UnsupportedOperationException();
        }

        void setExists(boolean exists) {
            this.exists = exists;
        }

        @Override
        public long getSize() {
            return size;
        }

        void setSize(long size) {
            this.size = size;
        }
    }

    // Dummy hamcrest matcher that match the function name of a skykey
    static class SkyKeyMatchers extends BaseMatcher<SkyKey> {
        private final SkyFunctionName functionName;

        public SkyKeyMatchers(SkyFunctionName functionName) {
            this.functionName = functionName;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof SkyKey) {
                return functionName().equals(functionName);
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
        }
    }

    @Test
    public void testImportToChunkMapSimple() throws Exception {
        scratch.file("a.bzl", "a = 'a'");
        scratch.file("b.bzl", "b = 'b'");
        scratch.file("BUILD", "");
        RootedPath workspace = createWorkspaceFile("WORKSPACE", "workspace(name = 'good')", "load('//:a.bzl', 'a')", "x = 1  #for chunk break", "load('//:b.bzl', 'b')");
        SkyKey key1 = WorkspaceFileValue.key(workspace, 1);
        EvaluationResult<WorkspaceFileValue> result1 = eval(key1);
        WorkspaceFileValue value1 = result1.get(key1);
        assertThat(value1.getImportToChunkMap()).containsEntry("//:a.bzl", 1);
        SkyKey key2 = WorkspaceFileValue.key(workspace, 2);
        EvaluationResult<WorkspaceFileValue> result2 = eval(key2);
        WorkspaceFileValue value2 = result2.get(key2);
        assertThat(value2.getImportToChunkMap()).containsEntry("//:a.bzl", 1);
        assertThat(value2.getImportToChunkMap()).containsEntry("//:b.bzl", 2);
    }

    @Test
    public void testImportToChunkMapDoesNotOverrideDuplicate() throws Exception {
        scratch.file("a.bzl", "a = 'a'");
        scratch.file("BUILD", "");
        RootedPath workspace = createWorkspaceFile("WORKSPACE", "workspace(name = 'good')", "load('//:a.bzl', 'a')", "x = 1  #for chunk break", "load('//:a.bzl', 'a')");
        SkyKey key1 = WorkspaceFileValue.key(workspace, 1);
        EvaluationResult<WorkspaceFileValue> result1 = eval(key1);
        WorkspaceFileValue value1 = result1.get(key1);
        assertThat(value1.getImportToChunkMap()).containsEntry("//:a.bzl", 1);
        SkyKey key2 = WorkspaceFileValue.key(workspace, 2);
        EvaluationResult<WorkspaceFileValue> result2 = eval(key2);
        WorkspaceFileValue value2 = result2.get(key2);
        assertThat(value2.getImportToChunkMap()).containsEntry("//:a.bzl", 1);
        assertThat(value2.getImportToChunkMap()).doesNotContainEntry("//:a.bzl", 2);
    }

    @Test
    public void testRepositoryMappingInChunks() throws Exception {
        scratch.file("b.bzl", "b = 'b'");
        scratch.file("BUILD", "");
        RootedPath workspace = createWorkspaceFile("WORKSPACE", "workspace(name = 'good')", "local_repository(name = 'a', path = '../a', repo_mapping = {'@x' : '@y'})", "load('//:b.bzl', 'b')", "local_repository(name = 'b', path = '../b', repo_mapping = {'@x' : '@y'})");
        RepositoryName a = RepositoryName.create("@a");
        RepositoryName b = RepositoryName.create("@b");
        RepositoryName x = RepositoryName.create("@x");
        RepositoryName y = RepositoryName.create("@y");
        SkyKey key0 = WorkspaceFileValue.key(workspace, 0);
        EvaluationResult<WorkspaceFileValue> result0 = eval(key0);
        WorkspaceFileValue value0 = result0.get(key0);
        assertThat(value0.getRepositoryMapping()).containsEntry(a, ImmutableMap.of(x, y));
        SkyKey key1 = WorkspaceFileValue.key(workspace, 1);
        EvaluationResult<WorkspaceFileValue> result1 = eval(key1);
        WorkspaceFileValue value1 = result1.get(key1);
        assertThat(value1.getRepositoryMapping()).containsEntry(a, ImmutableMap.of(x, y));
        assertThat(value1.getRepositoryMapping()).containsEntry(b, ImmutableMap.of(x, y));
    }

    @Test
    public void testInvalidRepo() throws Exception {
        RootedPath workspacePath = createWorkspaceFile("workspace(name = 'foo$')");
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(ExternalPackageFunction.key(workspacePath), getEnv())));
        Package pkg = value.getPackage();
        assertThat(containsErrors()).isTrue();
        MoreAsserts.assertContainsEvent(getEvents(), "foo$ is not a legal workspace name");
    }

    @Test
    public void testBindFunction() throws Exception {
        String[] lines = new String[]{ "bind(name = 'foo/bar',", "actual = '//foo:bar')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        SkyKey key = ExternalPackageFunction.key(workspacePath);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(key, getEnv())));
        Package pkg = value.getPackage();
        assertThat(getLabelMapping(pkg, "foo/bar")).isEqualTo(Label.parseAbsolute("//foo:bar", ImmutableMap.of()));
        MoreAsserts.assertNoEvents(getEvents());
    }

    @Test
    public void testBindArgsReversed() throws Exception {
        String[] lines = new String[]{ "bind(actual = '//foo:bar', name = 'foo/bar')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        SkyKey key = ExternalPackageFunction.key(workspacePath);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(key, getEnv())));
        Package pkg = value.getPackage();
        assertThat(getLabelMapping(pkg, "foo/bar")).isEqualTo(Label.parseAbsolute("//foo:bar", ImmutableMap.of()));
        MoreAsserts.assertNoEvents(getEvents());
    }

    @Test
    public void testNonExternalBinding() throws Exception {
        // name must be a valid label name.
        String[] lines = new String[]{ "bind(name = 'foo:bar', actual = '//bar/baz')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(ExternalPackageFunction.key(workspacePath), getEnv())));
        Package pkg = value.getPackage();
        assertThat(containsErrors()).isTrue();
        MoreAsserts.assertContainsEvent(getEvents(), "target names may not contain ':'");
    }

    @Test
    public void testWorkspaceFileParsingError() throws Exception {
        // //external:bar:baz is not a legal package.
        String[] lines = new String[]{ "bind(name = 'foo/bar', actual = '//external:bar:baz')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(ExternalPackageFunction.key(workspacePath), getEnv())));
        Package pkg = value.getPackage();
        assertThat(containsErrors()).isTrue();
        MoreAsserts.assertContainsEvent(getEvents(), "target names may not contain ':'");
    }

    @Test
    public void testNoWorkspaceFile() throws Exception {
        // Even though the WORKSPACE exists, Skyframe thinks it doesn't, so it doesn't.
        String[] lines = new String[]{ "bind(name = 'foo/bar', actual = '//foo:bar')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        fakeWorkspaceFileValue.setExists(false);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(ExternalPackageFunction.key(workspacePath), getEnv())));
        Package pkg = value.getPackage();
        assertThat(containsErrors()).isFalse();
        MoreAsserts.assertNoEvents(getEvents());
    }

    @Test
    public void testListBindFunction() throws Exception {
        String[] lines = new String[]{ "L = ['foo', 'bar']", "bind(name = '%s/%s' % (L[0], L[1]),", "actual = '//foo:bar')" };
        RootedPath workspacePath = createWorkspaceFile(lines);
        SkyKey key = ExternalPackageFunction.key(workspacePath);
        PackageValue value = ((PackageValue) (externalSkyFunc.compute(key, getEnv())));
        Package pkg = value.getPackage();
        assertThat(getLabelMapping(pkg, "foo/bar")).isEqualTo(Label.parseAbsolute("//foo:bar", ImmutableMap.of()));
        MoreAsserts.assertNoEvents(getEvents());
    }
}

