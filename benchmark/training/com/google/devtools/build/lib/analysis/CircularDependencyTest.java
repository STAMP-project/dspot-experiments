/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.transitions.SplitTransition;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.analysis.util.MockRule;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.packages.Attribute.LabelLateBoundDefault;
import com.google.devtools.build.lib.packages.Attribute.SplitTransitionProvider;
import com.google.devtools.build.lib.packages.AttributeMap;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.skylarkinterface.SkylarkPrinter;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that check that dependency cycles are reported correctly.
 */
@RunWith(JUnit4.class)
public class CircularDependencyTest extends BuildViewTestCase {
    @Test
    public void testOneRuleCycle() throws Exception {
        // error message
        // Rule
        checkError("cycle", "foo.g", selfEdgeMsg("//cycle:foo.g"), "genrule(name = 'foo.g',", "        outs = ['Foo.java'],", "        srcs = ['foo.g'],", "        cmd = 'cat $(SRCS) > $<' )");
    }

    @Test
    public void testDirectPackageGroupCycle() throws Exception {
        checkError("cycle", "melon", selfEdgeMsg("//cycle:moebius"), "package_group(name='moebius', packages=[], includes=['//cycle:moebius'])", "sh_library(name='melon', visibility=[':moebius'])");
    }

    @Test
    public void testThreeLongPackageGroupCycle() throws Exception {
        String expectedEvent = "cycle in dependency graph:\n" + (((("    //cycle:superman\n" + ".-> //cycle:rock\n") + "|   //cycle:paper\n") + "|   //cycle:scissors\n") + "`-- //cycle:rock");
        checkError("cycle", "superman", expectedEvent, "# dummy line", "package_group(name='paper', includes=['//cycle:scissors'])", "package_group(name='rock', includes=['//cycle:paper'])", "package_group(name='scissors', includes=['//cycle:rock'])", "sh_library(name='superman', visibility=[':rock'])");
        Event foundEvent = null;
        for (Event event : eventCollector) {
            if (event.getMessage().contains(expectedEvent)) {
                foundEvent = event;
                break;
            }
        }
        assertThat(foundEvent).isNotNull();
        Location location = foundEvent.getLocation();
        assertThat(location.getStartLineAndColumn().getLine()).isEqualTo(3);
        assertThat(location.getPath().toString()).isEqualTo("/workspace/cycle/BUILD");
    }

    /**
     * Test to detect implicit input/output file overlap in rules.
     */
    @Test
    public void testOneRuleImplicitCycleJava() throws Exception {
        Package pkg = createScratchPackageForImplicitCycle("cycle", "java_library(name='jcyc',", "      srcs = ['libjcyc.jar', 'foo.java'])");
        try {
            pkg.getTarget("jcyc");
            Assert.fail();
        } catch (NoSuchTargetException e) {
            /* ok */
        }
        assertThat(containsErrors()).isTrue();
        assertContainsEvent(("rule 'jcyc' has file 'libjcyc.jar' as both an" + " input and an output"));
    }

    /**
     * Test not to detect implicit input/output file overlap in rules,
     * when coming from a different package.
     */
    @Test
    public void testInputOutputConflictDifferentPackage() throws Exception {
        Package pkg = createScratchPackageForImplicitCycle("googledata/xxx", "genrule(name='geo',", "    srcs = ['//googledata/geo:geo_info.txt'],", "    outs = ['geoinfo.txt'],", "    cmd = '$(SRCS) > $@')");
        assertThat(containsErrors()).isFalse();
    }

    @Test
    public void testTwoRuleCycle() throws Exception {
        scratchRule("b", "rule2", "cc_library(name='rule2',", "           deps=['//a:rule1'])");
        checkError("a", "rule1", ("in cc_library rule //a:rule1: cycle in dependency graph:\n" + ((".-> //a:rule1\n" + "|   //b:rule2\n") + "`-- //a:rule1")), "cc_library(name='rule1',", "           deps=['//b:rule2'])");
    }

    @Test
    public void testTwoRuleCycle2() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expect errors

