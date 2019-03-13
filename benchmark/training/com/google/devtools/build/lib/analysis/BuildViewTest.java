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


import AspectCollection.EMPTY;
import ConfiguredRuleClassProvider.Builder;
import NullConfiguration.INSTANCE;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.Actions;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.FailAction;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.configuredtargets.InputFileConfiguredTarget;
import com.google.devtools.build.lib.analysis.configuredtargets.OutputFileConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.analysis.util.BuildViewTestBase;
import com.google.devtools.build.lib.analysis.util.ExpectedTrimmedConfigurationErrors;
import com.google.devtools.build.lib.analysis.util.MockRule;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.events.OutputFilter.RegexOutputFilter;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.lib.testutil.TestRuleClassProvider;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.skyframe.NotifyingHelper;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.common.options.Options;
import com.google.devtools.common.options.OptionsParsingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.devtools.build.lib.analysis.util.AnalysisTestCase.Flag.KEEP_GOING;
import static com.google.devtools.build.lib.analysis.util.AnalysisTestCase.Flag.SKYFRAME_PREPARE_ANALYSIS;
import static com.google.devtools.build.lib.analysis.util.AnalysisTestCase.Flag.TRIMMED_CONFIGURATIONS;
import static com.google.devtools.build.lib.testutil.TestConstants.InternalTestExecutionMode.NORMAL;


/**
 * Tests for the {@link BuildView}.
 */
@TestSpec(size = Suite.SMALL_TESTS)
@RunWith(JUnit4.class)
public class BuildViewTest extends BuildViewTestBase {
    private static final Function<AnalysisFailureEvent, Pair<String, String>> ANALYSIS_EVENT_TO_STRING_PAIR = new Function<AnalysisFailureEvent, Pair<String, String>>() {
        @Override
        public Pair<String, String> apply(AnalysisFailureEvent event) {
            return Pair.of(event.getFailedTarget().getLabel().toString(), event.getLegacyFailureReason().toString());
        }
    };

    @Test
    public void testRuleConfiguredTarget() throws Exception {
        scratch.file("pkg/BUILD", "genrule(name='foo', ", "        cmd = '',", "        srcs=['a.src'],", "        outs=['a.out'])");
        update("//pkg:foo");
        Rule ruleTarget = ((Rule) (getTarget("//pkg:foo")));
        assertThat(ruleTarget.getRuleClass()).isEqualTo("genrule");
        ConfiguredTargetAndData ruleCTAT = getConfiguredTargetAndTarget("//pkg:foo");
        assertThat(ruleCTAT.getTarget()).isSameAs(ruleTarget);
    }

    @Test
    public void testFilterByTargets() throws Exception {
        scratch.file("tests/BUILD", "sh_test(name = 'small_test_1',", "        srcs = ['small_test_1.sh'],", "        data = [':xUnit'],", "        size = 'small',", "        tags = ['tag1'])", "", "sh_test(name = 'small_test_2',", "        srcs = ['small_test_2.sh'],", "        size = 'small',", "        tags = ['tag2'])", "", "", "test_suite( name = 'smallTests', tags=['small'])");
        // scratch.file("tests/small_test_1.py");
        update("//tests:smallTests");
        ConfiguredTargetAndData test1 = getConfiguredTargetAndTarget("//tests:small_test_1");
        ConfiguredTargetAndData test2 = getConfiguredTargetAndTarget("//tests:small_test_2");
        ConfiguredTargetAndData suite = getConfiguredTargetAndTarget("//tests:smallTests");
        ConfiguredTarget test1CT = test1.getConfiguredTarget();
        ConfiguredTarget test2CT = test2.getConfiguredTarget();
        ConfiguredTarget suiteCT = suite.getConfiguredTarget();
        assertNoEvents();// start from a clean slate

        Collection<ConfiguredTarget> targets = new java.util.LinkedHashSet(ImmutableList.of(test1CT, test2CT, suiteCT));
        targets = Lists.<ConfiguredTarget>newArrayList(BuildView.filterTestsByTargets(targets, Sets.newHashSet(test1.getTarget().getLabel(), suite.getTarget().getLabel())));
        assertThat(targets).containsExactlyElementsIn(Sets.newHashSet(test1CT, suiteCT));
    }

    @Test
    public void testSourceArtifact() throws Exception {
        setupDummyRule();
        update("//pkg:a.src");
        InputFileConfiguredTarget inputCT = getInputFileConfiguredTarget("//pkg:a.src");
        Artifact inputArtifact = inputCT.getArtifact();
        assertThat(getGeneratingAction(inputArtifact)).isNull();
        assertThat(inputArtifact.getExecPathString()).isEqualTo("pkg/a.src");
    }

    @Test
    public void testGeneratedArtifact() throws Exception {
        setupDummyRule();
        update("//pkg:a.out");
        ConfiguredTargetAndData ctad = getConfiguredTargetAndData("//pkg:a.out");
        OutputFileConfiguredTarget output = ((OutputFileConfiguredTarget) (ctad.getConfiguredTarget()));
        Artifact outputArtifact = output.getArtifact();
        assertThat(outputArtifact.getRoot()).isEqualTo(ctad.getConfiguration().getBinDirectory(output.getLabel().getPackageIdentifier().getRepository()));
        assertThat(outputArtifact.getExecPath()).isEqualTo(ctad.getConfiguration().getBinFragment().getRelative("pkg/a.out"));
        assertThat(outputArtifact.getRootRelativePath()).isEqualTo(PathFragment.create("pkg/a.out"));
        Action action = getGeneratingAction(outputArtifact);
        assertThat(action.getClass()).isSameAs(FailAction.class);
    }

    @Test
    public void testSyntaxErrorInDepPackage() throws Exception {
        // Check that a loading error in a dependency is properly reported.
        scratch.file("a/BUILD", "genrule(name='x',", "        srcs = ['file.txt'],", "        outs = ['foo'],", "        cmd = 'echo')", "@");// syntax error

        scratch.file("b/BUILD", "genrule(name= 'cc',", "        tools = ['//a:x'],", "        outs = ['bar'],", "        cmd = 'echo')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//b:cc");
        assertContainsEvent("invalid character: '@'");
        assertThat(result.hasError()).isTrue();
    }

