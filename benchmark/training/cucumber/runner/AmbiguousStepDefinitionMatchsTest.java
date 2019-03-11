package cucumber.runner;


import cucumber.api.Scenario;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class AmbiguousStepDefinitionMatchsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final PickleStep pickleStep = new PickleStep("", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList());

    private final AmbiguousStepDefinitionsException e = new AmbiguousStepDefinitionsException(pickleStep, Collections.<PickleStepDefinitionMatch>emptyList());

    public final AmbiguousPickleStepDefinitionsMatch match = new AmbiguousPickleStepDefinitionsMatch("uri", Mockito.mock(PickleStep.class), e);

    @Test
    public void throws_ambiguous_step_definitions_exception_when_run() {
        expectedException.expect(AmbiguousStepDefinitionsException.class);
        match.runStep(Mockito.mock(Scenario.class));
    }

    @Test
    public void throws_ambiguous_step_definitions_exception_when_dry_run() {
        expectedException.expect(AmbiguousStepDefinitionsException.class);
        match.dryRunStep(Mockito.mock(Scenario.class));
    }
}

