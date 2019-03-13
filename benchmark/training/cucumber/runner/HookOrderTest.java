package cucumber.runner;


import cucumber.api.Scenario;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.StubStepDefinition;
import cucumber.runtime.cucumber.runtime.Glue;
import gherkin.events.PickleEvent;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import io.cucumber.stepexpression.TypeRegistry;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static TimeService.SYSTEM;


public class HookOrderTest {
    private static final String ENGLISH = "en";

    private final RuntimeOptions runtimeOptions = new RuntimeOptions("");

    private final EventBus bus = new TimeServiceEventBus(SYSTEM);

    private final StubStepDefinition stepDefinition = new StubStepDefinition("pattern1", new TypeRegistry(Locale.ENGLISH));

    private final PickleStep pickleStep = new PickleStep("pattern1", Collections.<Argument>emptyList(), Collections.singletonList(new PickleLocation(2, 2)));

    private final PickleEvent pickleEvent = new PickleEvent("uri", new gherkin.pickles.Pickle("scenario1", HookOrderTest.ENGLISH, Collections.singletonList(pickleStep), Collections.<PickleTag>emptyList(), Collections.singletonList(new PickleLocation(1, 1))));

    @Test
    public void before_hooks_execute_in_order() throws Throwable {
        final List<HookDefinition> hooks = mockHooks(3, Integer.MAX_VALUE, 1, (-1), 0, 10000, Integer.MIN_VALUE);
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(cucumber.runtime.Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(new StubStepDefinition("pattern1", new TypeRegistry(Locale.ENGLISH)));
                for (HookDefinition hook : hooks) {
                    glue.addBeforeHook(hook);
                }
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(hooks.toArray());
        inOrder.verify(hooks.get(6)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(3)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(4)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(5)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
    }

    @Test
    public void before_step_hooks_execute_in_order() throws Throwable {
        final List<HookDefinition> hooks = mockHooks(3, Integer.MAX_VALUE, 1, (-1), 0, 10000, Integer.MIN_VALUE);
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(cucumber.runtime.Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
                for (HookDefinition hook : hooks) {
                    glue.addBeforeStepHook(hook);
                }
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(hooks.toArray());
        inOrder.verify(hooks.get(6)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(3)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(4)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(5)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
    }

    @Test
    public void after_hooks_execute_in_reverse_order() throws Throwable {
        final List<HookDefinition> hooks = mockHooks(Integer.MIN_VALUE, 2, Integer.MAX_VALUE, 4, (-1), 0, 10000);
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(cucumber.runtime.Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
                for (HookDefinition hook : hooks) {
                    glue.addAfterHook(hook);
                }
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(hooks.toArray());
        inOrder.verify(hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(6)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(3)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(5)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(4)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
    }

    @Test
    public void after_step_hooks_execute_in_reverse_order() throws Throwable {
        final List<HookDefinition> hooks = mockHooks(Integer.MIN_VALUE, 2, Integer.MAX_VALUE, 4, (-1), 0, 10000);
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(cucumber.runtime.Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
                for (HookDefinition hook : hooks) {
                    glue.addAfterStepHook(hook);
                }
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(hooks.toArray());
        inOrder.verify(hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(6)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(3)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(5)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(4)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
    }

    @Test
    public void hooks_order_across_many_backends() throws Throwable {
        final List<HookDefinition> backend1Hooks = mockHooks(3, Integer.MAX_VALUE, 1);
        final List<HookDefinition> backend2Hooks = mockHooks(2, Integer.MAX_VALUE, 4);
        TestRunnerSupplier runnerSupplier = new TestRunnerSupplier(bus, runtimeOptions) {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
                for (HookDefinition hook : backend1Hooks) {
                    glue.addBeforeHook(hook);
                }
                for (HookDefinition hook : backend2Hooks) {
                    glue.addBeforeHook(hook);
                }
            }
        };
        runnerSupplier.get().runPickle(pickleEvent);
        List<HookDefinition> allHooks = new ArrayList<>();
        allHooks.addAll(backend1Hooks);
        allHooks.addAll(backend2Hooks);
        InOrder inOrder = Mockito.inOrder(allHooks.toArray());
        inOrder.verify(backend1Hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(backend2Hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(backend1Hooks.get(0)).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(backend2Hooks.get(2)).execute(ArgumentMatchers.<Scenario>any());
        Mockito.verify(backend2Hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
        Mockito.verify(backend1Hooks.get(1)).execute(ArgumentMatchers.<Scenario>any());
    }
}

