package cucumber.runner;


import cucumber.api.Scenario;
import gherkin.pickles.PickleStep;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class UndefinedStepDefinitionMatchTest {
    public final UndefinedPickleStepDefinitionMatch match = new UndefinedPickleStepDefinitionMatch(Mockito.mock(PickleStep.class));

    @Test(expected = UndefinedStepDefinitionException.class)
    public void throws_ambiguous_step_definitions_exception_when_run() throws Throwable {
        match.runStep(Mockito.mock(Scenario.class));
        Assert.fail("UndefinedStepDefinitionsException expected");
    }

    @Test(expected = UndefinedStepDefinitionException.class)
    public void throws_ambiguous_step_definitions_exception_when_dry_run() throws Throwable {
        match.dryRunStep(Mockito.mock(Scenario.class));
        Assert.fail("UndefinedStepDefinitionsException expected");
    }
}

