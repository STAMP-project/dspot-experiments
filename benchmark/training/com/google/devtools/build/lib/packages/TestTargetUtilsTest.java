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
package com.google.devtools.build.lib.packages;


import PathFragment.EMPTY_FRAGMENT;
import TestSize.LARGE;
import TestSize.SMALL;
import TestTimeout.LONG;
import TestTimeout.SHORT;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.ResolvedTargets;
import com.google.devtools.build.lib.cmdline.TargetParsingException;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.ExtendedEventHandler;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.packages.util.PackageLoadingTestCase;
import com.google.devtools.build.lib.pkgcache.LoadingOptions;
import com.google.devtools.build.lib.pkgcache.TargetProvider;
import com.google.devtools.build.lib.pkgcache.TestFilter;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.EnumSet;
import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class TestTargetUtilsTest extends PackageLoadingTestCase {
    private Target test1;

    private Target test2;

    private Target test1b;

    private Target suite;

    @Test
    public void testFilterBySize() throws Exception {
        Predicate<Target> sizeFilter = TestFilter.testSizeFilter(EnumSet.of(SMALL, LARGE));
        assertThat(sizeFilter.test(test1)).isTrue();
        assertThat(sizeFilter.test(test2)).isTrue();
        assertThat(sizeFilter.test(test1b)).isTrue();
        sizeFilter = TestFilter.testSizeFilter(EnumSet.of(SMALL));
        assertThat(sizeFilter.test(test1)).isTrue();
        assertThat(sizeFilter.test(test2)).isTrue();
        assertThat(sizeFilter.test(test1b)).isFalse();
    }

    @Test
    public void testFilterByLang() throws Exception {
        StoredEventHandler eventHandler = new StoredEventHandler();
        LoadingOptions options = new LoadingOptions();
        options.testLangFilterList = ImmutableList.of("nonexistent", "existent", "-noexist", "-exist");
        options.testSizeFilterSet = ImmutableSet.of();
        options.testTimeoutFilterSet = ImmutableSet.of();
        options.testTagFilterList = ImmutableList.of();
        TestFilter filter = TestFilter.forOptions(options, eventHandler, ImmutableSet.of("existent_test", "exist_test"));
        assertThat(eventHandler.getEvents()).hasSize(2);
        Package pkg = Mockito.mock(Package.class);
        RuleClass ruleClass = Mockito.mock(RuleClass.class);
        Rule mockRule = new Rule(pkg, null, ruleClass, Location.fromPathFragment(EMPTY_FRAGMENT), new AttributeContainer(ruleClass));
        Mockito.when(ruleClass.getName()).thenReturn("existent_library");
        assertThat(filter.apply(mockRule)).isTrue();
        Mockito.when(ruleClass.getName()).thenReturn("exist_library");
        assertThat(filter.apply(mockRule)).isFalse();
        assertThat(eventHandler.getEvents()).contains(Event.warn("Unknown language 'nonexistent' in --test_lang_filters option"));
        assertThat(eventHandler.getEvents()).contains(Event.warn("Unknown language 'noexist' in --test_lang_filters option"));
    }

    @Test
    public void testFilterByTimeout() throws Exception {
        scratch.file("timeouts/BUILD", "sh_test(name = 'long_timeout',", "          srcs = ['a.sh'],", "          size = 'small',", "          timeout = 'long')", "sh_test(name = 'short_timeout',", "          srcs = ['b.sh'],", "          size = 'small')", "sh_test(name = 'moderate_timeout',", "          srcs = ['c.sh'],", "          size = 'small',", "          timeout = 'moderate')");
        Target longTest = getTarget("//timeouts:long_timeout");
        Target shortTest = getTarget("//timeouts:short_timeout");
        Target moderateTest = getTarget("//timeouts:moderate_timeout");
        Predicate<Target> timeoutFilter = TestFilter.testTimeoutFilter(EnumSet.of(SHORT, LONG));
        assertThat(timeoutFilter.test(longTest)).isTrue();
        assertThat(timeoutFilter.test(shortTest)).isTrue();
        assertThat(timeoutFilter.test(moderateTest)).isFalse();
    }

    @Test
    public void testExpandTestSuites() throws Exception {
        assertExpandedSuites(Sets.newHashSet(test1, test2), Sets.newHashSet(test1, test2));
        assertExpandedSuites(Sets.newHashSet(test1, test2), Sets.newHashSet(suite));
        assertExpandedSuites(Sets.newHashSet(test1, test2, test1b), Sets.newHashSet(test1, suite, test1b));
        // The large test if returned as filtered from the test_suite rule, but should still be in the
        // result set as it's explicitly added.
        assertExpandedSuites(Sets.newHashSet(test1, test2, test1b), ImmutableSet.<Target>of(test1b, suite));
    }

    @Test
    public void testSkyframeExpandTestSuites() throws Exception {
        assertExpandedSuitesSkyframe(Sets.newHashSet(test1, test2), ImmutableSet.<Target>of(test1, test2));
        assertExpandedSuitesSkyframe(Sets.newHashSet(test1, test2), ImmutableSet.<Target>of(suite));
        assertExpandedSuitesSkyframe(Sets.newHashSet(test1, test2, test1b), ImmutableSet.<Target>of(test1, suite, test1b));
        // The large test if returned as filtered from the test_suite rule, but should still be in the
        // result set as it's explicitly added.
        assertExpandedSuitesSkyframe(Sets.newHashSet(test1, test2, test1b), ImmutableSet.<Target>of(test1b, suite));
    }

    @Test
    public void testExpandTestSuitesKeepGoing() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("broken/BUILD", "test_suite(name = 'broken', tests = ['//missing:missing_test'])");
        ResolvedTargets<Target> actual = /* strict= */
        /* keepGoing= */
        TestTargetUtils.expandTestSuites(getPackageManager(), reporter, Sets.newHashSet(getTarget("//broken")), false, true);
        assertThat(actual.hasError()).isTrue();
        assertThat(actual.getTargets()).isEmpty();
    }

    private static final Function<Target, Label> TO_LABEL = new Function<Target, Label>() {
        @Override
        public Label apply(Target input) {
            return input.getLabel();
        }
    };

    @Test
    public void testExpandTestSuitesInterrupted() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("broken/BUILD", "test_suite(name = 'broken', tests = ['//missing:missing_test'])");
        try {
            /* strict= */
            /* keepGoing= */
            TestTargetUtils.expandTestSuites(new TargetProvider() {
                @Override
                public Target getTarget(ExtendedEventHandler eventHandler, Label label) throws InterruptedException {
                    throw new InterruptedException();
                }
            }, reporter, Sets.newHashSet(getTarget("//broken")), false, true);
        } catch (TargetParsingException e) {
            assertThat(e).hasMessageThat().isNotNull();
        }
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }
}

