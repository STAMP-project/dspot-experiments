package cucumber.runner;


import Result.Type.PASSED;
import Result.Type.SKIPPED;
import cucumber.api.HookType;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.runtime.HookDefinition;
import gherkin.events.PickleEvent;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class HookTestStepTest {
    private final HookDefinition hookDefintion = Mockito.mock(HookDefinition.class);

    private final HookDefinitionMatch definitionMatch = new HookDefinitionMatch(hookDefintion);

    private final TestCase testCase = new TestCase(Collections.<PickleStepTestStep>emptyList(), Collections.<HookTestStep>emptyList(), Collections.<HookTestStep>emptyList(), Mockito.mock(PickleEvent.class), false);

    private final EventBus bus = Mockito.mock(EventBus.class);

    private final Scenario scenario = new Scenario(bus, testCase);

    private HookTestStep step = new HookTestStep(HookType.AfterStep, definitionMatch);

    @Test
    public void run_does_run() throws Throwable {
        step.run(testCase, bus, scenario, false);
        InOrder order = Mockito.inOrder(bus, hookDefintion);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepStarted.class));
        order.verify(hookDefintion).execute(scenario);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepFinished.class));
    }

    @Test
    public void run_does_dry_run() throws Throwable {
        step.run(testCase, bus, scenario, true);
        InOrder order = Mockito.inOrder(bus, hookDefintion);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepStarted.class));
        order.verify(hookDefintion, Mockito.never()).execute(scenario);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepFinished.class));
    }

    @Test
    public void result_is_passed_when_step_definition_does_not_throw_exception() {
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertFalse(skipNextStep);
        Assert.assertEquals(PASSED, scenario.getStatus());
    }

    @Test
    public void result_is_skipped_when_skip_step_is_skip_all_skipable() {
        boolean skipNextStep = step.run(testCase, bus, scenario, true);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(SKIPPED, scenario.getStatus());
    }
}

