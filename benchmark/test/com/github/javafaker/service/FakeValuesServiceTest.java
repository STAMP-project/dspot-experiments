package com.github.javafaker.service;


import com.github.javafaker.AbstractFakerTest;
import com.github.javafaker.Faker;
import com.github.javafaker.Superhero;
import com.github.javafaker.matchers.MatchesRegularExpression;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FakeValuesServiceTest extends AbstractFakerTest {
    @Mock
    private RandomService randomService;

    private FakeValuesService fakeValuesService;

    @Test(expected = LocaleDoesNotExistException.class)
    public void localeShouldThrowException() {
        new FakeValuesService(new Locale("Does not exist"), randomService);
    }

    @Test
    public void fetchStringShouldReturnValue() {
        Assert.assertThat(fakeValuesService.fetchString("property.dummy"), is("x"));
    }

    @Test
    public void fetchShouldReturnValue() {
        Assert.assertThat(fakeValuesService.fetch("property.dummy"), Is.<Object>is("x"));
    }

    @Test
    public void fetchObjectShouldReturnValue() {
        Assert.assertThat(fakeValuesService.fetchObject("property.dummy"), Is.<Object>is(Arrays.asList("x", "y", "z")));
    }

    @Test
    public void safeFetchShouldReturnValueInList() {
        Mockito.doReturn(0).when(randomService).nextInt(Mockito.anyInt());
        Assert.assertThat(fakeValuesService.safeFetch("property.dummy", null), is("x"));
    }

    @Test
    public void safeFetchShouldReturnSimpleList() {
        Assert.assertThat(fakeValuesService.safeFetch("property.simple", null), is("hello"));
    }

    @Test
    public void safeFetchShouldReturnEmptyStringWhenPropertyDoesntExist() {
        Assert.assertThat(fakeValuesService.safeFetch("property.dummy2", ""), isEmptyString());
    }

    @Test
    public void bothify2Args() {
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Faker f = new Faker();
        String value = fakeValuesService.resolve("property.bothify_2", dummy, f);
        Assert.assertThat(value, MatchesRegularExpression.matchesRegularExpression("[A-Z]{2}\\d{2}"));
    }

    @Test
    public void regexifyDirective() {
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        String value = fakeValuesService.resolve("property.regexify1", dummy, faker);
        Assert.assertThat(value, isOneOf("55", "44", "45", "54"));
        Mockito.verify(faker).regexify("[45]{2}");
    }

    @Test
    public void regexifySlashFormatDirective() {
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        String value = fakeValuesService.resolve("property.regexify_slash_format", dummy, faker);
        Assert.assertThat(value, isOneOf("55", "44", "45", "54"));
        Mockito.verify(faker).regexify("[45]{2}");
    }

    @Test
    public void regexifyDirective2() {
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        String value = fakeValuesService.resolve("property.regexify_cell", dummy, faker);
        Assert.assertThat(value, isOneOf("479", "459"));
        Mockito.verify(faker).regexify("4[57]9");
    }

    @Test
    public void resolveKeyToPropertyWithAPropertyWithoutAnObject() {
        // #{hello} -> DummyService.hello
        // given
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Mockito.doReturn("Yo!").when(dummy).hello();
        // when
        final String actual = fakeValuesService.resolve("property.simpleResolution", dummy, faker);
        // then
        Assert.assertThat(actual, is("Yo!"));
        Mockito.verify(dummy).hello();
        Mockito.verifyZeroInteractions(faker);
    }

    @Test
    public void resolveKeyToPropertyWithAPropertyWithAnObject() {
        // given
        final Superhero person = Mockito.mock(Superhero.class);
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Mockito.doReturn(person).when(faker).superhero();
        Mockito.doReturn("Luke Cage").when(person).name();
        // when
        final String actual = fakeValuesService.resolve("property.advancedResolution", dummy, faker);
        // then
        Assert.assertThat(actual, is("Luke Cage"));
        Mockito.verify(faker).superhero();
        Mockito.verify(person).name();
    }

    @Test
    public void resolveKeyToPropertyWithAList() {
        // property.resolutionWithList -> #{hello}
        // #{hello} -> DummyService.hello
        // given
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Mockito.doReturn(0).when(randomService).nextInt(Mockito.anyInt());
        Mockito.doReturn("Yo!").when(dummy).hello();
        // when
        final String actual = fakeValuesService.resolve("property.resolutionWithList", dummy, faker);
        // then
        Assert.assertThat(actual, is("Yo!"));
        Mockito.verify(dummy).hello();
    }

    @Test
    public void resolveKeyWithMultiplePropertiesShouldJoinResults() {
        // given
        final Superhero person = Mockito.mock(Superhero.class);
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Mockito.doReturn(person).when(faker).superhero();
        Mockito.doReturn("Yo Superman!").when(dummy).hello();
        Mockito.doReturn("up up and away").when(person).descriptor();
        // when
        String actual = fakeValuesService.resolve("property.multipleResolution", dummy, faker);
        // then
        Assert.assertThat(actual, is("Yo Superman! up up and away"));
        Mockito.verify(faker).superhero();
        Mockito.verify(person).descriptor();
        Mockito.verify(dummy).hello();
    }

    @Test
    public void testLocaleChain() {
        final List<Locale> chain = fakeValuesService.localeChain(Locale.SIMPLIFIED_CHINESE);
        Assert.assertThat(chain, ArgumentMatchers.contains(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE, Locale.ENGLISH));
    }

    @Test
    public void testLocaleChainEnglish() {
        final List<Locale> chain = fakeValuesService.localeChain(Locale.ENGLISH);
        Assert.assertThat(chain, ArgumentMatchers.contains(Locale.ENGLISH));
    }

    @Test
    public void testLocaleChainLanguageOnly() {
        final List<Locale> chain = fakeValuesService.localeChain(Locale.CHINESE);
        Assert.assertThat(chain, ArgumentMatchers.contains(Locale.CHINESE, Locale.ENGLISH));
    }

    @Test
    public void expressionWithInvalidFakerObject() {
        expressionShouldFailWith("#{ObjectNotOnFaker.methodName}", "Unable to resolve #{ObjectNotOnFaker.methodName} directive.");
    }

    @Test
    public void expressionWithValidFakerObjectButInvalidMethod() {
        expressionShouldFailWith("#{Name.nonExistentMethod}", "Unable to resolve #{Name.nonExistentMethod} directive.");
    }

    /**
     * Two things are important here:
     * 1) the message in the exception should be USEFUL
     * 2) a {@link RuntimeException} should be thrown.
     *
     * if the message changes, it's ok to update the test provided
     * the two conditions above are still true.
     */
    @Test
    public void expressionWithValidFakerObjectValidMethodInvalidArgs() {
        expressionShouldFailWith("#{Number.number_between 'x','y'}", "Unable to resolve #{Number.number_between 'x','y'} directive.");
    }

    /**
     * Two things are important here:
     * 1) the message in the exception should be USEFUL
     * 2) a {@link RuntimeException} should be thrown.
     *
     * if the message changes, it's ok to update the test provided
     * the two conditions above are still true.
     */
    @Test
    public void expressionCompletelyUnresolvable() {
        expressionShouldFailWith("#{x}", "Unable to resolve #{x} directive.");
    }

    @Test
    public void resolveUsingTheSameKeyTwice() {
        // #{hello} -> DummyService.hello
        // given
        final FakeValuesServiceTest.DummyService dummy = Mockito.mock(FakeValuesServiceTest.DummyService.class);
        Mockito.when(dummy.hello()).thenReturn("1").thenReturn("2");
        // when
        final String actual = fakeValuesService.resolve("property.sameResolution", dummy, faker);
        // then
        Assert.assertThat(actual, is("1 2"));
        Mockito.verifyZeroInteractions(faker);
    }

    public static class DummyService {
        public String firstName() {
            return "John";
        }

        public String lastName() {
            return "Smith";
        }

        public String hello() {
            return "Hello";
        }
    }
}

