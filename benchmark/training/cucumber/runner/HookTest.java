package cucumber.runner;


import cucumber.api.Scenario;
import cucumber.runtime.Backend;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.cucumber.runtime.Glue;
import gherkin.events.PickleEvent;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import java.net.URI;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static TimeService.SYSTEM;


public class HookTest {
    private static final String ENGLISH = "en";

    private final EventBus bus = new TimeServiceEventBus(SYSTEM);

    private final RuntimeOptions runtimeOptions = new RuntimeOptions("");

    private final PickleStep pickleStep = new PickleStep("pattern1", Collections.<Argument>emptyList(), Collections.singletonList(new PickleLocation(2, 2)));

    private final PickleEvent pickleEvent = new PickleEvent("uri", new gherkin.pickles.Pickle("scenario1", HookTest.ENGLISH, Collections.singletonList(pickleStep), Collections.<PickleTag>emptyList(), Collections.singletonList(new PickleLocation(1, 1))));

    /**
     * Test for <a href="https://github.com/cucumber/cucumber-jvm/issues/23">#23</a>.
     */
    @Test
    public void after_hooks_execute_before_objects_are_disposed() throws Throwable {
        Backend backend = Mockito.mock(Backend.class);
        final HookDefinition hook = Mockito.mock(HookDefinition.class);
        Mockito.when(hook.matches(ArgumentMatchers.<PickleTag>anyCollection())).thenReturn(true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                cucumber.runtime.Glue glue = invocation.getArgument(0);
                glue.addBeforeHook(hook);
                return null;
            }
        }).when(backend).loadGlue(ArgumentMatchers.any(Glue.class), ArgumentMatchers.<URI>anyList());
        Runner runner = new Runner(bus, Collections.singleton(backend), runtimeOptions);
        runner.runPickle(pickleEvent);
        InOrder inOrder = Mockito.inOrder(hook, backend);
        inOrder.verify(backend).buildWorld();
        inOrder.verify(hook).execute(ArgumentMatchers.<Scenario>any());
        inOrder.verify(backend).disposeWorld();
    }
}

