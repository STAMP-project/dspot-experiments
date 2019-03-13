package cucumber.runtime.java;


import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleTag;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;


public class JavaHookTest {
    private final PickleTag pickleTagBar = new PickleTag(Mockito.mock(PickleLocation.class), "@bar");

    private final PickleTag pickleTagZap = new PickleTag(Mockito.mock(PickleLocation.class), "@zap");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Method BEFORE;

    private static final Method AFTER;

    private static final Method BEFORESTEP;

    private static final Method AFTERSTEP;

    private static final Method BAD_AFTER;

    static {
        try {
            BEFORE = JavaHookTest.HasHooks.class.getMethod("before");
            AFTER = JavaHookTest.HasHooks.class.getMethod("after");
            BEFORESTEP = JavaHookTest.HasHooks.class.getMethod("beforeStep");
            AFTERSTEP = JavaHookTest.HasHooks.class.getMethod("afterStep");
            BAD_AFTER = JavaHookTest.BadHook.class.getMethod("after", String.class);
        } catch (NoSuchMethodException note) {
            throw new InternalError("dang");
        }
    }

    @Mock
    private Glue glue;

    private JavaBackend backend;

    private SingletonFactory objectFactory;

    @Test
    public void before_hooks_get_registered() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BEFORE.getAnnotation(Before.class), JavaHookTest.BEFORE);
        Mockito.verify(glue).addBeforeHook(ArgumentMatchers.argThat(JavaHookTest.isHookFor(JavaHookTest.BEFORE)));
    }

    @Test
    public void before_step_hooks_get_registered() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BEFORESTEP.getAnnotation(BeforeStep.class), JavaHookTest.BEFORESTEP);
        Mockito.verify(glue).addBeforeStepHook(ArgumentMatchers.argThat(JavaHookTest.isHookFor(JavaHookTest.BEFORESTEP)));
    }

    @Test
    public void after_step_hooks_get_registered() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.AFTERSTEP.getAnnotation(AfterStep.class), JavaHookTest.AFTERSTEP);
        Mockito.verify(glue).addAfterStepHook(ArgumentMatchers.argThat(JavaHookTest.isHookFor(JavaHookTest.AFTERSTEP)));
    }

    @Test
    public void after_hooks_get_registered() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.AFTER.getAnnotation(After.class), JavaHookTest.AFTER);
        Mockito.verify(glue).addAfterHook(ArgumentMatchers.argThat(JavaHookTest.isHookFor(JavaHookTest.AFTER)));
    }

    @Test
    public void hook_order_gets_registered() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.AFTER.getAnnotation(After.class), JavaHookTest.AFTER);
        Mockito.verify(glue).addAfterHook(ArgumentMatchers.argThat(JavaHookTest.isHookWithOrder(1)));
    }

    @Test
    public void hook_with_no_order_is_last() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BEFORE.getAnnotation(Before.class), JavaHookTest.BEFORE);
        Mockito.verify(glue).addBeforeHook(ArgumentMatchers.argThat(JavaHookTest.isHookWithOrder(10000)));
    }

    @Test
    public void matches_matching_tags() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BEFORE.getAnnotation(Before.class), JavaHookTest.BEFORE);
        Mockito.verify(glue).addBeforeHook(ArgumentMatchers.argThat(JavaHookTest.isHookThatMatches(pickleTagBar, pickleTagZap)));
    }

    @Test
    public void does_not_match_non_matching_tags() {
        objectFactory.setInstance(new JavaHookTest.HasHooks());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BEFORE.getAnnotation(Before.class), JavaHookTest.BEFORE);
        Mockito.verify(glue).addBeforeHook(AdditionalMatchers.not(ArgumentMatchers.argThat(JavaHookTest.isHookThatMatches(pickleTagBar))));
    }

    @Test
    public void fails_if_hook_argument_is_not_scenario_result() throws Throwable {
        objectFactory.setInstance(new JavaHookTest.BadHook());
        backend.buildWorld();
        backend.addHook(JavaHookTest.BAD_AFTER.getAnnotation(After.class), JavaHookTest.BAD_AFTER);
        ArgumentCaptor<JavaHookDefinition> javaHookDefinitionArgumentCaptor = ArgumentCaptor.forClass(JavaHookDefinition.class);
        Mockito.verify(glue).addAfterHook(javaHookDefinitionArgumentCaptor.capture());
        HookDefinition bad = javaHookDefinitionArgumentCaptor.getValue();
        expectedException.expectMessage("When a hook declares an argument it must be of type cucumber.api.Scenario. public void cucumber.runtime.java.JavaHookTest$BadHook.after(java.lang.String)");
        bad.execute(Mockito.mock(Scenario.class));
    }

    public static class HasHooks {
        @Before({ "(@foo or @bar) and @zap" })
        public void before() {
        }

        @BeforeStep
        public void beforeStep() {
        }

        @AfterStep
        public void afterStep() {
        }

        @After(order = 1)
        public void after() {
        }
    }

    public static class BadHook {
        @After
        public void after(String badType) {
        }
    }
}