    @Test
    public void testReportsAnalysisRootCauses() throws Exception {
        scratch.file("private/BUILD", "genrule(", "    name='private',", "    outs=['private.out'],", "    cmd='',", "    visibility=['//visibility:private'])");
        scratch.file("foo/BUILD", "genrule(", "    name='foo',", "    tools=[':bar'],", "    outs=['foo.out'],", "    cmd='')", "genrule(", "    name='bar',", "    tools=['//private'],", "    outs=['bar.out'],", "    cmd='')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.AnalysisFailureRecorder recorder = new BuildViewTestBase.AnalysisFailureRecorder();
        eventBus.register(recorder);
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//foo");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).hasSize(1);
        AnalysisFailureEvent event = recorder.events.get(0);
        assertThat(event.getLegacyFailureReason().toString()).isEqualTo("//foo:bar");
        assertThat(event.getFailedTarget().getLabel().toString()).isEqualTo("//foo:foo");
    }

    @Test
    public void testAnalysisReportsDependencyCycle() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name='foo',deps=['//bar'])");
        scratch.file("bar/BUILD", "sh_library(name='bar',deps=[':bar'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.AnalysisFailureRecorder recorder = new BuildViewTestBase.AnalysisFailureRecorder();
        eventBus.register(recorder);
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//foo");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).hasSize(1);
        AnalysisFailureEvent event = recorder.events.get(0);
        assertThat(event.getConfigurationId()).isNotEqualTo(INSTANCE.getEventId());
    }

