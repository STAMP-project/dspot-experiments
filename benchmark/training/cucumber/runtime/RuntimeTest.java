package cucumber.runtime;


import HookType.Before;
import Result.Type.AMBIGUOUS;
import Result.Type.FAILED;
import Result.Type.PASSED;
import Result.Type.PENDING;
import Result.Type.SKIPPED;
import Result.Type.UNDEFINED;
import cucumber.api.Plugin;
import cucumber.api.Result;
import cucumber.api.Scenario;
import cucumber.api.StepDefinitionReporter;
import cucumber.runner.EventBus;
import cucumber.runner.TestBackendSupplier;
import cucumber.runner.TestHelper;
import cucumber.runner.TimeService;
import cucumber.runtime.formatter.FormatterBuilder;
import cucumber.runtime.formatter.FormatterSpy;
import cucumber.runtime.io.ClasspathResourceLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import gherkin.pickles.PickleTag;
import io.cucumber.stepexpression.TypeRegistry;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RuntimeTest {
    private static final long ANY_TIMESTAMP = 1234567890;

    private final TypeRegistry TYPE_REGISTRY = new TypeRegistry(Locale.ENGLISH);

    private final EventBus bus = new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void runs_feature_with_json_formatter() {
        final CucumberFeature feature = TestHelper.feature("test.feature", ("" + (((("Feature: feature name\n" + "  Background: background name\n") + "    Given b\n") + "  Scenario: scenario name\n") + "    When s\n")));
        StringBuilder out = new StringBuilder();
        Plugin jsonFormatter = FormatterBuilder.jsonFormatter(out);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BackendSupplier backendSupplier = new BackendSupplier() {
            @Override
            public Collection<? extends Backend> get() {
                return Collections.singletonList(Mockito.mock(Backend.class));
            }
        };
        FeatureSupplier featureSupplier = new FeatureSupplier() {
            @Override
            public List<CucumberFeature> get() {
                return Collections.singletonList(feature);
            }
        };
        run();
        String expected = "[\n" + (((((((((((((((((((((((((((((((((((((((((((((((("  {\n" + "    \"line\": 1,\n") + "    \"elements\": [\n") + "      {\n") + "        \"line\": 2,\n") + "        \"name\": \"background name\",\n") + "        \"description\": \"\",\n") + "        \"type\": \"background\",\n") + "        \"keyword\": \"Background\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"result\": {\n") + "              \"status\": \"undefined\"\n") + "            },\n") + "            \"line\": 3,\n") + "            \"name\": \"b\",\n") + "            \"match\": {},\n") + "            \"keyword\": \"Given \"\n") + "          }\n") + "        ]\n") + "      },\n") + "      {\n") + "        \"line\": 4,\n") + "        \"name\": \"scenario name\",\n") + "        \"description\": \"\",\n") + "        \"id\": \"feature-name;scenario-name\",\n") + "        \"type\": \"scenario\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"result\": {\n") + "              \"status\": \"undefined\"\n") + "            },\n") + "            \"line\": 5,\n") + "            \"name\": \"s\",\n") + "            \"match\": {},\n") + "            \"keyword\": \"When \"\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"name\": \"feature name\",\n") + "    \"description\": \"\",\n") + "    \"id\": \"feature-name\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"uri\": \"file:test.feature\",\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertEquals(expected, out.toString());
    }

    @Test
    public void strict_with_passed_scenarios() {
        Runtime runtime = createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void non_strict_with_passed_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void non_strict_with_undefined_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void strict_with_undefined_scenarios() {
        Runtime runtime = createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void strict_with_pending_scenarios() {
        Runtime runtime = createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void non_strict_with_pending_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void non_strict_with_skipped_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void strict_with_skipped_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void non_strict_with_failed_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void strict_with_failed_scenarios() {
        Runtime runtime = createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void non_strict_with_ambiguous_scenarios() {
        Runtime runtime = createNonStrictRuntime();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void strict_with_ambiguous_scenarios() {
        Runtime runtime = createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(1, exitStatus());
    }

    @Test
    public void should_pass_if_no_features_are_found() {
        ResourceLoader resourceLoader = createResourceLoaderThatFindsNoFeatures();
        Runtime runtime = createStrictRuntime(resourceLoader);
        run();
        Assert.assertEquals(0, exitStatus());
    }

    @Test
    public void reports_step_definitions_to_plugin() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        final StubStepDefinition stepDefinition = new StubStepDefinition("some pattern", new TypeRegistry(Locale.ENGLISH));
        TestBackendSupplier testBackendSupplier = new TestBackendSupplier() {
            @Override
            public void loadGlue(Glue glue, List<URI> gluePaths) {
                glue.addStepDefinition(stepDefinition);
            }
        };
        run();
        Assert.assertSame(stepDefinition, RuntimeTest.StepdefsPrinter.instance.stepDefinition);
    }

    public static class StepdefsPrinter implements StepDefinitionReporter {
        static RuntimeTest.StepdefsPrinter instance;

        StepDefinition stepDefinition;

        public StepdefsPrinter() {
            RuntimeTest.StepdefsPrinter.instance = this;
        }

        @Override
        public void stepDefinition(StepDefinition stepDefinition) {
            this.stepDefinition = stepDefinition;
        }
    }

    @Test
    public void should_make_scenario_name_available_to_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("Feature: feature name\n" + ((("  Scenario: scenario name\n" + "    Given first step\n") + "    When second step\n") + "    Then third step\n")));
        HookDefinition beforeHook = Mockito.mock(HookDefinition.class);
        Mockito.when(beforeHook.matches(ArgumentMatchers.<PickleTag>anyCollection())).thenReturn(true);
        Runtime runtime = createRuntimeWithMockedGlue(beforeHook, Before, feature);
        run();
        ArgumentCaptor<Scenario> capturedScenario = ArgumentCaptor.forClass(Scenario.class);
        Mockito.verify(beforeHook).execute(capturedScenario.capture());
        Assert.assertEquals("scenario name", capturedScenario.getValue().getName());
    }

    @Test
    public void should_call_formatter_for_two_scenarios_with_background() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario: scenario_1 name\n") + "    When second step\n") + "    Then third step\n") + "  Scenario: scenario_2 name\n") + "    Then second step\n")));
        Map<String, Result> stepsToResult = new HashMap<>();
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        String formatterOutput = runFeatureWithFormatterSpy(feature, stepsToResult);
        Assert.assertEquals(("" + (((((((((((((("TestCase started\n" + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestRun finished\n")), formatterOutput);
    }

    @Test
    public void should_call_formatter_for_scenario_outline_with_two_examples_table_and_background() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((((((((("Feature: feature name\n" + "  Background: background\n") + "    Given first step\n") + "  Scenario Outline: scenario outline name\n") + "    When <x> step\n") + "    Then <y> step\n") + "    Examples: examples 1 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n") + "      | second | third |\n") + "    Examples: examples 2 name\n") + "      |   x    |   y   |\n") + "      | second | third |\n")));
        Map<String, Result> stepsToResult = new HashMap<>();
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        String formatterOutput = runFeatureWithFormatterSpy(feature, stepsToResult);
        Assert.assertEquals(("" + (((((((((((((((((((((((("TestCase started\n" + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestRun finished\n")), formatterOutput);
    }

    @Test
    public void should_call_formatter_with_correct_sequence_of_events_when_running_in_parallel() {
        CucumberFeature feature1 = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name 1\n" + "  Scenario: scenario_1 name\n") + "    Given first step\n") + "  Scenario: scenario_2 name\n") + "    Given first step\n")));
        CucumberFeature feature2 = TestHelper.feature("path/test2.feature", ("" + (("Feature: feature name 2\n" + "  Scenario: scenario_2 name\n") + "    Given first step\n")));
        CucumberFeature feature3 = TestHelper.feature("path/test3.feature", ("" + (("Feature: feature name 3\n" + "  Scenario: scenario_3 name\n") + "    Given first step\n")));
        Map<String, Result> stepsToResult = new HashMap<>();
        stepsToResult.put("first step", TestHelper.result("passed"));
        FormatterSpy formatterSpy = new FormatterSpy();
        final List<CucumberFeature> features = Arrays.asList(feature1, feature2, feature3);
        run();
        String formatterOutput = formatterSpy.toString();
        Assert.assertEquals(("" + (((((((((((((((("TestCase started\n" + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestCase started\n") + "  TestStep started\n") + "  TestStep finished\n") + "TestCase finished\n") + "TestRun finished\n")), formatterOutput);
    }
}

