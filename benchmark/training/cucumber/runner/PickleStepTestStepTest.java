package cucumber.runner;


import Result.Type.FAILED;
import Result.Type.PENDING;
import cucumber.api.HookType;
import cucumber.api.PendingException;
import cucumber.api.Result;
import cucumber.api.event.TestCaseEvent;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.runtime.HookDefinition;
import gherkin.events.PickleEvent;
import gherkin.pickles.PickleStep;
import java.util.Collections;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static Result.Type.FAILED;


public class PickleStepTestStepTest {
    private PickleEvent pickle = Mockito.mock(PickleEvent.class);

    private final TestCase testCase = new TestCase(Collections.<PickleStepTestStep>emptyList(), Collections.<HookTestStep>emptyList(), Collections.<HookTestStep>emptyList(), pickle, false);

    private final EventBus bus = Mockito.mock(EventBus.class);

    private final Scenario scenario = new Scenario(bus, testCase);

    private final PickleStepDefinitionMatch definitionMatch = Mockito.mock(PickleStepDefinitionMatch.class);

    private HookDefinition afterHookDefinition = Mockito.mock(HookDefinition.class);

    private HookDefinition beforeHookDefinition = Mockito.mock(HookDefinition.class);

    private final HookTestStep beforeHook = new HookTestStep(HookType.BeforeStep, new HookDefinitionMatch(beforeHookDefinition));

    private final HookTestStep afterHook = new HookTestStep(HookType.AfterStep, new HookDefinitionMatch(afterHookDefinition));

    private final PickleStepTestStep step = new PickleStepTestStep("uri", Mockito.mock(PickleStep.class), Collections.singletonList(beforeHook), Collections.singletonList(afterHook), definitionMatch);

