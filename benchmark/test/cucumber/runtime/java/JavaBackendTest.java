package cucumber.runtime.java;


import cucumber.api.java.ObjectFactory;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.StepDefinition;
import cucumber.runtime.java.stepdefs.Stepdefs;
import java.net.URI;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class JavaBackendTest {
    private ObjectFactory factory;

    private JavaBackend backend;

    @Test
    public void finds_step_definitions_by_classpath_url() {
        JavaBackendTest.GlueStub glue = new JavaBackendTest.GlueStub();
        backend.loadGlue(glue, Arrays.asList(URI.create("classpath:cucumber/runtime/java/stepdefs")));
        backend.buildWorld();
        Assert.assertEquals(Stepdefs.class, factory.getInstance(Stepdefs.class).getClass());
    }

    @Test(expected = CucumberException.class)
    public void detects_subclassed_glue_and_throws_exception() {
        JavaBackendTest.GlueStub glue = new JavaBackendTest.GlueStub();
        backend.loadGlue(glue, Arrays.asList(URI.create("classpath:cucumber/runtime/java/stepdefs"), URI.create("classpath:cucumber/runtime/java/incorrectlysubclassedstepdefs")));
    }

    private class GlueStub implements Glue {
        @Override
        public void addStepDefinition(StepDefinition stepDefinition) {
            // no-op
        }

        @Override
        public void addBeforeStepHook(HookDefinition beforeStepHook) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addBeforeHook(HookDefinition hookDefinition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addAfterStepHook(HookDefinition hookDefinition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addAfterHook(HookDefinition hookDefinition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeScenarioScopedGlue() {
        }
    }
}

