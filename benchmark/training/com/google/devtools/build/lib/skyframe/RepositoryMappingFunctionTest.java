/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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


import RepositoryName.MAIN;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.SkyKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RepositoryMappingFunction} and {@link RepositoryMappingValue}.
 */
@RunWith(JUnit4.class)
public class RepositoryMappingFunctionTest extends BuildViewTestCase {
    @Test
    public void testSimpleMapping() throws Exception {
        scratch.overwriteFile("WORKSPACE", "workspace(name = 'good')", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", "    repo_mapping = {'@a' : '@b'},", ")");
        RepositoryName name = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey = RepositoryMappingValue.key(name);
        EvaluationResult<RepositoryMappingValue> result = eval(skyKey);
        assertThat(result.hasError()).isFalse();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasEntryThat(skyKey).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@a"), RepositoryName.create("@b"))));
    }

    @Test
    public void testMultipleRepositoriesWithMapping() throws Exception {
        scratch.overwriteFile("WORKSPACE", "workspace(name = 'good')", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", "    repo_mapping = {'@a' : '@b'},", ")", "local_repository(", "    name = 'other_remote_repo',", "    path = '/other_remote_repo',", "    repo_mapping = {'@x' : '@y'},", ")");
        RepositoryName name1 = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey1 = RepositoryMappingValue.key(name1);
        RepositoryName name2 = RepositoryName.create("@other_remote_repo");
        SkyKey skyKey2 = RepositoryMappingValue.key(name2);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey1)).hasEntryThat(skyKey1).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@a"), RepositoryName.create("@b"))));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey2)).hasEntryThat(skyKey2).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@x"), RepositoryName.create("@y"))));
    }

    @Test
    public void testRepositoryWithMultipleMappings() throws Exception {
        scratch.overwriteFile("WORKSPACE", "workspace(name = 'good')", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", "    repo_mapping = {'@a' : '@b', '@x' : '@y'},", ")");
        RepositoryName name = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey = RepositoryMappingValue.key(name);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey)).hasEntryThat(skyKey).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@a"), RepositoryName.create("@b"), RepositoryName.create("@x"), RepositoryName.create("@y"))));
    }

    @Test
    public void testErrorWithMapping() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.overwriteFile("WORKSPACE", "workspace(name = 'good')", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", "    repo_mapping = {'x' : '@b'},", ")");
        RepositoryName name = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey = RepositoryMappingValue.key(name);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey)).hasErrorEntryForKeyThat(skyKey).hasExceptionThat().isInstanceOf(NoSuchPackageException.class);
        assertContainsEvent("invalid repository name 'x': workspace names must start with '@'");
    }

    @Test
    public void testDefaultMainRepoNameInMapping() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_remap_main_repo");
        scratch.overwriteFile("WORKSPACE", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", "    repo_mapping = {},", ")");
        RepositoryName name = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey = RepositoryMappingValue.key(name);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey)).hasEntryThat(skyKey).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.createFromValidStrippedName(TestConstants.WORKSPACE_NAME), MAIN)));
    }

    @Test
    public void testExplicitMainRepoNameInMapping() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_remap_main_repo");
        scratch.overwriteFile("WORKSPACE", "workspace(name = 'good')", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo',", ")");
        RepositoryName name = RepositoryName.create("@a_remote_repo");
        SkyKey skyKey = RepositoryMappingValue.key(name);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(eval(skyKey)).hasEntryThat(skyKey).isEqualTo(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@good"), MAIN)));
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        new EqualsTester().addEqualityGroup(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@foo"), RepositoryName.create("@bar"))), RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@foo"), RepositoryName.create("@bar")))).addEqualityGroup(RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@fizz"), RepositoryName.create("@buzz"))), RepositoryMappingValue.withMapping(ImmutableMap.of(RepositoryName.create("@fizz"), RepositoryName.create("@buzz")))).testEquals();
    }
}