    @Test
    public void testReportsLoadingRootCauses() throws Exception {
        // This test checks that two simultaneous errors are both reported:
        // - missing outs attribute,
        // - package referenced in tools does not exist
        scratch.file("pkg/BUILD", "genrule(name='foo',", "        tools=['//nopackage:missing'],", "        cmd='')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.LoadingFailureRecorder recorder = new BuildViewTestBase.LoadingFailureRecorder();
        eventBus.register(recorder);
        // Note: no need to run analysis for a loading failure.
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//pkg:foo");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()), Label.parseAbsolute("//nopackage:missing", ImmutableMap.of())));
        assertContainsEvent("missing value for mandatory attribute 'outs'");
        assertContainsEvent("no such package 'nopackage'");
        // Skyframe correctly reports the other root cause as the genrule itself (since it is
        // missing attributes).
        assertThat(recorder.events).hasSize(2);
        assertThat(recorder.events).contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()), Label.parseAbsolute("//pkg:foo", ImmutableMap.of())));
    }

    @Test
    public void testConvolutedLoadRootCauseAnalysis() throws Exception {
        // You need license declarations in third_party. We use this constraint to
        // create targets that are loadable, but are in error.
        scratch.file("third_party/first/BUILD", "sh_library(name='first', deps=['//third_party/second'], licenses=['notice'])");
        scratch.file("third_party/second/BUILD", "sh_library(name='second', deps=['//third_party/third'], licenses=['notice'])");
        scratch.file("third_party/third/BUILD", "sh_library(name='third', deps=['//third_party/fourth'], licenses=['notice'])");
        scratch.file("third_party/fourth/BUILD", "sh_library(name='fourth', deps=['//third_party/fifth'])");
        scratch.file("third_party/fifth/BUILD", "sh_library(name='fifth', licenses=['notice'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.LoadingFailureRecorder recorder = new BuildViewTestBase.LoadingFailureRecorder();
        eventBus.register(recorder);
        // Note: no need to run analysis for a loading failure.
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//third_party/first", "//third_party/third");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).hasSize(2);
        assertWithMessage(recorder.events.toString()).that(recorder.events.contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//third_party/first", ImmutableMap.of()), Label.parseAbsolute("//third_party/fourth", ImmutableMap.of())))).isTrue();
        assertThat(recorder.events).contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//third_party/third", ImmutableMap.of()), Label.parseAbsolute("//third_party/fourth", ImmutableMap.of())));
    }

    @Test
    public void testMultipleRootCauseReporting() throws Exception {
        scratch.file("gp/BUILD", "sh_library(name = 'gp', deps = ['//p:p'])");
        scratch.file("p/BUILD", "sh_library(name = 'p', deps = ['//c1:not', '//c2:not'])");
        scratch.file("c1/BUILD");
        scratch.file("c2/BUILD");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.LoadingFailureRecorder recorder = new BuildViewTestBase.LoadingFailureRecorder();
        eventBus.register(recorder);
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//gp");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).hasSize(2);
        assertThat(recorder.events).contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//gp", ImmutableMap.of()), Label.parseAbsolute("//c1:not", ImmutableMap.of())));
        assertThat(recorder.events).contains(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//gp", ImmutableMap.of()), Label.parseAbsolute("//c2:not", ImmutableMap.of())));
    }

    /**
     * Regression test for: "Package group includes are broken"
     */
    @Test
    public void testTopLevelPackageGroup() throws Exception {
        scratch.file("tropical/BUILD", "package_group(name='guava', includes=[':mango'])", "package_group(name='mango')");
        // If the analysis phase results in an error, this will throw an exception
        update("//tropical:guava");
        // Check if the included package group also got analyzed
        assertThat(getConfiguredTarget("//tropical:mango", null)).isNotNull();
    }

    @Test
    public void testTopLevelInputFile() throws Exception {
        scratch.file("tropical/BUILD", "exports_files(['file.txt'])");
        update("//tropical:file.txt");
        assertThat(getConfiguredTarget("//tropical:file.txt", null)).isNotNull();
    }

    @Test
    public void testGetDirectPrerequisites() throws Exception {
        scratch.file("package/BUILD", "filegroup(name='top', srcs=[':inner', 'file'])", "sh_binary(name='inner', srcs=['script.sh'])");
        update("//package:top");
        ConfiguredTarget top = getConfiguredTarget("//package:top", getTargetConfiguration());
        Iterable<ConfiguredTarget> targets = getView().getDirectPrerequisitesForTesting(reporter, top, getBuildConfigurationCollection());
        Iterable<Label> labels = Iterables.transform(targets, ( target) -> target.getLabel());
        assertThat(labels).containsExactly(Label.parseAbsolute("//package:inner", ImmutableMap.of()), Label.parseAbsolute("//package:file", ImmutableMap.of()));
    }

    @Test
    public void testGetDirectPrerequisiteDependencies() throws Exception {
        // Override the trimming transition to not distort the results.
        ConfiguredRuleClassProvider.Builder builder = new ConfiguredRuleClassProvider.Builder();
        TestRuleClassProvider.addStandardRules(builder);
        builder.overrideTrimmingTransitionFactoryForTesting(( rule) -> NoTransition.INSTANCE);
        useRuleClassProvider(builder.build());
        update();
        scratch.file("package/BUILD", "filegroup(name='top', srcs=[':inner', 'file'])", "sh_binary(name='inner', srcs=['script.sh'])");
        ConfiguredTarget top = Iterables.getOnlyElement(update("//package:top").getTargetsToBuild());
        Iterable<Dependency> targets = /* toolchainLabels= */
        getView().getDirectPrerequisiteDependenciesForTesting(reporter, top, getBuildConfigurationCollection(), ImmutableSet.of()).values();
        Dependency innerDependency = Dependency.withTransitionAndAspects(Label.parseAbsolute("//package:inner", ImmutableMap.of()), NoTransition.INSTANCE, EMPTY);
        Dependency fileDependency = Dependency.withNullConfiguration(Label.parseAbsolute("//package:file", ImmutableMap.of()));
        assertThat(targets).containsExactly(innerDependency, fileDependency);
    }

    /**
     * Tests that the {@code --output directory name} option cannot be used on
     * the command line.
     */
    @Test
    public void testConfigurationShortName() throws Exception {
        // Check that output directory name is still the name, otherwise this test is not testing what
        // we expect.
        BuildConfiguration.Options options = Options.getDefaults(Options.class);
        options.outputDirectoryName = "/home/wonkaw/wonka_chocolate/factory/out";
        assertWithMessage("The flag's name may have been changed; this test may need to be updated.").that(options.asMap().get("output directory name")).isEqualTo("/home/wonkaw/wonka_chocolate/factory/out");
        try {
            useConfiguration("--output directory name=foo");
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessage("Unrecognized option: --output directory name=foo");
        }
    }

    @Test
    public void testFileTranslations() throws Exception {
        scratch.file("foo/file");
        scratch.file("foo/BUILD", "exports_files(['file'])");
        useConfiguration("--message_translations=//foo:file");
        scratch.file("bar/BUILD", "sh_library(name = 'bar')");
        update("//bar");
    }

    // Regression test: "output_filter broken (but in a different way)"
    @Test
    public void testOutputFilterSeeWarning() throws Exception {
        runAnalysisWithOutputFilter(Pattern.compile(".*"));
        assertContainsEvent("please do not import '//java/a:A.java'");
    }

    // Regression test: "output_filter broken (but in a different way)"
    @Test
    public void testOutputFilter() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        runAnalysisWithOutputFilter(Pattern.compile("^//java/c"));
        assertNoEvents();
    }

    @Test
    public void testOutputFilterWithDebug() throws Exception {
        scratch.file("java/a/BUILD", "java_library(name = 'a',", "  srcs = ['A.java'],", "  deps = ['//java/b'])");
        scratch.file("java/b/rules.bzl", "def _impl(ctx):", "  print('debug in b')", "  ctx.file_action(", "    output = ctx.outputs.my_output,", "    content = 'foo',", "  )", "gen = rule(implementation = _impl, outputs = {'my_output': 'B.java'})");
        scratch.file("java/b/BUILD", "load(':rules.bzl', 'gen')", "gen(name='src')", "java_library(name = 'b', srcs = [':src'])");
        reporter.setOutputFilter(RegexOutputFilter.forPattern(Pattern.compile("^//java/a")));
        update("//java/a:a");
        assertContainsEvent("DEBUG /workspace/java/b/rules.bzl:2:3: debug in b");
    }

    @Test
    public void testAnalysisErrorMessageWithKeepGoing() throws Exception {
        scratch.file("a/BUILD", "sh_binary(name='a', srcs=['a1.sh', 'a2.sh'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        AnalysisResult result = update(defaultFlags().with(KEEP_GOING), "//a");
        assertThat(result.hasError()).isTrue();
        assertContainsEvent("errors encountered while analyzing target '//a:a'");
    }

    /**
     * Regression test: Exception in ConfiguredTargetGraph.checkForCycles()
     * when multiple top-level targets depend on the same cycle.
     */
    @Test
    public void testCircularDependencyBelowTwoTargets() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        scratch.file("foo/BUILD", "sh_library(name = 'top1', srcs = ['top1.sh'], deps = [':rec1'])", "sh_library(name = 'top2', srcs = ['top2.sh'], deps = [':rec1'])", "sh_library(name = 'rec1', srcs = ['rec1.sh'], deps = [':rec2'])", "sh_library(name = 'rec2', srcs = ['rec2.sh'], deps = [':rec1'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        AnalysisResult result = update(defaultFlags().with(KEEP_GOING), "//foo:top1", "//foo:top2");
        assertThat(result.hasError()).isTrue();
        assertContainsEvent("in sh_library rule //foo:rec1: cycle in dependency graph:\n");
        assertContainsEvent("in sh_library rule //foo:top");
    }

    // Regression test: cycle node depends on error.
    @Test
    public void testErrorBelowCycle() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling (also b/67412276: handle cycles properly).
            return;
        }
        scratch.file("foo/BUILD", "sh_library(name = 'top', deps = ['mid'])", "sh_library(name = 'mid', deps = ['bad', 'cycle1'])", "sh_library(name = 'bad', srcs = ['//badbuild:isweird'])", "sh_library(name = 'cycle1', deps = ['cycle2', 'mid'])", "sh_library(name = 'cycle2', deps = ['cycle1'])");
        scratch.file("badbuild/BUILD", "");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        /* deterministic= */
        injectGraphListenerForTesting(new NotifyingHelper.Listener() {
            @Override
            public void accept(SkyKey key, NotifyingHelper.EventType type, NotifyingHelper.Order order, Object context) {
            }
        }, true);
        try {
            update("//foo:top");
            Assert.fail();
        } catch (ViewCreationFailedException e) {
            // Expected.
        }
        assertContainsEvent(("no such target '//badbuild:isweird': target 'isweird' not declared in " + "package 'badbuild'"));
        assertContainsEvent("and referenced by '//foo:bad'");
        assertContainsEvent("in sh_library rule //foo");
        assertContainsEvent("cycle in dependency graph");
        MoreAsserts.assertEventCountAtLeast(2, eventCollector);
    }

    @Test
    public void testErrorBelowCycleKeepGoing() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        scratch.file("foo/BUILD", "sh_library(name = 'top', deps = ['mid'])", "sh_library(name = 'mid', deps = ['bad', 'cycle1'])", "sh_library(name = 'bad', srcs = ['//badbuild:isweird'])", "sh_library(name = 'cycle1', deps = ['cycle2', 'mid'])", "sh_library(name = 'cycle2', deps = ['cycle1'])");
        scratch.file("badbuild/BUILD", "");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        update(defaultFlags().with(KEEP_GOING), "//foo:top");
        assertContainsEvent(("no such target '//badbuild:isweird': target 'isweird' not declared in " + "package 'badbuild'"));
        assertContainsEvent("and referenced by '//foo:bad'");
        assertContainsEvent("in sh_library rule //foo");
        assertContainsEvent("cycle in dependency graph");
        // This error is triggered both in configuration trimming (which visits the transitive target
        // closure) and in the normal configured target cycle detection path. So we get an additional
        // instance of this check (which varies depending on whether Skyframe loading phase is enabled).
        // TODO(gregce): Fix above and uncomment the below. Note that the duplicate doesn't make it into
        // real user output (it only affects tests).
        // assertEventCount(3, eventCollector);
    }

    @Test
    public void testAnalysisEntryHasActionsEvenWithError() throws Exception {
        scratch.file("foo/BUILD", "cc_binary(name = 'foo', linkshared = 1, srcs = ['foo.cc'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//foo:foo");
            Assert.fail();// Expected ViewCreationFailedException.

        } catch (ViewCreationFailedException e) {
            // ok.
        }
    }

    @Test
    public void testHelpfulErrorForWrongPackageLabels() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("x/BUILD", "cc_library(name='x', srcs=['x.cc'])");
        scratch.file("y/BUILD", "cc_library(name='y', srcs=['y.cc'], deps=['//x:z'])");
        AnalysisResult result = update(defaultFlags().with(KEEP_GOING), "//y:y");
        assertThat(result.hasError()).isTrue();
        assertContainsEvent(("no such target '//x:z': " + ("target 'z' not declared in package 'x' " + "defined by /workspace/x/BUILD and referenced by '//y:y'")));
    }

    @Test
    public void testNewActionsAreDifferentAndDontConflict() throws Exception {
        scratch.file("pkg/BUILD", "genrule(name='a', ", "        cmd='',", "        outs=['a.out'])");
        OutputFileConfiguredTarget outputCT = ((OutputFileConfiguredTarget) (Iterables.getOnlyElement(update("//pkg:a.out").getTargetsToBuild())));
        Artifact outputArtifact = outputCT.getArtifact();
        Action action = getGeneratingAction(outputArtifact);
        assertThat(action).isNotNull();
        scratch.overwriteFile("pkg/BUILD", "genrule(name='a', ", "        cmd='false',", "        outs=['a.out'])");
        update("//pkg:a.out");
        assertWithMessage("Actions should not be compatible").that(Actions.canBeShared(actionKeyContext, action, getGeneratingAction(outputArtifact))).isFalse();
    }

    /**
     * This test exercises the case where we invalidate (mark dirty) a node in one build command
     * invocation and the revalidation (because it did not change) happens in a subsequent build
     * command call.
     *
     * - In the first update call we construct A.
     *
     * - Then we construct B and we make the glob get invalidated. We do that by deleting a file
     * because it depends on the directory listing. Because of that A gets invalidated.
     *
     * - Then we construct A again. The glob gets revalidated because it is still matching just A.java
     * and A configured target gets revalidated too. At the end of the analysis A java action should
     * be in the action graph.
     */
    @Test
    public void testMultiBuildInvalidationRevalidation() throws Exception {
        scratch.file("java/a/A.java", "bla1");
        scratch.file("java/a/C.java", "bla2");
        scratch.file("java/a/BUILD", "java_test(name = 'A',", "          srcs = glob(['A*.java']))", "java_test(name = 'B',", "          srcs = ['B.java'])");
        ConfiguredTarget ct = Iterables.getOnlyElement(update("//java/a:A").getTargetsToBuild());
        scratch.deleteFile("java/a/C.java");
        update("//java/a:B");
        update("//java/a:A");
        assertThat(getGeneratingAction(getBinArtifact("A_deploy.jar", ct))).isNotNull();
    }

    /**
     * Regression test: ClassCastException in SkyframeLabelVisitor.updateRootCauses.
     */
    @Test
    public void testDepOnGoodTargetInBadPkgAndTransitivelyBadTarget() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("parent/BUILD", "sh_library(name = 'foo',", "           srcs = ['//badpkg1:okay-target', '//okaypkg:transitively-bad-target'])");
        Path badpkg1BuildFile = scratch.file("badpkg1/BUILD", "exports_files(['okay-target'])", "invalidbuildsyntax");
        scratch.file("okaypkg/BUILD", "sh_library(name = 'transitively-bad-target',", "           srcs = ['//badpkg2:bad-target'])");
        Path badpkg2BuildFile = scratch.file("badpkg2/BUILD", "sh_library(name = 'bad-target')", "invalidbuildsyntax");
        update(defaultFlags().with(KEEP_GOING), "//parent:foo");
        assertThat(BuildViewTestBase.getFrequencyOfErrorsWithLocation(badpkg1BuildFile.asFragment(), eventCollector)).isEqualTo(1);
        assertThat(BuildViewTestBase.getFrequencyOfErrorsWithLocation(badpkg2BuildFile.asFragment(), eventCollector)).isEqualTo(1);
    }

    @Test
    public void testDepOnGoodTargetInBadPkgAndTransitiveCycle_NotIncremental() throws Exception {
        /* incremental= */
        runTestDepOnGoodTargetInBadPkgAndTransitiveCycle(false);
    }

    @Test
    public void testDepOnGoodTargetInBadPkgAndTransitiveCycle_Incremental() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        /* incremental= */
        runTestDepOnGoodTargetInBadPkgAndTransitiveCycle(true);
    }

    /**
     * Regression test: in keep_going mode, cycles in target graph aren't reported
     * if package is in error.
     */
    @Test
    public void testCycleReporting_TargetCycleWhenPackageInError() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("cycles/BUILD", "sh_library(name = 'a', deps = [':b'])", "sh_library(name = 'b', deps = [':a'])", "notvalidbuildsyntax");
        update(defaultFlags().with(KEEP_GOING), "//cycles:a");
        assertContainsEvent("'notvalidbuildsyntax'");
        assertContainsEvent("cycle in dependency graph");
    }

    @Test
    public void testTransitiveLoadingDoesntShortCircuitInKeepGoing() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("parent/BUILD", "sh_library(name = 'a', deps = ['//child:b'])", "parentisbad");
        scratch.file("child/BUILD", "sh_library(name = 'b')", "childisbad");
        update(defaultFlags().with(KEEP_GOING), "//parent:a");
        assertContainsEventWithFrequency("parentisbad", 1);
        assertContainsEventWithFrequency("childisbad", 1);
        assertContainsEventWithFrequency("and referenced by '//parent:a'", 1);
    }

    /**
     * Smoke test for the Skyframe code path.
     */
    @Test
    public void testSkyframe() throws Exception {
        setupDummyRule();
        String aoutLabel = "//pkg:a.out";
        update(aoutLabel);
        // However, a ConfiguredTarget was actually produced.
        ConfiguredTarget target = Iterables.getOnlyElement(getAnalysisResult().getTargetsToBuild());
        assertThat(target.getLabel().toString()).isEqualTo(aoutLabel);
        Artifact aout = Iterables.getOnlyElement(target.getProvider(FileProvider.class).getFilesToBuild());
        Action action = getGeneratingAction(aout);
        assertThat(action.getClass()).isSameAs(FailAction.class);
    }

    /**
     * ConfiguredTargetFunction should not register actions in legacy Blaze ActionGraph unless
     * the creation of the node is successful.
     */
    @Test
    public void testActionsNotRegisteredInLegacyWhenError() throws Exception {
        // First find the artifact we want to make sure is not generated by an action with an error.
        // Then update the BUILD file and re-analyze.
        scratch.file("actions_not_registered/BUILD", "cc_binary(name = 'foo', srcs = ['foo.cc'])");
        ConfiguredTarget foo = Iterables.getOnlyElement(update("//actions_not_registered:foo").getTargetsToBuild());
        Artifact fooOut = Iterables.getOnlyElement(foo.getProvider(FileProvider.class).getFilesToBuild());
        assertThat(getActionGraph().getGeneratingAction(fooOut)).isNotNull();
        clearAnalysisResult();
        scratch.overwriteFile("actions_not_registered/BUILD", "cc_binary(name = 'foo', linkshared = 1, srcs = ['foo.cc'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//actions_not_registered:foo");
            Assert.fail("This build should fail because: 'linkshared' used in non-shared library");
        } catch (ViewCreationFailedException e) {
            assertThat(getActionGraph().getGeneratingAction(fooOut)).isNull();
        }
    }

    /**
     * Regression test:
     * "skyframe: ArtifactFactory and ConfiguredTargets out of sync".
     */
    @Test
    public void testSkyframeAnalyzeRuleThenItsOutputFile() throws Exception {
        scratch.file("pkg/BUILD", "testing_dummy_rule(name='foo', ", "                   srcs=['a.src'],", "                   outs=['a.out'])");
        scratch.file("pkg2/BUILD", "testing_dummy_rule(name='foo', ", "                   srcs=['a.src'],", "                   outs=['a.out'])");
        String aoutLabel = "//pkg:a.out";
        update("//pkg2:foo");
        update("//pkg:foo");
        scratch.overwriteFile("pkg2/BUILD", "testing_dummy_rule(name='foo', ", "                   srcs=['a.src'],", "                   outs=['a.out'])", "# Comment");
        update("//pkg:a.out");
        // However, a ConfiguredTarget was actually produced.
        ConfiguredTarget target = Iterables.getOnlyElement(getAnalysisResult().getTargetsToBuild());
        assertThat(target.getLabel().toString()).isEqualTo(aoutLabel);
        Artifact aout = Iterables.getOnlyElement(target.getProvider(FileProvider.class).getFilesToBuild());
        Action action = getGeneratingAction(aout);
        assertThat(action.getClass()).isSameAs(FailAction.class);
    }

    /**
     * Tests that skyframe reports the root cause as being the target that depended on the symlink
     * cycle.
     */
    @Test
    public void testRootCauseReportingFileSymlinks() throws Exception {
        scratch.file("gp/BUILD", "sh_library(name = 'gp', deps = ['//p'])");
        scratch.file("p/BUILD", "sh_library(name = 'p', deps = ['//c'])");
        scratch.file("c/BUILD", "sh_library(name = 'c', deps = [':c1', ':c2'])", "sh_library(name = 'c1', deps = ['//cycles1'])", "sh_library(name = 'c2', deps = ['//cycles2'])");
        Path cycles1BuildFilePath = scratch.file("cycles1/BUILD", "sh_library(name = 'cycles1', srcs = glob(['*.sh']))");
        Path cycles2BuildFilePath = scratch.file("cycles2/BUILD", "sh_library(name = 'cycles2', srcs = glob(['*.sh']))");
        cycles1BuildFilePath.getParentDirectory().getRelative("cycles1.sh").createSymbolicLink(PathFragment.create("cycles1.sh"));
        cycles2BuildFilePath.getParentDirectory().getRelative("cycles2.sh").createSymbolicLink(PathFragment.create("cycles2.sh"));
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.LoadingFailureRecorder recorder = new BuildViewTestBase.LoadingFailureRecorder();
        eventBus.register(recorder);
        AnalysisResult result = update(eventBus, defaultFlags().with(KEEP_GOING), "//gp");
        assertThat(result.hasError()).isTrue();
        assertThat(recorder.events).containsExactly(new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//gp", ImmutableMap.of()), Label.parseAbsolute("//cycles1", ImmutableMap.of())), new com.google.devtools.build.lib.pkgcache.LoadingFailureEvent(Label.parseAbsolute("//gp", ImmutableMap.of()), Label.parseAbsolute("//cycles2", ImmutableMap.of())));
    }

    /**
     * Regression test for bug when a configured target has missing deps, but also depends
     * transitively on an error. We build //foo:query, which depends on a valid and an invalid target
     * pattern. We first make sure the invalid target pattern is in the graph, so that it throws when
     * requested by //foo:query. Then, when bubbling the invalid target pattern error up, //foo:query
     * must cope with the combination of an error and a missing dep.
     */
    @Test
    public void testGenQueryWithBadTargetAndUnfinishedTarget() throws Exception {
        // The target //foo:zquery is used to force evaluation of //foo:nosuchtarget before the target
        // patterns in //foo:query are enqueued for evaluation. That way, //foo:query will depend on one
        // invalid target pattern and two target patterns that haven't been evaluated yet.
        // It is important that a missing target pattern is requested before the exception is thrown, so
        // we have both //foo:b and //foo:z missing from the deps, in the hopes that at least one of
        // them will come before //foo:nosuchtarget.
        scratch.file("foo/BUILD", "genquery(name = 'query',", "         expression = 'deps(//foo:b) except //foo:nosuchtarget except //foo:z',", "         scope = ['//foo:a'])", "genquery(name = 'zquery',", "         expression = 'deps(//foo:nosuchtarget)',", "         scope = ['//foo:a'])", "sh_library(name = 'a')", "sh_library(name = 'b')", "sh_library(name = 'z')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//foo:zquery");
            Assert.fail();
        } catch (ViewCreationFailedException e) {
            assertThat(e).hasMessageThat().contains("Analysis of target '//foo:zquery' failed; build aborted");
        }
        try {
            update("//foo:query");
            Assert.fail();
        } catch (ViewCreationFailedException e) {
            assertThat(e).hasMessageThat().contains("Analysis of target '//foo:query' failed; build aborted");
        }
    }

    /**
     * Tests that rules with configurable attributes can be accessed through {@link com.google.devtools.build.lib.skyframe.PostConfiguredTargetFunction}.
     * This is a regression test for a Bazel crash.
     */
    @Test
    public void testPostProcessedConfigurableAttributes() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        useConfiguration("--cpu=k8");
        reporter.removeHandler(FoundationTestCase.failFastHandler);// Expect errors from action conflicts.

        scratch.file("conflict/BUILD", "config_setting(name = 'a', values = {'test_arg': 'a'})", "cc_library(name='x', srcs=select({':a': ['a.cc'], '//conditions:default': ['foo.cc']}))", "cc_binary(name='_objs/x/foo.o', srcs=['bar.cc'])");
        AnalysisResult result = update(defaultFlags().with(KEEP_GOING), "//conflict:_objs/x/foo.o", "//conflict:x");
        assertThat(result.hasError()).isTrue();
        // Expect to reach this line without a Precondition-triggered NullPointerException.
        assertContainsEvent("file 'conflict/_objs/x/foo.o' is generated by these conflicting actions");
    }

    @Test
    public void testCycleDueToJavaLauncherConfiguration() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        if (defaultFlags().contains(TRIMMED_CONFIGURATIONS)) {
            // Trimmed configurations don't yet support late-bound attributes.
            // TODO(gregce): re-enable this when ready.
            return;
        }
        scratch.file("foo/BUILD", "java_binary(name = 'java', srcs = ['DoesntMatter.java'])", "cc_binary(name = 'cpp', data = [':java'])");
        // Everything is fine - the dependency graph is acyclic.
        update("//foo:java", "//foo:cpp");
        if (getTargetConfiguration().trimConfigurations()) {
            Assert.fail(ExpectedTrimmedConfigurationErrors.LATE_BOUND_ATTRIBUTES_UNSUPPORTED);
        }
        // Now there will be an analysis-phase cycle because the java_binary now has an implicit dep on
        // the cc_binary launcher.
        useConfiguration("--java_launcher=//foo:cpp");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//foo:java", "//foo:cpp");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
            assertThat(expected).hasMessageThat().matches("Analysis of target '//foo:(java|cpp)' failed; build aborted.*");
        }
        assertContainsEvent("cycle in dependency graph");
    }

    @Test
    public void testDependsOnBrokenTarget() throws Exception {
        scratch.file("foo/BUILD", "sh_test(name = 'test', srcs = ['test.sh'], data = ['//bar:data'])");
        scratch.file("bar/BUILD", "BROKEN BROKEN BROKEN!!!");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//foo:test");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
            assertThat(expected).hasMessageThat().matches("Analysis of target '//foo:test' failed; build aborted.*");
        }
    }

    /**
     * Regression test: IllegalStateException in BuildView.update() on circular dependency instead of
     * graceful failure.
     */
    @Test
    public void testCircularDependency() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        scratch.file("cycle/BUILD", "cc_library(name = 'foo', srcs = ['foo.cc'], deps = [':bar'])", "cc_library(name = 'bar', srcs = ['bar.cc'], deps = [':foo'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//cycle:foo");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
            assertContainsEvent("in cc_library rule //cycle:foo: cycle in dependency graph:");
            assertThat(expected).hasMessageThat().contains("Analysis of target '//cycle:foo' failed; build aborted");
        }
    }

    /**
     * Regression test: IllegalStateException in BuildView.update() on circular dependency instead of
     * graceful failure.
     */
    @Test
    public void testCircularDependencyWithKeepGoing() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67412276): handle cycles properly.
            return;
        }
        scratch.file("cycle/BUILD", "cc_library(name = 'foo', srcs = ['foo.cc'], deps = [':bar'])", "cc_library(name = 'bar', srcs = ['bar.cc'], deps = [':foo'])", "cc_library(name = 'bat', srcs = ['bat.cc'], deps = [':bas'])", "cc_library(name = 'bas', srcs = ['bas.cc'], deps = [':bau'])", "cc_library(name = 'bau', srcs = ['bas.cc'], deps = [':bas'])", "cc_library(name = 'baz', srcs = ['baz.cc'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EventBus eventBus = new EventBus();
        BuildViewTestBase.LoadingFailureRecorder loadingFailureRecorder = new BuildViewTestBase.LoadingFailureRecorder();
        BuildViewTestBase.AnalysisFailureRecorder analysisFailureRecorder = new BuildViewTestBase.AnalysisFailureRecorder();
        eventBus.register(loadingFailureRecorder);
        eventBus.register(analysisFailureRecorder);
        update(eventBus, defaultFlags().with(KEEP_GOING), "//cycle:foo", "//cycle:bat", "//cycle:baz");
        assertContainsEvent("in cc_library rule //cycle:foo: cycle in dependency graph:");
        assertContainsEvent("in cc_library rule //cycle:bas: cycle in dependency graph:");
        assertContainsEvent("errors encountered while analyzing target '//cycle:foo': it will not be built");
        assertContainsEvent("errors encountered while analyzing target '//cycle:bat': it will not be built");
        // With interleaved loading and analysis, we can no longer distinguish loading-phase cycles
        // and analysis-phase cycles. This was previously reported as a loading-phase cycle, as it
        // happens with any configuration (cycle is hard-coded in the BUILD files). Also see the
        // test below.
        assertThat(Iterables.transform(analysisFailureRecorder.events, BuildViewTest.ANALYSIS_EVENT_TO_STRING_PAIR)).containsExactly(Pair.of("//cycle:foo", "//cycle:foo"), Pair.of("//cycle:bat", "//cycle:bas"));
    }

    @Test
    public void testLoadingErrorReportedCorrectly() throws Exception {
        scratch.file("a/BUILD", "cc_library(name='a')");
        scratch.file("b/BUILD", "cc_library(name='b', deps = ['//missing:lib'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        AnalysisResult result = update(defaultFlags().with(KEEP_GOING), "//a", "//b");
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError()).contains("command succeeded, but there were loading phase errors");
    }

    @Test
    public void testVisibilityReferencesNonexistentPackage() throws Exception {
        scratch.file("z/a/BUILD", "py_library(name='a', visibility=['//nonexistent:nothing'])");
        scratch.file("z/b/BUILD", "py_library(name='b', deps=['//z/a:a'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//z/b:b");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
            assertContainsEvent("no such package 'nonexistent'");
        }
    }

    // regression test ("java.lang.IllegalStateException: cannot happen")
    @Test
    public void testDefaultVisibilityInNonexistentPackage() throws Exception {
        scratch.file("z/a/BUILD", "package(default_visibility=['//b'])", "py_library(name='alib')");
        scratch.file("z/b/BUILD", "py_library(name='b', deps=['//z/a:alib'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//z/b:b");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
            assertContainsEvent("no such package 'b'");
        }
    }

    @Test
    public void testNonTopLevelErrorsPrintedExactlyOnce() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        scratch.file("parent/BUILD", "sh_library(name = 'a', deps = ['//child:b'])");
        scratch.file("child/BUILD", "sh_library(name = 'b')", "undefined_symbol");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//parent:a");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
        }
        assertContainsEventWithFrequency("name 'undefined_symbol' is not defined", 1);
        assertContainsEventWithFrequency(("Target '//child:b' contains an error and its package is in error and referenced " + "by '//parent:a'"), 1);
    }

    @Test
    public void testNonTopLevelErrorsPrintedExactlyOnce_KeepGoing() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        scratch.file("parent/BUILD", "sh_library(name = 'a', deps = ['//child:b'])");
        scratch.file("child/BUILD", "sh_library(name = 'b')", "undefined_symbol");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        update(defaultFlags().with(KEEP_GOING), "//parent:a");
        assertContainsEventWithFrequency("name 'undefined_symbol' is not defined", 1);
        assertContainsEventWithFrequency(("Target '//child:b' contains an error and its package is in error and referenced " + "by '//parent:a'"), 1);
    }

    @Test
    public void testNonTopLevelErrorsPrintedExactlyOnce_ActionListener() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        scratch.file("parent/BUILD", "sh_library(name = 'a', deps = ['//child:b'])");
        scratch.file("child/BUILD", "sh_library(name = 'b')", "undefined_symbol");
        scratch.file("okay/BUILD", "sh_binary(name = 'okay', srcs = ['okay.sh'])");
        useConfiguration("--experimental_action_listener=//parent:a");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//okay");
            Assert.fail();
        } catch (ViewCreationFailedException e) {
            // Expected.
        }
        assertContainsEventWithFrequency("name 'undefined_symbol' is not defined", 1);
        assertContainsEventWithFrequency(("Target '//child:b' contains an error and its package is in error and referenced " + "by '//parent:a'"), 1);
    }

    @Test
    public void testNonTopLevelErrorsPrintedExactlyOnce_ActionListener_KeepGoing() throws Exception {
        if ((getInternalTestExecutionMode()) != (NORMAL)) {
            // TODO(b/67651960): fix or justify disabling.
            return;
        }
        scratch.file("parent/BUILD", "sh_library(name = 'a', deps = ['//child:b'])");
        scratch.file("child/BUILD", "sh_library(name = 'b')", "undefined_symbol");
        scratch.file("okay/BUILD", "sh_binary(name = 'okay', srcs = ['okay.sh'])");
        useConfiguration("--experimental_action_listener=//parent:a");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        update(defaultFlags().with(KEEP_GOING), "//okay");
        assertContainsEventWithFrequency("name 'undefined_symbol' is not defined", 1);
        assertContainsEventWithFrequency(("Target '//child:b' contains an error and its package is in error and referenced " + "by '//parent:a'"), 1);
    }

    @Test
    public void testTopLevelTargetsAreTrimmedWithTrimmedConfigurations() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name='x', ", "        srcs=['x.sh'])");
        useConfiguration("--experimental_dynamic_configs=on");
        AnalysisResult res = update("//foo:x");
        ConfiguredTarget topLevelTarget = Iterables.getOnlyElement(res.getTargetsToBuild());
        assertThat(getConfiguration(topLevelTarget).getFragmentsMap().keySet()).containsExactlyElementsIn(ruleClassProvider.getUniversalFragments());
    }

    /**
     * Here, injecting_rule injects an aspect which acts on a action_rule() and registers an action.
     * The action_rule() registers another action of its own.
     *
     * <p>This test asserts that both actions are reported.
     */
    @Test
    public void ruleExtraActionsDontHideAspectExtraActions() throws Exception {
        useConfiguration("--experimental_action_listener=//pkg:listener");
        scratch.file("x/BUILD", "load(':extension.bzl', 'injecting_rule', 'action_rule')", "injecting_rule(name='a', deps=[':b'])", "action_rule(name='b')");
        scratch.file("x/extension.bzl", "def _aspect1_impl(target, ctx):", "  ctx.actions.do_nothing(mnemonic='Mnemonic')", "  return struct()", "aspect1 = aspect(_aspect1_impl, attr_aspects=['deps'])", "", "def _injecting_rule_impl(ctx):", "  return struct()", "injecting_rule = rule(_injecting_rule_impl, ", "    attrs = { 'deps' : attr.label_list(aspects = [aspect1]) })", "", "def _action_rule_impl(ctx):", "  out = ctx.actions.declare_file(ctx.label.name)", "  ctx.actions.run_shell(outputs = [out], command = 'dontcare', mnemonic='Mnemonic')", "  return struct()", "action_rule = rule(_action_rule_impl, attrs = { 'deps' : attr.label_list() })");
        scratch.file("pkg/BUILD", "extra_action(name='xa', cmd='echo dont-care')", "action_listener(name='listener', mnemonics=['Mnemonic'], extra_actions=[':xa'])");
        AnalysisResult analysisResult = update("//x:a");
        List<String> owners = new ArrayList<>();
        for (Artifact artifact : analysisResult.getTopLevelArtifactsToOwnerLabels().getArtifacts()) {
            if ("xa".equals(artifact.getExtension())) {
                owners.add(artifact.getOwnerLabel().toString());
            }
        }
        assertThat(owners).containsExactly("//x:b", "//x:b");
    }

    @Test
    public void testErrorMessageForMissingPackageGroup() throws Exception {
        scratch.file("apple/BUILD", "py_library(name='apple', visibility=['//non:existent'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//apple");
            Assert.fail();
        } catch (ViewCreationFailedException e) {
            // Expected.
        }
        assertDoesNotContainEvent("implicitly depends upon");
    }

    @Test
    public void allowedRuleClassesAndAllowedRuleClassesWithWarning() throws Exception {
        setRulesAvailableInTests(((MockRule) (() -> MockRule.define("custom_rule", attr("deps", BuildType.LABEL_LIST).allowedFileTypes().allowedRuleClasses("java_library", "java_binary").allowedRuleClassesWithWarning("genrule")))));
        scratch.file("foo/BUILD", "genrule(", "    name = 'genlib',", "    srcs = [],", "    outs = ['genlib.out'],", "    cmd = 'echo hi > $@')", "custom_rule(", "    name = 'foo',", "    deps = [':genlib'])");
        update("//foo");
        assertContainsEvent(("WARNING /workspace/foo/BUILD:8:12: in deps attribute of custom_rule rule " + ("//foo:foo: genrule rule '//foo:genlib' is unexpected here (expected java_library or " + "java_binary); continuing anyway")));
    }

    @Test
    public void errorInImplicitDeps() throws Exception {
        setRulesAvailableInTests(((MockRule) (() -> {
            try {
                return MockRule.define("custom_rule", attr("$implicit1", BuildType.LABEL_LIST).defaultValue(ImmutableList.of(Label.parseAbsoluteUnchecked("//bad2:label"), Label.parseAbsoluteUnchecked("//foo:dep"))), attr("$implicit2", BuildType.LABEL).defaultValue(Label.parseAbsoluteUnchecked("//bad:label")));
            } catch ( e) {
                throw new <e>IllegalStateException();
            }
        })));
        scratch.file("foo/BUILD", "custom_rule(name = 'foo')", "sh_library(name = 'dep')");
        scratch.file("bad/BUILD", "sh_library(name = 'other_label', nonexistent_attribute = 'blah')", "sh_library(name = 'label')");
        // bad2/BUILD is completely missing.
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        update(defaultFlags().with(KEEP_GOING), "//foo:foo");
        assertContainsEvent(("every rule of type custom_rule implicitly depends upon the target '//bad2:label', but this" + (" target could not be found because of: no such package 'bad2': BUILD file not found " + "on package path")));
        assertContainsEvent(("every rule of type custom_rule implicitly depends upon the target '//bad:label', but this " + ("target could not be found because of: Target '//bad:label' contains an error and its" + " package is in error")));
    }

    @Test
    public void onlyAllowedRuleClassesWithWarning() throws Exception {
        setRulesAvailableInTests(((MockRule) (() -> MockRule.define("custom_rule", attr("deps", BuildType.LABEL_LIST).allowedFileTypes().allowedRuleClassesWithWarning("genrule")))));
        scratch.file("foo/BUILD", "genrule(", "    name = 'genlib',", "    srcs = [],", "    outs = ['genlib.out'],", "    cmd = 'echo hi > $@')", "custom_rule(", "    name = 'foo',", "    deps = [':genlib'])");
        update("//foo");
        assertContainsEvent(("WARNING /workspace/foo/BUILD:8:12: in deps attribute of custom_rule rule " + "//foo:foo: genrule rule '//foo:genlib' is unexpected here; continuing anyway"));
    }

    /**
     * Runs the same test with trimmed configurations.
     */
    @TestSpec(size = Suite.SMALL_TESTS)
    @RunWith(JUnit4.class)
    public static class WithTrimmedConfigurations extends BuildViewTest {
        @Override
        protected AnalysisTestCase.FlagBuilder defaultFlags() {
            return super.defaultFlags().with(TRIMMED_CONFIGURATIONS);
        }
    }

    /**
     * Runs the same test with the Skyframe-based analysis prep.
     */
    @TestSpec(size = Suite.SMALL_TESTS)
    @RunWith(JUnit4.class)
    public static class WithSkyframePrepareAnalysis extends BuildViewTest {
        @Override
        protected AnalysisTestCase.FlagBuilder defaultFlags() {
            return super.defaultFlags().with(SKYFRAME_PREPARE_ANALYSIS);
        }
    }

    /**
     * Runs the same test with the Skyframe-based analysis prep and trimmed configurations.
     */
    @TestSpec(size = Suite.SMALL_TESTS)
    @RunWith(JUnit4.class)
    public static class WithSkyframePrepareAnalysisAndTrimmedConfigurations extends BuildViewTest {
        @Override
        protected AnalysisTestCase.FlagBuilder defaultFlags() {
            return super.defaultFlags().with(SKYFRAME_PREPARE_ANALYSIS).with(TRIMMED_CONFIGURATIONS);
        }

        // We can't recover from dependency cycles in TransitiveTargetFunction, so we disable the tests
        // for now. We will likely have to fix this before we can enable the new code by default.
        @Override
        @Test
        public void testCircularDependency() {
        }

        @Override
        @Test
        public void testErrorBelowCycleKeepGoing() {
        }

        @Override
        @Test
        public void testCircularDependencyBelowTwoTargets() {
        }

        @Override
        @Test
        public void testCycleReporting_TargetCycleWhenPackageInError() {
        }

        @Override
        @Test
        public void testCircularDependencyWithKeepGoing() {
        }

        @Override
        @Test
        public void testErrorBelowCycle() {
        }

        @Override
        @Test
        public void testAnalysisReportsDependencyCycle() {
        }
    }
}

