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
package com.google.devtools.build.lib.query2.engine;


import Setting.NO_HOST_DEPS;
import Setting.NO_IMPLICIT_DEPS;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.transitions.SplitTransition;
import com.google.devtools.build.lib.analysis.test.TestConfiguration.TestOptions;
import com.google.devtools.build.lib.analysis.util.MockRule;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.vfs.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ConfiguredTargetQueryEnvironment}.
 * TODO(juliexxia): separate out tests in this file into one test per tested functionality.
 */
@RunWith(JUnit4.class)
public class ConfiguredTargetQueryTest extends PostAnalysisQueryTest<ConfiguredTarget> {
    @Test
    public void testConfigurationRespected() throws Exception {
        writeBuildFilesWithConfigurableAttributesUnconditionally();
        assertThat(eval("deps(//configurable:main) ^ //configurable:adep")).isEmpty();
        assertThat(eval("deps(//configurable:main) ^ //configurable:defaultdep")).hasSize(1);
    }

    @Test
    public void testLabelFunction_getsCorrectConfigurations() throws Exception {
        setUpLabelsFunctionTests();
        // (Test that you can use the labels function without an error (b/112593112)).
        // Note - 'labels' as a command for cquery is a slight misnomer since it always prints
        // labels AND configurations. But still a helpful function so oh well.
        assertThat(Iterables.getOnlyElement(eval("labels('patch_dep', //test:my_rule)"))).isNotNull();
    }

    @Test
    public void testLabelFunction_getCorrectlyConfiguredDeps() throws Exception {
        setUpLabelsFunctionTests();
        // Test that this retrieves the correctly configured version(s) of the dep(s).
        ConfiguredTarget patchDep = Iterables.getOnlyElement(eval("labels('patch_dep', //test:my_rule)"));
        ConfiguredTarget myRule = Iterables.getOnlyElement(eval("//test:my_rule"));
        String targetConfiguration = myRule.getConfigurationChecksum();
        assertThat(patchDep.getConfigurationChecksum()).doesNotMatch(targetConfiguration);
    }

    @Test
    public void testLabelsFunction_splitTransitionAttribute() throws Exception {
        setUpLabelsFunctionTests();
        ConfiguredTarget myRule = Iterables.getOnlyElement(eval("//test:my_rule"));
        String targetConfiguration = myRule.getConfigurationChecksum();
        Collection<ConfiguredTarget> splitDeps = eval("labels('split_dep', //test:my_rule)");
        assertThat(splitDeps).hasSize(2);
        for (ConfiguredTarget ct : splitDeps) {
            assertThat(ct.getConfigurationChecksum()).doesNotMatch(targetConfiguration);
        }
    }

    @Test
    public void testLabelsFunction_labelListAttribute() throws Exception {
        setUpLabelsFunctionTests();
        ConfiguredTarget myRule = Iterables.getOnlyElement(eval("//test:my_rule"));
        String targetConfiguration = myRule.getConfigurationChecksum();
        // Test that this works for label_lists as well.
        Set<ConfiguredTarget> deps = eval("labels('patch_dep_list', //test:my_rule)");
        assertThat(deps).hasSize(2);
        for (ConfiguredTarget ct : deps) {
            assertThat(ct.getConfigurationChecksum()).doesNotMatch(targetConfiguration);
        }
    }

    @Test
    public void testLabelsFunction_errorsOnBadAttribute() throws Exception {
        setUpLabelsFunctionTests();
        ConfiguredTarget myRule = Iterables.getOnlyElement(eval("//test:my_rule"));
        String targetConfiguration = myRule.getConfigurationChecksum();
        // Test that the proper error is thrown when requesting an attribute that doesn't exist.
        assertThat(evalThrows("labels('fake_attr', //test:my_rule)", true)).isEqualTo(String.format(("in 'fake_attr' of rule //test:my_rule:  ConfiguredTarget(//test:my_rule, %s) " + "of type rule_with_transitions does not have attribute 'fake_attr'"), targetConfiguration));
    }

    @Test
    public void testAlias_filtering() throws Exception {
        MockRule ruleWithHostDep = () -> MockRule.define("rule_with_host_dep", attr("host_dep", LABEL).allowedFileTypes(FileTypeSet.ANY_FILE).cfg(HostTransition.INSTANCE), attr("$impl_dep", LABEL).allowedFileTypes(FileTypeSet.ANY_FILE).value(Label.parseAbsoluteUnchecked("//test:other")));
        MockRule simpleRule = () -> MockRule.define("simple_rule");
        helper.useRuleClassProvider(setRuleClassProviders(ruleWithHostDep, simpleRule).build());
        writeFile("test/BUILD", "alias(name = 'other_my_rule', actual = ':my_rule')", "rule_with_host_dep(name = 'my_rule', host_dep = ':host_dep')", "alias(name = 'other_host_dep', actual = ':host_dep')", "simple_rule(name='host_dep')", "alias(name = 'other_impl_dep', actual = 'impl_dep')", "simple_rule(name='impl_dep')");
        ConfiguredTarget other = Iterables.getOnlyElement(eval("//test:other_my_rule"));
        ConfiguredTarget myRule = Iterables.getOnlyElement(eval("//test:my_rule"));
        // Note: {@link AliasConfiguredTarget#getLabel} returns the label of the "actual" value not the
        // label of the alias.
        assertThat(other.getLabel()).isEqualTo(myRule.getLabel());
        // Regression test for b/73496081 in which alias-ed configured targets were skipping filtering.
        helper.setQuerySettings(NO_HOST_DEPS, NO_IMPLICIT_DEPS);
        assertThat(evalToListOfStrings("deps(//test:other_my_rule)-//test:other_my_rule")).isEqualTo(evalToListOfStrings("//test:my_rule"));
    }

