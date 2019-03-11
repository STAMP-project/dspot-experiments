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


import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.syntax.BuildFileAST;
import com.google.devtools.build.skyframe.SkyFunctionException;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for WorkspaceASTFunction.
 */
@RunWith(JUnit4.class)
public class WorkspaceASTFunctionTest extends BuildViewTestCase {
    private WorkspaceASTFunction astSkyFunc;

    private WorkspaceFileFunctionTest.FakeFileValue fakeWorkspaceFileValue;

    @Test
    public void testSplitASTNoLoad() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1");
        assertThat(asts).hasSize(1);
        assertThat(asts.get(0).getStatements()).hasSize(1);
    }

    @Test
    public void testSplitASTOneLoadAtTop() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("load('//:foo.bzl', 'bar')", "foo_bar = 1");
        assertThat(asts).hasSize(1);
        assertThat(asts.get(0).getStatements()).hasSize(2);
    }

    @Test
    public void testSplitASTOneLoad() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1", "load('//:foo.bzl', 'bar')");
        assertThat(asts).hasSize(2);
        assertThat(asts.get(0).getStatements()).hasSize(1);
        assertThat(asts.get(1).getStatements()).hasSize(1);
    }

    @Test
    public void testSplitASTTwoSuccessiveLoads() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1", "load('//:foo.bzl', 'bar')", "load('//:bar.bzl', 'foo')");
        assertThat(asts).hasSize(2);
        assertThat(asts.get(0).getStatements()).hasSize(1);
        assertThat(asts.get(1).getStatements()).hasSize(2);
    }

    @Test
    public void testSplitASTTwoSucessiveLoadsWithNonLoadStatement() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1", "load('//:foo.bzl', 'bar')", "load('//:bar.bzl', 'foo')", "local_repository(name = 'foobar', path = '/bar/foo')");
        assertThat(asts).hasSize(2);
        assertThat(asts.get(0).getStatements()).hasSize(1);
        assertThat(asts.get(1).getStatements()).hasSize(3);
    }

    @Test
    public void testSplitASTThreeLoadsThreeSegments() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1", "load('//:foo.bzl', 'bar')", "load('//:bar.bzl', 'foo')", "local_repository(name = 'foobar', path = '/bar/foo')", "load('@foobar//:baz.bzl', 'bleh')");
        assertThat(asts).hasSize(3);
        assertThat(asts.get(0).getStatements()).hasSize(1);
        assertThat(asts.get(1).getStatements()).hasSize(3);
        assertThat(asts.get(2).getStatements()).hasSize(1);
    }

    @Test
    public void testSplitASTThreeLoadsThreeSegmentsWithContent() throws SkyFunctionException, IOException, InterruptedException {
        List<BuildFileAST> asts = getASTs("foo_bar = 1", "load('//:foo.bzl', 'bar')", "load('//:bar.bzl', 'foo')", "local_repository(name = 'foobar', path = '/bar/foo')", "load('@foobar//:baz.bzl', 'bleh')", "bleh()");
        assertThat(asts).hasSize(3);
        assertThat(asts.get(0).getStatements()).hasSize(1);
        assertThat(asts.get(1).getStatements()).hasSize(3);
        assertThat(asts.get(2).getStatements()).hasSize(2);
    }
}

