package cucumber.runtime.java8;


import LambdaGlueRegistry.INSTANCE;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.StepDefinition;
import cucumber.runtime.java.Function;
import cucumber.runtime.java.LambdaGlueRegistry;
import io.cucumber.stepexpression.TypeRegistry;
import java.util.Locale;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class Java8LambdaStepDefinitionMarksCorrectStackElementTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Java8LambdaStepDefinitionMarksCorrectStackElementTest.MyLambdaGlueRegistry myLambdaGlueRegistry = new Java8LambdaStepDefinitionMarksCorrectStackElementTest.MyLambdaGlueRegistry();

    @Test
    public void exception_from_step_should_be_defined_at_step_definition_class() throws Throwable {
        INSTANCE.set(myLambdaGlueRegistry);
        new SomeLambdaStepDefs();
        final StepDefinition stepDefinition = myLambdaGlueRegistry.getStepDefinition();
        expectedException.expect(new CustomTypeSafeMatcher<Throwable>("exception with matching stack trace") {
            @Override
            protected boolean matchesSafely(Throwable item) {
                for (StackTraceElement stackTraceElement : item.getStackTrace()) {
                    if (stepDefinition.isDefinedAt(stackTraceElement)) {
                        return SomeLambdaStepDefs.class.getName().equals(stackTraceElement.getClassName());
                    }
                }
                return false;
            }
        });
        stepDefinition.execute(new Object[0]);
    }

    private class MyLambdaGlueRegistry implements LambdaGlueRegistry {
        private StepDefinition stepDefinition;

        @Override
        public void addStepDefinition(Function<TypeRegistry, StepDefinition> stepDefinitionFunction) {
            stepDefinition = stepDefinitionFunction.apply(new TypeRegistry(Locale.ENGLISH));
        }

        @Override
        public void addBeforeStepHookDefinition(HookDefinition beforeStepHook) {
        }

        @Override
        public void addBeforeHookDefinition(HookDefinition beforeHook) {
        }

        @Override
        public void addAfterStepHookDefinition(HookDefinition afterStepHook) {
        }

        @Override
        public void addAfterHookDefinition(HookDefinition afterHook) {
        }

        StepDefinition getStepDefinition() {
            return stepDefinition;
        }
    }
}

