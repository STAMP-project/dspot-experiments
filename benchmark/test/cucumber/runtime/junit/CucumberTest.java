package cucumber.runtime.junit;


import cucumber.annotation.DummyWhen;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.CucumberException;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class CucumberTest {
    private String dir;

    @Test
    public void finds_features_based_on_implicit_package() throws InitializationError {
        Cucumber cucumber = new Cucumber(CucumberTest.ImplicitFeatureAndGluePath.class);
        Assert.assertEquals(2, cucumber.getChildren().size());
        Assert.assertEquals("Feature: Feature A", cucumber.getChildren().get(0).getName());
    }

    @Test
    public void finds_features_based_on_explicit_root_package() throws InitializationError {
        Cucumber cucumber = new Cucumber(CucumberTest.ExplicitFeaturePath.class);
        Assert.assertEquals(2, cucumber.getChildren().size());
        Assert.assertEquals("Feature: Feature A", cucumber.getChildren().get(0).getName());
    }

    @Test
    public void testThatParsingErrorsIsNicelyReported() throws Exception {
        try {
            new Cucumber(CucumberTest.LexerErrorFeature.class);
            Assert.fail("Expecting error");
        } catch (CucumberException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("gherkin.ParserException$CompositeParserException: Parser errors:"));
        }
    }

    @Test
    public void testThatFileIsNotCreatedOnParsingError() throws Exception {
        try {
            new Cucumber(CucumberTest.FormatterWithLexerErrorFeature.class);
            Assert.fail("Expecting error");
        } catch (CucumberException e) {
            Assert.assertFalse("File is created despite Lexor Error", new File("target/lexor_error_feature.json").exists());
        }
    }

    @Test
    public void finds_no_features_when_explicit_feature_path_has_no_features() throws InitializationError {
        Cucumber cucumber = new Cucumber(CucumberTest.ExplicitFeaturePathWithNoFeatures.class);
        List<FeatureRunner> children = cucumber.getChildren();
        Assert.assertEquals(Collections.emptyList(), children);
    }

    @Test
    public void cucumber_can_run_pickles_in_parallel() throws Exception {
        RunNotifier notifier = new RunNotifier();
        RunListener listener = Mockito.mock(RunListener.class);
        notifier.addListener(listener);
        ParallelComputer computer = new ParallelComputer(true, true);
        Request.classes(computer, CucumberTest.RunCukesTestValidEmpty.class).getRunner().run(notifier);
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("A good start(Feature A)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("A good start(Feature A)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("Followed by some examples(Feature A)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("A(Feature B)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("A(Feature B)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("B(Feature B)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("B(Feature B)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
        }
        {
            InOrder order = Mockito.inOrder(listener);
            order.verify(listener).testStarted(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
            order.verify(listener).testFinished(ArgumentMatchers.argThat(new DescriptionMatcher("C(Feature B)")));
        }
    }

    @Test
    public void cucumber_returns_description_tree_with_features_and_pickles() throws InitializationError {
        Description description = new Cucumber(CucumberTest.RunCukesTestValidEmpty.class).getDescription();
        Assert.assertThat(description.getDisplayName(), CoreMatchers.is("cucumber.runtime.junit.CucumberTest$RunCukesTestValidEmpty"));
        Description feature = description.getChildren().get(0);
        Assert.assertThat(feature.getDisplayName(), CoreMatchers.is("Feature: Feature A"));
        Description pickle = feature.getChildren().get(0);
        Assert.assertThat(pickle.getDisplayName(), CoreMatchers.is("A good start(Feature A)"));
    }

    @RunWith(Cucumber.class)
    public class RunCukesTestValidEmpty {}

    @RunWith(Cucumber.class)
    private class RunCukesTestValidIgnored {
        public void ignoreMe() {
        }
    }

    @RunWith(Cucumber.class)
    private class RunCukesTestInvalid {
        @DummyWhen
        public void ignoreMe() {
        }
    }

    @Test
    public void no_stepdefs_in_cucumber_runner_valid() {
        Assertions.assertNoCucumberAnnotatedMethods(CucumberTest.RunCukesTestValidEmpty.class);
        Assertions.assertNoCucumberAnnotatedMethods(CucumberTest.RunCukesTestValidIgnored.class);
    }

    @Test(expected = CucumberException.class)
    public void no_stepdefs_in_cucumber_runner_invalid() {
        Assertions.assertNoCucumberAnnotatedMethods(CucumberTest.RunCukesTestInvalid.class);
    }

    public class ImplicitFeatureAndGluePath {}

    @CucumberOptions(features = { "classpath:cucumber/runtime/junit" })
    public class ExplicitFeaturePath {}

    @CucumberOptions(features = { "classpath:gibber/ish" })
    public class ExplicitFeaturePathWithNoFeatures {}

    @CucumberOptions(features = { "classpath:cucumber/runtime/error/lexer_error.feature" })
    public class LexerErrorFeature {}

    @CucumberOptions(features = { "classpath:cucumber/runtime/error/lexer_error.feature" }, plugin = { "json:target/lexor_error_feature.json" })
    public class FormatterWithLexerErrorFeature {}
}

