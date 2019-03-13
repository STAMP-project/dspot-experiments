package cucumber.runner;


import cucumber.api.Scenario;
import cucumber.runtime.Backend;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.StepDefinition;
import cucumber.runtime.snippets.FunctionNameGenerator;
import gherkin.events.PickleEvent;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static TimeService.SYSTEM;


public class RunnerTest {
    private static final String ENGLISH = "en";

    private static final String NAME = "name";

    private static final List<PickleStep> NO_STEPS = Collections.emptyList();

    private static final List<PickleTag> NO_TAGS = Collections.emptyList();

    private static final List<PickleLocation> MOCK_LOCATIONS = Arrays.asList(Mockito.mock(PickleLocation.class));

    private final RuntimeOptions runtimeOptions = new RuntimeOptions("");

    private final EventBus bus = new TimeServiceEventBus(SYSTEM);

    @Test
    public void hooks_execute_when_world_exist() throws Throwable {
        final HookDefinition beforeHook = addBeforeHook();
        final HookDefinition afterHook = addAfterHook();
        Backend backend = Mockito.mock(Backend.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Glue glue = invocation.getArgument(0);
                glue.addAfterHook(afterHook);
                glue.addBeforeHook(beforeHook);
                return null;
            }
        }).when(backend).loadGlue(ArgumentMatchers.any(Glue.class), ArgumentMatchers.<URI>anyList());
        PickleStep step = Mockito.mock(PickleStep.class);
        new Runner(bus, Collections.singletonList(backend), runtimeOptions).runPickle(createPickleEventWithSteps(Arrays.asList(step)));
        InOrder inOrder = Mockito.inOrder(beforeHook, afterHook, backend);
        inOrder.verify(backend).buildWorld();
        inOrder.verify(beforeHook).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(afterHook).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(backend).disposeWorld();
    }

    @Test
    public void steps_are_skipped_after_failure() throws Throwable {
        final StepDefinition stepDefinition = Mockito.mock(StepDefinition.class);
        PickleEvent pickleEventMatchingStepDefinitions = createPickleEventMatchingStepDefinitions(Arrays.asList(stepDefinition));
        final HookDefinition failingBeforeHook = addBeforeHook();
        Mockito.doThrow(RuntimeException.class).when(failingBeforeHook).execute(ArgumentMatchers.<Scenario>any());
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addBeforeHook(failingBeforeHook);
                glue.addStepDefinition(stepDefinition);
            }
        };
        runnerSupplier.get().runPickle(pickleEventMatchingStepDefinitions);
        InOrder inOrder = Mockito.inOrder(failingBeforeHook, stepDefinition);
        inOrder.verify(failingBeforeHook).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(stepDefinition, Mockito.never()).execute(ArgumentMatchers.any(Object[].class));
    }

    @Test
    public void aftersteps_are_executed_after_failed_step() throws Throwable {
        final StepDefinition stepDefinition = Mockito.mock(StepDefinition.class);
        Mockito.doThrow(RuntimeException.class).when(stepDefinition).execute(ArgumentMatchers.<Object[]>any());
        PickleEvent pickleEventMatchingStepDefinitions = createPickleEventMatchingStepDefinitions(Arrays.asList(stepDefinition));
        final HookDefinition afteStepHook = addAfterStepHook();
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addAfterHook(afteStepHook);
                glue.addStepDefinition(stepDefinition);
            }
        };
        runnerSupplier.get().runPickle(pickleEventMatchingStepDefinitions);
        InOrder inOrder = Mockito.inOrder(afteStepHook, stepDefinition);
        inOrder.verify(stepDefinition).execute(ArgumentMatchers.any(Object[].class));
        inOrder.verify(afteStepHook).execute(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void aftersteps_executed_for_passed_step() throws Throwable {
        final StepDefinition stepDefinition = Mockito.mock(StepDefinition.class);
        PickleEvent pickleEvent = createPickleEventMatchingStepDefinitions(Arrays.asList(stepDefinition));
        final HookDefinition afteStepHook1 = addAfterStepHook();
        final HookDefinition afteStepHook2 = addAfterStepHook();
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addAfterHook(afteStepHook1);
                glue.addAfterHook(afteStepHook2);
                glue.addStepDefinition(stepDefinition);
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(afteStepHook1, afteStepHook2, stepDefinition);
        inOrder.verify(stepDefinition).execute(ArgumentMatchers.any(Object[].class));
        inOrder.verify(afteStepHook1).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(afteStepHook2).execute(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void hooks_execute_also_after_failure() throws Throwable {
        final HookDefinition failingBeforeHook = addBeforeHook();
        Mockito.doThrow(RuntimeException.class).when(failingBeforeHook).execute(ArgumentMatchers.any(Scenario.class));
        final HookDefinition beforeHook = addBeforeHook();
        final HookDefinition afterHook = addAfterHook();
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addBeforeHook(failingBeforeHook);
                glue.addBeforeHook(beforeHook);
                glue.addAfterHook(afterHook);
            }
        };
        PickleStep step = Mockito.mock(PickleStep.class);
        runnerSupplier.get().runPickle(createPickleEventWithSteps(Arrays.asList(step)));
        InOrder inOrder = Mockito.inOrder(failingBeforeHook, beforeHook, afterHook);
        inOrder.verify(failingBeforeHook).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(beforeHook).execute(ArgumentMatchers.any(Scenario.class));
        inOrder.verify(afterHook).execute(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void steps_are_executed() throws Throwable {
        final StepDefinition stepDefinition = Mockito.mock(StepDefinition.class);
        PickleEvent pickleEventMatchingStepDefinitions = createPickleEventMatchingStepDefinitions(Arrays.asList(stepDefinition));
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
            }
        };
        runnerSupplier.get().runPickle(pickleEventMatchingStepDefinitions);
        Mockito.verify(stepDefinition).execute(ArgumentMatchers.any(Object[].class));
    }

    @Test
    public void steps_are_not_executed_on_dry_run() throws Throwable {
        final StepDefinition stepDefinition = Mockito.mock(StepDefinition.class);
        final PickleEvent pickleEvent = createPickleEventMatchingStepDefinitions(Arrays.asList(stepDefinition));
        RuntimeOptions runtimeOptions = new RuntimeOptions("--dry-run");
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        Mockito.verify(stepDefinition, Mockito.never()).execute(ArgumentMatchers.any(Object[].class));
    }

    @Test
    public void hooks_not_executed_in_dry_run_mode() throws Throwable {
        RuntimeOptions runtimeOptions = new RuntimeOptions("--dry-run");
        final HookDefinition beforeHook = addBeforeHook();
        final HookDefinition afterHook = addAfterHook();
        final HookDefinition afterStepHook = addAfterStepHook();
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addBeforeHook(beforeHook);
                glue.addBeforeHook(afterHook);
                glue.addAfterStepHook(afterStepHook);
            }
        };
        PickleStep step = Mockito.mock(PickleStep.class);
        runnerSupplier.get().runPickle(createPickleEventWithSteps(Arrays.asList(step)));
        Mockito.verify(beforeHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
        Mockito.verify(afterStepHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
        Mockito.verify(afterHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void hooks_not_executed_for_empty_pickles() throws Throwable {
        final HookDefinition beforeHook = addBeforeHook();
        final HookDefinition afterHook = addAfterHook();
        final HookDefinition afterStepHook = addAfterStepHook();
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addBeforeHook(beforeHook);
                glue.addBeforeHook(afterHook);
                glue.addAfterStepHook(afterStepHook);
            }
        };
        runnerSupplier.get().runPickle(createEmptyPickleEvent());
        Mockito.verify(beforeHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
        Mockito.verify(afterStepHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
        Mockito.verify(afterHook, Mockito.never()).execute(ArgumentMatchers.any(Scenario.class));
    }

    @Test
    public void backends_are_asked_for_snippets_for_undefined_steps() {
        PickleStep step = Mockito.mock(PickleStep.class);
        Backend backend = Mockito.mock(Backend.class);
        Runner runner = new Runner(bus, Collections.singletonList(backend), runtimeOptions);
        runner.runPickle(createPickleEventWithSteps(Arrays.asList(step)));
        Mockito.verify(backend).getSnippet(ArgumentMatchers.eq(step), ArgumentMatchers.anyString(), ArgumentMatchers.any(FunctionNameGenerator.class));
    }
}

