package cucumber.runtime;


import cucumber.runner.EventBus;
import cucumber.runner.TestHelper;
import cucumber.runner.TimeServiceEventBus;
import cucumber.runner.TimeServiceStub;
import cucumber.runtime.model.CucumberFeature;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class UndefinedStepsTrackerTest {
    @Test
    public void has_undefined_steps() {
        UndefinedStepsTracker undefinedStepsTracker = new UndefinedStepsTracker();
        undefinedStepsTracker.handleSnippetsSuggested(uri(), locations(), Arrays.asList(""));
        Assert.assertTrue(undefinedStepsTracker.hasUndefinedSteps());
    }

    @Test
    public void has_no_undefined_steps() {
        UndefinedStepsTracker undefinedStepsTracker = new UndefinedStepsTracker();
        Assert.assertFalse(undefinedStepsTracker.hasUndefinedSteps());
    }

    @Test
    public void removes_duplicates() {
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.handleSnippetsSuggested(uri(), locations(), Arrays.asList("**KEYWORD** ^B$"));
        tracker.handleSnippetsSuggested(uri(), locations(), Arrays.asList("**KEYWORD** ^B$"));
        Assert.assertEquals("[Given ^B$]", tracker.getSnippets().toString());
    }

    @Test
    public void uses_given_when_then_keywords() throws IOException {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given A\n") + "    Then B\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("file:path/test.feature"), locations(line(4)), Arrays.asList("**KEYWORD** ^B$"));
        Assert.assertEquals("[Then ^B$]", tracker.getSnippets().toString());
    }

    @Test
    public void converts_and_to_previous_step_keyword() throws IOException {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    When A\n") + "    And B\n") + "    But C\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("file:path/test.feature"), locations(line(5)), Arrays.asList("**KEYWORD** ^C$"));
        Assert.assertEquals("[When ^C$]", tracker.getSnippets().toString());
    }

    @Test
    public void backtrack_into_background_to_find_step_keyword() throws IOException {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((("Feature: feature name\n" + "  Background:\n") + "    When A\n") + "  Scenario: scenario name\n") + "    And B\n") + "    But C\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("file:path/test.feature"), locations(line(5)), Arrays.asList("**KEYWORD** ^C$"));
        Assert.assertEquals("[When ^C$]", tracker.getSnippets().toString());
    }

    @Test
    public void doesnt_try_to_use_star_keyword() throws IOException {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    When A\n") + "    And B\n") + "    * C\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("file:path/test.feature"), locations(line(5)), Arrays.asList("**KEYWORD** ^C$"));
        Assert.assertEquals("[When ^C$]", tracker.getSnippets().toString());
    }

    @Test
    public void star_keyword_becomes_given_when_no_previous_step() throws IOException {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    * A\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("path/test.feature"), locations(line(3)), Arrays.asList("**KEYWORD** ^A$"));
        Assert.assertEquals("[Given ^A$]", tracker.getSnippets().toString());
    }

    @Test
    public void snippets_are_generated_for_correct_locale() throws Exception {
        EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.setEventPublisher(bus);
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("#language:ru\n" + "\u0424\u0443\u043d\u043a\u0446\u0438\u044f:\n") + "  \u0421\u0446\u0435\u043d\u0430\u0440\u0438\u0439: \n") + "    * \u0411\n")));
        feature.sendTestSourceRead(bus);
        tracker.handleSnippetsSuggested(uri("file:path/test.feature"), locations(line(4)), Arrays.asList("**KEYWORD** ^?$"));
        Assert.assertEquals("[???????? ^?$]", tracker.getSnippets().toString());
    }
}