    @Test
    public void testTopLevelTransition() throws Exception {
        MockRule ruleClassTransition = () -> MockRule.define("rule_class_transition", ( builder, env) -> builder.cfg(new TestArgPatchTransition("SET BY PATCH")).build());
        helper.useRuleClassProvider(setRuleClassProviders(ruleClassTransition).build());
        helper.setUniverseScope("//test:rule_class");
        writeFile("test/BUILD", "rule_class_transition(name='rule_class')");
        Set<ConfiguredTarget> ruleClass = eval("//test:rule_class");
        TestOptions testOptions = getConfiguration(Iterables.getOnlyElement(ruleClass)).getOptions().get(TestOptions.class);
        assertThat(testOptions.testArguments).containsExactly("SET BY PATCH");
    }

    /**
     * SplitTransition on --test_arg
     */
    public static class TestArgSplitTransition implements SplitTransition {
        String toOption1;

        String toOption2;

        public TestArgSplitTransition(String toOption1, String toOptions2) {
            this.toOption1 = toOption1;
            this.toOption2 = toOptions2;
        }

        @Override
        public List<BuildOptions> split(BuildOptions options) {
            BuildOptions result1 = options.clone();
            BuildOptions result2 = options.clone();
            result1.get(TestOptions.class).testArguments = Collections.singletonList(toOption1);
            result2.get(TestOptions.class).testArguments = Collections.singletonList(toOption2);
            return ImmutableList.of(result1, result2);
        }
    }

    @Test
    public void testConfig() throws Exception {
        createConfigRulesAndBuild();
        assertThat(eval("config(//test:target_dep,target)")).isEqualTo(eval("//test:target_dep"));
        assertThat(evalThrows("config(//test:target_dep,host)", true)).isEqualTo("No target (in) //test:target_dep could be found in the 'host' configuration");
        getHelper().setWholeTestUniverseScope("test:my_rule");
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("config(//test:host_dep, host)"))).isHostConfiguration()).isTrue();
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("config(//test:dep, host)"))).isHostConfiguration()).isTrue();
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("config(//test:dep, target)")))).isNotNull();
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("config(//test:dep, target)"))).isHostConfiguration()).isFalse();
    }

    @Test
    public void testConfig_nullConfig() throws Exception {
        writeFile("test/BUILD", "java_library(name='my_java',", "  srcs = ['foo.java'],", ")");
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("config(//test:foo.java,null)")))).isNull();
    }

    @Test
    public void testConfig_badConfig() throws Exception {
        createConfigRulesAndBuild();
        assertThat(evalThrows("config(//test:my_rule,foo)", true)).isEqualTo("the second argument of the config function must be 'target', 'host', or 'null'");
    }

    @Test
    public void testRecursiveTargetPatternNeverThrowsError() throws Exception {
        Path parent = getHelper().getScratch().file("parent/BUILD", "sh_library(name = 'parent')").getParentDirectory();
        Path child = parent.getRelative("child");
        child.createDirectory();
        Path badBuild = child.getRelative("BUILD");
        badBuild.createSymbolicLink(badBuild);
        helper.setKeepGoing(true);
        assertThat(eval("//parent:all")).isEqualTo(eval("//parent:parent"));
        helper.setKeepGoing(false);
        getHelper().turnOffFailFast();
        assertThat(evalThrows("//parent/...", true)).isEqualTo(("no such package 'parent/child': Symlink cycle detected while trying to " + "find BUILD file /workspace/parent/child/BUILD"));
    }

    @Override
    @Test
    public void testMultipleTopLevelConfigurations_nullConfigs() throws Exception {
        writeFile("test/BUILD", "java_library(name='my_java',", "  srcs = ['foo.java'],", ")");
        Set<ConfiguredTarget> result = eval("//test:my_java+//test:foo.java");
        assertThat(result).hasSize(2);
        Iterator<ConfiguredTarget> resultIterator = result.iterator();
        ConfiguredTarget first = resultIterator.next();
        if (first.getLabel().toString().equals("//test:foo.java")) {
            assertThat(getConfiguration(first)).isNull();
            assertThat(getConfiguration(resultIterator.next())).isNotNull();
        } else {
            assertThat(getConfiguration(first)).isNotNull();
            assertThat(getConfiguration(resultIterator.next())).isNull();
        }
    }
}

