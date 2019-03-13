package com.baeldung.vavr;


import io.vavr.API;
import io.vavr.MatchError;
import io.vavr.Predicates;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Test;


public class PatternMatchingUnitTest {
    @Test
    public void whenMatchesDefault_thenCorrect() {
        int input = 5;
        String output = API.Match(input).of(API.Case(API.$(1), "one"), API.Case(API.$(2), "two"), API.Case(API.$(3), "three"), API.Case(API.$(), "unknown"));
        Assert.assertEquals("unknown", output);
    }

    @Test(expected = MatchError.class)
    public void givenNoMatchAndNoDefault_whenThrows_thenCorrect() {
        int input = 5;
        API.Match(input).of(API.Case(API.$(1), "one"), API.Case(API.$(2), "two"));
    }

    @Test
    public void whenMatchWorksWithOption_thenCorrect() {
        int i = 10;
        Option<String> s = API.Match(i).option(API.Case(API.$(0), "zero"));
        Assert.assertTrue(s.isEmpty());
        Assert.assertEquals("None", s.toString());
    }

    @Test
    public void whenMatchWorksWithPredicate_thenCorrect() {
        int i = 3;
        String s = API.Match(i).of(API.Case(API.$(Predicates.is(1)), "one"), API.Case(API.$(Predicates.is(2)), "two"), API.Case(API.$(Predicates.is(3)), "three"), API.Case(API.$(), "?"));
        Assert.assertEquals("three", s);
    }

    @Test
    public void givenInput_whenMatchesClass_thenCorrect() {
        Object obj = 5;
        String s = API.Match(obj).of(API.Case(API.$(Predicates.instanceOf(String.class)), "string matched"), API.Case(API.$(), "not string"));
        Assert.assertEquals("not string", s);
    }

    @Test
    public void givenInput_whenMatchesNull_thenCorrect() {
        Object obj = 5;
        String s = API.Match(obj).of(API.Case(API.$(Predicates.isNull()), "no value"), API.Case(API.$(Predicates.isNotNull()), "value found"));
        Assert.assertEquals("value found", s);
    }

    @Test
    public void givenInput_whenContainsWorks_thenCorrect() {
        int i = 5;
        String s = API.Match(i).of(API.Case(API.$(Predicates.isIn(2, 4, 6, 8)), "Even Single Digit"), API.Case(API.$(Predicates.isIn(1, 3, 5, 7, 9)), "Odd Single Digit"), API.Case(API.$(), "Out of range"));
        Assert.assertEquals("Odd Single Digit", s);
    }

    @Test
    public void givenInput_whenMatchAllWorks_thenCorrect() {
        Integer i = null;
        String s = API.Match(i).of(API.Case(API.$(Predicates.allOf(Predicates.isNotNull(), Predicates.isIn(1, 2, 3, null))), "Number found"), API.Case(API.$(), "Not found"));
        Assert.assertEquals("Not found", s);
    }

    @Test
    public void givenInput_whenMatchesAnyOfWorks_thenCorrect() {
        Integer year = 1990;
        String s = API.Match(year).of(API.Case(API.$(Predicates.anyOf(Predicates.isIn(1990, 1991, 1992), Predicates.is(1986))), "Age match"), API.Case(API.$(), "No age match"));
        Assert.assertEquals("Age match", s);
    }

    @Test
    public void givenInput_whenMatchesNoneOfWorks_thenCorrect() {
        Integer year = 1990;
        String s = API.Match(year).of(API.Case(API.$(Predicates.noneOf(Predicates.isIn(1990, 1991, 1992), Predicates.is(1986))), "Age match"), API.Case(API.$(), "No age match"));
        Assert.assertEquals("No age match", s);
    }

    @Test
    public void whenMatchWorksWithPredicate_thenCorrect2() {
        int i = 5;
        String s = API.Match(i).of(API.Case(API.$(Predicates.isIn(2, 4, 6, 8)), "Even Single Digit"), API.Case(API.$(Predicates.isIn(1, 3, 5, 7, 9)), "Odd Single Digit"), API.Case(API.$(), "Out of range"));
        Assert.assertEquals("Odd Single Digit", s);
    }

    @Test
    public void whenMatchCreatesSideEffects_thenCorrect() {
        int i = 4;
        API.Match(i).of(API.Case(API.$(Predicates.isIn(2, 4, 6, 8)), ( o) -> run(this::displayEven)), API.Case(API.$(Predicates.isIn(1, 3, 5, 7, 9)), ( o) -> run(this::displayOdd)), API.Case(API.$(), ( o) -> run(() -> {
            throw new IllegalArgumentException(String.valueOf(i));
        })));
    }
}

