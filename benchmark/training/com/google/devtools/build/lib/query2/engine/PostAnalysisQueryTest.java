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


import Setting.NO_IMPLICIT_DEPS;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.transitions.PatchTransition;
import com.google.devtools.build.lib.analysis.test.TestConfiguration.TestOptions;
import com.google.devtools.build.lib.analysis.util.MockRule;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.packages.AttributeMap;
import com.google.devtools.build.lib.testutil.TestConstants;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.junit.Test;


/**
 * Tests for {@link PostAnalysisQueryEnvironment}.
 */
public abstract class PostAnalysisQueryTest<T> extends AbstractQueryTest<T> {
    static final String DEFAULT_UNIVERSE = "DEFAULT_UNIVERSE";

    @Override
    @Test
    public void testTargetLiteralWithMissingTargets() throws Exception {
        getHelper().turnOffFailFast();
        super.testTargetLiteralWithMissingTargets();
    }

    @Override
    @Test
    public void testBadTargetLiterals() throws Exception {
        getHelper().turnOffFailFast();
        super.testBadTargetLiterals();
    }

    @Override
    @Test
    public void testNoImplicitDeps() throws Exception {
        MockRule ruleWithImplicitDeps = () -> MockRule.define("implicit_deps_rule", attr("explicit", LABEL).allowedFileTypes(FileTypeSet.ANY_FILE), attr("explicit_with_default", LABEL).value(Label.parseAbsoluteUnchecked("//test:explicit_with_default")).allowedFileTypes(FileTypeSet.ANY_FILE), attr("$implicit", LABEL).value(Label.parseAbsoluteUnchecked("//test:implicit")), attr(":latebound", LABEL).value(Attribute.LateBoundDefault.fromConstantForTesting(Label.parseAbsoluteUnchecked("//test:latebound"))));
        helper.useRuleClassProvider(setRuleClassProviders(ruleWithImplicitDeps).build());
        writeFile("test/BUILD", "implicit_deps_rule(", "    name = 'my_rule',", "    explicit = ':explicit',", "    explicit_with_default = ':explicit_with_default',", ")", "cc_library(name = 'explicit')", "cc_library(name = 'explicit_with_default')", "cc_library(name = 'implicit')", "cc_library(name = 'latebound')");
        final String implicits = "//test:implicit + //test:latebound";
        final String explicits = "//test:my_rule + //test:explicit + //test:explicit_with_default";
        // Check for implicit dependencies (late bound attributes, implicit attributes, platforms)
        PostAnalysisQueryTest.assertThat(evalToListOfStrings("deps(//test:my_rule)")).containsAllIn(evalToListOfStrings(((((explicits + " + ") + implicits) + " + ") + (TestConstants.PLATFORM_LABEL))));
        helper.setQuerySettings(NO_IMPLICIT_DEPS);
        PostAnalysisQueryTest.assertThat(evalToListOfStrings("deps(//test:my_rule)")).containsExactlyElementsIn(evalToListOfStrings(explicits));
    }

    @Override
    @Test
    public void testNoImplicitDeps_computedDefault() throws Exception {
        MockRule computedDefaultRule = () -> MockRule.define("computed_default_rule", attr("conspiracy", Type.STRING).value("space jam was a documentary"), attr("dep", LABEL).allowedFileTypes(FileTypeSet.ANY_FILE).value(new Attribute.ComputedDefault("conspiracy") {
            @Override
            public Object getDefault(AttributeMap rule) {
                return rule.get("conspiracy", Type.STRING).equals("space jam was a documentary") ? Label.parseAbsoluteUnchecked("//test:foo") : null;
            }
        }));
        helper.useRuleClassProvider(setRuleClassProviders(computedDefaultRule).build());
        writeFile("test/BUILD", "cc_library(name = 'foo')", "computed_default_rule(name = 'my_rule')");
        String target = "//test:my_rule";
        PostAnalysisQueryTest.assertThat(evalToListOfStrings((("deps(" + target) + ")"))).contains("//test:foo");
        helper.setQuerySettings(NO_IMPLICIT_DEPS);
        PostAnalysisQueryTest.assertThat(eval((("deps(" + target) + ")"))).isEqualTo(eval(target));
    }

    @Override
    @Test
    public void testLet() throws Exception {
        getHelper().setWholeTestUniverseScope("//a,//b,//c,//d");
        super.testLet();
    }

    @Override
    @Test
    public void testSet() throws Exception {
        getHelper().setWholeTestUniverseScope("//a:*,//b:*,//c:*,//d:*");
        super.testSet();
    }

    /**
     * PatchTransition on --test_arg
     */
    public static class TestArgPatchTransition implements PatchTransition {
        String toOption;

        String name;

        public TestArgPatchTransition(String toOption, String name) {
            this.toOption = toOption;
            this.name = name;
        }

        public TestArgPatchTransition(String toOption) {
            this(toOption, "TestArgPatchTransition");
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public BuildOptions patch(BuildOptions options) {
            BuildOptions result = options.clone();
            result.get(TestOptions.class).testArguments = Collections.singletonList(toOption);
            return result;
        }
    }

    @Test
    public void testMultipleTopLevelConfigurations() throws Exception {
        MockRule transitionedRule = () -> MockRule.define("transitioned_rule", ( builder, env) -> builder.cfg(new com.google.devtools.build.lib.query2.engine.TestArgPatchTransition("SET BY PATCH")).build());
        MockRule untransitionedRule = () -> MockRule.define("untransitioned_rule");
        helper.useRuleClassProvider(setRuleClassProviders(transitionedRule, untransitionedRule).build());
        writeFile("test/BUILD", "transitioned_rule(name = 'transitioned_rule')", "untransitioned_rule(name = 'untransitioned_rule')");
        Set<T> result = eval("//test:transitioned_rule+//test:untransitioned_rule");
        PostAnalysisQueryTest.assertThat(result).hasSize(2);
        Iterator<T> resultIterator = result.iterator();
        assertThat(getConfiguration(resultIterator.next())).isNotEqualTo(getConfiguration(resultIterator.next()));
    }

    @Test
    public void testMultipleTopLevelConfigurations_multipleConfigsPrefersTopLevel() throws Exception {
        MockRule ruleWithTransitionAndDep = () -> MockRule.define("rule_with_transition_and_dep", ( builder, env) -> builder.cfg(new com.google.devtools.build.lib.query2.engine.TestArgPatchTransition("SET BY PATCH")).addAttribute(attr("dep", LABEL).allowedFileTypes(FileTypeSet.ANY_FILE).build()).build());
        MockRule simpleRule = () -> MockRule.define("simple_rule");
        helper.useRuleClassProvider(setRuleClassProviders(ruleWithTransitionAndDep, simpleRule).build());
        writeFile("test/BUILD", "rule_with_transition_and_dep(name = 'top-level', dep = ':dep')", "simple_rule(name = 'dep')");
        helper.setUniverseScope("//test:*");
        assertThat(getConfiguration(Iterables.getOnlyElement(eval("//test:dep")))).isNotEqualTo(getConfiguration(Iterables.getOnlyElement(eval("//test:top-level"))));
    }
}

