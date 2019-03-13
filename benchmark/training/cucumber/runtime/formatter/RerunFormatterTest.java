package cucumber.runtime.formatter;


import cucumber.api.Result;
import cucumber.runner.TestHelper;
import cucumber.runtime.model.CucumberFeature;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class RerunFormatterTest {
    private final List<CucumberFeature> features = new ArrayList<>();

    private final Map<String, Result> stepsToResult = new HashMap<>();

    private final List<AbstractMap.SimpleEntry<String, Result>> hooks = new ArrayList<>();

    @Test
    public void should_leave_report_empty_when_exit_code_is_zero() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: feature name\n" + "  Scenario: passed scenario\n") + "    Given passed step\n") + "  Scenario: pending scenario\n") + "    Given pending step\n") + "  Scenario: undefined scenario\n") + "    Given undefined step\n")));
        features.add(feature);
        stepsToResult.put("passed step", TestHelper.result("passed"));
        stepsToResult.put("pending step", TestHelper.result("pending"));
        stepsToResult.put("undefined step", TestHelper.result("undefined"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("", formatterOutput);
    }

    @Test
    public void should_put_data_in_report_when_exit_code_is_non_zero() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: feature name\n" + "  Scenario: failed scenario\n") + "    Given failed step\n") + "  Scenario: pending scenario\n") + "    Given pending step\n") + "  Scenario: undefined scenario\n") + "    Given undefined step\n")));
        features.add(feature);
        stepsToResult.put("failed step", TestHelper.result("failed"));
        stepsToResult.put("pending step", TestHelper.result("pending"));
        stepsToResult.put("undefined step", TestHelper.result("undefined"));
        String formatterOutput = runFeaturesWithFormatter(true);
        Assert.assertEquals("file:path/test.feature:2:4:6\n", formatterOutput);
    }

    @Test
    public void should_use_scenario_location_when_scenario_step_fails() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("failed"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:2\n", formatterOutput);
    }

    @Test
    public void should_use_scenario_location_when_background_step_fails() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((("Feature: feature name\n" + "  Background: the background\n") + "    Given background step\n") + "  Scenario: scenario name\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("background step", TestHelper.result("failed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:4\n", formatterOutput);
    }

    @Test
    public void should_use_example_row_location_when_scenario_outline_fails() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((((("Feature: feature name\n" + "  Scenario Outline: scenario name\n") + "    When executing <row> row\n") + "    Then everything is ok\n") + "    Examples:\n") + "    |  row   |\n") + "    | first  |\n") + "    | second |")));
        features.add(feature);
        stepsToResult.put("executing first row", TestHelper.result("passed"));
        stepsToResult.put("executing second row", TestHelper.result("failed"));
        stepsToResult.put("everything is ok", TestHelper.result("passed"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:8\n", formatterOutput);
    }

    @Test
    public void should_use_scenario_location_when_before_hook_fails() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("failed")));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:2\n", formatterOutput);
    }

    @Test
    public void should_use_scenario_location_when_after_hook_fails() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("failed")));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:2\n", formatterOutput);
    }

    @Test
    public void should_one_entry_for_feature_with_many_failing_scenarios() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: feature name\n" + "  Scenario: scenario 1 name\n") + "    When first step\n") + "    Then second step\n") + "  Scenario: scenario 2 name\n") + "    When third step\n") + "    Then forth step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("failed"));
        stepsToResult.put("third step", TestHelper.result("failed"));
        stepsToResult.put("forth step", TestHelper.result("passed"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/test.feature:2:5\n", formatterOutput);
    }

    @Test
    public void should_one_entry_for_each_failing_feature() throws Throwable {
        CucumberFeature feature1 = TestHelper.feature("path/first.feature", ("" + ((("Feature: feature 1 name\n" + "  Scenario: scenario 1 name\n") + "    When first step\n") + "    Then second step\n")));
        CucumberFeature feature2 = TestHelper.feature("path/second.feature", ("" + ((("Feature: feature 2 name\n" + "  Scenario: scenario 2 name\n") + "    When third step\n") + "    Then forth step\n")));
        features.add(feature1);
        features.add(feature2);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("failed"));
        stepsToResult.put("third step", TestHelper.result("failed"));
        stepsToResult.put("forth step", TestHelper.result("passed"));
        String formatterOutput = runFeaturesWithFormatter(false);
        Assert.assertEquals("file:path/first.feature:2\nfile:path/second.feature:2\n", formatterOutput);
    }
}

