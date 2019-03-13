package cucumber.runner;


import cucumber.api.Scenario;
import org.junit.Test;
import org.mockito.Mockito;


public class FailedStepInstantiationMatchTest {
    private FailedPickleStepInstantiationMatch match;

    @Test(expected = Exception.class)
    public void throws_the_exception_passed_to_the_match_when_run() throws Throwable {
        match.runStep(Mockito.mock(Scenario.class));
    }

    @Test(expected = Exception.class)
    public void throws_the_exception_passed_to_the_match_when_dry_run() throws Throwable {
        match.dryRunStep(Mockito.mock(Scenario.class));
    }
}

