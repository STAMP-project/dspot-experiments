package cucumber.runtime.formatter;


import TimelineFormatter.GroupData;
import TimelineFormatter.TestData;
import cucumber.api.Result;
import cucumber.runner.TestHelper;
import cucumber.runtime.model.CucumberFeature;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class TimelineFormatterTest {
    private static final Comparator<TimelineFormatter.TestData> TEST_DATA_COMPARATOR = new Comparator<TimelineFormatter.TestData>() {
        @Override
        public int compare(TimelineFormatter.TestData o1, TimelineFormatter.TestData o2) {
            return o1.id.compareTo(o2.id);
        }
    };

    private static final String REPORT_TEMPLATE_RESOURCE_DIR = "src/main/resources/io/cucumber/formatter/timeline";

    private static final String REPORT_JS = "report.js";

    private static final long STEP_DURATION_MS = 1000;

    private final Gson gson = new GsonBuilder().create();

    private final Map<String, Result> stepsToResult = new HashMap<>();

    private final Map<String, String> stepsToLocation = new HashMap<>();

    private final CucumberFeature failingFeature = TestHelper.feature("some/path/failing.feature", ("" + ((((((((((((("Feature: Failing Feature\n" + "  Background:\n") + "    Given bg_1\n") + "    When bg_2\n") + "    Then bg_3\n") + "  @TagA\n") + "  Scenario: Scenario 1\n") + "    Given step_01\n") + "    When step_02\n") + "    Then step_03\n") + "  Scenario: Scenario 2\n") + "    Given step_01\n") + "    When step_02\n") + "    Then step_03")));

    private final CucumberFeature successfulFeature = TestHelper.feature("some/path/successful.feature", ("" + ((((((((("Feature: Successful Feature\n" + "  Background:\n") + "    Given bg_1\n") + "    When bg_2\n") + "    Then bg_3\n") + "  @TagB @TagC\n") + "  Scenario: Scenario 1\n") + "    Given step_10\n") + "    When step_20\n") + "    Then step_30")));

    private final CucumberFeature pendingFeature = TestHelper.feature("some/path/pending.feature", ("" + (((((((("Feature: Pending Feature\n" + "  Background:\n") + "    Given bg_1\n") + "    When bg_2\n") + "    Then bg_3\n") + "  Scenario: Scenario 1\n") + "    Given step_10\n") + "    When step_20\n") + "    Then step_50")));

    private File reportDir;

    private File reportJsFile;

    @Test
    public void shouldWriteAllRequiredFilesToOutputDirectory() throws IOException {
        runFormatterWithPlugin();
        Assert.assertTrue(((TimelineFormatterTest.REPORT_JS) + ": did not exist in output dir"), reportJsFile.exists());
        final List<String> files = Arrays.asList("index.html", "formatter.js", "jquery-3.3.1.min.js", "vis.min.css", "vis.min.js", "vis.override.css");
        for (final String e : files) {
            final File actualFile = new File(reportDir, e);
            Assert.assertTrue((e + ": did not exist in output dir"), actualFile.exists());
            final String actual = readFileContents(actualFile.getAbsolutePath());
            final String expected = readFileContents(new File(TimelineFormatterTest.REPORT_TEMPLATE_RESOURCE_DIR, e).getAbsolutePath());
            Assert.assertEquals((e + " differs"), expected, actual);
        }
    }

    @Test
    public void shouldWriteItemsCorrectlyToReportJsWhenRunInParallel() throws Throwable {
        TestHelper.builder().withFeatures(failingFeature, successfulFeature, pendingFeature).withRuntimeArgs("--plugin", ("timeline:" + (reportDir.getAbsolutePath())), "--threads", "3").withStepsToResult(stepsToResult).withStepsToLocation(stepsToLocation).withTimeServiceIncrement(TimeUnit.MILLISECONDS.toNanos(TimelineFormatterTest.STEP_DURATION_MS)).build().run();
        final TimelineFormatter[] expectedTests = getExpectedTestData(0L);// Have to ignore actual thread id and just check not null

        final TimelineFormatterTest.ActualReportOutput actualOutput = readReport();
        // Cannot verify size / contents of Groups as multi threading not guaranteed in Travis CI
        assertThat(actualOutput.groups).isNotEmpty();
        for (int i = 0; i < (actualOutput.groups.size()); i++) {
            final TimelineFormatter.GroupData actual = actualOutput.groups.get(i);
            Assert.assertTrue(String.format("id on group %s, was not as expected", i), ((actual.id) > 0));
            Assert.assertNotNull(String.format("content on group %s, was not as expected", i), actual.content);
        }
        // Sort the tests, output order is not a problem but obviously asserting it is
        Collections.sort(actualOutput.tests, TimelineFormatterTest.TEST_DATA_COMPARATOR);
        assertTimelineTestDataIsAsExpected(expectedTests, actualOutput.tests, false, false);
    }

    @Test
    public void shouldWriteItemsAndGroupsCorrectlyToReportJs() throws Throwable {
        runFormatterWithPlugin();
        Assert.assertTrue(((TimelineFormatterTest.REPORT_JS) + " was not found"), reportJsFile.exists());
        final Long groupId = Thread.currentThread().getId();
        final String groupName = Thread.currentThread().toString();
        final TimelineFormatter[] expectedTests = getExpectedTestData(groupId);
        final TimelineFormatter[] expectedGroups = gson.fromJson(("[\n" + (((("  {\n" + "    \"id\": groupId,\n") + "    \"content\": \"groupName\"\n") + "  }\n") + "]")).replaceAll("groupId", groupId.toString()).replaceAll("groupName", groupName), GroupData[].class);
        final TimelineFormatterTest.ActualReportOutput actualOutput = readReport();
        // Sort the tests, output order is not a problem but obviously asserting it is
        Collections.sort(actualOutput.tests, TimelineFormatterTest.TEST_DATA_COMPARATOR);
        assertTimelineTestDataIsAsExpected(expectedTests, actualOutput.tests, true, true);
        assertTimelineGroupDataIsAsExpected(expectedGroups, actualOutput.groups);
    }

    private class ActualReportOutput {
        private final List<TimelineFormatter.TestData> tests;

        private final List<TimelineFormatter.GroupData> groups;

        ActualReportOutput(final TimelineFormatter[] tests, final TimelineFormatter[] groups) {
            this.tests = Arrays.asList(tests);
            this.groups = Arrays.asList(groups);
        }
    }
}

