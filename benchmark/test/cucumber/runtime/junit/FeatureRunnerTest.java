package cucumber.runtime.junit;


import cucumber.runtime.model.CucumberFeature;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FeatureRunnerTest {
    @Test
    public void should_not_create_step_descriptions_by_default() throws Exception {
        CucumberFeature cucumberFeature = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((((((((("Feature: feature name\n" + "  Background:\n") + "    Given background step\n") + "  Scenario: A\n") + "    Then scenario name\n") + "  Scenario: B\n") + "    Then scenario name\n") + "  Scenario Outline: C\n") + "    Then scenario <name>\n") + "  Examples:\n") + "    | name |\n") + "    | C    |\n") + "    | D    |\n") + "    | E    |\n")));
        FeatureRunner runner = createFeatureRunner(cucumberFeature);
        Description feature = runner.getDescription();
        Description scenarioA = feature.getChildren().get(0);
        Assert.assertTrue(scenarioA.getChildren().isEmpty());
        Description scenarioB = feature.getChildren().get(1);
        Assert.assertTrue(scenarioB.getChildren().isEmpty());
        Description scenarioC0 = feature.getChildren().get(2);
        Assert.assertTrue(scenarioC0.getChildren().isEmpty());
        Description scenarioC1 = feature.getChildren().get(3);
        Assert.assertTrue(scenarioC1.getChildren().isEmpty());
        Description scenarioC2 = feature.getChildren().get(4);
        Assert.assertTrue(scenarioC2.getChildren().isEmpty());
    }

    @Test
    public void should_not_issue_notification_for_steps_by_default_scenario_outline_with_two_examples_table_and_background() throws Throwable {
        CucumberFeature feature = TestPickleBuilder.parseFeature("path/test.feature", ("" + (((((((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario Outline: scenario outline name\n") + "    When <x> step\n") + "    Then <y> step\n") + "    Examples: examples 1 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n") + "      | second | third |\n") + "    Examples: examples 2 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n")));
        RunNotifier notifier = runFeatureWithNotifier(feature);
        InOrder order = Mockito.inOrder(notifier);
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
        order.verify(notifier, Mockito.times(3)).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("scenario outline name(feature name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
        order.verify(notifier, Mockito.times(3)).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("scenario outline name(feature name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
        order.verify(notifier, Mockito.times(3)).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("scenario outline name(feature name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name(feature name)")));
    }

    @Test
    public void should_not_issue_notification_for_steps_by_default_two_scenarios_with_background() throws Throwable {
        CucumberFeature feature = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario: scenario_1 name\n") + "    When second step\n") + "    Then third step\n") + "  Scenario: scenario_2 name\n") + "    Then another second step\n")));
        RunNotifier notifier = runFeatureWithNotifier(feature);
        InOrder order = Mockito.inOrder(notifier);
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_1 name(feature name)")));
        order.verify(notifier, Mockito.times(3)).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("scenario_1 name(feature name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_1 name(feature name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_2 name(feature name)")));
        order.verify(notifier, Mockito.times(2)).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("scenario_2 name(feature name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_2 name(feature name)")));
    }

    @Test
    public void should_populate_descriptions_with_stable_unique_ids() throws Exception {
        CucumberFeature cucumberFeature = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((((((((("Feature: feature name\n" + "  Background:\n") + "    Given background step\n") + "  Scenario: A\n") + "    Then scenario name\n") + "  Scenario: B\n") + "    Then scenario name\n") + "  Scenario Outline: C\n") + "    Then scenario <name>\n") + "  Examples:\n") + "    | name |\n") + "    | C    |\n") + "    | D    |\n") + "    | E    |\n")));
        FeatureRunner runner = createFeatureRunner(cucumberFeature);
        FeatureRunner rerunner = createFeatureRunner(cucumberFeature);
        Set<Description> descriptions = new HashSet<Description>();
        FeatureRunnerTest.assertDescriptionIsUnique(runner.getDescription(), descriptions);
        FeatureRunnerTest.assertDescriptionIsPredictable(runner.getDescription(), descriptions);
        FeatureRunnerTest.assertDescriptionIsPredictable(rerunner.getDescription(), descriptions);
    }

    @Test
    public void step_descriptions_can_be_turned_on() throws Exception {
        CucumberFeature cucumberFeature = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((((((((("Feature: feature name\n" + "  Background:\n") + "    Given background step\n") + "  Scenario: A\n") + "    Then scenario name\n") + "  Scenario: B\n") + "    Then scenario name\n") + "  Scenario Outline: C\n") + "    Then scenario <name>\n") + "  Examples:\n") + "    | name |\n") + "    | C    |\n") + "    | D    |\n") + "    | E    |\n")));
        FeatureRunner runner = createFeatureRunner(cucumberFeature, "--step-notifications");
        Description feature = runner.getDescription();
        Description scenarioA = feature.getChildren().get(0);
        Assert.assertEquals(2, scenarioA.getChildren().size());
        Description scenarioB = feature.getChildren().get(1);
        Assert.assertEquals(2, scenarioB.getChildren().size());
        Description scenarioC0 = feature.getChildren().get(2);
        Assert.assertEquals(2, scenarioC0.getChildren().size());
        Description scenarioC1 = feature.getChildren().get(3);
        Assert.assertEquals(2, scenarioC1.getChildren().size());
        Description scenarioC2 = feature.getChildren().get(4);
        Assert.assertEquals(2, scenarioC2.getChildren().size());
    }

    @Test
    public void step_notification_can_be_turned_on_scenario_outline_with_two_examples_table_and_background() throws Throwable {
        CucumberFeature feature = TestPickleBuilder.parseFeature("path/test.feature", ("" + (((((((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario Outline: scenario outline name\n") + "    When <x> step\n") + "    Then <y> step\n") + "    Examples: examples 1 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n") + "      | second | third |\n") + "    Examples: examples 2 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n")));
        RunNotifier notifier = runFeatureWithNotifier(feature, "--step-notifications");
        InOrder order = Mockito.inOrder(notifier);
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario outline name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario outline name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario outline name")));
    }

    @Test
    public void step_notification_can_be_turned_on_two_scenarios_with_background() throws Throwable {
        CucumberFeature feature = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario: scenario_1 name\n") + "    When second step\n") + "    Then third step\n") + "  Scenario: scenario_2 name\n") + "    Then another second step\n")));
        RunNotifier notifier = runFeatureWithNotifier(feature, "--step-notifications");
        InOrder order = Mockito.inOrder(notifier);
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_1 name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario_1 name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("first step(scenario_1 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario_1 name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario_1 name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("second step(scenario_1 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("second step(scenario_1 name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario_1 name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("third step(scenario_1 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("third step(scenario_1 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_1 name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_2 name")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario_2 name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("first step(scenario_2 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("first step(scenario_2 name)")));
        order.verify(notifier).fireTestStarted(ArgumentMatchers.argThat(new DescriptionMatcher("another second step(scenario_2 name)")));
        order.verify(notifier).fireTestAssumptionFailed(ArgumentMatchers.argThat(new FailureMatcher("another second step(scenario_2 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("another second step(scenario_2 name)")));
        order.verify(notifier).fireTestFinished(ArgumentMatchers.argThat(new DescriptionMatcher("scenario_2 name")));
    }
}

