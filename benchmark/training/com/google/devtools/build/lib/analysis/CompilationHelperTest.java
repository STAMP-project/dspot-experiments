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
package com.google.devtools.build.lib.analysis;


import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.util.AnalysisTestUtil;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for the {@link CompilationHelper} class.
 */
@RunWith(JUnit4.class)
public class CompilationHelperTest extends BuildViewTestCase {
    private AnalysisTestUtil.CollectingAnalysisEnvironment analysisEnvironment;

    /**
     * Tests that duplicate calls to
     * {@link com.google.devtools.build.lib.analysis.CompilationHelper#getAggregatingMiddleman}
     * with identical parameters return the same artifact.
     */
    @Test
    public void testDuplicateCallsReturnSameObject() throws Exception {
        ConfiguredTargetAndData rule = scratchConfiguredTargetAndData("package", "a", ("cc_binary(name = 'a'," + "    srcs = ['a.cc'])"));
        List<Artifact> middleman1 = getAggregatingMiddleman(rule, false);
        assertThat(middleman1).hasSize(1);
        List<Artifact> middleman2 = getAggregatingMiddleman(rule, false);
        assertThat(middleman2).hasSize(1);
        assertThat(middleman2.get(0)).isEqualTo(middleman1.get(0));
    }

    /**
     * Tests that
     * {@link com.google.devtools.build.lib.analysis.CompilationHelper#getAggregatingMiddleman}
     * returns distinct artifacts even when called with identical rules, depending on
     * whether solib symlink are created.
     */
    @Test
    public void testMiddlemanAndSolibMiddlemanAreDistinct() throws Exception {
        ConfiguredTargetAndData rule = scratchConfiguredTargetAndData("package", "liba.so", "cc_binary(name = 'liba.so', srcs = ['a.cc'], linkshared = 1)");
        List<Artifact> middleman = getAggregatingMiddleman(rule, false);
        assertThat(middleman).hasSize(1);
        List<Artifact> middlemanWithSymlinks = getAggregatingMiddleman(rule, true);
        assertThat(middlemanWithSymlinks).hasSize(1);
        assertThat(middlemanWithSymlinks.get(0)).isNotSameAs(middleman.get(0));
    }

    /**
     * Regression test: tests that Python CPU configurations are taken into account
     * when generating a rule's aggregating middleman, so that otherwise equivalent rules can sustain
     * distinct middlemen.
     */
    @Test
    public void testPythonCcConfigurations() throws Exception {
        setupJavaPythonCcConfigurationFiles();
        // Equivalent cc / Python configurations:
        ConfiguredTargetAndData ccRuleA = getConfiguredTargetAndData("//foo:liba.so");
        List<Artifact> middleman1 = getAggregatingMiddleman(ccRuleA, true);
        try {
            ConfiguredTargetAndData ccRuleB = getConfiguredTargetAndData("//foo:libb.so");
            getAggregatingMiddleman(ccRuleB, true);
            analysisEnvironment.registerWith(getMutableActionGraph());
            Assert.fail("Expected ActionConflictException due to same middleman artifact with different files");
        } catch (ActionsTestUtil.UncheckedActionConflictException e) {
            // Expected failure: same "purpose" and root directory sent to the middleman generator
            // (which results in the same output artifact), but different rules / middleman inputs.
        }
        // This should succeed because the py_binary's middleman is under the Python configuration's
        // internal directory, while the cc_binary's middleman is under the cc config's directory,
        // and both configurations are the same.
        ConfiguredTargetAndData pyRuleB = getConfiguredTargetAndDataDirectPrerequisite(getConfiguredTargetAndData("//foo:c"), "//foo:libb.so");
        List<Artifact> middleman2 = getAggregatingMiddleman(pyRuleB, true);
        assertThat(Iterables.getOnlyElement(middleman2).getExecPathString()).isEqualTo(Iterables.getOnlyElement(middleman1).getExecPathString());
    }

    /**
     * Regression test: tests that Java CPU configurations are taken into account when
     * generating a rule's aggregating middleman, so that otherwise equivalent rules can sustain
     * distinct middlemen.
     */
    @Test
    public void testJavaCcConfigurations() throws Exception {
        setupJavaPythonCcConfigurationFiles();
        // Equivalent cc / Java configurations:
        ConfiguredTargetAndData ccRuleA = getConfiguredTargetAndData("//foo:liba.so");
        List<Artifact> middleman1 = getAggregatingMiddleman(ccRuleA, true);
        try {
            ConfiguredTargetAndData ccRuleB = getConfiguredTargetAndData("//foo:libb.so");
            getAggregatingMiddleman(ccRuleB, true);
            analysisEnvironment.registerWith(getMutableActionGraph());
            Assert.fail("Expected ActionConflictException due to same middleman artifact with different files");
        } catch (ActionsTestUtil.UncheckedActionConflictException e) {
            // Expected failure: same "purpose" and root directory sent to the middleman generator
            // (which results in the same output artifact), but different rules / middleman inputs.
        }
        // This should succeed because the java_binary's middleman is under the Java configuration's
        // internal directory, while the cc_binary's middleman is under the cc config's directory.
        ConfiguredTargetAndData javaRuleB = getConfiguredTargetAndDataDirectPrerequisite(getConfiguredTargetAndData("//foo:d"), "//foo:libb.so");
        List<Artifact> middleman2 = getAggregatingMiddleman(javaRuleB, false);
        assertThat(Iterables.getOnlyElement(middleman1).getExecPathString().equals(Iterables.getOnlyElement(middleman2).getExecPathString())).isFalse();
    }
}

