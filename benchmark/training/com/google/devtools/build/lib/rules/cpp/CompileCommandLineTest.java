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
package com.google.devtools.build.lib.rules.cpp;


import CppRuleClasses.UNFILTERED_COMPILE_FLAGS_FEATURE_NAME;
import com.google.devtools.build.lib.analysis.RuleContext;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.rules.cpp.CompileCommandLine}, for example testing
 * the ordering of individual command line flags, or that command line is emitted differently
 * subject to the presence of certain build variables. Also used to test migration logic (removing
 * hardcoded flags and expressing them using feature configuration.
 */
@RunWith(JUnit4.class)
public class CompileCommandLineTest extends BuildViewTestCase {
    private RuleContext ruleContext;

    @Test
    public void testFeatureConfigurationCommandLineIsUsed() throws Exception {
        CompileCommandLine compileCommandLine = makeCompileCommandLineBuilder().setFeatureConfiguration(CompileCommandLineTest.getMockFeatureConfiguration(ruleContext, "", "action_config {", "  config_name: 'c++-compile'", "  action_name: 'c++-compile'", "  implies: 'some_foo_feature'", "  tool {", "    tool_path: 'foo/bar/DUMMY_COMPILER'", "  }", "}", "feature {", "  name: 'some_foo_feature'", "  flag_set {", "     action: 'c++-compile'", "     flag_group {", "       flag: '-some_foo_flag'", "    }", "  }", "}")).build();
        assertThat(/* overwrittenVariables= */
        compileCommandLine.getArguments(null)).contains("-some_foo_flag");
    }

    @Test
    public void testUnfilteredFlagsAreNotFiltered() throws Exception {
        List<String> actualCommandLine = getCompileCommandLineWithCoptsFilter(UNFILTERED_COMPILE_FLAGS_FEATURE_NAME);
        assertThat(actualCommandLine).contains("-i_am_a_flag");
    }

    @Test
    public void testNonUnfilteredFlagsAreFiltered() throws Exception {
        List<String> actualCommandLine = getCompileCommandLineWithCoptsFilter("filtered_flags");
        assertThat(actualCommandLine).doesNotContain("-i_am_a_flag");
    }
}

