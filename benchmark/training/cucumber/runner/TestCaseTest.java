package cucumber.runner;


import cucumber.api.HookType;
import cucumber.api.Scenario;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestCaseStarted;
import cucumber.runtime.HookDefinition;
import gherkin.pickles.PickleStep;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestCaseTest {
    private final EventBus bus = Mockito.mock(EventBus.class);

    private final PickleStepDefinitionMatch definitionMatch1 = Mockito.mock(PickleStepDefinitionMatch.class);

    private HookDefinition beforeStep1HookDefinition1 = Mockito.mock(HookDefinition.class);

    private HookDefinition afterStep1HookDefinition1 = Mockito.mock(HookDefinition.class);

    private final PickleStepTestStep testStep1 = new PickleStepTestStep("uri", Mockito.mock(PickleStep.class), Collections.singletonList(new HookTestStep(HookType.BeforeStep, new HookDefinitionMatch(beforeStep1HookDefinition1))), Collections.singletonList(new HookTestStep(HookType.AfterStep, new HookDefinitionMatch(afterStep1HookDefinition1))), definitionMatch1);

    private final PickleStepDefinitionMatch definitionMatch2 = Mockito.mock(PickleStepDefinitionMatch.class);

    private HookDefinition beforeStep1HookDefinition2 = Mockito.mock(HookDefinition.class);

    private HookDefinition afterStep1HookDefinition2 = Mockito.mock(HookDefinition.class);

    private final PickleStepTestStep testStep2 = new PickleStepTestStep("uri", Mockito.mock(PickleStep.class), Collections.singletonList(new HookTestStep(HookType.BeforeStep, new HookDefinitionMatch(beforeStep1HookDefinition2))), Collections.singletonList(new HookTestStep(HookType.AfterStep, new HookDefinitionMatch(afterStep1HookDefinition2))), definitionMatch2);

    @Test
    public void run_wraps_execute_in_test_case_started_and_finished_events() throws Throwable {
        Mockito.doThrow(new UndefinedStepDefinitionException()).when(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        createTestCase(testStep1).run(bus);
        InOrder order = Mockito.inOrder(bus, definitionMatch1);
        order.verify(bus).send(ArgumentMatchers.isA(TestCaseStarted.class));
        order.verify(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        order.verify(bus).send(ArgumentMatchers.isA(TestCaseFinished.class));
    }

    @Test
    public void run_all_steps() throws Throwable {
        TestCase testCase = createTestCase(testStep1, testStep2);
        testCase.run(bus);
        InOrder order = Mockito.inOrder(definitionMatch1, definitionMatch2);
        order.verify(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        order.verify(definitionMatch2).runStep(ArgumentMatchers.isA(Scenario.class));
    }

    @Test
    public void run_hooks_after_the_first_non_passed_result_for_gherkin_step() throws Throwable {
        Mockito.doThrow(new UndefinedStepDefinitionException()).when(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        TestCase testCase = createTestCase(testStep1, testStep2);
        testCase.run(bus);
        InOrder order = Mockito.inOrder(beforeStep1HookDefinition1, definitionMatch1, afterStep1HookDefinition1);
        order.verify(beforeStep1HookDefinition1).execute(ArgumentMatchers.isA(Scenario.class));
        order.verify(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        order.verify(afterStep1HookDefinition1).execute(ArgumentMatchers.isA(Scenario.class));
    }

    @Test
    public void skip_hooks_of_step_after_skipped_step() throws Throwable {
        Mockito.doThrow(new UndefinedStepDefinitionException()).when(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        TestCase testCase = createTestCase(testStep1, testStep2);
        testCase.run(bus);
        InOrder order = Mockito.inOrder(beforeStep1HookDefinition2, definitionMatch2, afterStep1HookDefinition2);
        order.verify(beforeStep1HookDefinition2, Mockito.never()).execute(ArgumentMatchers.isA(Scenario.class));
        order.verify(definitionMatch2).dryRunStep(ArgumentMatchers.isA(Scenario.class));
        order.verify(afterStep1HookDefinition2, Mockito.never()).execute(ArgumentMatchers.isA(Scenario.class));
    }

    @Test
    public void skip_steps_at_first_gherkin_step_after_non_passed_result() throws Throwable {
        Mockito.doThrow(new UndefinedStepDefinitionException()).when(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        TestCase testCase = createTestCase(testStep1, testStep2);
        testCase.run(bus);
        InOrder order = Mockito.inOrder(definitionMatch1, definitionMatch2);
        order.verify(definitionMatch1).runStep(ArgumentMatchers.isA(Scenario.class));
        order.verify(definitionMatch2).dryRunStep(ArgumentMatchers.isA(Scenario.class));
    }
}

