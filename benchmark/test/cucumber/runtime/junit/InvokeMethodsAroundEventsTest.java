package cucumber.runtime.junit;


import cucumber.api.CucumberOptions;
import cucumber.api.event.ConcurrentEventListener;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestRunStarted;
import cucumber.api.junit.Cucumber;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;


public class InvokeMethodsAroundEventsTest {
    private static final List<String> events = new ArrayList<>();

    private static EventHandler<TestRunStarted> testRunStartedEventHandler = new EventHandler<TestRunStarted>() {
        @Override
        public void receive(TestRunStarted event) {
            InvokeMethodsAroundEventsTest.events.add("TestRunStarted");
        }
    };

    private static EventHandler<TestRunFinished> testRunFinishedEventHandler = new EventHandler<TestRunFinished>() {
        @Override
        public void receive(TestRunFinished event) {
            InvokeMethodsAroundEventsTest.events.add("TestRunFinished");
        }
    };

    @Test
    public void finds_features_based_on_implicit_package() throws InitializationError {
        Cucumber cucumber = new Cucumber(InvokeMethodsAroundEventsTest.BeforeAfterClass.class);
        cucumber.run(new RunNotifier());
        Assert.assertThat(InvokeMethodsAroundEventsTest.events, contains("BeforeClass", "TestRunStarted", "TestRunFinished", "AfterClass"));
    }

    @CucumberOptions(plugin = { "cucumber.runtime.junit.InvokeMethodsAroundEventsTest$TestRunStartedFinishedListener" })
    public static class BeforeAfterClass {
        @BeforeClass
        public static void beforeClass() {
            InvokeMethodsAroundEventsTest.events.add("BeforeClass");
        }

        @AfterClass
        public static void afterClass() {
            InvokeMethodsAroundEventsTest.events.add("AfterClass");
        }
    }

    // Used as a plugin by BeforeAfterClass
    @SuppressWarnings("unused")
    public static class TestRunStartedFinishedListener implements ConcurrentEventListener {
        @Override
        public void setEventPublisher(EventPublisher publisher) {
            publisher.registerHandlerFor(TestRunStarted.class, InvokeMethodsAroundEventsTest.testRunStartedEventHandler);
            publisher.registerHandlerFor(TestRunFinished.class, InvokeMethodsAroundEventsTest.testRunFinishedEventHandler);
        }
    }
}