    @Test
    public void run_wraps_run_step_in_test_step_started_and_finished_events() throws Throwable {
        step.run(testCase, bus, scenario, false);
        InOrder order = Mockito.inOrder(bus, definitionMatch);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepStarted.class));
        order.verify(definitionMatch).runStep(scenario);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepFinished.class));
    }

    @Test
    public void run_does_dry_run_step_when_skip_steps_is_true() throws Throwable {
        step.run(testCase, bus, scenario, true);
        InOrder order = Mockito.inOrder(bus, definitionMatch);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepStarted.class));
        order.verify(definitionMatch).dryRunStep(scenario);
        order.verify(bus).send(ArgumentMatchers.isA(TestStepFinished.class));
    }

    @Test
    public void result_is_passed_when_step_definition_does_not_throw_exception() {
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertFalse(skipNextStep);
        Assert.assertEquals(Type.PASSED, scenario.getStatus());
    }

    @Test
    public void result_is_skipped_when_skip_step_is_not_run_all() {
        boolean skipNextStep = step.run(testCase, bus, scenario, true);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.SKIPPED, scenario.getStatus());
    }

    @Test
    public void result_is_skipped_when_before_step_hook_does_not_pass() throws Throwable {
        Mockito.doThrow(AssumptionViolatedException.class).when(beforeHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.SKIPPED, scenario.getStatus());
    }

    @Test
    public void step_execution_is_dry_run_when_before_step_hook_does_not_pass() throws Throwable {
        Mockito.doThrow(AssumptionViolatedException.class).when(beforeHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
        step.run(testCase, bus, scenario, false);
        Mockito.verify(definitionMatch).dryRunStep(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void result_is_result_from_hook_when_before_step_hook_does_not_pass() throws Throwable {
        Exception exception = new RuntimeException();
        Mockito.doThrow(exception).when(beforeHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
        Result failure = new Result(FAILED, 0L, exception);
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.FAILED, scenario.getStatus());
        ArgumentCaptor<TestCaseEvent> captor = ArgumentCaptor.forClass(TestCaseEvent.class);
        Mockito.verify(bus, Mockito.times(6)).send(captor.capture());
        List<TestCaseEvent> allValues = captor.getAllValues();
        Assert.assertEquals(failure, ((TestStepFinished) (allValues.get(1))).result);
    }

    @Test
    public void result_is_result_from_step_when_step_hook_does_not_pass() throws Throwable {
        RuntimeException runtimeException = new RuntimeException();
        Result failure = new Result(FAILED, 0L, runtimeException);
        Mockito.doThrow(runtimeException).when(definitionMatch).runStep(ArgumentMatchers.any(Scenario.class));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.FAILED, scenario.getStatus());
        ArgumentCaptor<TestCaseEvent> captor = ArgumentCaptor.forClass(TestCaseEvent.class);
        Mockito.verify(bus, Mockito.times(6)).send(captor.capture());
        List<TestCaseEvent> allValues = captor.getAllValues();
        Assert.assertEquals(failure, ((TestStepFinished) (allValues.get(3))).result);
    }

    @Test
    public void result_is_result_from_hook_when_after_step_hook_does_not_pass() throws Throwable {
        Exception exception = new RuntimeException();
        Result failure = new Result(FAILED, 0L, exception);
        Mockito.doThrow(exception).when(afterHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.FAILED, scenario.getStatus());
        ArgumentCaptor<TestCaseEvent> captor = ArgumentCaptor.forClass(TestCaseEvent.class);
        Mockito.verify(bus, Mockito.times(6)).send(captor.capture());
        List<TestCaseEvent> allValues = captor.getAllValues();
        Assert.assertEquals(failure, ((TestStepFinished) (allValues.get(5))).result);
    }

    @Test
    public void after_step_hook_is_run_when_before_step_hook_does_not_pass() throws Throwable {
        Mockito.doThrow(RuntimeException.class).when(beforeHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
        step.run(testCase, bus, scenario, false);
        Mockito.verify(afterHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
    }

    @Test
    public void after_step_hook_is_run_when_step_does_not_pass() throws Throwable {
        Mockito.doThrow(Exception.class).when(definitionMatch).runStep(ArgumentMatchers.any(Scenario.class));
        step.run(testCase, bus, scenario, false);
        Mockito.verify(afterHookDefinition).execute(ArgumentMatchers.any(cucumber.api.Scenario.class));
    }

    @Test
    public void after_step_hook_scenario_contains_step_failure_when_step_does_not_pass() throws Throwable {
        Throwable expectedError = new AssumptionViolatedException("oops");
        Mockito.doThrow(expectedError).when(definitionMatch).runStep(ArgumentMatchers.any(Scenario.class));
        Mockito.doThrow(new Exception()).when(afterHookDefinition).execute(ArgumentMatchers.argThat(PickleStepTestStepTest.scenarioDoesNotHave(expectedError)));
        step.run(testCase, bus, scenario, false);
        Assert.assertThat(scenario.getError(), Is.is(expectedError));
    }

    @Test
    public void after_step_hook_scenario_contains_before_step_hook_failure_when_before_step_hook_does_not_pass() throws Throwable {
        Throwable expectedError = new AssumptionViolatedException("oops");
        Mockito.doThrow(expectedError).when(beforeHookDefinition).execute(ArgumentMatchers.any(Scenario.class));
        Mockito.doThrow(new Exception()).when(afterHookDefinition).execute(ArgumentMatchers.argThat(PickleStepTestStepTest.scenarioDoesNotHave(expectedError)));
        step.run(testCase, bus, scenario, false);
        Assert.assertThat(scenario.getError(), Is.is(expectedError));
    }

    @Test
    public void result_is_skipped_when_step_definition_throws_assumption_violated_exception() throws Throwable {
        Mockito.doThrow(AssumptionViolatedException.class).when(definitionMatch).runStep(((Scenario) (ArgumentMatchers.any())));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(Type.SKIPPED, scenario.getStatus());
    }

    @Test
    public void result_is_failed_when_step_definition_throws_exception() throws Throwable {
        Mockito.doThrow(RuntimeException.class).when(definitionMatch).runStep(ArgumentMatchers.any(Scenario.class));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(FAILED, scenario.getStatus());
    }

    @Test
    public void result_is_pending_when_step_definition_throws_pending_exception() throws Throwable {
        Mockito.doThrow(PendingException.class).when(definitionMatch).runStep(ArgumentMatchers.any(Scenario.class));
        boolean skipNextStep = step.run(testCase, bus, scenario, false);
        Assert.assertTrue(skipNextStep);
        Assert.assertEquals(PENDING, scenario.getStatus());
    }

    @Test
    public void step_execution_time_is_measured() {
        TestStep step = new PickleStepTestStep("uri", Mockito.mock(PickleStep.class), definitionMatch);
        Mockito.when(bus.getTime()).thenReturn(234L, ((Long) (1234L)));
        step.run(testCase, bus, scenario, false);
        ArgumentCaptor<TestCaseEvent> captor = ArgumentCaptor.forClass(TestCaseEvent.class);
        Mockito.verify(bus, Mockito.times(2)).send(captor.capture());
        List<TestCaseEvent> allValues = captor.getAllValues();
        TestStepStarted started = ((TestStepStarted) (allValues.get(0)));
        TestStepFinished finished = ((TestStepFinished) (allValues.get(1)));
        Assert.assertEquals(((Long) (234L)), started.getTimeStamp());
        Assert.assertEquals(((Long) (1234L)), finished.getTimeStamp());
        Assert.assertEquals(((Long) (1000L)), finished.result.getDuration());
    }
}

