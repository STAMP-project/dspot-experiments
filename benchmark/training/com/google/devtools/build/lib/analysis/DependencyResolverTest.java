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


import com.google.devtools.build.lib.analysis.DependencyResolver.DependencyKind;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.analysis.util.TestAspects;
import com.google.devtools.build.lib.packages.AspectDescriptor;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.OrderedSetMultimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.devtools.build.lib.analysis.util.AnalysisTestCase.Flag.TRIMMED_CONFIGURATIONS;


/**
 * Tests for {@link DependencyResolver}.
 *
 * <p>These use custom rules so that all usual and unusual cases related to aspect processing can
 * be tested.
 *
 * <p>It would be nicer is we didn't have a Skyframe executor, if we didn't have that, we'd need a
 * way to create a configuration, a package manager and a whole lot of other things, so it's just
 * easier this way.
 */
@RunWith(JUnit4.class)
public class DependencyResolverTest extends AnalysisTestCase {
    private DependencyResolver dependencyResolver;

    @Test
    public void hasAspectsRequiredByRule() throws Exception {
        setRulesAvailableInTests(TestAspects.ASPECT_REQUIRING_RULE, TestAspects.BASE_RULE);
        pkg("a", "aspect(name='a', foo=[':b'])", "aspect(name='b', foo=[])");
        OrderedSetMultimap<DependencyKind, Dependency> map = dependentNodeMap("//a:a", null);
        assertDep(map, "foo", "//a:b", new AspectDescriptor(TestAspects.SIMPLE_ASPECT));
    }

    @Test
    public void hasAspectsRequiredByAspect() throws Exception {
        setRulesAvailableInTests(TestAspects.BASE_RULE, TestAspects.SIMPLE_RULE);
        pkg("a", "simple(name='a', foo=[':b'])", "simple(name='b', foo=[])");
        OrderedSetMultimap<DependencyKind, Dependency> map = dependentNodeMap("//a:a", TestAspects.ATTRIBUTE_ASPECT);
        assertDep(map, "foo", "//a:b", new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT));
    }

    @Test
    public void hasAllAttributesAspect() throws Exception {
        setRulesAvailableInTests(TestAspects.BASE_RULE, TestAspects.SIMPLE_RULE);
        pkg("a", "simple(name='a', foo=[':b'])", "simple(name='b', foo=[])");
        OrderedSetMultimap<DependencyKind, Dependency> map = dependentNodeMap("//a:a", TestAspects.ALL_ATTRIBUTES_ASPECT);
        assertDep(map, "foo", "//a:b", new AspectDescriptor(TestAspects.ALL_ATTRIBUTES_ASPECT));
    }

    @Test
    public void hasAspectDependencies() throws Exception {
        setRulesAvailableInTests(TestAspects.BASE_RULE);
        pkg("a", "base(name='a')");
        pkg("extra", "base(name='extra')");
        OrderedSetMultimap<DependencyKind, Dependency> map = dependentNodeMap("//a:a", TestAspects.EXTRA_ATTRIBUTE_ASPECT);
        assertDep(map, "$dep", "//extra:extra");
    }

    /**
     * Null configurations should always be explicit (vs. holding transitions). This lets Bazel skip
     * its complicated dependency configuration logic for these cases.
     */
    @Test
    public void nullConfigurationsAlwaysExplicit() throws Exception {
        pkg("a", "genrule(name = 'gen', srcs = ['gen.in'], cmd = '', outs = ['gen.out'])");
        update();
        Dependency dep = assertDep(dependentNodeMap("//a:gen", null), "srcs", "//a:gen.in");
        assertThat(dep.hasExplicitConfiguration()).isTrue();
        assertThat(dep.getConfiguration()).isNull();
    }

    /**
     * Runs the same test with trimmed configurations.
     */
    @TestSpec(size = Suite.SMALL_TESTS)
    @RunWith(JUnit4.class)
    public static class WithTrimmedConfigurations extends DependencyResolverTest {
        @Override
        protected AnalysisTestCase.FlagBuilder defaultFlags() {
            return super.defaultFlags().with(TRIMMED_CONFIGURATIONS);
        }
    }
}

