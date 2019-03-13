/**
 * Copyright 2006 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.filegroup;


import Filegroup.ILLEGAL_OUTPUT_GROUP_ERROR;
import JavaSemantics.SOURCE_JARS_OUTPUT_GROUP;
import OutputGroupInfo.HIDDEN_TOP_LEVEL;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.configuredtargets.FileConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.FileType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Filegroup}.
 */
@RunWith(JUnit4.class)
public class FilegroupConfiguredTargetTest extends BuildViewTestCase {
    @Test
    public void testGroup() throws Exception {
        scratch.file("nevermore/BUILD", "filegroup(name  = 'staticdata',", "          srcs = ['staticdata/spam.txt', 'staticdata/good.txt'])");
        ConfiguredTarget groupTarget = getConfiguredTarget("//nevermore:staticdata");
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(groupTarget))).containsExactly("nevermore/staticdata/spam.txt", "nevermore/staticdata/good.txt");
    }

    @Test
    public void testDependencyGraph() throws Exception {
        scratch.file("java/com/google/test/BUILD", "java_binary(name  = 'test_app',", "    resources = [':data'],", "    create_executable = 0,", "    srcs  = ['InputFile.java', 'InputFile2.java'])", "filegroup(name  = 'data',", "          srcs = ['b.txt', 'a.txt'])");
        FileConfiguredTarget appOutput = getFileConfiguredTarget("//java/com/google/test:test_app.jar");
        assertThat(actionsTestUtil().predecessorClosureOf(appOutput.getArtifact(), FileType.of(".txt"))).isEqualTo("b.txt a.txt");
    }

    @Test
    public void testEmptyGroupIsAnOk() throws Exception {
        scratchConfiguredTarget("empty", "empty", "filegroup(name='empty', srcs=[])");
    }

    @Test
    public void testEmptyGroupInGenruleIsOk() throws Exception {
        scratchConfiguredTarget("empty", "genempty", "filegroup(name='empty', srcs=[])", "genrule(name='genempty', tools=[':empty'], outs=['nothing'], cmd='touch $@')");
    }

    @Test
    public void testFileCanBeSrcsOfMultipleRules() throws Exception {
        writeTest();
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(getConfiguredTarget("//test:a")))).containsExactly("test/a.txt");
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(getConfiguredTarget("//test:b")))).containsExactly("test/a.txt");
    }

    @Test
    public void testRuleCanBeSrcsOfOtherRule() throws Exception {
        writeTest();
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(getConfiguredTarget("//test:c")))).containsExactly("test/a.txt", "test/b.txt");
    }

    @Test
    public void testOtherPackageCanBeSrcsOfRule() throws Exception {
        writeTest();
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(getConfiguredTarget("//test:d")))).containsExactly("another/another.txt");
    }

    @Test
    public void testIsNotExecutable() throws Exception {
        scratch.file("x/BUILD", "filegroup(name = 'not_exec_two_files', srcs = ['bin', 'bin.sh'])");
        assertThat(getExecutable("//x:not_exec_two_files")).isNull();
    }

    @Test
    public void testIsExecutable() throws Exception {
        scratch.file("x/BUILD", "filegroup(name = 'exec', srcs = ['bin'])");
        assertThat(getExecutable("//x:exec").getExecPath().getPathString()).isEqualTo("x/bin");
    }

    @Test
    public void testNoDuplicate() throws Exception {
        scratch.file("x/BUILD", "filegroup(name = 'a', srcs = ['file'])", "filegroup(name = 'b', srcs = ['file'])", "filegroup(name = 'c', srcs = [':a', ':b'])");
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(getConfiguredTarget("//x:c")))).containsExactly("x/file");
    }

    @Test
    public void testGlobMatchesRuleOutputsInsteadOfFileWithTheSameName() throws Exception {
        scratch.file("pkg/file_or_rule");
        scratch.file("pkg/a.txt");
        ConfiguredTarget target = scratchConfiguredTarget("pkg", "my_rule", "filegroup(name = 'file_or_rule', srcs = ['a.txt'])", "filegroup(name = 'my_rule', srcs = glob(['file_or_rule']))");
        assertThat(ActionsTestUtil.baseArtifactNames(getFilesToBuild(target))).containsExactly("a.txt");
    }

    @Test
    public void testOutputGroupExtractsCorrectArtifacts() throws Exception {
        scratch.file("pkg/a.java");
        scratch.file("pkg/b.java");
        scratch.file("pkg/in_ouput_group_a");
        scratch.file("pkg/in_ouput_group_b");
        scratch.file("pkg/BUILD", "java_library(name='lib_a', srcs=['a.java'])", "java_library(name='lib_b', srcs=['b.java'])", ("filegroup(name='group', srcs=[':lib_a', ':lib_b']," + (String.format("output_group='%s')", SOURCE_JARS_OUTPUT_GROUP))));
        ConfiguredTarget group = getConfiguredTarget("//pkg:group");
        assertThat(ActionsTestUtil.prettyArtifactNames(getFilesToBuild(group))).containsExactly("pkg/liblib_a-src.jar", "pkg/liblib_b-src.jar");
    }

    @Test
    public void testErrorForIllegalOutputGroup() throws Exception {
        scratch.file("pkg/a.cc");
        scratch.file("pkg/BUILD", "cc_library(name='lib_a', srcs=['a.cc'])", String.format("filegroup(name='group', srcs=[':lib_a'], output_group='%s')", HIDDEN_TOP_LEVEL));
        try {
            getConfiguredTarget("//pkg:group");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(ILLEGAL_OUTPUT_GROUP_ERROR, HIDDEN_TOP_LEVEL));
        }
    }
}

