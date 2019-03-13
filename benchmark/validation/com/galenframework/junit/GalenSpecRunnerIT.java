/**
 * *****************************************************************************
 * Copyright 2018 Ivan Shubin http://galenframework.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package com.galenframework.junit;


import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.Result;
import org.junit.runner.RunWith;


public class GalenSpecRunnerIT {
    private static final String HTML_FILE = "/tmp/GalsenSpecRunnerIT.html";

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class ValidSpec {}

    @Test
    public void shouldBeSuccessfulForValidSpec() {
        Assume.assumeTrue(GalenSpecRunnerIT.existsTmpFolder());
        Result result = runTest(GalenSpecRunnerIT.ValidSpec.class);
        // We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(true));
        collector.checkThat("has no failures", result.getFailures(), is(empty()));
        collector.checkThat("has a test for each spec", result.getRunCount(), is(4));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/inapplicable.gspec")
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class InapplicableSpec {}

    @Test
    public void shouldFailForInapplicableSpec() {
        Assume.assumeTrue(GalenSpecRunnerIT.existsTmpFolder());
        Result result = runTest(GalenSpecRunnerIT.InapplicableSpec.class);
        // We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is not successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failures", result.getFailures(), hasSize(2));
        collector.checkThat("has only assertion errors", result.getFailures(), not(hasFailureWithException(not(instanceOf(AssertionError.class)))));
        collector.checkThat("describes failure", result.getFailures(), hasFailureWithException(hasProperty("message", equalTo("[\"first_paragraph\" width is 400px but it should be less than 10px]"))));
        collector.checkThat("has a test for each spec", result.getRunCount(), is(3));
    }

    @RunWith(GalenSpecRunner.class)
    @Include("variantA")
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/tag.gspec")
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class ExcludeTag {}

    @Test
    public void shouldNotRunTestsForSectionsThatAreExcluded() {
        Assume.assumeTrue(GalenSpecRunnerIT.existsTmpFolder());
        Result result = runTest(GalenSpecRunnerIT.ExcludeTag.class);
        collector.checkThat("has only tests for not excluded sections", result.getRunCount(), is(3));
    }

    @RunWith(GalenSpecRunner.class)
    @Include("variantA")
    @Exclude("variantB")
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/tag.gspec")
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class IncludeTag {}

    @Test
    public void shouldOnlyRunTestsForSectionsThatAreIncluded() {
        Assume.assumeTrue(GalenSpecRunnerIT.existsTmpFolder());
        Result result = runTest(GalenSpecRunnerIT.IncludeTag.class);
        collector.checkThat("has only tests for included sections", result.getRunCount(), is(2));
    }

    @RunWith(GalenSpecRunner.class)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class NoSizeAnnotation {}

    @Test
    public void shouldProvideHelpfulMessageIfSizeAnnotationsAreMissing() {
        Result result = runTest(GalenSpecRunnerIT.NoSizeAnnotation.class);
        // We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(), hasFailureWithException(hasProperty("message", equalTo("The annotation @Size is missing."))));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Url("file://" + (GalenSpecRunnerIT.HTML_FILE))
    public static class NoSpecAnnotation {}

    @Test
    public void shouldProvideHelpfulMessageIfSpecAnnotationIsMissing() {
        Result result = runTest(GalenSpecRunnerIT.NoSpecAnnotation.class);
        // We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(), hasFailureWithException(hasProperty("message", equalTo("The annotation @Spec is missing."))));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    public static class NoUrlAnnotation {}

    @Test
    public void shouldProvideHelpfulMessageIfUrlAnnotationIsMissing() {
        Result result = runTest(GalenSpecRunnerIT.NoUrlAnnotation.class);
        // We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(), hasFailureWithException(hasProperty("message", equalTo("The annotation @Url is missing."))));
    }
}

