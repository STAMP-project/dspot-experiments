package cucumber.runtime.junit;


import cucumber.runner.ThreadLocalRunnerSupplier;
import cucumber.runtime.junit.PickleRunners.PickleRunner;
import cucumber.runtime.junit.PickleRunners.WithStepDescriptions;
import cucumber.runtime.model.CucumberFeature;
import gherkin.events.PickleEvent;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleStep;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.mockito.Mockito;


public class PickleRunnerWithStepDescriptionsTest {
    @Test
    public void shouldAssignUnequalDescriptionsToDifferentOccurrencesOfSameStepInAScenario() throws Exception {
        CucumberFeature features = TestPickleBuilder.parseFeature("path/test.feature", ("" + (((((((("Feature: FB\n" + "# Scenario with same step occurring twice\n") + "\n") + "  Scenario: SB\n") + "    When foo\n") + "    Then bar\n") + "\n") + "    When foo\n") + "    Then baz\n")));
        Compiler compiler = new Compiler();
        List<PickleEvent> pickleEvents = new ArrayList<>();
        for (Pickle pickle : compiler.compile(features.getGherkinFeature())) {
            pickleEvents.add(new PickleEvent(features.getUri().toString(), pickle));
        }
        WithStepDescriptions runner = ((WithStepDescriptions) (PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), pickleEvents.get(0), createJUnitOptions())));
        // fish out the two occurrences of the same step and check whether we really got them
        PickleStep stepOccurrence1 = runner.getChildren().get(0);
        PickleStep stepOccurrence2 = runner.getChildren().get(2);
        Assert.assertEquals(stepOccurrence1.getText(), stepOccurrence2.getText());
        // then check that the descriptions are unequal
        Description runnerDescription = runner.getDescription();
        Description stepDescription1 = runnerDescription.getChildren().get(0);
        Description stepDescription2 = runnerDescription.getChildren().get(2);
        Assert.assertFalse("Descriptions must not be equal.", stepDescription1.equals(stepDescription2));
    }

    @Test
    public void shouldAssignUnequalDescriptionsToDifferentStepsInAScenarioOutline() throws Exception {
        CucumberFeature features = TestPickleBuilder.parseFeature("path/test.feature", ("" + (((((("Feature: FB\n" + "  Scenario Outline: SO\n") + "    When <action>\n") + "    Then <result>\n") + "    Examples:\n") + "    | action | result |\n") + "    |   a1   |   r1   |\n")));
        WithStepDescriptions runner = ((WithStepDescriptions) (PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), features.getPickles().get(0), createJUnitOptions())));
        Description runnerDescription = runner.getDescription();
        Description stepDescription1 = runnerDescription.getChildren().get(0);
        Description stepDescription2 = runnerDescription.getChildren().get(1);
        Assert.assertFalse("Descriptions must not be equal.", stepDescription1.equals(stepDescription2));
    }

    @Test
    public void shouldIncludeScenarioNameAsClassNameInStepDescriptions() throws Exception {
        CucumberFeature features = TestPickleBuilder.parseFeature("path/test.feature", ("" + ((((((("Feature: In cucumber.junit\n" + "  Scenario: first\n") + "    When step\n") + "    Then another step\n") + "\n") + "  Scenario: second\n") + "    When step\n") + "    Then another step\n")));
        PickleRunner runner = PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), features.getPickles().get(0), createJUnitOptions());
        // fish out the data from runner
        Description runnerDescription = runner.getDescription();
        Description stepDescription = runnerDescription.getChildren().get(0);
        Assert.assertEquals("description includes scenario name as class name", "first", stepDescription.getClassName());
        Assert.assertEquals("description includes step keyword and name as method name", "step", stepDescription.getMethodName());
        Assert.assertEquals("description includes scenario and step name in display name", "step(first)", stepDescription.getDisplayName());
    }

    @Test
    public void shouldUseScenarioNameForDisplayName() throws Exception {
        List<PickleEvent> pickles = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Then it works\n")));
        PickleRunner runner = PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), pickles.get(0), createJUnitOptions());
        Assert.assertEquals("scenario name", runner.getDescription().getDisplayName());
    }

    @Test
    public void shouldUseStepKeyworkAndNameForChildName() throws Exception {
        List<PickleEvent> pickleEvents = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Then it works\n")));
        PickleRunner runner = PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), pickleEvents.get(0), createJUnitOptions());
        Assert.assertEquals("it works", runner.getDescription().getChildren().get(0).getMethodName());
    }

    @Test
    public void shouldConvertTextFromFeatureFileForNamesWithFilenameCompatibleNameOption() throws Exception {
        List<PickleEvent> pickles = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Then it works\n")));
        PickleRunner runner = PickleRunners.withStepDescriptions(Mockito.mock(ThreadLocalRunnerSupplier.class), pickles.get(0), createJunitOptions("--filename-compatible-names"));
        Assert.assertEquals("scenario_name", runner.getDescription().getDisplayName());
        Assert.assertEquals("scenario_name", runner.getDescription().getChildren().get(0).getClassName());
        Assert.assertEquals("it_works", runner.getDescription().getChildren().get(0).getMethodName());
    }
}

