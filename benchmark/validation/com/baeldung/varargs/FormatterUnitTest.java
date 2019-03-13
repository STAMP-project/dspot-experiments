package com.baeldung.varargs;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class FormatterUnitTest {
    private static final String FORMAT = "%s %s %s";

    @Test
    public void givenNoArgument_thenEmptyAndTwoSpacesAreReturned() {
        String actualResult = format();
        MatcherAssert.assertThat(actualResult, CoreMatchers.is("empty  "));
    }

    @Test
    public void givenOneArgument_thenResultHasTwoTrailingSpace() {
        String actualResult = format("baeldung");
        MatcherAssert.assertThat(actualResult, CoreMatchers.is("baeldung  "));
    }

    @Test
    public void givenTwoArguments_thenOneTrailingSpaceExists() {
        String actualResult = format("baeldung", "rocks");
        MatcherAssert.assertThat(actualResult, CoreMatchers.is("baeldung rocks "));
    }

    @Test
    public void givenMoreThanThreeArguments_thenTheFirstThreeAreUsed() {
        String actualResult = formatWithVarArgs("baeldung", "rocks", "java", "and", "spring");
        MatcherAssert.assertThat(actualResult, CoreMatchers.is("baeldung rocks java"));
    }
}

