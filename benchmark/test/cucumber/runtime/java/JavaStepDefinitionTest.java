package cucumber.runtime.java;


import cucumber.api.Result;
import cucumber.api.java.en.Given;
import cucumber.runner.AmbiguousStepDefinitionsException;
import cucumber.runner.Runner;
import cucumber.runtime.DuplicateStepDefinitionException;
import gherkin.events.PickleEvent;
import gherkin.pickles.Argument;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JavaStepDefinitionTest {
    private static final Method THREE_DISABLED_MICE;

    private static final Method THREE_BLIND_ANIMALS;

    private static final String ENGLISH = "en";

    static {
        try {
            THREE_DISABLED_MICE = JavaStepDefinitionTest.Defs.class.getMethod("threeDisabledMice", String.class);
            THREE_BLIND_ANIMALS = JavaStepDefinitionTest.Defs.class.getMethod("threeBlindAnimals", String.class);
        } catch (NoSuchMethodException e) {
            throw new InternalError("dang");
        }
    }

    private final JavaStepDefinitionTest.Defs defs = new JavaStepDefinitionTest.Defs();

    private JavaBackend backend;

    private Result latestReceivedResult;

    private Runner runner;

    @Test(expected = DuplicateStepDefinitionException.class)
    public void throws_duplicate_when_two_stepdefs_with_same_regexp_found() throws Throwable {
        backend.addStepDefinition(JavaStepDefinitionTest.THREE_BLIND_ANIMALS.getAnnotation(Given.class), JavaStepDefinitionTest.THREE_DISABLED_MICE);
        backend.addStepDefinition(JavaStepDefinitionTest.THREE_BLIND_ANIMALS.getAnnotation(Given.class), JavaStepDefinitionTest.THREE_BLIND_ANIMALS);
    }

    @Test
    public void throws_ambiguous_when_two_matches_are_found() throws Throwable {
        backend.addStepDefinition(JavaStepDefinitionTest.THREE_DISABLED_MICE.getAnnotation(Given.class), JavaStepDefinitionTest.THREE_DISABLED_MICE);
        backend.addStepDefinition(JavaStepDefinitionTest.THREE_BLIND_ANIMALS.getAnnotation(Given.class), JavaStepDefinitionTest.THREE_BLIND_ANIMALS);
        PickleTag tag = new PickleTag(Mockito.mock(PickleLocation.class), "@foo");
        PickleStep step = new PickleStep("three blind mice", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        Pickle pickle = new Pickle("pickle name", JavaStepDefinitionTest.ENGLISH, Arrays.asList(step), Arrays.asList(tag), Arrays.asList(Mockito.mock(PickleLocation.class)));
        PickleEvent pickleEvent = new PickleEvent("uri", pickle);
        runner.runPickle(pickleEvent);
        Assert.assertEquals(AmbiguousStepDefinitionsException.class, latestReceivedResult.getError().getClass());
    }

    @Test
    public void does_not_throw_ambiguous_when_nothing_is_ambiguous() throws Throwable {
        backend.addStepDefinition(JavaStepDefinitionTest.THREE_DISABLED_MICE.getAnnotation(Given.class), JavaStepDefinitionTest.THREE_DISABLED_MICE);
        PickleTag tag = new PickleTag(Mockito.mock(PickleLocation.class), "@foo");
        PickleStep step = new PickleStep("three blind mice", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        Pickle pickle = new Pickle("pickle name", JavaStepDefinitionTest.ENGLISH, Arrays.asList(step), Arrays.asList(tag), Arrays.asList(Mockito.mock(PickleLocation.class)));
        PickleEvent pickleEvent = new PickleEvent("uri", pickle);
        runner.runPickle(pickleEvent);
        Assert.assertNull(latestReceivedResult.getError());
        Assert.assertTrue(defs.foo);
        Assert.assertFalse(defs.bar);
    }

    public static class Defs {
        public boolean foo;

        public boolean bar;

        @Given("three (.*) mice")
        public void threeDisabledMice(String disability) {
            foo = true;
        }

        @Given("three blind (.*)")
        public void threeBlindAnimals(String animals) {
            bar = true;
        }
    }
}