        scratch.file("x/BUILD", "java_library(name='x', deps=['y'])", "java_library(name='y', deps=['x'])");
        getConfiguredTarget("//x");
        assertContainsEvent("in java_library rule //x:x: cycle in dependency graph");
    }

    @Test
    public void testIndirectOneRuleCycle() throws Exception {
        scratchRule("cycle", "foo.h", "genrule(name = 'foo.h',", "      outs = ['bar.h'],", "      srcs = ['foo.h'],", "      cmd = 'cp $< $@')");
        // error message
        // Rule
        checkError("main", "mygenrule", selfEdgeMsg("//cycle:foo.h"), "genrule(name='mygenrule',", "      outs = ['baz.h'],", "      srcs = ['//cycle:foo.h'],", "      cmd = 'cp $< $@')");
    }

    // Regression test for: "IllegalStateException in
    // AbstractConfiguredTarget.initialize()".
    // Failure to mark all cycle-forming nodes when there are *two* cycles led to
    // an attempt to initialise a node we'd already visited.
    @Test
    public void testTwoCycles() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expect errors

        scratch.file("x/BUILD", "genrule(name='b', srcs=['c'], tools=['c'], outs=['b.out'], cmd=':')", "genrule(name='c', srcs=['b.out'], outs=[], cmd=':')");
        getConfiguredTarget("//x:b");// doesn't crash!

        assertContainsEvent("cycle in dependency graph");
    }

    @Test
    public void testAspectCycle() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        // Using data= makes the dependency graph clearer because then the aspect does not propagate
        // from aspectdep through a to b (and c)
        scratch.file("x/BUILD", "load('//x:x.bzl', 'aspected', 'plain')", "plain(name = 'a', noaspect_deps = [':b'])", "aspected(name = 'b', aspect_deps = ['c'])", "plain(name = 'c')", "plain(name = 'aspectdep', aspect_deps = ['a'])");
        scratch.file("x/x.bzl", "def _impl(ctx):", "    return struct()", "", "rule_aspect = aspect(", "    implementation = _impl,", "    attr_aspects = ['aspect_deps'],", "    attrs = { '_implicit': attr.label(default = Label('//x:aspectdep')) })", "", "plain = rule(", "    implementation = _impl,", "    attrs = { 'aspect_deps': attr.label_list(), 'noaspect_deps': attr.label_list() })", "", "aspected = rule(", "    implementation = _impl,", "    attrs = { 'aspect_deps': attr.label_list(aspects = [rule_aspect]) })");
        getConfiguredTarget("//x:a");
        assertContainsEvent("cycle in dependency graph");
        assertContainsEvent("//x:c with aspect //x:x.bzl%rule_aspect");
    }

    /**
     * A late bound dependency which depends on the 'dep' label if the 'define' is in --defines.
     */
    // TODO(b/65746853): provide a way to do this without passing the entire configuration
    private static final LabelLateBoundDefault<BuildConfiguration> LATE_BOUND_DEP = LabelLateBoundDefault.fromTargetConfiguration(BuildConfiguration.class, null, ( rule, attributes, config) -> config.getCommandLineBuildVariables().containsKey(attributes.get("define", STRING)) ? attributes.get("dep", NODEP_LABEL) : null);

    /**
     * A rule which always depends on the given label.
     */
    private static final MockRule NORMAL_DEPENDER = () -> MockRule.define("normal_dep", attr("dep", LABEL).allowedFileTypes());

    /**
     * A rule which depends on a given label only if the given define is set.
     */
    private static final MockRule LATE_BOUND_DEPENDER = () -> MockRule.define("late_bound_dep", attr("define", STRING).mandatory(), attr("dep", NODEP_LABEL).mandatory(), attr(":late_bound_dep", LABEL).value(LATE_BOUND_DEP));

    /**
     * A rule which removes a define from the configuration of its dependency.
     */
    private static final MockRule DEFINE_CLEARER = () -> MockRule.define("define_clearer", attr("define", STRING).mandatory(), attr("dep", LABEL).mandatory().allowedFileTypes().cfg(new SplitTransitionProvider() {
        @Override
        public void repr(SkylarkPrinter printer) {
        }

        @Override
        public SplitTransition apply(AttributeMap map) {
            return (BuildOptions options) -> {
                String define = map.get("define", STRING);
                BuildOptions newOptions = options.clone();
                BuildConfiguration.Options optionsFragment = newOptions.get(.class);
                optionsFragment.commandLineBuildVariables = optionsFragment.commandLineBuildVariables.stream().filter(( pair) -> !(pair.getKey().equals(define))).collect(toImmutableList());
                return ImmutableList.of(newOptions);
            };
        }
    }));

    @Test
    public void testLateBoundTargetCycleNotConfiguredTargetCycle() throws Exception {
        // Target graph: //a -> //b -?> //c -> //a (loop)
        // Configured target graph: //a -> //b -> //c -> //a (2) -> //b (2)
        scratch.file("a/BUILD", "normal_dep(name = 'a', dep = '//b')");
        scratch.file("b/BUILD", "late_bound_dep(name = 'b', dep = '//c', define = 'CYCLE_ON')");
        scratch.file("c/BUILD", "define_clearer(name = 'c', dep = '//a', define = 'CYCLE_ON')");
        useConfiguration("--define=CYCLE_ON=yes");
        getConfiguredTarget("//a");
        assertNoEvents();
    }

    @Test
    public void testSelectTargetCycleNotConfiguredTargetCycle() throws Exception {
        // Target graph: //a -> //b -?> //c -> //a (loop)
        // Configured target graph: //a -> //b -> //c -> //a (2) -> //b (2) -> //b:stop (2)
        scratch.file("a/BUILD", "normal_dep(name = 'a', dep = '//b')");
        scratch.file("b/BUILD", "config_setting(name = 'cycle', define_values = {'CYCLE_ON': 'yes'})", "normal_dep(name = 'stop')", "normal_dep(name = 'b', dep = select({':cycle': '//c', '//conditions:default': ':stop'}))");
        scratch.file("c/BUILD", "define_clearer(name = 'c', dep = '//a', define = 'CYCLE_ON')");
        useConfiguration("--define=CYCLE_ON=yes");
        getConfiguredTarget("//a");
        assertNoEvents();
    }
}

