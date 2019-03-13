package cucumber.runtime.formatter;


import cucumber.api.Result;
import cucumber.runner.TestHelper;
import cucumber.runtime.model.CucumberFeature;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.mockito.stubbing.Answer;


public class JUnitFormatterTest {
    private final List<CucumberFeature> features = new ArrayList<>();

    private final Map<String, Result> stepsToResult = new HashMap<>();

    private final Map<String, String> stepsToLocation = new HashMap<>();

    private final List<AbstractMap.SimpleEntry<String, Result>> hooks = new ArrayList<>();

    private final List<String> hookLocations = new ArrayList<>();

    private final List<Answer<Object>> hookActions = new ArrayList<>();

    private Long stepDuration = null;

    @Test
    public void featureSimpleTest() throws Exception {
        File report = runFeaturesWithJunitFormatter(Arrays.asList("classpath:cucumber/runtime/formatter/JUnitFormatterTest_1.feature"));
        assertXmlEqual("cucumber/runtime/formatter/JUnitFormatterTest_1.report.xml", report);
    }

    @Test
    public void featureWithBackgroundTest() throws Exception {
        File report = runFeaturesWithJunitFormatter(Arrays.asList("classpath:cucumber/runtime/formatter/JUnitFormatterTest_2.feature"));
        assertXmlEqual("cucumber/runtime/formatter/JUnitFormatterTest_2.report.xml", report);
    }

    @Test
    public void featureWithOutlineTest() throws Exception {
        File report = runFeaturesWithJunitFormatter(Arrays.asList("classpath:cucumber/runtime/formatter/JUnitFormatterTest_3.feature"));
        assertXmlEqual("cucumber/runtime/formatter/JUnitFormatterTest_3.report.xml", report);
    }

    @Test
    public void featureSimpleStrictTest() throws Exception {
        boolean strict = true;
        File report = runFeaturesWithJunitFormatter(Arrays.asList("classpath:cucumber/runtime/formatter/JUnitFormatterTest_1.feature"), strict);
        assertXmlEqual("cucumber/runtime/formatter/JUnitFormatterTest_1_strict.report.xml", report);
    }

