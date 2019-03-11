package cucumber.runtime.junit;


import cucumber.runner.ThreadLocalRunnerSupplier;
import cucumber.runtime.junit.PickleRunners.PickleRunner;
import gherkin.events.PickleEvent;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PickleRunnerWithNoStepDescriptionsTest {
    @Test
    public void shouldUseScenarioNameWithFeatureNameAsClassNameForDisplayName() throws Exception {
        List<PickleEvent> pickles = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Then it works\n")));
        PickleRunner runner = PickleRunners.withNoStepDescriptions("feature name", Mockito.mock(ThreadLocalRunnerSupplier.class), pickles.get(0), createJUnitOptions());
        Assert.assertEquals("scenario name(feature name)", runner.getDescription().getDisplayName());
    }

    @Test
    public void shouldConvertTextFromFeatureFileForNamesWithFilenameCompatibleNameOption() throws Exception {
        List<PickleEvent> pickles = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Then it works\n")));
        PickleRunner runner = PickleRunners.withNoStepDescriptions("feature name", Mockito.mock(ThreadLocalRunnerSupplier.class), pickles.get(0), createJUnitOptions("--filename-compatible-names"));
        Assert.assertEquals("scenario_name(feature_name)", runner.getDescription().getDisplayName());
    }

    @Test
    public void shouldConvertTextFromFeatureFileWithRussianLanguage() throws Exception {
        List<PickleEvent> pickles = TestPickleBuilder.pickleEventsFromFeature("featurePath", ("" + ((("#language:ru\n" + "\u0424\u0443\u043d\u043a\u0446\u0438\u044f: \u0438\u043c\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438\n") + "  \u0421\u0446\u0435\u043d\u0430\u0440\u0438\u0439: \u0438\u043c\u044f \u0441\u0446\u0435\u043d\u0430\u0440\u0438\u044f\n") + "    \u0422\u043e\u0433\u0434\u0430 \u043e\u043d \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442\n")));
        PickleRunner runner = PickleRunners.withNoStepDescriptions("??? ???????", Mockito.mock(ThreadLocalRunnerSupplier.class), pickles.get(0), createJUnitOptions("--filename-compatible-names"));
        Assert.assertEquals("____________(___________)", runner.getDescription().getDisplayName());
    }
}