    @Test
    public void should_format_passed_scenario() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + (((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.003\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step............................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_skipped_scenario() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        Throwable exception = new AssumptionViolatedException("message");
        stepsToResult.put("first step", TestHelper.result("skipped", exception));
        stepsToResult.put("second step", TestHelper.result("skipped"));
        stepsToResult.put("third step", TestHelper.result("skipped"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String stackTrace = getStackTrace(exception);
        String expected = ((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + (("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"1\" tests=\"1\" time=\"0.003\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.003\">\n") + "        <skipped message=\"")) + (stackTrace.replace("\n\t", "&#10;&#9;").replaceAll("\r", "&#13;"))) + "\"><![CDATA[") + "Given first step............................................................skipped\n") + "When second step............................................................skipped\n") + "Then third step.............................................................skipped\n") + "\n") + "StackTrace:\n") + stackTrace) + "]]></skipped>\n") + "    </testcase>\n") + "</testsuite>\n";
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_pending_scenario() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("pending"));
        stepsToResult.put("second step", TestHelper.result("skipped"));
        stepsToResult.put("third step", TestHelper.result("undefined"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + (((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"1\" tests=\"1\" time=\"0.003\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.003\">\n") + "        <skipped><![CDATA[") + "Given first step............................................................pending\n") + "When second step............................................................skipped\n") + "Then third step.............................................................undefined\n") + "]]></skipped>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_failed_scenario() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("failed"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((("<testsuite failures=\"1\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.003\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.003\">\n") + "        <failure message=\"the stack trace\"><![CDATA[") + "Given first step............................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................failed\n") + "\n") + "StackTrace:\n") + "the stack trace") + "]]></failure>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_handle_failure_in_before_hook() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("failed")));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((("<testsuite failures=\"1\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.004\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.004\">\n") + "        <failure message=\"the stack trace\"><![CDATA[") + "Given first step............................................................skipped\n") + "When second step............................................................skipped\n") + "Then third step.............................................................skipped\n") + "\n") + "StackTrace:\n") + "the stack trace") + "]]></failure>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_handle_pending_in_before_hook() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("skipped"));
        stepsToResult.put("second step", TestHelper.result("skipped"));
        stepsToResult.put("third step", TestHelper.result("skipped"));
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("pending")));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + (((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"1\" tests=\"1\" time=\"0.004\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.004\">\n") + "        <skipped><![CDATA[") + "Given first step............................................................skipped\n") + "When second step............................................................skipped\n") + "Then third step.............................................................skipped\n") + "]]></skipped>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_handle_failure_in_before_hook_with_background() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + (((("  Background: background name\n" + "    Given first step\n") + "  Scenario: scenario name\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("failed")));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((("<testsuite failures=\"1\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.004\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.004\">\n") + "        <failure message=\"the stack trace\"><![CDATA[") + "Given first step............................................................skipped\n") + "When second step............................................................skipped\n") + "Then third step.............................................................skipped\n") + "\n") + "StackTrace:\n") + "the stack trace") + "]]></failure>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_handle_failure_in_after_hook() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("failed")));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((("<testsuite failures=\"1\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.004\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.004\">\n") + "        <failure message=\"the stack trace\"><![CDATA[") + "Given first step............................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "\n") + "StackTrace:\n") + "the stack trace") + "]]></failure>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_accumulate_time_from_steps_and_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + (("  Scenario: scenario name\n" + "    * first step\n") + "    * second step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("passed")));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"1\" time=\"0.004\">\n" + "    <testcase classname=\"feature name\" name=\"scenario name\" time=\"0.004\">\n") + "        <system-out><![CDATA[") + "* first step................................................................passed\n") + "* second step...............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_scenario_outlines() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((((((("  Scenario Outline: outline_name\n" + "    Given first step \"<arg>\"\n") + "    When second step\n") + "    Then third step\n\n") + "  Examples: examples\n") + "    | arg |\n") + "    |  a  |\n") + "    |  b  |\n")));
        features.add(feature);
        stepsToResult.put("first step \"a\"", TestHelper.result("passed"));
        stepsToResult.put("first step \"b\"", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"2\" time=\"0.006\">\n" + "    <testcase classname=\"feature name\" name=\"outline_name\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"a\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "    <testcase classname=\"feature name\" name=\"outline_name_2\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"b\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_scenario_outlines_with_multiple_examples() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((((((((((("  Scenario Outline: outline name\n" + "    Given first step \"<arg>\"\n") + "    When second step\n") + "    Then third step\n\n") + "  Examples: examples 1\n") + "    | arg |\n") + "    |  a  |\n") + "    |  b  |\n\n") + "  Examples: examples 2\n") + "    | arg |\n") + "    |  c  |\n") + "    |  d  |\n")));
        features.add(feature);
        stepsToResult.put("first step \"a\"", TestHelper.result("passed"));
        stepsToResult.put("first step \"b\"", TestHelper.result("passed"));
        stepsToResult.put("first step \"c\"", TestHelper.result("passed"));
        stepsToResult.put("first step \"d\"", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((((((((((((((((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"4\" time=\"0.012\">\n" + "    <testcase classname=\"feature name\" name=\"outline name\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"a\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "    <testcase classname=\"feature name\" name=\"outline name 2\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"b\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "    <testcase classname=\"feature name\" name=\"outline name 3\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"c\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "    <testcase classname=\"feature name\" name=\"outline name 4\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"d\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }

    @Test
    public void should_format_scenario_outlines_with_arguments_in_name() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((((((("  Scenario Outline: outline name <arg>\n" + "    Given first step \"<arg>\"\n") + "    When second step\n") + "    Then third step\n\n") + "  Examples: examples 1\n") + "    | arg |\n") + "    |  a  |\n") + "    |  b  |\n")));
        features.add(feature);
        stepsToResult.put("first step \"a\"", TestHelper.result("passed"));
        stepsToResult.put("first step \"b\"", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((((((((((((((("<testsuite failures=\"0\" name=\"cucumber.runtime.formatter.JUnitFormatter\" skipped=\"0\" tests=\"2\" time=\"0.006\">\n" + "    <testcase classname=\"feature name\" name=\"outline name a\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"a\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "    <testcase classname=\"feature name\" name=\"outline name b\" time=\"0.003\">\n") + "        <system-out><![CDATA[") + "Given first step \"b\"........................................................passed\n") + "When second step............................................................passed\n") + "Then third step.............................................................passed\n") + "]]></system-out>\n") + "    </testcase>\n") + "</testsuite>\n");
        assertXmlEqual(expected, formatterOutput);
    }
}

